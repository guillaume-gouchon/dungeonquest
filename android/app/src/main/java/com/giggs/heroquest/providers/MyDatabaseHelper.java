package com.giggs.heroquest.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.giggs.heroquest.models.Game;
import com.giggs.heroquest.utils.providers.AbstractDatabaseHelper;


public class MyDatabaseHelper extends AbstractDatabaseHelper {

    private static final String DATABASE_NAME = "dungeon_quest";
    private static final int DATABASE_VERSION = 1000;

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Game.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Game.TABLE_NAME);
        db.execSQL(Game.CREATE_TABLE);
    }

    public static boolean isDataBaseReady(Context context) {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = context.getDatabasePath(DATABASE_NAME).toString();
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            // database does't exist yet.
        }

        if (checkDB != null) {
            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

}
