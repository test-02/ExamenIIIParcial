package com.example.mycontacts.Conexion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.mycontacts.Clase.Contract.SitiosEntry;

public class Dbhelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "sitios.db";
    public static final int DATABASE_VERSION = 1;



    public Dbhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_TABLE = "CREATE TABLE " + SitiosEntry.TABLE_NAME + " ("
                + SitiosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SitiosEntry.COLUMN_Descripcion + " TEXT NOT NULL, "
                + SitiosEntry.COLUMN_Fecha + " TEXT NOT NULL, "
                + SitiosEntry.COLUMN_PICTURE  + " TEXT);";

                db.execSQL(SQL_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
