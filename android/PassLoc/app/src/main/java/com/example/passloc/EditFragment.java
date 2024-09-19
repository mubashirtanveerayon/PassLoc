package com.example.passloc;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
import com.example.database.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class EditFragment extends Fragment {




    TextInputEditText passwordEditText, usernameEditText,tagEditText;

//    Button submitButton;

    ScrollView scroll;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_edit, container, false);


        tagEditText = view.findViewById(R.id.edit_tag_edit_text);
        usernameEditText = view.findViewById(R.id.edit_username_edit_text);
        passwordEditText = view.findViewById(R.id.edit_password_edit_text);
        scroll = view.findViewById(R.id.edit_scroll_view);


//        submitButton = view.findViewById(R.id.edit_action_button);


        return view;
    }


    public void onResume(){
        super.onResume();

        usernameEditText.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(view.getId() == usernameEditText.getId())
                    scroll.smoothScrollTo(0,view.getBottom());
//                    scrollToView(scroll,view);

                return false;
            }


        });



        passwordEditText.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent me){
                if(v.getId() == passwordEditText.getId()){
//                    scroll.smoothScrollTo(0,v.getBottom());
                    scrollToView(scroll,v);
                    v.getParent().requestDisallowInterceptTouchEvent(true);

                    switch(me.getAction() & MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_UP: {

                            v.getParent().requestDisallowInterceptTouchEvent(false);

                            break;
                        }
                    }

                }
                return false;
            }
        });

//        submitButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String tag = tagEditText.getText().toString();
//                String username = usernameEditText.getText().toString();
//                String password = passwordEditText.getText().toString();
//
//
//                if(tag.isEmpty() || username.isEmpty() || password.isEmpty()){
//                    Toast.makeText(getActivity(),"You cannot leave any field empty",Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//
//
//
//                // upload the data
//                if(!DatabaseHelper.isOnline()){
//                    Toast.makeText(getActivity(),"Database is offline",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                try {
//                    if (AutoLoad.entryModel == null) {
//                        DatabaseHelper.getInstance().insert(tag, username, password);
//                    } else {
//                        DatabaseHelper.getInstance().update(username, password, AutoLoad.entryModel.id);
//                    }
//                    Toast.makeText(getActivity(),"Data push successful",Toast.LENGTH_SHORT).show();
//                }catch(Exception ex){
//                    Toast.makeText(getActivity(),ex.getMessage(),Toast.LENGTH_LONG).show();
//                }
//
//                // remove entryModel
//
//                AutoLoad.entryModel = null;
//
//                tagEditText.setText("");
//                usernameEditText.setText("");
//                passwordEditText.setText("");
//
//                tagEditText.setEnabled(true);
//
//            }
//        });

        if(AutoLoad.entryModel != null){
            tagEditText.setText(AutoLoad.entryModel.tag);
            usernameEditText.setText(AutoLoad.entryModel.username);
            passwordEditText.setText(AutoLoad.entryModel.password);
            tagEditText.setEnabled(false);
        }else{
            tagEditText.setEnabled(true);
        }

    }

    public void onPause(){
        tagEditText.setText("");
        usernameEditText.setText("");
        passwordEditText.setText("");

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

    public String getEntryTag(){return tagEditText.getText().toString();}
    public String getEntryUsername(){return usernameEditText.getText().toString();}
    public String getEntryPassword(){return passwordEditText.getText().toString();}


}