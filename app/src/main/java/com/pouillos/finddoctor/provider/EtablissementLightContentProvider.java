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
import com.pouillos.finddoctor.dao.EtablissementLightDao;
import com.pouillos.finddoctor.entities.Contact;
import com.pouillos.finddoctor.entities.ContactLight;
import com.pouillos.finddoctor.entities.Etablissement;
import com.pouillos.finddoctor.entities.EtablissementLight;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

public class EtablissementLightContentProvider extends ContentProvider {

    private DaoSession daoSession;
    private EtablissementLightDao etablissementLightDao;
    private EtablissementDao etablissementDao;

    // FOR DATA
    public static final String AUTHORITY = "com.pouillos.finddoctor.provider";
    public static final String TABLE_NAME = EtablissementLight.class.getSimpleName();
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
        etablissementLightDao = daoSession.getEtablissementLightDao();
        etablissementDao = daoSession.getEtablissementDao();
        return false;
    }

    /*@Nullable
    public List<EtablissementLight> listAllEtablissementLight() {
        List<EtablissementLight> listAllEtablissementLight = etablissementLightDao.loadAll();
        return listAllEtablissementLight;
    }*/

    public Cursor getCursorFromList(List<EtablissementLight> listEtablissementLight) {
        MatrixCursor cursor = new MatrixCursor(
                new String[] {"_id", "numeroFinessET", "raisonSocial", "adresse", "cp", "ville",
                        "telephone", "fax"}
        );
        for ( EtablissementLight etablissementLight : listEtablissementLight ) {
            cursor.newRow()
                    .add("_id", etablissementLight.getId())
                    .add("numeroFinessET", etablissementLight.getNumeroFinessET())
                    .add("raisonSocial", etablissementLight.getRaisonSocial())
                    .add("adresse", etablissementLight.getAdresse())
                    .add("cp", etablissementLight.getCp())
                    .add("ville", etablissementLight.getVille())
                    .add("telephone", etablissementLight.getTelephone())
                    .add("fax", etablissementLight.getFax());
        }
        return cursor;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (selection == null) {
            Cursor cursor = getCursorFromList(etablissementLightDao.loadAll());
            return cursor;
        } else if (selection.equalsIgnoreCase("selected")) {
            List<Etablissement> listEtablissementSelected = etablissementDao.queryRaw("where is_selected = ?","1");
            List<EtablissementLight> listEtablissementLightSelected = new ArrayList<>();
            for (Etablissement current : listEtablissementSelected) {
                EtablissementLight currentEtablissementLight = etablissementLightDao.load(current.getId());
                listEtablissementLightSelected.add(currentEtablissementLight);
            }
            Cursor cursor = getCursorFromList(listEtablissementLightSelected);
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
