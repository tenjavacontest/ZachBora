package com.worldcretornica.zachboraplugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

abstract class ArenaPlayerMove extends Event implements Cancellable {

    public static HandlerList handlers = new HandlerList();
    protected World w;
    protected Location l1;
    protected Location l2;
    protected Player p;
    protected boolean cancelled;

    public ArenaPlayerMove(World world, Location location1, Location location2, Player player) {
        w = world;
        l1 = location1;
        l2 = location2;
        p = player;
        cancelled = false;
    }

    public Player getPlayer() {
        return p;
    }

    public World getWorld() {
        return w;
    }

    public Location getLocation1() {
        return l1;
    }

    public Location getLocation2() {
        return l2;
    }

    public List<Player> getArenaPlayers() {

        List<Player> players = new ArrayList<>();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().equals(w)) {

                Location ploc = p.getLocation();

                if (ZachBoraPlugin.isInside(ploc, l1, l2)) {
                    players.add(p);
                }
            }
        }

        return players;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
