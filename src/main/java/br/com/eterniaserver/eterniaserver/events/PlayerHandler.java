package br.com.eterniaserver.eterniaserver.events;

import br.com.eterniaserver.eternialib.EQueries;
import br.com.eterniaserver.eternialib.UUIDFetcher;
import br.com.eterniaserver.eterniaserver.Configs;
import br.com.eterniaserver.eterniaserver.generics.*;
import br.com.eterniaserver.eterniaserver.objects.PlayerTeleport;
import br.com.eterniaserver.paperlib.PaperLib;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;

import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerHandler implements Listener {

    private final List<Material> list = List.of(Material.RAIL, Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.ACTIVATOR_RAIL);

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!Configs.instance.moduleHomes && !Configs.instance.moduleExperience && !Configs.instance.moduleSpawners) return;

        final Player player = event.getPlayer();
        final Action action = event.getAction();

        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            final ItemStack is = player.getInventory().getItemInMainHand();
            final List<String> lore = is.getLore();
            if (Configs.instance.moduleHomes && is.getType().equals(Material.COMPASS)
                    && lore != null) {
                final String[] isso = lore.get(0).split(":");
                final Location location = new Location(Bukkit.getWorld(isso[0]), Double.parseDouble(isso[1]) + 1, Double.parseDouble(isso[2]), Double.parseDouble(isso[3]), Float.parseFloat(isso[4]), Float.parseFloat(isso[5]));

                if (APIPlayer.isTeleporting(player)) {
                    player.sendMessage(PluginMSGs.MSG_IN_TELEPORT);
                } else {
                    APIServer.putInTeleport(player, new PlayerTeleport(player, location, PluginMSGs.M_HOME_DONE));
                }
                event.setCancelled(true);
            }
            if (Configs.instance.moduleExperience && is.getType().equals(Material.EXPERIENCE_BOTTLE)
                    && lore != null) {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                player.giveExp(Integer.parseInt(lore.get(0)));
            }
            if (event.getClickedBlock() != null && list.contains(event.getClickedBlock().getType())) {
                final Location location = event.getClickedBlock().getLocation();
                location.getNearbyEntities(2, 2, 2).forEach(k -> {
                    if (k instanceof Minecart) {
                        event.setCancelled(true);
                    }
                });
            }
        }
        if (Configs.instance.moduleSpawners && event.getClickedBlock() != null
                && action.equals(Action.RIGHT_CLICK_BLOCK) && event.getItem() != null
                && event.getClickedBlock().getType() == Material.SPAWNER
                && !player.hasPermission("eternia.change-spawner")) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (Configs.instance.moduleTeleports) {
            final Player player = event.getEntity();
            APIServer.putBackLocation(player.getName(), player.getLocation());
        }
    }

    @EventHandler (priority = EventPriority.HIGH)
    public void onPlayerSpawnLocation(PlayerSpawnLocationEvent event) {
        if (Configs.instance.moduleTeleports && APIServer.hasLocation("warp.spawn")) {
            if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - event.getPlayer().getFirstPlayed()) < 10) {
                event.setSpawnLocation(APIServer.getLocation("warp.spawn"));
            }
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (Configs.instance.moduleTeleports && APIServer.hasLocation("warp.spawn")) {
            event.setRespawnLocation(APIServer.getLocation("warp.spawn"));
        }
    }

    @EventHandler (priority = EventPriority.LOW)
    public void onPlayerLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        final String playerName = player.getName();
        APIPlayer.removeFromAFK(playerName);
        final UUID uuid = UUIDFetcher.getUUIDOf(playerName);
        EQueries.executeQuery(PluginConstants.getQueryUpdate(Configs.instance.tablePlayer, PluginConstants.HOURS_STR, APIPlayer.getAndUpdateTimePlayed(uuid), PluginConstants.UUID_STR, uuid.toString()));
        if (Configs.instance.moduleChat) {
            UtilInternMethods.removeUUIF(player);
            if (player.hasPermission("eternia.spy")) {
                APIServer.removeFromSpy(playerName);
            }
        }
        event.setQuitMessage(null);
        Bukkit.broadcastMessage(UtilInternMethods.putName(player, PluginMSGs.MSG_LEAVE));
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();
        String message = event.getMessage().toLowerCase();
        if (message.equalsIgnoreCase("/tps")) {
            player.sendMessage(PluginMSGs.MSG_TPS.replace(PluginConstants.TPS, String.format("%.2f", Bukkit.getTPS()[0])));
            event.setCancelled(true);
            return;
        }
        for (String line : Configs.instance.blockedCommands) {
            if (message.startsWith(line)) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (!Configs.instance.moduleBed) return;

        if (event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            final Player player = event.getPlayer();
            final String playerName = player.getName();
            if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - UtilInternMethods.getCooldown(playerName)) > 6) {
                APIServer.putBedCooldown(playerName);
                Bukkit.broadcastMessage(UtilInternMethods.putName(player, PluginMSGs.MSG_PLAYER_SKIP));
            }
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerBedLeave(PlayerBedLeaveEvent event) {
        if (!Configs.instance.moduleBed) return;

        final Player player = event.getPlayer();
        final String playerName = player.getName();
        if (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - UtilInternMethods.getCooldown(playerName)) > 6) {
            APIServer.putBedCooldown(playerName);
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) return;
        final Player player = event.getPlayer();
        if (Configs.instance.moduleTeleports) {
            APIServer.putBackLocation(player.getName(), player.getLocation());
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        final Player player = event.getPlayer();
        if (Configs.instance.moduleElevator && player.hasPermission("eternia.elevator") && !player.isSneaking()) {
            Block block = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
            Material material = block.getType();
            for (Material value : Configs.instance.elevatorMaterials) {
                if (value == material) {
                    block = block.getRelative(BlockFace.DOWN, Configs.instance.elevatorMin);
                    int i;
                    for (i = Configs.instance.elevatorMax; i > 0 && (block.getType() != material); block = block.getRelative(BlockFace.DOWN)) --i;
                    elevatorDown(player, i);
                    break;
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerJump(PlayerJumpEvent event) {
        final Player player = event.getPlayer();
        if (Configs.instance.moduleTeleports && player.hasPermission("eternia.elevator")) {
            Block block = event.getTo().getBlock().getRelative(BlockFace.DOWN);
            Material material = block.getType();
            for (Material value : Configs.instance.elevatorMaterials) {
                if (value == material) {
                    block = block.getRelative(BlockFace.UP, Configs.instance.elevatorMin);
                    int i;
                    for (i = Configs.instance.elevatorMax; i > 0 && (block.getType() != material); block = block.getRelative(BlockFace.UP)) -- i;
                    elevatorUp(player, i);
                    break;
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        final String playerName = player.getName();
        final UUID uuid = UUIDFetcher.getUUIDOf(playerName);

        event.setJoinMessage(null);
        Bukkit.broadcastMessage(UtilInternMethods.putName(player, PluginMSGs.MSG_JOIN));

        if (!APIPlayer.hasProfile(uuid)) {
            PaperLib.teleportAsync(player, APIServer.getLocation("warp.spawn"));
            APIServer.playerProfileCreate(uuid, playerName, player.getFirstPlayed());
        } else {
            APIPlayer.updatePlayerProfile(uuid, player, System.currentTimeMillis());
        }

        if (Configs.instance.moduleChat) {
            UtilInternMethods.addUUIF(player);
            if (player.hasPermission("eternia.spy")) {
                APIServer.putSpy(playerName);
            }
            if (APIPlayer.hasProfile(uuid)) {
                player.setDisplayName(APIPlayer.getDisplayName(uuid));
            }
        }

        APIServer.playerKitsCreate(playerName);
        APIPlayer.putAfk(playerName);
    }

    private void elevatorUp(final Player player, final int i) {
        if (i > 0) {
            Location location = player.getLocation();
            location.setY((location.getY() + Configs.instance.elevatorMax + 3.0D - (double) i) - 1);
            PaperLib.teleportAsync(player, location);
            player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.F));
        }
    }

    private void elevatorDown(final Player player, final int i) {
        if (i > 0) {
            Location location = player.getLocation();
            location.setY((location.getY() - Configs.instance.elevatorMax - 3.0D + (double) i) + 1);
            PaperLib.teleportAsync(player, location);
            player.playNote(player.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.D));
        }
    }

}