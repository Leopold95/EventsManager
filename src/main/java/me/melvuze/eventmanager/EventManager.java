package me.melvuze.eventmanager;

import me.melvuze.eventmanager.core.Config;
import org.bukkit.plugin.java.JavaPlugin;

public final class EventManager extends JavaPlugin {

    @Override
    public void onEnable() {
        Config.register(this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
