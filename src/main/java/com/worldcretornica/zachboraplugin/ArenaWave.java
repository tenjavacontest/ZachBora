package com.worldcretornica.zachboraplugin;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaWave extends BukkitRunnable {

    private HashMap<EntityType, Integer> mobs;
    private Location loc1;
    private Location loc2;
    private int waveid;

    public ArenaWave(HashMap<EntityType, Integer> mobs, Location loc1, Location loc2, int waveid) {
        this.mobs = mobs;
        this.loc1 = loc1;
        this.loc2 = loc2;
        this.waveid = waveid;
    }

    @Override
    public void run() {
        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        int maxY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int minY = Math.min(loc1.getBlockY(), loc2.getBlockY());

        double xVal = 0;
        double zVal = 0;
        double yVal = 0;
        World w = loc1.getWorld();

        String mobnames = "";

        for (Entry<EntityType, Integer> entry : mobs.entrySet()) {
            for (int ctr = 0; ctr < entry.getValue(); ctr++) {
                do {
                    xVal = Math.random() * (maxX - minX) + minX;
                    zVal = Math.random() * (maxZ - minZ) + minZ;
                    yVal = minY;

                    while (w.getBlockAt((int) xVal, (int) yVal, (int) zVal).getType() != Material.AIR && yVal < maxY) {
                        yVal += 1;
                    }
                } while (yVal == maxY);

                Entity e = w.spawnEntity(new Location(w, xVal, yVal, zVal), entry.getKey());
                if (e instanceof Skeleton) {
                    Skeleton skel = (Skeleton) e;
                    skel.getEquipment().setItemInHand(new ItemStack(Material.BOW));
                }
            }
            if (entry.getValue() > 1) {
                mobnames += entry.getValue() + " " + entry.getKey().name() + "S, ";
            } else {
                mobnames += entry.getValue() + " " + entry.getKey().name() + ", ";
            }
        }

        if (mobnames.length() > 2) {
            mobnames = mobnames.substring(0, mobnames.length() - 2);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld().equals(loc1.getWorld())) {

                Location ploc = p.getLocation();

                if (ZachBoraPlugin.isInside(ploc, loc1, loc2)) {
                    p.sendMessage("WAVE " + waveid + " ! " + mobnames);
                }
            }
        }
    }

}
