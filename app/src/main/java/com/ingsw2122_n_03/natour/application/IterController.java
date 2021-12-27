package com.ingsw2122_n_03.natour.application;

import com.ingsw2122_n_03.natour.infastructure.implementations.ItineraryDaoImplementation;
import com.ingsw2122_n_03.natour.infastructure.implementations.UserDaoImplementation;
import com.ingsw2122_n_03.natour.infastructure.interfaces.ItineraryDaoInterface;
import com.ingsw2122_n_03.natour.infastructure.interfaces.UserDaoInterface;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.model.WayPoint;
import com.ingsw2122_n_03.natour.presentation.MainActivity;

public class IterController extends Controller {

    private static IterController instance = null;

    private MainActivity mainActivity;
    private ItineraryDaoInterface itineraryDao;
    private UserDaoInterface userDao;

    private IterController(){
        itineraryDao = new ItineraryDaoImplementation(this);
        userDao = new UserDaoImplementation();
    }

    public static IterController getInstance() {
        if(instance == null){
            instance = new IterController();
        }
        return instance;
    }

    //@TODO OTHER ATTRIBUTES REQUIRED
    public void insertItinerary(String name, String description, String difficulty, int hours, int minutes) {
        User creator = new User(userDao.getCurrentUserId());
        //@TODO 1)OBTAIN START WAYPOINT FROM MAP, 2)CREATE WAYPOINT DAO TO INSERT WAYPOINTS, 3)CREATE ITER ID, 4)CREATE CLASS FOR IMAGE UPLOAD
        WayPoint startPoint = new WayPoint(1, 2);
        Itinerary iter = new Itinerary(name, difficulty, hours, minutes, startPoint, creator);
        if(description.length() > 0)
           iter.setDescription(description);

    }

    public void onItineraryInsertSuccess() {

    }

    public void onItineraryInsertError(String msg) {

    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

}


