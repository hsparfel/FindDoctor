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
import com.pouillos.finddoctor.dao.ContactLightDao;
import com.pouillos.finddoctor.dao.DaoMaster;
import com.pouillos.finddoctor.dao.DaoSession;
import com.pouillos.finddoctor.dao.AssociationContactLightEtablissementLightDao;
import com.pouillos.finddoctor.dao.EtablissementDao;
import com.pouillos.finddoctor.dao.EtablissementLightDao;
import com.pouillos.finddoctor.entities.AssociationContactLightEtablissementLight;
import com.pouillos.finddoctor.entities.Contact;
import com.pouillos.finddoctor.entities.ContactLight;
import com.pouillos.finddoctor.entities.Etablissement;
import com.pouillos.finddoctor.entities.EtablissementLight;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;

public class AssociationContactLightEtablissementLightContentProvider extends ContentProvider {

    private DaoSession daoSession;
    private AssociationContactLightEtablissementLightDao associationContactLightEtablissementLightDao;
    private EtablissementDao etablissementDao;
    private EtablissementLightDao etablissementLightDao;
    private ContactDao contactDao;
    private ContactLightDao contactLightDao;

    // FOR DATA
    public static final String AUTHORITY = "com.pouillos.finddoctor.provider";
    public static final String TABLE_NAME = AssociationContactLightEtablissementLight.class.getSimpleName();
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
        associationContactLightEtablissementLightDao = daoSession.getAssociationContactLightEtablissementLightDao();
        contactDao = daoSession.getContactDao();
        contactLightDao = daoSession.getContactLightDao();
        etablissementDao = daoSession.getEtablissementDao();
        etablissementLightDao = daoSession.getEtablissementLightDao();
        return false;
    }

    public Cursor getCursorFromList(List<AssociationContactLightEtablissementLight> listAssociationContactLightEtablissementLight) {
        MatrixCursor cursor = new MatrixCursor(
                new String[] {"_id", "contactLight", "etablissementLight"}
        );
        for ( AssociationContactLightEtablissementLight associationContactLightEtablissementLight : listAssociationContactLightEtablissementLight ) {
            cursor.newRow()
                    .add("_id", associationContactLightEtablissementLight.getId())
                    .add("contactLight", associationContactLightEtablissementLight.getContactLight())
                    .add("etablissementLight", associationContactLightEtablissementLight.getEtablissementLight());
        }
        return cursor;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (selection == null) {
            Cursor cursor = getCursorFromList(associationContactLightEtablissementLightDao.loadAll());
            return cursor;
        } else if (selection.equalsIgnoreCase("selected")) {

            List<Contact> listContactSelected = contactDao.queryRaw("where is_selected = ?","1");
            List<ContactLight> listContactLightSelected = new ArrayList<>();
            for (Contact current : listContactSelected) {
                ContactLight currentContactLight = contactLightDao.load(current.getId());
                listContactLightSelected.add(currentContactLight);
            }
            List<Etablissement> listEtablissementSelected = etablissementDao.queryRaw("where is_selected = ?","1");
            List<EtablissementLight> listEtablissementLightSelected = new ArrayList<>();
            for (Etablissement current : listEtablissementSelected) {
                EtablissementLight currentEtablissementLight = etablissementLightDao.load(current.getId());
                listEtablissementLightSelected.add(currentEtablissementLight);
            }

            List<AssociationContactLightEtablissementLight> listAssoc = new ArrayList<>();
            for(ContactLight currentContactLight : listContactLightSelected) {
                for (EtablissementLight currentEtablissementLight : listEtablissementLightSelected) {
                    List<AssociationContactLightEtablissementLight> currentListAssoc = associationContactLightEtablissementLightDao.queryRaw("where contact_light_id = ? and etablisseent_light_id = ?",""+currentContactLight.getId(),""+currentEtablissementLight.getId());
                    listAssoc.addAll(currentListAssoc);
                }
            }
            Cursor cursor = getCursorFromList(listAssoc);
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
