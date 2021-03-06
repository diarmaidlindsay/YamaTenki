package pulseanddecibels.jp.yamatenki.database.dao;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table ETAG.
 */
public class ETag {

    private Long id;
    private String etag;
    private long mountainId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient ETagDao myDao;

    private Mountain mountain;
    private Long mountain__resolvedKey;


    public ETag() {
    }

    public ETag(Long id) {
        this.id = id;
    }

    public ETag(Long id, String etag, long mountainId) {
        this.id = id;
        this.etag = etag;
        this.mountainId = mountainId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getETagDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public long getMountainId() {
        return mountainId;
    }

    public void setMountainId(long mountainId) {
        this.mountainId = mountainId;
    }

    /** To-one relationship, resolved on first access. */
    public Mountain getMountain() {
        long __key = this.mountainId;
        if (mountain__resolvedKey == null || !mountain__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MountainDao targetDao = daoSession.getMountainDao();
            Mountain mountainNew = targetDao.load(__key);
            synchronized (this) {
                mountain = mountainNew;
            	mountain__resolvedKey = __key;
            }
        }
        return mountain;
    }

    public void setMountain(Mountain mountain) {
        if (mountain == null) {
            throw new DaoException("To-one property 'mountainId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.mountain = mountain;
            mountainId = mountain.getId();
            mountain__resolvedKey = mountainId;
        }
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
