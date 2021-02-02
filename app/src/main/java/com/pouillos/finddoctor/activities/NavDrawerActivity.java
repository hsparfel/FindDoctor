package com.pouillos.finddoctor.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.pouillos.finddoctor.R;
import com.pouillos.finddoctor.activities.ajouter.AjouterLieuActivity;
import com.pouillos.finddoctor.activities.ajouter.AjouterLieuEnregistreActivity;
import com.pouillos.finddoctor.dao.AppOpenHelper;
import com.pouillos.finddoctor.dao.DaoMaster;
import com.pouillos.finddoctor.dao.DaoSession;
import com.pouillos.finddoctor.dao.LieuDao;
import com.pouillos.finddoctor.dao.LieuEnregistreDao;

import org.greenrobot.greendao.database.Database;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import icepick.Icepick;

public class NavDrawerActivity extends AppCompatActivity {
    //FOR DESIGN

    protected Toolbar toolbar;
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected BottomNavigationView bottomNavigationView;

    protected DaoSession daoSession;

    protected LieuEnregistreDao lieuEnregistreDao;
    protected LieuDao lieuDao;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //a redefinit à chq fois
        super.onCreate(savedInstanceState);
        //initialiser greenDAO
        initialiserDao();
        lieuEnregistreDao = daoSession.getLieuEnregistreDao();
        lieuDao = daoSession.getLieuDao();

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
                            case R.id.bottom_navigation_add_lieu:
                                ouvrirActiviteSuivante(NavDrawerActivity.this, AjouterLieuActivity.class, true);
                                break;
                            case R.id.bottom_navigation_add_lieu_enregistre:
                                ouvrirActiviteSuivante(NavDrawerActivity.this, AjouterLieuEnregistreActivity.class, true);
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

    protected boolean isFilled(TextInputEditText textInputEditText){
        boolean bool;
        if (textInputEditText.length()>0) {
            bool = true;
        } else {
            bool = false;
        }
        return bool;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public void initialiserDao() {
        AppOpenHelper helper = new AppOpenHelper(this, "where_is_my_car_db", null);
        Database db = helper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }
}
