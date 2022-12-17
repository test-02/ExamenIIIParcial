package com.example.mycontacts;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.mycontacts.Clase.Contract;
import com.example.mycontacts.Conexion.Dbhelper;

public class Provider extends ContentProvider {

    public static final int Sitios = 100;
    public static final int Sitios_ID = 101;
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_SITIOS, Sitios);

        sUriMatcher.addURI(Contract.CONTENT_AUTHORITY, Contract.PATH_SITIOS + "/#", Sitios_ID);

    }

    public Dbhelper mDbhelper;

    @Override
    public boolean onCreate() {
        mDbhelper = new Dbhelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri,  String[] projection, String selection,   String[] selectionArgs,   String sortOrder) {
        SQLiteDatabase database  = mDbhelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case Sitios:
                cursor = database.query(Contract.SitiosEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case Sitios_ID:

                selection = Contract.SitiosEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(Contract.SitiosEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cant Query" + uri);


        }


       cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }



    @Override
    public String getType( Uri uri) {
        return null;
    }

    @Override
    public Uri insert(  Uri uri,  ContentValues values) {

        int match = sUriMatcher.match(uri);
        switch (match) {
            case Sitios:
                return insertContact(uri, values);

            default:
                throw new IllegalArgumentException("Cannot insert sitio" + uri);
        }

    }

    private Uri insertContact(Uri uri, ContentValues values) {

        String name = values.getAsString(Contract.SitiosEntry.COLUMN_Descripcion);
        if (name == null) {
            throw new IllegalArgumentException("Descripcion requerida");
        }

        String email = values.getAsString(Contract.SitiosEntry.COLUMN_Fecha);
        if (email == null) {
            throw new IllegalArgumentException("Fecha requerida");
        }

        SQLiteDatabase database = mDbhelper.getWritableDatabase();
        long id = database.insert(Contract.SitiosEntry.TABLE_NAME, null, values);

        if (id == -1) {
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);


    }

    @Override
    public int delete ( Uri uri,   String selection,  String[] selectionArgs) {

        int rowsDeleted;
        SQLiteDatabase database = mDbhelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match) {
            case Sitios:
                rowsDeleted = database.delete(Contract.SitiosEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case Sitios_ID:
                selection = Contract.SitiosEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(Contract.SitiosEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Cannot delete" + uri);



        }

        if (rowsDeleted!=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(  Uri uri,  ContentValues values,  String selection,  String[] selectionArgs) {
        // here we can update row by row also
        int match = sUriMatcher.match(uri);
        switch (match) {
            case Sitios:
                return updateContact(uri, values, selection, selectionArgs);

            case Sitios_ID:

                selection = Contract.SitiosEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateContact(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException(" Cannot update the sitio");


        }
    }

    private int updateContact(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(Contract.SitiosEntry.COLUMN_Descripcion)) {
            String name = values.getAsString(Contract.SitiosEntry.COLUMN_Descripcion);
           if (name == null) {
            throw new IllegalArgumentException("Descripcion requerida");
            }
        }

        if (values.containsKey(Contract.SitiosEntry.COLUMN_Fecha)) {
            String email = values.getAsString(Contract.SitiosEntry.COLUMN_Fecha);
            if (email == null) {
                throw new IllegalArgumentException("Fecha requerida");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbhelper.getWritableDatabase();
        int rowsUpdated = database.update(Contract.SitiosEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated!=0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;

    }
}
