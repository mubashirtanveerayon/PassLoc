package com.example.passloc;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.database.DatabaseHelper;

public class LogoutFragment extends Fragment {

    TextView logoutText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_logout, container, false);
        logoutText = view.findViewById(R.id.logout_text_view);
        return view;
    }

    public void onResume(){
        super.onResume();

        changeText();
    }

    public void changeText(){
        if(DatabaseHelper.isOnline())logoutText.setText(getResources().getString(R.string.to_be_logged_out));
        else logoutText.setText(getResources().getString(R.string.not_logged_in));
    }

}