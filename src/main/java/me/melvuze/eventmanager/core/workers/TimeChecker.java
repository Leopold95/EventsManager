package me.melvuze.eventmanager.core.workers;

import me.melvuze.eventmanager.EventManager;
import me.melvuze.eventmanager.abstraction.RepeatingTask;
import me.melvuze.eventmanager.core.Config;
import me.melvuze.eventmanager.model.EventModel;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class TimeChecker {
    private EventManager plugin;

    private final int maxTime = Config.getInt("max-event-execute-time-difference");

    public TimeChecker(EventManager plugin){
        this.plugin = plugin;
    }

    public void run(){
        new RepeatingTask(plugin, 0, Config.getInt("time-check-delay")) {
            @Override
            public void run() {
                System.out.println("updating....");

                LocalTime currentTime = LocalTime.now();

                boolean currentTimeEventExists = plugin.events.getEvents().stream().anyMatch(event -> predicate(event, currentTime));

                if(!currentTimeEventExists)
                    return;

                List<EventModel> eventOption = plugin.events.getEvents().stream().filter(event -> predicate(event, currentTime)).toList();

                if(eventOption.isEmpty())
                    return;

                for(EventModel model: eventOption){
                    plugin.events.executeEvent(model);
                }
            }
        };

        plugin.getLogger().log(Level.FINE, Config.getMessage("time-checker-task-started"));
    }

    private boolean predicate(EventModel event, LocalTime currentTime){
        long difference = Duration.between(event.getRunTime(), currentTime).toSeconds();

        if(difference <= -1)
            return false;

        return difference <= maxTime;
    }
}
