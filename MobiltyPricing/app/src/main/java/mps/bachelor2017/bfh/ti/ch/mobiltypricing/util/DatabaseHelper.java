package mps.bachelor2017.bfh.ti.ch.mobiltypricing.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.math.BigDecimal;
import java.util.Date;

import data.Tuple;

/**
 * Created by isabelcosta on 15.10.17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SQLiteMobilityPricing.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TUPLE_TABLE_NAME = "tuple";
    public static final String TUPLE_COLUMN_ID = "_id";
    public static final String TUPLE_COLUMN_GROUPID = "groupId";
    public static final String TUPLE_COLUMN_LONGITUDE = "longitude";
    public static final String TUPLE_COLUMN_LATITUDE = "latitude";
    public static final String TUPLE_COLUMN_CREATED = "created";
    public static final String TUPLE_COLUMN_HASH = "hash";
    public static final String TUPLE_COLUMN_UPLOADED = "uploaded";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TUPLE_TABLE_NAME + "(" +
                TUPLE_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                TUPLE_COLUMN_GROUPID + " INTEGER, " +
                TUPLE_COLUMN_LONGITUDE + " REAL, " +
                TUPLE_COLUMN_LATITUDE + " REAL, " +
                TUPLE_COLUMN_CREATED + " INTEGER, " +
                TUPLE_COLUMN_HASH + " TEXT, " +
                TUPLE_COLUMN_UPLOADED + " INTEGER)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TUPLE_TABLE_NAME);
        onCreate(db);
    }

    public boolean insertTuple(Tuple tuple, String hash) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TUPLE_COLUMN_GROUPID, tuple.getGroupId());
        contentValues.put(TUPLE_COLUMN_LONGITUDE, tuple.getLongitude().doubleValue());
        contentValues.put(TUPLE_COLUMN_LATITUDE, tuple.getLatitude().doubleValue());
        contentValues.put(TUPLE_COLUMN_CREATED,tuple.getCreated().getTime());
        contentValues.put(TUPLE_COLUMN_HASH, hash);
        contentValues.put(TUPLE_COLUMN_UPLOADED, 0);
        db.insert(TUPLE_TABLE_NAME, null, contentValues);
        return true;
    }

    public boolean setTupleIsUploaded(String hash){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TUPLE_COLUMN_UPLOADED, 1);
        db.update(TUPLE_TABLE_NAME, contentValues, TUPLE_COLUMN_HASH + " = ? ", new String[] { hash } );
        return true;
    }

    public Integer deleteTuple(String hash) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TUPLE_TABLE_NAME,
                TUPLE_COLUMN_HASH + " = ? ",
                new String[] { hash });
    }

    public Cursor getTuple(String hash) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + TUPLE_TABLE_NAME + " WHERE " +
                TUPLE_COLUMN_HASH + "=?", new String[] { hash } );
        return res;
    }

    public Cursor getAllTuples() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + TUPLE_TABLE_NAME, null );
        return res;
    }

    public Cursor getNotUploadedTuples() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + TUPLE_TABLE_NAME + " WHERE " +
                TUPLE_COLUMN_UPLOADED + "=?", new String[] { "0" } );
        return res;
    }
    public Cursor getUploadedTuples() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + TUPLE_TABLE_NAME + " WHERE " +
                TUPLE_COLUMN_UPLOADED + "=?", new String[] { "1" } );
        return res;
    }

    public Cursor deleteAllTuples() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "DELETE * FROM " + TUPLE_TABLE_NAME, null );
        return res;
    }

    public Tuple getTupleFromCursor(Cursor c){
        c.moveToFirst();
        Tuple tuple = new Tuple();

        int createdIndex = c.getColumnIndex(DatabaseHelper.TUPLE_COLUMN_CREATED);
        long created = c.getInt(createdIndex);
        tuple.setCreated(new Date(created));

        int longitudeIndex = c.getColumnIndex(DatabaseHelper.TUPLE_COLUMN_LONGITUDE);
        BigDecimal longitude = new BigDecimal(c.getDouble(longitudeIndex));
        tuple.setLongitude(longitude);

        int latitudeIndex = c.getColumnIndex(DatabaseHelper.TUPLE_COLUMN_LATITUDE);
        BigDecimal latitude = new BigDecimal(c.getDouble(latitudeIndex));
        tuple.setLatitude(latitude);

        return tuple;

    }


}
