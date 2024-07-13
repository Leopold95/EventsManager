package me.melvuze.eventmanager.core.workers;

import me.melvuze.eventmanager.EventManager;
import me.melvuze.eventmanager.abstraction.RepeatingTask;
import me.melvuze.eventmanager.core.Config;
import me.melvuze.eventmanager.core.Time;
import me.melvuze.eventmanager.model.EventModel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.TemporalUnit;
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
                LocalTime currentTime = LocalTime.now();

                checkEventAnnouncments(currentTime);
                checkCurrentEventsToExecute(currentTime);
            }
        };

        plugin.getLogger().log(Level.FINE, Config.getMessage("time-checker-task-started"));
    }

    /**
     * анонсирование ивентов
     */
    private void checkEventAnnouncments(LocalTime now){
        for(EventModel event: plugin.events.getEvents()){
            if(event.isWasAnnounced() || event.isActive(now))
                continue;

            int annTime = Config.getInt("announcements.time");

            if(Time.intoOneSecRange(now.plusSeconds(annTime), event.getRunTime())){
                event.setWasAnnounced(true);

                for(Player player: Bukkit.getOnlinePlayers()){
                    player.sendMessage(createAnnMessage(event, annTime));
                }
            }
        }
    }




    private String createAnnMessage(EventModel eventModel, long secondsToStart){
        return Config.getMessage("event.announcement")
                .replace("%name%", eventModel.getName())
                .replace("%type%", eventModel.getType())
                .replace("%description%", eventModel.getDescription())
                .replace("%loc_x%", String.valueOf(eventModel.getLocation().x()))
                .replace("%loc_y%", String.valueOf(eventModel.getLocation().y()))
                .replace("%loc_z%", String.valueOf(eventModel.getLocation().z()))
                .replace("%world_name%", eventModel.getLocation().worldName())
                .replace("%time%", String.valueOf(secondsToStart));
    }

    /**
     * Запуск такущего ивента
     */
    private void checkCurrentEventsToExecute(LocalTime currentTime){
        boolean currentTimeEventExists = plugin.events.getEvents().stream().anyMatch(e -> Time.intoOneSecRange(currentTime, e.getRunTime()));

        if(!currentTimeEventExists)
            return;

        List<EventModel> eventOption = plugin.events.getEvents().stream().filter(e -> Time.intoOneSecRange(currentTime, e.getRunTime())).toList();

        if(eventOption.isEmpty())
            return;

        for(EventModel model: eventOption){
            plugin.events.executeEvent(model);
        }
    }
}
