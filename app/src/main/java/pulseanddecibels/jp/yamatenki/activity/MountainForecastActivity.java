package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.database.Database;
import pulseanddecibels.jp.yamatenki.database.dao.Mountain;
import pulseanddecibels.jp.yamatenki.database.dao.MountainDao;
import pulseanddecibels.jp.yamatenki.database.dao.MyMountain;
import pulseanddecibels.jp.yamatenki.database.dao.MyMountainDao;
import pulseanddecibels.jp.yamatenki.model.ForecastArrayElement;
import pulseanddecibels.jp.yamatenki.model.MountainArrayElement;
import pulseanddecibels.jp.yamatenki.model.MountainForecastJSON;
import pulseanddecibels.jp.yamatenki.model.WindAndTemperatureElement;
import pulseanddecibels.jp.yamatenki.utils.DateUtils;
import pulseanddecibels.jp.yamatenki.utils.JSONDownloader;
import pulseanddecibels.jp.yamatenki.utils.JSONParser;
import pulseanddecibels.jp.yamatenki.utils.Settings;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/29.
 * Copyright Pulse and Decibels 2015
 */
public class MountainForecastActivity extends Activity {
    private final SparseIntArray DIFFICULTY_SMALL_IMAGES = new SparseIntArray() {
        {
            append(1, R.drawable.difficulty_small_a);
            append(2, R.drawable.difficulty_small_b);
            append(3, R.drawable.difficulty_small_c);
        }
    };
    private final SparseIntArray DIFFICULTY_BIG_IMAGES = new SparseIntArray() {
        {
            append(1, R.drawable.difficulty_large_a);
            append(2, R.drawable.difficulty_large_b);
            append(3, R.drawable.difficulty_large_c);
        }
    };
    private final List<ForecastScrollViewElement> scrollViewElements = new ArrayList<>();
    private TextView title;
    private ImageView currentDifficultyImage;
    private Button addMyMountainButton;
    private long mountainId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        Bundle arguments = getIntent().getExtras();
        mountainId = arguments.getLong("mountainId");
        MountainDao mountainDao = Database.getInstance(this).getMountainDao();
        Mountain mountain =
                mountainDao.queryBuilder().where(MountainDao.Properties.Id.eq(mountainId)).unique();

        title = (TextView) findViewById(R.id.text_forecast_header);
        title.setTypeface(Utils.getHannariTypeFace(this));
        title.setText(mountain.getTitle());
        currentDifficultyImage = (ImageView) findViewById(R.id.mountain_forecast_current_difficulty);
        TextView currentDifficultyText = (TextView) findViewById(R.id.mountain_forecast_current_difficulty_text);
        currentDifficultyText.setTypeface(Utils.getHannariTypeFace(this));
        ImageView helpIconDifficulty = (ImageView) findViewById(R.id.mountain_forecast_difficulty_help);
        helpIconDifficulty.setOnClickListener(
                getHelpDialogOnClickListener(R.string.help_text_difficulty));
        addMyMountainButton = (Button) findViewById(R.id.button_add_my_mountain);
        addMyMountainButton.setOnClickListener(getAddMyMountainListener());
        addMyMountainButton.setText(getMyMountainForMountainId() == null ?
                getResources().getString(R.string.button_add_my_mountain) :
                getResources().getString(R.string.button_remove_my_mountain));
        addMyMountainButton.setTypeface(Utils.getHannariTypeFace(this));
        Button addMemoButton = (Button) findViewById(R.id.button_add_memo);
        addMemoButton.setTypeface(Utils.getHannariTypeFace(this));
        addMemoButton.setOnClickListener(getAddMemoListener());
        initialiseWidgets();
        populateWidgets(JSONParser.parseMountainForecastFromFile(
                JSONDownloader.getMockMountainForecast(this, mountain.getYid())));
        if(new Settings(this).getSetting("setting_display_warning")) {
            displayWarningDialog();
        }
    }

    private void initialiseWidgets() {
        ScrollView mountainForecastScrollView = (ScrollView) findViewById(R.id.scroll_forecasts);
        LinearLayout todayAMForecast = (LinearLayout) mountainForecastScrollView.findViewById(R.id.today_am_forecast);
        LinearLayout todayPMForecast = (LinearLayout) mountainForecastScrollView.findViewById(R.id.today_pm_forecast);
        LinearLayout tomorrowAMForecast = (LinearLayout) mountainForecastScrollView.findViewById(R.id.tomorrow_am_forecast);
        LinearLayout tomorrowPMForecast = (LinearLayout) mountainForecastScrollView.findViewById(R.id.tomorrow_pm_forecast);
        //add in correct order because index matters for looking up date
        scrollViewElements.add(new ForecastScrollViewElement(todayAMForecast));
        scrollViewElements.add(new ForecastScrollViewElement(todayPMForecast));
        scrollViewElements.add(new ForecastScrollViewElement(tomorrowAMForecast));
        scrollViewElements.add(new ForecastScrollViewElement(tomorrowPMForecast));
        /*
        +2Forecast
        +3Forecast
        +4Forecast
        +5Forecast
        +6Forecast
         */
    }

    private int getRandomDifficulty() {
        final int MIN = 1;
        final int MAX = 3;
        return Utils.getRandomInRange(MIN, MAX);
    }

    /**
     * Return any entries from the MyMountains table
     */
    private MyMountain getMyMountainForMountainId() {
        MyMountainDao myMountainDao = Database.getInstance(MountainForecastActivity.this).getMyMountainDao();
        return myMountainDao.queryBuilder().where(MyMountainDao.Properties.MountainId.eq(mountainId)).unique();
    }

    /**
     * When the user deletes, the button becomes an add button.
     */
    private View.OnClickListener getAddMyMountainListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyMountainDao myMountainDao = Database.getInstance(MountainForecastActivity.this).getMyMountainDao();
                MyMountain myMountain = getMyMountainForMountainId();

                //if the mountain is in the my mountain table, disable the add button
                if (myMountain != null) {
                    myMountainDao.delete(myMountain);
                    addMyMountainButton.setText(getResources().getString(R.string.button_add_my_mountain));
                } else {
                    myMountainDao.insert(new MyMountain(null, mountainId));
                    addMyMountainButton.setText(getResources().getString(R.string.button_remove_my_mountain));
                }

                Intent intent = getIntent();
                intent.putExtra("changedMountain", mountainId);
                setResult(RESULT_OK, intent);
            }
        };
    }

    private View.OnClickListener getAddMemoListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MemoDetailActivity.class);
                intent.putExtra("mountainId", mountainId); //which mountain to make a memo for
                startActivity(intent);
            }
        };
    }

    private void populateWidgets(MountainForecastJSON forecasts) {
        if (forecasts == null) {
            return;
        }
        MountainArrayElement mountainInfo = forecasts.getMountainArrayElement();
        //set forecast page's title
        title.setText(mountainInfo.getTitle());
        //set forecast page's big difficulty image
        currentDifficultyImage.setImageResource(DIFFICULTY_BIG_IMAGES.get(getRandomDifficulty()));

        Map<String, ForecastArrayElement> forecastMap = forecasts.getForecasts();
        //TEMPORARY until we get realtime forecasts
        ForecastArrayElement[] forecastArray = forecastMap.values().toArray(new ForecastArrayElement[forecastMap.values().size()]);
        //iterate through... today morning, today afternoon, tomorrow morning, tomorrow afternoon... etc
        for (int i = 0; i < scrollViewElements.size(); i++) {
            ForecastScrollViewElement scrollViewElement = scrollViewElements.get(i);
            //set ScrollViewElement header
            scrollViewElement.getHeader().setText(DateUtils.getFormattedHeader(i));

            //now depending on the amount of heights, we will display a different amount of rows...
            SparseArray<Integer> heights = forecasts.getHeights();
            String heightPressureTemplate = "高度 %s m付近（%shPa )";
            if (heights.size() == 3 || heights.size() == 2 || heights.size() == 1) {
                int height = heights.keyAt(0);
                String lowHeightPressure = String.format(heightPressureTemplate, height, heights.get(height));
                scrollViewElement.getLowHeightPressure().setText(lowHeightPressure);
            }
            if (heights.size() == 3 || heights.size() == 2) {
                int height = heights.keyAt(1);
                String midHeightPressure = String.format(heightPressureTemplate, height, heights.get(height));
                scrollViewElement.getMidHeightPressure().setText(midHeightPressure);
                scrollViewElement.showMidHeightRow(true);
            }
            if (heights.size() == 3) {
                int height = heights.keyAt(2);
                String highHeightPressure = String.format(heightPressureTemplate, height, heights.get(height));
                scrollViewElement.getHighHeightPressure().setText(highHeightPressure);
                scrollViewElement.showHighHeightRow(true);
            }

            int j = 0; //TEMPORARY until we get realtime forecasts

            for (ForecastColumn forecastColumn : scrollViewElement.getColumns()) {
                //find if there is a matching forecast available from json. If not, whole column is grey.
                String key = DateUtils.timeToMapKey(i, forecastColumn.getColumnId());
//                ForecastArrayElement forecastArrayElement = forecastMap.get(key); //when we get realtime forecasts
                int forecastIndex = j + (i * 4); //TEMPORARY until we get realtime forecasts
                ForecastArrayElement forecastArrayElement = forecastArray[forecastIndex]; //TEMPORARY until we get realtime forecasts
                forecastColumn.getTime().setText(forecastColumn.getColumnId());

                if (forecastArrayElement != null) {
                    List<WindAndTemperatureElement> windAndTemperatureElements =
                            forecastArrayElement.getWindAndTemperatures();
                    //again depending on the amount of heights, we will display a different amount of rows...
                    if (heights.size() == 3 || heights.size() == 2 || heights.size() == 1) {
                        int direction = getDirectionFromDegrees(windAndTemperatureElements.get(0).getWindDirection());
                        int velocity = windAndTemperatureElements.get(0).getWindVelocity();
                        forecastColumn.getLowHeightTemperature().setText(windAndTemperatureElements.get(0).getTemperature());
                        forecastColumn.getLowHeightWindDirection().setImageResource(getWindImage(direction, velocity));
                        forecastColumn.getLowHeightWind().setText(windAndTemperatureElements.get(0).getWindVelocityString());

                    }
                    if (heights.size() == 3 || heights.size() == 2) {
                        int direction = getDirectionFromDegrees(windAndTemperatureElements.get(1).getWindDirection());
                        int velocity = windAndTemperatureElements.get(1).getWindVelocity();
                        forecastColumn.getMidHeightTemperature().setText(windAndTemperatureElements.get(1).getTemperature());
                        forecastColumn.getMidHeightWindDirection().setImageResource(getWindImage(direction, velocity));
                        forecastColumn.getMidHeightWind().setText(windAndTemperatureElements.get(1).getWindVelocityString());
                    }
                    if (heights.size() == 3) {
                        int direction = getDirectionFromDegrees(windAndTemperatureElements.get(2).getWindDirection());
                        int velocity = windAndTemperatureElements.get(2).getWindVelocity();
                        forecastColumn.getHighHeightTemperature().setText(windAndTemperatureElements.get(2).getTemperature());
                        forecastColumn.getHighHeightWindDirection().setImageResource(getWindImage(direction, velocity));
                        forecastColumn.getHighHeightWind().setText(windAndTemperatureElements.get(2).getWindVelocityString());
                    }

                    forecastColumn.getDifficulty().setImageResource(DIFFICULTY_SMALL_IMAGES.get(forecastArrayElement.getMountainStatus()));
                    forecastColumn.getRainLevel().setText(forecastArrayElement.getPrecipitation());
                    forecastColumn.getCloudCover().setText(forecastArrayElement.getTotalCloudCover());
                } else {
                    //set grey background and blank
                    forecastColumn.getDifficulty().setBackgroundColor(Color.GRAY);
                    forecastColumn.getLowHeightTemperature().setBackgroundColor(Color.GRAY);
                    forecastColumn.getLowHeightWind().setBackgroundColor(Color.GRAY);
                    forecastColumn.getMidHeightTemperature().setBackgroundColor(Color.GRAY);
                    forecastColumn.getMidHeightWind().setBackgroundColor(Color.GRAY);
                    forecastColumn.getHighHeightTemperature().setBackgroundColor(Color.GRAY);
                    forecastColumn.getHighHeightWind().setBackgroundColor(Color.GRAY);
                    forecastColumn.getRainLevel().setBackgroundColor(Color.GRAY);
                    forecastColumn.getCloudCover().setBackgroundColor(Color.GRAY);
                }
                j++; //TEMPORARY until we get realtime forecasts
            }
        }
    }

    private int getWindImage(int direction, int velocity) {
        final SparseIntArray GREEN_ARROWS = new SparseIntArray() {
            {
                append(1, R.drawable.arrow_green01);
                append(2, R.drawable.arrow_green02);
                append(3, R.drawable.arrow_green03);
                append(4, R.drawable.arrow_green04);
                append(5, R.drawable.arrow_green05);
                append(6, R.drawable.arrow_green06);
                append(7, R.drawable.arrow_green07);
                append(8, R.drawable.arrow_green08);
            }
        };
        final SparseIntArray BLUE_ARROWS = new SparseIntArray() {
            {
                append(1, R.drawable.arrow_blue01);
                append(2, R.drawable.arrow_blue02);
                append(3, R.drawable.arrow_blue03);
                append(4, R.drawable.arrow_blue04);
                append(5, R.drawable.arrow_blue05);
                append(6, R.drawable.arrow_blue06);
                append(7, R.drawable.arrow_blue07);
                append(8, R.drawable.arrow_blue08);
            }
        };
        final SparseIntArray RED_ARROWS = new SparseIntArray() {
            {
                append(1, R.drawable.arrow_red01);
                append(2, R.drawable.arrow_red02);
                append(3, R.drawable.arrow_red03);
                append(4, R.drawable.arrow_red04);
                append(5, R.drawable.arrow_red05);
                append(6, R.drawable.arrow_red06);
                append(7, R.drawable.arrow_red07);
                append(8, R.drawable.arrow_red08);
            }
        };

        if (velocity < 7) {
            return GREEN_ARROWS.get(direction);
        } else if (velocity < 15) {
            return BLUE_ARROWS.get(direction);
        } else {
            return RED_ARROWS.get(direction);
        }
    }

    private int getDirectionFromDegrees(int degrees) {
        if (((degrees >= 337.5) && (degrees <= 360)) || (degrees >= 0 & degrees < 22.5)) {
            return 1;
        } else if (degrees >= 22.5 && degrees < 67.5) {
            return 2;
        } else if (degrees >= 67.5 && degrees < 112.5) {
            return 3;
        } else if (degrees >= 112.5 && degrees < 157.5) {
            return 4;
        } else if (degrees >= 157.5 && degrees < 202.5) {
            return 5;
        } else if (degrees >= 202.5 && degrees < 247.5) {
            return 6;
        } else if (degrees >= 247.5 && degrees < 292.5) {
            return 7;
        } else if (degrees >= 292.5 && degrees < 337.5) {
            return 8;
        }
        return 0;
    }

    public View.OnClickListener getHelpDialogOnClickListener(final int stringId) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String text = MountainForecastActivity.this.getResources().getString(stringId);
                final Dialog dialog = new Dialog(MountainForecastActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_help);
                TextView helpText = (TextView) dialog.findViewById(R.id.help_text);
                helpText.setText(text);
                dialog.setCanceledOnTouchOutside(true);
                Drawable d = new ColorDrawable(getResources().getColor(R.color.yama_brown));
                d.setAlpha(200);
                dialog.getWindow().setBackgroundDrawable(d);
                dialog.show();
            }
        };
    }

    public void displayWarningDialog() {
        final String text = MountainForecastActivity.this.getResources().getString(R.string.text_forecast_warning);
        final Dialog dialog = new Dialog(MountainForecastActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_help);
        TextView helpText = (TextView) dialog.findViewById(R.id.help_text);
        helpText.setText(text);
        dialog.setCanceledOnTouchOutside(true);
        Drawable d = new ColorDrawable(getResources().getColor(R.color.yama_brown));
        d.setAlpha(200);
        dialog.getWindow().setBackgroundDrawable(d);
        dialog.show();
    }

    // For now this is used for "Today" and "Tomorrow", ie, short term forecasts
    private class ForecastScrollViewElement {
        private final String[] TIME_INCREMENTS = {"00", "03", "06", "09"}; // column headers
        private final TextView header;
        private final List<ForecastColumn> columns = new ArrayList<>();
        private final TextView lowHeightPressure; //1000m
        private final TextView midHeightPressure; //2000m
        private final TextView highHeightPressure; //3000m

        private final TableRow lowHeightPressureRow;
        private final TableRow midHeightPressureRow;
        private final TableRow highHeightPressureRow;

        private final TableRow lowTempRow;
        private final TableRow lowWindRow;
        private final ImageView lowWindHelp;
        private final TableRow midTempRow;
        private final TableRow midWindRow;
        private final ImageView midWindHelp;
        private final TableRow highTempRow;
        private final TableRow highWindRow;
        private final ImageView highWindHelp;
        private final ImageView cloudCoverHelp;

        public ForecastScrollViewElement(LinearLayout forecast) {
            header = (TextView) forecast.findViewById(R.id.forecast_header);
            TableLayout forecastTable = (TableLayout) forecast.findViewById(R.id.forecast_table);

            lowTempRow = (TableRow) forecastTable.findViewById(R.id.forecast_low_row_temperature);
            lowWindRow = (TableRow) forecastTable.findViewById(R.id.forecast_low_row_wind);
            lowWindHelp = (ImageView) lowWindRow.findViewById(R.id.help_icon_wind_speed);
            lowWindHelp.setOnClickListener(getHelpDialogOnClickListener(R.string.help_text_wind_speed));

            midTempRow = (TableRow) forecastTable.findViewById(R.id.forecast_mid_row_temperature);
            midWindRow = (TableRow) forecastTable.findViewById(R.id.forecast_mid_row_wind);
            midWindHelp = (ImageView) midWindRow.findViewById(R.id.help_icon_wind_speed);
            midWindHelp.setOnClickListener(getHelpDialogOnClickListener(R.string.help_text_wind_speed));

            highTempRow = (TableRow) forecastTable.findViewById(R.id.forecast_high_row_temperature);
            highWindRow = (TableRow) forecastTable.findViewById(R.id.forecast_high_row_wind);
            highWindHelp = (ImageView) highWindRow.findViewById(R.id.help_icon_wind_speed);
            highWindHelp.setOnClickListener(getHelpDialogOnClickListener(R.string.help_text_wind_speed));
            cloudCoverHelp = (ImageView) forecastTable.findViewById(R.id.help_icon_cloud_cover);
            cloudCoverHelp.setOnClickListener(getHelpDialogOnClickListener(R.string.help_text_cloud_cover));

            for (String time : TIME_INCREMENTS) {
                columns.add(new ForecastColumn(forecastTable, lowTempRow, lowWindRow, midTempRow, midWindRow, highTempRow, highWindRow, time));
            }

            lowHeightPressureRow = (TableRow) forecastTable.findViewById(R.id.forecast_low_row_height);
            lowHeightPressure = (TextView) lowHeightPressureRow.findViewById(R.id.forecast_height_pressure);
            midHeightPressureRow = (TableRow) forecastTable.findViewById(R.id.forecast_mid_row_height);
            midHeightPressure = (TextView) midHeightPressureRow.findViewById(R.id.forecast_height_pressure);
            highHeightPressureRow = (TableRow) forecastTable.findViewById(R.id.forecast_high_row_height);
            highHeightPressure = (TextView) highHeightPressureRow.findViewById(R.id.forecast_height_pressure);

            showMidHeightRow(false); //hide by default (assume small mountain)
            showHighHeightRow(false); //hide by default (assume small mountain)
        }

        public void showMidHeightRow(boolean show) {
            int visibility;
            if (show) {
                visibility = View.VISIBLE;
            } else {
                visibility = View.GONE;
            }
            midTempRow.setVisibility(visibility);
            midWindRow.setVisibility(visibility);
            midHeightPressureRow.setVisibility(visibility);
        }

        public void showHighHeightRow(boolean show) {
            int visibility;
            if (show) {
                visibility = View.VISIBLE;
            } else {
                visibility = View.GONE;
            }
            highTempRow.setVisibility(visibility);
            highWindRow.setVisibility(visibility);
            highHeightPressureRow.setVisibility(visibility);
        }

        public TextView getHeader() {
            return header;
        }

        public List<ForecastColumn> getColumns() {
            return columns;
        }

        public TextView getLowHeightPressure() {
            return lowHeightPressure;
        }

        public TextView getMidHeightPressure() {
            return midHeightPressure;
        }

        public TextView getHighHeightPressure() {
            return highHeightPressure;
        }
    }

    private class ForecastColumn {
        private final String columnId;
        private final TextView time;
        private final ImageView difficulty;
        private final TextView lowHeightTemperature;
        private final TextView lowHeightWind;
        private final ImageView lowHeightWindDirection;
        private final TextView midHeightTemperature;
        private final TextView midHeightWind;
        private final ImageView midHeightWindDirection;
        private final TextView highHeightTemperature;
        private final TextView highHeightWind;
        private final ImageView highHeightWindDirection;
        private final TextView rainLevel;
        private final TextView cloudCover;

        public ForecastColumn(TableLayout forecastTable, TableRow lowTemp, TableRow lowWind, TableRow midTemp, TableRow midWind, TableRow highTemp, TableRow highWind, String columnId) {
            this.columnId = columnId;
            time = (TextView) forecastTable.findViewById(getResources().getIdentifier("forecast_time_" + columnId, "id", getPackageName()));
            difficulty = (ImageView) forecastTable.findViewById(getResources().getIdentifier("forecast_difficulty_" + columnId, "id", getPackageName()));
            lowHeightTemperature = (TextView) lowTemp.findViewById(getResources().getIdentifier("forecast_temp_" + columnId, "id", getPackageName()));
            lowHeightWindDirection = (ImageView) lowWind.findViewById(getResources().getIdentifier("forecast_wind_direction_" + columnId, "id", getPackageName()));
            lowHeightWind = (TextView) lowWind.findViewById(getResources().getIdentifier("forecast_wind_" + columnId, "id", getPackageName()));
            midHeightTemperature = (TextView) midTemp.findViewById(getResources().getIdentifier("forecast_temp_" + columnId, "id", getPackageName()));
            midHeightWindDirection = (ImageView) midWind.findViewById(getResources().getIdentifier("forecast_wind_direction_" + columnId, "id", getPackageName()));
            midHeightWind = (TextView) midWind.findViewById(getResources().getIdentifier("forecast_wind_" + columnId, "id", getPackageName()));
            highHeightTemperature = (TextView) highTemp.findViewById(getResources().getIdentifier("forecast_temp_" + columnId, "id", getPackageName()));
            highHeightWindDirection = (ImageView) highWind.findViewById(getResources().getIdentifier("forecast_wind_direction_" + columnId, "id", getPackageName()));
            highHeightWind = (TextView) highWind.findViewById(getResources().getIdentifier("forecast_wind_" + columnId, "id", getPackageName()));
            rainLevel = (TextView) forecastTable.findViewById(getResources().getIdentifier("forecast_rain_level_" + columnId, "id", getPackageName()));
            cloudCover = (TextView) forecastTable.findViewById(getResources().getIdentifier("forecast_cloud_cover_" + columnId, "id", getPackageName()));
        }

        public String getColumnId() {
            return columnId;
        }

        public TextView getTime() {
            return time;
        }

        public ImageView getDifficulty() {
            return difficulty;
        }

        public TextView getLowHeightTemperature() {
            return lowHeightTemperature;
        }

        public TextView getLowHeightWind() {
            return lowHeightWind;
        }

        public TextView getMidHeightTemperature() {
            return midHeightTemperature;
        }

        public TextView getMidHeightWind() {
            return midHeightWind;
        }

        public TextView getHighHeightTemperature() {
            return highHeightTemperature;
        }

        public TextView getHighHeightWind() {
            return highHeightWind;
        }

        public TextView getRainLevel() {
            return rainLevel;
        }

        public TextView getCloudCover() {
            return cloudCover;
        }

        public ImageView getLowHeightWindDirection() {
            return lowHeightWindDirection;
        }

        public ImageView getMidHeightWindDirection() {
            return midHeightWindDirection;
        }

        public ImageView getHighHeightWindDirection() {
            return highHeightWindDirection;
        }
    }
}
