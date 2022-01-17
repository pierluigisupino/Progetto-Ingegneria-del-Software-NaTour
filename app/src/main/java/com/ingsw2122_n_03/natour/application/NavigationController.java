package com.ingsw2122_n_03.natour.application;

import android.app.Activity;
import android.content.Intent;

import com.ingsw2122_n_03.natour.model.Itinerary;


import java.util.ArrayList;
import java.util.HashMap;

public class NavigationController {

    protected NavigationController() {}


    public void goToActivity(Activity source, Class<?> destination){

        Intent intent = new Intent(source, destination);
        source.startActivity(intent);

    }

    public void goToActivity(Activity source, Class<?> destination, HashMap<String, String> extras){

        Intent intent = new Intent(source, destination);

        for(String key : extras.keySet()){
            intent.putExtra(key, extras.get(key));
        }

        source.startActivity(intent);

    }

    public void goToActivity(Activity source, Class<?> destination, Itinerary itinerary){

        Intent intent = new Intent(source, destination);
        intent.putExtra("itinerary", itinerary);
        source.startActivity(intent);

    }


    public void goToActivityAndFinish(Activity source, Class<?> destination){

        Intent intent = new Intent(source, destination);
        source.startActivity(intent);
        source.finish();

    }

    public void goToActivityAndFinish(Activity source, Class<?> destination, HashMap<String, String> extras){

        Intent intent = new Intent(source, destination);

        for(String key : extras.keySet()){
            intent.putExtra(key, extras.get(key));
        }

        source.startActivity(intent);
        source.finish();

    }

    public void goToActivityAndFinish(Activity source, Class<?> destination, ArrayList<Itinerary> itineraries){

        Intent intent = new Intent(source, destination);
        intent.putExtra("itineraries", itineraries);

        source.startActivity(intent);
        source.finish();
    }
    
}
