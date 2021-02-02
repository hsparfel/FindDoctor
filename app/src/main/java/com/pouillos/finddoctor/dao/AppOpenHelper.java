package com.pouillos.finddoctor.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class AppOpenHelper extends DaoMaster.OpenHelper {
    public AppOpenHelper(Context context, String name) {
        super(context, name);
    }

    public AppOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }
}
