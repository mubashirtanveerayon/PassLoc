package com.loc.passloc2android;




import static android.content.Context.STORAGE_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.zxing.WriterException;
import com.loc.service.passloc.database.Database;
import com.loc.service.passloc.generator.qr.QRCodeGenerator;
import com.loc.service.passloc.generator.qr.QRCodeReader;
import com.loc.service.passloc.model.EntryModel;
import com.loc.service.passloc.secure.Credential;
import com.loc.service.utils.HelperFunctions;
import com.loc.service.utils.SwipeDetector;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SyncFragment extends Fragment {


    ImageView imageView;

    TextView indexTextView;

    public ArrayList<Bitmap> images;


    Button loadQRButton;
    private int imageIndex = 0;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_sync, container, false);



        imageView = view.findViewById(R.id.qr_image_view);

        indexTextView = view.findViewById(R.id.qr_image_index_text);

        images = new ArrayList<>();



        loadQRButton = view.findViewById(R.id.load_qr_button);

        loadQRButton.setOnClickListener(new View.OnClickListener()  {

            @Override
            public void onClick(View view) {
                loadOrSaveQRCodes();
            }
        });




        return view;
    }

    public void onResume(){
        super.onResume();
        images.clear();

        loadImageView();
        placeFooter();



        loadQRButton.setText("Load QR Codes");




        if(Database.online())
            loadQRButton.setVisibility(View.VISIBLE);
        else
            loadQRButton.setVisibility(View.GONE);



        new SwipeDetector(view).setOnSwipeListener(new SwipeDetector.onSwipeEvent() {

            @Override
            public void SwipeEventDetected(View v, SwipeDetector.SwipeTypeEnum swipeType) {

                if(images.isEmpty())
                    return;

                Animation enterAnimation;

                int newIndex;

                if (swipeType == SwipeDetector.SwipeTypeEnum.LEFT_TO_RIGHT) {
                    newIndex = imageIndex == 0 ? imageIndex : imageIndex - 1;
                    enterAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_left_to_right);
                } else {
                    newIndex = imageIndex == images.size() - 1 ? imageIndex : imageIndex + 1;
                    enterAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.enter_right_to_left);

                }

                if(imageIndex == newIndex)
                    return;
                imageIndex = newIndex;

                loadImageView();
                placeFooter();

                enterAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {



                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                imageView.startAnimation(enterAnimation);


            }
        });


    }




    public void loadOrSaveQRCodes() {

        if(!Database.online())
            return;

        if(images.isEmpty()) {

            String json = HelperFunctions.convertToJsonString(Database.getInstance().getAllData());
            Database db = Database.getInstance();
            String signature = db.getTableName();

            String encryptedData = Credential.getInstance().encrypt(json);
            ArrayList<String> partitions = QRCodeGenerator.createPartitionFromData(encryptedData.getBytes(), 2000, signature);
            images.clear();
            try {
                images.addAll(QRCodeGenerator.generateQRImages(partitions, 400, 400));
                imageIndex = 0;

                loadImageView();
                placeFooter();


                loadQRButton.setText("Save QR Codes");
            } catch (WriterException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
        }else{
            try{
                for(int i=0;i<images.size();i++){

                    Bitmap image = images.get(i);
                    ContentValues metaData = new ContentValues();
                    metaData.put(MediaStore.Images.Media.DISPLAY_NAME,String.valueOf(i)+".png");
                    metaData.put(MediaStore.Images.Media.WIDTH,image.getWidth());
                    metaData.put(MediaStore.Images.Media.HEIGHT,image.getHeight());

                    Uri uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,metaData);
                    OutputStream os = getActivity().getContentResolver().openOutputStream(uri);
                    image.compress(Bitmap.CompressFormat.PNG,100,os);
                    os.flush();
                    os.close();





//                        StorageManager storageManager = (StorageManager)getActivity().getSystemService(STORAGE_SERVICE);
//                        StorageVolume storageVolume = storageManager.getStorageVolumes().get(0); // internal Storage
//                        File imageFile = new File(String.format("%s%s%s%s%s", storageVolume.getDirectory().getPath(),File.separator,"Download",File.separator,generateUniqueName(String.valueOf(i))));
//                        FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
//                        Bitmap image = images.get(i);
//
//                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//                        image.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
//
//                        fileOutputStream.write(byteArrayOutputStream.toByteArray());
//
//
//
//
//
//                        fileOutputStream.flush();
//                        fileOutputStream.close();
//
//                        byteArrayOutputStream.close();



                }

                Toast.makeText(getActivity(),"Images saved to downloads",Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("exp", e.getMessage());
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void placeFooter() {
        if(images.isEmpty())
            indexTextView.setText("Scan the QR code for project GitHub link!");
        else
            indexTextView.setText("Swipe left or right to browse generated qr codes: ("+(imageIndex+1)+"/"+images.size()+")");
    }

    private void loadImageView() {
        if(images.isEmpty())
            imageView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.github));
        else
            imageView.setImageBitmap(images.get(imageIndex));
    }

    public void sync(ArrayList<String> partitions) throws JSONException {

            byte[] encryptedData = QRCodeReader.loadByteArrayFromPartition(partitions);
            Database db = Database.getInstance();
            String json = Credential.getInstance().decrypt(new String(encryptedData));

            ArrayList<EntryModel> entries = EntryModel.fromJSONArray(new JSONArray(json));

            for (EntryModel entry : entries) {
                String id = entry.getId();
                if (id == null || !db.alreadyExists(id))
                    db.insert(entry);
                else
                    db.update(entry);
            }



    }


    public String generateUniqueName(String imageName) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddsshhmmss", Locale.getDefault());
        String date = sdf.format(new Date());

        String fileName = String.format("%s_%s.png", imageName, date);

        return fileName;

    }

}
