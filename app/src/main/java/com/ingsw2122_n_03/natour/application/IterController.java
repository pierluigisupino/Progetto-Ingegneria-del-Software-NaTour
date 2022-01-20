package com.ingsw2122_n_03.natour.application;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.infastructure.implementations.ImageDownloader;
import com.ingsw2122_n_03.natour.infastructure.implementations.ImageUploader;
import com.ingsw2122_n_03.natour.infastructure.implementations.ItineraryDaoImplementation;
import com.ingsw2122_n_03.natour.infastructure.implementations.UserDaoImplementation;
import com.ingsw2122_n_03.natour.infastructure.interfaces.ItineraryDaoInterface;
import com.ingsw2122_n_03.natour.infastructure.interfaces.UserDaoInterface;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.model.WayPoint;
import com.ingsw2122_n_03.natour.presentation.ErrorActivity;
import com.ingsw2122_n_03.natour.presentation.LoadingDialog;
import com.ingsw2122_n_03.natour.presentation.SplashActivity;
import com.ingsw2122_n_03.natour.presentation.itinerary.FollowItineraryActivity;
import com.ingsw2122_n_03.natour.presentation.itinerary.ItineraryDetailActivity;
import com.ingsw2122_n_03.natour.presentation.itinerary.addItinerary.AddItineraryActivity;
import com.ingsw2122_n_03.natour.presentation.main.MainActivity;
import com.ingsw2122_n_03.natour.presentation.main.MainFragment;
import com.ingsw2122_n_03.natour.presentation.support.ImageUtilities;

import org.joda.time.LocalTime;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class IterController extends NavigationController {

    private static IterController instance = null;

    private SplashActivity splashActivity;
    private MainActivity mainActivity;
    private MainFragment mainFragment;
    private AddItineraryActivity addItineraryActivity;
    private ItineraryDetailActivity detailActivity;
    private FollowItineraryActivity followItineraryActivity;
    private LoadingDialog loadingDialog;

    private final ItineraryDaoInterface itineraryDao;
    private final UserDaoInterface userDao;

    private final ImageUploader imageUploader;
    private final ImageDownloader imageDownloader;

    private ArrayList<byte[]> photos = new ArrayList<>();
    private byte[] uploadingPhoto;

    private User currentUser;
    private Itinerary currentIter;
    private Itinerary updatedIter;
    private ArrayList<Itinerary> itineraries = new ArrayList<>();


    private IterController(){

        itineraryDao = new ItineraryDaoImplementation(this);
        userDao = new UserDaoImplementation(this);
        imageUploader = new ImageUploader(this);
        imageDownloader = new ImageDownloader(this);

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


    public void onSetUpError(boolean isResolvableError) {
        if(isResolvableError)
            goToActivityAndFinish(splashActivity, MainActivity.class);
        else
            goToActivityAndFinish(splashActivity, ErrorActivity.class);
    }


    /******************
     * GET ITINERARIES
     ******************/

    public void updateItineraries(){ itineraryDao.getRecentItineraries(); }


    public void onUpdateItinerariesSuccess(ArrayList<Itinerary> iters){
        itineraries.clear();
        itineraries = iters;
        mainFragment.updateItineraries(itineraries);
        mainFragment.onSuccess();
        mainActivity.onSuccess(mainActivity.getResources().getString(R.string.update));
    }


    public void retrieveItineraries() {
        int lastIndex = itineraries.size()-1;
        if(lastIndex >= 0) {
            itineraryDao.getOlderItineraries(itineraries.get(lastIndex).getIterId());
        }
    }


    public void onRetrieveItinerarySuccess(ArrayList<Itinerary> iters) {
        if(iters.size() > 0){
            itineraries.addAll(iters);
            mainFragment.updateItineraries(itineraries);
        }
        mainFragment.onSuccess();
    }


    public void onUpdateError(){
        mainFragment.onError();
        mainActivity.onFail(mainActivity.getString(R.string.generic_error));
    }


    /*****************
     * POST ITINERARY
     ****************/

    public void insertItinerary(String name, String description, int difficulty, LocalTime duration, ArrayList<byte[]> imagesBytes, ArrayList<GeoPoint> waypoints) {

        loadingDialog = new LoadingDialog(addItineraryActivity, addItineraryActivity.getString(R.string.loading_text_add_itinerary));
        loadingDialog.startLoading();

        this.photos = imagesBytes;

        ArrayList<WayPoint> wayPointArrayList = new ArrayList<>();
        for(GeoPoint g : waypoints){
            wayPointArrayList.add(new WayPoint(g.getLatitude(), g.getLongitude()));
        }
        currentIter = new Itinerary(name, difficulty, duration, wayPointArrayList.get(0), currentUser, new Date());
        wayPointArrayList.remove(0);

        if(description.length() > 0)
           currentIter.setDescription(description);

        if(!wayPointArrayList.isEmpty())
            currentIter.setWayPoints(wayPointArrayList);

        itineraryDao.postItinerary(currentIter);

    }


    public void onItineraryInsertSuccess(int iterID) {

        currentIter.setIterId(iterID);

        loadingDialog.dismissDialog();
        itineraries.add(0, currentIter);
        mainFragment.updateItineraries(itineraries);
        addItineraryActivity.finish();

        if(photos.isEmpty())
            onItineraryInsertFinish(photos.size());
        else{
            mainActivity.onWaitingBackgroundTask("WAIT"); // TODO: 1/20/22  string
            imageUploader.uploadImages(iterID, photos);
        }

    }


    public void onItineraryInsertError() {
        loadingDialog.dismissDialog();
        addItineraryActivity.onFail(addItineraryActivity.getString(R.string.itinerary_insert_failure));
    }


    public void onItineraryInsertFinish(int pivot) {

        if(pivot == photos.size()){
            mainActivity.onBackgroundTaskEnd();
            mainActivity.onSuccess(mainActivity.getString(R.string.itinerary_insert_success));
        }

    }


    public void onUploadPhotoError() {
        //DISMISS SNACKBAR
        mainActivity.onFail(mainActivity.getString(R.string.photo_upload_failed));
    }


    /***************
     * PUT FEEDBACK
     **************/


    public void manageFeedback(int newDurationMinutes, int newDifficulty) {

        boolean toUpdate = false;
        updatedIter = new Itinerary(currentIter);

        int currDurationMinutes = currentIter.getDuration().getMinuteOfHour();
        currDurationMinutes += (currentIter.getDuration().getHourOfDay()*60);

        if(currDurationMinutes != newDurationMinutes) {

            toUpdate = true;
            int averageMinutes = (newDurationMinutes + currDurationMinutes) / 2;
            int averageHours = (averageMinutes / 60);
            averageMinutes -= (60 * averageHours);
            LocalTime averageDuration = new LocalTime(averageHours, averageMinutes);
            updatedIter.setDuration(averageDuration);

        }

        int currDifficulty = currentIter.getDifficulty();

        if(currDifficulty != newDifficulty) {

            toUpdate = true;
            int averageDifficultyLevel = (currDifficulty + newDifficulty)/2;
            updatedIter.setDifficulty(averageDifficultyLevel);

        }

        if(toUpdate){
            itineraryDao.putItineraryFromFeedback(updatedIter);
            loadingDialog = new LoadingDialog(detailActivity, detailActivity.getString(R.string.loading_text_update_itinerary));
            loadingDialog.startLoading();
        } else
            detailActivity.onSuccess(detailActivity.getString(R.string.feedback_no_changes));

    }


    public void onItineraryUpdateSuccess() {
        itineraries.remove(currentIter);
        itineraries.add(updatedIter);
        detailActivity.updateItineraryView(updatedIter);
        loadingDialog.dismissDialog();
        detailActivity.onSuccess(detailActivity.getString(R.string.feedback_success_text));
    }


    public void onItineraryUpdateError() {
        loadingDialog.dismissDialog();
        detailActivity.onFail(detailActivity.getString(R.string.generic_error));
    }


    /*************
     * NAVIGATION
     ************/

    public void onItineraryClick(Itinerary iter) {

        currentIter = iter;
        photos.clear();
        imageDownloader.ResetSession(iter.getIterId());

        if(iter.getCreator().getName() != null)
            onRetrieveUserSuccess();
        else {
            if(iter.getCreator().getUid().equals(currentUser.getUid())) {
                iter.setCreator(currentUser);
                onRetrieveUserSuccess();
            }else {
                userDao.setUserName(iter.getCreator());
            }
        }
    }


    public void onRetrieveUserSuccess() {
        imageDownloader.downloadImages();
        goToActivity(mainActivity, ItineraryDetailActivity.class, currentIter);
    }


    public void onRetrieveUserError() {
        mainActivity.onFail(mainActivity.getString(R.string.retrieve_itinerary_error));
    }


    public void onRetrievePhotosSuccess(ArrayList<byte[]> images) {
        photos.addAll(images);
        detailActivity.updateImages(images);
    }


    public void onRetrievePhotosError() {
        //TODO: SHOW ERROR ON DETAIL ACTIVITY
    }


    public void onRetrievePhotosEnd() {
        //TODO: SHOW ON DETAIL ACTIVITY MESSAGE <NO MORE PHOTO FOUND>
    }


    /**************
     * PHOTO UTILS
     *************/

    public void uploadPhoto(byte[] photo) {
        uploadingPhoto = photo;
        imageUploader.uploadImage(currentIter.getIterId(), photo);
    }


    public void onUploadPhotoFinish(boolean success) {

        if(success){
            photos.add(0, uploadingPhoto);
            detailActivity.updateImages(photos);
            followItineraryActivity.onPhotoUploadFinish(true);
        }else{
            followItineraryActivity.onPhotoUploadFinish(false);
        }

    }


    public HashMap<byte[], GeoPoint> calculatePhotoPosition() {

        ImageUtilities imageUtilities = new ImageUtilities();
        HashMap<byte[], GeoPoint> pointOfInterests = new HashMap<>();


        for (byte[] imageBytes : photos) {
            double[] coordinates = imageUtilities.getImageLocation(imageBytes);
            if (coordinates != null) {
                    pointOfInterests.put(imageBytes, new GeoPoint(coordinates[0], coordinates[1]));
            }
        }

        return pointOfInterests;

    }


    public HashMap<byte[], GeoPoint> calculatePhotoPosition(ArrayList<byte[]> imagesBytes) {
        photos = imagesBytes;
        return calculatePhotoPosition();
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

    public void setFollowItineraryActivity(FollowItineraryActivity followItineraryActivity) {
        this.followItineraryActivity = followItineraryActivity;
    }

}


