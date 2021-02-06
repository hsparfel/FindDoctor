package com.pouillos.finddoctor.activities.recherche;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pouillos.finddoctor.activities.NavDrawerActivity;
import com.pouillos.finddoctor.activities.afficher.AfficherEtablissementActivity;
import com.pouillos.finddoctor.dao.DaoSession;
import com.pouillos.finddoctor.dao.EtablissementDao;
import com.pouillos.finddoctor.entities.Etablissement;
import com.pouillos.finddoctor.entities.ImportEtablissement;
import com.pouillos.finddoctor.entities.TypeEtablissement;
import com.pouillos.finddoctor.recycler.adapter.RecyclerAdapterEtablissement;
import com.pouillos.finddoctor.R;
import com.pouillos.finddoctor.utils.ItemClickSupport;

import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import icepick.Icepick;
import icepick.State;

public class ChercherEtablissementActivity extends NavDrawerActivity implements RecyclerAdapterEtablissement.Listener {

    @State
    boolean booleanVille;
    @State
    boolean booleanCp;
    @State
    boolean booleanProximite;
    @State
    boolean booleanPharmacie;
    @State
    boolean booleanAutreType;
    @State
    String[] listAutocompletionVille;

    private List<String> listVilleBD;
    private List<TypeEtablissement> listTypeEtablissementBD;
    private List<Etablissement> listEtablissementBD;
    private RecyclerAdapterEtablissement adapterEtablissement;
    List<Etablissement> listEtablissement;

    Etablissement etablissementSelected;

    @BindView(R.id.textZip)
    TextInputEditText textZip;
    @BindView(R.id.layoutZip)
    TextInputLayout layoutZip;

    @BindView(R.id.list_etablissement)
    RecyclerView listeEtablissement;

    @BindView(R.id.chipVille)
    Chip chipVille;
    @BindView(R.id.chipCp)
    Chip chipCp;

    @BindView(R.id.chipPharmacie)
    Chip chipPharmacie;
    @BindView(R.id.chipAutreType)
    Chip chipAutreType;
    @BindView(R.id.chipGroupRechercheType)
    ChipGroup chipGroupRechercheType;

    @BindView(R.id.fabChercher)
    FloatingActionButton fabChercher;
    @BindView(R.id.fabRaz)
    FloatingActionButton fabRaz;

    @BindView(R.id.selectionVille)
    AutoCompleteTextView selectionVille;
    @BindView(R.id.listVille)
    TextInputLayout listVille;
    @BindView(R.id.selectionAutreType)
    AutoCompleteTextView selectionAutreType;
    @BindView(R.id.listAutreType)
    TextInputLayout listAutreType;
    @BindView(R.id.my_progressBar)
    ProgressBar progressBar;

    double actualLatitude;
    double actualLongitude;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_chercher_etablissement);
// 6 - Configure all views
        this.configureToolBar();

        this.configureBottomView();

        ButterKnife.bind(this);

        progressBar.setVisibility(View.VISIBLE);

        ChercherEtablissementActivity.AsyncTaskRunnerBDTypeEtablissement runnerBDTypeEtablissement = new ChercherEtablissementActivity.AsyncTaskRunnerBDTypeEtablissement();
        runnerBDTypeEtablissement.execute();

        fabChercher.hide();
        fabRaz.hide();
        hideAll();
        setTitle("Chercher Etablissement");
    }

    final Looper looper = null;
    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("Location Changes", location.toString());
            actualLatitude=location.getLatitude();
            actualLongitude=location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("Status Changed", String.valueOf(status));
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("Provider Enabled", provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("Provider Disabled", provider);
        }
    };

    public void localiserTelephone() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationProvider provider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            locationManager.requestSingleUpdate(criteria, locationListener, looper);
            Location localisation = locationManager.getLastKnownLocation(provider.getName());
            if (localisation != null) {
                actualLatitude = localisation.getLatitude();
                actualLongitude = localisation.getLongitude();
            }
        } else {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    public static double calculerDistance(double latDepart, double longDepart, double latArrivee, double longArrivee) {
        double distance = 0;
        if ((latDepart != latArrivee) || (longDepart != longArrivee)) {

            double radlat1 = Math.PI * latDepart / 180;
            double radlat2 = Math.PI * latArrivee / 180;
            double theta = longDepart - longArrivee;
            double radtheta = Math.PI * theta / 180;

            distance = Math.sin(radlat1) * Math.sin(radlat2) + Math.cos(radlat1) * Math.cos(radlat2) * Math.cos(radtheta);
            if (distance > 1) {
                distance = 1;
            }
            distance = Math.acos(distance);

            distance = 6371*distance;
        }
        return distance;
    }

    public class AsyncTaskRunnerEtablissement extends AsyncTask<Void, Integer, Void> {

        protected Void doInBackground(Void...voids) {
            publishProgress(0);
            publishProgress(5);
            listEtablissement = new ArrayList<>();
            if (booleanPharmacie) {
                listTypeEtablissementBD = typeEtablissementDao.queryRaw("where name = ?","Pharmacie d'Officine");
            } else if (booleanAutreType) {
                listTypeEtablissementBD = typeEtablissementDao.queryRaw("where name = ?",selectionAutreType.getText().toString());
            }
            if (booleanVille) {
                Query<Etablissement> query = etablissementDao.queryBuilder().where(
                        EtablissementDao.Properties.Ville.eq(selectionVille.getText().toString()), EtablissementDao.Properties.TypeEtablissementId.eq(listTypeEtablissementBD.get(0).getId())
                ).build();
                listEtablissement = query.list();
            } else if (booleanCp) {
                Query<Etablissement> query = etablissementDao.queryBuilder().where(
                        EtablissementDao.Properties.Cp.eq(textZip.getText().toString()), EtablissementDao.Properties.TypeEtablissementId.eq(listTypeEtablissementBD.get(0).getId())
                ).build();
                listEtablissement = query.list();
            }
            for (Etablissement currentEtablissemnt : listEtablissement) {
                currentEtablissemnt.setDistance(calculerDistance(actualLatitude,actualLongitude,currentEtablissemnt.getLatitude(),currentEtablissemnt.getLongitude()));
            }
            Collections.sort(listEtablissement);
            publishProgress(100);
            return null;
        }

        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            if (listEtablissement.size() == 0) {
                Toast.makeText(ChercherEtablissementActivity.this, R.string.text_no_matching, Toast.LENGTH_LONG).show();

            } else {
                configureRecyclerView();
                configureOnClickRecyclerView();
                listeEtablissement.setVisibility(View.VISIBLE);
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        protected void onProgressUpdate(Integer... integer) {
            progressBar.setProgress(integer[0],true);
        }
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(listeEtablissement, R.layout.recycler_list_etablissement)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Log.e("TAG", "Position : "+position);
                    }
                });
    }

    public void configureRecyclerView() {
        adapterEtablissement = new RecyclerAdapterEtablissement(listEtablissement, this);
        listeEtablissement.setAdapter(adapterEtablissement);
        listeEtablissement.setLayoutManager(new LinearLayoutManager(this));
    }

    private void hideAll() {
        listVille.setVisibility(View.GONE);
        listAutreType.setVisibility(View.GONE);
        layoutZip.setVisibility(View.GONE);
        chipGroupRechercheType.setVisibility(View.GONE);
        listeEtablissement.setVisibility(View.GONE);
        booleanVille = false;
        booleanAutreType = false;
        booleanCp = false;
        booleanPharmacie = false;
        booleanProximite = false;
    }

    @Override
    public void onClickEtablissementButton(int position) {
         Etablissement etablissement = listEtablissement.get(position);
         ouvrirActiviteSuivante(this, AfficherEtablissementActivity.class,"etablissementId",etablissement.getId(),false);
    }

    public class AsyncTaskRunnerEtablissementVille extends AsyncTask<Void, Integer, Void> {

        protected Void doInBackground(Void...voids) {
            publishProgress(0);
            publishProgress(25);
            List<String> listVilleString = new ArrayList<>();
            listAutocompletionVille = new String[listVilleBD.size()];
            for (String currentVille : listVilleBD) {
                listVilleString.add(currentVille);
            }
            listVilleString.toArray(listAutocompletionVille);

            publishProgress(100);
            return null;
        }

        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            //publishProgress(0);
            if (listVilleBD.size() == 0) {
                Toast.makeText(ChercherEtablissementActivity.this, R.string.text_no_matching, Toast.LENGTH_LONG).show();
                //textRechercheIntervenant.setVisibility(View.GONE);
                //layoutContact.setVisibility(View.GONE);

            } else {
                //layoutContact.setVisibility(View.VISIBLE);
                listVille.setVisibility(View.VISIBLE);
                selectionVille.requestFocus();
            }

            ArrayAdapter<String> adapter  = new ArrayAdapter<String>(ChercherEtablissementActivity.this, R.layout.list_item_two_lines, R.id.item, listAutocompletionVille);
            selectionVille.setAdapter(adapter);
            selectionVille.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                        long arg3) {
                    Toast.makeText(ChercherEtablissementActivity.this, "Selected ville: \t" + arg0.getAdapter().getItem(arg2), Toast.LENGTH_SHORT).show();
                    chipGroupRechercheType.setVisibility(View.VISIBLE);
                }
            });
            selectionVille.setThreshold(1);

            //layoutContact.setVisibility(View.VISIBLE);


            //Masquer & raz tout le reste



        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        protected void onProgressUpdate(Integer... integer) {
            progressBar.setProgress(integer[0],true);
        }
    }

    public class AsyncTaskRunnerBDTypeEtablissement extends AsyncTask<Void, Integer, Void> {

        protected Void doInBackground(Void...voids) {
            publishProgress(0);
            listTypeEtablissementBD = typeEtablissementDao.loadAll();
            Collections.sort(listTypeEtablissementBD);
            listTypeEtablissementBD.remove(0);
            publishProgress(50);
            listVilleBD = new ArrayList<>();
            //todo trouver une requete un peu plus light pour accelerer - select distinct ville from etablissement
            /*listEtablissementBD = etablissementDao.loadAll();
            for (Etablissement currentEtablissement : listEtablissementBD) {
                if (!listVilleBD.contains(currentEtablissement.getVille())) {
                    listVilleBD.add(currentEtablissement.getVille());
                }
            }*/


            //requete raw pr selectionner que la colonne ville de Etablissement
            String SQL_DISTINCT_ENAME = "SELECT DISTINCT "+ EtablissementDao.Properties.Ville.columnName +" FROM "+EtablissementDao.TABLENAME;

            //public List<String> listEName      (DaoSession daoSession {
               // ArrayList<String> result = new ArrayList<String>();


                Cursor c = daoSession.getDatabase().rawQuery(SQL_DISTINCT_ENAME, null);
                try{
                    if (c.moveToFirst()) {
                        do {
                            listVilleBD.add(c.getString(0));
                        } while (c.moveToNext());
                    }
                } finally {
                    c.close();
                }

            //listVilleBD = executerSql(SQL_DISTINCT_ENAME, String.class);



                //return result;
           // }


            /////////

            Collections.sort(listVilleBD);
            publishProgress(100);
            return null;
        }

        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            buildDropdownMenu(listTypeEtablissementBD, ChercherEtablissementActivity.this,selectionAutreType);
            localiserTelephone();
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        protected void onProgressUpdate(Integer... integer) {
            progressBar.setProgress(integer[0],true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @OnClick(R.id.chipVille)
    public void chipVilleClick() {
        booleanVille=!booleanVille;
        if (booleanVille) {
            progressBar.setVisibility(View.VISIBLE);
            ChercherEtablissementActivity.AsyncTaskRunnerEtablissementVille runnerEtablissementVille = new ChercherEtablissementActivity.AsyncTaskRunnerEtablissementVille();
            runnerEtablissementVille.execute();
            chipVille.setEnabled(false);
            chipCp.setEnabled(true);
            booleanCp=false;
            chipPharmacie.setEnabled(true);
            chipPharmacie.setChecked(false);
            booleanPharmacie=false;
            chipAutreType.setEnabled(true);
            chipAutreType.setChecked(false);
            booleanAutreType=false;
            chipGroupRechercheType.setVisibility(View.GONE);
            layoutZip.setVisibility(View.GONE);
            textZip.setText("");
            listAutreType.setVisibility(View.GONE);
            selectionAutreType.setText("",false);
            listeEtablissement.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @OnClick(R.id.chipCp)
    public void chipCpClick() {
        booleanCp=!booleanCp;
        if (booleanCp) {
            layoutZip.setVisibility(View.VISIBLE);
            textZip.requestFocus();
            chipVille.setEnabled(true);
            chipCp.setEnabled(false);
            booleanVille=false;
            chipPharmacie.setEnabled(true);
            chipPharmacie.setChecked(false);
            booleanPharmacie=false;
            chipAutreType.setEnabled(true);
            chipAutreType.setChecked(false);
            booleanAutreType=false;
            chipGroupRechercheType.setVisibility(View.GONE);
            listVille.setVisibility(View.GONE);
            selectionVille.setText("",false);
            listAutreType.setVisibility(View.GONE);
            selectionAutreType.setText("",false);
            listeEtablissement.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @OnClick(R.id.chipPharmacie)
    public void chipPharmacieClick() {
        booleanPharmacie=!booleanPharmacie;
        if (booleanPharmacie) {
            progressBar.setVisibility(View.VISIBLE);
            ChercherEtablissementActivity.AsyncTaskRunnerEtablissement runnerEtablissement = new ChercherEtablissementActivity.AsyncTaskRunnerEtablissement();
            runnerEtablissement.execute();
            chipPharmacie.setEnabled(false);
            chipAutreType.setEnabled(true);
            chipAutreType.setEnabled(true);
            chipAutreType.setChecked(false);
            booleanAutreType=false;
            listAutreType.setVisibility(View.GONE);
            selectionAutreType.setText("",false);
            listeEtablissement.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @OnClick(R.id.chipAutreType)
    public void chipAutreTypeClick() {
        booleanAutreType=!booleanAutreType;
        if (booleanAutreType) {
            listAutreType.setVisibility(View.VISIBLE);
            selectionAutreType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View view, int arg2,
                                        long arg3) {
                    //Toast.makeText(ChercherEtablissementActivity.this, "Selected type: \t" + arg0.getAdapter().getItem(arg2), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.VISIBLE);
                    ChercherEtablissementActivity.AsyncTaskRunnerEtablissement runnerEtablissement = new ChercherEtablissementActivity.AsyncTaskRunnerEtablissement();
                    runnerEtablissement.execute();
                }
            });
            chipPharmacie.setEnabled(true);
            chipPharmacie.setChecked(false);
            booleanPharmacie=false;
            chipAutreType.setEnabled(false);
            booleanPharmacie=false;
            listVille.setVisibility(View.GONE);
            selectionVille.setText("",false);
            listeEtablissement.setVisibility(View.GONE);
        }
    }

    @OnTextChanged(R.id.textZip)
    public void textZipChange() {
        if (textZip.getText().toString().length() == 5) {
            chipGroupRechercheType.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}
