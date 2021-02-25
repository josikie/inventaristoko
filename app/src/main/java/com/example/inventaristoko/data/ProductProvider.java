package com.example.inventaristoko.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.IntegerRes;

import static com.example.inventaristoko.data.ProductContract.CONTENT_AUTHORITY;
import static com.example.inventaristoko.data.ProductContract.PATH_PRODUCT;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.COLUMN_NAME;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.COLUMN_SUPPLIER_CONTACT;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.CONTENT_ITEM_TYPE;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.CONTENT_LIST_TYPE;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.TABLE_NAME;
import static com.example.inventaristoko.data.ProductContract.ProductEntry._ID;

public class ProductProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = ProductProvider.class.getSimpleName();

    /**
     * constant to match PRODUCT and PRODUCT_ID
     */
    private static final int PRODUCT = 100;
    private static final int PRODUCT_ID = 101;

    /**
     * make Uri Matcher
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /**
     *  give a criteria to match uri we give
     */
    static {
        // criteria for whole of table
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PRODUCT, PRODUCT);
        // criteria for one row specific data of table
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PRODUCT + "/#", PRODUCT_ID);
    }

    /**
     *  ProductDBHelper for gain access to the inventaris database
     */
    private ProductDbHelper productDbHelper;

    /**
     *
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
//        TODO: Create and initialize a ProductDbHelper to gain access to the inventaris database
//        make sure the variable is a global variable, so it can be referenced from other
//        ContentProvider methods.
        productDbHelper = new ProductDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // use database object to get access to the database
        SQLiteDatabase database = productDbHelper.getReadableDatabase();

        // cursor for return the query method
        Cursor cursor;

        // Match the uri and get some int
        int match = sUriMatcher.match(uri);

        switch (match){
            case PRODUCT:
                cursor = database.query(TABLE_NAME, projection, null, null, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = _ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " +  uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    public Uri insertProduct(Uri uri, ContentValues contentValues){
        // TODO: insert a new product into product database table with the given ContentValues

        //get writeable database so we can insert the data to Product database table
        SQLiteDatabase database = productDbHelper.getWritableDatabase();

        //Insert the new product with the given values
        long id = database.insert(TABLE_NAME, null ,contentValues);
        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        //Once we know the ID of the new row in the table,
        //return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // argument will be a String array containing the actual ID.
                selection = _ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * return the number of rows that were successfully updated.
     */
    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        // TODO : Update the selected products in the database table with the given ContentValues

        // Sanity check : If the key is present,
        // check that the field value is not null.
        if (contentValues.containsKey(COLUMN_NAME)){
            String name = contentValues.getAsString(COLUMN_NAME);
            if (name == null){
                throw new IllegalArgumentException("Product Required product name");
            }
        }

        if (contentValues.containsKey(COLUMN_PRICE)){
            Integer price = contentValues.getAsInteger(COLUMN_PRICE);
            if (price == null){
                throw new IllegalArgumentException("Product Required product price");
            }
        }

        // if there no values to update, then don't try to update the database
        if (contentValues.size() == 0){
            return 0;
        }

        // get access write to SQLite Database
        SQLiteDatabase sqLiteDatabase = productDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = sqLiteDatabase.update(TABLE_NAME, contentValues, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // get writeable database
        SQLiteDatabase database = productDbHelper.getWritableDatabase();

        // amount of deleted rows
        int rowsDeleted = 0;

        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCT :
                // delete all rows that match the selection and selection args
                rowsDeleted =  database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // delete a single row given by the ID in the URI
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;

    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case PRODUCT:
                return CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
