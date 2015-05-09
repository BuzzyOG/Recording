package com.arrayprolc.spigot.recorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Recording {

    private ArrayList<Location> locs = new ArrayList<Location>();

    public static final HashMap<String, Recording> current = new HashMap<String, Recording>();

    public Player player;

    private BukkitTask runnable;

    public String fileName;

    @SuppressWarnings("deprecation")
    public void startRecording() {
        runnable = Bukkit.getScheduler().runTaskTimer(RecordingMain.getInstance(), new BukkitRunnable() {
            public void run() {
                if (!player.isOnline()) {
                    destroy();
                    this.cancel();
                    return;
                }
                locs.add(player.getLocation());
            }
        }, 0, 1);
        System.out.println("[Rec] Started recording" + fileName + ".dem.");
    }

    public void destroy() {
        player = null;
    }

    public void stopRecording() {
        if (runnable != null) {
            runnable.cancel();
        }
        System.out.println("[Rec] Done recording " + fileName + ".dem, saving.");
    }

    public void saveDemo() throws FileNotFoundException {
        new File("./demos/").mkdirs();
        PrintWriter out = new PrintWriter("./demos/" + fileName + ".dem");
        for (Location l : locs) {
            out.println(new BoxedLocation(l).toString());
        }
        out.close();
        System.out.println("[Rec] Done writing.");
    }

    public void play() {

    }

}
