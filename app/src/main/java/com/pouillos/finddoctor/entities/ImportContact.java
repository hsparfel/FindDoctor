package com.pouillos.finddoctor.entities;



import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ImportContact {

    @Id
    private Long id;

    @NotNull
    private String path;
    @NotNull
    private boolean importCompleted;

    private String dateDebut;
    private String dateFin;
    private int nbLigneLue;
    private int nbImportEffectue;
    private int nbImportIgnore;
    @Generated(hash = 1098502676)
    public ImportContact(Long id, @NotNull String path, boolean importCompleted,
            String dateDebut, String dateFin, int nbLigneLue, int nbImportEffectue,
            int nbImportIgnore) {
        this.id = id;
        this.path = path;
        this.importCompleted = importCompleted;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.nbLigneLue = nbLigneLue;
        this.nbImportEffectue = nbImportEffectue;
        this.nbImportIgnore = nbImportIgnore;
    }
    @Generated(hash = 535066277)
    public ImportContact() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public boolean getImportCompleted() {
        return this.importCompleted;
    }
    public void setImportCompleted(boolean importCompleted) {
        this.importCompleted = importCompleted;
    }
    public String getDateDebut() {
        return this.dateDebut;
    }
    public void setDateDebut(String dateDebut) {
        this.dateDebut = dateDebut;
    }
    public String getDateFin() {
        return this.dateFin;
    }
    public void setDateFin(String dateFin) {
        this.dateFin = dateFin;
    }
    public int getNbLigneLue() {
        return this.nbLigneLue;
    }
    public void setNbLigneLue(int nbLigneLue) {
        this.nbLigneLue = nbLigneLue;
    }
    public int getNbImportEffectue() {
        return this.nbImportEffectue;
    }
    public void setNbImportEffectue(int nbImportEffectue) {
        this.nbImportEffectue = nbImportEffectue;
    }
    public int getNbImportIgnore() {
        return this.nbImportIgnore;
    }
    public void setNbImportIgnore(int nbImportIgnore) {
        this.nbImportIgnore = nbImportIgnore;
    }

}
