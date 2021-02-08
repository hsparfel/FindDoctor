package com.pouillos.finddoctor.activities.afficher;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pouillos.finddoctor.R;
import com.pouillos.finddoctor.activities.NavDrawerActivity;
import com.pouillos.finddoctor.entities.Contact;
import com.pouillos.finddoctor.entities.Etablissement;
import com.pouillos.finddoctor.entities.Region;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;

public class AfficherMesEtablissementsActivity extends NavDrawerActivity implements AdapterView.OnItemClickListener {


    Etablissement etablissementSelected;

    List<Etablissement> listMesEtablissementsBD;
    ArrayAdapter adapter;

    @BindView(R.id.selectEtablissement)
    AutoCompleteTextView selectedEtablissement;
    @BindView(R.id.listEtablissements)
    TextInputLayout listEtablissements;


    @BindView(R.id.layoutRaisonSocial)
    TextInputLayout layoutRaisonSocial;
    @BindView(R.id.textRaisonSocial)
    TextInputEditText textRaisonSocial;


    @BindView(R.id.layoutType)
    TextInputLayout layoutType;
    @BindView(R.id.textType)
    TextInputEditText textType;

    @BindView(R.id.layoutNumStreet)
    TextInputLayout layoutNumStreet;
    @BindView(R.id.textNumStreet)
    TextInputEditText textNumStreet;

    @BindView(R.id.layoutZip)
    TextInputLayout layoutZip;
    @BindView(R.id.textZip)
    TextInputEditText textZip;
    @BindView(R.id.layoutTown)
    TextInputLayout layoutTown;
    @BindView(R.id.textTown)
    TextInputEditText textTown;

    @BindView(R.id.layoutPhone)
    TextInputLayout layoutPhone;
    @BindView(R.id.textPhone)
    TextInputEditText textPhone;

    @BindView(R.id.layoutFax)
    TextInputLayout layoutFax;
    @BindView(R.id.textFax)
    TextInputEditText textFax;


    @BindView(R.id.fabGoogleMap)
    FloatingActionButton fabGoogleMap;
    @BindView(R.id.fabWaze)
    FloatingActionButton fabWaze;
    @BindView(R.id.fabSave)
    FloatingActionButton fabSave;
    @BindView(R.id.fabCancel)
    FloatingActionButton fabCancel;
    @BindView(R.id.fabEdit)
    FloatingActionButton fabEdit;
    @BindView(R.id.fabDelete)
    FloatingActionButton fabDelete;
    @BindView(R.id.my_progressBar)
    ProgressBar progressBar;

    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_afficher_mes_etablissements);

        this.configureToolBar();
        this.configureBottomView();

        ButterKnife.bind(this);

        progressBar.setVisibility(View.VISIBLE);

        AfficherMesEtablissementsActivity.AsyncTaskRunner runner = new AfficherMesEtablissementsActivity.AsyncTaskRunner();
        runner.execute();

        setTitle("Mes Etablissements");

        displayFabs();
        hideAllFields();

        selectedEtablissement.setOnItemClickListener(this);

        Menu bottomNavigationViewMenu = bottomNavigationView.getMenu();
        bottomNavigationViewMenu.findItem(R.id.bottom_navigation_list_etablissement).setChecked(true);
    }



    public boolean checkFields() {
        boolean bool = true;
        if (!TextUtils.isEmpty(textZip.getText()) && !isFilled(textZip.getText())) {
            layoutZip.setError(getString(R.string.not_valid));
            bool = false;
        } else {
            layoutZip.setError(null);
        }
        if (!TextUtils.isEmpty(textPhone.getText()) && !isFilled(textPhone.getText())) {
            layoutPhone.setError(getString(R.string.not_valid));
            bool = false;
        } else {
            layoutPhone.setError(null);
        }
        if (!TextUtils.isEmpty(textFax.getText()) && !isFilled(textFax.getText())) {
            layoutFax.setError(getString(R.string.not_valid));
            bool = false;
        } else {
            layoutFax.setError(null);
        }
        if (!TextUtils.isEmpty(textZip.getText()) && !isValidZip(textZip)) {
            layoutZip.setError(getString(R.string.not_valid_five_digits));
            bool = false;
        } else {
            layoutZip.setError(null);
        }
        if (!TextUtils.isEmpty(textPhone.getText()) && !isValidTel(textPhone)) {
            layoutPhone.setError(getString(R.string.not_valid_ten_digits));
            bool = false;
        } else {
            layoutPhone.setError(null);
        }
        if (!TextUtils.isEmpty(textFax.getText()) && !isValidTel(textFax)) {
            layoutFax.setError(getString(R.string.not_valid_ten_digits));
            bool = false;
        } else {
            layoutPhone.setError(null);
        }
        return bool;
    }

    private void resizeAllFields(boolean bool) {
        if (bool) {

            textRaisonSocial.setMinWidth(getResources().getDimensionPixelOffset(R.dimen.field_min_width));
            textType.setMinWidth(getResources().getDimensionPixelOffset(R.dimen.field_min_width));
            textType.setMinWidth(getResources().getDimensionPixelOffset(R.dimen.field_min_width));
            textNumStreet.setMinWidth(getResources().getDimensionPixelOffset(R.dimen.field_min_width));
            textZip.setMinWidth(getResources().getDimensionPixelOffset(R.dimen.field_min_width));
            textTown.setMinWidth(getResources().getDimensionPixelOffset(R.dimen.field_min_width));
            textPhone.setMinWidth(getResources().getDimensionPixelOffset(R.dimen.field_min_width));
            textFax.setMinWidth(getResources().getDimensionPixelOffset(R.dimen.field_min_width));

        } else {

            textRaisonSocial.setMinWidth(0);
            textType.setMinWidth(0);
            textType.setMinWidth(0);
            textNumStreet.setMinWidth(0);
            textZip.setMinWidth(0);
            textTown.setMinWidth(0);
            textPhone.setMinWidth(0);
            textFax.setMinWidth(0);

        }

    }

    private void clearAllFields() {

        textRaisonSocial.setText(null);

        textType.setText(null);
        textType.setText(null);

        textNumStreet.setText(null);
        textZip.setText(null);
        textTown.setText(null);
        textPhone.setText(null);
        textFax.setText(null);

    }


    public void displayFabs() {
        fabSave.hide();
        fabCancel.hide();
        if (etablissementSelected == null) {
            fabEdit.hide();
            fabGoogleMap.hide();

            fabWaze.hide();
            fabDelete.hide();
        } else {

            fabEdit.show();
            fabDelete.show();

            if (etablissementSelected.getAdresse() != null && etablissementSelected.getCp() != null &&
                    etablissementSelected.getVille() != null && !etablissementSelected.getAdresse().equalsIgnoreCase("")
                    && !etablissementSelected.getCp().equalsIgnoreCase("") && !etablissementSelected.getVille().equalsIgnoreCase("")) {
                fabWaze.show();
                fabGoogleMap.show();
            } else {
                fabWaze.hide();
                fabGoogleMap.hide();
            }
        }
    }

    private void fillAllFields() {

        if (etablissementSelected.getRaisonSocial() != null && !etablissementSelected.getRaisonSocial().equalsIgnoreCase("")) {
            textRaisonSocial.setText(etablissementSelected.getRaisonSocial());
        }

        if (etablissementSelected.getTypeEtablissement() != null && !etablissementSelected.getTypeEtablissement().getName().equalsIgnoreCase("")) {
            textType.setText(etablissementSelected.getTypeEtablissement().getName());
        }

        if (etablissementSelected.getAdresse() != null && !etablissementSelected.getAdresse().equalsIgnoreCase("")) {
            textNumStreet.setText(etablissementSelected.getAdresse());
        }
        if (etablissementSelected.getCp() != null && !etablissementSelected.getCp().equalsIgnoreCase("")) {
            textZip.setText(etablissementSelected.getCp());
        }
        if (etablissementSelected.getVille() != null && !etablissementSelected.getVille().equalsIgnoreCase("")) {
            textTown.setText(etablissementSelected.getVille());
        }
        if (etablissementSelected.getTelephone() != null && !etablissementSelected.getTelephone().equalsIgnoreCase("")) {
            textPhone.setText(etablissementSelected.getTelephone());
        }
        if (etablissementSelected.getFax() != null && !etablissementSelected.getFax().equalsIgnoreCase("")) {
            textFax.setText(etablissementSelected.getFax());
        }

    }

    private void enableFields(boolean bool) {

        layoutRaisonSocial.setEnabled(false);

        layoutType.setEnabled(false);

        layoutNumStreet.setEnabled(bool);
        layoutZip.setEnabled(bool);
        layoutTown.setEnabled(bool);
        layoutPhone.setEnabled(bool);
        layoutFax.setEnabled(bool);

    }

    private void displayAllFields(boolean bool) {
        layoutRaisonSocial.setVisibility(View.VISIBLE);

        layoutType.setVisibility(View.VISIBLE);
        if (bool) {

            layoutNumStreet.setVisibility(View.VISIBLE);
            layoutZip.setVisibility(View.VISIBLE);
            layoutTown.setVisibility(View.VISIBLE);
            layoutPhone.setVisibility(View.VISIBLE);
            layoutFax.setVisibility(View.VISIBLE);

        } else {

            if (textNumStreet.getText() == null || textNumStreet.getText().toString().equalsIgnoreCase("")) {
                layoutNumStreet.setVisibility(View.GONE);
            } else {
                layoutNumStreet.setVisibility(View.VISIBLE);
            }
            if (textZip.getText() == null || textZip.getText().toString().equalsIgnoreCase("")) {
                layoutZip.setVisibility(View.GONE);
            } else {
                layoutZip.setVisibility(View.VISIBLE);
            }
            if (textTown.getText() == null || textTown.getText().toString().equalsIgnoreCase("")) {
                layoutTown.setVisibility(View.GONE);
            } else {
                layoutTown.setVisibility(View.VISIBLE);
            }
            if (textPhone.getText() == null || textPhone.getText().toString().equalsIgnoreCase("")) {
                layoutPhone.setVisibility(View.GONE);
            } else {
                layoutPhone.setVisibility(View.VISIBLE);
            }
            if (textFax.getText() == null || textFax.getText().toString().equalsIgnoreCase("")) {
                layoutFax.setVisibility(View.GONE);
            } else {
                layoutFax.setVisibility(View.VISIBLE);
            }

        }
    }

    private void hideAllFields() {
        layoutRaisonSocial.setVisibility(View.GONE);
        layoutType.setVisibility(View.GONE);
        layoutNumStreet.setVisibility(View.GONE);
        layoutZip.setVisibility(View.GONE);
        layoutTown.setVisibility(View.GONE);
        layoutPhone.setVisibility(View.GONE);
        layoutFax.setVisibility(View.GONE);
    }

    public class AsyncTaskRunner extends AsyncTask<Void, Integer, Void> {

        protected Void doInBackground(Void...voids) {
            publishProgress(0);
            
            publishProgress(10);
            /*String requete = "";
            listMesEtablissementsBD = new ArrayList<>();
            requete += "SELECT E.* FROM ETABLISSEMENT AS E INNER JOIN ASSOCIATION_UTILISATEUR_ETABLISSEMENT AS AUE ON E.ID = AUE.ETABLISSEMENT WHERE AUE.UTILISATEUR = "+activeUser.getId().toString();
            requete += " ORDER BY RAISON_SOCIAL";
            listMesEtablissementsBD = Etablissement.findWithQuery(Etablissement.class, requete);
*/
            /////////
            listMesEtablissementsBD = etablissementDao.queryRaw("where is_selected = ?", "1");

            /////////
            
            publishProgress(100);
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            if (listMesEtablissementsBD.size() == 0) {
                //Toast.makeText(AfficherMesEtablissementsActivity.this, R.string.text_no_matching, Toast.LENGTH_LONG).show();
                Snackbar.make(layoutRaisonSocial, R.string.text_no_matching, Snackbar.LENGTH_SHORT).setAnchorView(layoutRaisonSocial).show();

                //listEtablissements.setVisibility(View.GONE);
            } else {
                buildDropdownMenu(listMesEtablissementsBD, AfficherMesEtablissementsActivity.this,selectedEtablissement);


            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        protected void onProgressUpdate(Integer... integer) {
            progressBar.setProgress(integer[0],true);
        }
    }

    @OnClick(R.id.fabGoogleMap)
    public void fabGoogleMapClick() {
            String url = "geo:";
            String addr = "";
            if (etablissementSelected.getLatitude() != 0 && etablissementSelected.getLongitude() != 0) {
                url += etablissementSelected.getLatitude()+","+etablissementSelected.getLongitude();
                url += "?q="+etablissementSelected.getLatitude()+","+etablissementSelected.getLongitude();
            } else if (etablissementSelected.getAdresse() != null && etablissementSelected.getCp() != null && etablissementSelected.getVille() != null) {
                url += "0,0?q=";
                addr += Uri.parse(etablissementSelected.getAdresse()+", "+etablissementSelected.getCp()+", "+etablissementSelected.getVille()+", FRANCE");
                url += addr;
            }
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
            intent.setPackage("com.google.android.apps.maps");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
    }

    @OnClick(R.id.fabWaze)
    public void fabWazeClick() {
        try
        {
            String url = "https://waze.com/ul?q=";
            url += etablissementSelected.getAdresse()+"%20"+etablissementSelected.getCp()+"%20"+etablissementSelected.getVille();
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
            startActivity( intent );
        }
        catch ( ActivityNotFoundException ex  )
        {
            Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
            startActivity(intent);
        }
    }

    @OnClick(R.id.fabSave)
    public void fabSaveClick() {

        Toast.makeText(AfficherMesEtablissementsActivity.this, "à implementer 5a", Toast.LENGTH_LONG).show();
        if (checkFields()) {

        if (textNumStreet.getText() != null && !textNumStreet.getText().toString().equalsIgnoreCase(etablissementSelected.getAdresse())) {
            etablissementSelected.setAdresse(textNumStreet.getText().toString().toUpperCase());
        }

        if (textZip.getText() != null && !textZip.getText().toString().equalsIgnoreCase(etablissementSelected.getCp())) {
            etablissementSelected.setCp(textZip.getText().toString().toUpperCase());
        }

        if (textTown.getText() != null && !textTown.getText().toString().equalsIgnoreCase(etablissementSelected.getVille())) {
            etablissementSelected.setVille(textTown.getText().toString().toUpperCase());
        }

        if (textPhone.getText() != null && !textPhone.getText().toString().equalsIgnoreCase(etablissementSelected.getTelephone())) {
            etablissementSelected.setTelephone(textPhone.getText().toString().toUpperCase());
        }

        if (textFax.getText() != null && !textFax.getText().toString().equalsIgnoreCase(etablissementSelected.getFax())) {
            etablissementSelected.setFax(textFax.getText().toString().toUpperCase());
        }

        //etablissementSelected.save();
        etablissementDao.update(etablissementSelected);
        enableFields(false);
        displayAllFields(false);
        displayFabs();
        Toast.makeText(AfficherMesEtablissementsActivity.this, R.string.modification_saved, Toast.LENGTH_LONG).show();

        }
    }

    @OnClick(R.id.fabCancel)
    public void fabCancelClick() {
        clearAllFields();
        resizeAllFields(false);
        enableFields(false);
        displayFabs();
        fillAllFields();
        displayAllFields(false);
    }

    @OnClick(R.id.fabEdit)
    public void fabEditClick() {
        enableFields(true);
        displayAllFields(true);
        resizeAllFields(true);
        fabSave.show();
        fabCancel.show();
        fabEdit.hide();
        fabDelete.hide();
        fabWaze.hide();
        fabGoogleMap.hide();
    }

    @OnClick(R.id.fabDelete)
    public void fabDeleteClick() {
        /*List<AssociationUtilisateurEtablissement> associationList = AssociationUtilisateurEtablissement.find(AssociationUtilisateurEtablissement.class,"utilisateur = ? and etablissement = ?",activeUser.getId().toString(),etablissementSelected.getId().toString());
        AssociationUtilisateurEtablissement association = null;
        if (associationList.size()>0) {
            association = associationList.get(0);
        }
        deleteItem(AfficherMesEtablissementsActivity.this, association, AfficherMesEtablissementsActivity.class,true);
    */
        etablissementSelected.setIsSelected(false);
        etablissementDao.update(etablissementSelected);
        recreate();
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        etablissementSelected = listMesEtablissementsBD.get(position);
        clearAllFields();
        enableFields(false);
        displayFabs();
        fillAllFields();
        displayAllFields(false);
    }
}

