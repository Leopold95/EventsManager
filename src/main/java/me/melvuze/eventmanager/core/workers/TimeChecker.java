package me.melvuze.eventmanager.core.workers;

import me.melvuze.eventmanager.EventManager;
import me.melvuze.eventmanager.abstraction.RepeatingTask;
import me.melvuze.eventmanager.core.Config;
import me.melvuze.eventmanager.model.EventModel;

import java.time.LocalTime;
import java.util.Optional;
import java.util.stream.Collectors;

public class TimeChecker {
    private EventManager plugin;
    public TimeChecker(EventManager plugin){
        this.plugin = plugin;
    }

    public void run(){
        new RepeatingTask(plugin, 0, Config.getInt("time-check-delay")) {
            @Override
            public void run() {
                LocalTime currentTime = LocalTime.now();

                boolean currentTimeEventExists = plugin.events.getEvents().stream().anyMatch(event -> event.runTime() == currentTime);

                if(!currentTimeEventExists)
                    return;

                Optional<EventModel> eventOption = plugin.events.getEvents().stream().filter(e -> e.runTime() == currentTime).findAny();

                if(eventOption.isEmpty())
                    return;

                plugin.events.executeEvent(eventOption.get());
            }
        };
    }
}
