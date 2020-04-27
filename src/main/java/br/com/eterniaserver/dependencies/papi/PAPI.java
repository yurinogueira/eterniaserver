package br.com.eterniaserver.dependencies.papi;

import br.com.eterniaserver.configs.Messages;
import br.com.eterniaserver.configs.Strings;

import me.clip.placeholderapi.PlaceholderAPI;

import org.bukkit.Bukkit;

public class PAPI {
    public PAPI () {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            Messages.ConsoleMessage("server.no-papi");
            Strings.papi = false;
        } else {
            PlaceholderAPI.registerPlaceholderHook("eterniaserver", new PlaceHolders());
        }
    }
}