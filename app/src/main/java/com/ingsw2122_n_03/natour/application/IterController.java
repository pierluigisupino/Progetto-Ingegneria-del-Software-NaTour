package com.ingsw2122_n_03.natour.application;

import com.ingsw2122_n_03.natour.presentation.MainActivity;

public class IterController extends Controller {

    private static IterController instance = null;

    private MainActivity mainActivity;

    private IterController(){ }

    public static IterController getInstance() {
        if(instance == null){
            instance = new IterController();
        }
        return instance;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}
