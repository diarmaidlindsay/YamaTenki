package pulseanddecibels.jp.yamatenki.database.dao;

import de.greenrobot.dao.DaoException;
import pulseanddecibels.jp.yamatenki.enums.Difficulty;

// THIS CODE IS GENERATED BY greenDAO, EDIT ONLY INSIDE THE "KEEP"-SECTIONS

// KEEP INCLUDES - put your custom includes here
// KEEP INCLUDES END
/**
 * Entity mapped to table FORECAST.
 */
public class Forecast {

    private Long id;
    private Long timestamp;
    private Integer peakTemp;
    private Integer peakVelocity;
    private Integer peakDirection;
    private Integer baseTemp;
    private Integer baseVelocity;
    private Integer baseDirection;
    private Integer weather;
    private Integer temperature;
    private Float precipitation;
    private Integer temperatureHigh;
    private Integer temperatureLow;
    private long mountainId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient ForecastDao myDao;

    private Mountain mountain;
    private Long mountain__resolvedKey;


    // KEEP FIELDS - put your custom fields here
    // KEEP FIELDS END

    public Forecast() {
    }

    public Forecast(Long id) {
        this.id = id;
    }

    public Forecast(Long id, Long timestamp, Integer peakTemp, Integer peakVelocity, Integer peakDirection, Integer baseTemp, Integer baseVelocity, Integer baseDirection, Integer weather, Integer temperature, Float precipitation, Integer temperatureHigh, Integer temperatureLow, long mountainId) {
        this.id = id;
        this.timestamp = timestamp;
        this.peakTemp = peakTemp;
        this.peakVelocity = peakVelocity;
        this.peakDirection = peakDirection;
        this.baseTemp = baseTemp;
        this.baseVelocity = baseVelocity;
        this.baseDirection = baseDirection;
        this.weather = weather;
        this.temperature = temperature;
        this.precipitation = precipitation;
        this.temperatureHigh = temperatureHigh;
        this.temperatureLow = temperatureLow;
        this.mountainId = mountainId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getForecastDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getPeakTemp() {
        return peakTemp;
    }

    public void setPeakTemp(Integer peakTemp) {
        this.peakTemp = peakTemp;
    }

    public Integer getPeakVelocity() {
        return peakVelocity;
    }

    public void setPeakVelocity(Integer peakVelocity) {
        this.peakVelocity = peakVelocity;
    }

    public Integer getPeakDirection() {
        return peakDirection;
    }

    public void setPeakDirection(Integer peakDirection) {
        this.peakDirection = peakDirection;
    }

    public Integer getBaseTemp() {
        return baseTemp;
    }

    public void setBaseTemp(Integer baseTemp) {
        this.baseTemp = baseTemp;
    }

    public Integer getBaseVelocity() {
        return baseVelocity;
    }

    public void setBaseVelocity(Integer baseVelocity) {
        this.baseVelocity = baseVelocity;
    }

    public Integer getBaseDirection() {
        return baseDirection;
    }

    public void setBaseDirection(Integer baseDirection) {
        this.baseDirection = baseDirection;
    }

    public Integer getWeather() {
        return weather;
    }

    public void setWeather(Integer weather) {
        this.weather = weather;
    }

    public Integer getTemperature() {
        return temperature;
    }

    public void setTemperature(Integer temperature) {
        this.temperature = temperature;
    }

    public Float getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(Float precipitation) {
        this.precipitation = precipitation;
    }

    public Integer getTemperatureHigh() {
        return temperatureHigh;
    }

    public void setTemperatureHigh(Integer temperatureHigh) {
        this.temperatureHigh = temperatureHigh;
    }

    public Integer getTemperatureLow() {
        return temperatureLow;
    }

    public void setTemperatureLow(Integer temperatureLow) {
        this.temperatureLow = temperatureLow;
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

    // KEEP METHODS - put your custom methods here
    public Difficulty getDifficultyLevel() {
        float precipitation = getPrecipitation();
        int peakWind = getPeakVelocity();
        int peakTemp = getPeakTemp();

        if (precipitation > 1 || peakWind >= 15 || peakTemp <= -11) {
            return Difficulty.C;
        } else if ((precipitation >= 0.3 && precipitation <= 1) || (peakWind >= 8 && peakWind <= 14) || (peakTemp <= -5 && peakTemp >= -10)) {
            return Difficulty.B;
        } else {
            return Difficulty.A;
        }
    }
    // KEEP METHODS END

}
