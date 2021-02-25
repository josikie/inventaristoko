package com.example.inventaristoko.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.inventaristoko.InventarisAdapter;
import com.example.inventaristoko.R;
import com.example.inventaristoko.data.ProductDbHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static com.example.inventaristoko.data.ProductContract.CONTENT_AUTHORITY;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.COLUMN_NAME;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.COLUMN_SUPPLIER_CONTACT;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.CONTENT_URI;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.TABLE_NAME;
import static com.example.inventaristoko.data.ProductContract.ProductEntry._ID;

public class ListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private InventarisAdapter inventarisAdapter;
    private final static int LOAD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        FloatingActionButton floatingActionButton = findViewById(R.id.fab_button);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the product data
        ListView listView = findViewById(R.id.listview_inventaris);

        // find and set empty view on the ListView, so that it only shows when the list has 0 items
        View view = findViewById(R.id.empty_view);
        listView.setEmptyView(view);
        inventarisAdapter = new InventarisAdapter(ListActivity.this, null);

        listView.setAdapter(inventarisAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(ListActivity.this, EditActivity.class);
                Uri specificUri = ContentUris.withAppendedId(CONTENT_URI, id);
                intent.setData(specificUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(LOAD, null, this);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        displayDatabaseInfo();
//    }

    public void insertProduct(){
        // content values to store many bunch of keys (column name) and its value
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, "Thai Goats Milk");
        contentValues.put(COLUMN_PRICE, 7500);
        contentValues.put(COLUMN_QUANTITY, 12);
        contentValues.put(COLUMN_SUPPLIER_CONTACT, "shopee@gmail.com");

        Uri uri = getContentResolver().insert(CONTENT_URI, contentValues);
    }
//    /**
//     * Temporary helper method to display information in the onscreen TextView about the state of
//     * the pets database.
//     */
//    private void displayDatabaseInfo() {
//
//        // Define a projection that specifies which columns from the database
//        // you will actually use after this query.
//        String[] projection = {
//                _ID,
//                COLUMN_NAME,
//                COLUMN_PRICE,
//                COLUMN_QUANTITY,
//                COLUMN_SUPPLIER_CONTACT
//        };
//
//        //Perform a query on the provider using the ContentResolver
//        // Use the {@link ProductEntry#CONTENT_URI} to access the product data.
//        Cursor cursor = getContentResolver().query(
//                CONTENT_URI,
//                projection,
//                null,
//                null,
//                null
//        );
//
//
//
////        TextView displayView = (TextView) findViewById(R.id.text_view);
//////        try {
////            /**
////             * Create a header in the Text View that looks like this :
////             *
////             * The pets table contains <number of rows in Cursor> inventaris.
////             * _id - name - price - quantity - supplier_contact
////             *
////             * In the while loop below, iterate through the rows of the cursor and display
////             * the information from each column in this order.
////             */
////
////            displayView.setText("The inventaris table contains " + cursor.getCount() + " inventaris. \n\n");
////            displayView.append(_ID + " - " + COLUMN_NAME + " - " + COLUMN_PRICE + " - "
////                                + COLUMN_QUANTITY + " - " + COLUMN_SUPPLIER_CONTACT + "\n");
////            // Figure out the index of each column
////            int idColumnIndex = cursor.getColumnIndex(_ID);
////            int nameColumnIndex = cursor.getColumnIndex(COLUMN_NAME);
////            int priceColumnIndex = cursor.getColumnIndex(COLUMN_PRICE);
////            int quantityColumnIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
////            int supplierColumnIndex = cursor.getColumnIndex(COLUMN_SUPPLIER_CONTACT);
////
////            // iterate through all the returned rows in the cursor
////            while (cursor.moveToNext()){
////                // Use that index to extract the String or Int value of the word
////                // at the current row the cursor is on.
////                int id = cursor.getInt(idColumnIndex);
////                String name = cursor.getString(nameColumnIndex);
////                int price = cursor.getInt(priceColumnIndex);
////                int quantity = cursor.getInt(quantityColumnIndex);
////                String supplierContact = cursor.getString(supplierColumnIndex);
////
////                displayView.append("\n" + id + " - " + name + " - " + price + " - " + quantity + " - " + supplierContact);
////            }
////        } finally {
////            // Always close the cursor when you're done reading from it. This releases all its
////            // resources and makes it invalid.
////            cursor.close();
////        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection
        switch (item.getItemId()){
            case R.id.action_insert_dummy_data:
                insertProduct();
                return true;
            case R.id.action_delete_all_entries:
                // do nothing for now
                showDeleteConfirmationDialog();
                return true;
            case R.id.about:
                // go to new about activity
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showDeleteConfirmationDialog(){
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);
        builder.setMessage(R.string.delete_all_products);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //User clicked the "Delete" button, so delete the product.
                deleteAllProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null){
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show an AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));



    }
    private void deleteAllProduct(){
        int deletedRows = getContentResolver().delete(CONTENT_URI, null, null);
        if (deletedRows != 0){
            Toast.makeText(this, R.string.delete_all_products, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, R.string.editor_delete_product_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                _ID,
                COLUMN_NAME,
                COLUMN_PRICE,
                COLUMN_QUANTITY,
        };
        /**
         * Takes action based on the ID the Loader that's being created
         */
        switch (id){
            case LOAD :
                return new CursorLoader(
                        ListActivity.this,
                        CONTENT_URI,
                        projection,
                        null,
                        null,
                        null
                );

            default:
                //An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null){
            inventarisAdapter.swapCursor(cursor);
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        inventarisAdapter.swapCursor(null);
    }

    //TODO: Decrease count by one
    public void decreaseAQuantity(int columnId, int quan){
        if (quan > 0){
            quan = quan - 1;
            ContentValues cvUpdateQuantity = new ContentValues();
            cvUpdateQuantity.put(COLUMN_QUANTITY, quan);
            Uri updateUri = ContentUris.withAppendedId(CONTENT_URI, columnId);
            int rowsAffected = getContentResolver().update(updateUri, cvUpdateQuantity, null, null);
            if (rowsAffected != 0){
                Toast.makeText(this, "Quantity Updated", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Error with update quantity", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Sorry, Can't decrease zero quantity", Toast.LENGTH_SHORT).show();
        }

    }

}