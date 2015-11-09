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
        Schema schema = new Schema(1, "pulseanddecibels.jp.yamatenki.database.dao");
        Entity mountain = schema.addEntity("Mountain");
        Entity coordinate = schema.addEntity("Coordinate");
        Entity forecast = schema.addEntity("Forecast");
        Entity area = schema.addEntity("Area");
        Entity prefecture = schema.addEntity("Prefecture");
        Entity myMountain = schema.addEntity("MyMountain");
        Entity myMemo = schema.addEntity("MyMemo");

        /**
         Mountain
         **/
        mountain.addIdProperty().autoincrement();
        mountain.addStringProperty("yid");
        mountain.addStringProperty("title");
        mountain.addStringProperty("titleExt");
        mountain.addStringProperty("titleEnglish");
        mountain.addStringProperty("kana");
        Property coordinateId = mountain.addLongProperty("coordinateId").notNull().getProperty();
        Property prefectureId = mountain.addLongProperty("prefectureId").notNull().getProperty();
        Property areaId = mountain.addLongProperty("areaId").notNull().getProperty();
        mountain.addIntProperty("height");
        //one mountain to one area
        mountain.addToOne(area, areaId);
        mountain.addToOne(prefecture, prefectureId);
        mountain.addToOne(coordinate, coordinateId);
        mountain.setHasKeepSections(true);

        /**
         Coordinate
         **/
        coordinate.addIdProperty();
        //one mountain to one coordinate
        coordinate.addFloatProperty("latitude");
        coordinate.addFloatProperty("longitude");

        /**
         Forecast
         **/
        forecast.addIdProperty();
        forecast.addLongProperty("timestamp");
        forecast.addIntProperty("peakTemp");
        forecast.addIntProperty("peakVelocity");
        forecast.addIntProperty("peakDirection");
        forecast.addIntProperty("baseTemp");
        forecast.addIntProperty("baseVelocity");
        forecast.addIntProperty("baseDirection");
        forecast.addIntProperty("weather");
        forecast.addIntProperty("temperature");
        forecast.addFloatProperty("precipitation");
        forecast.addIntProperty("temperatureHigh");
        forecast.addIntProperty("temperatureLow");
        Property mountainIdF = forecast.addLongProperty("mountainId").notNull().getProperty(); //which mountain it belongs to
        //One mountain, Many forecasts
        forecast.addToOne(mountain, mountainIdF);
        mountain.addToMany(forecast, mountainIdF);
        forecast.setHasKeepSections(true);

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

        /**
         * My Mountain
         */
        myMountain.addIdProperty();
        Property mountainIdMy = myMountain.addLongProperty("mountainId").notNull().getProperty();
        myMountain.addToOne(mountain, mountainIdMy);
        myMountain.setHasKeepSections(true);

        /**
         * Mountain Memo
         */
        myMemo.addIdProperty();
        Property mountainIdMemo = myMemo.addLongProperty("mountainId").notNull().getProperty();
        myMemo.addToOne(mountain, mountainIdMemo);
        myMemo.addLongProperty("dateTimeFrom");
        myMemo.addLongProperty("dateTimeUntil");
        myMemo.addStringProperty("weather");
        myMemo.addIntProperty("rating");
        myMemo.addStringProperty("memo");

        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        new DaoGenerator().generateAll(schema, "app/src/main/java");
    }
}
