package com.example.list;



import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.constants.AutoLoad;
import com.example.constants.PassLoc;
import com.example.database.DatabaseHelper;
import com.example.models.EntryModel;
import com.example.passloc.R;
import com.example.passloc.ViewFragment;
import com.example.test.Test;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> implements  RecyclerViewItemClick{

    private ArrayList<EntryModel>entries;

    ViewFragment parentViewFragment;

    public RecyclerAdapter(ViewFragment fragment){
        entries = new ArrayList<>();
        parentViewFragment=fragment;
    }

    public void putEntries(ArrayList<EntryModel>entries){
        clearEntries();
        for(EntryModel model:entries){
            this.entries.add(model);
            notifyItemInserted(getItemCount()-1);
        }

    }

    public void clearEntries(){
        int size = entries.size();
        entries.clear();
        notifyItemRangeRemoved(0,size);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.entry_row_item,parent,false);



        ViewHolder viewHolder = new ViewHolder(view);



        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.bottomMargin =300; // last item bottom margin
            holder.itemView.setLayoutParams(params);

        } else {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.bottomMargin = 10; // other items bottom margin
            holder.itemView.setLayoutParams(params);
        }
        EntryModel entry = entries.get(position);
        holder.usernameText.setText(entry.username);
        String placeHolderPassword = "";
        for(int i=0;i<entry.password.length();i++)placeHolderPassword+=(char)8226;
        holder.passwordText.setText(placeHolderPassword);

    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    @Override
    public void onItemClick(int position) {


        AutoLoad.entryModel = entries.get(position);

        parentViewFragment.loadEditFragment();


    }

    @Override
    public void onItemLongClick(int position) {

        if(!DatabaseHelper.isOnline())
            return;



        String id =  entries.get(position).id;

        DatabaseHelper.getInstance().delete(id);
        entries.remove(position);
        notifyItemRemoved(position);

        Toast.makeText(parentViewFragment.getActivity(),"Entry deleted.",Toast.LENGTH_SHORT);

    }





    public void onMenuButtonClick(int position,PopupMenu popupMenu){

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                long itemId = item.getItemId();
                if(itemId ==  R.id.copy_username){
                    ClipboardManager clipboard = (ClipboardManager)parentViewFragment.getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("passloc_entry", entries.get(position).username);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(parentViewFragment.getActivity(),"Username copied to clipboard",Toast.LENGTH_SHORT).show();
                    return true;
                }else if(itemId == R.id.copy_password){
                    ClipboardManager clipboard = (ClipboardManager)parentViewFragment.getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("passloc_entry", entries.get(position).password);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(parentViewFragment.getActivity(),"Password copied to clipboard",Toast.LENGTH_SHORT).show();
                    return true;
                }else if(itemId == R.id.copy_username_and_password){
                    ClipboardManager clipboard = (ClipboardManager)parentViewFragment.getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                    EntryModel entry = entries.get(position);
                    ClipData clip = ClipData.newPlainText("passloc_entry", "Username:"+entry.username+" | "+"Password:"+entry.password);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(parentViewFragment.getActivity(),"Username & password copied to clipboard",Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }
        });

    }


    class ViewHolder extends RecyclerView.ViewHolder {

        TextView usernameText,passwordText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.entry_username_text);

            passwordText = itemView.findViewById(R.id.entry_password_text);

            ImageButton menuButton = itemView.findViewById(R.id.more_option_button);
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(v.getContext(),v);
                    menu.inflate(R.menu.entry_row_popup);
                    onMenuButtonClick(getAdapterPosition(),menu);
                    menu.show();
                }
            });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemLongClick(getAdapterPosition());
                return false;
            }
        });






        }



    }



}
