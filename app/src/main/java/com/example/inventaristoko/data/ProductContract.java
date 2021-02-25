package com.example.inventaristoko.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import static com.example.inventaristoko.data.ProductContract.ProductEntry.COLUMN_NAME;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.COLUMN_PRICE;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.COLUMN_SUPPLIER_CONTACT;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.TABLE_NAME;
import static com.example.inventaristoko.data.ProductContract.ProductEntry._ID;

public final class ProductContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public ProductContract() {
    }

    /** CONTENT AUTHORITY SAME AS LIKE AUTHORITIES IN MANIFEST*/
    public static final String CONTENT_AUTHORITY = "com.example.inventaristoko";

    /** BASE CONTENT FOR URI */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /** table name for append to base content URI*/
    public static final String PATH_PRODUCT = "product";

    /**
     * The "Content Authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string use for the
     * content authority is the package name for the app, which is guaranteed to be unique on
     * the device.
     */

    public static final class ProductEntry implements BaseColumns{

        // The content URI to access the pet data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);

        // MIME type for Product
        // first MIME type for a list of product (list)
        // and second MIME type for a single product (item)
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        // table name
        public static final String TABLE_NAME = "product";

        // column name {@link _ID, PRODUCT_NAME, PRICE, QUANTITY, SUPPLIER_CONTACT}
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "product_name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_SUPPLIER_CONTACT = "supplier_contact";
    }

    /**
     * Queri to create new table in inventaris database
     */
    private static final String TEXT_TYPE = " TEXT ";
    private static final String COMMA_SEP = ", ";
    private static final String NOT_NULL = " NOT NULL ";
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    COLUMN_NAME + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                    COLUMN_PRICE + " INTEGER" + NOT_NULL + COMMA_SEP +
                    COLUMN_QUANTITY + " INTEGER" + COMMA_SEP +
                    COLUMN_SUPPLIER_CONTACT + TEXT_TYPE + " );";

    /**
     *  Queri to delete table
     */
    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;
}
