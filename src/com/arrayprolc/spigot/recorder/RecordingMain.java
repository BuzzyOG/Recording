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

	public void onDisable() {
		for (FPSBasedRecording rec : Recording.current.values()) {
			rec.stopRecording();
			rec = null;
		}
		Recording.current.clear();
	}

	@SuppressWarnings({ "deprecation" })
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("record")) {
			if (!(sender instanceof Player)) {
				return true;
			}
			Player p = (Player) sender;
			if (Recording.current.containsKey(p.getUniqueId().toString())) {
				sender.sendMessage("You are already FPSBasedRecording!");
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage("Usage: record filename");
				return true;
			}
			String fileName = args[0].replace("..", "").replace("/", "").replace("\\", "").replace(".", "");
            /*
             * FPSBasedRecording rec = new FPSBasedRecording(); rec.player = p;
             * rec.fileName = fileName;
             * 
             * rec.startFPSBasedRecording();
             */
			FPSBasedRecording rec = new FPSBasedRecording(p, 1000, fileName);
			rec.start();
			Recording.current.put(p.getUniqueId().toString(), rec);
			System.out.println("Started FPSBasedRecording!");
			return true;
		}

		if (label.equalsIgnoreCase("stoprec")) {
			if (!(sender instanceof Player)) {
				return true;
			}
			final Player p = (Player) sender;
			if (!Recording.current.containsKey(p.getUniqueId().toString())) {
				sender.sendMessage("You are not FPSBasedRecording!");
				return true;
			}
			FPSBasedRecording r = Recording.current.get(p.getUniqueId().toString());
			r.stopRecording();
			System.out.println("Stopped FPSBasedRecording.");
			Recording.current.remove(p.getUniqueId().toString());
			return true;
		}

		if (label.equalsIgnoreCase("playdemo")) {
			if (!(sender instanceof Player)) {
				return true;
			}
			Player p = (Player) sender;
			if (Recording.current.containsKey(p.getUniqueId().toString())) {
				sender.sendMessage("You are FPSBasedRecording!");
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage("Usage: playdemo filename [scale]");
				return true;
			}
			double timescale = 1;
			if (args.length > 1) {
				try {
					timescale = Double.parseDouble(args[1]);
				} catch (NumberFormatException ex) {
					timescale = 1;
				}
			}
			if (timescale <= 0) {
				timescale = 0.1;
			}
			timescale = Math.min(50, timescale);
			String file = args[0].replace("..", "");
			File f = new File("./demos/" + file + ".dem");
			if (!f.exists()) {
				sender.sendMessage("That doesn't exit! :(");
				return true;
			}

			try {
				TimeScalePlayback b = new TimeScalePlayback(p, f, timescale);
				b.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
            for (EntityType t : EntityType.values()) {
                if (t.toString().replace("_", "").equalsIgnoreCase(args[1].replace("_", ""))) {
                    type = t;
                }
            }
            final Entity e = p.getLocation().getWorld().spawnEntity(p.getLocation(), type);
            if (Recording.current.containsKey(p.getUniqueId().toString())) {
                sender.sendMessage("You are FPSBasedRecording!");
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
							Playback.interpret(i.nextLine(), e);
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
