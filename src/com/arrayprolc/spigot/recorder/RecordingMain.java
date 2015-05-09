package com.arrayprolc.spigot.recorder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class RecordingMain extends JavaPlugin {

    private static RecordingMain instance;

    public static RecordingMain getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
    }

    @SuppressWarnings({ "resource", "deprecation" })
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("record")) {
            if (!(sender instanceof Player)) {
                return true;
            }
            Player p = (Player) sender;
            if (Recording.current.containsKey(p.getUniqueId().toString())) {
                sender.sendMessage("You are already recording!");
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage("Usage: record filename");
                return true;
            }
            String fileName = args[0].replace("..", "").replace("/", "").replace("\\", "").replace(".", "");
            Recording rec = new Recording();
            rec.player = p;
            rec.fileName = fileName;
            Recording.current.put(p.getUniqueId().toString(), rec);
            rec.startRecording();
            System.out.println("Started recording!");
            return true;
        }

        if (label.equalsIgnoreCase("stoprec")) {
            if (!(sender instanceof Player)) {
                return true;
            }
            final Player p = (Player) sender;
            if (!Recording.current.containsKey(p.getUniqueId().toString())) {
                sender.sendMessage("You are not recording!");
                return true;
            }
            Recording r = Recording.current.get(p.getUniqueId().toString());
            r.stopRecording();
            System.out.println("Stopped recording.");
            try {
                r.saveDemo();
                System.out.println("Saved demo!");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Recording.current.remove(p.getUniqueId().toString());
            return true;
        }

        if (label.equalsIgnoreCase("playdemo")) {
            if (!(sender instanceof Player)) {
                return true;
            }
            Player p = (Player) sender;
            if (Recording.current.containsKey(p.getUniqueId().toString())) {
                sender.sendMessage("You are recording!");
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage("Usage: playdemo filename");
                return true;
            }
            String file = args[0].replace("..", "");
            File f = new File("./demos/" + file + ".dem");
            if (!f.exists()) {
                sender.sendMessage("That doesn't exit! :(");
                return true;
            }

            try {
                final Scanner i = new Scanner(new FileInputStream(f));

                Bukkit.getScheduler().runTaskTimer(RecordingMain.getInstance(), new BukkitRunnable() {
                    public void run() {
                        if (i.hasNextLine()) {
                            p.teleport(new BoxedLocation(i.nextLine()).unbox());
                        } else {
                            cancel();
                            i.close();
                        }
                    }
                }, 0, 1);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return true;
        }

        if (label.equalsIgnoreCase("remoteplay")) {
            if (!(sender instanceof Player)) {
                return true;
            }
            Player p = (Player) sender;
            if (args.length != 2) {
                sender.sendMessage("Usage: remoteplay filename type");
                return true;
            }
            EntityType type = EntityType.VILLAGER;
            for(EntityType t : EntityType.values()){
                if(t.toString().replace("_", "").equalsIgnoreCase(args[1].replace("_", ""))){
                    type = t;
                }
            }
            Entity e = p.getLocation().getWorld().spawnEntity(p.getLocation(), type);
            if (Recording.current.containsKey(p.getUniqueId().toString())) {
                sender.sendMessage("You are recording!");
                return true;
            }
            String file = args[0].replace("..", "");
            File f = new File("./demos/" + file + ".dem");
            if (!f.exists()) {
                sender.sendMessage("That doesn't exit! :(");
                return true;
            }

            try {
                final Scanner i = new Scanner(new FileInputStream(f));

                Bukkit.getScheduler().runTaskTimer(RecordingMain.getInstance(), new BukkitRunnable() {
                    public void run() {
                        if (i.hasNextLine()) {
                            e.teleport(new BoxedLocation(i.nextLine()).unbox());
                            e.setFallDistance(0);
                        } else {
                            e.remove();
                            i.close();
                            cancel();
                        }
                    }
                }, 0, 1);

            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
