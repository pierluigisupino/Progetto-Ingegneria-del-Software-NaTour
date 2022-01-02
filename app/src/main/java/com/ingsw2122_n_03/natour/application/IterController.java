package com.ingsw2122_n_03.natour.application;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.infastructure.implementations.ImageUploader;
import com.ingsw2122_n_03.natour.infastructure.implementations.ItineraryDaoImplementation;
import com.ingsw2122_n_03.natour.infastructure.implementations.UserDaoImplementation;
import com.ingsw2122_n_03.natour.infastructure.interfaces.ItineraryDaoInterface;
import com.ingsw2122_n_03.natour.infastructure.interfaces.UserDaoInterface;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.model.WayPoint;
import com.ingsw2122_n_03.natour.presentation.AddItineraryActivity;
import com.ingsw2122_n_03.natour.presentation.ItineraryDetailActivity;
import com.ingsw2122_n_03.natour.presentation.LoadingDialog;
import com.ingsw2122_n_03.natour.presentation.MainActivity;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;


public class IterController extends Controller {

    private static IterController instance = null;

    private MainActivity mainActivity;
    private AddItineraryActivity addItineraryActivity;
    private LoadingDialog loadingDialog;
    private ItineraryDetailActivity itineraryDetailActivity;

    private final ItineraryDaoInterface itineraryDao;
    private final UserDaoInterface userDao;
    private final ImageUploader imageUploader;

    private User creator;
    private Itinerary iter;

    private ArrayList<byte[]> imagesBytes;

    private IterController(){

        itineraryDao = new ItineraryDaoImplementation(this);
        userDao = new UserDaoImplementation();
        imageUploader = new ImageUploader(this);

    }

    public static IterController getInstance() {

        if(instance == null){
            instance = new IterController();
        }
        return instance;

    }


    public void insertItinerary(String name, String description, String difficulty, int hours, int minutes, ArrayList<byte[]> imagesBytes, ArrayList<GeoPoint> waypoints) {

        loadingDialog = new LoadingDialog(addItineraryActivity);
        loadingDialog.startLoading();
        creator = new User(userDao.getCurrentUserId());
        this.imagesBytes = imagesBytes;
        ArrayList<WayPoint> wayPointArrayList = new ArrayList<>();
        for(GeoPoint g : waypoints){
            wayPointArrayList.add(new WayPoint(g.getLatitude(), g.getLongitude()));
        }
        iter = new Itinerary(name, difficulty, hours, minutes, wayPointArrayList.get(0), creator);
        wayPointArrayList.remove(0);

        if(description.length() > 0)
           iter.setDescription(description);

        if(!wayPointArrayList.isEmpty())
            iter.setWayPoints(wayPointArrayList);

        itineraryDao.postItinerary(iter);

    }

    public void onItineraryInsertSuccess(int iterID, String userName) {

        creator.setName(userName);

        if(imagesBytes.isEmpty())
            onItineraryInsertComplete(0);
        else
            imageUploader.uploadImages(iterID, imagesBytes);

    }

    public void onItineraryInsertError() {

        loadingDialog.dismissDialog();
        addItineraryActivity.onFail(addItineraryActivity.getString(R.string.itinerary_insert_failure));

    }

    public void onItineraryInsertComplete(int response) {

        loadingDialog.dismissDialog();

        //@TODO CHANGE TO MAP<STRING, OBJECT>
        HashMap<String, String> map = new HashMap<>();
        map.put("name", iter.getName());
        map.put("hours", String.valueOf(iter.getHoursDuration()));
        map.put("creator", creator.getName());
        map.put("minutes", String.valueOf(iter.getMinutesDuration()));

        if(response == 0)
            goToActivityAndFinish(addItineraryActivity, ItineraryDetailActivity.class, map);
        else
            goToActivityAndFinish(addItineraryActivity, MainActivity.class);

    }


    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setAddItineraryActivity(AddItineraryActivity addItineraryActivity) {
        this.addItineraryActivity = addItineraryActivity;
    }

    public void setItineraryDetailActivity(ItineraryDetailActivity itineraryDetailActivity) {
        this.itineraryDetailActivity = itineraryDetailActivity;
    }

}


