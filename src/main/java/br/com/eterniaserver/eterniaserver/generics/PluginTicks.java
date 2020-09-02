package br.com.eterniaserver.eterniaserver.generics;

import br.com.eterniaserver.eternialib.UUIDFetcher;
import br.com.eterniaserver.eterniaserver.EterniaServer;
import br.com.eterniaserver.eterniaserver.objects.PlayerTeleport;
import br.com.eterniaserver.paperlib.PaperLib;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PluginTicks extends BukkitRunnable {

    private final EterniaServer plugin;

    public PluginTicks(EterniaServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Location location = player.getLocation();
            final String playerName = player.getName();
            tpaTime(playerName);
            checkNetherTrap(player, location, playerName);
            checkAFK(player, playerName);
            getPlayersInTp(player);
            refreshPlayers(player);
        }
    }

    private void refreshPlayers(Player player) {
        UUID uuid = UUIDFetcher.getUUIDOf(player.getName());
        PluginVars.playersName.put("@" + player.getName(), uuid);
        PluginVars.playersName.put("@" + player.getDisplayName(), uuid);
    }

    private void tpaTime(final String playerName) {
        if (PluginVars.tpaRequests.containsKey(playerName) && PluginVars.tpaTime.containsKey(playerName) &&
                TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - PluginVars.tpaTime.get(playerName)) >= 25) {
            PluginVars.tpaRequests.remove(playerName);
            PluginVars.tpaTime.remove(playerName);
        }
    }

    private void checkNetherTrap(final Player player, final Location location, final String playerName) {
        if (location.getBlock().getType() == Material.NETHER_PORTAL) {
            if (!PluginVars.playersInPortal.containsKey(playerName)) {
                PluginVars.playersInPortal.put(playerName, 7);
            } else if (PluginVars.playersInPortal.get(playerName) <= 1) {
                if (location.getBlock().getType() == Material.NETHER_PORTAL) {
                    runSync(() -> PaperLib.teleportAsync(player, getWarp()));
                    player.sendMessage(PluginMSGs.MSG_WARP_DONE);
                }
            } else if (PluginVars.playersInPortal.get(playerName) > 1) {
                PluginVars.playersInPortal.put(playerName, PluginVars.playersInPortal.get(playerName) - 1);
                if (PluginVars.playersInPortal.get(playerName) < 5) {
                    player.sendMessage(PluginMSGs.MSG_NETHER_TRAP.replace(PluginConstants.COOLDOWN, String.valueOf(PluginVars.playersInPortal.get(playerName))));
                }
            }
        } else {
            PluginVars.playersInPortal.remove(playerName);
        }
    }

    private void checkAFK(final Player player, final String playerName) {
        if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - PluginVars.afkTime.get(playerName)) >= EterniaServer.serverConfig.getInt("server.afk-timer")) {
            if (EterniaServer.serverConfig.getBoolean("server.afk-kick")) {
                if (!PluginVars.afk.contains(playerName) && !player.hasPermission("eternia.nokickbyafksorrymates")) {
                    Bukkit.broadcastMessage(UtilInternMethods.putName(player, PluginMSGs.MSG_AFK_BROAD));
                    runSync(() -> player.kickPlayer(PluginMSGs.MSG_AFK_KICKED));
                }
            } else {
                Bukkit.broadcastMessage(UtilInternMethods.putName(player, PluginMSGs.MSG_AFK_ENABLE));
                PluginVars.afk.add(playerName);
            }
        }
    }

    private void getPlayersInTp(final Player player) {
        if (PluginVars.teleports.containsKey(player)) {
            final PlayerTeleport playerTeleport = PluginVars.teleports.get(player);
            if (!player.hasPermission("eternia.timing.bypass")) {
                if (!playerTeleport.hasMoved()) {
                    if (playerTeleport.getCountdown() == 0) {
                        runSync(()-> PaperLib.teleportAsync(player, playerTeleport.getWantLocation()));
                        player.sendMessage(playerTeleport.getMessage());
                        PluginVars.teleports.remove(player);
                    } else {
                        player.sendMessage(PluginMSGs.MSG_TELEPORT_TIMING.replace(PluginConstants.COOLDOWN, String.valueOf(playerTeleport.getCountdown())));
                        playerTeleport.decreaseCountdown();
                    }
                } else {
                    player.sendMessage(PluginMSGs.MSG_TELEPORT_MOVE);
                    PluginVars.teleports.remove(player);
                }
            } else {
                runSync(()-> PaperLib.teleportAsync(player, playerTeleport.getWantLocation()));
                player.sendMessage(playerTeleport.getMessage());
                PluginVars.teleports.remove(player);
            }
        }
    }

    private Location getWarp() {
        return PluginVars.locations.getOrDefault("warp.spawn", PluginVars.error);
    }

    public void runSync(Runnable runnable) {
        if (EterniaServer.serverConfig.getBoolean("server.async-check")) {
            Bukkit.getScheduler().runTask(plugin, runnable);
            return;
        }
        runnable.run();
    }

}