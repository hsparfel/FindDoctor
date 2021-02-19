package com.pouillos.finddoctor.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pouillos.finddoctor.MyApp;
import com.pouillos.finddoctor.dao.DaoMaster;
import com.pouillos.finddoctor.dao.DaoSession;
import com.pouillos.finddoctor.dao.ProfessionDao;
import com.pouillos.finddoctor.entities.Profession;

import org.greenrobot.greendao.database.Database;

import java.util.List;

public class ProfessionContentProvider extends ContentProvider {

    private DaoSession daoSession;
    private ProfessionDao professionDao;

    // FOR DATA
    public static final String AUTHORITY = "com.pouillos.finddoctor.provider";
    public static final String TABLE_NAME = Profession.class.getSimpleName();
    public static final Uri URI_ITEM = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    public void initialiserDao() {
        //Base pendant dev
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(MyApp.getInstance(), "find_doctor_db");
        //Base de prod
        //AppOpenHelper helper = new AppOpenHelper(this, "find_doctor_db", null);
        Database db = helper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    @Override
    public boolean onCreate() {
        initialiserDao();
        professionDao = daoSession.getProfessionDao();
        return false;
    }

    /*@Nullable
    public List<Profession> listAllProfession() {
        List<Profession> listAllProfession = professionDao.loadAll();
        return listAllProfession;
    }*/

    public Cursor getCursorFromList(List<Profession> listProfession) {
        MatrixCursor cursor = new MatrixCursor(
                new String[] {"_id", "name"}
        );
        for ( Profession profession : listProfession ) {
            cursor.newRow()
                    .add("_id", profession.getId())
                    .add("name", profession.getName());
        }
        return cursor;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (selection == null) {
            Cursor cursor = getCursorFromList(professionDao.loadAll());
            return cursor;
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
