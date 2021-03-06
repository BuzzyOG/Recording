package com.arrayprolc.spigot.recorder.recording;

import java.io.FileNotFoundException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.arrayprolc.spigot.recorder.RecordingPlugin;

public class Recording extends Thread implements Listener {
	private Player recorder;
	private boolean end = false;
	private final long sleeptime;
	private final int fps;
	private final String saveFile;
	private final Interpreter interpreter;

	public Recording(Player player, int fps, String saveFile) {
		this.fps = fps;
		interpreter = new Interpreter(this);
		sleeptime = 1000 / fps;
		recorder = player;
		this.saveFile = saveFile;
	}

	public Interpreter getInterpreter() {
		return interpreter;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		Bukkit.getServer().getPluginManager().registerEvents(this, RecordingPlugin.getInstance());
		while (true) {
			if (end || interpreter == null) {
				stop();
				return;
			}
			new BukkitRunnable() {
				@Override
				public void run() {
					if (!recorder.isOnline()) {
						recorder = null;
						end = true;
						try {
							saveDemo();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						return;
					}
					if (recorder == null) {
						System.out.println("[Debug] Recorder is null! :(");
					}
					if (interpreter == null) {
						System.out.println("[Debug] Interpreter is null :(");
					}
					interpreter.interpretLocation(recorder.getLocation());
				}
			}.runTask(RecordingPlugin.getInstance());
			try {
				sleep(sleeptime);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void stopRecording() {
		HandlerList.unregisterAll(this);
		try {
			saveDemo();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if (interpreter != null) {
			interpreter.clearMapEdits();
		}
		end = true;
	}

	public int getFps() {
		return fps;
	}

	private void saveDemo() throws FileNotFoundException {
		interpreter.save(saveFile);
	}

	@EventHandler
	public void chat(AsyncPlayerChatEvent e) {
		if (!e.getPlayer().getName().equals(recorder.getName()))
			return;
		interpreter.interpretChatEvent(e);
	}

	@EventHandler
	public void breake(BlockBreakEvent e) {
		if (!e.getPlayer().getName().equals(recorder.getName()))
			return;
		interpreter.interpretBlockBreakEvent(e);
	}

	@EventHandler
	public void place(BlockPlaceEvent e) {
		if (!e.getPlayer().getName().equals(recorder.getName()))
			return;
		interpreter.interpretBlockPlaceEvent(e);
	}
}