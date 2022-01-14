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
import com.ingsw2122_n_03.natour.presentation.itinerary.addItinerary.AddItineraryActivity;
import com.ingsw2122_n_03.natour.presentation.ErrorActivity;
import com.ingsw2122_n_03.natour.presentation.itinerary.ItineraryDetailActivity;
import com.ingsw2122_n_03.natour.presentation.LoadingDialog;
import com.ingsw2122_n_03.natour.presentation.main.MainActivity;
import com.ingsw2122_n_03.natour.presentation.SplashActivity;
import com.ingsw2122_n_03.natour.presentation.main.MainFragment;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Date;

public class IterController extends Controller {

    private static IterController instance = null;

    private SplashActivity splashActivity;
    private MainActivity mainActivity;
    private MainFragment mainFragment;
    private AddItineraryActivity addItineraryActivity;
    private ItineraryDetailActivity detailActivity;
    private LoadingDialog loadingDialog;

    private final ItineraryDaoInterface itineraryDao;
    private final UserDaoInterface userDao;

    private final ImageUploader imageUploader;

    private User currentUser;
    private Itinerary currentIter;
    private ArrayList<Itinerary> itineraries = new ArrayList<>();

    private ArrayList<byte[]> photos;

    private IterController(){

        itineraryDao = new ItineraryDaoImplementation(this);
        userDao = new UserDaoImplementation(this);
        imageUploader = new ImageUploader(this);

    }

    public static IterController getInstance() {

        if(instance == null){
            instance = new IterController();
        }
        return instance;

    }

    /*********
     * SET UP
     *********/

    public void setUp() {
        currentUser = new User(userDao.getCurrentUserId());
        currentUser.setName(splashActivity.getResources().getString(R.string.current_user_name_text));
        itineraryDao.getSetUpItineraries();
        //goToActivityAndFinish(splashActivity, MainActivity.class, itineraries); /* TO DELETE, FOR TEST USAGE**/
    }

    public void onSetUpSuccess(ArrayList<Itinerary> itineraries) {
        this.itineraries = itineraries;
        goToActivityAndFinish(splashActivity, MainActivity.class, itineraries);
    }

    public void onSetUpError() {
        goToActivityAndFinish(splashActivity, ErrorActivity.class);
    }


    /******************
     * GET ITINERARIES
     ******************/

    public void getUpdatedItineraries(){
        itineraryDao.getRecentItineraries();
    }

    public void onUpdateError(){
        mainActivity.onFail(mainActivity.getString(R.string.generic_error));
    }

    public void onUpdateSuccess(ArrayList<Itinerary> iters){
        itineraries.clear();
        mainActivity.onSuccess(mainActivity.getResources().getString(R.string.update));
        itineraries = iters;
        mainFragment.updateItineraries(itineraries);
    }


    /*****************
     * POST ITINERARY
     ****************/

    public void insertItinerary(String name, String description, String difficulty, int hours, int minutes, ArrayList<byte[]> imagesBytes, ArrayList<GeoPoint> waypoints) {

        loadingDialog = new LoadingDialog(addItineraryActivity);
        loadingDialog.startLoading();

        this.photos = imagesBytes;

        ArrayList<WayPoint> wayPointArrayList = new ArrayList<>();
        for(GeoPoint g : waypoints){
            wayPointArrayList.add(new WayPoint(g.getLatitude(), g.getLongitude()));
        }
        currentIter = new Itinerary(name, difficulty, hours, minutes, wayPointArrayList.get(0), currentUser);
        wayPointArrayList.remove(0);

        currentIter.setShareDate(new Date());

        if(description.length() > 0)
           currentIter.setDescription(description);

        if(!wayPointArrayList.isEmpty())
            currentIter.setWayPoints(wayPointArrayList);

        itineraryDao.postItinerary(currentIter);
    }

    public void onItineraryInsertSuccess(int iterID) {

        currentIter.setIterId(iterID);

        if(photos.isEmpty())
            onItineraryInsertComplete(true);
        else
            imageUploader.uploadImages(iterID, photos);

    }

    public void onItineraryInsertError() {

        loadingDialog.dismissDialog();
        addItineraryActivity.onFail(addItineraryActivity.getString(R.string.itinerary_insert_failure));

    }

    public void onItineraryInsertComplete(boolean success) {

        loadingDialog.dismissDialog();
        itineraries.add(0, currentIter);
        mainFragment.updateItineraries(itineraries);
        addItineraryActivity.finish();

        if(success)
            mainActivity.onSuccess(mainActivity.getString(R.string.itinerary_insert_success));
        else
            mainActivity.onFail(mainActivity.getString(R.string.photo_upload_failed));

    }

    /*************
     * NAVIGATION
     ************/
    //TODO HERE MAY BE CALL GETIMAGESBYITER
    public void onItineraryClick(Itinerary iter) {

        if(iter.getCreator().getUid().equals(currentUser.getUid()))
            iter.setCreator(currentUser);

        currentIter = iter;

        if(iter.getCreator().getName() != null)
            goToActivity(mainActivity, ItineraryDetailActivity.class, iter);
        else
            userDao.getNameFromId(iter.getCreator());

    }

    public void onRetrieveUserSuccess() {
        goToActivity(mainActivity, ItineraryDetailActivity.class, currentIter);
    }

    public void onRetrieveUserError() {
        mainActivity.onFail(mainActivity.getString(R.string.retrieve_itinerary_error));
    }


    /**********
     * SETTERS
     **********/

    public void setSplashActivity(SplashActivity splashActivity) { this.splashActivity = splashActivity; }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setMainFragment(MainFragment mainFragment) { this.mainFragment = mainFragment;}

    public void setAddItineraryActivity(AddItineraryActivity addItineraryActivity) {
        this.addItineraryActivity = addItineraryActivity;
    }

    public void setItineraryDetailActivity(ItineraryDetailActivity detailActivity) {
        this.detailActivity = detailActivity;
    }

}


