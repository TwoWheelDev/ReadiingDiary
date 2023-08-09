package dev.dlintott.readingdiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class dbAdapter {
    dbHelper dbhelper;
    SQLiteDatabase db;
    public dbAdapter(Context context) {
        dbhelper = new dbHelper(context);
        db = dbhelper.getWritableDatabase();
    }

    public long insertData(ReadingEntry entry) {
        ContentValues contentValues = getContentValues(entry);
        return db.insert(dbHelper.TABLE_NAME, null, contentValues);
    }

    public int updateEntry(ReadingEntry entry) {
        ContentValues contentValues = getContentValues(entry);
        String[] args = { String.valueOf(entry.getId()) };
        return db.update(dbHelper.TABLE_NAME, contentValues, "_id=?", args);
    }

    public List<ReadingEntry> getEntries() {
        String[] columns = {dbHelper.UID, dbHelper.TITLE, dbHelper.DATE, dbHelper.PAGE_FROM,
                dbHelper.PAGE_TO, dbHelper.RATING, dbHelper.CHILD_COMMENT, dbHelper.PARENT_COMMENT,
                dbHelper.BOOK_IMAGE};
        String orderBy = String.format("%s DESC", dbHelper.DATE);
        Cursor cursor = db.query(dbHelper.TABLE_NAME, columns, null, null,
                null, null, orderBy);
        List<ReadingEntry> entries = new ArrayList<>();
        while (cursor.moveToNext()) {
            entries.add(parseRecord(cursor));
        }
        cursor.close();

        return entries;
    }

    public List<ReadingEntry> getEntries(String query) {
        String[] columns = {dbHelper.UID, dbHelper.TITLE, dbHelper.DATE, dbHelper.PAGE_FROM,
                dbHelper.PAGE_TO, dbHelper.RATING, dbHelper.CHILD_COMMENT, dbHelper.PARENT_COMMENT,
                dbHelper.BOOK_IMAGE};
        String selection = String.format("%s LIKE ? OR %s LIKE ? OR %s LIKE ?", dbHelper.TITLE, dbHelper.CHILD_COMMENT, dbHelper.PARENT_COMMENT);
        String[] args = { "%" + query + "%", "%" + query + "%", "%" + query + "%" };
        String orderBy = String.format("%s DESC", dbHelper.DATE);
        Cursor cursor = db.query(dbHelper.TABLE_NAME, columns, selection, args,
                null, null, orderBy);
        List<ReadingEntry> entries = new ArrayList<>();
        while (cursor.moveToNext()) {
            entries.add(parseRecord(cursor));
        }
        cursor.close();

        return entries;
    }

    public ReadingEntry getEntryById(long id) throws RuntimeException {
        String[] columns = {dbHelper.UID, dbHelper.TITLE, dbHelper.DATE, dbHelper.PAGE_FROM,
                dbHelper.PAGE_TO, dbHelper.RATING, dbHelper.CHILD_COMMENT, dbHelper.PARENT_COMMENT,
                dbHelper.BOOK_IMAGE};
        String[] args = { String.valueOf(id) };
        Cursor cursor = db.query(dbHelper.TABLE_NAME, columns, "_id=?", args,
                null, null, null);
        if (cursor.getCount() != 1) {
            throw new RuntimeException("Multiple records for id " + id);
        } else if (cursor.getCount() == -1) {
            throw new RuntimeException("No records returned for id " + id);
        }

        cursor.moveToFirst();
        ReadingEntry entry = parseRecord(cursor);

        cursor.close();
        return entry;
    }

    public int deleteEntryById(long id) throws RuntimeException {
        String[] args = { String.valueOf(id) };
        return db.delete(dbHelper.TABLE_NAME, "_id=?", args);
    }

    public void close() {
        db.close();
    }

    private ContentValues getContentValues(ReadingEntry entry) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.TITLE, entry.getTitle());
        contentValues.put(dbHelper.DATE, entry.getDate());
        contentValues.put(dbHelper.PAGE_FROM, entry.getPageFrom());
        contentValues.put(dbHelper.PAGE_TO, entry.getPageTo());
        contentValues.put(dbHelper.RATING, entry.getRating());
        contentValues.put(dbHelper.CHILD_COMMENT, entry.getChildComment());
        contentValues.put(dbHelper.PARENT_COMMENT, entry.getParentComment());
        contentValues.put(dbHelper.BOOK_IMAGE, entry.getBookImage());

        return contentValues;
    }

    private ReadingEntry parseRecord(Cursor cursor) {
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(dbHelper.UID));
        String title = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.TITLE));
        long date = cursor.getLong(cursor.getColumnIndexOrThrow(dbHelper.DATE));
        int pageFrom = cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.PAGE_FROM));
        int pageTo = cursor.getInt(cursor.getColumnIndexOrThrow(dbHelper.PAGE_TO));
        float rating = cursor.getFloat(cursor.getColumnIndexOrThrow(dbHelper.RATING));
        String childComment = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.CHILD_COMMENT));
        String parentComment = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.PARENT_COMMENT));
        String bookImage = cursor.getString(cursor.getColumnIndexOrThrow(dbHelper.BOOK_IMAGE));

        return new ReadingEntry(id, title, date, pageFrom, pageTo, rating, childComment, parentComment, bookImage);
    }

    static class dbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "readingDiary";
        private static final int DATABASE_VERSION = 3;
        private static final String TABLE_NAME = "entries";
        private static final String UID = "_id";
        private static final String TITLE = "title";
        private static final String DATE = "date";
        private static final String PAGE_FROM = "pageFrom";
        private static final String PAGE_TO = "pageTo";
        private static final String RATING = "rating";
        private static final String CHILD_COMMENT = "childComment";
        private static final String PARENT_COMMENT = "parentComment";
        private static final String BOOK_IMAGE = "bookImage";

        private static final String CREATE_TABLE =
                String.format(
                "CREATE TABLE %s ( " +
                "%s INTEGER PRIMARY KEY AUTOINCREMENT, " + // ID
                "%s TEXT, " + // TITLE
                "%s INTEGER, " + // DATE
                "%s INTEGER, " + // PAGE_FROM
                "%s INTEGER, " + // PAGE_TO
                "%s REAL, " + // RATING
                "%s TEXT, " + // CHILD_COMMENT
                "%s TEXT, " + // PARENT_COMMENT
                "%s TEXT)", //BOOK_IMAGE
                        TABLE_NAME, UID, TITLE, DATE, PAGE_FROM, PAGE_TO, RATING, CHILD_COMMENT,
                        PARENT_COMMENT, BOOK_IMAGE);
        private static final String DROP_TABLE = String.format("DROP TABLE IF EXISTS %s", TABLE_NAME);

        private final Context context;

        public dbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                System.out.println(CREATE_TABLE);
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
                Toast.makeText(this.context, "Failed to create DB:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(DROP_TABLE);
                onCreate(db);
            } catch (Exception e) {
                Toast.makeText(this.context, "Failed to update DB:" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
