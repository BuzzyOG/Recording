package com.arrayprolc.spigot.recorder;

import java.util.HashMap;

import org.bukkit.event.Listener;

public class Recording implements Listener {

//    private ArrayList<String> locs = new ArrayList<String>();

    public static final HashMap<String, FPSBasedRecording> current = new HashMap<String, FPSBasedRecording>();

  /*  public Player player;

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
    
    @EventHandler
    public void breake(BlockBreakEvent e) {
        if (e.isCancelled())
            return;
        if (e.getPlayer().getName().equals(player.getName())) {
            locs.add("$b:" + "AIR" + ";" + new BoxedLocation(e.getBlock().getLocation()).toString().replace(":", "[colon]"));
        }
    }
    
    @EventHandler
    public void place(BlockBreakEvent e) {
        if (e.isCancelled())
            return;
        if (e.getPlayer().getName().equals(player.getName())) {
            locs.add("$b:" + e.getBlock().getType() + ";" + new BoxedLocation(e.getBlock().getLocation()).toString().replace(":", "[colon]"));
        }
    }*/

}
