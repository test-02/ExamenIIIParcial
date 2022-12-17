package com.example.mycontacts;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mycontacts.Clase.Contract;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    EditText txtDescripcion, txtFecha;
    private Uri mPhotoUri;
    private Uri mCurrentSitioUri;

    ImageView mPhoto;

    private boolean mSitioCambiado = false;

    public static final int LOADER = 0;

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mSitioCambiado = true;
            return false;
        }
    };

    boolean hasAllRequiredValues = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentSitioUri = intent.getData();

        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtFecha = findViewById(R.id.txtFecha);
        mPhoto = findViewById(R.id.imageView);

        Calendar c = Calendar.getInstance(); System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd-MMMM-yyyy");
        String formattedDate = df.format(c.getTime());
        txtFecha.setText("" + formattedDate);

        if (mCurrentSitioUri == null) {
            mPhoto.setImageResource(R.drawable.photo);
            setTitle("Agregar Sitio");

            invalidateOptionsMenu();

        } else {
            setTitle("Editar Sitio");
            getLoaderManager().initLoader(LOADER, null, this);

        }

        txtDescripcion.setOnTouchListener(mOnTouchListener);
        txtFecha.setOnTouchListener(mOnTouchListener);
        mPhoto.setOnTouchListener(mOnTouchListener);

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trySelector();
                mSitioCambiado = true;
            }
        });

    }


    public void trySelector() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return;
        }
        openSelector();
    }

    private void openSelector() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType(getString(R.string.intent_type));
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openSelector();
                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mPhotoUri = data.getData();
                mPhoto.setImageURI(mPhotoUri);
                mPhoto.invalidate();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menueditor, menu);
        return true;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentSitioUri == null) {
            MenuItem item = (MenuItem) menu.findItem(R.id.delete);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                saveContact();
                if (hasAllRequiredValues == true) {
                    finish();
                }
                return true;

            case R.id.delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mSitioCambiado) {

                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;

                }

                DialogInterface.OnClickListener discardButton = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);

                    }
                };
                showUnsavedChangesDialog(discardButton);
                return true;



        }
            return super.onOptionsItemSelected(item);
    }

    private boolean saveContact() {
        String descripcion = txtDescripcion.getText().toString().trim();
        String fecha = txtFecha.getText().toString().trim();
//        String photoUri = mPhotoUri.toString().trim();
        String photoUri = mPhotoUri != null ? mPhotoUri.toString() : null;

        if (mCurrentSitioUri == null && TextUtils.isEmpty(descripcion)
        && TextUtils.isEmpty(fecha) && mPhotoUri == null) {

            hasAllRequiredValues = true;
            return hasAllRequiredValues;

        }

        ContentValues values = new ContentValues();

        if (TextUtils.isEmpty(descripcion)) {
            Toast.makeText(this, "La descripcion es requerida", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;


        } else {
            values.put(Contract.SitiosEntry.COLUMN_Descripcion, descripcion);
        }

        if (TextUtils.isEmpty(fecha)) {
            Toast.makeText(this, "La fecha es requerida", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;


        } else {
            values.put(Contract.SitiosEntry.COLUMN_Fecha, fecha);
        }


        if (TextUtils.isEmpty(photoUri)) {
            Toast.makeText(this, "La imagen es requerida", Toast.LENGTH_SHORT).show();
            return hasAllRequiredValues;

        } else {
            values.put(Contract.SitiosEntry.COLUMN_PICTURE, mPhotoUri.toString());
        }

        if (mCurrentSitioUri == null) {

            Uri newUri = getContentResolver().insert(Contract.SitiosEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show();

            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentSitioUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Actualizado correctamente", Toast.LENGTH_SHORT).show();

            }

        }

        hasAllRequiredValues = true;

        return hasAllRequiredValues;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

       String[] projection = {Contract.SitiosEntry._ID,
               Contract.SitiosEntry.COLUMN_Descripcion,
               Contract.SitiosEntry.COLUMN_Fecha,
               Contract.SitiosEntry.COLUMN_PICTURE
       };

       return new CursorLoader(this, mCurrentSitioUri,
               projection, null,
               null,
               null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            int descripcion = cursor.getColumnIndex(Contract.SitiosEntry.COLUMN_Descripcion);
            int fecha = cursor.getColumnIndex(Contract.SitiosEntry.COLUMN_Fecha);
            int picture = cursor.getColumnIndex(Contract.SitiosEntry.COLUMN_PICTURE);

            String sitioDescripcion = cursor.getString(descripcion);
            String sitioFecha = cursor.getString(fecha);
            String contactpicture = cursor.getString(picture);
            mPhotoUri = Uri.parse(contactpicture);

            txtDescripcion.setText(sitioDescripcion);
            txtFecha.setText(sitioFecha);
            mPhoto.setImageURI(mPhotoUri);

        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        txtDescripcion.setText("");
        txtFecha.setText("");
        mPhoto.setImageResource(R.drawable.photo);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (mCurrentSitioUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentSitioUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

    @Override
    public void onBackPressed() {
        if (!mSitioCambiado) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };

        showUnsavedChangesDialog(discardButtonClickListener);
    }
}

