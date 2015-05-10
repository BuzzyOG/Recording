package com.arrayprolc.spigot.recorder.recording;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Stack;

import org.apache.commons.codec.binary.Base64;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.util.Vector;

public class InputInterpreter {
	private ArrayList<String> data;
	private Stack<BlockState> stateChanges;

	public static final char separator = ':';
	public static final char iterations = ';';
	public static final DecimalFormat FORMAT = new DecimalFormat("#.###");

	private String lastWorld = "";
	private double x0 = 0;
	private double y0 = 0;
	private double z0 = 0;

	private String active = "";
	private int activeTally = 0;

	// Variable tags
	public static final String WORLD = "@w"; // Keeps track of the world and allows us to only
	// need to write out the world if it changes
	public static final String NEW_LOCATION = "@l"; // If the player moves instead of the
	// camera
	public static final String CHANGE_VIEW = "@v"; // Just for changing the camera angle (eg.
	// player looking around but not walking
	public static final String PLAYER_CHAT = "@c"; // Used to identify chat messages
	public static final String BLOCK_PLACE = "@p"; // Used to identify when the player places
	// a block
	public static final String BLOCK_BREAK = "@b"; // Used to identify when the player removes
	// a block
	public static final String HOTBAR_POSTITION = "@h"; // Used to identify if the player
	// scrolls to a new hotbar item
	public static final String INFO = "@i"; // Used to indentify information, such as FPS

	// Actual Class
	public InputInterpreter(Recording recording) {
		this.data = new ArrayList<String>();
		this.lastWorld = null;
		stateChanges = new Stack<BlockState>();
		out(INFO + separator + "fps=" + recording.getFps());
	}

	// Event Interpretation

	public void interpret(Object data) {
		try {
			if (data instanceof Location)
				interpretLocation((Location) data);
			else if (data instanceof AsyncPlayerChatEvent)
				interpretChatEvent((AsyncPlayerChatEvent) data);
			else if (data instanceof BlockBreakEvent)
				interpretBlockBreakEvent((BlockBreakEvent) data);
			else if (data instanceof BlockPlaceEvent)
				interpretBlockPlaceEvent((BlockPlaceEvent) data);
			else
				System.out.println("Failed to interpret " + data);
		} catch (Exception ex) {
			System.out.println("Failed to interpret " + data);
			// ex.printStackTrace();
		}
	}

	public void interpretLocation(Location location) {
		if (location == null) {
			System.out.println("Location is null! :(");
			return;
		}
		String world = location.getWorld().getName();
		if (!world.equals(lastWorld)) {
			out(WORLD + separator + world);
			this.lastWorld = world;
		}
		double x = Double.parseDouble(FORMAT.format(location.getX()));
		double y = Double.parseDouble(FORMAT.format(location.getY()));
		double z = Double.parseDouble(FORMAT.format(location.getZ()));
		float a = Float.parseFloat(FORMAT.format(location.getYaw()));
		float p = Float.parseFloat(FORMAT.format(location.getPitch()));
		// Check to see if the player is only looking around
		if ((x - x0 != 0) || (y - y0 != 0) || (z - z0 != 0)) {
			out(NEW_LOCATION + separator + vectorToString(x, y, z) + separator + a + separator + p);
			x0 = x;
			y0 = y;
			z0 = z;
		} else {
			out(CHANGE_VIEW + separator + FORMAT.format(a) + separator + FORMAT.format(p));
		}
	}

	public void interpretChatEvent(AsyncPlayerChatEvent event) {
		out(PLAYER_CHAT + separator + Base64.encodeBase64String(event.getMessage().getBytes()), "UTF-8");
	}

	@SuppressWarnings("deprecation")
	public void interpretBlockPlaceEvent(BlockPlaceEvent event) {
		stateChanges.push(event.getBlockReplacedState());
		Block block = event.getBlockPlaced();
		int x = block.getLocation().getBlockX();
		int y = block.getLocation().getBlockY();
		int z = block.getLocation().getBlockZ();
		out(BLOCK_PLACE + separator + vectorToString(x, y, z) + separator + block.getType().toString() + separator + block.getData());
	}

	public void interpretBlockBreakEvent(BlockBreakEvent event) {
		Block block = event.getBlock();
		int x = block.getLocation().getBlockX();
		int y = block.getLocation().getBlockY();
		int z = block.getLocation().getBlockZ();
		out(BLOCK_BREAK + separator + vectorToString(x, y, z));
		stateChanges.push(block.getState());
	}

	// End of events

	public String vectorToString(Vector vector) {
		String x = FORMAT.format(vector.getX());
		String y = FORMAT.format(vector.getY());
		String z = FORMAT.format(vector.getZ());
		return x + separator + y + separator + z;
	}

	private String vectorToString(double x0, double y0, double z0) {
		String x = FORMAT.format(x0);
		String y = FORMAT.format(y0);
		String z = FORMAT.format(z0);
		return x + separator + y + separator + z;
	}

	private void out(String... output) {
		if (output.length < 0)
			return;
		for (String s : output) {
			if (s.equals(active)) {
				activeTally++;
				continue;
			} else {
				data.add(active + (activeTally >= 2 ? iterations + activeTally : ""));
				active = s;
				activeTally = 0;
			}
		}
	}

	public void save(String saveFile) throws FileNotFoundException {
		new File("./demos/").mkdirs();
		PrintWriter out = new PrintWriter("./demos/" + saveFile + ".dem");
		out("");
		for (String s : data) {
			out.println(s);
		}
		out.close();
	}

	@SuppressWarnings("deprecation")
	public void clearMapEdits() {
		ArrayList<Chunk> forcedLoads = new ArrayList<Chunk>();
		while (!stateChanges.isEmpty()) {
			BlockState state = stateChanges.pop();
			Chunk c = state.getLocation().getChunk();
			if (!c.isLoaded()) {
				c.load();
				state.getLocation().getBlock().setTypeIdAndData(state.getTypeId(), state.getRawData(), true);
				c.unload();
			} else
				state.getLocation().getBlock().setTypeIdAndData(state.getTypeId(), state.getRawData(), true);
		}
		if (!forcedLoads.isEmpty())
			for (Chunk c : forcedLoads)
				c.unload(true, true);
	}
}