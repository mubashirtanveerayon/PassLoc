package com.loc.passloc2android;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.loc.service.passloc.database.Database;

import services.secure.Credential;

public class LoginFragment extends Fragment {


    TextInputEditText dbNameEditText, passwordEditText,masterPasswordEditText;


//    TextView helpTextView;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        dbNameEditText = view.findViewById(R.id.login_db_name_edit_text);
        passwordEditText = view.findViewById(R.id.login_password_edit_text);
        masterPasswordEditText = view.findViewById(R.id.login_master_password_edit_text);





//        helpTextView = view.findViewById(R.id.login_need_help_text_view);





        return view;


    }

//    public void onResume() {
//        super.onResume();
//
//
//        helpTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity mainActivity = (MainActivity) getActivity();
//                mainActivity.loadFragment(mainActivity.helpFragment, true);
//            }
//        });
//    }

    public String getDatabaseName(){
        return dbNameEditText.getText().toString();
    }

    public String getDatabasePassword(){
        return passwordEditText.getText().toString();
    }

    public String getMasterPassword(){
        return masterPasswordEditText.getText().toString();
    }


    public void onResume(){
        super.onResume();
        logout();
    }


    public void login(String dbName,String dbPassword,String msPassword){
        logout();
        Credential.getInstance(msPassword);
        try {
            Database.establishConnection(dbName, dbPassword);
            Toast.makeText(getActivity(), "Login successful.", Toast.LENGTH_SHORT).show();

        }catch (Exception ex){
            Log.e("exception",ex.getMessage());
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void logout(){

        if(!Database.online())
            return;

        Credential.resetInstance();

        Database.disconnect();

        Toast.makeText(getActivity(),"Log out successful.",Toast.LENGTH_SHORT).show();


    }

    public void onPause(){
        super.onPause();
        dbNameEditText.setText("");
        passwordEditText.setText("");
        masterPasswordEditText.setText("");
    }

}
