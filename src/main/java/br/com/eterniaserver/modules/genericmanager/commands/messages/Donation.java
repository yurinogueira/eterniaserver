package br.com.eterniaserver.modules.genericmanager.commands.messages;

import br.com.eterniaserver.configs.methods.ConsoleMessage;
import br.com.eterniaserver.configs.methods.PlayerMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Donation implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("eternia.donation")) {
                new PlayerMessage("text.donation", player);
            } else {
                new PlayerMessage("server.no-perm", player);
            }
        } else {
            new ConsoleMessage("doacao");
        }
        return true;
    }
}