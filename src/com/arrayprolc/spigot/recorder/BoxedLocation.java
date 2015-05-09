package com.arrayprolc.spigot.recorder;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class BoxedLocation {

    private World world;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;

    private static final DecimalFormat FORMAT = new DecimalFormat("#.###");

    public BoxedLocation(World world, double x, double y, double z, float pitch, float yaw) {
        super();
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    public BoxedLocation(Location l) {
        super();
        this.world = l.getWorld();
        this.x = l.getX();
        this.y = l.getY();
        this.z = l.getZ();
        this.pitch = l.getPitch();
        this.yaw = l.getYaw();
    }

    public BoxedLocation(String s) throws NumberFormatException, ArrayIndexOutOfBoundsException, NullPointerException {
        s = s.split(":")[1];
        String[] key = s.split(",");
        this.world = Bukkit.getWorld(key[0].replace("[comma]", ",").replace("[colon]", ":"));
        this.x = Double.parseDouble(key[1]);
        this.y = Double.parseDouble(key[2]);
        this.z = Double.parseDouble(key[3]);
        this.yaw = Float.parseFloat(key[4]);
        this.pitch = Float.parseFloat(key[5]);
    }

    public Location unbox() {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public String toString() {
        return "$L:" + world.getName().replace(",", "[comma]").replace(":", "[colon]") + "," + FORMAT.format(x) + "," + FORMAT.format(y) + "," + FORMAT.format(z) + "," + FORMAT.format(yaw) + ","
                + FORMAT.format(pitch);
    }

}
