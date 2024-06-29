package me.melvuze.eventmanager;

import me.melvuze.eventmanager.commands.EventCommand;
import me.melvuze.eventmanager.core.Config;
import me.melvuze.eventmanager.core.events.Events;
import me.melvuze.eventmanager.core.workers.TimeChecker;
import org.bukkit.plugin.java.JavaPlugin;

public final class EventManager extends JavaPlugin {
    public Events events;

    private TimeChecker timeChecker;

    @Override
    public void onEnable() {
        Config.register(this);

        events = new Events(this);
        events.loadEvents();

        getCommand("event").setExecutor(new EventCommand());
        getCommand("event").setTabCompleter(new EventCommand());

        timeChecker.run();
    }
}
