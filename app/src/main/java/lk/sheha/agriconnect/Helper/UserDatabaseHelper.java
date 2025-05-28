package lk.sheha.agriconnect.Helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "userDB";
    private static final int DATABASE_VERSION = 2; // Increased version for migration

    // Table Name
    private static final String TABLE_USERS = "users";

    // Column Names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_ACTIVE = "active"; // Status renamed to Active
    private static final String COLUMN_MOBILE = "mobile";

    // SQL Query to Create Table
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_USERNAME + " TEXT, "
                    + COLUMN_EMAIL + " TEXT, " // Removed UNIQUE constraint
                    + COLUMN_ACTIVE + " TEXT, "
                    + COLUMN_MOBILE + " TEXT);";

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Insert User Data (All fields can be null)
    public boolean insertUser(String username, String email, String active, String mobile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_ACTIVE, active);
        values.put(COLUMN_MOBILE, mobile);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // Get User Data by Email (Can return null values)
    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS,
                new String[]{COLUMN_ID, COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_ACTIVE, COLUMN_MOBILE},
                COLUMN_EMAIL + "=?", new String[]{email},
                null, null, null);
    }

    // Update User Data (If a value is null, it won't be updated)
    public boolean updateUser(String email, String newUsername, String newActive, String newMobile) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        if (newUsername != null) values.put(COLUMN_USERNAME, newUsername);
        if (newActive != null) values.put(COLUMN_ACTIVE, newActive);
        if (newMobile != null) values.put(COLUMN_MOBILE, newMobile);

        int rowsUpdated = db.update(TABLE_USERS, values, COLUMN_EMAIL + "=?", new String[]{email});
        return rowsUpdated > 0;
    }

    // Delete User by Email
    public boolean deleteUser(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_USERS, COLUMN_EMAIL + "=?", new String[]{email});
        return rowsDeleted > 0;
    }
}
