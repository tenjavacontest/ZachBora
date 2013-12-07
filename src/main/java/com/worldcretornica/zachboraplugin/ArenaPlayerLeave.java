package com.worldcretornica.zachboraplugin;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ArenaPlayerLeave extends ArenaPlayerMove {

    public ArenaPlayerLeave(World world, Location location1, Location location2, Player player) {
        super(world, location1, location2, player);
    }
}
