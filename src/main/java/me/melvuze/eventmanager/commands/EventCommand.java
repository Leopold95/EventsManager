package me.melvuze.eventmanager.commands;

import me.melvuze.eventmanager.EventManager;
import me.melvuze.eventmanager.core.Config;
import me.melvuze.eventmanager.enums.Commands;
import me.melvuze.eventmanager.enums.Permissions;
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
//        //event <arg>
//        if(args.length == 1){
//            String arg = args[0];
//
//            //event list
//            if(arg.equals(Commands.EVENT_LIST))
//                return plugin.events.getPossibleTypes();
//
//        }

        return List.of(Commands.EVENT_LIST);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player) && !Config.getBoolean("can-console-command")){
            sender.sendMessage(Config.getMessage("cant-console-command"));
            return true;
        }

        if(args.length == 0)
            return false;

        if(!sender.hasPermission(Permissions.EVENT)){
            sender.sendMessage(Config.getMessage("permisssion-bad"));
            return true;
        }


        listNearestOrActiveEvents((Player) sender);


        switch (args[0]){
            //event list
            case Commands.EVENT_LIST:{
//                //event list <type>
//                if(args.length == 2){
//                    String type = args[1];
//
//                    if(!plugin.events.getPossibleTypes().contains(type)){
//                        sender.sendMessage(Config.getMessage("bad-event-type"));
//                        return true;
//                    }
//
//                    listNearestOrActiveEvents((Player) sender, type);
//                    break;
//                }
//
//                //event list
//                listNearestOrActiveEvents((Player) sender);
//                break;
            }
        }


        return true;
    }

    /**
     * Выводит список ближайших ивентов для игрока
     * @param sender игрок
     * @param type тип события
     */
    private void listNearestOrActiveEvents(Player sender, String type){
        List<EventModel> nearest = plugin.events.getNearestEvents().stream().filter(e -> e.getType().equals(type)).toList();
        printEvents(nearest, sender);
    }

    /**
     * Выводит список ближайших ивентов для игрока
     * @param sender игрок
     */
    private void listNearestOrActiveEvents(Player sender){
        List<EventModel> nearest = plugin.events.getNearestEvents();
        printEvents(nearest, sender);
    }

    private void printEvents(List<EventModel> nearest, Player sender){
        if(nearest.isEmpty()){
            int nearestMaxTime = Config.getInt("time-to-nearest-events");
            String message = Config.getMessage("event.no-nearest").replace("%nearest_time%", String.valueOf(nearestMaxTime));
            sender.sendMessage(message);

            return;
        }

        LocalTime currentTime = LocalTime.now();

        for(EventModel event:  nearest){
            //собтыие сейчас активно
            if(event.isActive(currentTime)){
                String message = Config.getMessage("event.active").replace("%event_name%", event.getName());
                sender.sendMessage(message);
                continue;
            }

            //событие скоро начнется
            long timeLeft = Duration.between(currentTime, event.getRunTime()).getSeconds();

            String message = Config.getMessage("event.nearest")
                    .replace("%event_name%", event.getName())
                    .replace("%start_delay%", String.valueOf(timeLeft));
            sender.sendMessage(message);
        }
    }
}
