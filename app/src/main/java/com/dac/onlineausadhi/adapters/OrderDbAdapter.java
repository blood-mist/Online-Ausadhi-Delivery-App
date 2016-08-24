package com.dac.onlineausadhi.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.dac.onlineausadhi.classes.CartList;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDbAdapter {

    private static final String TAG = "DbAdapter";

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }

    }


    private static final String DATABASE_NAME = "orderinfo";
    private static final String DATABASE_TABLE = "orders";
    private static final int DATABASE_VERSION = 3;
    public static final String MEDICINE = "medicine_name";
    public static final String QUANTITY = "quantity";
    public static final String MEDICINE_TYPE = "medicine_type";
    public static final String ID = "id";
    private DatabaseHelper DbHelper;
    private SQLiteDatabase Db;
    private static final String MEDICINE_ID = "medicine_id";
    private static final String CREATED_AT = "currentDate";
    private static final String DATABASE_CREATE = "CREATE TABLE "
            + DATABASE_TABLE
            + " ( "
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
            + MEDICINE + " TEXT,"
            + QUANTITY + " INTEGER, "
            + MEDICINE_ID + " TEXT, "
            + CREATED_AT + " DEFAULT CURRENT_TIMESTAMP, "
            + MEDICINE_TYPE + " TEXT "
            + " ) ";

    private final Context context;

    public OrderDbAdapter(Context context) {
        this.context = context;
    }

    public OrderDbAdapter open() throws SQLException {
        DbHelper = new DatabaseHelper(context);
        Db = DbHelper.getWritableDatabase();
        return this;
    }


    public long createOrder(String medicine, int quantity, String medicine_type, String medicine_id, String currentdate) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(MEDICINE, medicine);
        initialValues.put(QUANTITY, quantity);
        initialValues.put(MEDICINE_TYPE, medicine_type);
        initialValues.put(MEDICINE_ID, medicine_id);
        initialValues.put(CREATED_AT, currentdate);
        return Db.insert(DATABASE_TABLE, ID, initialValues);

    }

    public void deleteTable() {
        Db.execSQL("DELETE FROM " + DATABASE_TABLE);
    }

    public boolean deleteOrder(String medicine) {
        Log.d(TAG, medicine);
        return Db.delete(DATABASE_TABLE, MEDICINE + " = ?", new String[]{medicine}) > 0;
    }


    public int countjournals() {
        DbHelper = new DatabaseHelper(context);
        Db = DbHelper.getReadableDatabase();
        SQLiteStatement dbJournalCountQuery;
        dbJournalCountQuery = Db.compileStatement("select count(*) FROM " + DATABASE_TABLE );
        return (int) dbJournalCountQuery.simpleQueryForLong();
    }


    public List<CartList> getAllOrder() {
        List<CartList> list = new ArrayList<>();
        String query = "SELECT medicine_name,quantity,medicine_type,currentDate  FROM " + DATABASE_TABLE + " ORDER BY currentDate DESC ";
        DbHelper = new DatabaseHelper(context);
        Db = DbHelper.getReadableDatabase();
        Cursor cursor = Db.rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {

                //int id = cursor.getInt(cursor.getColumnIndex(ID));
                String medicineName = cursor.getString(cursor.getColumnIndex(MEDICINE));
                int quantity = cursor.getInt(cursor.getColumnIndex(QUANTITY));
                String medicineType = cursor.getString(cursor.getColumnIndex(MEDICINE_TYPE));
                /*String medicineId=cursor.getString(cursor.getColumnIndex(MEDICINE_ID));*/
                String currentDate = cursor.getString(cursor.getColumnIndex(CREATED_AT));
                list.add(new CartList(medicineName, quantity, medicineType, "123", currentDate));
            }
        }
        cursor.close();
        return list;

    }

    public CartList getData(String medicine) {
        CartList list = null;
        Db = DbHelper.getReadableDatabase();
        String[] columns = {MEDICINE, QUANTITY, MEDICINE_TYPE, MEDICINE_ID, CREATED_AT};
        Cursor cursor = Db.query(DATABASE_TABLE, columns, MEDICINE + " = '" + medicine + "'", null, null, null, null);
        if (cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(ID);
            int index2 = cursor.getColumnIndex(MEDICINE);
            int index3 = cursor.getColumnIndex(QUANTITY);
            int id = cursor.getInt(index);
            String medicineName = cursor.getString(index2);
            int quantity = cursor.getInt(index3);
            String medicineType = cursor.getString(cursor.getColumnIndex(MEDICINE_TYPE));
            String medicineId = cursor.getString(cursor.getColumnIndex(MEDICINE_ID));
            String currentDate = cursor.getString(cursor.getColumnIndex(CREATED_AT));
            list = new CartList(medicineName, quantity, medicineType, medicineId, currentDate);
        }
        return list;
    }

    public boolean updateOrder(String OrignalMed, String medicine, int quantity, String medicine_type, String med_id, String date) {
        ContentValues args = new ContentValues();
        args.put(MEDICINE, medicine);
        args.put(QUANTITY, quantity);
        args.put(MEDICINE_TYPE, medicine_type);
        args.put(MEDICINE_ID, med_id);
        args.put(CREATED_AT, date);
        return
                Db.update(DATABASE_TABLE, args, MEDICINE + "=? ", new String[]{OrignalMed}) > 0;
    }

    public int checkMedicine(String medicine) {
        DbHelper = new DatabaseHelper(context);
        Db = DbHelper.getReadableDatabase();
        SQLiteStatement dbJournalCountQuery;
        dbJournalCountQuery = Db.compileStatement("select count('"+medicine+"') from " + DATABASE_TABLE+" where medicine_name='"+medicine+"'");
        return (int) dbJournalCountQuery.simpleQueryForLong();
    }

    public boolean addup(String medicine, int quantity, String medicine_type, String med_id, String date) {
        Db.execSQL("UPDATE " + DATABASE_TABLE + " SET " + QUANTITY + " = quantity+" + quantity + " WHERE medicine_name='" + medicine + "'");
        ContentValues args = new ContentValues();
        args.put(MEDICINE, medicine);
        args.put(MEDICINE_TYPE, medicine_type);
        args.put(MEDICINE_ID, med_id);
        args.put(CREATED_AT, date);
        return
                Db.update(DATABASE_TABLE, args, MEDICINE + "=? ", new String[]{medicine}) > 0;
    }
}