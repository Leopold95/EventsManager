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

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

public class EventCommand implements CommandExecutor, TabCompleter {
    private EventManager plugin;

    public EventCommand(EventManager plugin){
        this.plugin = plugin;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        //event <arg>
        if(args.length == 1){
            String arg = args[0];

            //event list
            if(arg.equals(Commands.EVENT_LIST))
                return plugin.events.getEventTypes();

        }

        return List.of();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player) && !Config.getBoolean("can-console-command")){
            sender.sendMessage(Config.getMessage("cant-console-command"));
            return true;
        }

        if(args.length == 0)
            return false;


        switch (args[0]){
            //event list
            case Commands.EVENT_LIST:{
                //event list <type>
//                if(args.length == 2){
//                    String eventType = args[1];
//                }


                listEvents((Player) sender);
                break;
            }
        }


        return true;
    }

    private void listEvents(Player sender){
        List<EventModel> nearest = plugin.events.getNearestEvents();

        if(nearest.isEmpty()){
            int nearestMaxTime = Config.getInt("time-to-nearest-events");
            String message = Config.getMessage("event.no-nearest").replace("%nearest_time%", String.valueOf(nearestMaxTime));
            sender.sendMessage(message);

            return;
        }

        for(EventModel event:  nearest){
            if(event.isGone())
                continue;

            long timeLeft = Duration.between(LocalTime.now(), event.getRunTime()).getSeconds();

            if(timeLeft <= -1){
                String message = Config.getMessage("event.active").replace("%event_name%", event.getName());
                sender.sendMessage(message);
                return;
            }

            String message = Config.getMessage("event.nearest")
                    .replace("%event_name%", event.getName())
                    .replace("%start_delay%", String.valueOf(timeLeft));
            sender.sendMessage(message);
        }
    }
}
