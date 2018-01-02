/**
 * Copyright 2018 Pascal Ammon, Gabriel Wyss
 * <p>
 * Implementation eines anonymen Mobility Pricing Systems auf Basis eines Gruppensignaturschemas
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mps.bachelor2017.bfh.ti.ch.mobiltypricing.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileSignature;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.data.MobileTuple;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.interfaces.DBClass;
import mps.bachelor2017.bfh.ti.ch.mobiltypricing.interfaces.DBField;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SQLiteMobilityPricing.db";

    private static final int DATABASE_VERSION = 1;
    private static final String TUPLE_TABLE_NAME = "tuple";
    private static final String TUPLE_COLUMN_HASH = "hash";

    private static final SimpleDateFormat periodeFormat = new SimpleDateFormat("dd-MM-yyyy");

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        if (MobileTuple.class.isAnnotationPresent(DBClass.class)) {
            String tableName = ((DBClass) MobileTuple.class.getAnnotation(DBClass.class)).Name();

            String fieldQuery = " (";
            for (Field field : getTupleFields()) {
                DBField dbField = field.getAnnotation(DBField.class);
                String fieldName = dbField.Name();
                boolean isPrimary = dbField.PrimaryKey();
                String fieldType = "TEXT";
                if (field.getType() == Boolean.class) {
                    fieldType = "INTEGER";
                } else if (field.getType() == BigDecimal.class) {
                    fieldType = "REAL";
                } else if (field.getType() == int.class) {
                    fieldType = "INTEGER";
                } else if (field.getType() == BigInteger.class) {
                    fieldType = "TEXT";
                }
                else if (field.getType() == Date.class) {
                    fieldType = "NUMERIC";
                }
                else if (field.getType().getSuperclass() == Enum.class) {
                    fieldType = "TEXT";
                }
                fieldQuery += fieldName + " " + fieldType + (isPrimary ? "  PRIMARY KEY" : "") + ", ";
            }
            fieldQuery = fieldQuery.substring(0, fieldQuery.length() - 2) + ")";
            db.execSQL("CREATE TABLE " + tableName + fieldQuery);
        }
    }

    private List<Field> tupleFields;

    private List<Field> getTupleFields() {
        if (tupleFields == null) {
            tupleFields = new ArrayList<Field>();
            for (Field field : MobileTuple.class.getDeclaredFields()) {
                if (field.isAnnotationPresent(DBField.class)) {
                    tupleFields.add(field);
                }
            }
            ;
            for (Field field : MobileSignature.class.getDeclaredFields()) {
                if (field.isAnnotationPresent(DBField.class)) {
                    tupleFields.add(field);
                }
            }
            ;
        }
        return tupleFields;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TUPLE_TABLE_NAME);
        onCreate(db);
    }

    private void setValue(ContentValues contentValues, Object object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(DBField.class)) {
                DBField dbField = field.getAnnotation(DBField.class);
                field.setAccessible(true);
                if (dbField.PrimaryKey()) {
                    continue;
                }
                try {
                    Object data = field.get(object);
                    if (data.getClass() == String.class) {
                        contentValues.put(dbField.Name(), ((String) data));
                    } else if (data.getClass() == Boolean.class) {
                        contentValues.put(dbField.Name(), ((Boolean) data) ? 1 : 0);
                    } else if (data.getClass() == Integer.class) {
                        contentValues.put(dbField.Name(), ((Integer) data));
                    } else if (data.getClass() == BigDecimal.class) {
                        contentValues.put(dbField.Name(), ((BigDecimal) data).doubleValue());
                    } else if (data.getClass() == BigInteger.class) {
                        contentValues.put(dbField.Name(), ((BigInteger) data).toString());
                    } else if (data.getClass() == Date.class) {
                        contentValues.put(dbField.Name(), ((Date) data).getTime());
                    }
                    else if (data.getClass().getSuperclass() == Enum.class) {
                        contentValues.put(dbField.Name(), data.toString());
                    }
                } catch (Exception ex) {
                    // todo error handling
                }
            }
        }
    }

    private void setValue(Object data, Cursor cursor) {
        for (Field field : data.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(DBField.class)) {
                DBField dbField = field.getAnnotation(DBField.class);
                try {
                    Log.v("dbHelper", dbField.Name() + field.getType());
                    int fieldIndex = cursor.getColumnIndex(dbField.Name());
                    field.setAccessible(true);
                    if (field.getType() == String.class) {
                        field.set(data, cursor.getString(fieldIndex));
                    } else if (field.getType() == Boolean.class) {
                        field.set(data, cursor.getInt(fieldIndex) == 1);
                    } else if (field.getType() == Integer.class || field.getType() == int.class) {
                        field.set(data, cursor.getInt(fieldIndex));
                    } else if (field.getType() == BigDecimal.class) {
                        field.set(data, new BigDecimal(cursor.getDouble(fieldIndex)).setScale(10, RoundingMode.HALF_UP));
                    } else if (field.getType() == BigInteger.class) {
                        field.set(data, new BigInteger(cursor.getString(fieldIndex)));
                    } else if (field.getType() == Date.class) {
                        field.set(data, new Date(cursor.getLong(fieldIndex)));
                    }
                    else if (field.getType().getSuperclass() == Enum.class) {
                        field.set(data, Enum.valueOf(field.getType().asSubclass(Enum.class), cursor.getString(fieldIndex)));
                    }
                } catch (Exception ex) {
                    // todo error handling
                }
            }
        }
    }

    public boolean save(MobileTuple tuple) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if (tuple.getClass().isAnnotationPresent(DBClass.class)) {
            String tableName = ((DBClass) tuple.getClass().getAnnotation(DBClass.class)).Name();
            setValue(contentValues, tuple);
            setValue(contentValues, tuple.getSignature());
            db.insert(tableName, null, contentValues);
        }
        return true;
    }

    public boolean setTupleStatus(String hash, MobileTuple.TupleStatus status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Const.DbTupleStatusField, status.toString());
        db.update(TUPLE_TABLE_NAME, contentValues, TUPLE_COLUMN_HASH + " = ? ", new String[]{hash});
        return true;
    }

    public List<MobileTuple> getTuplesByStatus(MobileTuple.TupleStatus status) {
        SQLiteDatabase db = this.getReadableDatabase();

        List<MobileTuple> res = new ArrayList<MobileTuple>();

        Cursor c = db.rawQuery("SELECT * FROM " + TUPLE_TABLE_NAME + " WHERE " +
                Const.DbTupleStatusField + "=?", new String[]{status.toString()});

        c.moveToFirst();
        while(c.isAfterLast() == false){
            MobileTuple t = new MobileTuple();
            setValue(t, c);

            MobileSignature ms = new MobileSignature();
            setValue(ms, c);
            t.setSignature(ms);
            res.add(t);
            c.moveToNext();
        }
        return res;
    }

    public List<String> getTuplesHashesStatus(MobileTuple.TupleStatus status) {
        SQLiteDatabase db = this.getReadableDatabase();

        List<String> res = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT " + TUPLE_COLUMN_HASH + " FROM " + TUPLE_TABLE_NAME + " WHERE " +
                Const.DbTupleStatusField + "=?", new String[]{status.toString()});

        c.moveToFirst();
        while(c.isAfterLast() == false){
            res.add(c.getString(0));
            c.moveToNext();
        }
        return res;
    }

    // periode format: dd-MM-yyyy
    public boolean hasRemoteTupleInPeriode(String periode, String periodeB) {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor c = null;

            if(periodeB == null) {
                String query = "SELECT COUNT(*) FROM " + TUPLE_TABLE_NAME + " WHERE " + Const.DbTupleCreatedField + ">= ? AND " + Const.DbTupleStatusField + " =?";
                String date = String.valueOf(periodeFormat.parse(periode).getTime());

                c = db.rawQuery(query, new String[]{date, MobileTuple.TupleStatus.REMOTE.toString()});
            }
            else {
                String query = "SELECT COUNT(*) FROM " + TUPLE_TABLE_NAME + " WHERE " + Const.DbTupleCreatedField + ">= ? AND "+ Const.DbTupleCreatedField + "< ? AND " + Const.DbTupleStatusField + " =?";
                String date = String.valueOf(periodeFormat.parse(periode).getTime());
                String dateB = String.valueOf(periodeFormat.parse(periode).getTime());

                c = db.rawQuery(query, new String[]{date, dateB, MobileTuple.TupleStatus.REMOTE.toString()});
            }
            c.moveToFirst();
            int count = c.getInt(0);
            return count > 0;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }
}
