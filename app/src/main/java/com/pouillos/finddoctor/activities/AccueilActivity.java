package com.pouillos.finddoctor.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.facebook.stetho.Stetho;
import com.google.android.material.snackbar.Snackbar;
import com.pouillos.finddoctor.R;

import com.pouillos.finddoctor.entities.Contact;
import com.pouillos.finddoctor.entities.ContactIgnore;
import com.pouillos.finddoctor.entities.Departement;
import com.pouillos.finddoctor.entities.Etablissement;
import com.pouillos.finddoctor.entities.ImportContact;
import com.pouillos.finddoctor.entities.ImportEtablissement;
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
        //contactLightDao.deleteAll();

           //importContactDao.deleteAll();

        //delete contactIgnore si numLigne = 1
        List<ContactIgnore> listContactIgnore = contactIgnoreDao.loadAll();
        for (ContactIgnore current : listContactIgnore) {
            if (current.getNumLigne() ==1) {
                contactIgnoreDao.delete(current);
            }
        }

        //delete importContact si termine et sans erreur
        /*List<ImportContact> listAllImportContact = importContactDao.loadAll();
        for (ImportContact current : listAllImportContact) {
            if (current.getImportCompleted() && current.getNbImportIgnore() == 0
                    && current.getNbImportEffectue() == 202) {
                importContactDao.delete(current);
            }
        }*/



     //  etablissementDao.deleteAll();
      //  importEtablissementDao.deleteAll();

        //moins critique
        //professionDao.deleteAll();
       // regionDao.deleteAll();
       // savoirFaireDao.deleteAll();
     //   typeEtablissementDao.deleteAll();
      //  departementDao.deleteAll();
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
            //long cptrContactIgnore = 0l;
            long cptrContactIgnore = contactIgnoreDao.count();

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


                    int readerSize = 20000;
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
                            ContactIgnore contactIgnore = new ContactIgnore(cptrContactIgnore,lineSplitted[1],current.getPath(),nbLigneLue);
                            contactIgnoreDao.insert(contactIgnore);
                            cptrContactIgnore++;
                            continue;
                        }

                        Contact contact = new Contact();
                        //ContactLight contactLight = new ContactLight();
                        contact.setIdPP(lineSplitted[2]);
                        contact.setCodeCivilite(lineSplitted[3]);
                        contact.setNom(lineSplitted[7].toUpperCase());
                        String prenom = "";
                        if (lineSplitted[8].length()>1) {
                            prenom = lineSplitted[8].substring(0,1).toUpperCase()+lineSplitted[8].substring(1,lineSplitted[8].length()-1).toLowerCase();
                        }
                        contact.setPrenom(prenom);
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
                                if (contact.getVille().contains("CEDEX")) {
                                    for (int i=100;i>=0;i--){
                                        contact.setVille(contact.getVille().replace("CEDEX "+i,""));
                                    }
                                    contact.setVille(contact.getVille().replace("CEDEX ",""));
                                    contact.setVille(contact.getVille().replace("CEDEX",""));
                                }
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
                        //List<ContactLight> listContactLight = contactLightDao.queryRaw("where id_pp = ?",lineSplitted[2]);
                        List<Contact> listContact = contactDao.queryRaw("where id_pp = ?",lineSplitted[2]);
                       // if (listContactLight.size()>0) {
                        if (listContact.size()>0) {

                            boolean bool = false;
                            //for (ContactLight currentContactLight : listContactLight){
                            for (Contact currentContact : listContact){
                                //if (comparer(currentContactLight, contact)){
                                if (comparer(currentContact,contact)) {
                                    Log.i("existant","medecin deja cree: "+lineSplitted[2]);
                                    bool = true;
                                    cptrContactIgnore++;
                                    nbLigneLue++;
                                    current.setNbLigneLue(nbLigneLue);
                                    //current.save();
                                    //importContactDao.update(current);
                                    ContactIgnore contactIgnore = new ContactIgnore(cptrContactIgnore,lineSplitted[1],current.getPath(),nbLigneLue);
                                    contactIgnoreDao.insert(contactIgnore);

                                    nbImportIgnore++;
                                    current.setNbImportIgnore(nbImportIgnore);
                                    importContactDao.update(current);
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
                        /*contactLight.setIdPP(contact.getIdPP());
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
                        contactLight.setVille(contact.getVille());*/

                        contactDao.insert(contact);
                        //contactLightDao.insert(contactLight);
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
            for (long i = 0l;i<105;i++) {
                importContactDao.insert(new ImportContact(i, "PS_LibreAcces_Personne_activite_202102020955_"+(int) i+".txt", false, "", "", 0, 0, 0));
                Log.i("enregistre","importContact: "+i);
            }
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



    private boolean comparer(Contact medecin1, Contact medecin2){
        boolean bool = true;
        if (medecin1.getIdPP() != null && medecin2.getIdPP() != null) {
            bool = bool && medecin1.getIdPP().equalsIgnoreCase(medecin2.getIdPP());
        }
        ///////
        if (medecin1.getCodeCivilite() != null && medecin2.getCodeCivilite() != null) {
            bool = bool && medecin1.getCodeCivilite().equalsIgnoreCase(medecin2.getCodeCivilite());
        }
        if (medecin1.getNom() != null && medecin2.getNom() != null) {
            bool = bool && medecin1.getNom().equalsIgnoreCase(medecin2.getNom());
        }
        if (medecin1.getPrenom() != null && medecin2.getPrenom() != null) {
            bool = bool && medecin1.getPrenom().equalsIgnoreCase(medecin2.getPrenom());
        }
        if (medecin1.getProfession() != null && medecin2.getProfession() != null) {
            bool = bool && (medecin1.getProfessionId() == (medecin2.getProfessionId()));
        }
        if (medecin1.getSavoirFaire() != null && medecin2.getSavoirFaire() != null) {
            bool = bool && (medecin1.getSavoirFaireId() == (medecin2.getSavoirFaireId()));
        }
        if (medecin1.getRaisonSocial() != null && medecin2.getRaisonSocial() != null) {
            bool = bool && medecin1.getRaisonSocial().equalsIgnoreCase(medecin2.getRaisonSocial());
        }
        if (medecin1.getComplement() != null && medecin2.getComplement() != null) {
            bool = bool && medecin1.getComplement().equalsIgnoreCase(medecin2.getComplement());
        }
        if (medecin1.getAdresse() != null && medecin2.getAdresse() != null) {
            bool = bool && medecin1.getAdresse().equalsIgnoreCase(medecin2.getAdresse());
        }
        if (medecin1.getCp() != null && medecin2.getCp() != null) {
            bool = bool && medecin1.getCp().equalsIgnoreCase(medecin2.getCp());
        }
        if (medecin1.getVille() != null && medecin2.getVille() != null) {
            bool = bool && medecin1.getVille().equalsIgnoreCase(medecin2.getVille());
        }
        if (medecin1.getTelephone() != null && medecin2.getTelephone() != null) {
            bool = bool && medecin1.getTelephone().equalsIgnoreCase(medecin2.getTelephone());
        }
        if (medecin1.getFax() != null && medecin2.getFax() != null) {
            bool = bool && medecin1.getFax().equalsIgnoreCase(medecin2.getFax());
        }
        if (medecin1.getEmail() != null && medecin2.getEmail() != null) {
            bool = bool && medecin1.getEmail().equalsIgnoreCase(medecin2.getEmail());
        }
        if (medecin1.getDepartement() != null && medecin2.getDepartement() != null) {
            bool = bool && (medecin1.getDepartementId() == (medecin2.getDepartementId()));
        }
        if (medecin1.getRegion() != null && medecin2.getRegion() != null) {
            bool = bool && (medecin1.getRegionId() == (medecin2.getRegionId()));
        }
        //if (medecin1.getLatitude() != null && medecin2.getLatitude() != null) {
            //bool = bool && (medecin1.getLatitude() == medecin2.getLatitude());
        //}
        //if (medecin1.getLongitude() != null && medecin2.getLongitude() != null) {
            //bool = bool && (medecin1.getLongitude() == medecin2.getLongitude());
        //}
        return bool;
    }

}
