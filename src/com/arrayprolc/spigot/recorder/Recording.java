package com.arrayprolc.spigot.recorder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Recording implements Listener {

    private ArrayList<String> locs = new ArrayList<String>();

    public static final HashMap<String, Recording> current = new HashMap<String, Recording>();

    public Player player;

    private BukkitTask runnable;

    public String fileName;

    @SuppressWarnings("deprecation")
    public void startRecording() {
        Bukkit.getServer().getPluginManager().registerEvents(this, RecordingMain.getInstance());
        runnable = Bukkit.getScheduler().runTaskTimer(RecordingMain.getInstance(), new BukkitRunnable() {
            public void run() {
                if (!player.isOnline()) {
                    destroy();
                    this.cancel();
                    return;
                }
                locs.add(new BoxedLocation(player.getLocation()).toString());
            }
        }, 0, 1);
        System.out.println("[Rec] Started recording " + fileName + ".dem.");
    }

    public void destroy() {
        player = null;
    }

    public void stopRecording() {
        HandlerList.unregisterAll(this);
        if (runnable != null) {
            runnable.cancel();
        }
        System.out.println("[Rec] Done recording " + fileName + ".dem, saving.");
    }

    public void saveDemo() throws FileNotFoundException {
        new File("./demos/").mkdirs();
        PrintWriter out = new PrintWriter("./demos/" + fileName + ".dem");
        for (String s : locs) {
            out.println(s);
        }
        out.close();
        System.out.println("[Rec] Done writing.");
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        if (e.isCancelled())
            return;
        if (e.getPlayer().getName().equals(player.getName())) {
            locs.add("$c:" + e.getMessage().replace(":", "[colon]"));
        }
    }

}
