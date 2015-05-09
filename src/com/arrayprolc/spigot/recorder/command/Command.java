package com.arrayprolc.spigot.recorder.command;

import org.bukkit.command.CommandSender;

public abstract class Command {

    private String[] aliases;
    private String neededPermission;

    public Command(String[] aliases, String neededPermission) {
        super();
        this.aliases = aliases;
        this.neededPermission = neededPermission;
    }

    public abstract void run(CommandSender sender, org.bukkit.command.Command command, String label, String[] args);

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    public String getNeededPermission() {
        return neededPermission;
    }

    public void setNeededPermission(String neededPermission) {
        this.neededPermission = neededPermission;
    }

}
