package com.example.list;

import android.widget.PopupMenu;

public interface RecyclerViewItemClick{
    public void onItemClick(int position);
    public void onItemLongClick(int position);
    public void onMenuButtonClick(int position,PopupMenu popupMenu);
}
