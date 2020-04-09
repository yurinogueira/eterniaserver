package br.com.eterniaserver.modules.genericmanager.commands.others;

import br.com.eterniaserver.configs.methods.ConsoleMessage;
import br.com.eterniaserver.configs.methods.PlayerMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EnderChest implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1 && player.hasPermission("eternia.enderchest.other")) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null && target.isOnline()) {
                    player.openInventory(target.getEnderChest());
                } else {
                    new PlayerMessage("server.player-offline", player);
                }
                player.openInventory(player.getEnderChest());
            } else if (args.length == 0 && player.hasPermission("eternia.enderchest")) {
                player.openInventory(player.getEnderChest());
            } else {
                new PlayerMessage("server.no-perm", player);
            }
        } else {
            new ConsoleMessage("server.only-player");
        }
        return true;
    }
}