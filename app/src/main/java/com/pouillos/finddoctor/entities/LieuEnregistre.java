package com.pouillos.finddoctor.entities;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class LieuEnregistre implements Comparable<LieuEnregistre> {

    @Id
    private Long id;

    @NotNull
    private String nom;


    @Generated(hash = 1323543859)
    public LieuEnregistre(Long id, @NotNull String nom) {
        this.id = id;
        this.nom = nom;
    }

    @Generated(hash = 1378548383)
    public LieuEnregistre() {
    }


    @Override
    public String toString() {
        return nom;
    }

    @Override
    public int compareTo(LieuEnregistre o) {
        return this.nom.compareTo(o.nom);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
