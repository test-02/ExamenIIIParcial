package com.example.mycontacts;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mycontacts.Clase.Contract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public Adapter mAdapter;
    public static final  int SitioLOADER = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView listView = findViewById(R.id.list);
        mAdapter = new Adapter(this, null);
        listView.setAdapter(mAdapter);

        // whenever we press a listview for updating
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri newUri = ContentUris.withAppendedId(Contract.SitiosEntry.CONTENT_URI, id);
                intent.setData(newUri);
                startActivity(intent);

            }
        });

        // get the loader running
        getLoaderManager().initLoader(SitioLOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {Contract.SitiosEntry._ID,
                Contract.SitiosEntry.COLUMN_Descripcion,
                Contract.SitiosEntry.COLUMN_Fecha,
                Contract.SitiosEntry.COLUMN_PICTURE
        };

        return new CursorLoader(this, Contract.SitiosEntry.CONTENT_URI,
                projection, null,
                null,
                null);
    }



    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mAdapter.swapCursor(null);

    }
}