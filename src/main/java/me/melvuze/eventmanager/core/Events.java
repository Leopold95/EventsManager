package me.melvuze.eventmanager.core;

import me.melvuze.eventmanager.EventManager;
import me.melvuze.eventmanager.enums.EventType;
import me.melvuze.eventmanager.model.EventLocation;
import me.melvuze.eventmanager.model.EventModel;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Array;
import java.time.LocalTime;
import java.util.ArrayList;

public class Events {
    private EventManager plugin;

    private ArrayList<EventModel> events;

    public Events(EventManager plugin){
        this.plugin = plugin;
    }

    public ArrayList<EventModel> getEvents(){
        return events;
    }

    public void executeEvent(EventModel event){
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), event.activationCommand());
    }

    public void loadEvents(){
        events = new ArrayList<>();

        ConfigurationSection eventsSection = Config.getSection("events");

        if(eventsSection == null || eventsSection.getKeys(false).isEmpty()) {
            plugin.getServer().getLogger().warning(Config.getMessage("cant-find-events-config-section"));
            return;
        }

        for(String key: eventsSection.getKeys(false)){
            try {
                events.add(new EventModel(
                        false,
                        Config.getString(key + ".name"),
                        EventType.valueOf(Config.getString(key + ".type")),
                        Config.getString(key + ".description"),
                        LocalTime.parse(Config.getString(key + ".run-time")),
                        Config.getString(key + ".activation-command"),
                        new EventLocation(
                                Config.getInt(key + ".location.coords.x"),
                                Config.getInt(key + ".location.coords.y"),
                                Config.getInt(key + ".location.coords.z"),
                                Config.getString(key + ".location.world-name"))
                ));
            }
            catch (Exception exp){
                plugin.getLogger().warning(
                        Config.getMessage("cant-initialise-event-data").replace("%event_name%", Config.getString(key + ".name"))
                );
            }
        }




    }
}
