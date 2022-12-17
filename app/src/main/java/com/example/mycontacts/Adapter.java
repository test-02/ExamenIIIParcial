package com.example.mycontacts;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mycontacts.Clase.Contract;

public class Adapter extends CursorAdapter {


    public Adapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.listitem, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView descripcionView, fechaView;
        ImageView mContactImageView;

        descripcionView = view.findViewById(R.id.textDescripcion);
        fechaView = view.findViewById(R.id.textFecha);
        mContactImageView = view.findViewById(R.id.imageView2);

        int descripcion = cursor.getColumnIndex(Contract.SitiosEntry.COLUMN_Descripcion);
        int fecha = cursor.getColumnIndex(Contract.SitiosEntry.COLUMN_Fecha);
        int picture = cursor.getColumnIndex(Contract.SitiosEntry.COLUMN_PICTURE);

        String sitiodescripcion = cursor.getString(descripcion);
        String sitiofecha = cursor.getString(fecha);
        String sitiopicture = cursor.getString(picture);
        Uri imageUri = Uri.parse(sitiopicture);

        descripcionView.setText(sitiodescripcion);
        fechaView.setText(sitiofecha);
        mContactImageView.setImageURI(imageUri);
    }
}
