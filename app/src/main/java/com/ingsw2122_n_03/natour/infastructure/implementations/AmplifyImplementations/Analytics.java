package com.ingsw2122_n_03.natour.infastructure.implementations.AmplifyImplementations;

import com.amplifyframework.analytics.AnalyticsEvent;
import com.amplifyframework.core.Amplify;

public class Analytics {

    public static void recordPositiveEvent(String eventName){

        AnalyticsEvent event = AnalyticsEvent.builder()
                .name(eventName)
                .addProperty("Successful", true)
                .build();

        Amplify.Analytics.recordEvent(event);
    }

    public static void recordNegativeEvent(String eventName, String error){

        AnalyticsEvent event = AnalyticsEvent.builder()
                .name(eventName)
                .addProperty("Successful", false)
                .addProperty("error", error)
                .build();

        Amplify.Analytics.recordEvent(event);
    }

}
