package com.ingsw2122_n_03.natour.application;

import android.annotation.SuppressLint;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.infastructure.exceptions.RetrieveItinerariesException;
import com.ingsw2122_n_03.natour.infastructure.exceptions.SetUpException;
import com.ingsw2122_n_03.natour.infastructure.implementations.ImageDownloader;
import com.ingsw2122_n_03.natour.infastructure.implementations.ImageUploader;
import com.ingsw2122_n_03.natour.infastructure.implementations.ItineraryDaoImplementation;
import com.ingsw2122_n_03.natour.infastructure.interfaces.ItineraryDaoInterface;
import com.ingsw2122_n_03.natour.model.Itinerary;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.model.WayPoint;
import com.ingsw2122_n_03.natour.presentation.ErrorActivity;
import com.ingsw2122_n_03.natour.presentation.SplashActivity;
import com.ingsw2122_n_03.natour.presentation.dialogs.LoadingDialog;
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

    public void setUp(User loggedUser) {

        currentUser = loggedUser;
        currentUser.setName(splashActivity.getResources().getString(R.string.current_user_name_text));
        try {
            itineraries = itineraryDao.getSetUpItineraries();
            goToActivityAndFinish(splashActivity, MainActivity.class, itineraries);
            MessageController.getInstance().setUpMessages(currentUser);
        } catch (RetrieveItinerariesException e) {
            onSetUpError(true);
        } catch (SetUpException e) {
            onSetUpError(false);
        }

    }


    public void onSetUpError(boolean isResolvableError) {
        if(isResolvableError) {
            goToActivityAndFinish(splashActivity, MainActivity.class);
            MessageController.getInstance().setUpMessages(currentUser);
        }else {
            goToActivityAndFinish(splashActivity, ErrorActivity.class);
        }
    }


    /******************
     * GET ITINERARIES
     ******************/


    public void updateItineraries(){

        try{
            itineraries = itineraryDao.getRecentItineraries();
            mainFragment.updateItineraries(itineraries);
            if(mainFragment.isVisible())
                mainActivity.onSuccess(mainActivity.getResources().getString(R.string.update));
        }catch(RetrieveItinerariesException e) {
            onUpdateError();
        }

    }


    public void retrieveItineraries() {

        if(itineraries.isEmpty())
            return;

        try{
            itineraries.addAll(itineraryDao.getOlderItineraries(itineraries.get(itineraries.size()-1).getIterId()));
            mainFragment.updateItineraries(itineraries);
        }catch(RetrieveItinerariesException e) {
            onUpdateError();
        }

    }


    public void onUpdateError(){
        mainFragment.onError();
        if(mainFragment.isVisible())
            mainActivity.onFail(mainActivity.getString(R.string.generic_error));
    }


    /*****************
     * POST ITINERARY
     ****************/

    @SuppressLint("NewApi")
    public void insertItinerary(String name, String description, int difficulty, LocalTime duration, ArrayList<byte[]> imagesBytes, ArrayList<GeoPoint> waypoints) {

        loadingDialog = new LoadingDialog(addItineraryActivity, addItineraryActivity.getString(R.string.loading_text_add_itinerary));
        loadingDialog.startLoading();

        this.photos = imagesBytes;

        ArrayList<WayPoint> wayPointArrayList = new ArrayList<>();
        for(GeoPoint g : waypoints){
            wayPointArrayList.add(new WayPoint(g.getLatitude(), g.getLongitude()));
        }
        currentIter = new Itinerary(name, difficulty, duration, wayPointArrayList.get(0), currentUser, new Date());
        currentIter.setModifiedSince(System.currentTimeMillis());
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
            onItineraryInsertFinish();
        else{
            mainActivity.onWaitingBackgroundTask(mainActivity.getString(R.string.photo_upload_wait));
            imageUploader.uploadImages(iterID, photos);
        }

    }


    public void onItineraryInsertError() {
        loadingDialog.dismissDialog();
        addItineraryActivity.onFail(addItineraryActivity.getString(R.string.itinerary_insert_failure));
    }


    public void onItineraryInsertFinish() {
        mainActivity.onBackgroundTaskEnd();
        mainActivity.onSuccess(mainActivity.getString(R.string.itinerary_insert_success));
    }


    public void onUploadPhotosError() {
        mainActivity.onBackgroundTaskEnd();
        mainActivity.onFail(mainActivity.getString(R.string.photo_upload_failed));
    }


    /****************
     * ADMIN ACTIONS
     ****************/

    public void putItineraryByAdmin(String name, String description, int difficulty, LocalTime duration) {
        updatedIter = new Itinerary(currentIter);
        updatedIter.setName(name);
        updatedIter.setDescription(description);
        updatedIter.setDifficulty(difficulty);
        updatedIter.setDuration(duration);
        if(currentIter.equals(updatedIter)) {
            detailActivity.onSuccess(detailActivity.getString(R.string.feedback_no_changes));
            return;
        }
        updatedIter.setEditDate(new Date());
        loadingDialog = new LoadingDialog(detailActivity, detailActivity.getString(R.string.loading_text_update_itinerary));
        loadingDialog.startLoading();
        itineraryDao.putItineraryByAdmin(updatedIter);
    }


    public void deleteItinerary() {
        loadingDialog = new LoadingDialog(detailActivity, mainActivity.getString(R.string.loading_text_delete_itinerary));
        loadingDialog.startLoading();
        itineraryDao.deleteItinerary(currentIter.getIterId());
    }


    public void onDeleteItinerarySuccess() {
        itineraries.remove(currentIter);
        mainFragment.updateItineraries(itineraries);
        detailActivity.finish();
        loadingDialog.dismissDialog();
        mainActivity.onSuccess(mainActivity.getString(R.string.itinerary_deleted_text));
    }


    public void onDeleteItineraryError() {
        loadingDialog.dismissDialog();
        detailActivity.onFail(detailActivity.getString(R.string.itinerary_delete_failure));
    }


    /***************
     * PUT FEEDBACK
     **************/

    public void putItineraryByFeedback(LocalTime newDuration, int newDifficulty) {

        updatedIter = new Itinerary(currentIter);

        updatedIter.setDuration(currentIter.calculateAverageDuration(newDuration));
        updatedIter.setDifficulty(currentIter.calculateAverageDifficulty(newDifficulty));

        if((currentIter.getDifficulty() == updatedIter.getDifficulty()) && (currentIter.getDuration().compareTo(updatedIter.getDuration()) == 0))
            detailActivity.onSuccess(detailActivity.getString(R.string.feedback_no_changes));
        else {
            itineraryDao.putItineraryFromFeedback(updatedIter);
            loadingDialog = new LoadingDialog(detailActivity, detailActivity.getString(R.string.loading_text_update_itinerary));
            loadingDialog.startLoading();
        }

    }


    public void onUpdateItinerarySuccess() {
        int pos = itineraries.indexOf(currentIter);
        itineraries.remove(currentIter);
        currentIter = updatedIter;
        itineraries.add(pos, updatedIter);
        detailActivity.updateItineraryViews(updatedIter);
        mainFragment.updateItineraries(itineraries);
        loadingDialog.dismissDialog();
        detailActivity.onSuccess(detailActivity.getString(R.string.feedback_success_text));
    }


    public void onUpdateItineraryError(boolean itemChanged) {
        loadingDialog.dismissDialog();
        if(itemChanged) {
            detailActivity.onFail(detailActivity.getString(R.string.itinerary_update_warning));
            mainFragment.showUpdateHint();
        }else
            detailActivity.onFail(detailActivity.getString(R.string.generic_error));
    }


    /*************
     * NAVIGATION
     ************/

    public void onItineraryClick(int position) {

        currentIter = itineraries.get(position);
        photos.clear();
        imageDownloader.resetSession(currentIter.getIterId());
        retrieveItineraryPhotos();

        if(currentIter.getCreator().getUid().equals(currentUser.getUid()))
            currentIter.setCreator(currentUser);

        goToActivity(mainActivity, ItineraryDetailActivity.class, currentIter, currentUser);

    }


    public void retrieveItineraryPhotos() {
        imageDownloader.downloadImages();
    }


    public void onRetrievePhotoSuccess(byte[] image) {
        photos.add(image);
        detailActivity.updateImages(image);
    }


    public void onRetrievePhotosError() {
        detailActivity.onFail(detailActivity.getString(R.string.itinerary_photo_error));
    }

    public void interruptDownloadSession(){
        imageDownloader.interruptSession();
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
            detailActivity.updateImages(uploadingPhoto);
            followItineraryActivity.onPhotoUploadFinish(true);
        }else{
            followItineraryActivity.onPhotoUploadFinish(false);
        }

    }


    public HashMap<byte[], GeoPoint> calculatePhotosPosition() {

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


    public HashMap<byte[], GeoPoint> calculatePhotosPosition(ArrayList<byte[]> imagesBytes) {
        photos = imagesBytes;
        return calculatePhotosPosition();
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


