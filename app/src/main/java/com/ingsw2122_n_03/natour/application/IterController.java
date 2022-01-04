package com.ingsw2122_n_03.natour.application;

import android.app.Activity;

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

    private User currentUser;
    private Itinerary currentIter;
    private final ArrayList<Itinerary> itineraries = new ArrayList<>();

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

    //@TODO RETRIEVE ITINERARIES FROM DB AND SHOW MAIN ACTIVITY
    public void setUp(Activity callingActivity) {
        itineraryDao.getItineraries();
        goToActivityAndFinish(callingActivity, MainActivity.class);
    }

    public void insertItinerary(String name, String description, String difficulty, int hours, int minutes, ArrayList<byte[]> imagesBytes, ArrayList<GeoPoint> waypoints) {

        loadingDialog = new LoadingDialog(addItineraryActivity);
        loadingDialog.startLoading();

        currentUser = new User(userDao.getCurrentUserId());
        userDao.setCurrentUserName(currentUser);

        this.imagesBytes = imagesBytes;
        ArrayList<WayPoint> wayPointArrayList = new ArrayList<>();
        for(GeoPoint g : waypoints){
            wayPointArrayList.add(new WayPoint(g.getLatitude(), g.getLongitude()));
        }
        currentIter = new Itinerary(name, difficulty, hours, minutes, wayPointArrayList.get(0), currentUser);
        wayPointArrayList.remove(0);

        if(description.length() > 0)
           currentIter.setDescription(description);

        if(!wayPointArrayList.isEmpty())
            currentIter.setWayPoints(wayPointArrayList);

        itineraryDao.postItinerary(currentIter);

    }

    public void onItineraryInsertSuccess(int iterID) {

        currentIter.setIterId(iterID);

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
        map.put("name", currentIter.getName());
        map.put("hours", String.valueOf(currentIter.getHoursDuration()));
        map.put("creator", currentUser.getName());
        map.put("minutes", String.valueOf(currentIter.getMinutesDuration()));

        if(response == 0)
            goToActivityAndFinish(addItineraryActivity, ItineraryDetailActivity.class, map);
        else
            goToActivityAndFinish(addItineraryActivity, MainActivity.class);

    }


    /**********
     * SETTERS
     **********/


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


