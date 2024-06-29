package me.melvuze.eventmanager.model;

import me.melvuze.eventmanager.enums.EventType;

public record EventModel(
    boolean isGone,

    String name,
    EventType type,
    String description,
    EventLocation location
){

}
