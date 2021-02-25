package com.example.inventaristoko.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.example.inventaristoko.data.ProductContract.SQL_CREATE_ENTRIES;
import static com.example.inventaristoko.data.ProductContract.SQL_DELETE_ENTRIES;

public class ProductDbHelper extends SQLiteOpenHelper {
    // database name
    public static final String DATABASE_NAME = "inventaris.db";

    //version of database, increment this if you make the scheme of database changes.
    public static final int DATABASE_VERSION = 1;

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
