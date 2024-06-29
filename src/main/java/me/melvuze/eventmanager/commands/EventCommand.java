package me.melvuze.eventmanager.commands;

import me.melvuze.eventmanager.EventManager;
import me.melvuze.eventmanager.core.Config;
import me.melvuze.eventmanager.enums.Commands;
import me.melvuze.eventmanager.model.EventModel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalTime;
import java.util.List;

public class EventCommand implements CommandExecutor, TabCompleter {
    private EventManager plugin;

    public EventCommand(EventManager plugin){
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return List.of();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player) && !Config.getBoolean("can-console-command")){
            sender.sendMessage(Config.getMessage("cant-console-command"));
            return true;
        }


        switch (args[0]){
            case Commands.EVENT_LIST:
                listEvents((Player) sender);
                break;
        }


        return true;
    }

    private void listEvents(Player sender){
        for(EventModel event:  plugin.events.getNearestEvents()){
            sender.sendMessage(
                    Config.getMessage("event.nearest")
                            .replace("%event_name%", event.name())
                            .replace("%start_delay%", String.valueOf(LocalTime.now().compareTo(event.runTime())))
            );
        }
    }
}
