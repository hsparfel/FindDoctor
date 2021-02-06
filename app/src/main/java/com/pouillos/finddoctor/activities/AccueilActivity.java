package com.pouillos.finddoctor.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.facebook.stetho.Stetho;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pouillos.finddoctor.R;

import com.pouillos.finddoctor.entities.Contact;
import com.pouillos.finddoctor.entities.ContactLight;
import com.pouillos.finddoctor.entities.Departement;
import com.pouillos.finddoctor.entities.Etablissement;
import com.pouillos.finddoctor.entities.ImportContact;
import com.pouillos.finddoctor.entities.ImportEtablissement;
import com.pouillos.finddoctor.entities.Lieu;
import com.pouillos.finddoctor.entities.LieuEnregistre;
import com.pouillos.finddoctor.entities.Profession;
import com.pouillos.finddoctor.entities.Region;
import com.pouillos.finddoctor.entities.SavoirFaire;
import com.pouillos.finddoctor.entities.TypeEtablissement;
import com.pouillos.finddoctor.utils.DateUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;

public class AccueilActivity extends NavDrawerActivity {

    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.textNbContact)
    TextView textNbContact;
    @BindView(R.id.textNbEtablissement)
    TextView textNbEtablissement;

    @BindView(R.id.my_progressBar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setContentView(R.layout.activity_accueil);
        Stetho.initializeWithDefaults(this);

        this.configureToolBar();
        this.configureBottomView();

        ButterKnife.bind(this);

        textView.setText(DateUtils.ecrireDateLettre(new Date()));

        progressBar.setVisibility(View.VISIBLE);

        AccueilActivity.AsyncTaskRunnerBD runnerBD = new AccueilActivity.AsyncTaskRunnerBD();
        runnerBD.execute();
    }

    public void razDb(View view) {
        //decommenter les tables à raz

        //contactDao.deleteAll();
      //  contactLightDao.deleteAll();
        //   importContactDao.deleteAll();


     //  etablissementDao.deleteAll();
      //  importEtablissementDao.deleteAll();

        //moins critique
        professionDao.deleteAll();
        regionDao.deleteAll();
        savoirFaireDao.deleteAll();
        typeEtablissementDao.deleteAll();
        departementDao.deleteAll();
    }

    public void importContact(View view) {
        progressBar.setVisibility(View.VISIBLE);
        //remplirMedicamentOfficielBD();
        progressBar.setProgress(0);
        AsyncTaskRunnerContact runner = new AsyncTaskRunnerContact(this);
        runner.execute();
    }

    public void importEtablissement(View view) {
        progressBar.setVisibility(View.VISIBLE);
        //remplirMedicamentOfficielBD();
        progressBar.setProgress(0);
        AsyncTaskRunnerEtablissement runner = new AsyncTaskRunnerEtablissement(this);
        runner.execute();
    }

    private class AsyncTaskRunnerEtablissement extends AsyncTask<Void, Integer, Void> {

        private Context context;
        public AsyncTaskRunnerEtablissement(Context context) {
            this.context=context;
        }

        protected Void doInBackground(Void...voids) {

            InputStream is = null;
            BufferedReader reader = null;

            //List<TypeEtablissement> listTypeEtablissement = TypeEtablissement.listAll(TypeEtablissement.class);
            List<TypeEtablissement> listTypeEtablissement = typeEtablissementDao.loadAll();
            Map<String, TypeEtablissement> mapTypeEtablissement = new HashMap<>();
            for (TypeEtablissement typeEtablissement : listTypeEtablissement) {
                mapTypeEtablissement.put(typeEtablissement.getName(), typeEtablissement);
            }
           //List<Departement> listDepartement = Departement.listAll(Departement.class);
            List<Departement> listDepartement = departementDao.loadAll();
            Map<String, Departement> mapDepartement = new HashMap<>();
            for (Departement departement : listDepartement) {
                mapDepartement.put(departement.getNumero(), departement);
            }

            //List<ImportEtablissement> listImportEtablissement = ImportEtablissement.find(ImportEtablissement.class,"import_completed = ?","0");
            //List<ImportEtablissement> listImportEtablissement = ImportEtablissement.listAll(ImportEtablissement.class);
            List<ImportEtablissement> listImportEtablissement = importEtablissementDao.queryRaw("where import_completed = ?","0");


            int nbImportEffectue =0;
            int nbImportIgnore = 0;
            int readerCount=0;
            int nbLigneLue=0;
            //ImportEtablissement current = listImportEtablissement.get(0);
            for (ImportEtablissement current : listImportEtablissement) {
                nbImportEffectue =0;
                nbImportIgnore = 0;
                //readerCount=0;
                nbLigneLue=0;
                //if (current.getDateDebut() == null) {
                    if (current.getDateDebut().equalsIgnoreCase("")) {
                    current.setDateDebut(DateUtils.ecrireDateHeure(new Date()));
                }
                if (current.getNbLigneLue() != 0) {
                    nbLigneLue = current.getNbLigneLue();
                }
                if (current.getNbImportEffectue() != 0) {
                    nbImportEffectue = current.getNbImportEffectue();
                }
                if (current.getNbImportIgnore() != 0) {
                    nbImportIgnore = current.getNbImportIgnore();
                }

                //current.setDateFin("");
                //current.setNbImportEffectue(nbImportEffectue);
                ///current.setNbImportIgnore(nbImportIgnore);
                // nbImportEffectue = 0;
                // nbImportIgnore = 0;

                try {
                    is = getAssets().open(current.getPath());
                    //reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    reader = new BufferedReader(new InputStreamReader(is,"windows-1252"));
                    String line = null;

                    int readerSize = 95500;
                    readerCount = 0;
                    int compteur = 0;
                    publishProgress(compteur);
                    while ((line = reader.readLine()) != null) {

                        if (readerCount<nbLigneLue) {
                            readerCount ++;
                            continue;
                        }
                        readerCount ++;
                        compteur = readerCount*100/readerSize;
                        //compteur ++;
                        publishProgress(compteur);
                        final String SEPARATEUR = "\\;";
                        String lineSplitted[] = line.split(SEPARATEUR);

                        Etablissement etablissement = new Etablissement();
                        etablissement.setNumeroFinessET(lineSplitted[0]);
                        etablissement.setRaisonSocial(lineSplitted[1]);
                        etablissement.setAdresse(lineSplitted[2]);
                        if (lineSplitted[4].length() == 4) {
                            etablissement.setCp("0"+lineSplitted[4]);
                        } else {
                            etablissement.setCp(lineSplitted[4]);
                        }
                     /*   if (lineSplitted[5].length()>6 && lineSplitted[5].substring(lineSplitted[5].length()-5).equalsIgnoreCase("CEDEX")){
                            etablissement.setVille(lineSplitted[5].substring(0,lineSplitted[5].length()-6));
                        } else {
                            etablissement.setVille(lineSplitted[5]);
                        }*/
                        //todo  revoir avec contains pour tous les cedex
                        if (lineSplitted[5].contains("CEDEX")) {
                            int index = lineSplitted[5].indexOf("CEDEX");
                            etablissement.setVille(lineSplitted[5].substring(0,index-1));
                        } else {
                            etablissement.setVille(lineSplitted[5]);
                        }


                        if (lineSplitted[5].length()>6 && lineSplitted[5].substring(lineSplitted[5].length()-5).equalsIgnoreCase("CEDEX")){
                            etablissement.setVille(lineSplitted[5].substring(0,lineSplitted[5].length()-6));
                        } else {
                            etablissement.setVille(lineSplitted[5]);
                        }
////////////////////////
                        if (lineSplitted[6].length() == 9) {
                            etablissement.setTelephone("0"+lineSplitted[6]);
                        }
                        if (lineSplitted[7].length() == 9) {
                            etablissement.setFax("0"+lineSplitted[7]);
                        }

                        etablissement.setTypeEtablissement(mapTypeEtablissement.get(lineSplitted[8]));

                        if (lineSplitted[3].equalsIgnoreCase("2A") || lineSplitted[3].equalsIgnoreCase("2B")) {
                            etablissement.setDepartement(mapDepartement.get("20"));
                        } else if (lineSplitted[3].equalsIgnoreCase("9A") || lineSplitted[3].equalsIgnoreCase("9B") || lineSplitted[3].equalsIgnoreCase("9C") || lineSplitted[3].equalsIgnoreCase("9D") || lineSplitted[3].equalsIgnoreCase("9E") || lineSplitted[3].equalsIgnoreCase("9F")) {
                            etablissement.setDepartement(mapDepartement.get("97"));
                        } else if (lineSplitted[3].length()==1) {
                            etablissement.setDepartement(mapDepartement.get("0"+lineSplitted[3]));
                        } else {
                            etablissement.setDepartement(mapDepartement.get(lineSplitted[3]));
                        }
                        etablissement.setRegion(etablissement.getDepartement().getRegion());

                        if (etablissement.getAdresse() != null && etablissement.getCp() != null && etablissement.getVille() != null) {
                            etablissement.enregisterCoordonnees(context);
                        }
                        //Log.i("enregistre","medecin new: "+lineSplitted[2]);
                        //etablissement.save();
                        etablissementDao.insert(etablissement);
                        nbLigneLue++;
                        nbImportEffectue++;
                        current.setNbLigneLue(nbLigneLue);
                        current.setNbImportEffectue(nbImportEffectue);
                        importEtablissementDao.update(current);
                        //current.save();
                    }
                } catch (final Exception e) {
                    nbImportIgnore++;
                    current.setNbImportIgnore(nbImportIgnore);
                    //current.save();
                    importEtablissementDao.update(current);
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException ignored) {
                        }
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
                current.setDateFin(DateUtils.ecrireDateHeure(new Date()));
                current.setNbImportIgnore(nbImportIgnore);
                current.setNbImportEffectue(nbImportEffectue);
                current.setImportCompleted(true);
                importEtablissementDao.update(current);
                //current.save();
                publishProgress(100);
                //a voir si ça passe
                //Toast.makeText(MainActivity.this, "Import de " + current.getPath() + " fini", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
           // afficherNbEtablissementEnregistre();
            //Toast.makeText(ImportEtablissementActivity.this, "IMPORT TOTAL FINI", Toast.LENGTH_LONG).show();
            Snackbar.make(textView, "IMPORT Etablissement FINI", Snackbar.LENGTH_SHORT).setAnchorView(textView).show();
            Long count = etablissementDao.count();
            textNbEtablissement.setText("nb d'etablissements = "+count);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        protected void onProgressUpdate(Integer... integer) {
            progressBar.setProgress(integer[0],true);
        }
    }


    private class AsyncTaskRunnerContact extends AsyncTask<Void, Integer, Void> {

        private Context context;
        public AsyncTaskRunnerContact(Context context) {
            this.context=context;
        }

        protected Void doInBackground(Void...voids) {

            InputStream is = null;
            BufferedReader reader = null;

            //List<Profession> listProfession = Profession.listAll(Profession.class);
            List<Profession> listProfession = professionDao.loadAll();
            Map<String, Profession> mapProfession = new HashMap<>();
            for (Profession profession : listProfession) {
                mapProfession.put(profession.getName(), profession);
            }

            //List<SavoirFaire> listSavoirFaire = SavoirFaire.listAll(SavoirFaire.class);
            List<SavoirFaire> listSavoirFaire = savoirFaireDao.loadAll();
            Map<String, SavoirFaire> mapSavoirFaire = new HashMap<>();
            for (SavoirFaire savoirFaire : listSavoirFaire) {
                mapSavoirFaire.put(savoirFaire.getName(), savoirFaire);
            }
//RAZ d'import Contact
            //List<ImportContact> listImportContact = ImportContact.find(ImportContact.class,"import_completed = ?","0");
            List<ImportContact> listImportContact = importContactDao.queryRaw("where import_completed = ?","0");

            int nbImportEffectue =0;
            int nbImportIgnore = 0;
            int readerCount=0;
            int nbLigneLue=0;

            for (ImportContact current : listImportContact) {
                nbImportEffectue =0;
                nbImportIgnore = 0;
                //readerCount=0;
                nbLigneLue=0;
                //if (current.getDateDebut() == null) {
                    if (current.getDateDebut().equalsIgnoreCase("")) {
                    current.setDateDebut(DateUtils.ecrireDateHeure(new Date()));
                }
                if (current.getNbLigneLue() != 0) {
                    nbLigneLue = current.getNbLigneLue();
                }
                if (current.getNbImportEffectue() != 0) {
                    nbImportEffectue = current.getNbImportEffectue();
                }
                if (current.getNbImportIgnore() != 0) {
                    nbImportIgnore = current.getNbImportIgnore();
                }

                try {
                    is = getAssets().open(current.getPath());
                    reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    String line = null;


                    int readerSize = 5000;
                    readerCount = 0;
                    int compteur = 0;
                    publishProgress(compteur);
                    while ((line = reader.readLine()) != null) {
                        if (readerCount<nbLigneLue) {
                            readerCount ++;
                            continue;
                        }
                        readerCount ++;
                        compteur = readerCount*100/readerSize;
                        publishProgress(compteur);
                        final String SEPARATEUR = "\\|";
                        String lineSplitted[] = line.split(SEPARATEUR);

                        if (lineSplitted[1].equals("Identifiant PP")) {
                            nbImportIgnore++;
                            current.setNbImportIgnore(nbImportIgnore);
                            nbLigneLue++;
                            current.setNbLigneLue(nbLigneLue);
                            //current.save();
                            importContactDao.update(current);
                            continue;
                        }

                        Contact contact = new Contact();
                        ContactLight contactLight = new ContactLight();
                        contact.setIdPP(lineSplitted[2]);
                        contact.setCodeCivilite(lineSplitted[3]);
                        contact.setNom(lineSplitted[7].toUpperCase());
                        if (lineSplitted[8].length()>1) {
                            String prenom = lineSplitted[8].substring(0,1).toUpperCase()+lineSplitted[8].substring(1,lineSplitted[8].length()-1).toLowerCase();
                        }
                        contact.setPrenom(lineSplitted[8]);
                        contact.setProfession(mapProfession.get(lineSplitted[10]));

                        /*if (lineSplitted[16].equals("Qualifié en Médecine Générale") || lineSplitted[16].equals("Spécialiste en Médecine Générale")) {
                            contact.setSavoirFaire(mapSavoirFaire.get("Médecine Générale"));
                        } else {
                            contact.setSavoirFaire(mapSavoirFaire.get(lineSplitted[16]));
                        }*/

                        if (lineSplitted.length>16) {
                            if (lineSplitted[16].equals("Qualifié en Médecine Générale") || lineSplitted[16].equals("Spécialiste en Médecine Générale")) {
                                contact.setSavoirFaire(mapSavoirFaire.get("Médecine Générale"));
                            } else {
                                contact.setSavoirFaire(mapSavoirFaire.get(lineSplitted[16]));
                            }
                        }


                        if (lineSplitted.length>24) {
                            contact.setRaisonSocial(lineSplitted[24]);
                        }
                        if (lineSplitted.length>26) {
                            contact.setComplement(lineSplitted[26]);
                            if (contact.getComplement().equalsIgnoreCase(contact.getRaisonSocial())) {
                                contact.setComplement(null);
                            }
                        }
                        String adresse = "";
                        if ((lineSplitted.length>28) && (!lineSplitted[28].isEmpty())){
                            adresse = lineSplitted[28]+" ";
                        }

                        if ((lineSplitted.length>31) && (!lineSplitted[31].isEmpty())){
                            adresse += lineSplitted[31]+" ";
                        }
                        if ((lineSplitted.length>32) && (!lineSplitted[32].isEmpty())){
                            adresse += lineSplitted[32];
                        }
                        contact.setAdresse(adresse.toUpperCase());

                        /*if (lineSplitted.length>34 && !lineSplitted[34].isEmpty())  {

                            contact.setCp(lineSplitted[34].substring(0,5));
                            contact.setVille(lineSplitted[34].substring(6));

                        } else {
                            contact.setCp("");
                        }*/
                        if (lineSplitted.length>34 && !lineSplitted[34].isEmpty())  {
                            if (lineSplitted.length>39 && lineSplitted[39].equalsIgnoreCase("Israel")) {
//plutot different de france à voir si necessaire
                            } else {
                                contact.setCp(lineSplitted[34].substring(0,5));
                                contact.setVille(lineSplitted[34].substring(6));
                                //todo modif ici si je veux supprimer cedex, cedex 1, cedex 20 etc ...
                            }



                        } else {
                            contact.setCp("");
                        }
                        contact.setDepartement(findDepartement(contact.getCp()));
                        contact.setRegion(contact.getDepartement().getRegion());
                        if (lineSplitted.length>40 && !lineSplitted[40].isEmpty())  {
                            lineSplitted[40] = lineSplitted[40].replace(" ", "");
                            lineSplitted[40] = lineSplitted[40].replace(".", "");
                            if (lineSplitted[40].length() == 9) {
                                contact.setTelephone("0" + lineSplitted[40]);
                            } else if (lineSplitted[40].length() == 10) {
                                contact.setTelephone(lineSplitted[40]);
                            }
                        }
                        if (lineSplitted.length>42 && !lineSplitted[42].isEmpty())  {
                            lineSplitted[42] = lineSplitted[42].replace(" ", "");
                            lineSplitted[42] = lineSplitted[42].replace(".", "");
                            if (lineSplitted[42].length() == 9) {
                                contact.setFax("0" + lineSplitted[42]);
                            } else if (lineSplitted[42].length() == 10) {
                                contact.setFax(lineSplitted[42]);
                            }
                        }
                        if (lineSplitted.length>43 && !lineSplitted[43].isEmpty())  {
                            contact.setEmail(lineSplitted[43]);
                        }

                       // List<ContactLight> listContactLight = ContactLight.find(ContactLight.class, "id_pp = ?",lineSplitted[2]);
                        List<ContactLight> listContactLight = contactLightDao.queryRaw("where id_pp = ?",lineSplitted[2]);

                        if (listContactLight.size()>0) {

                            boolean bool = false;
                            for (ContactLight currentContactLight : listContactLight){
                                if (comparer(currentContactLight, contact)){
                                    Log.i("existant","medecin deja cree: "+lineSplitted[2]);
                                    bool = true;
                                    continue;
                                }
                            }
                            if (bool) {
                                continue;
                            }
                        }
                        if (contact.getAdresse() != null && contact.getCp() != null && contact.getVille() != null) {
                            contact.enregisterCoordonnees(context);
                        }
                        Log.i("enregistre","medecin new: "+lineSplitted[2]);
                        //contact.save();
                        contactLight.setIdPP(contact.getIdPP());
                        contactLight.setAdresse(contact.getAdresse());
                        contactLight.setCodeCivilite(contact.getCodeCivilite());
                        contactLight.setComplement(contact.getComplement());
                        contactLight.setCp(contact.getCp());
                        contactLight.setFax(contact.getFax());
                        contactLight.setEmail(contact.getEmail());
                        contactLight.setLatitude(contact.getLatitude());
                        contactLight.setLongitude(contact.getLongitude());
                        contactLight.setNom(contact.getNom());
                        contactLight.setPrenom(contact.getPrenom());
                        contactLight.setRaisonSocial(contact.getRaisonSocial());
                        contactLight.setTelephone(contact.getTelephone());
                        contactLight.setVille(contact.getVille());

                        contactDao.insert(contact);
                        contactLightDao.insert(contactLight);
                        nbLigneLue++;
                        nbImportEffectue++;
                        current.setNbLigneLue(nbLigneLue);
                        current.setNbImportEffectue(nbImportEffectue);
                        importContactDao.update(current);
                    }
                } catch (final Exception e) {
                    nbImportIgnore++;
                    current.setNbImportIgnore(nbImportIgnore);
                    importContactDao.update(current);
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException ignored) {
                        }
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
                current.setDateFin(DateUtils.ecrireDateHeure(new Date()));
                current.setNbImportIgnore(nbImportIgnore);
                current.setNbImportEffectue(nbImportEffectue);
                current.setImportCompleted(true);
                //current.save();
                importContactDao.update(current);
                publishProgress(100);
                //a voir si ça passe
                //Toast.makeText(MainActivity.this, "Import de " + current.getPath() + " fini", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            //Toast.makeText(MainActivity.this, "IMPORT TOTAL FINI", Toast.LENGTH_LONG).show();
            Snackbar.make(textView, "IMPORT Contact FINI", Snackbar.LENGTH_SHORT).setAnchorView(textView).show();
            Long count = contactDao.count();
            textNbContact.setText("nb de contacts = "+count);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        protected void onProgressUpdate(Integer... integer) {
            progressBar.setProgress(integer[0],true);
        }
    }

    private class AsyncTaskRunnerBD extends AsyncTask<Void, Integer, Void> {

        protected Void doInBackground(Void...voids) {
            publishProgress(0);


            publishProgress(10);
            remplirImportEtablissementBD();
            publishProgress(20);
            remplirProfessionBD();
            publishProgress(30);
            remplirSavoirFaireBD();
            publishProgress(40);
            remplirRegionBD();
            publishProgress(50);
            remplirDepartementBD();
            publishProgress(70);
            remplirImportContactBD();
            publishProgress(90);
            remplirTypeEtablissementBD();
            publishProgress(100);
            return null;
        }

        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            //Toast.makeText(AccueilActivity.this, R.string.text_DB_created, Toast.LENGTH_LONG).show();
            Snackbar.make(textView, "DB Created", Snackbar.LENGTH_SHORT).setAnchorView(textView).show();
            Long count = contactDao.count();
            textNbContact.setText("nb de contacts = "+count);
            count = etablissementDao.count();
            textNbEtablissement.setText("nb d'etablissements = "+count);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        protected void onProgressUpdate(Integer... integer) {
            progressBar.setProgress(integer[0],true);
        }
    }

    public void remplirImportEtablissementBD() {
        //Long count = ImportEtablissement.count(ImportEtablissement.class);
        Long count = importEtablissementDao.count();
        if (count == 0) {
            importEtablissementDao.insert(new ImportEtablissement(0l,"etablissement.txt", false,"","",0,0,0));
        }
    }

    public void remplirImportContactBD() {
       // Long count = ImportContact.count(ImportContact.class);
        Long count = importContactDao.count();
        if (count == 0) {
            importContactDao.insert(new ImportContact(0l,"PS_LibreAcces_Personne_activite_202102020955_0.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(1l,"PS_LibreAcces_Personne_activite_202102020955_1.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(2l,"PS_LibreAcces_Personne_activite_202102020955_2.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(3l,"PS_LibreAcces_Personne_activite_202102020955_3.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(4l,"PS_LibreAcces_Personne_activite_202102020955_4.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(5l,"PS_LibreAcces_Personne_activite_202102020955_5.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(6l,"PS_LibreAcces_Personne_activite_202102020955_6.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(7l,"PS_LibreAcces_Personne_activite_202102020955_7.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(8l,"PS_LibreAcces_Personne_activite_202102020955_8.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(9l,"PS_LibreAcces_Personne_activite_202102020955_9.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(10l,"PS_LibreAcces_Personne_activite_202102020955_10.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(11l,"PS_LibreAcces_Personne_activite_202102020955_11.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(12l,"PS_LibreAcces_Personne_activite_202102020955_12.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(13l,"PS_LibreAcces_Personne_activite_202102020955_13.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(14l,"PS_LibreAcces_Personne_activite_202102020955_14.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(15l,"PS_LibreAcces_Personne_activite_202102020955_15.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(16l,"PS_LibreAcces_Personne_activite_202102020955_16.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(17l,"PS_LibreAcces_Personne_activite_202102020955_17.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(18l,"PS_LibreAcces_Personne_activite_202102020955_18.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(19l,"PS_LibreAcces_Personne_activite_202102020955_19.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(20l,"PS_LibreAcces_Personne_activite_202102020955_20.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(21l,"PS_LibreAcces_Personne_activite_202102020955_21.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(22l,"PS_LibreAcces_Personne_activite_202102020955_22.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(23l,"PS_LibreAcces_Personne_activite_202102020955_23.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(24l,"PS_LibreAcces_Personne_activite_202102020955_24.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(25l,"PS_LibreAcces_Personne_activite_202102020955_25.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(26l,"PS_LibreAcces_Personne_activite_202102020955_26.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(27l,"PS_LibreAcces_Personne_activite_202102020955_27.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(28l,"PS_LibreAcces_Personne_activite_202102020955_28.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(29l,"PS_LibreAcces_Personne_activite_202102020955_29.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(30l,"PS_LibreAcces_Personne_activite_202102020955_30.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(31l,"PS_LibreAcces_Personne_activite_202102020955_31.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(32l,"PS_LibreAcces_Personne_activite_202102020955_32.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(33l,"PS_LibreAcces_Personne_activite_202102020955_33.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(34l,"PS_LibreAcces_Personne_activite_202102020955_34.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(35l,"PS_LibreAcces_Personne_activite_202102020955_35.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(36l,"PS_LibreAcces_Personne_activite_202102020955_36.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(37l,"PS_LibreAcces_Personne_activite_202102020955_37.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(38l,"PS_LibreAcces_Personne_activite_202102020955_38.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(39l,"PS_LibreAcces_Personne_activite_202102020955_39.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(40l,"PS_LibreAcces_Personne_activite_202102020955_40.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(41l,"PS_LibreAcces_Personne_activite_202102020955_41.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(42l,"PS_LibreAcces_Personne_activite_202102020955_42.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(43l,"PS_LibreAcces_Personne_activite_202102020955_43.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(44l,"PS_LibreAcces_Personne_activite_202102020955_44.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(45l,"PS_LibreAcces_Personne_activite_202102020955_45.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(46l,"PS_LibreAcces_Personne_activite_202102020955_46.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(47l,"PS_LibreAcces_Personne_activite_202102020955_47.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(48l,"PS_LibreAcces_Personne_activite_202102020955_48.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(49l,"PS_LibreAcces_Personne_activite_202102020955_49.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(50l,"PS_LibreAcces_Personne_activite_202102020955_50.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(51l,"PS_LibreAcces_Personne_activite_202102020955_51.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(52l,"PS_LibreAcces_Personne_activite_202102020955_52.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(53l,"PS_LibreAcces_Personne_activite_202102020955_53.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(54l,"PS_LibreAcces_Personne_activite_202102020955_54.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(55l,"PS_LibreAcces_Personne_activite_202102020955_55.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(56l,"PS_LibreAcces_Personne_activite_202102020955_56.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(57l,"PS_LibreAcces_Personne_activite_202102020955_57.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(58l,"PS_LibreAcces_Personne_activite_202102020955_58.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(59l,"PS_LibreAcces_Personne_activite_202102020955_59.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(60l,"PS_LibreAcces_Personne_activite_202102020955_60.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(61l,"PS_LibreAcces_Personne_activite_202102020955_61.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(62l,"PS_LibreAcces_Personne_activite_202102020955_62.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(63l,"PS_LibreAcces_Personne_activite_202102020955_63.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(64l,"PS_LibreAcces_Personne_activite_202102020955_64.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(65l,"PS_LibreAcces_Personne_activite_202102020955_65.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(66l,"PS_LibreAcces_Personne_activite_202102020955_66.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(67l,"PS_LibreAcces_Personne_activite_202102020955_67.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(68l,"PS_LibreAcces_Personne_activite_202102020955_68.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(69l,"PS_LibreAcces_Personne_activite_202102020955_69.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(70l,"PS_LibreAcces_Personne_activite_202102020955_70.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(71l,"PS_LibreAcces_Personne_activite_202102020955_71.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(72l,"PS_LibreAcces_Personne_activite_202102020955_72.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(73l,"PS_LibreAcces_Personne_activite_202102020955_73.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(74l,"PS_LibreAcces_Personne_activite_202102020955_74.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(75l,"PS_LibreAcces_Personne_activite_202102020955_75.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(76l,"PS_LibreAcces_Personne_activite_202102020955_76.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(77l,"PS_LibreAcces_Personne_activite_202102020955_77.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(78l,"PS_LibreAcces_Personne_activite_202102020955_78.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(79l,"PS_LibreAcces_Personne_activite_202102020955_79.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(80l,"PS_LibreAcces_Personne_activite_202102020955_80.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(81l,"PS_LibreAcces_Personne_activite_202102020955_81.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(82l,"PS_LibreAcces_Personne_activite_202102020955_82.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(83l,"PS_LibreAcces_Personne_activite_202102020955_83.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(84l,"PS_LibreAcces_Personne_activite_202102020955_84.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(85l,"PS_LibreAcces_Personne_activite_202102020955_85.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(86l,"PS_LibreAcces_Personne_activite_202102020955_86.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(87l,"PS_LibreAcces_Personne_activite_202102020955_87.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(88l,"PS_LibreAcces_Personne_activite_202102020955_88.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(89l,"PS_LibreAcces_Personne_activite_202102020955_89.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(90l,"PS_LibreAcces_Personne_activite_202102020955_90.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(91l,"PS_LibreAcces_Personne_activite_202102020955_91.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(92l,"PS_LibreAcces_Personne_activite_202102020955_92.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(93l,"PS_LibreAcces_Personne_activite_202102020955_93.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(94l,"PS_LibreAcces_Personne_activite_202102020955_94.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(95l,"PS_LibreAcces_Personne_activite_202102020955_95.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(96l,"PS_LibreAcces_Personne_activite_202102020955_96.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(97l,"PS_LibreAcces_Personne_activite_202102020955_97.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(98l,"PS_LibreAcces_Personne_activite_202102020955_98.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(99l,"PS_LibreAcces_Personne_activite_202102020955_99.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(100l,"PS_LibreAcces_Personne_activite_202102020955_100.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(101l,"PS_LibreAcces_Personne_activite_202102020955_101.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(102l,"PS_LibreAcces_Personne_activite_202102020955_102.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(103l,"PS_LibreAcces_Personne_activite_202102020955_103.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(104l,"PS_LibreAcces_Personne_activite_202102020955_104.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(105l,"PS_LibreAcces_Personne_activite_202102020955_105.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(106l,"PS_LibreAcces_Personne_activite_202102020955_106.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(107l,"PS_LibreAcces_Personne_activite_202102020955_107.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(108l,"PS_LibreAcces_Personne_activite_202102020955_108.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(109l,"PS_LibreAcces_Personne_activite_202102020955_109.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(110l,"PS_LibreAcces_Personne_activite_202102020955_110.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(111l,"PS_LibreAcces_Personne_activite_202102020955_111.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(112l,"PS_LibreAcces_Personne_activite_202102020955_112.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(113l,"PS_LibreAcces_Personne_activite_202102020955_113.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(114l,"PS_LibreAcces_Personne_activite_202102020955_114.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(115l,"PS_LibreAcces_Personne_activite_202102020955_115.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(116l,"PS_LibreAcces_Personne_activite_202102020955_116.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(117l,"PS_LibreAcces_Personne_activite_202102020955_117.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(118l,"PS_LibreAcces_Personne_activite_202102020955_118.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(119l,"PS_LibreAcces_Personne_activite_202102020955_119.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(120l,"PS_LibreAcces_Personne_activite_202102020955_120.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(121l,"PS_LibreAcces_Personne_activite_202102020955_121.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(122l,"PS_LibreAcces_Personne_activite_202102020955_122.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(123l,"PS_LibreAcces_Personne_activite_202102020955_123.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(124l,"PS_LibreAcces_Personne_activite_202102020955_124.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(125l,"PS_LibreAcces_Personne_activite_202102020955_125.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(126l,"PS_LibreAcces_Personne_activite_202102020955_126.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(127l,"PS_LibreAcces_Personne_activite_202102020955_127.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(128l,"PS_LibreAcces_Personne_activite_202102020955_128.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(129l,"PS_LibreAcces_Personne_activite_202102020955_129.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(130l,"PS_LibreAcces_Personne_activite_202102020955_130.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(131l,"PS_LibreAcces_Personne_activite_202102020955_131.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(132l,"PS_LibreAcces_Personne_activite_202102020955_132.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(133l,"PS_LibreAcces_Personne_activite_202102020955_133.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(134l,"PS_LibreAcces_Personne_activite_202102020955_134.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(135l,"PS_LibreAcces_Personne_activite_202102020955_135.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(136l,"PS_LibreAcces_Personne_activite_202102020955_136.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(137l,"PS_LibreAcces_Personne_activite_202102020955_137.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(138l,"PS_LibreAcces_Personne_activite_202102020955_138.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(139l,"PS_LibreAcces_Personne_activite_202102020955_139.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(140l,"PS_LibreAcces_Personne_activite_202102020955_140.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(141l,"PS_LibreAcces_Personne_activite_202102020955_141.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(142l,"PS_LibreAcces_Personne_activite_202102020955_142.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(143l,"PS_LibreAcces_Personne_activite_202102020955_143.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(144l,"PS_LibreAcces_Personne_activite_202102020955_144.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(145l,"PS_LibreAcces_Personne_activite_202102020955_145.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(146l,"PS_LibreAcces_Personne_activite_202102020955_146.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(147l,"PS_LibreAcces_Personne_activite_202102020955_147.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(148l,"PS_LibreAcces_Personne_activite_202102020955_148.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(149l,"PS_LibreAcces_Personne_activite_202102020955_149.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(150l,"PS_LibreAcces_Personne_activite_202102020955_150.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(151l,"PS_LibreAcces_Personne_activite_202102020955_151.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(152l,"PS_LibreAcces_Personne_activite_202102020955_152.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(153l,"PS_LibreAcces_Personne_activite_202102020955_153.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(154l,"PS_LibreAcces_Personne_activite_202102020955_154.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(155l,"PS_LibreAcces_Personne_activite_202102020955_155.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(156l,"PS_LibreAcces_Personne_activite_202102020955_156.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(157l,"PS_LibreAcces_Personne_activite_202102020955_157.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(158l,"PS_LibreAcces_Personne_activite_202102020955_158.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(159l,"PS_LibreAcces_Personne_activite_202102020955_159.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(160l,"PS_LibreAcces_Personne_activite_202102020955_160.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(161l,"PS_LibreAcces_Personne_activite_202102020955_161.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(162l,"PS_LibreAcces_Personne_activite_202102020955_162.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(163l,"PS_LibreAcces_Personne_activite_202102020955_163.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(164l,"PS_LibreAcces_Personne_activite_202102020955_164.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(165l,"PS_LibreAcces_Personne_activite_202102020955_165.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(166l,"PS_LibreAcces_Personne_activite_202102020955_166.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(167l,"PS_LibreAcces_Personne_activite_202102020955_167.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(168l,"PS_LibreAcces_Personne_activite_202102020955_168.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(169l,"PS_LibreAcces_Personne_activite_202102020955_169.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(170l,"PS_LibreAcces_Personne_activite_202102020955_170.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(171l,"PS_LibreAcces_Personne_activite_202102020955_171.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(172l,"PS_LibreAcces_Personne_activite_202102020955_172.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(173l,"PS_LibreAcces_Personne_activite_202102020955_173.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(174l,"PS_LibreAcces_Personne_activite_202102020955_174.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(175l,"PS_LibreAcces_Personne_activite_202102020955_175.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(176l,"PS_LibreAcces_Personne_activite_202102020955_176.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(177l,"PS_LibreAcces_Personne_activite_202102020955_177.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(178l,"PS_LibreAcces_Personne_activite_202102020955_178.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(179l,"PS_LibreAcces_Personne_activite_202102020955_179.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(180l,"PS_LibreAcces_Personne_activite_202102020955_180.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(181l,"PS_LibreAcces_Personne_activite_202102020955_181.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(182l,"PS_LibreAcces_Personne_activite_202102020955_182.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(183l,"PS_LibreAcces_Personne_activite_202102020955_183.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(184l,"PS_LibreAcces_Personne_activite_202102020955_184.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(185l,"PS_LibreAcces_Personne_activite_202102020955_185.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(186l,"PS_LibreAcces_Personne_activite_202102020955_186.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(187l,"PS_LibreAcces_Personne_activite_202102020955_187.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(188l,"PS_LibreAcces_Personne_activite_202102020955_188.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(189l,"PS_LibreAcces_Personne_activite_202102020955_189.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(190l,"PS_LibreAcces_Personne_activite_202102020955_190.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(191l,"PS_LibreAcces_Personne_activite_202102020955_191.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(192l,"PS_LibreAcces_Personne_activite_202102020955_192.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(193l,"PS_LibreAcces_Personne_activite_202102020955_193.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(194l,"PS_LibreAcces_Personne_activite_202102020955_194.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(195l,"PS_LibreAcces_Personne_activite_202102020955_195.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(196l,"PS_LibreAcces_Personne_activite_202102020955_196.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(197l,"PS_LibreAcces_Personne_activite_202102020955_197.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(198l,"PS_LibreAcces_Personne_activite_202102020955_198.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(199l,"PS_LibreAcces_Personne_activite_202102020955_199.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(200l,"PS_LibreAcces_Personne_activite_202102020955_200.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(201l,"PS_LibreAcces_Personne_activite_202102020955_201.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(202l,"PS_LibreAcces_Personne_activite_202102020955_202.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(203l,"PS_LibreAcces_Personne_activite_202102020955_203.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(204l,"PS_LibreAcces_Personne_activite_202102020955_204.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(205l,"PS_LibreAcces_Personne_activite_202102020955_205.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(206l,"PS_LibreAcces_Personne_activite_202102020955_206.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(207l,"PS_LibreAcces_Personne_activite_202102020955_207.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(208l,"PS_LibreAcces_Personne_activite_202102020955_208.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(209l,"PS_LibreAcces_Personne_activite_202102020955_209.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(210l,"PS_LibreAcces_Personne_activite_202102020955_210.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(211l,"PS_LibreAcces_Personne_activite_202102020955_211.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(212l,"PS_LibreAcces_Personne_activite_202102020955_212.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(213l,"PS_LibreAcces_Personne_activite_202102020955_213.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(214l,"PS_LibreAcces_Personne_activite_202102020955_214.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(215l,"PS_LibreAcces_Personne_activite_202102020955_215.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(216l,"PS_LibreAcces_Personne_activite_202102020955_216.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(217l,"PS_LibreAcces_Personne_activite_202102020955_217.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(218l,"PS_LibreAcces_Personne_activite_202102020955_218.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(219l,"PS_LibreAcces_Personne_activite_202102020955_219.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(220l,"PS_LibreAcces_Personne_activite_202102020955_220.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(221l,"PS_LibreAcces_Personne_activite_202102020955_221.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(222l,"PS_LibreAcces_Personne_activite_202102020955_222.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(223l,"PS_LibreAcces_Personne_activite_202102020955_223.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(224l,"PS_LibreAcces_Personne_activite_202102020955_224.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(225l,"PS_LibreAcces_Personne_activite_202102020955_225.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(226l,"PS_LibreAcces_Personne_activite_202102020955_226.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(227l,"PS_LibreAcces_Personne_activite_202102020955_227.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(228l,"PS_LibreAcces_Personne_activite_202102020955_228.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(229l,"PS_LibreAcces_Personne_activite_202102020955_229.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(230l,"PS_LibreAcces_Personne_activite_202102020955_230.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(231l,"PS_LibreAcces_Personne_activite_202102020955_231.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(232l,"PS_LibreAcces_Personne_activite_202102020955_232.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(233l,"PS_LibreAcces_Personne_activite_202102020955_233.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(234l,"PS_LibreAcces_Personne_activite_202102020955_234.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(235l,"PS_LibreAcces_Personne_activite_202102020955_235.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(236l,"PS_LibreAcces_Personne_activite_202102020955_236.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(237l,"PS_LibreAcces_Personne_activite_202102020955_237.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(238l,"PS_LibreAcces_Personne_activite_202102020955_238.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(239l,"PS_LibreAcces_Personne_activite_202102020955_239.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(240l,"PS_LibreAcces_Personne_activite_202102020955_240.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(241l,"PS_LibreAcces_Personne_activite_202102020955_241.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(242l,"PS_LibreAcces_Personne_activite_202102020955_242.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(243l,"PS_LibreAcces_Personne_activite_202102020955_243.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(244l,"PS_LibreAcces_Personne_activite_202102020955_244.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(245l,"PS_LibreAcces_Personne_activite_202102020955_245.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(246l,"PS_LibreAcces_Personne_activite_202102020955_246.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(247l,"PS_LibreAcces_Personne_activite_202102020955_247.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(248l,"PS_LibreAcces_Personne_activite_202102020955_248.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(249l,"PS_LibreAcces_Personne_activite_202102020955_249.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(250l,"PS_LibreAcces_Personne_activite_202102020955_250.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(251l,"PS_LibreAcces_Personne_activite_202102020955_251.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(252l,"PS_LibreAcces_Personne_activite_202102020955_252.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(253l,"PS_LibreAcces_Personne_activite_202102020955_253.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(254l,"PS_LibreAcces_Personne_activite_202102020955_254.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(255l,"PS_LibreAcces_Personne_activite_202102020955_255.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(256l,"PS_LibreAcces_Personne_activite_202102020955_256.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(257l,"PS_LibreAcces_Personne_activite_202102020955_257.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(258l,"PS_LibreAcces_Personne_activite_202102020955_258.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(259l,"PS_LibreAcces_Personne_activite_202102020955_259.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(260l,"PS_LibreAcces_Personne_activite_202102020955_260.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(261l,"PS_LibreAcces_Personne_activite_202102020955_261.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(262l,"PS_LibreAcces_Personne_activite_202102020955_262.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(263l,"PS_LibreAcces_Personne_activite_202102020955_263.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(264l,"PS_LibreAcces_Personne_activite_202102020955_264.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(265l,"PS_LibreAcces_Personne_activite_202102020955_265.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(266l,"PS_LibreAcces_Personne_activite_202102020955_266.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(267l,"PS_LibreAcces_Personne_activite_202102020955_267.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(268l,"PS_LibreAcces_Personne_activite_202102020955_268.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(269l,"PS_LibreAcces_Personne_activite_202102020955_269.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(270l,"PS_LibreAcces_Personne_activite_202102020955_270.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(271l,"PS_LibreAcces_Personne_activite_202102020955_271.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(272l,"PS_LibreAcces_Personne_activite_202102020955_272.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(273l,"PS_LibreAcces_Personne_activite_202102020955_273.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(274l,"PS_LibreAcces_Personne_activite_202102020955_274.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(275l,"PS_LibreAcces_Personne_activite_202102020955_275.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(276l,"PS_LibreAcces_Personne_activite_202102020955_276.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(277l,"PS_LibreAcces_Personne_activite_202102020955_277.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(278l,"PS_LibreAcces_Personne_activite_202102020955_278.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(279l,"PS_LibreAcces_Personne_activite_202102020955_279.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(280l,"PS_LibreAcces_Personne_activite_202102020955_280.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(281l,"PS_LibreAcces_Personne_activite_202102020955_281.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(282l,"PS_LibreAcces_Personne_activite_202102020955_282.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(283l,"PS_LibreAcces_Personne_activite_202102020955_283.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(284l,"PS_LibreAcces_Personne_activite_202102020955_284.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(285l,"PS_LibreAcces_Personne_activite_202102020955_285.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(286l,"PS_LibreAcces_Personne_activite_202102020955_286.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(287l,"PS_LibreAcces_Personne_activite_202102020955_287.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(288l,"PS_LibreAcces_Personne_activite_202102020955_288.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(289l,"PS_LibreAcces_Personne_activite_202102020955_289.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(290l,"PS_LibreAcces_Personne_activite_202102020955_290.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(291l,"PS_LibreAcces_Personne_activite_202102020955_291.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(292l,"PS_LibreAcces_Personne_activite_202102020955_292.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(293l,"PS_LibreAcces_Personne_activite_202102020955_293.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(294l,"PS_LibreAcces_Personne_activite_202102020955_294.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(295l,"PS_LibreAcces_Personne_activite_202102020955_295.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(296l,"PS_LibreAcces_Personne_activite_202102020955_296.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(297l,"PS_LibreAcces_Personne_activite_202102020955_297.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(298l,"PS_LibreAcces_Personne_activite_202102020955_298.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(299l,"PS_LibreAcces_Personne_activite_202102020955_299.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(300l,"PS_LibreAcces_Personne_activite_202102020955_300.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(301l,"PS_LibreAcces_Personne_activite_202102020955_301.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(302l,"PS_LibreAcces_Personne_activite_202102020955_302.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(303l,"PS_LibreAcces_Personne_activite_202102020955_303.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(304l,"PS_LibreAcces_Personne_activite_202102020955_304.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(305l,"PS_LibreAcces_Personne_activite_202102020955_305.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(306l,"PS_LibreAcces_Personne_activite_202102020955_306.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(307l,"PS_LibreAcces_Personne_activite_202102020955_307.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(308l,"PS_LibreAcces_Personne_activite_202102020955_308.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(309l,"PS_LibreAcces_Personne_activite_202102020955_309.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(310l,"PS_LibreAcces_Personne_activite_202102020955_310.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(311l,"PS_LibreAcces_Personne_activite_202102020955_311.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(312l,"PS_LibreAcces_Personne_activite_202102020955_312.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(313l,"PS_LibreAcces_Personne_activite_202102020955_313.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(314l,"PS_LibreAcces_Personne_activite_202102020955_314.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(315l,"PS_LibreAcces_Personne_activite_202102020955_315.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(316l,"PS_LibreAcces_Personne_activite_202102020955_316.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(317l,"PS_LibreAcces_Personne_activite_202102020955_317.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(318l,"PS_LibreAcces_Personne_activite_202102020955_318.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(319l,"PS_LibreAcces_Personne_activite_202102020955_319.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(320l,"PS_LibreAcces_Personne_activite_202102020955_320.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(321l,"PS_LibreAcces_Personne_activite_202102020955_321.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(322l,"PS_LibreAcces_Personne_activite_202102020955_322.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(323l,"PS_LibreAcces_Personne_activite_202102020955_323.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(324l,"PS_LibreAcces_Personne_activite_202102020955_324.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(325l,"PS_LibreAcces_Personne_activite_202102020955_325.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(326l,"PS_LibreAcces_Personne_activite_202102020955_326.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(327l,"PS_LibreAcces_Personne_activite_202102020955_327.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(328l,"PS_LibreAcces_Personne_activite_202102020955_328.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(329l,"PS_LibreAcces_Personne_activite_202102020955_329.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(330l,"PS_LibreAcces_Personne_activite_202102020955_330.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(331l,"PS_LibreAcces_Personne_activite_202102020955_331.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(332l,"PS_LibreAcces_Personne_activite_202102020955_332.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(333l,"PS_LibreAcces_Personne_activite_202102020955_333.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(334l,"PS_LibreAcces_Personne_activite_202102020955_334.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(335l,"PS_LibreAcces_Personne_activite_202102020955_335.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(336l,"PS_LibreAcces_Personne_activite_202102020955_336.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(337l,"PS_LibreAcces_Personne_activite_202102020955_337.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(338l,"PS_LibreAcces_Personne_activite_202102020955_338.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(339l,"PS_LibreAcces_Personne_activite_202102020955_339.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(340l,"PS_LibreAcces_Personne_activite_202102020955_340.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(341l,"PS_LibreAcces_Personne_activite_202102020955_341.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(342l,"PS_LibreAcces_Personne_activite_202102020955_342.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(343l,"PS_LibreAcces_Personne_activite_202102020955_343.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(344l,"PS_LibreAcces_Personne_activite_202102020955_344.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(345l,"PS_LibreAcces_Personne_activite_202102020955_345.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(346l,"PS_LibreAcces_Personne_activite_202102020955_346.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(347l,"PS_LibreAcces_Personne_activite_202102020955_347.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(348l,"PS_LibreAcces_Personne_activite_202102020955_348.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(349l,"PS_LibreAcces_Personne_activite_202102020955_349.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(350l,"PS_LibreAcces_Personne_activite_202102020955_350.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(351l,"PS_LibreAcces_Personne_activite_202102020955_351.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(352l,"PS_LibreAcces_Personne_activite_202102020955_352.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(353l,"PS_LibreAcces_Personne_activite_202102020955_353.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(354l,"PS_LibreAcces_Personne_activite_202102020955_354.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(355l,"PS_LibreAcces_Personne_activite_202102020955_355.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(356l,"PS_LibreAcces_Personne_activite_202102020955_356.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(357l,"PS_LibreAcces_Personne_activite_202102020955_357.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(358l,"PS_LibreAcces_Personne_activite_202102020955_358.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(359l,"PS_LibreAcces_Personne_activite_202102020955_359.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(360l,"PS_LibreAcces_Personne_activite_202102020955_360.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(361l,"PS_LibreAcces_Personne_activite_202102020955_361.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(362l,"PS_LibreAcces_Personne_activite_202102020955_362.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(363l,"PS_LibreAcces_Personne_activite_202102020955_363.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(364l,"PS_LibreAcces_Personne_activite_202102020955_364.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(365l,"PS_LibreAcces_Personne_activite_202102020955_365.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(366l,"PS_LibreAcces_Personne_activite_202102020955_366.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(367l,"PS_LibreAcces_Personne_activite_202102020955_367.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(368l,"PS_LibreAcces_Personne_activite_202102020955_368.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(369l,"PS_LibreAcces_Personne_activite_202102020955_369.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(370l,"PS_LibreAcces_Personne_activite_202102020955_370.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(371l,"PS_LibreAcces_Personne_activite_202102020955_371.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(372l,"PS_LibreAcces_Personne_activite_202102020955_372.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(373l,"PS_LibreAcces_Personne_activite_202102020955_373.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(374l,"PS_LibreAcces_Personne_activite_202102020955_374.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(375l,"PS_LibreAcces_Personne_activite_202102020955_375.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(376l,"PS_LibreAcces_Personne_activite_202102020955_376.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(377l,"PS_LibreAcces_Personne_activite_202102020955_377.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(378l,"PS_LibreAcces_Personne_activite_202102020955_378.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(379l,"PS_LibreAcces_Personne_activite_202102020955_379.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(380l,"PS_LibreAcces_Personne_activite_202102020955_380.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(381l,"PS_LibreAcces_Personne_activite_202102020955_381.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(382l,"PS_LibreAcces_Personne_activite_202102020955_382.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(383l,"PS_LibreAcces_Personne_activite_202102020955_383.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(384l,"PS_LibreAcces_Personne_activite_202102020955_384.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(385l,"PS_LibreAcces_Personne_activite_202102020955_385.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(386l,"PS_LibreAcces_Personne_activite_202102020955_386.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(387l,"PS_LibreAcces_Personne_activite_202102020955_387.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(388l,"PS_LibreAcces_Personne_activite_202102020955_388.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(389l,"PS_LibreAcces_Personne_activite_202102020955_389.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(390l,"PS_LibreAcces_Personne_activite_202102020955_390.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(391l,"PS_LibreAcces_Personne_activite_202102020955_391.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(392l,"PS_LibreAcces_Personne_activite_202102020955_392.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(393l,"PS_LibreAcces_Personne_activite_202102020955_393.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(394l,"PS_LibreAcces_Personne_activite_202102020955_394.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(395l,"PS_LibreAcces_Personne_activite_202102020955_395.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(396l,"PS_LibreAcces_Personne_activite_202102020955_396.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(397l,"PS_LibreAcces_Personne_activite_202102020955_397.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(398l,"PS_LibreAcces_Personne_activite_202102020955_398.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(399l,"PS_LibreAcces_Personne_activite_202102020955_399.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(400l,"PS_LibreAcces_Personne_activite_202102020955_400.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(401l,"PS_LibreAcces_Personne_activite_202102020955_401.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(402l,"PS_LibreAcces_Personne_activite_202102020955_402.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(403l,"PS_LibreAcces_Personne_activite_202102020955_403.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(404l,"PS_LibreAcces_Personne_activite_202102020955_404.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(405l,"PS_LibreAcces_Personne_activite_202102020955_405.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(406l,"PS_LibreAcces_Personne_activite_202102020955_406.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(407l,"PS_LibreAcces_Personne_activite_202102020955_407.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(408l,"PS_LibreAcces_Personne_activite_202102020955_408.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(409l,"PS_LibreAcces_Personne_activite_202102020955_409.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(410l,"PS_LibreAcces_Personne_activite_202102020955_410.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(411l,"PS_LibreAcces_Personne_activite_202102020955_411.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(412l,"PS_LibreAcces_Personne_activite_202102020955_412.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(413l,"PS_LibreAcces_Personne_activite_202102020955_413.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(414l,"PS_LibreAcces_Personne_activite_202102020955_414.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(415l,"PS_LibreAcces_Personne_activite_202102020955_415.txt", false,"","",0,0,0));
            importContactDao.insert(new ImportContact(416l,"PS_LibreAcces_Personne_activite_202102020955_416.txt", false,"","",0,0,0));






        }
    }

    public void remplirDepartementBD() {
        //Long count = Departement.count(Departement.class);
        Long count = departementDao.count();
        if (count ==0) {

            //departementDao.insert(new Departement(0l,"01","Ain",0l));

            departementDao.insert(new Departement(1l,"01","Ain", 0l));
            departementDao.insert(new Departement(2l,"02","Aisne",6l));
            departementDao.insert(new Departement(3l,"03","Allier",0l));
            departementDao.insert(new Departement(4l,"04","Alpes-de-Haute-Provence",12l));
            departementDao.insert(new Departement(5l,"05","Hautes-Alpes",12l));
            departementDao.insert(new Departement(6l,"06","Alpes-Maritimes",12l));
            departementDao.insert(new Departement(7l,"07","Ardèche",0l));
            departementDao.insert(new Departement(8l,"08","Ardennes",5l));
            departementDao.insert(new Departement(9l,"09","Ariège",10l));
            departementDao.insert(new Departement(10l,"10","Aube",5l));
            departementDao.insert(new Departement(11l,"11","Aude",10l));
            departementDao.insert(new Departement(12l,"12","Aveyron",10l));
            departementDao.insert(new Departement(13l,"13","Bouches-du-Rhône",12l));
            departementDao.insert(new Departement(14l,"14","Calvados",8l));
            departementDao.insert(new Departement(15l,"15","Cantal",0l));
            departementDao.insert(new Departement(16l,"16","Charente",9l));
            departementDao.insert(new Departement(17l,"17","Charente-Maritime",9l));
            departementDao.insert(new Departement(18l,"18","Cher",3l));
            departementDao.insert(new Departement(19l,"19","Corrèze",9l));
            departementDao.insert(new Departement(20l,"20","Corse",4l));
            departementDao.insert(new Departement(21l,"21","Côte-d'Or",1l));
            departementDao.insert(new Departement(22l,"22","Côtes d'Armor",2l));
            departementDao.insert(new Departement(23l,"23","Creuse",9l));
            departementDao.insert(new Departement(24l,"24","Dordogne",9l));
            departementDao.insert(new Departement(25l,"25","Doubs",1l));
            departementDao.insert(new Departement(26l,"26","Drôme",0l));
            departementDao.insert(new Departement(27l,"27","Eure",8l));
            departementDao.insert(new Departement(28l,"28","Eure-et-Loir",3l));
            departementDao.insert(new Departement(29l,"29","Finistère",2l));
            departementDao.insert(new Departement(30l,"30","Gard",10l));
            departementDao.insert(new Departement(31l,"31","Haute-Garonne",10l));
            departementDao.insert(new Departement(32l,"32","Gers",10l));
            departementDao.insert(new Departement(33l,"33","Gironde",9l));
            departementDao.insert(new Departement(34l,"34","Hérault",10l));
            departementDao.insert(new Departement(35l,"35","Ille-et-Vilaine",2l));
            departementDao.insert(new Departement(36l,"36","Indre",3l));
            departementDao.insert(new Departement(37l,"37","Indre-et-Loire",3l));
            departementDao.insert(new Departement(38l,"38","Isère",0l));
            departementDao.insert(new Departement(39l,"39","Jura",1l));
            departementDao.insert(new Departement(40l,"40","Landes",9l));
            departementDao.insert(new Departement(41l,"41","Loir-et-Cher",3l));
            departementDao.insert(new Departement(42l,"42","Loire",0l));
            departementDao.insert(new Departement(43l,"43","Haute-Loire",0l));
            departementDao.insert(new Departement(44l,"44","Loire-Atlantique",11l));
            departementDao.insert(new Departement(45l,"45","Loiret",3l));
            departementDao.insert(new Departement(46l,"46","Lot",10l));
            departementDao.insert(new Departement(47l,"47","Lot-et-Garonne",9l));
            departementDao.insert(new Departement(48l,"48","Lozère",10l));
            departementDao.insert(new Departement(49l,"49","Maine-et-Loire",11l));
            departementDao.insert(new Departement(50l,"50","Manche",8l));
            departementDao.insert(new Departement(51l,"51","Marne",5l));
            departementDao.insert(new Departement(52l,"52","Haute-Marne",5l));
            departementDao.insert(new Departement(53l,"53","Mayenne",11l));
            departementDao.insert(new Departement(54l,"54","Meurthe-et-Moselle",5l));
            departementDao.insert(new Departement(55l,"55","Meuse",5l));
            departementDao.insert(new Departement(56l,"56","Morbihan",2l));
            departementDao.insert(new Departement(57l,"57","Moselle",5l));
            departementDao.insert(new Departement(58l,"58","Nièvre",1l));
            departementDao.insert(new Departement(59l,"59","Nord",6l));
            departementDao.insert(new Departement(60l,"60","Oise",6l));
            departementDao.insert(new Departement(61l,"61","Orne",8l));
            departementDao.insert(new Departement(62l,"62","Pas-de-Calais",6l));
            departementDao.insert(new Departement(63l,"63","Puy-de-Dôme",0l));
            departementDao.insert(new Departement(64l,"64","Pyrénées-Atlantiques",9l));
            departementDao.insert(new Departement(65l,"65","Hautes-Pyrénées",10l));
            departementDao.insert(new Departement(66l,"66","Pyrénées-Orientales",10l));
            departementDao.insert(new Departement(67l,"67","Bas-Rhin",5l));
            departementDao.insert(new Departement(68l,"68","Haut-Rhin",5l));
            departementDao.insert(new Departement(69l,"69","Rhône",0l));
            departementDao.insert(new Departement(70l,"70","Haute-Saône",1l));
            departementDao.insert(new Departement(71l,"71","Saône-et-Loire",1l));
            departementDao.insert(new Departement(72l,"72","Sarthe",11l));
            departementDao.insert(new Departement(73l,"73","Savoie",0l));
            departementDao.insert(new Departement(74l,"74","Haute-Savoie",0l));
            departementDao.insert(new Departement(75l,"75","Paris",7l));
            departementDao.insert(new Departement(76l,"76","Seine-Maritime",8l));
            departementDao.insert(new Departement(77l,"77","Seine-et-Marne",7l));
            departementDao.insert(new Departement(78l,"78","Yvelines",7l));
            departementDao.insert(new Departement(79l,"79","Deux-Sèvres",9l));
            departementDao.insert(new Departement(80l,"80","Somme",6l));
            departementDao.insert(new Departement(81l,"81","Tarn",10l));
            departementDao.insert(new Departement(82l,"82","Tarn-et-Garonne",10l));
            departementDao.insert(new Departement(83l,"83","Var",12l));
            departementDao.insert(new Departement(84l,"84","Vaucluse",12l));
            departementDao.insert(new Departement(85l,"85","Vendée",11l));
            departementDao.insert(new Departement(86l,"86","Vienne",9l));
            departementDao.insert(new Departement(87l,"87","Haute-Vienne",9l));
            departementDao.insert(new Departement(88l,"88","Vosges",5l));
            departementDao.insert(new Departement(89l,"89","Yonne",1l));
            departementDao.insert(new Departement(90l,"90","Territoire-de-Belfort",1l));
            departementDao.insert(new Departement(91l,"91","Essonne",7l));
            departementDao.insert(new Departement(92l,"92","Hauts-de-Seine",7l));
            departementDao.insert(new Departement(93l,"93","Seine-Saint-Denis",7l));
            departementDao.insert(new Departement(94l,"94","Val-de-Marne",7l));
            departementDao.insert(new Departement(95l,"95","Val-D'Oise",7l));
            departementDao.insert(new Departement(97l,"97","Outre-Mer",13l));
            departementDao.insert(new Departement(98l,"98","Autre",14l));
            departementDao.insert(new Departement(99l,"XX","Indéfini",15l));
        }
    }

    public void remplirRegionBD() {
        //Long count = Region.count(Region.class);
        Long count = regionDao.count();
        if (count ==0) {
            regionDao.insert(new Region(0l,"Auvergne-Rhône-Alpes"));
            regionDao.insert(new Region(1l,"Bourgogne-Franche-Comté"));
            regionDao.insert(new Region(2l,"Bretagne"));
            regionDao.insert(new Region(3l,"Centre-Val de Loire"));
            regionDao.insert(new Region(4l,"Corse"));
            regionDao.insert(new Region(5l,"Grand Est"));
            regionDao.insert(new Region(6l,"Hauts-de-France"));
            regionDao.insert(new Region(7l,"Ile-de-France"));
            regionDao.insert(new Region(8l,"Normandie"));
            regionDao.insert(new Region(9l,"Nouvelle-Aquitaine"));
            regionDao.insert(new Region(10l,"Occitanie"));
            regionDao.insert(new Region(11l,"Pays de la Loire"));
            regionDao.insert(new Region(12l,"Provence-Alpes-Côte d'Azur"));
            regionDao.insert(new Region(13l,"Dom-Tom"));
            regionDao.insert(new Region(14l,"Autre"));
            regionDao.insert(new Region(15l,"Indéfini"));
        }
    }

    public void remplirSavoirFaireBD() {
        //Long count = SavoirFaire.count(SavoirFaire.class);
        Long count = savoirFaireDao.count();
        if (count ==0) {
            savoirFaireDao.insert(new SavoirFaire(0l,"Allergologie"));
            savoirFaireDao.insert(new SavoirFaire(1l,"Anatomie et cytologie pathologiques"));
            savoirFaireDao.insert(new SavoirFaire(2l,"Anesthesie-réanimation"));
            savoirFaireDao.insert(new SavoirFaire(3l,"Biologie médicale"));
            savoirFaireDao.insert(new SavoirFaire(4l,"Cardiologie et maladies vasculaires"));
            savoirFaireDao.insert(new SavoirFaire(5l,"Chirurgie générale"));
            savoirFaireDao.insert(new SavoirFaire(6l,"Chirurgie infantile"));
            savoirFaireDao.insert(new SavoirFaire(7l,"Chirurgie maxillo-faciale"));
            savoirFaireDao.insert(new SavoirFaire(8l,"Chirurgie maxillo-faciale (réforme 2017)"));
            savoirFaireDao.insert(new SavoirFaire(9l,"Chirurgie maxillo-faciale et stomatologie"));
            savoirFaireDao.insert(new SavoirFaire(10l,"Chirurgie Orale"));
            savoirFaireDao.insert(new SavoirFaire(11l,"Chirurgie orthopédique et traumatologie"));
            savoirFaireDao.insert(new SavoirFaire(12l,"Chirurgie plastique reconstructrice et esthétique"));
            savoirFaireDao.insert(new SavoirFaire(13l,"Chirurgie thoracique et cardio-vasculaire"));
            savoirFaireDao.insert(new SavoirFaire(14l,"Chirurgie urologique"));
            savoirFaireDao.insert(new SavoirFaire(15l,"Chirurgie vasculaire"));
            savoirFaireDao.insert(new SavoirFaire(16l,"Chirurgie viscérale et digestive"));
            savoirFaireDao.insert(new SavoirFaire(17l,"Dermatologie et vénéréologie"));
            savoirFaireDao.insert(new SavoirFaire(18l,"Endocrinologie et métabolisme"));
            savoirFaireDao.insert(new SavoirFaire(19l,"Endocrinologie, diabétologie, nutrition"));
            savoirFaireDao.insert(new SavoirFaire(20l,"Gastro-entérologie et hépatologie"));
            savoirFaireDao.insert(new SavoirFaire(21l,"Génétique médicale"));
            savoirFaireDao.insert(new SavoirFaire(22l,"Gériatrie"));
            savoirFaireDao.insert(new SavoirFaire(23l,"Gynécologie médicale"));
            savoirFaireDao.insert(new SavoirFaire(24l,"Gynécologie médicale et obstétrique"));
            savoirFaireDao.insert(new SavoirFaire(25l,"Gynécologie-obstétrique"));
            savoirFaireDao.insert(new SavoirFaire(26l,"Gynéco-obstétrique et Gynéco médicale option Gynéco-médicale"));
            savoirFaireDao.insert(new SavoirFaire(27l,"Gynéco-obstétrique et Gynéco médicale option Gynéco-obst"));
            savoirFaireDao.insert(new SavoirFaire(28l,"Hématologie"));
            savoirFaireDao.insert(new SavoirFaire(29l,"Hématologie (option Maladie du sang)"));
            savoirFaireDao.insert(new SavoirFaire(30l,"Hématologie (option Onco-hématologie)"));
            savoirFaireDao.insert(new SavoirFaire(31l,"Hématologie (réforme 2017)"));
            savoirFaireDao.insert(new SavoirFaire(32l,"Maladies infectieuses et tropicales"));
            savoirFaireDao.insert(new SavoirFaire(33l,"Médecine Bucco-Dentaire"));
            savoirFaireDao.insert(new SavoirFaire(34l,"Médecine du travail"));
            savoirFaireDao.insert(new SavoirFaire(35l,"Médecine d'urgence"));
            savoirFaireDao.insert(new SavoirFaire(36l,"Médecine Générale"));
            savoirFaireDao.insert(new SavoirFaire(37l,"Médecine intensive-réanimation"));
            savoirFaireDao.insert(new SavoirFaire(38l,"Médecine interne"));
            savoirFaireDao.insert(new SavoirFaire(39l,"Médecine interne et immunologie clinique"));
            savoirFaireDao.insert(new SavoirFaire(40l,"Médecine légale et expertises médicales"));
            savoirFaireDao.insert(new SavoirFaire(41l,"Médecine nucléaire"));
            savoirFaireDao.insert(new SavoirFaire(42l,"Médecine physique et réadaptation"));
            savoirFaireDao.insert(new SavoirFaire(43l,"Médecine vasculaire"));
            savoirFaireDao.insert(new SavoirFaire(44l,"Néphrologie"));
            savoirFaireDao.insert(new SavoirFaire(45l,"Neuro-chirurgie"));
            savoirFaireDao.insert(new SavoirFaire(46l,"Neurologie"));
            savoirFaireDao.insert(new SavoirFaire(47l,"Neuro-psychiatrie"));
            savoirFaireDao.insert(new SavoirFaire(48l,"O.R.L et chirurgie cervico faciale"));
            savoirFaireDao.insert(new SavoirFaire(49l,"Obstétrique"));
            savoirFaireDao.insert(new SavoirFaire(50l,"Oncologie (option onco-hématologie)"));
            savoirFaireDao.insert(new SavoirFaire(51l,"Oncologie option médicale"));
            savoirFaireDao.insert(new SavoirFaire(52l,"Oncologie option radiothérapie"));
            savoirFaireDao.insert(new SavoirFaire(53l,"Ophtalmologie"));
            savoirFaireDao.insert(new SavoirFaire(54l,"Orthopédie dento-faciale"));
            savoirFaireDao.insert(new SavoirFaire(55l,"Oto-rhino-laryngologie"));
            savoirFaireDao.insert(new SavoirFaire(56l,"Pédiatrie"));
            savoirFaireDao.insert(new SavoirFaire(57l,"Pneumologie"));
            savoirFaireDao.insert(new SavoirFaire(58l,"Psychiatrie"));
            savoirFaireDao.insert(new SavoirFaire(59l,"Psychiatrie option enfant & adolescent"));
            savoirFaireDao.insert(new SavoirFaire(60l,"Qualification PAC"));
            savoirFaireDao.insert(new SavoirFaire(61l,"Radio-diagnostic"));
            savoirFaireDao.insert(new SavoirFaire(62l,"Radio-thérapie "));
            savoirFaireDao.insert(new SavoirFaire(63l,"Recherche médicale"));
            savoirFaireDao.insert(new SavoirFaire(64l,"Rhumatologie"));
            savoirFaireDao.insert(new SavoirFaire(65l,"Santé publique et médecine sociale"));
            savoirFaireDao.insert(new SavoirFaire(66l,"Stomatologie"));
            savoirFaireDao.insert(new SavoirFaire(67l,"Urologie"));
            savoirFaireDao.insert(new SavoirFaire(68l,"Biologie médicale option biologie de la reproduction"));
            savoirFaireDao.insert(new SavoirFaire(69l,""));
        }
    }

    public void remplirProfessionBD() {
        //Long count = Profession.count(Profession.class);
        Long count = professionDao.count();
        if (count ==0) {
            professionDao.insert(new Profession(0l,"Audioprothésiste"));
            professionDao.insert(new Profession(1l,"Chirurgien-Dentiste"));
            professionDao.insert(new Profession(2l,"Diététicien"));
            professionDao.insert(new Profession(3l,"Epithésiste"));
            professionDao.insert(new Profession(4l,"Ergothérapeute"));
            professionDao.insert(new Profession(5l,"Infirmier"));
            professionDao.insert(new Profession(6l,"Manipulateur ERM"));
            professionDao.insert(new Profession(7l,"Masseur-Kinésithérapeute"));
            professionDao.insert(new Profession(8l,"Médecin"));
            professionDao.insert(new Profession(9l,"Oculariste"));
            professionDao.insert(new Profession(10l,"Opticien-Lunetier"));
            professionDao.insert(new Profession(11l,"Orthopédiste-Orthésiste"));
            professionDao.insert(new Profession(12l,"Orthophoniste"));
            professionDao.insert(new Profession(13l,"Orthoprothésiste"));
            professionDao.insert(new Profession(14l,"Orthoptiste"));
            professionDao.insert(new Profession(15l,"Pédicure-Podologue"));
            professionDao.insert(new Profession(16l,"Pharmacien"));
            professionDao.insert(new Profession(17l,"Podo-Orthésiste"));
            professionDao.insert(new Profession(18l,"Psychomotricien"));
            professionDao.insert(new Profession(19l,"Sage-Femme"));
            professionDao.insert(new Profession(20l,"Technicien de laboratoire médical"));
            professionDao.insert(new Profession(21l,"Assistant de service social"));
            professionDao.insert(new Profession(22l,"Assistant dentaire"));
            professionDao.insert(new Profession(23l,"Chiropracteur"));
            professionDao.insert(new Profession(24l,"Ostéopathe"));
            professionDao.insert(new Profession(25l,"Psychologue"));
            professionDao.insert(new Profession(26l,"Psychothérapeute"));
            professionDao.insert(new Profession(27l,""));
        }
    }

    public void remplirTypeEtablissementBD() {
        //Long count = TypeEtablissement.count(TypeEtablissement.class);
        Long count = typeEtablissementDao.count();
        if (count ==0) {
            typeEtablissementDao.insert(new TypeEtablissement(0l,"Aire Station Nomades"));
            typeEtablissementDao.insert(new TypeEtablissement(1l,"Appartement de Coordination Thérapeutique (A.C.T.)"));
            typeEtablissementDao.insert(new TypeEtablissement(2l,"Appartement Thérapeutique"));
            typeEtablissementDao.insert(new TypeEtablissement(3l,"Atelier Thérapeutique"));
            typeEtablissementDao.insert(new TypeEtablissement(4l,"Autre Centre d'Accueil"));
            typeEtablissementDao.insert(new TypeEtablissement(5l,"Autre Etablissement Loi Hospitalière"));
            typeEtablissementDao.insert(new TypeEtablissement(6l,"Autre Laboratoire de Biologie Médicale sans FSE"));
            typeEtablissementDao.insert(new TypeEtablissement(7l,"Autre Résidence Sociale (hors Maison Relais, Pension de Fami"));
            typeEtablissementDao.insert(new TypeEtablissement(8l,"Bureau d'Aide Psychologique Universitaire (B.A.P.U.)"));
            typeEtablissementDao.insert(new TypeEtablissement(9l,"Centre Accueil Demandeurs Asile (C.A.D.A.)"));
            typeEtablissementDao.insert(new TypeEtablissement(10l,"Centre Action Médico-Sociale Précoce (C.A.M.S.P.)"));
            typeEtablissementDao.insert(new TypeEtablissement(11l,"Centre Circonscription Sanitaire et Sociale"));
            typeEtablissementDao.insert(new TypeEtablissement(12l,"Centre Crise Accueil Permanent"));
            typeEtablissementDao.insert(new TypeEtablissement(13l,"Centre d'Accueil Familial Spécialisé"));
            typeEtablissementDao.insert(new TypeEtablissement(14l,"Centre d'Accueil Thérapeutique à temps partiel (C.A.T.T.P.)"));
            typeEtablissementDao.insert(new TypeEtablissement(15l,"Centre d'Action Educative (C.A.E.)"));
            typeEtablissementDao.insert(new TypeEtablissement(16l,"Centre de Consultations Cancer"));
            typeEtablissementDao.insert(new TypeEtablissement(17l,"Centre de dialyse"));
            typeEtablissementDao.insert(new TypeEtablissement(18l,"Centre de Jour pour Personnes Agées"));
            typeEtablissementDao.insert(new TypeEtablissement(19l,"Centre de Lutte Contre Cancer"));
            typeEtablissementDao.insert(new TypeEtablissement(20l,"Centre de Médecine collective"));
            typeEtablissementDao.insert(new TypeEtablissement(21l,"Centre de Médecine Sportive"));
            typeEtablissementDao.insert(new TypeEtablissement(22l,"Centre de Médecine Universitaire"));
            typeEtablissementDao.insert(new TypeEtablissement(23l,"Centre de Pré orientation pour Handicapés"));
            typeEtablissementDao.insert(new TypeEtablissement(24l,"Centre de Santé"));
            typeEtablissementDao.insert(new TypeEtablissement(25l,"Centre de Services pour Associations"));
            typeEtablissementDao.insert(new TypeEtablissement(26l,"Centre de soins et de prévention"));
            typeEtablissementDao.insert(new TypeEtablissement(27l,"Centre de Vaccination BCG"));
            typeEtablissementDao.insert(new TypeEtablissement(28l,"Centre d'Examens de Santé"));
            typeEtablissementDao.insert(new TypeEtablissement(29l,"Centre Hébergement & Réinsertion Sociale (C.H.R.S.)"));
            typeEtablissementDao.insert(new TypeEtablissement(30l,"Centre Hospitalier (C.H.)"));
            typeEtablissementDao.insert(new TypeEtablissement(31l,"Centre Hospitalier Régional (C.H.R.)"));
            typeEtablissementDao.insert(new TypeEtablissement(32l,"Centre Hospitalier Spécialisé lutte Maladies Mentales"));
            typeEtablissementDao.insert(new TypeEtablissement(33l,"Centre hospitalier, ex Hôpital local"));
            typeEtablissementDao.insert(new TypeEtablissement(34l,"Centre Médico-Psychologique (C.M.P.)"));
            typeEtablissementDao.insert(new TypeEtablissement(35l,"Centre Médico-Psycho-Pédagogique (C.M.P.P.)"));
            typeEtablissementDao.insert(new TypeEtablissement(36l,"Centre Médico-Scolaire"));
            typeEtablissementDao.insert(new TypeEtablissement(37l,"Centre Placement Familial Socio-Educatif (C.P.F.S.E.)"));
            typeEtablissementDao.insert(new TypeEtablissement(38l,"Centre Planification ou Education Familiale"));
            typeEtablissementDao.insert(new TypeEtablissement(39l,"Centre Postcure Malades Mentaux"));
            typeEtablissementDao.insert(new TypeEtablissement(40l,"Centre Provisoire Hébergement (C.P.H.)"));
            typeEtablissementDao.insert(new TypeEtablissement(41l,"Centre Rééducation Professionnelle"));
            typeEtablissementDao.insert(new TypeEtablissement(42l,"Centre Social"));
            typeEtablissementDao.insert(new TypeEtablissement(43l,"Centre soins accompagnement prévention addictologie (CSAPA)"));
            typeEtablissementDao.insert(new TypeEtablissement(44l,"Centres de Ressources S.A.I. (Sans Aucune Indication)"));
            typeEtablissementDao.insert(new TypeEtablissement(45l,"Centres Locaux Information Coordination P.A .(C.L.I.C.)"));
            typeEtablissementDao.insert(new TypeEtablissement(46l,"Club Equipe de Prévention"));
            typeEtablissementDao.insert(new TypeEtablissement(47l,"Communautés professionnelles territoriales de santé (CPTS)"));
            typeEtablissementDao.insert(new TypeEtablissement(48l,"Ctre.Accueil/ Accomp.Réduc.Risq.Usag. Drogues (C.A.A.R.U.D.)"));
            typeEtablissementDao.insert(new TypeEtablissement(49l,"Dispensaire Antihansénien"));
            typeEtablissementDao.insert(new TypeEtablissement(50l,"Dispensaire Antituberculeux"));
            typeEtablissementDao.insert(new TypeEtablissement(51l,"Dispensaire Antivénérien"));
            typeEtablissementDao.insert(new TypeEtablissement(52l,"Ecole des Hautes Etudes en Santé Publique (E.H.E.S.P.)"));
            typeEtablissementDao.insert(new TypeEtablissement(53l,"Ecoles Formant aux Professions Sanitaires"));
            typeEtablissementDao.insert(new TypeEtablissement(54l,"Ecoles Formant aux Professions Sanitaires et Sociales"));
            typeEtablissementDao.insert(new TypeEtablissement(55l,"Ecoles Formant aux Professions Sociales"));
            typeEtablissementDao.insert(new TypeEtablissement(56l,"EHPA ne percevant pas des crédits d'assurance maladie"));
            typeEtablissementDao.insert(new TypeEtablissement(57l,"EHPA percevant des crédits d'assurance maladie"));
            typeEtablissementDao.insert(new TypeEtablissement(58l,"Entité Ayant Autorisation"));
            typeEtablissementDao.insert(new TypeEtablissement(59l,"Entreprise adaptée"));
            typeEtablissementDao.insert(new TypeEtablissement(60l,"Etab.Acc.Médicalisé en tout ou partie personnes handicapées"));
            typeEtablissementDao.insert(new TypeEtablissement(61l,"Etab.Accueil Non Médicalisé pour personnes handicapées"));
            typeEtablissementDao.insert(new TypeEtablissement(62l,"Etablissement Consultation Protection Infantile"));
            typeEtablissementDao.insert(new TypeEtablissement(63l,"Etablissement d'Accueil Mère-Enfant"));
            typeEtablissementDao.insert(new TypeEtablissement(64l,"Etablissement d'Accueil Temporaire d'Enfants Handicapés"));
            typeEtablissementDao.insert(new TypeEtablissement(65l,"Etablissement d'Accueil Temporaire pour Adultes Handicapés"));
            typeEtablissementDao.insert(new TypeEtablissement(66l,"Etablissement de Consultation Pré et Post-natale"));
            typeEtablissementDao.insert(new TypeEtablissement(67l,"Etablissement de santé privé autorisé en SSR"));
            typeEtablissementDao.insert(new TypeEtablissement(68l,"Etablissement de Soins Chirurgicaux"));
            typeEtablissementDao.insert(new TypeEtablissement(69l,"Etablissement de Soins du Service de Santé des Armées"));
            typeEtablissementDao.insert(new TypeEtablissement(70l,"Etablissement de Soins Longue Durée"));
            typeEtablissementDao.insert(new TypeEtablissement(71l,"Etablissement de Soins Médicaux"));
            typeEtablissementDao.insert(new TypeEtablissement(72l,"Etablissement de Soins Pluridisciplinaire"));
            typeEtablissementDao.insert(new TypeEtablissement(73l,"Etablissement de Transfusion Sanguine"));
            typeEtablissementDao.insert(new TypeEtablissement(74l,"Etablissement d'hébergement pour personnes âgées dépendantes"));
            typeEtablissementDao.insert(new TypeEtablissement(75l,"Etablissement et Service d'Aide par le Travail (E.S.A.T.)"));
            typeEtablissementDao.insert(new TypeEtablissement(76l,"Etablissement Expérimental Autres Adultes"));
            typeEtablissementDao.insert(new TypeEtablissement(77l,"Etablissement Expérimental Enfance Protégée"));
            typeEtablissementDao.insert(new TypeEtablissement(78l,"Etablissement Expérimental pour Adultes Handicapés"));
            typeEtablissementDao.insert(new TypeEtablissement(79l,"Etablissement Expérimental pour Enfance Handicapée"));
            typeEtablissementDao.insert(new TypeEtablissement(80l,"Etablissement Expérimental pour Personnes Agées"));
            typeEtablissementDao.insert(new TypeEtablissement(81l,"Etablissement Expérimental pour personnes handicapées"));
            typeEtablissementDao.insert(new TypeEtablissement(82l,"Etablissement Information Consultation Conseil Familial"));
            typeEtablissementDao.insert(new TypeEtablissement(83l,"Etablissement pour Enfants ou Adolescents Polyhandicapés"));
            typeEtablissementDao.insert(new TypeEtablissement(84l,"Etablissement Sanitaire des Prisons"));
            typeEtablissementDao.insert(new TypeEtablissement(85l,"Etablissement Soins Obstétriques Chirurgico-Gynécologiques"));
            typeEtablissementDao.insert(new TypeEtablissement(86l,"Etablissement Thermal"));
            typeEtablissementDao.insert(new TypeEtablissement(87l,"Foyer d'Accueil Médicalisé pour Adultes Handicapés (F.A.M.)"));
            typeEtablissementDao.insert(new TypeEtablissement(88l,"Foyer d'Accueil Polyvalent pour Adultes Handicapés"));
            typeEtablissementDao.insert(new TypeEtablissement(89l,"Foyer d'Action Educative (F.A.E.)"));
            typeEtablissementDao.insert(new TypeEtablissement(90l,"Foyer de Jeunes Travailleurs (résidence sociale ou non)"));
            typeEtablissementDao.insert(new TypeEtablissement(91l,"Foyer de l'Enfance"));
            typeEtablissementDao.insert(new TypeEtablissement(92l,"Foyer de Vie pour Adultes Handicapés"));
            typeEtablissementDao.insert(new TypeEtablissement(93l,"Foyer Hébergement Adultes Handicapés"));
            typeEtablissementDao.insert(new TypeEtablissement(94l,"Foyer Hébergement Enfants et Adolescents Handicapés"));
            typeEtablissementDao.insert(new TypeEtablissement(95l,"Foyer Travailleurs Migrants non transformé en Résidence Soc."));
            typeEtablissementDao.insert(new TypeEtablissement(96l,"Groupement de coopération sanitaire - Etablissement de santé"));
            typeEtablissementDao.insert(new TypeEtablissement(97l,"Groupement de coopération sanitaire de moyens"));
            typeEtablissementDao.insert(new TypeEtablissement(98l,"Groupement de coopération sanitaire de moyens - Exploitant"));
            typeEtablissementDao.insert(new TypeEtablissement(99l,"Hôpital des armées"));
            typeEtablissementDao.insert(new TypeEtablissement(100l,"Hospitalisation à Domicile"));
            typeEtablissementDao.insert(new TypeEtablissement(101l,"Installation autonome de chirurgie esthétique"));
            typeEtablissementDao.insert(new TypeEtablissement(102l,"Institut d'éducation motrice"));
            typeEtablissementDao.insert(new TypeEtablissement(103l,"Institut d'Education Sensorielle Sourd/Aveugle"));
            typeEtablissementDao.insert(new TypeEtablissement(104l,"Institut Médico-Educatif (I.M.E.)"));
            typeEtablissementDao.insert(new TypeEtablissement(105l,"Institut pour Déficients Auditifs"));
            typeEtablissementDao.insert(new TypeEtablissement(106l,"Institut pour Déficients Visuels"));
            typeEtablissementDao.insert(new TypeEtablissement(107l,"Institut Thérapeutique Éducatif et Pédagogique (I.T.E.P.)"));
            typeEtablissementDao.insert(new TypeEtablissement(108l,"Intermédiaire de Placement Social"));
            typeEtablissementDao.insert(new TypeEtablissement(109l,"Jardin d'Enfants Spécialisé"));
            typeEtablissementDao.insert(new TypeEtablissement(110l,"Laboratoire d'Analyses"));
            typeEtablissementDao.insert(new TypeEtablissement(111l,"Laboratoire de Biologie Médicale"));
            typeEtablissementDao.insert(new TypeEtablissement(112l,"Laboratoire pharmaceutique préparant délivrant allergènes"));
            typeEtablissementDao.insert(new TypeEtablissement(113l,"Lieux de vie"));
            typeEtablissementDao.insert(new TypeEtablissement(114l,"Lits d'Accueil Médicalisés (L.A.M.)"));
            typeEtablissementDao.insert(new TypeEtablissement(115l,"Lits Halte Soins Santé (L.H.S.S.)"));
            typeEtablissementDao.insert(new TypeEtablissement(116l,"Logement Foyer non Spécialisé"));
            typeEtablissementDao.insert(new TypeEtablissement(117l,"Maison d'Accueil Spécialisée (M.A.S.)"));
            typeEtablissementDao.insert(new TypeEtablissement(118l,"Maison de naissance"));
            typeEtablissementDao.insert(new TypeEtablissement(119l,"Maison de santé (L.6223-3)"));
            typeEtablissementDao.insert(new TypeEtablissement(120l,"Maison de Santé pour Maladies Mentales"));
            typeEtablissementDao.insert(new TypeEtablissement(121l,"Maison d'Enfants à Caractère Social"));
            typeEtablissementDao.insert(new TypeEtablissement(122l,"Maisons d'accueil hospitalières (M.A.H.)"));
            typeEtablissementDao.insert(new TypeEtablissement(123l,"Maisons Relais - Pensions de Famille"));
            typeEtablissementDao.insert(new TypeEtablissement(124l,"Pharmacie d'Officine"));
            typeEtablissementDao.insert(new TypeEtablissement(125l,"Pharmacie Minière"));
            typeEtablissementDao.insert(new TypeEtablissement(126l,"Pharmacie Mutualiste"));
            typeEtablissementDao.insert(new TypeEtablissement(127l,"Pouponnière à Caractère Social"));
            typeEtablissementDao.insert(new TypeEtablissement(128l,"Propharmacie"));
            typeEtablissementDao.insert(new TypeEtablissement(129l,"Protection Maternelle et Infantile (P.M.I.)"));
            typeEtablissementDao.insert(new TypeEtablissement(130l,"Résidence Hôtelière à Vocation Sociale (R.H.V.S.)"));
            typeEtablissementDao.insert(new TypeEtablissement(131l,"Résidences autonomie"));
            typeEtablissementDao.insert(new TypeEtablissement(132l,"Service d'Accompagnement à la Vie Sociale (S.A.V.S.)"));
            typeEtablissementDao.insert(new TypeEtablissement(133l,"Service d'accompagnement médico-social adultes handicapés"));
            typeEtablissementDao.insert(new TypeEtablissement(134l,"Service d'Aide aux Familles en Difficulté"));
            typeEtablissementDao.insert(new TypeEtablissement(135l,"Service d'Aide aux Personnes Agées"));
            typeEtablissementDao.insert(new TypeEtablissement(136l,"Service d'Aide et d'Accompagnement à Domicile (S.A.A.D.)"));
            typeEtablissementDao.insert(new TypeEtablissement(137l,"Service d'Aide Ménagère à Domicile"));
            typeEtablissementDao.insert(new TypeEtablissement(138l,"Service de Réparation Pénale"));
            typeEtablissementDao.insert(new TypeEtablissement(139l,"Service de Repas à Domicile"));
            typeEtablissementDao.insert(new TypeEtablissement(140l,"Service de Soins Infirmiers A Domicile (S.S.I.A.D)"));
            typeEtablissementDao.insert(new TypeEtablissement(141l,"Service de Travailleuses Familiales"));
            typeEtablissementDao.insert(new TypeEtablissement(142l,"Service dédié mesures d'accompagnement social personnalisé"));
            typeEtablissementDao.insert(new TypeEtablissement(143l,"Service d'Éducation Spéciale et de Soins à Domicile"));
            typeEtablissementDao.insert(new TypeEtablissement(144l,"Service délégué aux prestations familiales"));
            typeEtablissementDao.insert(new TypeEtablissement(145l,"Service d'Enquêtes Sociales (S.E.S.)"));
            typeEtablissementDao.insert(new TypeEtablissement(146l,"Service d'information et de soutien aux tuteurs familiaux"));
            typeEtablissementDao.insert(new TypeEtablissement(147l,"Service Educatif Auprès des Tribunaux (S.E.A.T.)"));
            typeEtablissementDao.insert(new TypeEtablissement(148l,"Service Investigation Orientation Educative (S.I.O.E.)"));
            typeEtablissementDao.insert(new TypeEtablissement(149l,"Service mandataire judiciaire à la protection des majeurs"));
            typeEtablissementDao.insert(new TypeEtablissement(150l,"Service Médico-Psychologique Régional (S.M.P.R.)"));
            typeEtablissementDao.insert(new TypeEtablissement(151l,"Service Polyvalent Aide et Soins A Domicile (S.P.A.S.A.D.)"));
            typeEtablissementDao.insert(new TypeEtablissement(152l,"Service Social Polyvalent de Secteur"));
            typeEtablissementDao.insert(new TypeEtablissement(153l,"Service Social Spécialisé ou Polyvalent de Catégorie"));
            typeEtablissementDao.insert(new TypeEtablissement(154l,"Service Tutelle Prestation Sociale"));
            typeEtablissementDao.insert(new TypeEtablissement(155l,"Services AEMO et AED"));
            typeEtablissementDao.insert(new TypeEtablissement(156l,"Structure d'Alternative à la dialyse en centre"));
            typeEtablissementDao.insert(new TypeEtablissement(157l,"Structure Dispensatrice à domicile d'Oxygène à usage médical"));
            typeEtablissementDao.insert(new TypeEtablissement(158l,"Structure Expérimentale en Santé"));
            typeEtablissementDao.insert(new TypeEtablissement(159l,"Syndicat Inter Hospitalier (S.I.H.)"));
            typeEtablissementDao.insert(new TypeEtablissement(160l,"Traitements Spécialisés à Domicile"));
            typeEtablissementDao.insert(new TypeEtablissement(161l,"Unités Evaluation Réentraînement et d'Orient. Soc. et Pro."));
            typeEtablissementDao.insert(new TypeEtablissement(162l,"Village d'Enfants"));
            typeEtablissementDao.insert(new TypeEtablissement(163l,""));
        }
    }

    private boolean comparer(ContactLight medecinLight, Contact medecin){
        boolean bool = true;
        if (medecinLight.getIdPP() != null && medecin.getIdPP() != null) {
            bool = bool && medecinLight.getIdPP().equalsIgnoreCase(medecin.getIdPP());
        }
        if (medecinLight.getAdresse() != null && medecin.getAdresse() != null) {
            bool = bool && medecinLight.getAdresse().equalsIgnoreCase(medecin.getAdresse());
        }
        if (medecinLight.getCp() != null && medecin.getCp() != null) {
            bool = bool && medecinLight.getCp().equalsIgnoreCase(medecin.getCp());
        }
        if (medecinLight.getVille() != null && medecin.getVille() != null) {
            bool = bool && medecinLight.getVille().equalsIgnoreCase(medecin.getVille());
        }
        return bool;
    }

}
