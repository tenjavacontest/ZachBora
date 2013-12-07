package com.worldcretornica.zachboraplugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ZachBoraPlugin extends JavaPlugin {

    public Location loc1 = null;
    public Location loc2 = null;
    public String worldname = "";
    public int nbpeople = 0;
    public boolean gamestarted;
    public boolean countdownstarted;
    
    public List<Integer> gamestarttaskid;

    @Override
    public void onEnable() {
        
        Bukkit.getPluginManager().registerEvents(new ZachBoraListener(this), this);
        
        worldname = "TenJava";
        loc1 = new Location(Bukkit.getWorld(worldname), -10, 60, -10);
        loc2 = new Location(Bukkit.getWorld(worldname), 10, 74, 10);
        gamestarted = false;
        countdownstarted = false;
        gamestarttaskid = new ArrayList<>();
    }
    
    public static boolean isInside(Location entityLoc, Location loc1, Location loc2) {
        double x0 = entityLoc.getBlockX();
        int x1 = loc1.getBlockX();
        int x2 = loc2.getBlockX();

        double y0 = entityLoc.getBlockY();
        int y1 = loc1.getBlockY();
        int y2 = loc2.getBlockY();

        double z0 = entityLoc.getBlockZ();
        int z1 = loc1.getBlockZ();
        int z2 = loc2.getBlockZ();

        return x0 >= Math.min(x1, x2) && y0 >= Math.min(y1, y2) && z0 >= Math.min(z1, z2) && x0 <= Math.max(x1, x2) && y0 <= Math.max(y1, y2) && z0 <= Math.max(z1, z2);
    }
    
    public void cancelArena() {
        World w = Bukkit.getWorld(worldname);
        
        for(int taskid : gamestarttaskid) {
            Bukkit.getScheduler().cancelTask(taskid);
        }
        
        for(Entity e : w.getEntities()) {
            if(!(e instanceof Player) && isInside(e.getLocation(), loc1, loc2)) {
                e.remove();
            }
        }
    }
    
    public void startGame() {
        gamestarted = true;
        
        HashMap<EntityType, Integer> mobs = new HashMap<>();
        
        //Wave 1
        mobs.put(EntityType.ZOMBIE, getNbPlayers() * 2);
        gamestarttaskid.add(Bukkit.getScheduler().scheduleSyncDelayedTask(this, new ArenaWave(mobs, loc1, loc2), 0 * 20));
        //Wave 2
        mobs = new HashMap<>();
        mobs.put(EntityType.ZOMBIE, getNbPlayers() * 4);
        gamestarttaskid.add(Bukkit.getScheduler().scheduleSyncDelayedTask(this, new ArenaWave(mobs, loc1, loc2), 10 * 20));
        //Wave 3
        mobs = new HashMap<>();
        mobs.put(EntityType.ZOMBIE, getNbPlayers() * 4);
        mobs.put(EntityType.SPIDER, getNbPlayers() * 2);
        gamestarttaskid.add(Bukkit.getScheduler().scheduleSyncDelayedTask(this, new ArenaWave(mobs, loc1, loc2), 20 * 20));
        //Wave 4
        mobs = new HashMap<>();
        mobs.put(EntityType.SKELETON, getNbPlayers() * 4);
        gamestarttaskid.add(Bukkit.getScheduler().scheduleSyncDelayedTask(this, new ArenaWave(mobs, loc1, loc2), 30 * 20));
        //Wave 5
        mobs = new HashMap<>();
        mobs.put(EntityType.PIG, getNbPlayers() * 3);
        gamestarttaskid.add(Bukkit.getScheduler().scheduleSyncDelayedTask(this, new ArenaWave(mobs, loc1, loc2), 40 * 20));
        //Wave 6
        mobs = new HashMap<>();
        mobs.put(EntityType.SILVERFISH, getNbPlayers() * 5);
        mobs.put(EntityType.ZOMBIE, getNbPlayers() * 2);
        gamestarttaskid.add(Bukkit.getScheduler().scheduleSyncDelayedTask(this, new ArenaWave(mobs, loc1, loc2), 60 * 20));
        //Wave 7
        mobs = new HashMap<>();
        mobs.put(EntityType.SKELETON, getNbPlayers() * 5);
        mobs.put(EntityType.ZOMBIE, getNbPlayers() * 5);
        gamestarttaskid.add(Bukkit.getScheduler().scheduleSyncDelayedTask(this, new ArenaWave(mobs, loc1, loc2), 90 * 20));
    }
    
    public int getNbPlayers() {
        int nb = 0;
        
        for(Player p : Bukkit.getOnlinePlayers()) {
                        
            if(p.getWorld().equals(loc1.getWorld())) {
                
                Location ploc = p.getLocation();
                
                getLogger().info("player " + p.getName());
                
                if (ZachBoraPlugin.isInside(ploc, loc1, loc2)) {
                    nb++;
                }
            }
        }
        
        return nb;
    }
}
