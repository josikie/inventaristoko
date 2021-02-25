package com.example.inventaristoko.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.inventaristoko.R;
import com.example.inventaristoko.data.ProductDbHelper;

import org.w3c.dom.Text;

import java.sql.SQLInput;

import static com.example.inventaristoko.data.ProductContract.ProductEntry.COLUMN_NAME;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.COLUMN_SUPPLIER_CONTACT;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.CONTENT_URI;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.TABLE_NAME;
import static com.example.inventaristoko.data.ProductContract.ProductEntry._ID;

public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private EditText edtName;
    private EditText edtPrice;
    private EditText edtQuantity;
    private EditText edtSupplierContact;
    private Uri currentUri;
    private final static int LOAD_ITEM = 2;
    private boolean productHasChanged = false;

    //OnTouchListener  that listens for any user touches on a View, implying they are modifying
    // the View, and we change the productHasChanged boolean to true
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

         Intent i = getIntent();
         currentUri = i.getData();

        Button btnSave = findViewById(R.id.btn_save_product);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct();
                finish();
            }
        });

        if (currentUri == null){
            // This is a new product, so change the app bar to say "Add a Product"
            setTitle("Add a Product");
            // This is a new product, so change the button to say "Save Product"
            btnSave.setText(R.string.saved_product);
            //Invalidate the options menu, so the "Delete" menu option can be hidden
            //(It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        }else{
            // This is an existing product, so change the app bar to say "Edit a Product"
            setTitle("Edit a Product");
            // This is a new product, so change the button to say "Update Product"
            btnSave.setText(R.string.updated_product);
            getLoaderManager().initLoader(LOAD_ITEM, null, this);

        }

        edtName  = findViewById(R.id.edt_name);
        edtPrice = findViewById(R.id.edt_price);
        edtQuantity = findViewById(R.id.edt_quantity);
        edtSupplierContact = findViewById(R.id.edt_supplier);

        edtName.setOnTouchListener(touchListener);
        edtPrice.setOnTouchListener(touchListener);
        edtQuantity.setOnTouchListener(touchListener);
        edtSupplierContact.setOnTouchListener(touchListener);

    }

    private void showDeleteConfirmationDialog(){
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //User clicked the "Delete" button, so delete the product.
                deleteProduct();
                finish();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the product in the database
     */
    private void deleteProduct(){
        // TODO: Implement this method
        int deletedRow = getContentResolver().delete(currentUri, null, null);
        if (deletedRow != 0){
            Toast.makeText(this, R.string.editor_delete_product_successful, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, R.string.editor_delete_product_failed, Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void showUnsavedChangeDialog(DialogInterface.OnClickListener discardButtonClick){
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClick);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                // User clicked the "Keep Editing" button, so dismiss the dialog
                // and continue editing the product.
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {

        if (!productHasChanged){
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" Button, close the current activity
                        finish();
                    }
                };

        //show dialog that there are unsaved changed
        showUnsavedChangeDialog(discardButtonClickListener);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        //If this a new product, hide the "Delete" menu item.
        if (currentUri == null){
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    public void saveProduct(){
        int intQuantity = 0;

        String name = edtName.getText().toString().trim();
        String price = edtPrice.getText().toString().trim();
        String quantity = edtQuantity.getText().toString().trim();
        String supplierContact = edtSupplierContact.getText().toString().trim();

        //check th fields, if the name and price fields == null, return;
        if (TextUtils.isEmpty(name)){
            return;
        }else if(TextUtils.isEmpty(price)){
            return;
        }

        if (!TextUtils.isEmpty(quantity)){
            intQuantity = Integer.parseInt(quantity);
        }
        int intPrice = Integer.parseInt(price);

        // store many bunch of the data from edtText
        ContentValues cvProduct = new ContentValues();
        cvProduct.put(COLUMN_NAME, name);
        cvProduct.put(COLUMN_PRICE, intPrice);
        cvProduct.put(COLUMN_QUANTITY, intQuantity);
        cvProduct.put(COLUMN_SUPPLIER_CONTACT, supplierContact);

        if (currentUri == null){
            Uri uri = getContentResolver().insert(CONTENT_URI, cvProduct);

            // make a toast to inform that we successful or failed
            if (uri != null){
                Toast.makeText(this, R.string.product_saved, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, R.string.error_saving_product, Toast.LENGTH_SHORT).show();
            }
        }else{
            int updatedRow = getContentResolver().update(currentUri, cvProduct, null, null);
            if (updatedRow != 0){
                Toast.makeText(this, R.string.product_updated, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, R.string.error_updating_product, Toast.LENGTH_LONG).show();
            }
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu
        getMenuInflater().inflate(R.menu.editor, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // the menu
        switch (item.getItemId()){
            case R.id.action_save :
                // save the new pet into database
                saveProduct();
                // exit acticity
                finish();
                return true;
            case R.id.action_delete :
                // delete the existing pet from database
                if (currentUri != null){
                    showDeleteConfirmationDialog();
                }
                return true;
            // Respond to a click on tht "Up" arrow button in the app bar
            case android.R.id.home:
                //If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link ListActivity}
                if (!productHasChanged){
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangeDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection = {
                _ID,
                COLUMN_NAME,
                COLUMN_PRICE,
                COLUMN_QUANTITY,
                COLUMN_SUPPLIER_CONTACT
        };

        switch (id){
            case LOAD_ITEM:
                return new CursorLoader(
                        this,
                        currentUri,
                        projection,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()){

            // find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(COLUMN_NAME);
            int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
            int supplierContactIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_CONTACT);

            // extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierContact = cursor.getString(supplierContactIndex);

            // update the views on the screen with the values from the database
            edtName.setText(name);
            edtPrice.setText(Integer.toString(price));
            edtQuantity.setText(Integer.toString(quantity));
            edtSupplierContact.setText(supplierContact);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (edtName != null){
            edtName.getText().clear();
            edtPrice.getText().clear();
            edtQuantity.getText().clear();
            edtSupplierContact.getText().clear();
        }
    }




}