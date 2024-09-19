package com.example.passloc;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.database.DatabaseHelper;
import com.example.list.RecyclerAdapter;
import com.example.models.EntryModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;


public class ViewFragment extends Fragment {




    RecyclerView recyclerView;
    RecyclerAdapter adapter;


    TextInputLayout searchBar;
    TextInputEditText searchEditText;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i("testkey",getClass()+"OnCreateView");
        View view =  inflater.inflate(R.layout.fragment_view, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);

        searchBar =  view.findViewById(R.id.view_search_bar);

        searchEditText = view.findViewById(R.id.view_tag_edit_text);

        adapter = new RecyclerAdapter(this);




        recyclerView.setAdapter(adapter);

        return view;
    }

    public void onResume(){
        super.onResume();
        Log.i("testkey",getClass()+"OnResume");



        searchBar.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!DatabaseHelper.isOnline()){
                    Toast.makeText(getActivity(),"Database is offline",Toast.LENGTH_LONG).show();
                    return;

                }

                HashMap<String,ArrayList<String[]>> allData = DatabaseHelper.getInstance().loadData();
                ArrayList<String[]>entries = allData.get(searchEditText.getText().toString());
                if(entries==null||entries.isEmpty()){
                    Toast.makeText(getActivity(),"No data found",Toast.LENGTH_LONG).show();
                    return;
                }

                ArrayList<EntryModel> models =  new ArrayList<>();

                for(String[] data:entries){
                    EntryModel model = new EntryModel(data[0],data[1],data[2],data[3]);
                    models.add(model);
                }

                adapter.putEntries(models);


            }
        });


    }

    public void onPause(){
        super.onPause();
        Log.i("testkey",getClass()+"OnPause");
        adapter.clearEntries();

        searchEditText.setText("");

    }

    public void loadEditFragment() {
//        FragmentManager manager = getActivity().getSupportFragmentManager();
//        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.loadFragment(mainActivity.editFragment,true);

    }
}