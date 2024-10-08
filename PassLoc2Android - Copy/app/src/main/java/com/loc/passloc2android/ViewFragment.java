package com.loc.passloc2android;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.loc.service.passloc.database.Database;
import com.loc.service.apputils.adapter.RecyclerAdapter;

import java.util.ArrayList;

import services.model.EntryModel;

public class ViewFragment extends Fragment {

    ArrayAdapter<String> tagListAdapter;
    ArrayList<String> tagList;
    ListView tagListView;

    RecyclerView entryRecyclerView;
    RecyclerAdapter entryAdapter;

    SearchView searchView;

    public ViewFragment(){
        tagList = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_view, container, false);

        searchView = view.findViewById(R.id.view_search_view);



        entryRecyclerView = view.findViewById(R.id.view_recycler_view);
        tagListView = view.findViewById(R.id.view_search_list);

        entryAdapter = new RecyclerAdapter(entryRecyclerView,(MainActivity) getActivity());

        tagListAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,tagList)/*{

            public View getView(int position,  View convertView,
                                 ViewGroup parent) {
                View v = super.getView(position,convertView,parent);

                TextView textView = v.findViewById(android.R.id.text1);
                textView.setTextColor(PassLocUI.getContext().getColor(R.color.theme_dark));

                return v;
            }

        }*/;

        tagListView.setAdapter(tagListAdapter);

        return view;
    }

    public void onResume(){
        super.onResume();
        tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                tagListView.setVisibility(View.GONE);
                entryRecyclerView.setVisibility(View.VISIBLE);
                TextView textView = view.findViewById(android.R.id.text1);
                loadData(textView.getText().toString());

            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(!Database.online()){
                    Toast.makeText(getActivity(),"Database is offline",Toast.LENGTH_SHORT).show();
                    return false;
                }

                tagListView.setVisibility(View.GONE);
                entryRecyclerView.setVisibility(View.VISIBLE);


                loadData(query);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                tagListAdapter.getFilter().filter(newText);

                if(tagListView.getVisibility() == View.INVISIBLE || tagListView.getVisibility() == View.GONE)tagListView.setVisibility(View.VISIBLE);
                if(entryRecyclerView.getVisibility() == View.VISIBLE)entryRecyclerView.setVisibility(View.GONE);

                return false;
            }
        });

        if(Database.online()){
            tagList.clear();
            tagList.addAll(Database.getInstance().getAllTags());
            tagListAdapter.notifyDataSetChanged();
        }

    }
    public void onPause(){
        super.onPause();
        entryAdapter.clearEntries();

        tagList.clear();
        tagListAdapter.notifyDataSetChanged();

    }



    private void loadData(String tag){
        ArrayList<EntryModel> entries = Database.getInstance().getEntries(tag);
        if(entries==null||entries.isEmpty()){
            entryAdapter.clearEntries();
            Toast.makeText(getActivity(),"No data found",Toast.LENGTH_SHORT).show();
            return ;
        }




        entryAdapter.putEntries(entries);

    }

}
