package com.example.qlysv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "student_manager.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "students";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MASV = "maSV";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_CONTENT = "content";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_LOP = "lop";
    private static final String COLUMN_PHONE = "soDienThoai";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create database table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MASV + " TEXT UNIQUE, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_CONTENT + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_LOP + " TEXT, " +
                COLUMN_PHONE + " TEXT)";

        db.execSQL(sql);
    }

    // Upgrade database without deleting existing student records
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(
                    "ALTER TABLE " + TABLE_NAME +
                            " ADD COLUMN " + COLUMN_MASV + " TEXT"
            );

            db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS idx_students_maSV " +
                            "ON " + TABLE_NAME + "(" + COLUMN_MASV + ")"
            );
        }
    }

    // Prepare student information for saving
    private ContentValues getValues(TodoModel todo) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_MASV, todo.getMaSV());
        values.put(COLUMN_TITLE, todo.getTitle());
        values.put(COLUMN_CONTENT, todo.getContent());
        values.put(COLUMN_DATE, todo.getDate());
        values.put(COLUMN_TYPE, todo.getType());
        values.put(COLUMN_LOP, todo.getLop());
        values.put(COLUMN_PHONE, todo.getSoDienThoai());

        return values;
    }

    // Check whether the student code already exists
    public boolean isMaSVExists(String maSV, int excludeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_ID +
                        " FROM " + TABLE_NAME +
                        " WHERE " + COLUMN_MASV + " = ? AND " +
                        COLUMN_ID + " != ?",
                new String[]{
                        maSV,
                        String.valueOf(excludeId)
                }
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();

        return exists;
    }

    // 1. Add student
    // Returns the new database ID, or -1 if adding fails
    public long addTodo(TodoModel todo) {

        if (isMaSVExists(todo.getMaSV(), -1)) {
            return -1;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        return db.insert(
                TABLE_NAME,
                null,
                getValues(todo)
        );
    }

    // 2. Get all students
    public ArrayList<TodoModel> getAllTodos() {

        ArrayList<TodoModel> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " +
                        COLUMN_ID + ", " +
                        COLUMN_MASV + ", " +
                        COLUMN_TITLE + ", " +
                        COLUMN_CONTENT + ", " +
                        COLUMN_DATE + ", " +
                        COLUMN_TYPE + ", " +
                        COLUMN_LOP + ", " +
                        COLUMN_PHONE +
                        " FROM " + TABLE_NAME +
                        " ORDER BY " + COLUMN_ID + " DESC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                TodoModel todo = new TodoModel(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7)
                );

                list.add(todo);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return list;
    }

    // 3. Update student
    // Returns the number of rows updated
    public int updateTodo(TodoModel todo) {

        if (isMaSVExists(todo.getMaSV(), todo.getId())) {
            return 0;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        return db.update(
                TABLE_NAME,
                getValues(todo),
                COLUMN_ID + " = ?",
                new String[]{
                        String.valueOf(todo.getId())
                }
        );
    }

    // 4. Delete student
    // Returns the number of rows deleted
    public int deleteTodo(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(
                TABLE_NAME,
                COLUMN_ID + " = ?",
                new String[]{
                        String.valueOf(id)
                }
        );
    }
}
