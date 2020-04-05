package com.eterniaserver.configs;

import com.eterniaserver.EterniaServer;

public class MVar {

    public static String getMessage(String valor) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', getString(valor));
    }

    public static String getString(String valor) {
        return EterniaServer.getMessages().getString(valor);
    }

    public static String getColor(String valor) {
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', valor);
    }

}
