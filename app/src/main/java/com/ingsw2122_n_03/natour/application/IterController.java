package com.ingsw2122_n_03.natour.application;

import com.ingsw2122_n_03.natour.infastructure.implementations.ImageUploader;
import com.ingsw2122_n_03.natour.infastructure.implementations.ItineraryDaoImplementation;
import com.ingsw2122_n_03.natour.infastructure.implementations.UserDaoImplementation;
import com.ingsw2122_n_03.natour.infastructure.interfaces.ItineraryDaoInterface;
import com.ingsw2122_n_03.natour.infastructure.interfaces.UserDaoInterface;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.model.WayPoint;
import com.ingsw2122_n_03.natour.presentation.MainActivity;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class IterController extends Controller {

    private static IterController instance = null;

    private MainActivity mainActivity;

    private ItineraryDaoInterface itineraryDao;
    private UserDaoInterface userDao;
    private ImageUploader imageUploader;

    private User creator;

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

        creator = new User(userDao.getCurrentUserId());
        this.imagesBytes = imagesBytes;
        ArrayList<WayPoint> wayPointArrayList = new ArrayList<>();
        for(GeoPoint g : waypoints){
            wayPointArrayList.add(new WayPoint(g.getLatitude(), g.getLongitude()));
        }
        Itinerary iter = new Itinerary(name, difficulty, hours, minutes, wayPointArrayList.get(0), creator);
        wayPointArrayList.remove(0);

        if(description.length() > 0)
           iter.setDescription(description);

        if(wayPointArrayList.size() > 0)
            iter.setWayPoints(wayPointArrayList);

        itineraryDao.postItinerary(iter);

    }

    public void onItineraryInsertSuccess(int iterID) {

        if(imagesBytes.isEmpty())
            onItineraryInsertComplete();
        else
            imageUploader.uploadImages(iterID, imagesBytes);

    }

    public void onItineraryInsertError() {

    }

    public void onItineraryInsertComplete() {

    }


    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

}


