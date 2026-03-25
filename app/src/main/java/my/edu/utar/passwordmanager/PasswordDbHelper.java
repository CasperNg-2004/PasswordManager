package my.edu.utar.passwordmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
public class PasswordDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "password.db";
    private static final int DB_VERSION = 2;

    private static final String TABLE_PASSWORDS = "passwords";
    private static final String COL_ID = "_id";
    private static final String COL_SITE = "site";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_EXTRA_PIN = "extra_pin";
    private static final String COL_CHALLENGE_QUESTION = "challenge_question";
    private static final String COL_CHALLENGE_ANSWER = "challenge_answer";

    public PasswordDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_PASSWORDS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_SITE + " TEXT NOT NULL, " +
                COL_USERNAME + " TEXT NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_EXTRA_PIN + " TEXT, " +
                COL_CHALLENGE_QUESTION + " TEXT, " +
                COL_CHALLENGE_ANSWER + " TEXT" +
                ");";
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PASSWORDS);
        onCreate(db);
    }
    public long insertEntry(String site, String username, String password,
                            String extraPin, String challengeQuestion, String challengeAnswer) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SITE, site);
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, CryptoUtil.encrypt(password));
        values.put(COL_EXTRA_PIN, CryptoUtil.encrypt(extraPin));
        values.put(COL_CHALLENGE_QUESTION, challengeQuestion);
        values.put(COL_CHALLENGE_ANSWER, CryptoUtil.encrypt(challengeAnswer));
        return db.insert(TABLE_PASSWORDS, null, values);
    }
    public int updateEntry(long id, String site, String username, String password,
                           String extraPin, String challengeQuestion, String challengeAnswer) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SITE, site);
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, CryptoUtil.encrypt(password));
        values.put(COL_EXTRA_PIN, CryptoUtil.encrypt(extraPin));
        values.put(COL_CHALLENGE_QUESTION, challengeQuestion);
        values.put(COL_CHALLENGE_ANSWER, CryptoUtil.encrypt(challengeAnswer));
        return db.update(TABLE_PASSWORDS, values, COL_ID + "=?", new String[]{String.valueOf(id)});
    }
    public int deleteEntry(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PASSWORDS, COL_ID + "=?", new String[]{String.valueOf(id)});
    }
    public List<PasswordEntry> getAllEntries() {
        return getEntries(null, null, true);
    }

    public List<PasswordEntry> getEntries(String query, boolean sortAsc) {
        return getEntries(query, query, sortAsc);
    }

    private List<PasswordEntry> getEntries(String siteQuery, String usernameQuery, boolean sortAsc) {
        List<PasswordEntry> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String selection = null;
        String[] selectionArgs = null;
        if (siteQuery != null && !siteQuery.trim().isEmpty()) {
            selection = COL_SITE + " LIKE ? OR " + COL_USERNAME + " LIKE ?";
            String q = "%" + siteQuery.trim() + "%";
            selectionArgs = new String[]{q, q};
        }

        String orderBy = COL_SITE + (sortAsc ? " ASC" : " DESC");
        Cursor c = db.query(TABLE_PASSWORDS, null, selection, selectionArgs, null, null, orderBy);
        if (c != null) {
            while (c.moveToNext()) {
                long id = c.getLong(c.getColumnIndexOrThrow(COL_ID));
                String site = c.getString(c.getColumnIndexOrThrow(COL_SITE));
                String username = c.getString(c.getColumnIndexOrThrow(COL_USERNAME));
                String password = CryptoUtil.decrypt(c.getString(c.getColumnIndexOrThrow(COL_PASSWORD)));
                String extraPin = c.getString(c.getColumnIndexOrThrow(COL_EXTRA_PIN));
                String challengeQuestion = c.getString(c.getColumnIndexOrThrow(COL_CHALLENGE_QUESTION));
                String challengeAnswer = c.getString(c.getColumnIndexOrThrow(COL_CHALLENGE_ANSWER));
                result.add(new PasswordEntry(
                        id,
                        site,
                        username,
                        password,
                        CryptoUtil.decrypt(extraPin),
                        challengeQuestion,
                        CryptoUtil.decrypt(challengeAnswer)
                ));
            }
            c.close();
        }
        return result;
    }


}
