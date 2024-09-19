package com.example.database;

import net.zetetic.database.sqlcipher.SQLiteDatabase;

public interface DatabaseListener {

    public void onSuccess(String message);
    public void onFailure(String message);

    public void onUpgrade(String message);
}
