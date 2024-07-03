package me.melvuze.eventmanager.core.events;

import lombok.Getter;
import me.melvuze.eventmanager.EventManager;
import me.melvuze.eventmanager.core.Config;
import me.melvuze.eventmanager.enums.EventType;
import me.melvuze.eventmanager.model.EventLocation;
import me.melvuze.eventmanager.model.EventModel;
import org.bukkit.configuration.ConfigurationSection;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class Events {
    private EventManager plugin;

    @Getter
    private ArrayList<EventModel> events;
    @Getter
    private List<String> possibleTypes;

    private final String EVENT_SEC = "events";

    public Events(EventManager plugin){
        this.plugin = plugin;
    }



    /**
     * Вернет список ближайших событий. Время сравнения настроивается в конфиге
     * @return список ближайших событий
     */
    public List<EventModel> getNearestEvents(){
        LocalTime now = LocalTime.now();

        return events.stream()
                .filter(e -> Duration.between(e.getRunTime(), now).getSeconds() <= Config.getInt("time-to-nearest-events"))
                .collect(Collectors.toList());
    }

    /**
     * Начать ивент
     * @param event данные ивента
     */
    public void executeEvent(EventModel event){
        event.setGone(true);
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), event.getActivationCommand());
    }

    /**
     * Инициализирует конфиг с ивентами
     */
    public void loadEvents(){
        initPossibleTypes();

        events = new ArrayList<>();

        ConfigurationSection eventsSection = Config.getSection(EVENT_SEC);

        //базвовая проверка кфг секции
        if(eventsSection == null || eventsSection.getKeys(false).isEmpty()) {
            plugin.getServer().getLogger().warning(Config.getMessage("cant-find-events-config-section"));
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        //пытаем инициализирвоть все ивенты из конфига
        for(String key: eventsSection.getKeys(false)){
            try {
                EventModel model = new EventModel(
                    false,
                    false,
                    Config.getString(EVENT_SEC + "." + key + ".name"),
                    Config.getString(EVENT_SEC + "." + key + ".type"),
                    Config.getString(EVENT_SEC + "." + key + ".description"),
                    Config.getInt(EVENT_SEC + "." + key + ".active-time"),
                    LocalTime.parse(Config.getString(EVENT_SEC + "." +key + ".run-time")),
                    Config.getString(EVENT_SEC + "." +key + ".activation-command"),
                    new EventLocation(
                            Config.getInt(EVENT_SEC + "." +key + ".location.coords.x"),
                            Config.getInt(EVENT_SEC + "." +key + ".location.coords.y"),
                            Config.getInt(EVENT_SEC + "." +key + ".location.coords.z"),
                            Config.getString(EVENT_SEC + "." +key + ".location.world-name"))
                );


                if(checkEventAccurate(model))
                    events.add(model);
            }
            catch (Exception exp){
                exp.printStackTrace();

                plugin.getLogger().warning(
                    Config.getMessage("cant-initialise-event-data")
                            .replace("%error%", exp.getMessage()
                    )
                );
            }
        }


        plugin.getLogger().log(Level.FINE, Config.getMessage("events-total").replace("%count%", String.valueOf(events.size())));
    }

    /**
     * Проверяет правильность заполнени неокторой инфрмации об ивенте
     * @param model ивент
     * @return корретный ивент или нет
     */
    private boolean checkEventAccurate(EventModel model){
        if(!possibleTypes.contains(model.getType())){
            String message = Config.getMessage("event.bad-init-type")
                .replace("%event_name%",  model.getName())
                .replace("%run_time%% ", model.getRunTime().toString());

            plugin.getLogger().warning(message);
            return false;
        }


        return true;
    }

    /**
     * Прогружает списк с возможными типами событий. Событие без типа нельзя создать
     */
    private void initPossibleTypes(){
        possibleTypes = new ArrayList<>();

        List<String> eventTypes = Config.getStringList("event-possible-types");

        if(eventTypes == null || eventTypes.isEmpty()){
            plugin.getLogger().warning(Config.getMessage("bad-event-types"));
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        possibleTypes = eventTypes;
    }
}
