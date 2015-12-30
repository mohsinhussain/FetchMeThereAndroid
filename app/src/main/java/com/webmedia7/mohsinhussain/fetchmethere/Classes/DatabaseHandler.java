package com.webmedia7.mohsinhussain.fetchmethere.Classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.webmedia7.mohsinhussain.fetchmethere.Model.Contacts;

import java.util.ArrayList;

/**
 * Created by mohsinhussain on 6/23/15.
 */
public class DatabaseHandler extends SQLiteOpenHelper{

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = Constants.DB_VERSION;

    // Database Name
    private static final String DATABASE_NAME = Constants.DB_NAME;

    // Contacts table name
    private static final String TABLE_CONTACTS = "contacts";

    String name;
    String mobileNumber;
    String userId;
    boolean friend;
    String profileImageString = "";
    String sortFriend;
    String hasApp;

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PH_NO = "mobile_number";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_PROFILE_PICTURE = "profile_picture";
    private static final String KEY_IS_FRIEND = "is_friend";
    private static final String KEY_SORT_FRIEND = "sort_friend";
    private static final String KEY_HAS_APP = "has_app";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_PH_NO + " TEXT," + KEY_USER_ID + " TEXT," + KEY_PROFILE_PICTURE + " TEXT," + KEY_IS_FRIEND + " TEXT," + KEY_SORT_FRIEND + " TEXT," + KEY_HAS_APP + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    /** ALL CRUD OPERATIONS ****************/

    // Adding new contact
    public void addContact(Contacts contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName()); // Contact Name
        values.put(KEY_PH_NO, contact.getMobileNumber()); // Contact Phone Number
        values.put(KEY_USER_ID, contact.getUserId());
        values.put(KEY_PROFILE_PICTURE, contact.getProfileImageString());
        if (contact.isFriend()){
            values.put(KEY_IS_FRIEND, "true");
        }
        else{
            values.put(KEY_IS_FRIEND, "false");
        }
        values.put(KEY_SORT_FRIEND, contact.getSortFriend());
        if (contact.isHasApp()){
            values.put(KEY_HAS_APP, "true");
        }
        else{
            values.put(KEY_HAS_APP, "false");
        }

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        db.close(); // Closing database c
    }

    // Getting single contact
//    public Contacts getContact(int id) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(TABLE_CONTACTS, new String[] { KEY_ID,
//                        KEY_NAME, KEY_PH_NO }, KEY_ID + "=?",
//                new String[] { String.valueOf(id) }, null, null, null, null);
//        if (cursor != null)
//            cursor.moveToFirst();
//
//        Contacts contact = new Contacts(Integer.parseInt(cursor.getString(0)),
//                cursor.getString(1), cursor.getString(2));
//        // return contact
//        return contact;
//    }

    // Getting contacts Count
    public int getContactsCount() {
        int count = 0;
        String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
//        cursor.close();

        if(cursor != null && !cursor.isClosed()){
            count = cursor.getCount();
            cursor.close();
        }
        return count;
    }

    /**
     * Remove all users and groups from database.
     */
    public void removeAllContacts()
    {
        // db.delete(String tableName, String whereClause, String[] whereArgs);
        // If whereClause is null, it will delete all rows.
        SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(TABLE_CONTACTS, null, null);
    }

    // Getting All Contacts
    public ArrayList<Contacts> getAllContacts() {
        ArrayList<Contacts> contactList = new ArrayList<Contacts>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contacts contact = new Contacts();
//                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setMobileNumber(cursor.getString(2));
                contact.setUserId(cursor.getString(3));
                contact.setProfileImageString(cursor.getString(4));
                if(cursor.getString(5).equalsIgnoreCase("true")){
                    contact.setFriend(true);
                }
                else{
                    contact.setFriend(false);
                }
                contact.setSortFriend(cursor.getString(6));
                if(cursor.getString(7).equalsIgnoreCase("true")){
                    contact.setHasApp(true);
                }
                else{
                    contact.setHasApp(false);
                }
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // Updating single contact
//    public int updateContact(Contacts contact) {}

    // Deleting single contact
//    public void deleteContact(Contacts contact) {}
}