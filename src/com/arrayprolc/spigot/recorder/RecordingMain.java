package com.arrayprolc.spigot.recorder;

import java.io.File;
import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RecordingMain extends JavaPlugin {

	public static final HashMap<String, FPSBasedRecording> recordings = new HashMap<String, FPSBasedRecording>();
	public static final HashMap<String, TimeScalePlayback> playbacks = new HashMap<String, TimeScalePlayback>();
	private static final int DEFAULT_RECORD_FPS = 120;

	private static RecordingMain instance;

	public static RecordingMain getInstance() {
		return instance;
	}

	public void onEnable() {
		instance = this;
	}

	public void onDisable() {
		for (FPSBasedRecording rec : recordings.values()) {
			rec.stopRecording();
			rec = null;
		}
		recordings.clear();

		for (TimeScalePlayback playback : playbacks.values()) {
			playback.stopPlayback();
			playback = null;
		}
		playbacks.clear();

	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("record")) {
			if (!(sender instanceof Player)) {
				return true;
			}
			Player p = (Player) sender;
			if (recordings.containsKey(p.getUniqueId().toString())) {
				sender.sendMessage("You are already FPSBasedRecording!");
				return true;
			}
			if (args.length == 0) {
				sender.sendMessage("Usage: Record <File name> (FPS)");
				return true;
			}
			String fileName = args[0].replace("..", "").replace("/", "").replace("\\", "").replace(".", "");
			int fps = RecordingMain.DEFAULT_RECORD_FPS;
			if (args.length >= 2)
				try {
					fps = Integer.parseInt(args[1]);
				} catch (Exception exc) {
				}
			fps = Math.min(fps, 1000);
			FPSBasedRecording rec = new FPSBasedRecording(p, fps, fileName);
			rec.start();
			recordings.put(p.getUniqueId().toString(), rec);
			p.sendMessage("You are now recording \"" + fileName + "\" at " + fps + " FPS!");
			return true;
		}

		if (label.equalsIgnoreCase("stoprec")) {
			if (!(sender instanceof Player)) {
				return true;
			}
			final Player p = (Player) sender;
			if (!recordings.containsKey(p.getUniqueId().toString())) {
				sender.sendMessage("You are not FPSBasedRecording!");
				return true;
			}
			FPSBasedRecording r = recordings.get(p.getUniqueId().toString());
			r.stopRecording();
			System.out.println("Stopped FPSBasedRecording.");
			recordings.remove(p.getUniqueId().toString());
			return true;
		}

		if (label.equalsIgnoreCase("playdemo")) {
			if (!(sender instanceof Player)) {
				return true;
			}
			Player p = (Player) sender;
			if (recordings.containsKey(p.getUniqueId().toString())) {
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
			if (recordings.containsKey(p.getUniqueId().toString())) {
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
				TimeScalePlayback playback = new TimeScalePlayback(e, f, 1);
				playback.start();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return true;
		}
		return false;
	}
}
