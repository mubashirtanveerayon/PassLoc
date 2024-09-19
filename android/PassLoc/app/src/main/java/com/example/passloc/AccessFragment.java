package com.example.passloc;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.constants.AutoLoad;
import com.example.constants.PassLoc;
import com.example.constants.SoftInputAssist;
import com.example.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;


public class AccessFragment extends Fragment {


    TextInputLayout accountInputLayout,passwordInputLayout,pinInputLayout;
    TextInputEditText accountEditText, passwordEditText,pinEditText;

    ScrollView scroll;


//    Button actionButton;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("testkey",getClass()+"OnCreateView");
        View view = inflater.inflate(R.layout.fragment_access, container, false);



        accountInputLayout = view.findViewById(R.id.account_name_input_layout);

        passwordInputLayout = view.findViewById(R.id.password_input_layout);

        pinInputLayout = view.findViewById(R.id.pin_input_layout);


        accountEditText = view.findViewById(R.id.account_name_edit_text);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        pinEditText = view.findViewById(R.id.pin_edit_text);
        scroll = view.findViewById(R.id.access_scroll_view);
//        actionButton = view.findViewById(R.id.action_button);






        return view;
    }

    public void onResume(){
        super.onResume();
        Log.i("testkey",getClass()+"OnResume");

        accountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0)return;
                if(PassLoc.getContext().getDatabasePath(charSequence.toString()).exists()){
                    accountInputLayout.setHelperText("Enter account name");
//                    actionButton.setText("Access");
                }else{
                    accountInputLayout.setHelperText("Enter new account name");
//                    actionButton.setText("Create");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passwordEditText.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent me){
                if(view.getId() == passwordEditText.getId()){
                    scrollToView(scroll,view);
                }
                return false;
            }
        });




        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.length()< PassLoc.getContext().getResources().getInteger(R.integer.password_min_length)){
                    passwordInputLayout.setError("Password is too short");
                }
                else{
                    passwordInputLayout.setHelperText("Enter password");
                    passwordInputLayout.setError("");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        pinEditText.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View view, MotionEvent me){
                if(view.getId() == pinEditText.getId()){
                    scrollToView(scroll,view);
                }
                return false;
            }
        });


        pinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int length = charSequence.length();
                if(length< PassLoc.getContext().getResources().getInteger(R.integer.pin_min_length)){

                    pinInputLayout.setError("Pin is too short");
                }else if(length>PassLoc.getContext().getResources().getInteger(R.integer.pin_max_length)){

                    pinInputLayout.setError("Pin is too large");
                }
                else{

                    pinInputLayout.setHelperText("Enter Pin");
                    pinInputLayout.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



//        actionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//
//
//                String databaseName = accountEditText.getText().toString();
//                String password = passwordEditText.getText().toString();
//                char[] pin = pinEditText.getText().toString().toCharArray();
//                int passwordMinLength = PassLoc.getContext().getResources().getInteger(R.integer.password_min_length);
//                int pinMinLength = PassLoc.getContext().getResources().getInteger(R.integer.pin_min_length);
//                int pinMaxLength = PassLoc.getContext().getResources().getInteger(R.integer.pin_max_length);
//                if(databaseName.isEmpty())return;
//                else if(password.length()<passwordMinLength){
//                    Toast.makeText(getActivity(),"Password must be at least "+passwordMinLength+" characters long",Toast.LENGTH_LONG).show();
//                    return;
//                }else if(pin.length < pinMinLength){
//                    Toast.makeText(getActivity(),"Pin must be at least "+pinMinLength+" digits long",Toast.LENGTH_LONG).show();
//                    return;
//                }else if(pin.length >pinMaxLength) {
//                    Toast.makeText(getActivity(), "Pin can be at most " + pinMaxLength + " digits long", Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                DatabaseHelper.disconnect();
//                try {
//                    DatabaseHelper.establishConnection(databaseName, password, pin);
//                    Toast.makeText(getActivity(),"Database connection established",Toast.LENGTH_SHORT).show();
//                }catch(Exception ex){
//                    Toast.makeText(getActivity(),ex.getMessage(),Toast.LENGTH_SHORT).show();
//                }
//
//
//
//
//
//            }
//        });
    }

    public void onPause(){

        Log.i("testkey",getClass()+"OnPause");

        accountEditText.setText("");
        passwordEditText.setText("");
        pinEditText.setText("");
        super.onPause();
    }

    private void scrollToView(final ScrollView scrollViewParent, final View view) {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);
        // Scroll to child.
        scrollViewParent.smoothScrollTo(0, childOffset.y);
    }


    private void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }

    public String getAccount(){return accountEditText.getText().toString();}
    public String getPassword(){return passwordEditText.getText().toString();}
    public char[] getPin(){return pinEditText.getText().toString().toCharArray();}


}