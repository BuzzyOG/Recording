package com.arrayprolc.spigot.recorder;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class RecordingInterpreter {

    public static final void interpret(String line, Entity e) {
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
