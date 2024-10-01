package com.loc.passloc2android;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.loc.service.passloc.database.Database;

public class LogoutFragment extends Fragment {

    TextView logoutTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {




        View view = inflater.inflate(R.layout.fragment_logout, container, false);

        logoutTextView = view.findViewById(R.id.logout_text_view);

        return view;
    }

    public void onResume(){
        super.onResume();

        changeText();
    }

    public void changeText(){
        if(Database.online())logoutTextView.setText(getResources().getString(R.string.to_be_logged_out));
        else logoutTextView.setText(getResources().getString(R.string.not_logged_in));
    }
}
