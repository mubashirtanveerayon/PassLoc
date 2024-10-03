package com.loc.passloc2android;

import android.app.Dialog;
import android.content.ClipData;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.loc.service.passloc.database.Database;
import com.loc.service.passloc.generator.qr.QRCodeReader;


import org.json.JSONArray;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import services.model.EntryModel;
import services.secure.Credential;
import utils.Identifier;
import utils.dbInterface.DatabaseListener;

public class MainActivity extends AppCompatActivity implements DatabaseListener, View.OnClickListener {
    LoginFragment loginFragment;
    public EditFragment editFragment;
    LogoutFragment logoutFragment;
    ViewFragment viewFragment;
    SyncFragment syncFragment;

    public HelpFragment helpFragment;
    public GeneratePasswordFragment generateFragment;

    Fragment currentFragment;

    QRScanFragment qrScanFragment;

    public ExtendedFloatingActionButton actionButton;

    Dialog syncActionDialog, syncMergeConfirmationDialog;

    public BottomNavigationView navigationView;
    ArrayList<String> partitions;

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



        partitions = new ArrayList<>();
        qrScanFragment = new QRScanFragment();

        loginFragment = new LoginFragment();
        editFragment = new EditFragment();
        logoutFragment = new LogoutFragment();
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
                }else if(itemId == R.id.view_button){
                    loadFragment(viewFragment,currentFragment == loginFragment);
                }else if(itemId == R.id.logout_button){
                    loadFragment(logoutFragment,true);
                }else if(itemId == R.id.sync_button){
                    loadFragment(syncFragment,currentFragment == loginFragment || currentFragment == viewFragment);
                }



                return true;
            }
        });
        loadFragment(loginFragment,true);


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



        syncMergeConfirmationDialog = new Dialog(this);
        syncMergeConfirmationDialog.setContentView(R.layout.sync_data_merge_confirmation);
        syncMergeConfirmationDialog.getWindow().setBackgroundDrawableResource(R.drawable.qr_import_action_menu_background);
        syncMergeConfirmationDialog.setCancelable(false);
        AppCompatButton updateEntryButton = syncMergeConfirmationDialog.findViewById(R.id.sync_confirmation_update);
        AppCompatButton createEntryButton = syncMergeConfirmationDialog.findViewById(R.id.sync_confirmation_create);

        updateEntryButton.setOnClickListener(this);
        createEntryButton.setOnClickListener(this);


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
        }else if(fragment == logoutFragment){


            if(Database.online()){
//                actionButton.setText("Logout");
                actionButton.setIcon(this.getResources().getDrawable(R.drawable.logout91) );
            }else{
//                actionButton.setText("Login");
                actionButton.setIcon( this.getResources().getDrawable(R.drawable.lock364));
            }
            navigationView.getMenu().getItem(4).setChecked(true);

        }else if(fragment == editFragment){
            if(!editFragment.isInserting())
                actionButton.setIcon(this.getResources().getDrawable(R.drawable.done64));

        }else if(fragment == generateFragment){
            actionButton.setIcon(getResources().getDrawable(R.drawable.redo64));
        }else if(fragment == helpFragment){
            actionButton.setIcon(getResources().getDrawable(R.drawable.back91));
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

                    editFragment.clearTexts();

                    Credential.resetInstance();

                    try {
                        Credential.getInstance(masterPassword);



                        Database.establishConnection(dbName,dbPassword);
                    }catch(Exception ex){
                        Log.e("exception",ex.getMessage());

                        Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
                    }

                    if(Database.online()) {
                        Database.getInstance().setListener(this);
                        loadFragment(viewFragment,true);
                    }else{
                        Toast.makeText(this,"Login failed",Toast.LENGTH_SHORT).show();
                    }





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


                    editFragment.insertOrUpdate();

                    if(!inserting)editFragment.clearTag();
                    editFragment.clearUsername();
                    editFragment.clearPassword();

                    editFragment.setTagEditTextEnabled(true);

                    if(!inserting)loadFragment(viewFragment,true);

                }else if(currentFragment == logoutFragment){
                    Credential.resetInstance();
                    editFragment.clearTexts();
                    if(Database.online()){
                        Database.disconnect();
                        actionButton.setText("Login");
                        actionButton.setIcon(this.getResources().getDrawable(R.drawable.lock364));
                        logoutFragment.logoutTextView.setText(getResources().getString(R.string.logged_out_text));
                    }


                    else loadFragment(loginFragment,false);



                }else if(currentFragment == generateFragment){



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


                    partitions.addAll(qrScanFragment.getChunks());

                    syncMergeConfirmationDialog.show();

                    try {

                    }catch(Exception ex){
                        Log.e("exception",ex.getMessage());
                        Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
                    }



                }


            }else{

                if(currentFragment == loginFragment){




                    if(getExternalFilesDir("databases"+ File.separator+loginFragment.getDatabaseName()+"."+ Identifier.DATABASE_EXTENSION).exists())actionButton.setText("Login");
                    else actionButton.setText("Create");

                }else if(currentFragment == viewFragment){
                    actionButton.setText("Add");
                }else if(currentFragment == editFragment){
                    actionButton.setText("Done");
                }else if(currentFragment == logoutFragment){
                    if(Database.online()){
                        actionButton.setText("Logout");
                    }else{
                        actionButton.setText("Login");
                    }

                }else if(currentFragment == generateFragment){
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
            startActivityForResult(Intent.createChooser(intent, "Select QR Codes, no need to select in order"), 1);
        }else if(view.getId() == R.id.sync_confirmation_update){

            try {
                sync(false);
            }catch (Exception ex){
                Log.e("exception",ex.getMessage());
                Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
            }

            syncMergeConfirmationDialog.cancel();
        }else if(view.getId() == R.id.sync_confirmation_create){
            try {
                sync(true);
            }catch (Exception ex){
                Log.e("exception",ex.getMessage());
                Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
            }
            syncMergeConfirmationDialog.cancel();
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode == 1 && resultCode== RESULT_OK && data != null)
        {
            ClipData mClipData = data.getClipData();

            if (mClipData != null) {

                ArrayList<InputStream> images = new ArrayList<>();
                int count = mClipData.getItemCount();
                try{
                    for (int i = 0; i < count; i++) {


                        images.add(getContentResolver().openInputStream(mClipData.getItemAt(i).getUri()));

                    }

                    partitions.addAll(QRCodeReader.createPartitionFromQRImages(images));


                    syncMergeConfirmationDialog.show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
                }



            }

        }
    }

    public void sync( boolean insertAll) throws Exception {



        byte[] encryptedData = QRCodeReader.loadByteArrayFromPartition(partitions);
        Database db = Database.getInstance();
        String json = Credential.getInstance().decrypt(new String(encryptedData));

        ArrayList<EntryModel> entries = EntryModel.fromJSONArray(json);

        Log.i("json",json);

        for (EntryModel entry : entries) {
            String id = entry.getId();
            if (id == null || insertAll || !db.alreadyExists(id))
                db.insert(entry);
            else
                db.update(entry);
        }

        partitions.clear();
        loadFragment(viewFragment,false);

    }
}