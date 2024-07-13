package me.melvuze.eventmanager.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.melvuze.eventmanager.core.Time;

import java.time.Duration;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class EventModel {
    @Setter
    private boolean isGone;

    @Setter
    private boolean wasAnnounced;

    @Setter
    private boolean wasActivated;

    private String name;
    private String type;
    private String description;
    private int activeTime;
    private LocalTime runTime;
    private String activationCommand;
    private EventLocation location;

    /**
     * Проверяет активен ли текущее событие
     * @param currentTime текущее вермя
     * @return true | false
     */
    public boolean isActive(LocalTime currentTime){
        LocalTime endTime = runTime.plusSeconds(activeTime);
        return currentTime.isBefore(runTime) && currentTime.isAfter(endTime);
    }
}
