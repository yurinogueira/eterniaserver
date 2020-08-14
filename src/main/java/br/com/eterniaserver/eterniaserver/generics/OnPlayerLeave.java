package br.com.eterniaserver.eterniaserver.generics;

import br.com.eterniaserver.eternialib.EQueries;
import br.com.eterniaserver.eternialib.UUIDFetcher;
import br.com.eterniaserver.eterniaserver.configs.Configs;
import br.com.eterniaserver.eterniaserver.configs.Constants;
import br.com.eterniaserver.eterniaserver.EterniaServer;
import br.com.eterniaserver.eterniaserver.configs.Strings;
import br.com.eterniaserver.eterniaserver.objects.PlayerProfile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class OnPlayerLeave implements Listener, Constants {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {

        final Player player = event.getPlayer();
        final String playerName = player.getName();
        Vars.afkTime.remove(playerName);
        final UUID uuid = UUIDFetcher.getUUIDOf(playerName);
        final PlayerProfile playerProfile = Vars.playerProfile.get(uuid);
        int hours = playerProfile.getHours() + (int) TimeUnit.MICROSECONDS.toHours(System.currentTimeMillis() - playerProfile.getLastLogin());
        playerProfile.setHours(hours);
        Vars.playerProfile.put(uuid, playerProfile);
        EQueries.executeQuery(Constants.getQueryUpdate(Configs.TABLE_PLAYER, HOURS_STR, hours, UUID_STR, uuid.toString()));


        if (EterniaServer.serverConfig.getBoolean("modules.chat")) {
            InternMethods.removeUUIF(player);
            if (player.hasPermission("eternia.spy")) Vars.spy.remove(playerName);
        }

        event.setQuitMessage(null);
        Bukkit.broadcastMessage(Strings.MSG_LEAVE.replace(PLAYER, player.getDisplayName()));

    }

}