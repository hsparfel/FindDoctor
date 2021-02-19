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
import com.pouillos.finddoctor.dao.ContactDao;
import com.pouillos.finddoctor.entities.Contact;

import org.greenrobot.greendao.database.Database;

import java.util.List;

public class ContactContentProvider extends ContentProvider {

    private DaoSession daoSession;
    private ContactDao contactDao;

    // FOR DATA
    public static final String AUTHORITY = "com.pouillos.finddoctor.provider";
    public static final String TABLE_NAME = Contact.class.getSimpleName();
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
        contactDao = daoSession.getContactDao();
        return false;
    }

    /*@Nullable
    public List<Contact> listAllContact() {
        List<Contact> listAllContact = contactDao.loadAll();
        return listAllContact;
    }*/

    public Cursor getCursorFromList(List<Contact> listContact) {
        MatrixCursor cursor = new MatrixCursor(
                new String[] {"_id", "idPP", "codeCivilite", "nom", "prenom", "profession", "savoirFaire",
                        "raisonSocial", "complement", "adresse", "cp", "ville", "telephone", "fax", "email",
                        "departement", "region", "latitude", "longitude", "isSelected"}
        );
        for ( Contact contact : listContact ) {
            cursor.newRow()
                    .add("_id", contact.getId())
                    .add("idPP", contact.getIdPP())
                    .add("codeCivilite", contact.getCodeCivilite())
                    .add("nom", contact.getNom())
                    .add("prenom", contact.getPrenom())
                    .add("profession", contact.getProfession())
                    .add("savoirFaire", contact.getSavoirFaire())
                    .add("raisonSocial", contact.getRaisonSocial())
                    .add("complement", contact.getComplement())
                    .add("adresse", contact.getAdresse())
                    .add("cp", contact.getCp())
                    .add("ville", contact.getVille())
                    .add("telephone", contact.getTelephone())
                    .add("fax", contact.getFax())
                    .add("email", contact.getEmail())
                    .add("departement", contact.getDepartement())
                    .add("region", contact.getRegion())
                    .add("latitude", contact.getLatitude())
                    .add("longitude", contact.getLongitude())
                    .add("isSelected", contact.getIsSelected());
        }
        return cursor;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (selection == null) {
            Cursor cursor = getCursorFromList(contactDao.loadAll());
            return cursor;
        } else if (selection.equalsIgnoreCase("selected")) {
            Cursor cursor = getCursorFromList(contactDao.queryRaw("where is_selected = ?","1"));
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
