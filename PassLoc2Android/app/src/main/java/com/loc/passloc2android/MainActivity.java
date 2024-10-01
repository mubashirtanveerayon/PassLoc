package com.loc.passloc2android;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.loc.service.passloc.database.Database;
import com.loc.service.passloc.model.EntryModel;
import com.loc.service.passloc.secure.AES256WithPassword;
import com.loc.service.passloc.secure.Credential;
import com.loc.service.utils.Identifier;
import com.loc.service.utils.dbInterface.DatabaseListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DatabaseListener {
    LoginFragment loginFragment;
    public EditFragment editFragment;
    LogoutFragment logoutFragment;
    ViewFragment viewFragment;
    SyncFragment syncFragment;

    public HelpFragment helpFragment;
    public GeneratePasswordFragment generateFragment;

    Fragment currentFragment;



    public ExtendedFloatingActionButton actionButton;

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




        loginFragment = new LoginFragment();
        editFragment = new EditFragment();
        logoutFragment = new LogoutFragment();
        viewFragment = new ViewFragment();
        syncFragment = new SyncFragment();
        generateFragment = new GeneratePasswordFragment();
        helpFragment = new HelpFragment();

        actionButton = findViewById(R.id.action_button);

        actionButton.shrink();
        actionButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                onActionButtonClick();
            }
        });
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
                    EntryModel.staticEntry=null;
                }else if(itemId == R.id.logout_button){
                    loadFragment(logoutFragment,true);
                }else if(itemId == R.id.sync_button){
                    loadFragment(syncFragment,currentFragment == loginFragment || currentFragment == viewFragment);
                }



                return true;
            }
        });
        loadFragment(loginFragment,true);

    }


    public void onActionButtonClick(){
        if(actionButton.isExtended()){

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

                Credential.resetInstance();

                try {
                    Credential credential = Credential.getInstance(masterPassword);
                    Database.establishConnection(credential,this,dbName,dbPassword);
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


                EntryModel.staticEntry=null;
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


                boolean inserting = EntryModel.staticEntry==null;

                if (inserting) {
                    Database.getInstance().insert(new EntryModel(tag,username,password));
                } else {
                    EntryModel.staticEntry = new EntryModel(EntryModel.staticEntry.getId(),tag,username,password);
                    Database.getInstance().update(EntryModel.staticEntry);
                }


                // remove entryModel

                EntryModel.staticEntry = null;

                if(!inserting)editFragment.clearTag();
                editFragment.clearUsername();
                editFragment.clearPassword();

                editFragment.setTagEditTextEnabled(true);

                if(!inserting)loadFragment(viewFragment,true);

            }else if(currentFragment == logoutFragment){
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
            }



            actionButton.extend();
        }
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
            if(EntryModel.staticEntry!=null)
                actionButton.setIcon(this.getResources().getDrawable(R.drawable.done64));

        }else if(fragment == generateFragment){
            actionButton.setIcon(getResources().getDrawable(R.drawable.redo64));
        }else if(fragment == helpFragment){
            actionButton.setIcon(getResources().getDrawable(R.drawable.back91));
        }else if(fragment == syncFragment){
            navigationView.getMenu().getItem(3).setChecked(true);
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




}