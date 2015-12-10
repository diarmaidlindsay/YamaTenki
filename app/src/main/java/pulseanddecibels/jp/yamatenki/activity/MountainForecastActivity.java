package pulseanddecibels.jp.yamatenki.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.protocol.HTTP;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.database.Database;
import pulseanddecibels.jp.yamatenki.database.dao.Coordinate;
import pulseanddecibels.jp.yamatenki.database.dao.CoordinateDao;
import pulseanddecibels.jp.yamatenki.database.dao.Forecast;
import pulseanddecibels.jp.yamatenki.database.dao.ForecastDao;
import pulseanddecibels.jp.yamatenki.database.dao.Mountain;
import pulseanddecibels.jp.yamatenki.database.dao.MountainDao;
import pulseanddecibels.jp.yamatenki.database.dao.MyMountain;
import pulseanddecibels.jp.yamatenki.database.dao.MyMountainDao;
import pulseanddecibels.jp.yamatenki.database.dao.Pressure;
import pulseanddecibels.jp.yamatenki.database.dao.PressureDao;
import pulseanddecibels.jp.yamatenki.database.dao.WindAndTemperature;
import pulseanddecibels.jp.yamatenki.interfaces.OnDownloadComplete;
import pulseanddecibels.jp.yamatenki.model.ForecastArrayElement;
import pulseanddecibels.jp.yamatenki.model.MountainForecastJSON;
import pulseanddecibels.jp.yamatenki.utils.DateUtils;
import pulseanddecibels.jp.yamatenki.utils.JSONDownloader;
import pulseanddecibels.jp.yamatenki.utils.JSONParser;
import pulseanddecibels.jp.yamatenki.utils.Settings;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/29.
 * Copyright Pulse and Decibels 2015
 */
public class MountainForecastActivity extends Activity implements OnDownloadComplete {
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
    private Button addMyMountainButton;
    private long mountainId;
    private MapView mMapView;
    private CallbackManager callbackManager;
    private LoginManager loginManager;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        Bundle arguments = getIntent().getExtras();
        mountainId = arguments.getLong("mountainId");
        MountainDao mountainDao = Database.getInstance(this).getMountainDao();
        final Mountain mountain = mountainDao.load(mountainId);

        title = (TextView) findViewById(R.id.text_forecast_header);
        title.setTypeface(Utils.getHannariTypeFace(this));
        title.setText(mountain.getTitle());
        ImageView currentDifficultyImage = (ImageView) findViewById(R.id.mountain_forecast_current_difficulty);
        currentDifficultyImage.setImageResource(DIFFICULTY_BIG_IMAGES.get(mountain.getStatus()));
        TextView currentDifficultyText = (TextView) findViewById(R.id.mountain_forecast_current_difficulty_text);
        currentDifficultyText.setTypeface(Utils.getHannariTypeFace(this));
        ImageView helpIconDifficulty = (ImageView) findViewById(R.id.mountain_forecast_difficulty_help);
        helpIconDifficulty.setOnClickListener(
                getHelpDialogOnClickListener(R.string.help_text_difficulty));
        ImageView iconFacebook = (ImageView) findViewById(R.id.icon_facebook);
        iconFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayFacebookDialog();
            }
        });
        ImageView iconLine = (ImageView) findViewById(R.id.icon_line);
        iconLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInstalledLINE()) {
                    try {
                        shareWithLINE();
                    } catch (URISyntaxException | IOException e) {
                        Toast.makeText(MountainForecastActivity.this,
                                getString(R.string.toast_facebook_post_fail), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    displayInstallSNSDialog("LINE", "market://details?id=jp.naver.line.android");
                }
            }
        });
        ImageView iconTwitter = (ImageView) findViewById(R.id.icon_twitter);
        iconTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInstalledTwitter()) {
                    try {
                        shareWithTwitter();
                    } catch (URISyntaxException | IOException e) {
                        Toast.makeText(MountainForecastActivity.this,
                                getString(R.string.toast_facebook_post_fail), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    displayInstallSNSDialog("Twitter", "market://details?id=com.twitter.android");
                }
            }
        });
        ImageView iconMail = (ImageView) findViewById(R.id.icon_mail);
        iconMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    shareWithMail();
                } catch (URISyntaxException | IOException e) {
                    Toast.makeText(MountainForecastActivity.this,
                            getString(R.string.toast_facebook_post_fail), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        ImageView iconMaps = (ImageView) findViewById(R.id.icon_maps);
        iconMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMapDialog(savedInstanceState);
            }
        });
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
        JSONDownloader.getMountainForecastFromServer(this, mountain.getYid(), this);
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

    private Map<String, Forecast> getMappedForecasts() {
        Map<String, Forecast> forecastMap = new HashMap<>();
        ForecastDao forecastDao = Database.getInstance(this).getForecastDao();
        List<Forecast> forecastList = forecastDao.queryBuilder().where(ForecastDao.Properties.MountainId.eq(mountainId)).list();
        for (Forecast forecast : forecastList) {
            String dateTimeString = forecast.getDateTime();
            DateTime dateTime = DateUtils.getDateTimeFromForecast(dateTimeString);
            forecastMap.put(DateUtils.getDateToForecastKey(dateTime), forecast);
        }

        return forecastMap;
    }

    /**
     * For example if it's 2pm we don't want to see this morning's forecast anymore
     */
    private void hideWidgetsWithOldData() {
        for(ForecastScrollViewElement scrollViewElement : scrollViewElements) {
            boolean allEmpty = true;
            for(ForecastColumn column : scrollViewElement.getColumns()) {
                if(!column.getLowHeightWind().getText().equals("") ||
                        column.getLowHeightWindDirection().getDrawable() != null ||
                        !column.getLowHeightTemperature().getText().equals("")) {
                    allEmpty = false;
                    break;
                }
            }
            if(allEmpty) {
                scrollViewElement.getLayout().setVisibility(View.GONE);
            }
        }

        //make the very top scrollview have a padding of 10
        for(ForecastScrollViewElement scrollViewElement : scrollViewElements) {
            if(scrollViewElement.getLayout().getVisibility() != View.GONE) {
                int left = scrollViewElement.getLayout().getPaddingLeft();
                int right = scrollViewElement.getLayout().getPaddingRight();
                int bottom = scrollViewElement.getLayout().getPaddingBottom();

                scrollViewElement.getLayout().setPadding(left, 10, right, bottom);
                break;
            }
        }
    }

    private void populateWidgetsFromDatabase() {
        MountainDao mountainDao = Database.getInstance(this).getMountainDao();
        PressureDao pressureDao = Database.getInstance(this).getPressureDao();
        final Mountain mountain = mountainDao.load(mountainId);

        title.setText(mountain.getTitle());
        Map<String, Forecast> forecastMap = getMappedForecasts();
        List<Pressure> heights = pressureDao.queryBuilder().where(PressureDao.Properties.MountainId.eq(mountainId)).list();

        for (int i = 0; i < scrollViewElements.size(); i++) {
            ForecastScrollViewElement scrollViewElement = scrollViewElements.get(i);
            //set ScrollViewElement header
            scrollViewElement.getHeader().setText(DateUtils.getFormattedHeader(i));

            //now depending on the amount of heights, we will display a different amount of rows...
            String heightPressureTemplate = "高度 %s m付近（%shPa )";
            if (heights.size() == 3 || heights.size() == 2 || heights.size() == 1) {
                int height = heights.get(0).getHeight();
                int pressure = heights.get(0).getPressure();
                String lowHeightPressure = String.format(heightPressureTemplate, height, pressure);
                scrollViewElement.getLowHeightPressure().setText(lowHeightPressure);
            }
            if (heights.size() == 3 || heights.size() == 2) {
                int height = heights.get(1).getHeight();
                int pressure = heights.get(1).getPressure();
                String midHeightPressure = String.format(heightPressureTemplate, height, pressure);
                scrollViewElement.getMidHeightPressure().setText(midHeightPressure);
                scrollViewElement.showMidHeightRow(true);
            }
            if (heights.size() == 3) {
                int height = heights.get(2).getHeight();
                int pressure = heights.get(2).getPressure();
                String highHeightPressure = String.format(heightPressureTemplate, height, pressure);
                scrollViewElement.getHighHeightPressure().setText(highHeightPressure);
                scrollViewElement.showHighHeightRow(true);
            }

            for (ForecastColumn forecastColumn : scrollViewElement.getColumns()) {
                String key = DateUtils.getWidgetToForecastKey(i, forecastColumn.getColumnIndex());
                Forecast forecast = forecastMap.get(key);

                forecastColumn.getTime().setText(DateUtils.getColumnHeadingFor(i, forecastColumn.getColumnIndex()));

                if (forecast != null) {
                    //we found a matching forecast
                    forecastColumn.getDifficulty().setImageResource(DIFFICULTY_SMALL_IMAGES.get(forecast.getMountainStatus()));
                    List<WindAndTemperature> windAndTemperatureList = forecast.getWindAndTemperatureList();
                    //again depending on the amount of heights, we will display a different amount of rows...
                    if (heights.size() == 3 || heights.size() == 2 || heights.size() == 1) {
                        int direction = getDirectionFromDegrees(windAndTemperatureList.get(0).getWindDirection());
                        int velocity = windAndTemperatureList.get(0).getWindVelocity().intValue();
                        forecastColumn.getLowHeightTemperature().setText(String.format("%d", windAndTemperatureList.get(0).getTemperature().intValue()));
                        forecastColumn.getLowHeightWindDirection().setImageResource(getWindImage(direction, velocity));
                        forecastColumn.getLowHeightWind().setText(String.format("%d", windAndTemperatureList.get(0).getWindVelocity().intValue()));

                    }
                    if (heights.size() == 3 || heights.size() == 2) {
                        int direction = getDirectionFromDegrees(windAndTemperatureList.get(1).getWindDirection());
                        int velocity = windAndTemperatureList.get(1).getWindVelocity().intValue();
                        forecastColumn.getMidHeightTemperature().setText(String.format("%d", windAndTemperatureList.get(1).getTemperature().intValue()));
                        forecastColumn.getMidHeightWindDirection().setImageResource(getWindImage(direction, velocity));
                        forecastColumn.getMidHeightWind().setText(String.format("%d", windAndTemperatureList.get(1).getWindVelocity().intValue()));
                    }
                    if (heights.size() == 3) {
                        int direction = getDirectionFromDegrees(windAndTemperatureList.get(2).getWindDirection());
                        int velocity = windAndTemperatureList.get(2).getWindVelocity().intValue();
                        forecastColumn.getHighHeightTemperature().setText(String.format("%d", windAndTemperatureList.get(2).getTemperature().intValue()));
                        forecastColumn.getHighHeightWindDirection().setImageResource(getWindImage(direction, velocity));
                        forecastColumn.getHighHeightWind().setText(String.format("%d", windAndTemperatureList.get(2).getWindVelocity().intValue()));
                    }

                    forecastColumn.getRainLevel().setText(forecast.getPrecipitation() < 0.1 ? "0" : String.format("%.1f", forecast.getPrecipitation()));
                    forecastColumn.getCloudCover().setText(String.format("%d", forecast.getTotalCloudCover().intValue() / 10));

                } else {
                    //set grey background and blank
                    forecastColumn.getDifficulty().setBackgroundColor(Color.LTGRAY);
                    forecastColumn.getLowHeightTemperature().setBackgroundColor(Color.LTGRAY);
                    forecastColumn.getLowHeightWindDirection().setBackgroundColor(Color.LTGRAY);
                    forecastColumn.getLowHeightWind().setBackgroundColor(Color.LTGRAY);
                    forecastColumn.getMidHeightTemperature().setBackgroundColor(Color.LTGRAY);
                    forecastColumn.getMidHeightWindDirection().setBackgroundColor(Color.LTGRAY);
                    forecastColumn.getMidHeightWind().setBackgroundColor(Color.LTGRAY);
                    forecastColumn.getHighHeightTemperature().setBackgroundColor(Color.LTGRAY);
                    forecastColumn.getHighHeightWindDirection().setBackgroundColor(Color.LTGRAY);
                    forecastColumn.getHighHeightWind().setBackgroundColor(Color.LTGRAY);
                    forecastColumn.getRainLevel().setBackgroundColor(Color.LTGRAY);
                    forecastColumn.getCloudCover().setBackgroundColor(Color.LTGRAY);
                }
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

        if (velocity < 8) {
            return GREEN_ARROWS.get(direction);
        } else if (velocity < 15) {
            return BLUE_ARROWS.get(direction);
        } else {
            return RED_ARROWS.get(direction);
        }
    }

    private int getDirectionFromDegrees(double degrees) {
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
                Drawable d = new ColorDrawable(ContextCompat.getColor(MountainForecastActivity.this, R.color.yama_brown));
                d.setAlpha(200);
                dialog.getWindow().setBackgroundDrawable(d);
                dialog.show();
            }
        };
    }

    private void displayWarningDialog() {
        final String text = MountainForecastActivity.this.getResources().getString(R.string.text_forecast_warning);
        final Dialog dialog = new Dialog(MountainForecastActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_help);
        TextView helpText = (TextView) dialog.findViewById(R.id.help_text);
        helpText.setText(text);
        dialog.setCanceledOnTouchOutside(true);
        Drawable d = new ColorDrawable(ContextCompat.getColor(this, R.color.yama_brown));
        d.setAlpha(200);
        dialog.getWindow().setBackgroundDrawable(d);
        dialog.show();
    }

    private void displayMapDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(MountainForecastActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_map);
        dialog.setCanceledOnTouchOutside(true);
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mMapView = (MapView) dialog.findViewById(R.id.mapview);
        //must call the whole lifecycle http://www.matt-reid.co.uk/blog_post.php?id=93
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        MapsInitializer.initialize(MountainForecastActivity.this);
        GoogleMap map = mMapView.getMap();

        MountainDao mountainDao = Database.getInstance(this).getMountainDao();
        CoordinateDao coordinateDao = Database.getInstance(this).getCoordinateDao();
        Mountain mountain = mountainDao.load(mountainId);
        Coordinate coordinate =
                coordinateDao.queryBuilder().where(CoordinateDao.Properties.MountainId.eq(mountainId)).unique();

        LatLng latLng = new LatLng(coordinate.getLatitude(), coordinate.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        map.moveCamera(cameraUpdate);
        map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.mountain_axe_pin))
                .snippet(mountain.getHeight() + "m")
                .title(mountain.getTitleExt())).showInfoWindow();

        dialog.show();
    }

    private void displayFacebookDialog() {
        final Dialog dialog = new Dialog(MountainForecastActivity.this, R.style.SocialDialog);
        dialog.setContentView(R.layout.dialog_facebook);
        dialog.setTitle(getResources().getString(R.string.dialog_facebook));
        dialog.setCanceledOnTouchOutside(true);

        Button postButton = (Button) dialog.findViewById(R.id.facebook_post_button);
        final EditText postText = (EditText) dialog.findViewById(R.id.facebook_post_text);
        ImageView postPreview = (ImageView) dialog.findViewById(R.id.facebook_image_preview);

        final Bitmap composite = createForecastComposite();
        postPreview.setImageBitmap(composite);

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postToFacebook(postText.getText().toString(), composite);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * When user hasn't got Twitter or LINE installed
     * Prompt them to download
     */
    private void displayInstallSNSDialog(String sns, final String playStorePackage) {
        final Dialog dialog = new Dialog(MountainForecastActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_alert);
        String installTextString = String.format(getString(R.string.text_dialog_sns_not_installed), sns);
        TextView installText = (TextView) dialog.findViewById(R.id.alert_text);
        installText.setText(installTextString);
        dialog.setCanceledOnTouchOutside(true);
        Button yes = (Button) dialog.findViewById(R.id.yes_button);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(playStorePackage));
                startActivity(intent);
                dialog.dismiss();
            }
        });
        Button no = (Button) dialog.findViewById(R.id.no_button);
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private Bitmap createForecastComposite() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        LinearLayout snLayout = (LinearLayout) inflater.inflate(R.layout.social_network_forecast, null);
        snLayout.setDrawingCacheEnabled(true);
        snLayout.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        final String[] TIME_INTERVAL = {"00", "03", "06", "09", "12", "15", "18", "21"};

        MountainDao mountainDao = Database.getInstance(this).getMountainDao();
        final Mountain mountain = mountainDao.load(mountainId);

        TextView snForecastHeader = (TextView) snLayout.findViewById(R.id.sn_forecast_header);
        snForecastHeader.setText(mountain.getTitle());
        snForecastHeader.setTypeface(Utils.getHannariTypeFace(this));

        TextView snCurrentDifficultyText = (TextView) snLayout.findViewById(R.id.sn_current_difficulty_text);
        snCurrentDifficultyText.setTypeface(Utils.getHannariTypeFace(this));

        TextView snPoweredByText = (TextView) snLayout.findViewById(R.id.text_powered_by);
        snPoweredByText.setTypeface(Utils.getHannariTypeFace(this));

        ImageView snCurrentDifficulty = (ImageView) snLayout.findViewById(R.id.sn_current_difficulty);
        snCurrentDifficulty.setImageResource(DIFFICULTY_BIG_IMAGES.get(mountain.getStatus()));

        TextView snDate = (TextView) snLayout.findViewById(R.id.sn_date);
        DateTime now = new DateTime();
        snDate.setText(String.format("%s月%s日　本日の山行指数", Utils.num2DigitString(now.getMonthOfYear()), Utils.num2DigitString(now.getDayOfMonth())));

        MountainForecastJSON forecastJSON = JSONParser.parseMountainForecast(
                JSONDownloader.getMockMountainForecast(this, mountain.getYid()));
        if (forecastJSON != null) {
            Map<String, ForecastArrayElement> forecasts = forecastJSON.getForecasts();
            //TEMPORARY until we get realtime forecasts
            ForecastArrayElement[] forecastArray = forecasts.values().toArray(new ForecastArrayElement[forecasts.values().size()]);
            for (int i = 0; i < TIME_INTERVAL.length; i++) {
                ForecastArrayElement forecast = forecastArray[i];
                TextView time = (TextView) snLayout.findViewById(getResources().getIdentifier("sn_time_" + TIME_INTERVAL[i], "id", getPackageName()));
                time.setText(TIME_INTERVAL[i]);
                ImageView image = (ImageView) snLayout.findViewById(getResources().getIdentifier("sn_image_" + TIME_INTERVAL[i], "id", getPackageName()));
                image.setImageResource(DIFFICULTY_SMALL_IMAGES.get(forecast.getMountainStatus()));
            }
        }
        snLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        snLayout.layout(0, 0, snLayout.getMeasuredWidth(), snLayout.getMeasuredHeight());
        snLayout.buildDrawingCache(true);
        Bitmap composite = Bitmap.createBitmap(snLayout.getDrawingCache(true));

        snLayout.setDrawingCacheEnabled(false);
        return composite;
    }

    private void postToFacebook(final String text, final Bitmap image) {
        callbackManager = CallbackManager.Factory.create();
        List<String> permissionNeeds = Collections.singletonList("publish_actions");
        //this loginManager helps you eliminate adding a LoginButton to your UI
        loginManager = LoginManager.getInstance();
        loginManager.logInWithPublishPermissions(this, permissionNeeds);
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                SharePhoto photo = new SharePhoto.Builder()
                        .setBitmap(image)
                        .setCaption(text)
                        .build();
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                ShareApi.share(content, null);
                Toast.makeText(MountainForecastActivity.this,
                        getString(R.string.toast_facebook_post_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                System.out.println("sharePictureToFacebook.onCancel()");
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(MountainForecastActivity.this,
                        getString(R.string.toast_facebook_post_fail), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shareWithLINE() throws URISyntaxException, IOException {
        Bitmap bitmap = createForecastComposite();
        File shareImage = new File(getExternalCacheDir(), "forecast.png");
        OutputStream outputStream = new FileOutputStream(shareImage);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        outputStream.close();
        outputStream.flush();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("line://msg/image" + shareImage.getAbsolutePath()));
        startActivity(intent);
    }

    private void shareWithTwitter() throws URISyntaxException, IOException {
        Bitmap image = createForecastComposite();
        File shareImage = new File(getExternalCacheDir(), "forecast.png");
        OutputStream outputStream = new FileOutputStream(shareImage);
        image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        outputStream.close();
        outputStream.flush();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setPackage("com.twitter.android");
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shareImage));
        startActivity(intent);
    }

    private void shareWithMail() throws URISyntaxException, IOException {
        Bitmap image = createForecastComposite();
        File shareImage = new File(getExternalCacheDir(), "forecast.png");
        OutputStream outputStream = new FileOutputStream(shareImage);
        image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        outputStream.close();
        outputStream.flush();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(HTTP.PLAIN_TEXT_TYPE);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(shareImage));
        startActivity(intent);
    }

    // アプリがインストールされているかチェック
    private boolean isInstalledLINE() {
        try {
            PackageManager pm = getPackageManager();
            pm.getApplicationInfo("jp.naver.line.android", PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean isInstalledTwitter() {
        try {
            PackageManager pm = getPackageManager();
            pm.getApplicationInfo("com.twitter.android", PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent data) {
        super.onActivityResult(requestCode, responseCode, data);
        callbackManager.onActivityResult(requestCode, responseCode, data);
    }

    @Override
    public void downloadingCompleted(boolean result) {
        populateWidgetsFromDatabase();
        if (!new Settings(this).getSetting("setting_dont_display_warning")) {
            displayWarningDialog();
        }
        hideWidgetsWithOldData();
    }

    // For now this is used for "Today" and "Tomorrow", ie, short term forecasts
    private class ForecastScrollViewElement {
        private final LinearLayout layout;
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
            layout = forecast;
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

            for (int columnIndex = 0; columnIndex < 4; columnIndex++) {
                columns.add(new ForecastColumn(forecastTable, lowTempRow, lowWindRow, midTempRow, midWindRow, highTempRow, highWindRow, columnIndex));
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

        public LinearLayout getLayout() {
            return layout;
        }
    }

    private class ForecastColumn {
        private final int columnIndex;
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

        public ForecastColumn(TableLayout forecastTable, TableRow lowTemp, TableRow lowWind, TableRow midTemp, TableRow midWind, TableRow highTemp, TableRow highWind, int columnIndex) {
            this.columnIndex = columnIndex;
            time = (TextView) forecastTable.findViewById(getResources().getIdentifier("forecast_time_" + columnIndex, "id", getPackageName()));
            difficulty = (ImageView) forecastTable.findViewById(getResources().getIdentifier("forecast_difficulty_" + columnIndex, "id", getPackageName()));
            lowHeightTemperature = (TextView) lowTemp.findViewById(getResources().getIdentifier("forecast_temp_" + columnIndex, "id", getPackageName()));
            lowHeightWindDirection = (ImageView) lowWind.findViewById(getResources().getIdentifier("forecast_wind_direction_" + columnIndex, "id", getPackageName()));
            lowHeightWind = (TextView) lowWind.findViewById(getResources().getIdentifier("forecast_wind_" + columnIndex, "id", getPackageName()));
            midHeightTemperature = (TextView) midTemp.findViewById(getResources().getIdentifier("forecast_temp_" + columnIndex, "id", getPackageName()));
            midHeightWindDirection = (ImageView) midWind.findViewById(getResources().getIdentifier("forecast_wind_direction_" + columnIndex, "id", getPackageName()));
            midHeightWind = (TextView) midWind.findViewById(getResources().getIdentifier("forecast_wind_" + columnIndex, "id", getPackageName()));
            highHeightTemperature = (TextView) highTemp.findViewById(getResources().getIdentifier("forecast_temp_" + columnIndex, "id", getPackageName()));
            highHeightWindDirection = (ImageView) highWind.findViewById(getResources().getIdentifier("forecast_wind_direction_" + columnIndex, "id", getPackageName()));
            highHeightWind = (TextView) highWind.findViewById(getResources().getIdentifier("forecast_wind_" + columnIndex, "id", getPackageName()));
            rainLevel = (TextView) forecastTable.findViewById(getResources().getIdentifier("forecast_rain_level_" + columnIndex, "id", getPackageName()));
            cloudCover = (TextView) forecastTable.findViewById(getResources().getIdentifier("forecast_cloud_cover_" + columnIndex, "id", getPackageName()));
        }

        public int getColumnIndex() {
            return columnIndex;
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
