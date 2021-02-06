package com.pouillos.finddoctor.entities;



import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

import java.io.Serializable;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TypeEtablissement implements  Comparable<TypeEtablissement> {

    @Id
    private Long id;
    
    @NotNull
    private String name;



    @Generated(hash = 1217155972)
    public TypeEtablissement(Long id, @NotNull String name) {
        this.id = id;
        this.name = name;
    }

    @Generated(hash = 545098318)
    public TypeEtablissement() {
    }



    @Override
    public int compareTo(TypeEtablissement o) {
        return this.name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return name;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
