package com.example.passloc;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.constants.AutoLoad;
import com.example.constants.PassLoc;
import com.example.database.DatabaseHelper;
import com.example.database.DatabaseListener;
import com.example.passloc.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity implements DatabaseListener {

    ActivityMainBinding binding;

    Fragment currentFragment;



    AccessFragment accessFragment;
    public EditFragment editFragment;
    ViewFragment viewFragment;
    LogoutFragment logoutFragment;

    FloatingActionButton actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());



//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        accessFragment = new AccessFragment();
        viewFragment = new ViewFragment();
        logoutFragment = new LogoutFragment();
        editFragment = new EditFragment();
        actionButton = binding.actionButton;
        binding.navBar.getMenu().getItem(2).setEnabled(false);
        binding.navBar.setItemActiveIndicatorColor(getColorStateList(R.color.theme_dark));
        binding.navBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {



            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                int itemId = item.getItemId();
                if(itemId == R.id.access_button){
                    loadFragment(accessFragment,false);
                }else if(itemId == R.id.view_button){
                    loadFragment(viewFragment,currentFragment == accessFragment);
                    AutoLoad.entryModel=null;
                }else if(itemId == R.id.logout_button){
                    loadFragment(logoutFragment,true);
                }


                return true;
            }
        });



//        loadFragment(viewFragment,true);
//        loadFragment(editFragment,true);
        loadFragment(accessFragment,true);
    }

    public void loadFragment(Fragment fragment, boolean openFromRight){
        if(currentFragment == fragment)return;
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if(openFromRight)transaction.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left);
        else transaction.setCustomAnimations(R.anim.enter_left_to_right,R.anim.exit_left_to_right);




        if(fragment == viewFragment){
            actionButton.setImageDrawable(this.getResources().getDrawable((R.drawable.add64)));
        }else if(fragment == accessFragment){
            actionButton.setImageDrawable(this.getResources().getDrawable(R.drawable.key91));
        }else if(fragment == logoutFragment){


            actionButton.setImageDrawable(DatabaseHelper.isOnline() ? this.getResources().getDrawable(R.drawable.logout91) : this.getResources().getDrawable(R.drawable.lock364));
        }else if(fragment == editFragment){
            if(AutoLoad.entryModel!=null)
                actionButton.setImageDrawable(this.getResources().getDrawable(R.drawable.done64));
        }


        currentFragment = fragment;
        transaction.replace(R.id.frame_layout,fragment);
        transaction.commit();

    }

    public void OnActionButtonClicked(View v){
        if(currentFragment == accessFragment){
            String databaseName = accessFragment.getAccount();
            String password = accessFragment.getPassword();
            char[] pin = accessFragment.getPin();
            int passwordMinLength = PassLoc.getContext().getResources().getInteger(R.integer.password_min_length);
            int pinMinLength = PassLoc.getContext().getResources().getInteger(R.integer.pin_min_length);
            int pinMaxLength = PassLoc.getContext().getResources().getInteger(R.integer.pin_max_length);
            if(databaseName.isEmpty())return;
            else if(password.length()<passwordMinLength){
                Toast.makeText(this,"Password must be at least "+passwordMinLength+" characters long",Toast.LENGTH_LONG).show();
                return;
            }else if(pin.length < pinMinLength){
                Toast.makeText(this,"Pin must be at least "+pinMinLength+" digits long",Toast.LENGTH_LONG).show();
                return;
            }else if(pin.length >pinMaxLength) {
                Toast.makeText(this, "Pin can be at most " + pinMaxLength + " digits long", Toast.LENGTH_LONG).show();
                return;
            }

            DatabaseHelper.disconnect();
            try {
                DatabaseHelper.establishConnection(databaseName, password, pin);
                Toast.makeText(this,"Database connection established",Toast.LENGTH_SHORT);
            }catch (Exception ex){
                Toast.makeText(this,ex.getMessage(),Toast.LENGTH_LONG).show();
                Log.e("exception",ex.getMessage());
            }

            if(DatabaseHelper.isOnline()) {
                DatabaseHelper.getInstance().setListener(this);
                loadFragment(viewFragment,true);
            }


        }else if(currentFragment == editFragment){
                String tag = editFragment.getEntryTag();
                String username = editFragment.getEntryUsername();
                String password = editFragment.getEntryPassword();


                if(tag.isEmpty() || username.isEmpty() || password.isEmpty()){
                    Toast.makeText(this,"You cannot leave any field empty",Toast.LENGTH_LONG).show();
                    return;
                }




                // upload the data
                if(!DatabaseHelper.isOnline()){
                    Toast.makeText(this,"Database is offline",Toast.LENGTH_SHORT).show();
                    return;
                }


                boolean inserting = AutoLoad.entryModel==null;

                if (inserting) {
                    DatabaseHelper.getInstance().insert(tag, username, password);
                } else {
                    DatabaseHelper.getInstance().update(username, password, AutoLoad.entryModel.id);
                }


                // remove entryModel

                AutoLoad.entryModel = null;

                if(!inserting)editFragment.tagEditText.setText("");
                editFragment.usernameEditText.setText("");
                editFragment.passwordEditText.setText("");

                editFragment.tagEditText.setEnabled(true);

                if(!inserting)loadFragment(viewFragment,true);

        }else if(currentFragment == logoutFragment){
            if(DatabaseHelper.isOnline()){
                DatabaseHelper.disconnect();
                actionButton.setImageDrawable(this.getResources().getDrawable(R.drawable.lock364));
                logoutFragment.changeText();
            }
            else loadFragment(accessFragment,false);
        }else if(currentFragment == viewFragment){
            loadFragment(editFragment,true);
        }
    }

    @Override
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