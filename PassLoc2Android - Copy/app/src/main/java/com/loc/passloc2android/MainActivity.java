package com.loc.passloc2android;

import android.Manifest;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.loc.service.passloc.database.Database;
import com.loc.service.passloc.generator.qr.QRCodeReader;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;

import services.model.EntryModel;
import services.secure.Credential;
import utils.StringCompressor;
import utils.dbInterface.DatabaseListener;


public class MainActivity extends AppCompatActivity implements DatabaseListener, View.OnClickListener{

    public static final int LOGIN_INDEX = 0;
    public static final int VIEW_INDEX = 1;
    public static final int EDIT_INDEX = 2;
    public static final int PG_INDEX = 3;
    public static final int SYNC_INDEX = 4;
    public static final int QR_INDEX = 5;

    private static final int IMPORT_IMAGE_REQUEST_CODE = 1;
    private static final int NOTIFICATION_REQUEST_CODE = 2;
    private static final int NOTIFICATION_CLICK_CODE = 3;
    private static String LOCK_FILE_PATH;
    LoginFragment loginFragment;
    public EditFragment editFragment;
//    LogoutFragment logoutFragment;
    ViewFragment viewFragment;
    SyncFragment syncFragment;

    public HelpFragment helpFragment;
    public GeneratePasswordFragment generateFragment;

    Fragment currentFragment;

    QRScanFragment qrScanFragment;

    public ExtendedFloatingActionButton actionButton;

    Dialog syncActionDialog, allowNotificationDialog;
//    Dialog syncMergeConfirmationDialog;

    public BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });



        LOCK_FILE_PATH = getFilesDir()+File.separator+"lock.txt";


        qrScanFragment = new QRScanFragment();

        loginFragment = new LoginFragment();
        editFragment = new EditFragment();
//        logoutFragment = new LogoutFragment();
        viewFragment = new ViewFragment();
        syncFragment = new SyncFragment();
        generateFragment = new GeneratePasswordFragment();
        helpFragment = new HelpFragment();

        actionButton = findViewById(R.id.action_button);

        actionButton.shrink();
        actionButton.setOnClickListener(this);
        navigationView = findViewById(R.id.nav_bar);
        navigationView.setBackground(null);

        navigationView.getMenu().getItem(2).setEnabled(false);

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();



                if(itemId == R.id.login_button){
                    loadFragment(loginFragment,false);

                    item.setIcon(getResources().getDrawable(R.drawable.outline_key_24));

                }else if(itemId == R.id.view_button){
                    loadFragment(viewFragment,currentFragment == loginFragment);
                }else if(itemId == R.id.help_button){
                    loadFragment(helpFragment,true);
                }else if(itemId == R.id.sync_button){
                    loadFragment(syncFragment,currentFragment == loginFragment || currentFragment == viewFragment);
                }



                return true;
            }
        });







        loadFragment(loginFragment, true);

        syncActionDialog = new Dialog(this);
        syncActionDialog.setContentView(R.layout.qr_import_action_menu);
        Window window = syncActionDialog.getWindow();
        window.setBackgroundDrawableResource(R.drawable.qr_import_action_menu_background);
//                window.setGravity(Gravity.BOTTOM);
        syncActionDialog.getWindow().getAttributes().y = 600;



        AppCompatButton cameraButton = syncActionDialog.findViewById(R.id.from_camera_button);
        AppCompatButton galleryButton = syncActionDialog.findViewById(R.id.from_gallery_button);




        cameraButton.setOnClickListener(this);
        galleryButton.setOnClickListener(this);

        allowNotificationDialog = new Dialog(this);
        allowNotificationDialog.setContentView(R.layout.request_notification_permission);
        allowNotificationDialog.getWindow().setBackgroundDrawableResource(R.drawable.qr_import_action_menu_background);

        AppCompatButton allowNotificationButton = allowNotificationDialog.findViewById(R.id.notification_allow_button);
        allowNotificationButton.setOnClickListener(this);







//        syncMergeConfirmationDialog = new Dialog(this);
//        syncMergeConfirmationDialog.setContentView(R.layout.sync_data_merge_confirmation);
//        syncMergeConfirmationDialog.getWindow().setBackgroundDrawableResource(R.drawable.qr_import_action_menu_background);
//        syncMergeConfirmationDialog.setCancelable(false);
//        AppCompatButton updateEntryButton = syncMergeConfirmationDialog.findViewById(R.id.sync_confirmation_update);
//        AppCompatButton createEntryButton = syncMergeConfirmationDialog.findViewById(R.id.sync_confirmation_create);
//
//        updateEntryButton.setOnClickListener(this);
//        createEntryButton.setOnClickListener(this);



//        StorageManager storageManager = (StorageManager)getSystemService(STORAGE_SERVICE);
//        StorageVolume storageVolume = storageManager.getStorageVolumes().get(0); // internal Storage
//
//        Log.i("dir",storageVolume.getDirectory().getAbsolutePath());

    }





    public void loadFragment(Fragment fragment, boolean openFromRight){

        if(currentFragment == fragment)return;

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if(openFromRight)transaction.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left);
        else transaction.setCustomAnimations(R.anim.enter_left_to_right,R.anim.exit_left_to_right);
        transaction.replace(R.id.fragment_layout,fragment);

        transaction.commit();


        if(fragment == viewFragment){
            actionButton.setIcon(this.getResources().getDrawable((R.drawable.add64)));
            navigationView.getMenu().getItem(1).setChecked(true);



        }else if(fragment == loginFragment){
            actionButton.setIcon(this.getResources().getDrawable(R.drawable.key91));
            navigationView.getMenu().getItem(0).setChecked(true);


        }
//        else if(fragment == logoutFragment){
//
//
//            if(Database.online()){
////                actionButton.setText("Logout");
//                actionButton.setIcon(this.getResources().getDrawable(R.drawable.logout91) );
//            }else{
////                actionButton.setText("Login");
//                actionButton.setIcon( this.getResources().getDrawable(R.drawable.lock364));
//            }
//            navigationView.getMenu().getItem(4).setChecked(true);
//
//        }
        else if(fragment == editFragment){
            if(!editFragment.isInserting())
                actionButton.setIcon(this.getResources().getDrawable(R.drawable.done64));

        }else if(fragment == generateFragment){
            actionButton.setIcon(getResources().getDrawable(R.drawable.redo64));
        }else if(fragment == helpFragment){
            actionButton.setIcon(getResources().getDrawable(R.drawable.back91));
            navigationView.getMenu().getItem(4).setChecked(true);
        }else if(fragment == syncFragment){
            navigationView.getMenu().getItem(3).setChecked(true);
            actionButton.setIcon(this.getResources().getDrawable(R.drawable.qr64));
        }else if(fragment == qrScanFragment) {
            actionButton.setIcon(this.getResources().getDrawable(R.drawable.done64));
        }


        currentFragment = fragment;
        actionButton.shrink();
    }

    public void onSuccess(String message) {

        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onUpgrade(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.action_button){
            if(actionButton.isExtended()){
                actionButton.shrink();
                if(currentFragment == loginFragment){

                    String dbName = loginFragment.getDatabaseName();
                    String dbPassword = loginFragment.getDatabasePassword();
                    String masterPassword = loginFragment.getMasterPassword();

                    if(dbName.isEmpty () || dbPassword.isEmpty() || masterPassword.isEmpty()){
                        Toast.makeText(this,"You cannot leave any field empty",Toast.LENGTH_LONG).show();
                        return;
                    }



                    if(Database.online()){
                        if(Database.getInstance().getDatabaseName().equals(dbName)) {
                            Toast.makeText(this, "You are already logged in", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Database.disconnect();
                    }

                    loginFragment.login(dbName,dbPassword,masterPassword);







                    if(Database.online()) {
                        navigationView.getMenu().getItem(0).setIcon(getResources().getDrawable(R.drawable.outline_logout_24));
                        Database.getInstance().setListener(this);
                        loadFragment(viewFragment,true);

                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)

                            if(ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                                    !=PackageManager.PERMISSION_GRANTED)
                                allowNotificationDialog.show();
                    }else{
                        Toast.makeText(this,"Login failed",Toast.LENGTH_SHORT).show();
                    }


//                    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    startActivityForResult(intent, DATABASE_PATH_REQUEST_CODE);





                }else if(currentFragment == viewFragment){


                    loadFragment(editFragment,true);

                }else if(currentFragment == editFragment){
                    String tag = editFragment.getEntryTag();
                    String username = editFragment.getEntryUsername();
                    String password = editFragment.getEntryPassword();


                    if(tag.isEmpty() || username.isEmpty() || password.isEmpty()){
                        Toast.makeText(this,"You cannot leave any field empty",Toast.LENGTH_LONG).show();
                        return;
                    }




                    // upload the data
                    if(!Database.online()){
                        Toast.makeText(this,"Database is offline",Toast.LENGTH_SHORT).show();
                        return;
                    }




                    boolean inserting = editFragment.isInserting();


                    if(inserting)
                        editFragment.insert();
                    else{
                        editFragment.update();
                        editFragment.clearTag();
                    }




                    editFragment.clearUsername();
                    editFragment.clearPassword();

                    editFragment.setTagEditTextEnabled(true);

                    if(!inserting)loadFragment(viewFragment,true);

                }
//                else if(currentFragment == logoutFragment){
//                    Credential.resetInstance();
//                    editFragment.clearTexts();
//                    if(Database.online()){
//                        Database.disconnect();
//                        actionButton.setText("Login");
//                        actionButton.setIcon(this.getResources().getDrawable(R.drawable.lock364));
//                        logoutFragment.logoutTextView.setText(getResources().getString(R.string.logged_out_text));
//                    }
//
//
//                    else loadFragment(loginFragment,false);
//
//
//
//                }
                else if(currentFragment == generateFragment){




                    generateFragment.generatePassword();


                }else if(currentFragment == helpFragment){
                    loadFragment(loginFragment,false);
                }else if(currentFragment == syncFragment){

                    if(Database.online())

                        syncActionDialog.show();
                    else
                        Toast.makeText(this,"Database is offline",Toast.LENGTH_SHORT).show();

                }else if(currentFragment == qrScanFragment){
//                    loadFragment(syncFragment,false);


                    if(!Database.online()){
                        Toast.makeText(this,"Database is offline",Toast.LENGTH_SHORT).show();
                        return;
                    }


                    ArrayList<String>partitions = qrScanFragment.getChunks();
//
//                    syncMergeConfirmationDialog.show();

                    try {

                        sync(partitions);

                    }catch(Exception ex){
                        Log.e("exception",ex.getMessage());
                        Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
                    }



                }


            }else{

                if(currentFragment == loginFragment){




                    actionButton.setText("Login");

                }else if(currentFragment == viewFragment){
                    actionButton.setText("Add");
                }else if(currentFragment == editFragment){
                    actionButton.setText("Done");
                }
//                else if(currentFragment == logoutFragment){
//                    if(Database.online()){
//                        actionButton.setText("Logout");
//                    }else{
//                        actionButton.setText("Login");
//                    }
//
//                }
                else if(currentFragment == generateFragment){
                    actionButton.setText("Generate");
                }else if(currentFragment == helpFragment){
                    actionButton.setText("Go back");
                }else if(currentFragment == syncFragment){
                    actionButton.setText("Import");
                }else if(currentFragment == qrScanFragment){
                    actionButton.setText("Done");
                }



                actionButton.extend();
            }
        }else if(view.getId() == R.id.from_camera_button){
            loadFragment(qrScanFragment,true);
            syncActionDialog.cancel();
        }else if(view.getId() == R.id.from_gallery_button){
            syncActionDialog.cancel();


            Intent intent = new Intent();

            // setting type to select to be image
            intent.setType("image/*");

            // allowing multiple image to be selected
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select QR Codes, no need to select in order"), IMPORT_IMAGE_REQUEST_CODE);
        }else if(view.getId() == R.id.notification_allow_button){
            allowNotificationDialog.cancel();
            askForNotificationPermission();
        }
//        else if(view.getId() == R.id.sync_confirmation_update){
//
//            try {
//                sync(false);
//            }catch (Exception ex){
//                Log.e("exception",ex.getMessage());
//                Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
//            }
//
//            syncMergeConfirmationDialog.cancel();
//        }else if(view.getId() == R.id.sync_confirmation_create){
//            try {
//                sync(true);
//            }catch (Exception ex){
//                Log.e("exception",ex.getMessage());
//                Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
//            }
//            syncMergeConfirmationDialog.cancel();
//        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == IMPORT_IMAGE_REQUEST_CODE && resultCode== RESULT_OK && data != null)
        {
            ClipData mClipData = data.getClipData();

            if (mClipData != null) {

                ArrayList<InputStream> images = new ArrayList<>();
                int count = mClipData.getItemCount();
                try{
                    for (int i = 0; i < count; i++) {


                        images.add(getContentResolver().openInputStream(mClipData.getItemAt(i).getUri()));

                    }

                    ArrayList<String> partitions = QRCodeReader.createPartitionFromQRImages(images);

                    sync(partitions);


//                    syncMergeConfirmationDialog.show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
                }



            }

        }
    }

    public void sync(ArrayList<String>partitions) throws Exception {



        String encryptedData = StringCompressor.decompress( QRCodeReader.loadByteArrayFromPartition(partitions));
        Database db = Database.getInstance();
        String json = Credential.getInstance().decrypt(new String(encryptedData));

        ArrayList<EntryModel> entries = EntryModel.fromJSONArray(json);



        for(EntryModel newEntry:entries)
            db.insert(newEntry);



        loadFragment(viewFragment,false);

    }


    public void askForNotificationPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)


                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.POST_NOTIFICATIONS},NOTIFICATION_REQUEST_CODE);
    }

    public void sendDatabaseBeingOnlineNotification(){




        Intent intent = new Intent(this,this.getClass()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        PendingIntent pi = PendingIntent.getActivity(this,NOTIFICATION_CLICK_CODE,intent,PendingIntent.FLAG_IMMUTABLE);



        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, PassLoc2.CHANNEL_ID)
                .setContentText("Database is still online.")
                .setContentTitle("PassLoc")
                .setContentIntent(pi)
                .setSmallIcon(R.drawable.appicon_ic_launcher_foreground)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(1,notification);




    }



    public void onPause(){
        super.onPause();

        if(Database.online())

            sendDatabaseBeingOnlineNotification();



    }

    private int getCurrentFragmentIndex() {

        if(currentFragment instanceof LoginFragment){
            return LOGIN_INDEX;
        }else if(currentFragment instanceof EditFragment){
            return EDIT_INDEX;
        }else if(currentFragment instanceof ViewFragment){
            return VIEW_INDEX;
        }else if(currentFragment instanceof SyncFragment){
            return SYNC_INDEX;
        }else if(currentFragment instanceof GeneratePasswordFragment){
            return PG_INDEX;
        }else if(currentFragment instanceof QRScanFragment){
            return QR_INDEX;
        }

        return -1;
    }

    private Fragment getFragment(int index){
        switch(index){
            case LOGIN_INDEX:
                return loginFragment;
            case VIEW_INDEX:
                return viewFragment;
            case EDIT_INDEX:
                return editFragment;
            case SYNC_INDEX:
                return syncFragment;
            case QR_INDEX:
                return qrScanFragment;
            case PG_INDEX:
                return generateFragment;
            default:
                return null;
        }
    }




}