package com.worldcretornica.zachboraplugin;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaWave extends BukkitRunnable {

    private HashMap<EntityType, Integer> mobs;
    private Location loc1;
    private Location loc2;
    
    public ArenaWave(HashMap<EntityType, Integer> mobs, Location loc1, Location loc2) {
        this.mobs = mobs;
        this.loc1 = loc1;
        this.loc2 = loc2;
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
        
        for(Entry<EntityType, Integer> entry : mobs.entrySet()) {
            for(int ctr = 0; ctr < entry.getValue(); ctr++) {
                xVal = Math.random() * (maxX - minX) + minX;
                zVal = Math.random() * (maxZ - minZ) + minZ;
                yVal = Math.random() * (maxY - minY) + minY;
                
                w.spawnEntity(new Location(w, xVal, yVal, zVal), entry.getKey());
            }
            if (entry.getValue() > 1) {
                mobnames += entry.getValue() + " " + entry.getKey().name() + "s, ";
            } else {
                mobnames += entry.getValue() + " " + entry.getKey().name() + ", ";
            }
        }
        
        if(mobnames.length() > 2) {
            mobnames = mobnames.substring(0, mobnames.length() - 2);
        }
        
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.getWorld().equals(loc1.getWorld())) {
                
                Location ploc = p.getLocation();
                
                if (ZachBoraPlugin.isInside(ploc, loc1, loc2)) {
                    p.sendMessage("WAVE STARTED! " + mobnames);
                }
            }
        }
    }

}
