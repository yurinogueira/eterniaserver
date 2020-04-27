package br.com.eterniaserver.modules.chatmanager.commands;

import br.com.eterniaserver.configs.Messages;
import br.com.eterniaserver.configs.Strings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Broadcast implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, java.lang.String label, java.lang.String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("eternia.advice")) {
                if (args.length >= 1) {
                    StringBuilder sb = new StringBuilder();
                    for (String arg : args) {
                        sb.append(arg).append(" ");
                    }
                    sb.substring(0, sb.length() - 1);
                    Messages.BroadcastMessage("chat.global-advice", "%advice%", Strings.getColor(sb.toString()));
                } else {
                    Messages.PlayerMessage("chat.use", player);
                }
            } else {
                Messages.PlayerMessage("server.no-perm", player);
            }
        } else {
            if (args.length >= 1) {
                StringBuilder sb = new StringBuilder();
                for (String arg : args) {
                    sb.append(arg).append(" ");
                }
                sb.substring(0, sb.length() - 1);
                Messages.BroadcastMessage("chat.global-advice", "%advice%", Strings.getColor(sb.toString()));
            } else {
                Messages.ConsoleMessage("chat.use");
            }
        }
        return true;
    }
}