package com.loc.passloc2android;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.loc.service.passloc.database.Database;
import com.loc.service.passloc.model.EntryModel;

public class EditFragment extends Fragment {

    private EditText tagEditText,usernameEditText,passwordEditText;

    Button generatePasswordButton;
    private String updateId = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_edit, container, false);

        tagEditText = view.findViewById(R.id.edit_tag_edit_text);
        usernameEditText = view.findViewById(R.id.edit_username_edit_text);
        passwordEditText = view.findViewById(R.id.edit_password_edit_text);

        generatePasswordButton = view.findViewById(R.id.edit_generate_button);



        return view;
    }

    public String getEntryTag(){return tagEditText.getText().toString();}
    public String getEntryUsername(){return usernameEditText.getText().toString();}
    public String getEntryPassword(){return passwordEditText.getText().toString();}

    public void clearUsername(){usernameEditText.setText("");}
    public void clearPassword(){passwordEditText.setText("");}
    public void clearTag(){tagEditText.setText("");}

    public void setTagEditTextEnabled(boolean b){
        tagEditText.setEnabled(b);
    }



    public void onResume(){
        super.onResume();

//        if(EntryModel.staticEntry != null){
//            tagEditText.setText(EntryModel.staticEntry.getTag());
//            usernameEditText.setText(EntryModel.staticEntry.getUsername());
//            passwordEditText.setText(EntryModel.staticEntry.getPassword());
//            tagEditText.setEnabled(false);
//        }else{
//            tagEditText.setEnabled(true);
//        }

        generatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.loadFragment(mainActivity.generateFragment,true);

            }
        });
    }

    public void setEntry(EntryModel entry){
        updateId = entry.getId();
        tagEditText.setText(entry.getTag());
        usernameEditText.setText(entry.getUsername());
        passwordEditText.setText(entry.getPassword());
        tagEditText.setEnabled(false);

    }

    public void insertOrUpdate() {
        if(isInserting()){
            Database.getInstance().insert(new EntryModel(tagEditText.getText().toString(),usernameEditText.getText().toString(),passwordEditText.getText().toString()));
        }else{
            Database.getInstance().update(new EntryModel(updateId,tagEditText.getText().toString(),usernameEditText.getText().toString(),passwordEditText.getText().toString()));
        }
        updateId = null;
    }

    public boolean isInserting() {
        return updateId == null;
    }
}
