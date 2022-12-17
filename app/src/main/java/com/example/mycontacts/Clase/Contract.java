package com.example.mycontacts.Clase;

import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {

    public Contract() {
        //
    }

    public static final String CONTENT_AUTHORITY = "com.example.mycontacts";
    public static final Uri BASE_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);

    public static final String PATH_SITIOS = "mysitios";

    public static abstract class SitiosEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_SITIOS);

        public static final String TABLE_NAME = "mysitios";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_Descripcion = "descripcion";
        public static final String COLUMN_Fecha = "fecha";
        public static final String COLUMN_PICTURE = "picture";

    }
}
