package com.pouillos.finddoctor.entities;



import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class Region implements Comparable<Region> {

    @Id
    private Long id;

    @NotNull
    private String nom;



    @Generated(hash = 338124540)
    public Region(Long id, @NotNull String nom) {
        this.id = id;
        this.nom = nom;
    }

    @Generated(hash = 600106640)
    public Region() {
    }



        @Override
    public int compareTo(Region o) {
        return this.getId().compareTo(o.getId());
    }

    @Override
    public String toString() {
        return nom;
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
