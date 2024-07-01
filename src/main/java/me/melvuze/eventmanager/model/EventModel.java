package me.melvuze.eventmanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.melvuze.eventmanager.enums.EventType;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class EventModel {

    @Setter
    private boolean isGone;

    private String name;
    private String type;
    private String description;
    private int activeTime;
    private LocalTime runTime;
    private String activationCommand;
    private EventLocation location;
}
