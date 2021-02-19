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
import com.pouillos.finddoctor.dao.SavoirFaireDao;
import com.pouillos.finddoctor.entities.SavoirFaire;

import org.greenrobot.greendao.database.Database;

import java.util.List;

public class SavoirFaireContentProvider extends ContentProvider {

    private DaoSession daoSession;
    private SavoirFaireDao savoirFaireDao;

    // FOR DATA
    public static final String AUTHORITY = "com.pouillos.finddoctor.provider";
    public static final String TABLE_NAME = SavoirFaire.class.getSimpleName();
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
        savoirFaireDao = daoSession.getSavoirFaireDao();
        return false;
    }

    /*@Nullable
    public List<SavoirFaire> listAllSavoirFaire() {
        List<SavoirFaire> listAllSavoirFaire = savoirFaireDao.loadAll();
        return listAllSavoirFaire;
    }*/

    public Cursor getCursorFromList(List<SavoirFaire> listSavoirFaire) {
        MatrixCursor cursor = new MatrixCursor(
                new String[] {"_id", "name"}
        );
        for ( SavoirFaire savoirFaire : listSavoirFaire ) {
            cursor.newRow()
                    .add("_id", savoirFaire.getId())
                    .add("name", savoirFaire.getName());
        }
        return cursor;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (selection == null) {
            Cursor cursor = getCursorFromList(savoirFaireDao.loadAll());
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
