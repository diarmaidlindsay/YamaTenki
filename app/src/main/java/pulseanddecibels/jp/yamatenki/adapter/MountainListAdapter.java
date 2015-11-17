package pulseanddecibels.jp.yamatenki.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;
import pulseanddecibels.jp.yamatenki.R;
import pulseanddecibels.jp.yamatenki.activity.MountainForecastActivity;
import pulseanddecibels.jp.yamatenki.database.Database;
import pulseanddecibels.jp.yamatenki.database.dao.Area;
import pulseanddecibels.jp.yamatenki.database.dao.AreaDao;
import pulseanddecibels.jp.yamatenki.database.dao.Coordinate;
import pulseanddecibels.jp.yamatenki.database.dao.CoordinateDao;
import pulseanddecibels.jp.yamatenki.database.dao.Mountain;
import pulseanddecibels.jp.yamatenki.database.dao.MountainDao;
import pulseanddecibels.jp.yamatenki.database.dao.MyMountain;
import pulseanddecibels.jp.yamatenki.database.dao.MyMountainDao;
import pulseanddecibels.jp.yamatenki.database.dao.Status;
import pulseanddecibels.jp.yamatenki.database.dao.StatusDao;
import pulseanddecibels.jp.yamatenki.enums.MountainListColumn;
import pulseanddecibels.jp.yamatenki.enums.SortOrder;
import pulseanddecibels.jp.yamatenki.utils.GeoLocation;
import pulseanddecibels.jp.yamatenki.utils.Utils;

/**
 * Created by Diarmaid Lindsay on 2015/09/28.
 * Copyright Pulse and Decibels 2015
 */
public class MountainListAdapter extends BaseAdapter {

    final SparseIntArray difficultyArray = new SparseIntArray() {
        {
            append(1, R.drawable.difficulty_small_a);
            append(2, R.drawable.difficulty_small_b);
            append(3, R.drawable.difficulty_small_c);
        }
    };
    final double EARTH_RADIUS = 6371.01;

    private MountainListColumn currentSorting;
    private SortOrder currentOrder;

    private List mountainList = new ArrayList<>();
    private Context mContext;
    private LayoutInflater layoutInflater;
    private GeoLocation here;
    private Map<Long, Integer> currentMountainStatus = new HashMap<>();
    String searchString; //in case we have to resubmit the query after update in child activity

    public MountainListAdapter(Context context) {
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
        initialiseDataSets();
        StatusDao statusDao = Database.getInstance(mContext).getStatusDao();
        List<Status> allStatus = statusDao.loadAll();
        for(Status status : allStatus) {
            currentMountainStatus.put(status.getMountainId(), status.getStatus());
        }
    }

    private void initialiseDataSets() {
//        String json = JSONDownloader.getMockMountainList(mContext);
//        //String json = JSONDownloader.getJsonFromServer(); // Future way
//        //Will be stored in database eventually, in future will get from datasource
//        MountainListJSON mountainListJSON = JSONParser.parseMountainsFromMountainList(json);
//        mountainList = mountainListJSON.getMountainArrayElements();
    }

    @Override
    public int getCount() {
        return mountainList.size();
    }

    @Override
    public Object getItem(int position) {
        return mountainList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolderItem();
            convertView = layoutInflater.inflate(R.layout.list_item_mountain, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.mountain_list_name);
            viewHolder.difficulty = (ImageView) convertView.findViewById(R.id.mountain_list_difficulty);
            viewHolder.height = (TextView) convertView.findViewById(R.id.mountain_list_height);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        //check first bit, if set then it is an odd number. We'll give alternate rows different backgrounds
        if ((position % 2) == 0) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.table_bg_alt));
        } else {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.yama_background));
        }
        Mountain mountain = (Mountain) getItem(position);
        viewHolder.name.setText(mountain.getTitle());
        final long mountainId = mountain.getId();
        viewHolder.difficulty.setImageResource(difficultyArray.get(currentMountainStatus.get(mountainId)));
        viewHolder.height.setText(String.format("%sm", String.valueOf(mountain.getHeight())));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MountainForecastActivity.class);
                intent.putExtra("mountainId", mountainId);
                ((Activity)mContext).startActivityForResult(intent, 100);
            }
        });

        return convertView;
    }

    private Coordinate getMountainCoordinants(Mountain mountain) {
        CoordinateDao coordinateDao = Database.getInstance(mContext).getCoordinateDao();
        return coordinateDao.queryBuilder().where(CoordinateDao.Properties.MountainId.eq(mountain.getId())).unique();
    }

    private double getDistanceFromHere(GeoLocation there) {
        double distance = -1;

        if (here != null) {
            distance = here.distanceTo(there, EARTH_RADIUS);
        }

        return distance;
    }

    public void searchByArea(String areaName) {
        AreaDao areaDao = Database.getInstance(mContext).getAreaDao();
        QueryBuilder qb = areaDao.queryBuilder();
        qb.where(AreaDao.Properties.Name.eq(areaName));

        List areas = qb.list();
        if (areas.size() == 1) {
            long areaId = ((Area) areas.get(0)).getId();
            MountainDao mountainDao = Database.getInstance(mContext).getMountainDao();
            qb = mountainDao.queryBuilder();
            qb.where(MountainDao.Properties.AreaId.eq(areaId));

            mountainList = qb.list();
            notifyDataSetChanged();
        } else {
            Log.e(this.getClass().getSimpleName(), "Couldn't find area with name : " + areaName);
        }
    }

    public void searchByName(String searchString) {
        this.searchString = searchString;
        MountainDao mountainDao = Database.getInstance(mContext).getMountainDao();
        QueryBuilder qb = mountainDao.queryBuilder();

        if (searchString.length() > 0) {

            if (Utils.isKanji(searchString.charAt(0))) {
                qb.where(MountainDao.Properties.Title.like("%" + searchString + "%"));
            } else if (Utils.isKana(searchString.charAt(0))) {
                qb.where(MountainDao.Properties.Kana.like("%" + searchString + "%"));
            } else {
                qb.where(MountainDao.Properties.TitleEnglish.like("%" + searchString + "%"));
            }
        }

        mountainList = qb.list();
        notifyDataSetChanged();
    }

    public void searchByMyMountainName(String searchString) {
        this.searchString = searchString;
        MountainDao mountainDao = Database.getInstance(mContext).getMountainDao();
        MyMountainDao myMountainDao = Database.getInstance(mContext).getMyMountainDao();

        List<Long> myMountainIds = new ArrayList<>();

        for(MyMountain mountain : myMountainDao.queryBuilder().list()) {
            myMountainIds.add(mountain.getMountainId());
        }

        QueryBuilder qb = mountainDao.queryBuilder();

        if (searchString.length() > 0) {

            if (Utils.isKanji(searchString.charAt(0))) {
                qb.where(MountainDao.Properties.Title.like("%" + searchString + "%"));
            } else if (Utils.isKana(searchString.charAt(0))) {
                qb.where(MountainDao.Properties.Kana.like("%" + searchString + "%"));
            } else {
                qb.where(MountainDao.Properties.TitleEnglish.like("%" + searchString + "%"));
            }
        }

        qb.where(MountainDao.Properties.Id.in(myMountainIds));

        mountainList = qb.list();
        notifyDataSetChanged();

    }

    public void searchByClosestMountains(double latitude, double longitude) {
        CoordinateDao coordinateDao = Database.getInstance(mContext).getCoordinateDao();
        List<Coordinate> nearbyMountainCoordinates = new ArrayList<>();

        for (int offset = 1; offset < 7; offset++) {
            final double minLat = latitude - offset;
            final double maxLat = latitude + offset;
            final double minLon = longitude - offset;
            final double maxLon = longitude + offset;

            nearbyMountainCoordinates = coordinateDao.queryBuilder()
                    .where(CoordinateDao.Properties.Latitude.between(minLat, maxLat),
                            CoordinateDao.Properties.Longitude.between(minLon, maxLon))
                    .list();

            if (nearbyMountainCoordinates.size() >= 20) {
                break;
            }
        }

        if (nearbyMountainCoordinates.size() > 0) {
            here = GeoLocation.fromDegrees(latitude, longitude);
            Coordinate[] sortedCoordinates = sortByDistanceFromHere(nearbyMountainCoordinates, here);
            List<Long> coordinateIds = new ArrayList<>();

            int coordinantsAmount = sortedCoordinates.length > 20 ? 20 : sortedCoordinates.length;
            for (int i = 0; i < coordinantsAmount; i++) {
                coordinateIds.add(sortedCoordinates[i].getId());
            }

            List<Coordinate> coordinateList =
                coordinateDao.queryBuilder().where(CoordinateDao.Properties.Id.in(coordinateIds)).list();

            List<Mountain> matchingMountains = new ArrayList<>();
            for(Coordinate coordinate : coordinateList) {
                matchingMountains.add(coordinate.getMountain());
            }

            mountainList = matchingMountains;
            notifyDataSetChanged();
        }
    }

    public void searchByHeight(int min, int max) {
        MountainDao mountainDao = Database.getInstance(mContext).getMountainDao();
        mountainList = mountainDao.queryBuilder()
                .where(MountainDao.Properties.Height.between(min, max))
                .list();
    }

    public Coordinate[] sortByDistanceFromHere(List<Coordinate> coordinatesList, GeoLocation here) {
        Coordinate[] coordinates =
                coordinatesList.toArray(new Coordinate[coordinatesList.size()]);
        Coordinate temp;
        for (int i = 1; i < coordinates.length; i++) {
            for (int j = i; j > 0; j--) {
                if (here.distanceTo(GeoLocation.fromCoordinates(coordinates[j]), EARTH_RADIUS) <
                        here.distanceTo(GeoLocation.fromCoordinates(coordinates[j - 1]), EARTH_RADIUS)) {
                    temp = coordinates[j];
                    coordinates[j] = coordinates[j - 1];
                    coordinates[j - 1] = temp;
                }
            }
        }
        return coordinates;
    }

    public void sort(MountainListColumn column) {
        if(currentSorting == column) {
            Collections.reverse(mountainList);
            if(currentOrder == SortOrder.DESC) {
                currentOrder = SortOrder.ASC;
            } else {
                currentOrder = SortOrder.DESC;
            }
        } else {
            currentSorting = column;
            currentOrder = SortOrder.DESC;

            switch (column) {
                case NAME:
                    Collections.sort(mountainList, getNameComparitor());
                    break;
                case DIFFICULTY:
                    Collections.sort(mountainList, getDifficultyComparitor());
                    break;
                case HEIGHT:
                    Collections.sort(mountainList, getHeightComparitor());
                    break;
            }
        }

        notifyDataSetInvalidated();
    }

    private Comparator<Mountain> getNameComparitor() {
        return new Comparator<Mountain>() {
            @Override
            public int compare(Mountain lhs, Mountain rhs) {
                return String.CASE_INSENSITIVE_ORDER.compare(lhs.getTitleEnglish(), rhs.getTitleEnglish());
            }
        };
    }

    private Comparator<Mountain> getDifficultyComparitor() {
        return new Comparator<Mountain>() {
            @Override
            public int compare(Mountain lhs, Mountain rhs) {
                Integer leftStatus = currentMountainStatus.get(lhs.getId());
                Integer rightStatus = currentMountainStatus.get(rhs.getId());

                if(leftStatus != null && rightStatus != null) {
                    if(leftStatus < rightStatus) {
                        return -1;
                    } else if (leftStatus > rightStatus) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
                return 0;
            }
        };
    }

    private Comparator<Mountain> getHeightComparitor() {
        return new Comparator<Mountain>() {
            @Override
            public int compare(Mountain lhs, Mountain rhs) {
                if (lhs.getHeight() < rhs.getHeight()) {
                    return -1;
                } else if (lhs.getHeight() > rhs.getHeight()) {
                    return 1;
                } else {
                    return 0;

                }
            }
        };
    }

    static class ViewHolderItem {
        TextView name;
        ImageView difficulty;
        TextView height;
    }

    public MountainListColumn getCurrentSorting() {
        return currentSorting;
    }

    public SortOrder getCurrentOrder() {
        return currentOrder;
    }
}
