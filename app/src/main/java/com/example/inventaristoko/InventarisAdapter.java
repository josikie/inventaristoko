package com.example.inventaristoko;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventaristoko.data.ProductProvider;
import com.example.inventaristoko.activity.ListActivity;
import com.example.inventaristoko.data.ProductContract;
import com.example.inventaristoko.data.ProductDbHelper;

import static android.widget.Toast.LENGTH_SHORT;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.COLUMN_QUANTITY;
import static com.example.inventaristoko.data.ProductContract.ProductEntry.TABLE_NAME;
import static com.example.inventaristoko.data.ProductContract.ProductEntry._ID;

public class InventarisAdapter extends CursorAdapter {

    Context mContext;
    Cursor mCursor;

    public InventarisAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        this.mCursor = cursor;
        // initialize tv for product name, quantity, price, and supplier contact.
        TextView tvProductName = view.findViewById(R.id.tv_product_name);
        TextView tvPrice = view.findViewById(R.id.tv_price);
        final TextView tvQuantity = view.findViewById(R.id.tv_quantity);
        Button btnSell = view.findViewById(R.id.btn_minus);

        // get the data from the cursor
        final int id = mCursor.getInt(cursor.getColumnIndex(_ID));
        String name = mCursor.getString(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_NAME));
        final int price = mCursor.getInt(cursor.getColumnIndex(ProductContract.ProductEntry.COLUMN_PRICE));
        final int quantity = mCursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));

        // set the data to the item blank view
        tvProductName.setText(name);
        tvPrice.setText(String.valueOf(price));
        tvQuantity.setText(String.valueOf(quantity));

        btnSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListActivity listActivity = (ListActivity) context;
                listActivity.decreaseAQuantity(id, quantity);
            }
        });

    }
}
