package com.arrayprolc.spigot.recorder;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class Playback {

    public static final void interpret(String line, Entity e) {
        if (line.startsWith("$b")) {
            String typeRaw = line.split(":")[1].split(";")[0];
            String locRaw = line.split(":")[1].split(";")[1].replace("[colon]", ":");
            BoxedLocation loc = new BoxedLocation(locRaw);
            Material type = Material.valueOf(typeRaw);
            loc.unbox().getBlock().setType(type);
            return;
        }
        if (line.startsWith("$c")) {
            if (e instanceof Player) {
                ((Player) e).chat(line.split(":")[1]);
            } else {
                Bukkit.broadcastMessage("<" + WordUtils.capitalize(e.getType().toString()) + "> " + line.split(":")[1]);
            }
            return;
        }
        e.teleport(new BoxedLocation(line).unbox());
        e.setFallDistance(0);
    }

}
