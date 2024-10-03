package com.loc.service.apputils.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.loc.passloc2android.MainActivity;
import com.loc.passloc2android.R;
import com.loc.service.passloc.database.Database;


import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import services.model.EntryModel;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<EntryModel> entries;

    private RecyclerView itemRecyclerView;


    MainActivity mainActivity;

    public RecyclerAdapter(RecyclerView recyclerView, MainActivity main){
        itemRecyclerView = recyclerView;
        entries = new ArrayList<>();
        mainActivity = main;
        itemRecyclerView.setAdapter(this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeActionCallback);

        itemTouchHelper.attachToRecyclerView ( itemRecyclerView);

    }

    ItemTouchHelper.SimpleCallback swipeActionCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {


        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();

            if(direction == ItemTouchHelper.LEFT){
                sendForDelete(position);

            }else if(direction == ItemTouchHelper.RIGHT){
                sendForEdit(position);
            }

        }


        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(mainActivity, R.color.error))
                    .addCornerRadius(TypedValue.COMPLEX_UNIT_DIP,15)
                    .addSwipeLeftActionIcon(R.drawable.delete24)
                    .addSwipeRightActionIcon(R.drawable.edit32)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(mainActivity,R.color.sky))
                    .create()
                    .decorate();



            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);




        }

    };


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.entry_row_item,parent,false);






        return new ViewHolder(view);
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


    private void sendForEdit(int position){
        mainActivity.editFragment.setEntry(entries.get(position));
        mainActivity.loadFragment(mainActivity.editFragment,true);

    }

    private void sendForDelete(int position){
        if(!Database.online()) {
            Toast.makeText(mainActivity, "Database is offline", Toast.LENGTH_SHORT).show();

            return;
        }


        EntryModel deletedEntry = entries.get(position);

        String id =  deletedEntry.getId();

        Database.getInstance().delete(id);
        entries.remove(position);
        notifyItemRemoved(position);

        Snackbar.make(mainActivity.navigationView,"Entry deleted",Snackbar.LENGTH_SHORT)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Database.online()){
                            Database.getInstance().insert(deletedEntry);
                            entries.add(position,deletedEntry);
                            notifyItemInserted(position);
                        }
                    }
                }).show();


        Toast.makeText(mainActivity,"Entry deleted",Toast.LENGTH_SHORT).show();
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.bottomMargin = 330; // last item bottom margin
            holder.itemView.setLayoutParams(params);

        } else {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.bottomMargin = 20; // other items bottom margin
            holder.itemView.setLayoutParams(params);
        }
        EntryModel entry = entries.get(position);
        holder.usernameText.setText(entry.getUsername());
        String placeHolderPassword = "";

        if(entry.getPassword().length()>30){
            for(int i=0;i<30;i++)placeHolderPassword+=(char)8226;
            placeHolderPassword += "...";
        }else{
            for(int i=0;i<entry.getPassword().length();i++)placeHolderPassword+=(char)8226;
        }
        holder.passwordText.setText(placeHolderPassword);

    }

    @Override
    public int getItemCount() {
        return entries.size();
    }


    public void onMenuButtonClick(int position, PopupMenu popupMenu){

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                long itemId = item.getItemId();
                if(itemId ==  R.id.copy_username){
                    ClipboardManager clipboard = (ClipboardManager)mainActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("passloc_entry", entries.get(position).getUsername());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(mainActivity,"Username copied to clipboard",Toast.LENGTH_SHORT).show();
                    return true;
                }else if(itemId == R.id.copy_password){
                    ClipboardManager clipboard = (ClipboardManager)mainActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("passloc_entry", entries.get(position).getPassword());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(mainActivity,"Password copied to clipboard",Toast.LENGTH_SHORT).show();
                    return true;
                }

                return false;
            }
        });

    }


    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView usernameText, passwordText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            usernameText = itemView.findViewById(R.id.entry_username_text);

            passwordText = itemView.findViewById(R.id.entry_password_text);


            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int id = v.getId();
                    int position = getAdapterPosition();

                    if(id == R.id.entry_edit_button){
                        sendForEdit(position);
                    }else if(id == R.id.entry_delete_button){
                        sendForDelete(position);
                    }else if(id == R.id.entry_copy_button){
                        PopupMenu menu = new PopupMenu(v.getContext(),v);
                        menu.inflate(R.menu.entry_row_popup);
                        onMenuButtonClick(position,menu);
                        menu.show();
                    }


                }
            };
            ImageButton editButton = itemView.findViewById(R.id.entry_edit_button);
            ImageButton deleteButton = itemView.findViewById(R.id.entry_delete_button);
            ImageButton copyButton = itemView.findViewById(R.id.entry_copy_button);
            editButton.setOnClickListener(listener);
            deleteButton.setOnClickListener(listener);
            copyButton.setOnClickListener(listener);




            }

        }

    }
