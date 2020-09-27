package br.com.eterniaserver.eterniaserver.events;

import br.com.eterniaserver.eterniaserver.Configs;

import br.com.eterniaserver.eterniaserver.EterniaServer;
import br.com.eterniaserver.eterniaserver.generics.PluginMSGs;
import br.com.eterniaserver.eterniaserver.generics.PluginVars;
import br.com.eterniaserver.eterniaserver.generics.UtilInternMethods;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BlockHandler implements Listener {

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerSignChange(SignChangeEvent event) {
        if (event.getPlayer().hasPermission("eternia.sign.color")) {
            for (byte i = 0; i < 4; i++) {
                event.setLine(i, PluginMSGs.getColor(event.getLine(i)));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerBlockPlace(BlockPlaceEvent event) {
        if (!Configs.instance.moduleSpawners) return;

        final Block block = event.getBlockPlaced();
        if (block.getType() == Material.SPAWNER) {
            final ItemMeta meta = event.getItemInHand().getItemMeta();
            if (meta != null) {
                final String entityName = ChatColor.stripColor(meta.getDisplayName()).split(" Spawner")[0].replace("[", "").replace(" ", "_").toUpperCase();
                final EntityType entity = EntityType.valueOf(entityName);
                CreatureSpawner spawner = (CreatureSpawner) block.getState();
                spawner.setSpawnedType(entity);
                spawner.update();
            }
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayerBlockBreak(BlockBreakEvent event) {
        if (!Configs.instance.moduleSpawners && !Configs.instance.moduleBlock) return;

        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        final Material material = block.getType();
        final String materialName = material.name().toUpperCase();
        final String worldName = player.getWorld().getName();

        if (Configs.instance.moduleSpawners && material == Material.SPAWNER && !isBlackListWorld(worldName) && player.hasPermission("eternia.spawners.break")) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.containsEnchantment(Enchantment.SILK_TOUCH) || player.hasPermission("eternia.spawners.nosilk")) {
                giveSpawner(player, material, block);
                event.setExpToDrop(0);
            } else {
                event.setCancelled(true);
                player.sendMessage(PluginMSGs.MSG_SPAWNER_SILK);
            }
        } else if (!player.hasPermission("eternia.spawners.break") && material == Material.SPAWNER) {
            player.sendMessage(PluginMSGs.MSG_NO_PERM);
            event.setCancelled(true);
        } else if (isBlackListWorld(worldName) && material == Material.SPAWNER) {
            player.sendMessage(PluginMSGs.MSG_SPAWNER_BLOCKED);
            event.setCancelled(true);
        }

        if (!Configs.instance.moduleBlock) return;

        if (EterniaServer.blockConfig.contains("blocks." + materialName)) {
            randomizeAndReward(materialName, player, "blocks.");
        }

        if (EterniaServer.blockConfig.contains("farm." + materialName)) {
            winFarmReward(block, player);
        }

    }

    private void winFarmReward(Block block, Player player) {
        BlockData blockData = block.getBlockData();
        if (blockData instanceof Ageable) {
            Ageable ageable = (Ageable) blockData;
            if (ageable.getAge() == ageable.getMaximumAge()) {
                String materialName = block.getType().name().toUpperCase();
                randomizeAndReward(materialName, player, "farm.");
            }
        }
    }

    private void randomizeAndReward(String materialName, Player player, String config) {
        double randomNumber = Math.random();
        double lowestNumberAboveRandom = 1.1;
        for (String key : EterniaServer.blockConfig.getConfigurationSection(config + materialName).getKeys(false)) {
            double current = Double.parseDouble(key);
            if (current < lowestNumberAboveRandom && current > randomNumber) {
                lowestNumberAboveRandom = current;
            }
        }
        if (lowestNumberAboveRandom <= 1) {
            for (String command : EterniaServer.blockConfig.getStringList(config + materialName + "." + lowestNumberAboveRandom)) {
                Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), UtilInternMethods.setPlaceholders(player, command));
            }
        }
    }

    private void giveSpawner(final Player player, final Material material, Block block) {
        double random = Math.random();
        if (random < Configs.instance.dropChance) {
            if (Configs.instance.invDrop) {
                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(getSpawner(block, material));
                    block.getDrops().clear();
                } else {
                    player.sendMessage(PluginMSGs.MSG_SPAWNER_INVFULL);
                    final Location loc = block.getLocation();
                    loc.getWorld().dropItemNaturally(loc, getSpawner(block, material));
                }
            } else {
                final Location loc = block.getLocation();
                loc.getWorld().dropItemNaturally(loc, getSpawner(block, material));
            }
        } else {
            player.sendMessage(PluginMSGs.MSG_SPAWNER_FAILED);
        }
    }

    private ItemStack getSpawner(final Block block, final Material material) {
        final CreatureSpawner spawner = (CreatureSpawner) block.getState();
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        String mobFormatted = spawner.getSpawnedType().toString();
        meta.setDisplayName(PluginVars.colors.get(8) + "[" + Configs.instance.mobSpawnerColor + mobFormatted + PluginVars.colors.get(7) + " Spawner" +  PluginVars.colors.get(8) + "]");
        item.setItemMeta(meta);
        return item;
    }

    private boolean isBlackListWorld(final String worldName) {
        return Configs.instance.blacklistedWorldsSpawners.contains(worldName);
    }


}