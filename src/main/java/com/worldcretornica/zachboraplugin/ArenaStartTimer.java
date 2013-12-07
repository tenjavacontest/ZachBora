package com.worldcretornica.zachboraplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaStartTimer extends BukkitRunnable {

    private final int seconds;
    private final Location loc1;
    private final Location loc2;
    private final ZachBoraPlugin plugin;

    public ArenaStartTimer(int seconds, Location loc1, Location loc2, ZachBoraPlugin instance) {
        this.seconds = seconds;
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.plugin = instance;
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().equals(loc1.getWorld())) {

                Location ploc = p.getLocation();

                if (ZachBoraPlugin.isInside(ploc, loc1, loc2)) {
                    if (seconds == 0) {
                        p.sendMessage("GAME STARTED !");
                    } else {
                        p.sendMessage("GAME STARTING IN " + seconds + " SECONDS");
                    }
                }
            }
        }

        if (seconds <= 0) {
            plugin.startGame();
        }
    }

}
