package com.pouillos.finddoctor.activities.afficher;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pouillos.finddoctor.activities.NavDrawerActivity;
import com.pouillos.finddoctor.entities.Etablissement;
import com.pouillos.finddoctor.R;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;

public class AfficherEtablissementActivity extends NavDrawerActivity {

    Etablissement etablissementSelected;

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

    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_afficher_etablissement);

        this.configureToolBar();
        this.configureBottomView();

        ButterKnife.bind(this);

        displayFabs();
        hideAllFields();
        traiterIntent();
        setTitle("Etablissement");
    }

    public void traiterIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra("etablissementId")) {
            Long etablissementId = intent.getLongExtra("etablissementId", 0);
            etablissementSelected = etablissementDao.load(etablissementId);
            fillAllFields();
            displayAllFields(false);
            displayFabs();
        }
        enableFields(false);
    }

    public void displayFabs() {
        if (etablissementSelected == null) {
            fabGoogleMap.hide();
            fabWaze.hide();
            fabSave.hide();
        } else {
            fabSave.show();
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
        etablissementSelected.setIsSelected(true);
        etablissementDao.update(etablissementSelected);
        fabSave.hide();
    }

}

