package com.pouillos.finddoctor.activities;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.pouillos.finddoctor.R;
import com.pouillos.finddoctor.activities.afficher.AfficherMesContactsActivity;
import com.pouillos.finddoctor.activities.afficher.AfficherMesEtablissementsActivity;
import com.pouillos.finddoctor.activities.recherche.ChercherContactActivity;
import com.pouillos.finddoctor.activities.recherche.ChercherEtablissementActivity;
import com.pouillos.finddoctor.dao.AppOpenHelper;
import com.pouillos.finddoctor.dao.ContactDao;
import com.pouillos.finddoctor.dao.ContactIgnoreDao;
import com.pouillos.finddoctor.dao.DaoMaster;
import com.pouillos.finddoctor.dao.DaoSession;
import com.pouillos.finddoctor.dao.DepartementDao;
import com.pouillos.finddoctor.dao.EtablissementDao;
import com.pouillos.finddoctor.dao.ImportContactDao;
import com.pouillos.finddoctor.dao.ImportEtablissementDao;

import com.pouillos.finddoctor.dao.ProfessionDao;
import com.pouillos.finddoctor.dao.RegionDao;
import com.pouillos.finddoctor.dao.SavoirFaireDao;
import com.pouillos.finddoctor.dao.TypeEtablissementDao;
import com.pouillos.finddoctor.entities.Departement;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import icepick.Icepick;

public class NavDrawerActivity<T> extends AppCompatActivity {
    //FOR DESIGN

    protected Toolbar toolbar;
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected BottomNavigationView bottomNavigationView;

    protected DaoSession daoSession;

    protected ContactDao contactDao;
    protected DepartementDao departementDao;
    protected ImportContactDao importContactDao;
    protected ImportEtablissementDao importEtablissementDao;
    protected ProfessionDao professionDao;
    protected RegionDao regionDao;
    protected SavoirFaireDao savoirFaireDao;
    protected TypeEtablissementDao typeEtablissementDao;
    protected EtablissementDao etablissementDao;
    protected ContactIgnoreDao contactIgnoreDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialiserDao();

        contactDao = daoSession.getContactDao();
        departementDao = daoSession.getDepartementDao();
        importContactDao = daoSession.getImportContactDao();
        professionDao = daoSession.getProfessionDao();
        regionDao = daoSession.getRegionDao();
        savoirFaireDao = daoSession.getSavoirFaireDao();
        typeEtablissementDao = daoSession.getTypeEtablissementDao();
        etablissementDao = daoSession.getEtablissementDao();
        importEtablissementDao = daoSession.getImportEtablissementDao();
        contactIgnoreDao = daoSession.getContactIgnoreDao();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //2 - Inflate the menu and add it to the Toolbar
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        // 5 - Handle back click to close menu
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myProfilActivity;
        //3 - Handle actions on menu items
      return true;
    }
    // ---------------------
    // CONFIGURATION
    // ---------------------

    // 2 - Configure BottomNavigationView Listener
    public void configureBottomView(){
        this.bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.bottom_navigation_home:
                                ouvrirActiviteSuivante(NavDrawerActivity.this, AccueilActivity.class, true);
                                break;
                            case R.id.bottom_navigation_search_doctor:
                                ouvrirActiviteSuivante(NavDrawerActivity.this, ChercherContactActivity.class, true);
                                break;
                            case R.id.bottom_navigation_search_etablissement:
                                ouvrirActiviteSuivante(NavDrawerActivity.this, ChercherEtablissementActivity.class, true);
                                break;
                            case R.id.bottom_navigation_list_doctor:
                                ouvrirActiviteSuivante(NavDrawerActivity.this, AfficherMesContactsActivity.class, true);
                                break;
                            case R.id.bottom_navigation_list_etablissement:
                                ouvrirActiviteSuivante(NavDrawerActivity.this, AfficherMesEtablissementsActivity.class, true);
                                break;
                        }
                        return true;
                    }
                });
    }

    // 1 - Configure Toolbar
    public void configureToolBar() {
        this.toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
    }

    public void ouvrirActiviteSuivante(Context context, Class classe, boolean bool) {
        Intent intent = new Intent(context, classe);
        startActivity(intent);
        if (bool) {
            finish();
        }
    }

    public void ouvrirActiviteSuivante(Context context, Class classe, String nomExtra, Long objetIdExtra, boolean bool) {
        Intent intent = new Intent(context, classe);
        intent.putExtra(nomExtra, objetIdExtra);
        startActivity(intent);
        if (bool) {
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    protected <T> void buildDropdownMenu(List<T> listObj, Context context, AutoCompleteTextView textView) {
        List<String> listString = new ArrayList<>();
        String[] listDeroulante;
        listDeroulante = new String[listObj.size()];
        for (T object : listObj) {
            listString.add(object.toString());
        }
        listString.toArray(listDeroulante);
        ArrayAdapter adapter = new ArrayAdapter(context, R.layout.list_item, listDeroulante);
        textView.setAdapter(adapter);
    }

    @Override
    public Executor getMainExecutor() {
        return super.getMainExecutor();
    }

    protected boolean isFilled(Object object){
        boolean bool;
        if (object!=null) {
            bool = true;
        } else {
            bool = false;
        }
        return bool;
    }

    protected boolean isValidTel(TextView textView) {
        if (!TextUtils.isEmpty(textView.getText()) && textView.getText().length() <10) {
            return false;
        } else {
            return true;
        }
    }

    protected boolean isValidZip(TextView textView) {
        if (!TextUtils.isEmpty(textView.getText()) && textView.getText().length() <5) {
            return false;
        } else {
            return true;
        }
    }

    protected boolean isEmailAdress(String email){
        Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$");
        Matcher m = p.matcher(email.toUpperCase());
        return m.matches();
    }
    protected boolean isValidEmail(TextView textView) {
        if (!TextUtils.isEmpty(textView.getText()) && !isEmailAdress(textView.getText().toString())) {
            return false;
        } else {
            return true;
        }
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void initialiserDao() {
        AppOpenHelper helper = new AppOpenHelper(this, "find_doctor_db", null);
        Database db = helper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public Departement findDepartement(String cp) {
        Departement departement;
        if (!cp.equalsIgnoreCase("")) {
            departement = departementDao.queryRaw("where numero = ?",cp.substring(0,2)).get(0);
        } else {
            departement = departementDao.queryRaw("where numero = ?","XX").get(0);

        }
        return departement;
    }
}
