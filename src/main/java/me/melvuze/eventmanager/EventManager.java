package me.melvuze.eventmanager;

import me.melvuze.eventmanager.commands.EventCommand;
import me.melvuze.eventmanager.core.Config;
import me.melvuze.eventmanager.core.events.Events;
import me.melvuze.eventmanager.core.workers.TimeChecker;
import me.melvuze.eventmanager.enums.Commands;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public final class EventManager extends JavaPlugin {
    public Events events;

    private TimeChecker timeChecker;

    @Override
    public void onEnable() {
        Config.register(this);

        events = new Events(this);
        timeChecker = new TimeChecker(this);

        events.loadEvents();

        getCommand(Commands.EVENT).setExecutor(new EventCommand(this));
        getCommand(Commands.EVENT).setTabCompleter(new EventCommand(this));

        timeChecker.run();
    }
}
