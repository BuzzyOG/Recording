package com.arrayprolc.spigot.recorder.recording;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;
import java.util.Stack;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.arrayprolc.spigot.recorder.RecordingPlugin;

public class Playback extends Thread {
    private final Scanner sc;
    private long sleeptime;
    private Entity entity;
    private World world;
    private Location location;
    private boolean running = false;
    private Stack<String> queue;

    public Playback(Entity entity, File demo, double scale) throws Exception {
        sc = new Scanner(new FileInputStream(demo));
        sleeptime = (long) (1000 / scale);
        this.entity = entity;
        this.world = Bukkit.getWorlds().get(0);
        location = new Location(world, 0, 0, 0);
        queue = new Stack<String>();
    }

    @SuppressWarnings("deprecation")
    @Override
    public synchronized void run() {
        running = true;
        while (running) {
            if (!entity.isValid()) {
                stop();
                return;
            }
            if (!queue.isEmpty()) {
                execRaw(queue.pop());
            } else if (sc.hasNextLine()) {
                final String line = sc.nextLine();
                new BukkitRunnable() {
                    public void run() {
                        execRaw(line);
                    }
                }.runTask(RecordingPlugin.getInstance());
                try {
                    sleep(sleeptime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Scanner is done. Demo terminating.");
                running = false;
                sc.close();
            }
        }
        this.stop();
        super.start();
    }

    private void execRaw(String line) {
        if (!line.contains(Interpreter.iterations + "")) {
            executeLine(line);
        } else {
            int top;
            try {
                top = Integer.parseInt(line.substring(line.indexOf(Interpreter.iterations) + 1));
            } catch (Exception exc) {
                top = 1;
            }
            String nl = line.substring(0, line.indexOf(Interpreter.iterations));
            for (int i = 1; i < top; i++) {
                queue.push(nl);
            }
            execRaw(nl);
        }
    }

    @SuppressWarnings("deprecation")
    private void executeLine(String line) {
        try {
            String[] data = line.split(Interpreter.separator + "");
            switch (data[0]) {
            case Interpreter.WORLD:
                this.world = Bukkit.getWorld(data[1]);
                location.setWorld(world);
                break;
            case Interpreter.NEW_LOCATION:
                location.setX(Double.parseDouble(data[1]));
                location.setY(Double.parseDouble(data[2]));
                location.setZ(Double.parseDouble(data[3]));
                entity.teleport(location);
                break;
            case Interpreter.CHANGE_VIEW:
                location.setYaw(Float.parseFloat(data[1]));
                location.setPitch(Float.parseFloat(data[2]));
                entity.teleport(location);
                break;
            case Interpreter.BLOCK_PLACE:
                Location place = location.clone();
                place.setX(Double.parseDouble(data[1]));
                place.setY(Double.parseDouble(data[2]));
                place.setZ(Double.parseDouble(data[3]));
                Material material = Material.valueOf(data[4]);
                byte raw = (byte) Integer.parseInt(data[5]);
                place.getBlock().setTypeIdAndData(material.getId(), raw, true);
                break;
            case Interpreter.BLOCK_BREAK:
                Location brk = location.clone();
                brk.setX(Double.parseDouble(data[1]));
                brk.setY(Double.parseDouble(data[2]));
                brk.setZ(Double.parseDouble(data[3]));
                brk.getBlock().setType(Material.AIR);
                for (Player p : Bukkit.getOnlinePlayers())
                    if (p.getWorld().getName().equals(brk.getWorld().getName())) {
                        p.playEffect(brk, Effect.STEP_SOUND, brk.getBlock().getType().getId());
                    }
                break;
            case Interpreter.PLAYER_CHAT:
                if (!(entity instanceof Player))
                    return;
                String message = new String(Base64.decodeBase64(data[1]), "UTF-8");
                ((Player) entity).chat(message);
                break;
            case Interpreter.INFO:
                parseInfo(data[1]);
                break;
            case Interpreter.HOTBAR_POSTITION:
                if (!(entity instanceof Player))
                    return;
                ((Player) entity).getInventory().setHeldItemSlot(Integer.parseInt(data[1]));
                break;
            default:
                break;
            }
        } catch (Exception exc) {
            System.out.println("Error parsing line: " + line);
        }
    }

    private void parseInfo(String data) {
        if (data.startsWith("fps")) {
            String amount = data.substring(data.indexOf('=') + 1, data.length());
            this.sleeptime /= (Integer.parseInt(amount) / 20);
        } else {

        }
    }

    public void stopPlayback() {
        queue.clear();
        int skipped = 0;
        while (sc.hasNext()) {
            sc.nextLine();
            skipped++;
        }
        System.out.println("Playback stopped. Skipping " + skipped + " unparsed lines.");
        sc.close();
        entity = null;
        world = null;
        location = null;
        running = false;
    }
}