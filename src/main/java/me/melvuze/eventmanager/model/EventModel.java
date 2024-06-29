package me.melvuze.eventmanager.model;

import me.melvuze.eventmanager.enums.EventType;

import java.time.LocalTime;

public record EventModel(
    boolean isGone,

    String name,
    EventType type,
    String description,
    LocalTime runTime,
    String activationCommand,
    EventLocation location
){

}
