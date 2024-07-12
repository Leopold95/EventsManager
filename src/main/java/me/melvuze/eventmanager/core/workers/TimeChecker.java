package me.melvuze.eventmanager.core.workers;

import me.melvuze.eventmanager.EventManager;
import me.melvuze.eventmanager.abstraction.RepeatingTask;
import me.melvuze.eventmanager.core.Config;
import me.melvuze.eventmanager.model.EventModel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
                LocalTime currentTime = LocalTime.now();

                checkEventAnnouncments();
                checkCurrentEventsToExecute(currentTime);
            }
        };

        plugin.getLogger().log(Level.FINE, Config.getMessage("time-checker-task-started"));
    }

    /**
     * анонсирование ивентов
     */
    private void checkEventAnnouncments(){
        for(EventModel event: plugin.events.getEvents()){
            if(event.isWasAnnounced() || event.isGone())
                continue;

            LocalTime canAnnFrom = LocalTime.now().plusSeconds(Config.getInt("announcements.from-time"));
            LocalTime canAnnTo = LocalTime.now().plusSeconds(Config.getInt("announcements.to-time"));

            LocalTime annTime = event.getRunTime().plusSeconds(Config.getInt("announcements.time"));

            if(annTime.isAfter(canAnnFrom) && annTime.isBefore(canAnnTo)){
                event.setWasAnnounced(true);

                for(Player player: Bukkit.getOnlinePlayers()){
                    player.sendMessage(createAnnMessage(event));
                }
            }
        }
    }

    private String createAnnMessage(EventModel eventModel){
        return Config.getMessage("event.announcement")
                .replace("%name%", eventModel.getName())
                .replace("%type%", eventModel.getType())
                .replace("%description%", eventModel.getDescription())
                .replace("%loc_x%", String.valueOf(eventModel.getLocation().x()))
                .replace("%loc_y%", String.valueOf(eventModel.getLocation().y()))
                .replace("%loc_z%", String.valueOf(eventModel.getLocation().z()))
                .replace("%world_name%", eventModel.getLocation().worldName());
    }

    /**
     * Запуск такущего ивента
     */
    private void checkCurrentEventsToExecute(LocalTime currentTime){
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

    /**
     * предикат для текущего ивента
     * @param event
     * @param currentTime
     * @return
     */
    private boolean predicate(EventModel event, LocalTime currentTime){
        long difference = Duration.between(event.getRunTime(), currentTime).toSeconds();

        if(difference <= -1)
            return false;

        return difference <= maxTime;
    }
}
