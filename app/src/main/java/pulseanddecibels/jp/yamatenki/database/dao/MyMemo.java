package pulseanddecibels.jp.yamatenki.database.dao;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table MY_MEMO.
 */
public class MyMemo {

    private Long id;
    private long mountainId;
    private Long dateTimeFrom;
    private Long dateTimeUntil;
    private String weather;
    private Integer rating;
    private String memo;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient MyMemoDao myDao;

    private Mountain mountain;
    private Long mountain__resolvedKey;


    public MyMemo() {
    }

    public MyMemo(Long id) {
        this.id = id;
    }

    public MyMemo(Long id, long mountainId, Long dateTimeFrom, Long dateTimeUntil, String weather, Integer rating, String memo) {
        this.id = id;
        this.mountainId = mountainId;
        this.dateTimeFrom = dateTimeFrom;
        this.dateTimeUntil = dateTimeUntil;
        this.weather = weather;
        this.rating = rating;
        this.memo = memo;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMyMemoDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getMountainId() {
        return mountainId;
    }

    public void setMountainId(long mountainId) {
        this.mountainId = mountainId;
    }

    public Long getDateTimeFrom() {
        return dateTimeFrom;
    }

    public void setDateTimeFrom(Long dateTimeFrom) {
        this.dateTimeFrom = dateTimeFrom;
    }

    public Long getDateTimeUntil() {
        return dateTimeUntil;
    }

    public void setDateTimeUntil(Long dateTimeUntil) {
        this.dateTimeUntil = dateTimeUntil;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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
