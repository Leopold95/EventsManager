package me.melvuze.eventmanager.core;

import me.melvuze.eventmanager.EventManager;
import me.melvuze.eventmanager.model.EventModel;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Events {
    private EventManager plugin;

    private ArrayList<EventModel> events;

    public Events(EventManager plugin){
        this.plugin = plugin;
    }


    public void loadEvents(){

    }
}
