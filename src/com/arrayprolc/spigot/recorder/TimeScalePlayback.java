package com.arrayprolc.spigot.recorder;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class TimeScalePlayback extends Thread {
    private final Scanner sc;
    private final long sleeptime;
    private final Entity entity;

    public TimeScalePlayback(Entity entity, File demo, double scale) throws Exception {
        sc = new Scanner(new FileInputStream(demo));
        sleeptime = (long) (50 / scale);
        this.entity = entity;
    }

    @SuppressWarnings("deprecation")
    @Override
    public synchronized void run() {
        while (sc.hasNextLine()) {
            if (!entity.isValid()) {
                stop();
                return;
            }
            String line = sc.nextLine();

            new BukkitRunnable() {
                public void run() {
                    Playback.interpret(line, entity);
                }
            }.runTask(RecordingMain.getInstance());
            try {
                sleep(sleeptime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        sc.close();
        this.stop();
        super.start();
    }
}