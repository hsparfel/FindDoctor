package com.pouillos.finddoctor.activities.recherche;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.pouillos.finddoctor.R;
import androidx.annotation.RequiresApi;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.pouillos.finddoctor.activities.NavDrawerActivity;
import com.pouillos.finddoctor.activities.afficher.AfficherContactActivity;
import com.pouillos.finddoctor.entities.Contact;
import com.pouillos.finddoctor.entities.Departement;
import com.pouillos.finddoctor.entities.Etablissement;
import com.pouillos.finddoctor.entities.Profession;
import com.pouillos.finddoctor.entities.Region;
import com.pouillos.finddoctor.entities.SavoirFaire;


import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;

public class ChercherContactActivity extends NavDrawerActivity implements AdapterView.OnItemClickListener  {

    @State
    boolean booleanMedecin;
    @State
    boolean booleanAutre;
    @State
    boolean booleanSpecialite;
    @State
    boolean booleanActivite;
    @State
    boolean booleanDepartement;
    @State
    boolean booleanRegion;
    @State
    String[] listAutocompletion;

    private List<Profession> listProfessionBD;
    private List<SavoirFaire> listSavoirFaireBD;
    private List<Departement> listDepartementBD;
    private List<Region> listRegionBD;

    List<Contact> listContact;


    Departement utilisateurDepartement;

    Region utilisateurRegion;

    Contact contactSelected;

    @BindView(R.id.textRechercheIntervenant)
    AutoCompleteTextView textRechercheIntervenant;
    @BindView(R.id.layoutContact)
    TextInputLayout layoutContact;

    @BindView(R.id.chipMedecin)
    Chip chipMedecin;
    @BindView(R.id.chipAutre)
    Chip chipAutre;
    @BindView(R.id.chipSpecialite)
    Chip chipSpecialite ;
    @BindView(R.id.chipActivite)
    Chip chipActivite;
    @BindView(R.id.chipDepartement)
    Chip chipDepartement;
    @BindView(R.id.chipRegion)
    Chip chipRegion;
    @BindView(R.id.fabChercher)
    FloatingActionButton fabChercher;
    @BindView(R.id.fabRaz)
    FloatingActionButton fabRaz;
    @BindView(R.id.selectionMetier)
    AutoCompleteTextView selectionMetier;
    @BindView(R.id.listMetier)
    TextInputLayout listMetier;
    @BindView(R.id.selectionGeo)
    AutoCompleteTextView selectionGeo;
    @BindView(R.id.listGeo)
    TextInputLayout listGeo;
    @BindView(R.id.my_progressBar)
    ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_chercher_contact);
// 6 - Configure all views
        this.configureToolBar();

        this.configureBottomView();

        ButterKnife.bind(this);

        progressBar.setVisibility(View.VISIBLE);

        ChercherContactActivity.AsyncTaskRunnerBD runnerBD = new ChercherContactActivity.AsyncTaskRunnerBD();
        runnerBD.execute();

        fabChercher.hide();
        fabRaz.hide();

        setTitle(getString(R.string.text_search_contact));
    }

    public class AsyncTaskRunnerBD extends AsyncTask<Void, Integer, Void> {

        protected Void doInBackground(Void...voids) {
            publishProgress(0);

            publishProgress(10);

            //listProfessionBD = Profession.listAll(Profession.class);
            listProfessionBD = professionDao.loadAll();
            Collections.sort(listProfessionBD);
            listProfessionBD.remove(0);
            publishProgress(25);
            //listSavoirFaireBD = SavoirFaire.listAll(SavoirFaire.class);
            listSavoirFaireBD = savoirFaireDao.loadAll();
            Collections.sort(listSavoirFaireBD);
            listSavoirFaireBD.remove(0);
            publishProgress(50);
            //listDepartementBD = Departement.listAll(Departement.class);
            listDepartementBD = departementDao.loadAll();
            Collections.sort(listDepartementBD);
            publishProgress(75);
            //listRegionBD = Region.listAll(Region.class);
            listRegionBD = regionDao.loadAll();
            Collections.sort(listRegionBD);
            publishProgress(100);
            return null;
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);

            }

        @RequiresApi(api = Build.VERSION_CODES.N)
        protected void onProgressUpdate(Integer... integer) {
            progressBar.setProgress(integer[0],true);
        }
    }

    private void displayFab(FloatingActionButton fab, boolean bool) {
        if (bool) {
            fab.hide();
        } else {
            fab.show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @OnClick(R.id.chipDepartement)
    public void chipDepartementClick() {
        if (chipDepartement.isCheckable()) {
            layoutContact.setVisibility(View.GONE);
            /*if (booleanDepartement) {
                fabChercher.hide();
            } else {
                fabChercher.show();
            }*/
            displayFab(fabChercher, booleanDepartement);

            if (!booleanDepartement) {
                chipRegion.setVisibility(View.GONE);
                listGeo.setVisibility(View.VISIBLE);
            } else {
                chipRegion.setVisibility(View.VISIBLE);
                listGeo.setVisibility(View.GONE);
            }
            booleanDepartement = !booleanDepartement;

            buildDropdownMenu(listDepartementBD, ChercherContactActivity.this,selectionGeo);
            //selectionGeo.setText(utilisateurDepartement.toString(), false);
            /*List<String> listGeoString = new ArrayList<>();
            String[] listDeroulanteGeo = new String[listDepartement.size()];
            for (Departement departement : listDepartement) {
                listGeoString.add(departement.getNom() + " (" + departement.getNumero() + ")");
            }
            listGeoString.toArray(listDeroulanteGeo);
            selectionGeo.setText(utilisateurDepartement.getNom() + " (" + utilisateurDepartement.getNumero() + ")", false);
            ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item, listDeroulanteGeo);
            selectionGeo.setAdapter(adapter);*/
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @OnClick(R.id.chipRegion)
    public void chipRegionClick() {
        if (chipRegion.isCheckable()) {
            layoutContact.setVisibility(View.GONE);
            displayFab(fabChercher,booleanRegion);
           /* if (booleanRegion) {
                fabChercher.hide();
            } else {
                fabChercher.show();
            }*/
            if (!booleanRegion) {
                chipDepartement.setVisibility(View.GONE);
                listGeo.setVisibility(View.VISIBLE);
            } else {
                chipDepartement.setVisibility(View.VISIBLE);
                listGeo.setVisibility(View.GONE);
            }
            booleanRegion = !booleanRegion;

            buildDropdownMenu(listRegionBD, ChercherContactActivity.this,selectionGeo);
            //selectionGeo.setText(utilisateurRegion.toString(), false);
            /*List<String> listGeoString = new ArrayList<>();
            String[] listDeroulanteGeo = new String[listRegion.size()];
            for (Region region : listRegion) {
                listGeoString.add(region.getNom());
            }
            listGeoString.toArray(listDeroulanteGeo);
            selectionGeo.setText(utilisateurRegion.getNom(), false);
            ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item, listDeroulanteGeo);
            selectionGeo.setAdapter(adapter);*/
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @OnClick(R.id.chipSpecialite)
    public void chipSpecialiteClick() {
        if (chipSpecialite.isCheckable()) {
            layoutContact.setVisibility(View.GONE);

            if (booleanSpecialite) {
                listMetier.setVisibility(View.GONE);
            } else {
                listMetier.setVisibility(View.VISIBLE);
            }
            booleanSpecialite = !booleanSpecialite;

            buildDropdownMenu(listSavoirFaireBD, ChercherContactActivity.this,selectionMetier);
            selectionMetier.setText("Médecine Générale", false);
            /*List<String> listMetierString = new ArrayList<>();
            String[] listDeroulanteMetier = new String[listSavoirFaire.size()];
            for (SavoirFaire savoirFaire : listSavoirFaire) {
                listMetierString.add(savoirFaire.getName());
            }
            listMetierString.toArray(listDeroulanteMetier);
            selectionMetier.setText("Médecine Générale", false);
            ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item, listDeroulanteMetier);
            selectionMetier.setAdapter(adapter);*/
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @OnClick(R.id.chipActivite)
    public void chipActiviteClick() {
        if (chipActivite.isCheckable()) {
            layoutContact.setVisibility(View.GONE);
            if (booleanActivite) {
                listMetier.setVisibility(View.GONE);
            } else {
                listMetier.setVisibility(View.VISIBLE);
            }
            booleanActivite = !booleanActivite;

            buildDropdownMenu(listProfessionBD, ChercherContactActivity.this,selectionMetier);
            selectionMetier.setText("Infirmier", false);

            /*List<String> listMetierString = new ArrayList<>();
            String[] listDeroulanteMetier = new String[listProfession.size()];
            for (Profession profession : listProfession) {
                listMetierString.add(profession.getName());
            }
            listMetierString.toArray(listDeroulanteMetier);
            selectionMetier.setText("Infirmier", false);
            ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_item, listDeroulanteMetier);
            selectionMetier.setAdapter(adapter);*/

        }
    }
    @OnClick(R.id.chipAutre)
    public void chipAutreClick() {
        if (chipAutre.isCheckable()) {
            layoutContact.setVisibility(View.GONE);
            if (booleanAutre) {
                chipActivite.setVisibility(View.GONE);
                chipActivite.setChecked(false);
                chipMedecin.setVisibility(View.VISIBLE);
                chipDepartement.setVisibility(View.GONE);
                chipDepartement.setChecked(false);
                chipRegion.setVisibility(View.GONE);
                chipRegion.setChecked(false);
                fabChercher.hide();
            } else {
                chipActivite.setVisibility(View.VISIBLE);
                chipMedecin.setVisibility(View.GONE);
                chipMedecin.setChecked(false);
                chipSpecialite.setVisibility(View.GONE);
                chipSpecialite.setChecked(false);
                chipDepartement.setVisibility(View.VISIBLE);
                chipRegion.setVisibility(View.VISIBLE);
            }
            booleanDepartement = false;
            booleanRegion = false;
            booleanActivite = false;
            listMetier.setVisibility(View.GONE);
            listGeo.setVisibility(View.GONE);
            booleanAutre = !booleanAutre;
        }
    }
    @OnClick(R.id.chipMedecin)
    public void chipMedecinClick() {
        if (chipMedecin.isCheckable()) {
            layoutContact.setVisibility(View.GONE);
            if (booleanMedecin) {
                chipSpecialite.setVisibility(View.GONE);
                chipSpecialite.setChecked(false);
                chipAutre.setVisibility(View.VISIBLE);
                chipDepartement.setVisibility(View.GONE);
                chipDepartement.setChecked(false);
                chipRegion.setVisibility(View.GONE);
                chipRegion.setChecked(false);
                fabChercher.hide();
            } else {
                chipSpecialite.setVisibility(View.VISIBLE);
                chipAutre.setVisibility(View.GONE);
                chipAutre.setChecked(false);
                chipActivite.setVisibility(View.GONE);
                chipActivite.setChecked(false);
                chipDepartement.setVisibility(View.VISIBLE);
                chipRegion.setVisibility(View.VISIBLE);
            }
            booleanDepartement = false;
            booleanRegion = false;
            booleanSpecialite = false;
            listMetier.setVisibility(View.GONE);
            listGeo.setVisibility(View.GONE);
            booleanMedecin = !booleanMedecin;
        }
    }
    @OnClick(R.id.fabChercher)
    public void fabChercherClick() {

        booleanMedecin=chipMedecin.isChecked();
        booleanAutre=chipAutre.isChecked();
        booleanSpecialite=chipSpecialite.isChecked();
        booleanActivite=chipActivite.isChecked();
        booleanDepartement=chipDepartement.isChecked();
        booleanRegion=chipRegion.isChecked();
        fabRaz.show();
        fabChercher.hide();
        //layoutContact.setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.VISIBLE);
        ChercherContactActivity.AsyncTaskRunnerContact runnerContact = new ChercherContactActivity.AsyncTaskRunnerContact();
        runnerContact.execute();

    }

    public class AsyncTaskRunnerContact extends AsyncTask<Void, Integer, Void> {

        protected Void doInBackground(Void...voids) {
            publishProgress(0);
            String requete = "";

            listContact = new ArrayList<>();

            //requete += "SELECT C.* FROM CONTACT AS C LEFT JOIN ASSOCIATION_UTILISATEUR_CONTACT AS AUC ON AUC.CONTACT = C.ID ";
            //requete += "WHERE (AUC.UTILISATEUR IS NULL OR AUC.UTILISATEUR <> "+activeUser.getId()+") ";
            requete += "SELECT * from CONTACT as C WHERE ";
            if (booleanMedecin) {

                //requete += "WHERE CODE_CIVILITE <>\"\" ";
                requete += "C.SAVOIR_FAIRE_ID <> 69 ";


                chipMedecin.setCheckable(false);
            }
            if (booleanAutre) {
                //requete += "WHERE CODE_CIVILITE =\"\" ";
                requete += "C.SAVOIR_FAIRE_ID = 69 ";
                chipAutre.setCheckable(false);
            }
            if (booleanSpecialite) {
                requete += "AND C.SAVOIR_FAIRE_ID = ";
                String metier = selectionMetier.getText().toString();
                //SavoirFaire savoirFaire = SavoirFaire.find(SavoirFaire.class, "name = ?", metier).get(0);
                SavoirFaire savoirFaire = savoirFaireDao.queryRaw("where name = ?", metier).get(0);
                requete += savoirFaire.getId().toString();
                chipSpecialite.setCheckable(false);
                listMetier.setClickable(false);
            }
            if (booleanActivite) {
                requete += "AND C.PROFESSION_ID = ";
                String metier = selectionMetier.getText().toString();
                //Profession profession = Profession.find(Profession.class, "name = ?", metier).get(0);
                Profession profession = professionDao.queryRaw("where name = ?", metier).get(0);
                requete += profession.getId().toString();
                chipActivite.setCheckable(false);
                listMetier.setClickable(false);
            }
            if (booleanDepartement) {
                requete += " AND C.DEPARTEMENT_ID = ";
                String departement = selectionGeo.getText().toString();
                departement = departement.substring(0, 2);
                //Departement dep = Departement.find(Departement.class, "numero = ?", departement).get(0);
                Departement dep = departementDao.queryRaw("where numero = ?", departement).get(0);
                requete += dep.getId().toString();
                chipDepartement.setCheckable(false);
                listGeo.setClickable(false);
            }
            if (booleanRegion) {
                requete += " AND C.REGION_ID = ";
                String region = selectionGeo.getText().toString();
                //Region reg = Region.find(Region.class, "nom = ?", region).get(0);
                Region reg = regionDao.queryRaw("where nom = ?", region).get(0);
                requete += reg.getId().toString();
                chipRegion.setCheckable(false);
                listGeo.setClickable(false);
            }
            publishProgress(5);
            //listContact = Contact.findWithQuery(Contact.class, requete);

            /*Query<Contact> query = contactDao.queryBuilder().where(
                    new WhereCondition.StringCondition("_ID IN " +
                            requete)
            ).build();
            listContact = query.list();*/

            //listContact = executerSql(requete,Contact.class);

            Cursor c = daoSession.getDatabase().rawQuery(requete, null);
            try{
                if (c.moveToFirst()) {
                    do {
                        //listContact.add(c.getString(0));
                        Contact contact = new Contact();
                        contact.setId(c.getLong(0));
                        contact.setIdPP(c.getString(1));
                        contact.setCodeCivilite(c.getString(2));
                        contact.setNom(c.getString(3));
                        contact.setPrenom(c.getString(4));
                        //contact.setProfessionId(c.getLong(5));
                        contact.setProfession(professionDao.load(c.getLong(5)));
                        //contact.setSavoirFaireId(c.getLong(6));
                        contact.setSavoirFaire(savoirFaireDao.load(c.getLong(6)));
                        contact.setRaisonSocial(c.getString(7));
                        contact.setComplement(c.getString(8));
                        contact.setAdresse(c.getString(9));
                        contact.setCp(c.getString(10));
                        contact.setVille(c.getString(11));
                        contact.setTelephone(c.getString(12));
                        contact.setFax(c.getString(13));
                        contact.setEmail(c.getString(14));
                        contact.setDepartementId(c.getLong(15));
                        //contact.setRegionId(c.getLong(16));
                        contact.setRegion(regionDao.load(c.getLong(16)));
                        contact.setLatitude(c.getDouble(17));
                        contact.setLongitude(c.getDouble(18));

                        listContact.add(contact);

                    } while (c.moveToNext());
                }
            } finally {
                c.close();
            }

            publishProgress(80);

            List<String> listMedecinOfficielString = new ArrayList<>();
            listAutocompletion = new String[listContact.size()];
            for (Contact contact : listContact) {
                String affichageMedecinOfficiel = "";
                affichageMedecinOfficiel += contact.getNom() + ", " + contact.getPrenom() + " (";
                if (!contact.getSavoirFaire().getName().equalsIgnoreCase("")) {
                    affichageMedecinOfficiel += contact.getSavoirFaire().getName() + ")";
                } else {
                    affichageMedecinOfficiel += contact.getProfession().getName() + ")";
                }
                if (contact.getVille() != null) {
                    affichageMedecinOfficiel += " * "+contact.getVille();
                }

                listMedecinOfficielString.add(affichageMedecinOfficiel);
            }
            listMedecinOfficielString.toArray(listAutocompletion);

            publishProgress(100);
            return null;
        }

        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            //publishProgress(0);
            if (listContact.size() == 0) {
                Toast.makeText(ChercherContactActivity.this, R.string.text_no_matching, Toast.LENGTH_LONG).show();
                //textRechercheIntervenant.setVisibility(View.GONE);
                layoutContact.setVisibility(View.GONE);

            } else {
                layoutContact.setVisibility(View.VISIBLE);
            }

            //ArrayAdapter adapterMedecins = new ArrayAdapter(ChercherMedecinOfficielActivity.this, android.R.layout.simple_list_item_1, listAutocompletion);
            //ArrayAdapter adapterMedecins = new ArrayAdapter(ChercherMedecinOfficielActivity.this, android.R.layout.simple_list_item_2, listAutocompletion);

            ArrayAdapter<String> adapter  = new ArrayAdapter<String>(ChercherContactActivity.this, R.layout.list_item_two_lines, R.id.item, listAutocompletion);







            textRechercheIntervenant.setAdapter(adapter);
            textRechercheIntervenant.setOnItemClickListener(ChercherContactActivity.this);
            textRechercheIntervenant.setThreshold(1);

            layoutContact.setVisibility(View.VISIBLE);


            //Masquer & raz tout le reste
            //if (listMedecinOfficiel.size()!=0) {
                chipMedecin.setVisibility(View.GONE);
                chipSpecialite.setVisibility(View.GONE);
                chipActivite.setVisibility(View.GONE);
                chipDepartement.setVisibility(View.GONE);
                chipRegion.setVisibility(View.GONE);

                listMetier.setVisibility(View.GONE);
                listGeo.setVisibility(View.GONE);
            //}
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        protected void onProgressUpdate(Integer... integer) {
            progressBar.setProgress(integer[0],true);
        }
    }

    @OnClick(R.id.fabRaz)
    public void fabRazClick() {
        layoutContact.setVisibility(View.GONE);
        fabChercher.hide();
        fabRaz.hide();
        textRechercheIntervenant.setFocusable(true);
        //Masquer & raz tout le reste
        booleanMedecin = false;
        chipMedecin.setCheckable(true);
        chipMedecin.setChecked(false);
        chipMedecin.setVisibility(View.VISIBLE);
        booleanAutre = false;
        chipAutre.setCheckable(true);
        chipAutre.setChecked(false);
        chipAutre.setVisibility(View.VISIBLE);
        booleanSpecialite = false;
        chipSpecialite.setCheckable(true);
        chipSpecialite.setChecked(false);
        chipSpecialite.setVisibility(View.GONE);
        booleanActivite = false;
        chipActivite.setCheckable(true);
        chipActivite.setChecked(false);
        chipActivite.setVisibility(View.GONE);
        booleanDepartement = false;
        chipDepartement.setCheckable(true);
        chipDepartement.setChecked(false);
        chipDepartement.setVisibility(View.GONE);
        booleanRegion = false;
        chipRegion.setCheckable(true);
        chipRegion.setChecked(false);
        chipRegion.setVisibility(View.GONE);
        listMetier.setVisibility(View.GONE);
        listGeo.setVisibility(View.GONE);
        textRechercheIntervenant.setText(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        // fetch the user selected value
        String item = parent.getItemAtPosition(position).toString();

        Toast.makeText(ChercherContactActivity.this, "Selected : \t" + item, Toast.LENGTH_SHORT).show();

        textRechercheIntervenant.setFocusable(false);
        int positionVirgule = textRechercheIntervenant.getText().toString().indexOf(",");
        int positionParentheseOuverte = textRechercheIntervenant.getText().toString().indexOf("(");
        int positionParentheseFermee = textRechercheIntervenant.getText().toString().indexOf(")");
        int positionEtoile = textRechercheIntervenant.getText().toString().indexOf("*");

        String nom = textRechercheIntervenant.getText().toString().substring(0, positionVirgule);
        String prenom = textRechercheIntervenant.getText().toString().substring(positionVirgule + 2, positionParentheseOuverte - 1);
        String metier = textRechercheIntervenant.getText().toString().substring(positionParentheseOuverte + 1, positionParentheseFermee);
        String ville="";
        if (positionEtoile != -1) {
            ville = textRechercheIntervenant.getText().toString().substring(positionEtoile + 2);
        }
        //String ville = textRechercheIntervenant.getText().toString().substring(positionEtoile + 2);

        if (booleanAutre) {
            //Profession profession = Profession.find(Profession.class, "name = ?", metier).get(0);
            Profession profession = professionDao.queryRaw("where name = ?", metier).get(0);

            if (ville.equalsIgnoreCase("")) {
               // contactSelected = Contact.find(Contact.class, "nom = ? and prenom = ? and profession = ?", nom, prenom, profession.getId().toString()).get(0);
                contactSelected = contactDao.queryRaw("where nom = ? and prenom = ? and profession_id = ?", nom, prenom, profession.getId().toString()).get(0);
            } else {
                //contactSelected = Contact.find(Contact.class, "nom = ? and prenom = ? and profession = ? and ville = ?", nom, prenom, profession.getId().toString(), ville).get(0);
                contactSelected = contactDao.queryRaw("where nom = ? and prenom = ? and profession_id = ? and ville = ?", nom, prenom, profession.getId().toString(), ville).get(0);
            }

            Toast.makeText(ChercherContactActivity.this, "Selected Item is: \t" + contactSelected.getPrenom() + contactSelected.getNom(), Toast.LENGTH_LONG).show();
        } else if (booleanMedecin) {
            //SavoirFaire savoirFaire = SavoirFaire.find(SavoirFaire.class, "name = ?", metier).get(0);
            SavoirFaire savoirFaire = savoirFaireDao.queryRaw("where name = ?", metier).get(0);

            if (ville.equalsIgnoreCase("")) {
                //contactSelected = Contact.find(Contact.class, "nom = ? and prenom = ? and savoir_faire = ?", nom, prenom, savoirFaire.getId().toString()).get(0);
                contactSelected = contactDao.queryRaw("where nom = ? and prenom = ? and savoir_faire_id = ?", nom, prenom, savoirFaire.getId().toString()).get(0);
            } else {
                //contactSelected = Contact.find(Contact.class, "nom = ? and prenom = ? and savoir_faire = ? and ville = ?", nom, prenom, savoirFaire.getId().toString(), ville).get(0);
                contactSelected = contactDao.queryRaw("where nom = ? and prenom = ? and savoir_faire_id = ? and ville = ?", nom, prenom, savoirFaire.getId().toString(), ville).get(0);

            }

            //mContactSelectionne = Contact.find(Contact.class, "nom = ? and prenom = ? and savoir_faire = ? and ville = ?", nom, prenom, savoirFaire.getId().toString(), ville).get(0);
            Toast.makeText(ChercherContactActivity.this, "Selected Item is: \t" + contactSelected.getPrenom() + contactSelected.getNom(), Toast.LENGTH_LONG).show();

        }

        if (contactSelected != null) {
            /*AssociationUtilisateurContact associationUtilisateurContact = new AssociationUtilisateurContact(activeUser, contactSelected);
            if (!associationUtilisateurContact.isExistante()) {
                associationUtilisateurContact.save();
            }
            Toast.makeText(ChercherContactActivity.this, activeUser.getName()+" & "+ contactSelected.getNom()+" ont été associés", Toast.LENGTH_LONG).show();
            */
            ouvrirActiviteSuivante(ChercherContactActivity.this, AfficherContactActivity.class,"contactId", contactSelected.getId(),true);
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
