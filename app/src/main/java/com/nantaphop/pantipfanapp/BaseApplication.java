package com.nantaphop.pantipfanapp;

import com.activeandroid.app.Application;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import org.androidannotations.annotations.EApplication;

/**
 * Created by nantaphop on 26-Jul-14.
 */
@EApplication
public class BaseApplication extends Application {

    static private Bus eventBus = null;
    static private Gson gson = null;

    public static Bus getEventBus(){
        if(eventBus == null){
            eventBus = new Bus(ThreadEnforcer.ANY);
        }
        return eventBus;
    }

    public static Gson getGson(){
        if(gson == null){
            gson = new GsonBuilder().setDateFormat("MM/dd/yyyy HH:mm:ss").create();
        }
        return gson;
    }
}
