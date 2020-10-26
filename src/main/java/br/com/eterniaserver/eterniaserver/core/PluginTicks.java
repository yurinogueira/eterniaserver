package br.com.eterniaserver.eterniaserver.core;

import br.com.eterniaserver.eterniaserver.EterniaServer;
import br.com.eterniaserver.eterniaserver.enums.Messages;
import br.com.eterniaserver.eterniaserver.objects.PlayerTeleport;
import br.com.eterniaserver.paperlib.PaperLib;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public class PluginTicks extends BukkitRunnable {

    private final EterniaServer plugin;

    public PluginTicks(EterniaServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = new User(player);

            tpaTime(user);
            checkNetherTrap(user);
            checkAFK(user);
            getPlayersInTp(user);
            refreshPlayers(user);
            optimizedMoveEvent(user);

        }
    }

    private void optimizedMoveEvent(User user) {
        Location location = user.getLocation();
        Location firstLocation = Vars.playerLocationMap.getOrDefault(user.getUUID(), location);

        if (!(firstLocation.getBlockX() == location.getBlockX() && firstLocation.getBlockY() == location.getBlockY() && firstLocation.getBlockZ() == location.getBlockZ())) {
            user.updateAfkTime();
            if (user.isAfk()) {
                user.changeAfkState();
                Bukkit.broadcastMessage(EterniaServer.msg.getMessage(Messages.AFK_LEAVE, true, user.getName(), user.getDisplayName()));
            }
        }
        Vars.playerLocationMap.put(user.getUUID(), location);
    }

    private void refreshPlayers(User user) {
        Vars.playersName.put(EterniaServer.constants.mentionPlaceholder + user.getName(), user.getUUID());
        Vars.playersName.put(EterniaServer.constants.mentionPlaceholder + user.getDisplayName(), user.getUUID());
    }

    private void tpaTime(User user) {
        if (user.hasTpaRequest() && TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - Vars.tpaTime.get(user.getUUID())) >= 25) {
            user.removeTpaRequest();
        }
    }

    private void checkNetherTrap(User user) {
        if (user.getLocation().getBlock().getType() == Material.NETHER_PORTAL) {
            int time = Vars.playersInPortal.getOrDefault(user.getUUID(), -1);
            if (time == -1) {
                Vars.playersInPortal.put(user.getUUID(), 10);
            } else if (Vars.playersInPortal.get(user.getUUID()) == 1) {
                runSync(() -> PaperLib.teleportAsync(user.getPlayer(), Vars.locations.getOrDefault("warp.spawn", Vars.getError())));
                user.sendMessage(Messages.WARP_SPAWN_TELEPORTED);
            } else if (time > 1) {
                Vars.playersInPortal.put(user.getUUID(), time - 1);
                if ((time - 1) < 5) {
                    user.sendMessage(Messages.SERVER_NETHER_TRAP_TIMING, String.valueOf(time - 1));
                }
            }
        } else {
            Vars.playersInPortal.put(user.getUUID(), -1);
        }
    }

    private void checkAFK(User user) {
        if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - user.getAfkTime()) < EterniaServer.configs.afkTimer) return;

        if (!EterniaServer.configs.afkKick) {
            Bukkit.broadcastMessage(EterniaServer.msg.getMessage(Messages.AFK_AUTO_ENTER, false, user.getName(), user.getDisplayName()));
            user.changeAfkState();
            return;
        }

        if (!user.isAfk() && !user.hasPermission(EterniaServer.constants.permNoKickAfk)) {
            Bukkit.broadcastMessage(EterniaServer.msg.getMessage(Messages.AFK_BROADCAST_KICK, true, user.getName(), user.getDisplayName()));
            user.clear();
            runSync(() -> user.kick(EterniaServer.msg.getMessage(Messages.AFK_KICKED, true)));
            return;
        }

        if (!user.isAfk() && user.hasPermission(EterniaServer.constants.permAfk)) {
            Bukkit.broadcastMessage(EterniaServer.msg.getMessage(Messages.AFK_AUTO_ENTER, false, user.getName(), user.getDisplayName()));
            user.changeAfkState();
        }
    }

    private void getPlayersInTp(User user) {
        if (user.isTeleporting()) {
            final PlayerTeleport playerTeleport = Vars.teleports.get(user.getUUID());
            if (!user.hasPermission(EterniaServer.constants.permTimingBypass)) {
                if (!playerTeleport.hasMoved()) {
                    if (playerTeleport.getCountdown() == 0) {
                        runSync(()-> PaperLib.teleportAsync(user.getPlayer(), playerTeleport.getWantLocation()));
                        user.getPlayer().sendMessage(playerTeleport.getMessage());
                        user.removeFromTeleporting();
                    } else {
                        user.sendMessage(Messages.TELEPORT_TIMING, String.valueOf(playerTeleport.getCountdown()));
                        playerTeleport.decreaseCountdown();
                    }
                } else {
                    user.sendMessage(Messages.TELEPORT_MOVED);
                    user.removeFromTeleporting();
                }
            } else {
                runSync(()-> PaperLib.teleportAsync(user.getPlayer(), playerTeleport.getWantLocation()));
                user.getPlayer().sendMessage(playerTeleport.getMessage());
                user.removeFromTeleporting();
            }
        }
    }

    private void runSync(Runnable runnable) {
        if (EterniaServer.configs.asyncCheck) {
            Bukkit.getScheduler().runTask(plugin, runnable);
            return;
        }
        runnable.run();
    }

}