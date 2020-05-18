package br.com.eterniaserver.eterniaserver.modules.experiencemanager.commands;

import br.com.eterniaserver.eterniaserver.API.Exp;
import br.com.eterniaserver.eterniaserver.configs.Messages;
import br.com.eterniaserver.eterniaserver.configs.methods.Checks;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Syntax;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class Experience extends BaseCommand {

    private final Checks checks;
    private final Messages messages;
    private final Exp expx;

    public Experience(Checks checks, Messages messages, Exp expx) {
        this.checks = checks;
        this.messages = messages;
        this.expx = expx;
    }

    @CommandAlias("checklevel|verlevel")
    @CommandPermission("eternia.checklvl")
    public void onCheckLevel(Player player) {
        int lvl = player.getLevel();
        float xp = player.getExp();
        player.setLevel(0);
        player.setExp(0);
        player.giveExp(expx.getExp(player.getName()));
        messages.sendMessage("xp.getxp", "%amount%", player.getLevel(), player);
        player.setLevel(lvl);
        player.setExp(xp);
    }

    @CommandAlias("bottlelvl|bottleexp|gaffinhas")
    @Syntax("<level>")
    @CommandPermission("eternia.bottlexp")
    public void onBottleLevel(Player player, Integer xp_want) {
        int xp_real = checks.getXPForLevel(player.getLevel());
        if (xp_want > 0 && xp_real > xp_want) {
            ItemStack item = new ItemStack(Material.EXPERIENCE_BOTTLE);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setLore(Collections.singletonList(String.valueOf(xp_want)));
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&8[&eGarrafa de EXP&8]"));
                item.setItemMeta(meta);
            }
            PlayerInventory inventory = player.getInventory();
            inventory.addItem(item);
            messages.sendMessage("xp.bottlexp", player);
            player.setLevel(0);
            player.setExp(0);
            player.giveExp(xp_real - xp_want);
        } else {
            messages.sendMessage("xp.noxp", player);
        }
    }

    @CommandAlias("withdrawlvl|pegarlvl|takelvl")
    @Syntax("<level>")
    @CommandPermission("eternia.withdrawlvl")
    public void onWithdrawLevel(Player player, Integer level) {
        int xpla = checks.getXPForLevel(level);
        if (expx.getExp(player.getName()) >= xpla) {
            expx.removeExp(player.getName(), xpla);
            player.giveExp(xpla);
            messages.sendMessage("xp.withdraw", "%level%", player.getLevel(), player);
        } else {
            messages.sendMessage("xp.noxp", player);
        }
    }

    @CommandAlias("depositlvl|depositarlvl")
    @Syntax("<level>")
    @CommandPermission("eternia.depositlvl")
    public void onDepositLevel(Player player, Integer xpla) {
        int xp_atual = player.getLevel();
        if (xp_atual >= xpla) {
            int xp = checks.getXPForLevel(xpla);
            int xpto = checks.getXPForLevel(xp_atual);
            expx.addExp(player.getName(), xp);
            messages.sendMessage("xp.deposit", "%amount%", xpla, player);
            player.setLevel(0);
            player.setExp(0);
            player.giveExp(xpto - xp);
        } else {
            messages.sendMessage("xp.noxp", player);
        }
    }

}
