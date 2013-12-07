package com.worldcretornica.zachboraplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
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

        Player p1 = event.getPlayer();
        World w = p1.getWorld();

        if (moved(to, from, p1, w)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        Player p1 = event.getEntity();
        World w = p1.getWorld();
        Location to = w.getSpawnLocation();
        Location from = p1.getLocation();

        moved(to, from, p1, w);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityKill(final EntityDeathEvent event) {
        Entity e0 = event.getEntity();
        Location l = e0.getLocation();
        Location area1 = plugin.loc1;
        Location area2 = plugin.loc2;
        World w = l.getWorld();

        if (ZachBoraPlugin.isInside(l, area1, area2)) {
            for (Entity e : w.getEntities()) {
                if (e instanceof LivingEntity && !e.isDead() && !(e instanceof Player)) {
                    Location loc = e.getLocation();
                    if (ZachBoraPlugin.isInside(loc, area1, area2)) {
                        return;
                    }
                }
            }

            plugin.startWave();
        }
    }

    private boolean moved(Location to, Location from, Player p1, World w) {
        boolean cancelled = false;
        Location area1 = plugin.loc1;
        Location area2 = plugin.loc2;

        if (ZachBoraPlugin.isInside(to, area1, area2)) {
            if (!ZachBoraPlugin.isInside(from, area1, area2)) {
                ArenaPlayerEnter apeevent = new ArenaPlayerEnter(w, area1, area2, p1);

                Bukkit.getPluginManager().callEvent(apeevent);

                if (apeevent.isCancelled()) {
                    cancelled = true;
                    p1.sendMessage(ChatColor.RED + "You cannot enter the arena");
                } else {
                    p1.sendMessage(ChatColor.BLUE + "YOU HAVE ENTERED THE ARENA (" + plugin.nbpeople + " PARTICIPANTS)");

                    if (!plugin.countdownstarted) {
                        // Start countdown
                        plugin.countdownstarted = true;
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
                    cancelled = true;
                    p1.sendMessage(ChatColor.RED + "You cannot leave the arena");
                } else {
                    p1.sendMessage(ChatColor.BLUE + "YOU HAVE LEFT THE ARENA (" + plugin.nbpeople + " PARTICIPANTS)");

                    if (plugin.nbpeople == 0) {
                        plugin.cancelArena();
                    }
                }
            }
        }

        return cancelled;
    }

    /*
     * @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
     * public void onArenaPlayerEnter(final ArenaPlayerEnter event) { if
     * (plugin.gamestarted) { event.setCancelled(true); } }
     */

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onArenaPlayerEnterMonitor(final ArenaPlayerEnter event) {
        plugin.nbpeople += 1;

        Player p1 = event.getPlayer();

        for (Player p : event.getArenaPlayers()) {
            if (!p.equals(p1)) {
                p.sendMessage(ChatColor.BLUE + p1.getDisplayName() + " HAS ENTERED THE ARENA (" + plugin.nbpeople + " PARTICIPANTS)");
            }
        }
    }

    /*
     * @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
     * public void onArenaPlayerLeave(final ArenaPlayerLeave event) { if
     * (plugin.gamestarted) { event.setCancelled(true); } }
     */

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
