package com.pouillos.finddoctor.entities;


import com.pouillos.finddoctor.dao.DaoSession;
import com.pouillos.finddoctor.dao.DepartementDao;
import com.pouillos.finddoctor.dao.RegionDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToOne;
import com.pouillos.finddoctor.dao.EtablissementLightDao;
import com.pouillos.finddoctor.dao.ContactLightDao;
import com.pouillos.finddoctor.dao.AssociationContactLightEtablissementLightDao;


@Entity
public class AssociationContactLightEtablissementLight implements Comparable<AssociationContactLightEtablissementLight> {

    @Id
    private Long id;

    @NotNull
    private long contactLightId;
    @ToOne(joinProperty = "contactLightId")
    private ContactLight contactLight;

    @NotNull
    private long etablissementLightId;
    @ToOne(joinProperty = "etablissementLightId")
    private EtablissementLight etablissementLight;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1135708594)
    private transient AssociationContactLightEtablissementLightDao myDao;

    @Generated(hash = 817561559)
    public AssociationContactLightEtablissementLight(Long id, long contactLightId, long etablissementLightId) {
        this.id = id;
        this.contactLightId = contactLightId;
        this.etablissementLightId = etablissementLightId;
    }

    @Generated(hash = 484594491)
    public AssociationContactLightEtablissementLight() {
    }

    @Generated(hash = 1340991213)
    private transient Long contactLight__resolvedKey;

    @Generated(hash = 406140306)
    private transient Long etablissementLight__resolvedKey;

    @Override
    public int compareTo(AssociationContactLightEtablissementLight o) {
        return this.id.compareTo(o.id);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getContactLightId() {
        return this.contactLightId;
    }

    public void setContactLightId(long contactLightId) {
        this.contactLightId = contactLightId;
    }

    public long getEtablissementLightId() {
        return this.etablissementLightId;
    }

    public void setEtablissementLightId(long etablissementLightId) {
        this.etablissementLightId = etablissementLightId;
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1199465330)
    public ContactLight getContactLight() {
        long __key = this.contactLightId;
        if (contactLight__resolvedKey == null || !contactLight__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ContactLightDao targetDao = daoSession.getContactLightDao();
            ContactLight contactLightNew = targetDao.load(__key);
            synchronized (this) {
                contactLight = contactLightNew;
                contactLight__resolvedKey = __key;
            }
        }
        return contactLight;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 19894178)
    public void setContactLight(@NotNull ContactLight contactLight) {
        if (contactLight == null) {
            throw new DaoException("To-one property 'contactLightId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.contactLight = contactLight;
            contactLightId = contactLight.getId();
            contactLight__resolvedKey = contactLightId;
        }
    }

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 590394314)
    public EtablissementLight getEtablissementLight() {
        long __key = this.etablissementLightId;
        if (etablissementLight__resolvedKey == null || !etablissementLight__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            EtablissementLightDao targetDao = daoSession.getEtablissementLightDao();
            EtablissementLight etablissementLightNew = targetDao.load(__key);
            synchronized (this) {
                etablissementLight = etablissementLightNew;
                etablissementLight__resolvedKey = __key;
            }
        }
        return etablissementLight;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1928797187)
    public void setEtablissementLight(@NotNull EtablissementLight etablissementLight) {
        if (etablissementLight == null) {
            throw new DaoException(
                    "To-one property 'etablissementLightId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.etablissementLight = etablissementLight;
            etablissementLightId = etablissementLight.getId();
            etablissementLight__resolvedKey = etablissementLightId;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 365748510)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getAssociationContactLightEtablissementLightDao() : null;
    }
}
