package pulseanddecibels.jp.yamatenki.database.dao;

import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table WIND_AND_TEMPERATURE.
 */
public class WindAndTemperature {

    private Long id;
    private Integer height;
    private Double temperature;
    private Double windVelocity;
    private Double windDirection;
    private long forecastId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient WindAndTemperatureDao myDao;

    private Forecast forecast;
    private Long forecast__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public WindAndTemperature() {
    }

    public WindAndTemperature(Long id) {
        this.id = id;
    }

    public WindAndTemperature(Long id, Integer height, Double temperature, Double windVelocity, Double windDirection, long forecastId) {
        this.id = id;
        this.height = height;
        this.temperature = temperature;
        this.windVelocity = windVelocity;
        this.windDirection = windDirection;
        this.forecastId = forecastId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getWindAndTemperatureDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getWindVelocity() {
        return windVelocity;
    }

    public void setWindVelocity(Double windVelocity) {
        this.windVelocity = windVelocity;
    }

    public Double getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(Double windDirection) {
        this.windDirection = windDirection;
    }

    public long getForecastId() {
        return forecastId;
    }

    public void setForecastId(long forecastId) {
        this.forecastId = forecastId;
    }

    /** To-one relationship, resolved on first access. */
    public Forecast getForecast() {
        long __key = this.forecastId;
        if (forecast__resolvedKey == null || !forecast__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ForecastDao targetDao = daoSession.getForecastDao();
            Forecast forecastNew = targetDao.load(__key);
            synchronized (this) {
                forecast = forecastNew;
            	forecast__resolvedKey = __key;
            }
        }
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        if (forecast == null) {
            throw new DaoException("To-one property 'forecastId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.forecast = forecast;
            forecastId = forecast.getId();
            forecast__resolvedKey = forecastId;
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

    // KEEP METHODS - put your custom methods here
    // KEEP METHODS END

}
