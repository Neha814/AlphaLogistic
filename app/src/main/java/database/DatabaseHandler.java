package database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import model.BarcodeData;

/**
 * Created by nehabh on 2/10/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "BarcodeData";

    // Contacts table name
    private static final String TABLE_CONTACTS = "barcodeTable";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_BARCODE = "Barcode";
    private static final String KEY_DRIVERID = "DriverID";
    private static final String KEY_MODIFIED = "Modified";
    private static final String KEY_ACTIVITY_TYPEID = "ActivityTypeID";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_BARCODE + " TEXT,"
                + KEY_DRIVERID + " TEXT,"+ KEY_MODIFIED + " TEXT," + KEY_ACTIVITY_TYPEID + " TEXT"+ ")";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        // Create tables again
        onCreate(db);
    }


    // Adding new data
    public void addContact(BarcodeData data) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BARCODE, data.getBarcode());
        values.put(KEY_DRIVERID, data.getDriverId());
        values.put(KEY_MODIFIED, data.getModified());
        values.put(KEY_ACTIVITY_TYPEID, data.getActivityTypeId());


        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database connection
    }


    public List<BarcodeData> getAllContacts() {
        List<BarcodeData> contactList = new ArrayList<BarcodeData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                BarcodeData contact = new BarcodeData();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setBarcode(cursor.getString(1));
                contact.setDriverId(cursor.getString(2));
                contact.setModified(cursor.getString(3));
                contact.setActivityTypeId(cursor.getString(4));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Deleting all contacts
    public void deleteWholeData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, null, null);
        db.close();
    }

    // Deleting single contact
    public void deleteSingleData(BarcodeData contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
        db.close();
    }
}
