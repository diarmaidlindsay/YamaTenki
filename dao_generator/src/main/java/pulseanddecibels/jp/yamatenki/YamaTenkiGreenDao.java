package pulseanddecibels.jp.yamatenki;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

/**
 * This is where we make the schema for 山天気.
 * Run this to generate the DAO files.
 */
public class YamaTenkiGreenDao {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(5, "pulseanddecibels.jp.yamatenki.database.dao");
        Entity mountain = schema.addEntity("Mountain");
        Entity coordinate = schema.addEntity("Coordinate");
        Entity forecast = schema.addEntity("Forecast");
        Entity windAndTemperature = schema.addEntity("WindAndTemperature");
        Entity pressure = schema.addEntity("Pressure");
        Entity area = schema.addEntity("Area");
        Entity prefecture = schema.addEntity("Prefecture");
        Entity myMountain = schema.addEntity("MyMountain");
        Entity myMemo = schema.addEntity("MyMemo");
        Entity status = schema.addEntity("Status");
        Entity checklistItem = schema.addEntity("CheckListItem");
        Entity eTag = schema.addEntity("ETag");

        /**
         Mountain
         **/
        mountain.addIdProperty().getProperty();
        mountain.addStringProperty("yid");
        mountain.addStringProperty("title");
        mountain.addStringProperty("titleExt");
        mountain.addStringProperty("titleSplitted_1");
        mountain.addStringProperty("titleSplitted_2");
        mountain.addStringProperty("titleEnglish");
        mountain.addStringProperty("titleSplittedEnglish_1");
        mountain.addStringProperty("titleSplittedEnglish_2");
        mountain.addStringProperty("kana");
        mountain.addStringProperty("referenceCity");
        mountain.addStringProperty("referenceCityEnglish");
        Property prefectureId = mountain.addLongProperty("prefectureId").notNull().getProperty();
        Property areaId = mountain.addLongProperty("areaId").notNull().getProperty();
        mountain.addIntProperty("height");
        mountain.addBooleanProperty("topMountain");
        //one mountain to one area
        mountain.addToOne(area, areaId);
        mountain.addToOne(prefecture, prefectureId);
        mountain.setHasKeepSections(true);

        /**
         Coordinate
         **/
        coordinate.addIdProperty();
        //one mountain to one coordinate
        Property mountainIdCoordinate = coordinate.addLongProperty("mountainId").notNull().getProperty();
        coordinate.addToOne(mountain, mountainIdCoordinate);
        coordinate.addFloatProperty("latitude");
        coordinate.addFloatProperty("longitude");
        coordinate.setHasKeepSections(true);

        /**
         Pressure
         */
        pressure.addIdProperty();
        pressure.addIntProperty("height");
        pressure.addIntProperty("pressure");
        Property mountainIdPressure = pressure.addLongProperty("mountainId").notNull().getProperty();
        //One mountain, many Pressures
        pressure.addToOne(mountain, mountainIdPressure);
        mountain.addToMany(pressure, mountainIdPressure);

        /**
         Etag
         */
        eTag.addIdProperty();
        eTag.addStringProperty("etag");
        Property mountainIdETag = eTag.addLongProperty("mountainId").notNull().getProperty();
        eTag.addToOne(mountain, mountainIdETag);

        /**
         Forecast
         **/
        forecast.addIdProperty();
        forecast.addDoubleProperty("temperature");
        forecast.addDoubleProperty("precipitation");
        forecast.addDoubleProperty("totalCloudCover");
        forecast.addIntProperty("mountainStatus");
        forecast.addStringProperty("dateTime");
        forecast.addBooleanProperty("daily");
        Property mountainIdForecast = forecast.addLongProperty("mountainId").notNull().getProperty(); //which mountain it belongs to
        //One mountain, Many forecasts
        forecast.addToOne(mountain, mountainIdForecast);
        mountain.addToMany(forecast, mountainIdForecast);
        forecast.setHasKeepSections(true);

        /**
         WindAndTemperature
         **/
        windAndTemperature.addIdProperty();
        windAndTemperature.addIntProperty("height"); //low (0), mid (1), high (2)
        windAndTemperature.addDoubleProperty("temperature");
        windAndTemperature.addDoubleProperty("windVelocity");
        windAndTemperature.addDoubleProperty("windDirection");
        Property forecastIdWindAndTemperature = windAndTemperature.addLongProperty("forecastId").notNull().getProperty();
        //One forecast, many WindAndTemperatures
        windAndTemperature.addToOne(forecast, forecastIdWindAndTemperature);
        forecast.addToMany(windAndTemperature, forecastIdWindAndTemperature);
        windAndTemperature.setHasKeepSections(true);

        /**
         Area
         **/
        area.addIdProperty();
        area.addStringProperty("name");

        /**
         * Prefecture
         */
        prefecture.addIdProperty();
        prefecture.addStringProperty("name");
        prefecture.addStringProperty("nameEnglish");

        /**
         * My Mountain
         */
        myMountain.addIdProperty();
        Property mountainIdMyMountain = myMountain.addLongProperty("mountainId").notNull().getProperty();
        myMountain.addToOne(mountain, mountainIdMyMountain);
        myMountain.setHasKeepSections(true);

        /**
         * Mountain Memo
         */
        myMemo.addIdProperty();
        Property mountainIdMemo = myMemo.addLongProperty("mountainId").notNull().getProperty();
        myMemo.addToOne(mountain, mountainIdMemo);
        mountain.addToMany(myMemo, mountainIdMemo);
        myMemo.addLongProperty("dateTimeFrom");
        myMemo.addLongProperty("dateTimeUntil");
        myMemo.addStringProperty("weather");
        myMemo.addIntProperty("rating");
        myMemo.addStringProperty("memo");

        /**
         * Current Mountain Status
         */
        status.addIdProperty().getProperty();
        Property mountainIdStatus = status.addLongProperty("mountainId").notNull().getProperty();
        status.addToOne(mountain, mountainIdStatus);
        status.addIntProperty("status");
        status.setHasKeepSections(true);

        /**
         * Checklist Item
         */
        checklistItem.addIdProperty();
        checklistItem.addStringProperty("text");
        checklistItem.addBooleanProperty("checked");

        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        new DaoGenerator().generateAll(schema, "app/src/main/java");
    }
}
