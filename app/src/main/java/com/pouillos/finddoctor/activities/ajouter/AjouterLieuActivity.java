package com.pouillos.finddoctor.activities.ajouter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.facebook.stetho.Stetho;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pouillos.finddoctor.R;
import com.pouillos.finddoctor.activities.AccueilActivity;
import com.pouillos.finddoctor.activities.NavDrawerActivity;
import com.pouillos.finddoctor.entities.Lieu;
import com.pouillos.finddoctor.entities.LieuEnregistre;
import com.pouillos.finddoctor.utils.DateUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;

public class AjouterLieuActivity extends NavDrawerActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.textView)
    TextView textView;

    @BindView(R.id.fabSaveLieu)
    FloatingActionButton fabSaveLieu;

    @BindView(R.id.my_progressBar)
    ProgressBar progressBar;

    private LocationManager locationManager;

    double actualLatitude;
    double actualLongitude;

    @BindView(R.id.selectLieuEnregistre)
    AutoCompleteTextView selectLieuEnregistre;
    @BindView(R.id.listLieuEnregistre)
    TextInputLayout listLieuEnregistre;

    @BindView(R.id.textAddresseGeo)
    TextInputEditText textAddresseGeo;
    @BindView(R.id.layoutAddresseGeo)
    TextInputLayout layoutAddresseGeo;
    @BindView(R.id.textDetail)
    TextInputEditText textDetail;
    @BindView(R.id.layoutDetail)
    TextInputLayout layoutDetail;

    List<LieuEnregistre> listeLieuEnregistre;
    LieuEnregistre groupeLieuEnregistre;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_ajouter_lieu);
        Stetho.initializeWithDefaults(this);
        ButterKnife.bind(this);
        this.configureToolBar();

        this.configureBottomView();


        Menu bottomNavigationViewMenu = bottomNavigationView.getMenu();
        bottomNavigationViewMenu.findItem(R.id.bottom_navigation_home).setChecked(false);
        bottomNavigationViewMenu.findItem(R.id.bottom_navigation_add_lieu).setChecked(true);

        textView.setText(DateUtils.ecrireDateLettre(new Date()));

        progressBar.setVisibility(View.VISIBLE);
        AjouterLieuActivity.AsyncTaskRunnerBD runnerBD = new AjouterLieuActivity.AsyncTaskRunnerBD();
        runnerBD.execute();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                actualLatitude = location.getLatitude();
                actualLongitude = location.getLongitude();
                convertirCoordonneesToAdresse();
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 15000, 10, new LocationListener() {

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }

                @Override
                public void onLocationChanged(Location location) {
                    actualLatitude = location.getLatitude();
                    actualLongitude = location.getLongitude();
                    convertirCoordonneesToAdresse();
                }
            });

        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        listeLieuEnregistre = lieuEnregistreDao.loadAll();
        buildDropdownMenu(listeLieuEnregistre,AjouterLieuActivity.this,selectLieuEnregistre);

        selectLieuEnregistre.setOnItemClickListener(this);
        selectLieuEnregistre.setText("Non Defini",false);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        groupeLieuEnregistre = listeLieuEnregistre.get(position);
    }

    @OnClick(R.id.fabSaveLieu)
    public void setFabSaveLieuClick() {
        Lieu lieu = new Lieu();
        Date date = new Date();
        lieu.setDate(date);
        lieu.setDateString(DateUtils.ecrireDateHeure(date));
        lieu.setAdresse(textAddresseGeo.getText().toString());
        lieu.setLatitude(actualLatitude);
        lieu.setLongitude(actualLongitude);
        lieu.setDetail(textDetail.getText().toString());
        if (groupeLieuEnregistre != null) {
            lieu.setLieuEnregistre(groupeLieuEnregistre);
        }
        lieuDao.insert(lieu);
        ouvrirActiviteSuivante(this, AccueilActivity.class,false);
    }

    public void convertirCoordonneesToAdresse() {
        Geocoder coder = new Geocoder(AjouterLieuActivity.this);
        try {
            List<Address> listAddress = coder.getFromLocation(actualLatitude,actualLongitude,1);
            if (listAddress.size()>0) {
                textAddresseGeo.setText(listAddress.get(0).getAddressLine(0));
            }
        } catch (IOException e) {
            textAddresseGeo.setText("Erreur - non trouvé");
            e.printStackTrace();
        }

    }

    private class AsyncTaskRunnerBD extends AsyncTask<Void, Integer, Void> {

        protected Void doInBackground(Void...voids) {
            publishProgress(0);
            publishProgress(10);
            publishProgress(100);
            return null;
        }

        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        protected void onProgressUpdate(Integer... integer) {
            progressBar.setProgress(integer[0],true);
        }
    }
}
