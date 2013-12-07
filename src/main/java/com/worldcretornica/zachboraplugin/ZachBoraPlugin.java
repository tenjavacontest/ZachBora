package com.worldcretornica.zachboraplugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
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
    public int currentWave = 0;

    public List<Integer> gamestarttaskid;

    @Override
    public void onEnable() {

        Bukkit.getPluginManager().registerEvents(new ZachBoraListener(this), this);

        worldname = "TenJava";
        loc1 = new Location(Bukkit.getWorld(worldname), -10, 63, -10);
        loc2 = new Location(Bukkit.getWorld(worldname), 10, 74, 10);
        gamestarted = false;
        countdownstarted = false;
        gamestarttaskid = new ArrayList<>();

        this.getConfig().options().copyDefaults();
        try {
            this.getConfig().save("config.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        for (int taskid : gamestarttaskid) {
            Bukkit.getScheduler().cancelTask(taskid);
        }
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
        gamestarted = false;
        countdownstarted = false;

        World w = Bukkit.getWorld(worldname);

        for (int taskid : gamestarttaskid) {
            Bukkit.getScheduler().cancelTask(taskid);
        }

        for (Entity e : w.getEntities()) {
            if (!(e instanceof Player) && isInside(e.getLocation(), loc1, loc2)) {
                e.remove();
            }
        }
    }

    public void startGame() {
        gamestarted = true;
        currentWave = 0;
        startWave();
    }

    public void startWave() {
        HashMap<EntityType, Integer> mobs = new HashMap<>();

        currentWave++;

        if (getConfig().contains("Waves")) {
            ConfigurationSection waves = getConfig().getConfigurationSection("Waves");

            if (waves.contains("" + currentWave)) {
                ConfigurationSection wave = waves.getConfigurationSection("" + currentWave);

                int secondsBeforeStart = wave.getInt("SecondsBeforeStart");

                int ctr = 1;

                while (wave.contains("Mob" + ctr)) {
                    ConfigurationSection mobsection = wave.getConfigurationSection("Mob" + ctr);

                    EntityType et = null;

                    if (mobsection.contains("Name")) {
                        et = EntityType.valueOf(mobsection.getString("Name"));
                    }

                    if (et == null) {
                        getLogger().warning("EntityType " + mobsection.getString("Name") + " is not valid");
                    } else {

                        int count = 0;

                        if (mobsection.contains("PerPlayer")) {
                            count += mobsection.getInt("PerPlayer") * getNbPlayers();
                        }

                        if (mobsection.contains("Additional")) {
                            count += mobsection.getInt("Additional");
                        }

                        mobs.put(et, count);
                    }
                    ctr++;
                }

                gamestarttaskid.add(Bukkit.getScheduler().scheduleSyncDelayedTask(this, new ArenaWave(mobs, loc1, loc2, currentWave), secondsBeforeStart * 20));
            } else {
                // game is finished
            }
        } else {
            getLogger().warning("Waves Configuration missing, cannot continue");
        }
    }

    public int getNbPlayers() {
        int nb = 0;

        for (Player p : Bukkit.getOnlinePlayers()) {

            if (p.getWorld().equals(loc1.getWorld())) {

                Location ploc = p.getLocation();

                if (ZachBoraPlugin.isInside(ploc, loc1, loc2)) {
                    nb++;
                }
            }
        }

        return nb;
    }
}
