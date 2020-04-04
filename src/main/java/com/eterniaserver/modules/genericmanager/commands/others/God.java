package com.eterniaserver.modules.genericmanager.commands.others;

import com.eterniaserver.configs.MVar;
import com.eterniaserver.configs.Vars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class God implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("eternia.god")) {
                if (Vars.god.get(player)) {
                    MVar.playerMessage("simp.godd", player);
                    Vars.god.remove(player);
                } else {
                    MVar.playerMessage("simp.gode", player);
                    Vars.god.put(player, true);
                }
            }
        } else {
            MVar.consoleMessage("server.only-player");
        }
        return true;
    }
}
