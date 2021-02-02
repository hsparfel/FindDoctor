package com.pouillos.finddoctor.activities.ajouter;

import android.os.Bundle;
import android.view.Menu;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pouillos.finddoctor.R;
import com.pouillos.finddoctor.activities.AccueilActivity;
import com.pouillos.finddoctor.activities.NavDrawerActivity;
import com.pouillos.finddoctor.entities.LieuEnregistre;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;

public class AjouterLieuEnregistreActivity extends NavDrawerActivity {

    @BindView(R.id.fabSave)
    FloatingActionButton fabSave;

    LieuEnregistre lieuEnregistre;
    @BindView(R.id.textName)
    TextInputEditText textName;
    @BindView(R.id.layoutName)
    TextInputLayout layoutName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_ajouter_lieu_enregistre);

        ButterKnife.bind(this);

        this.configureToolBar();
        this.configureBottomView();

        Menu bottomNavigationViewMenu = bottomNavigationView.getMenu();

        bottomNavigationViewMenu.findItem(R.id.bottom_navigation_home).setChecked(false);
        bottomNavigationViewMenu.findItem(R.id.bottom_navigation_add_lieu_enregistre).setChecked(true);

    }


    @OnClick(R.id.fabSave)
    public void setfabSaveClick() {
        if (isFullRempli()) {
            lieuEnregistre = new LieuEnregistre();
            String nom = textName.getText().toString();
            String nomCapitale = nom.substring(0,1).toUpperCase()+nom.substring(1);
            lieuEnregistre.setNom(nomCapitale);
            lieuEnregistreDao.insert(lieuEnregistre);
            ouvrirActiviteSuivante(AjouterLieuEnregistreActivity.this, AccueilActivity.class,false);
        }
    }

    private boolean isFullRempli() {
        boolean bool = true;
        if (!isFilled(textName)) {
            bool = false;
            layoutName.setError("Obligatoire");
        }
        return bool;
    }
}
