package com.worldcretornica.zachboraplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ZachBoraListener implements Listener {

    private ZachBoraPlugin plugin;

    public ZachBoraListener(ZachBoraPlugin instance) {
        plugin = instance;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(final PlayerMoveEvent event) {
        Location to = event.getTo();
        Location from = event.getFrom();
        Location area1 = plugin.loc1;
        Location area2 = plugin.loc2;
        Player p1 = event.getPlayer();
        World w = p1.getWorld();

        if (ZachBoraPlugin.isInside(to, area1, area2)) {
            if (!ZachBoraPlugin.isInside(from, area1, area2)) {
                ArenaPlayerEnter apeevent = new ArenaPlayerEnter(w, area1, area2, p1);

                Bukkit.getPluginManager().callEvent(apeevent);

                if (apeevent.isCancelled()) {
                    event.setCancelled(true);
                    p1.sendMessage(ChatColor.RED + "You cannot enter the arena");
                } else {
                    p1.sendMessage(ChatColor.BLUE + "YOU HAVE ENTERED THE ARENA (" + plugin.nbpeople + " PARTICIPANTS)");

                    if(!plugin.countdownstarted) {
                        //Start countdown
                        for (int seconds = 0; seconds <= 10; seconds = seconds + 2) {
                            plugin.gamestarttaskid.add(Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new ArenaStartTimer(10 - seconds, area1, area2, plugin), seconds * 20));
                        }
                    }
                }
            }
        } else {
            if (ZachBoraPlugin.isInside(from, area1, area2)) {
                ArenaPlayerLeave aplevent = new ArenaPlayerLeave(w, area1, area2, p1);

                Bukkit.getPluginManager().callEvent(aplevent);

                if (aplevent.isCancelled()) {
                    event.setCancelled(true);
                    p1.sendMessage(ChatColor.RED + "You cannot leave the arena");
                } else {
                    p1.sendMessage(ChatColor.BLUE + "YOU HAVE LEFT THE ARENA (" + plugin.nbpeople + " PARTICIPANTS)");
                    
                    if (plugin.nbpeople == 0) {
                        plugin.cancelArena();
                    }
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onArenaPlayerEnter(final ArenaPlayerEnter event) {
        if(plugin.gamestarted) {
            //event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onArenaPlayerEnterMonitor(final ArenaPlayerEnter event) {
        plugin.nbpeople += 1;
        
        Player p1 = event.getPlayer();
        
        for(Player p : event.getArenaPlayers()) {
            if(!p.equals(p1)) {
                p.sendMessage(ChatColor.BLUE + p1.getDisplayName() + " HAS ENTERED THE ARENA (" + plugin.nbpeople + " PARTICIPANTS)");
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onArenaPlayerLeave(final ArenaPlayerLeave event) {
        if(plugin.gamestarted) {
            //event.setCancelled(true);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onArenaPlayerLeaveMonitor(final ArenaPlayerLeave event) {
        plugin.nbpeople -= 1;

        Player p1 = event.getPlayer();

        for (Player p : event.getArenaPlayers()) {
            if (!p.equals(p1)) {
                p.sendMessage(ChatColor.BLUE + p1.getDisplayName() + " HAS LEFT THE ARENA (" + plugin.nbpeople + " PARTICIPANTS)");
            }
        }
    }
}
