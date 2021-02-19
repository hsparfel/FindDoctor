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
import com.pouillos.finddoctor.dao.EtablissementDao;
import com.pouillos.finddoctor.entities.Etablissement;

import org.greenrobot.greendao.database.Database;

import java.util.List;

public class EtablissementContentProvider extends ContentProvider {

    private DaoSession daoSession;
    private EtablissementDao etablissementDao;

    // FOR DATA
    public static final String AUTHORITY = "com.pouillos.finddoctor.provider";
    public static final String TABLE_NAME = Etablissement.class.getSimpleName();
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
        etablissementDao = daoSession.getEtablissementDao();
        return false;
    }

    /*@Nullable
    public List<Etablissement> listAllEtablissement() {
        List<Etablissement> listAllEtablissement = etablissementDao.loadAll();
        return listAllEtablissement;
    }*/

    public Cursor getCursorFromList(List<Etablissement> listEtablissement) {
        MatrixCursor cursor = new MatrixCursor(
                new String[] {"_id", "numeroFinessET", "raisonSocial", "adresse", "cp", "ville",
                        "telephone", "fax", "departement", "region", "latitude", "longitude", "typeEtablissement",
                        "isSelected"}
        );
        for ( Etablissement etablissement : listEtablissement ) {
            cursor.newRow()
                    .add("_id", etablissement.getId())
                    .add("numeroFinessET", etablissement.getNumeroFinessET())
                    .add("raisonSocial", etablissement.getRaisonSocial())
                    .add("adresse", etablissement.getAdresse())
                    .add("cp", etablissement.getCp())
                    .add("ville", etablissement.getVille())
                    .add("telephone", etablissement.getTelephone())
                    .add("fax", etablissement.getFax())
                    .add("departement", etablissement.getDepartement())
                    .add("region", etablissement.getRegion())
                    .add("latitude", etablissement.getLatitude())
                    .add("longitude", etablissement.getLongitude())
                    .add("typeEtablissement", etablissement.getTypeEtablissement())
                    .add("isSelected", etablissement.getIsSelected());
        }
        return cursor;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (selection == null) {
            Cursor cursor = getCursorFromList(etablissementDao.loadAll());
            return cursor;
        } else if (selection.equalsIgnoreCase("selected")) {
            Cursor cursor = getCursorFromList(etablissementDao.queryRaw("where is_selected = ?","1"));
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
