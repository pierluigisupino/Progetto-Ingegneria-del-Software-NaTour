package com.ingsw2122_n_03.natour.application;

import android.app.Activity;
import android.content.Intent;

import java.util.HashMap;

public class Controller {

    protected Controller() {}

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
    
}
