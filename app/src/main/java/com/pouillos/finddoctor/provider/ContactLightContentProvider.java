package com.pouillos.finddoctor.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.pouillos.finddoctor.MyApp;
import com.pouillos.finddoctor.dao.ContactDao;
import com.pouillos.finddoctor.dao.DaoMaster;
import com.pouillos.finddoctor.dao.DaoSession;
import com.pouillos.finddoctor.dao.ContactLightDao;
import com.pouillos.finddoctor.entities.Contact;
import com.pouillos.finddoctor.entities.ContactLight;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

public class ContactLightContentProvider extends ContentProvider {

    private DaoSession daoSession;
    private ContactLightDao contactLightDao;
    private ContactDao contactDao;

    // FOR DATA
    public static final String AUTHORITY = "com.pouillos.finddoctor.provider";
    public static final String TABLE_NAME = ContactLight.class.getSimpleName();
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
        contactLightDao = daoSession.getContactLightDao();
        contactDao = daoSession.getContactDao();
        return false;
    }

    /*@Nullable
    public List<ContactLight> listAllContactLight() {
        List<ContactLight> listAllContactLight = contactLightDao.loadAll();
        return listAllContactLight;
    }*/

    public Cursor getCursorFromList(List<ContactLight> listContactLight) {
        MatrixCursor cursor = new MatrixCursor(
                new String[] {"_id", "idPP", "codeCivilite", "nom", "prenom", "raisonSocial", "complement", "adresse", "cp", "ville", "telephone", "fax", "email",}
        );
        for ( ContactLight contactLight : listContactLight ) {
            cursor.newRow()
                    .add("_id", contactLight.getId())
                    .add("idPP", contactLight.getIdPP())
                    .add("codeCivilite", contactLight.getCodeCivilite())
                    .add("nom", contactLight.getNom())
                    .add("prenom", contactLight.getPrenom())
                    .add("raisonSocial", contactLight.getRaisonSocial())
                    .add("complement", contactLight.getComplement())
                    .add("adresse", contactLight.getAdresse())
                    .add("cp", contactLight.getCp())
                    .add("ville", contactLight.getVille())
                    .add("telephone", contactLight.getTelephone())
                    .add("fax", contactLight.getFax())
                    .add("email", contactLight.getEmail());
        }
        return cursor;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (selection == null) {
            Cursor cursor = getCursorFromList(contactLightDao.loadAll());
            return cursor;
        } else if (selection.equalsIgnoreCase("selected")) {
            List<Contact> listContactSelected = contactDao.queryRaw("where is_selected = ?","1");
            List<ContactLight> listContactLightSelected = new ArrayList<>();
            for (Contact current : listContactSelected) {
                ContactLight currentContactLight = contactLightDao.load(current.getId());
                listContactLightSelected.add(currentContactLight);
            }
            Cursor cursor = getCursorFromList(listContactLightSelected);
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
