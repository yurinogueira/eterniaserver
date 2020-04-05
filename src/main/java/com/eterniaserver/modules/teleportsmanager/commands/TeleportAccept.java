package com.eterniaserver.modules.teleportsmanager.commands;

import com.eterniaserver.EterniaServer;
import com.eterniaserver.configs.CVar;
import com.eterniaserver.configs.Vars;
import com.eterniaserver.configs.methods.ConsoleMessage;
import com.eterniaserver.configs.methods.PlayerMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportAccept implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("eternia.tpa")) {
                if (Vars.tpa_requests.containsKey(player.getName())) {
                    Player target = Bukkit.getPlayer(Vars.tpa_requests.get(player.getName()));
                    assert target != null;
                    if (target.hasPermission("eternia.timing.bypass")) {
                        target.teleport(player.getLocation());
                        new PlayerMessage("teleport.tpto", player.getName(), target);
                        new PlayerMessage("teleport.accept", player.getName(), target);
                        Vars.tpa_requests.remove(player.getName());
                    } else {
                        new PlayerMessage("teleport.timing", CVar.getInt("server.cooldown"), target);
                        new PlayerMessage("teleport.accept", player.getName(), target);
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(EterniaServer.getMain(), () ->
                        {
                            target.teleport(player.getLocation());
                            new PlayerMessage("teleport.tpto", player.getName(), target);
                            Vars.tpa_requests.remove(player.getName());
                        }, 20 * CVar.getInt("server.cooldown"));
                    }
                } else {
                    new PlayerMessage("teleport.noask", player);
                }
            } else {
                new PlayerMessage("server.no-perm", player);
            }
        } else {
            new ConsoleMessage("server.only-player");
        }
        return true;
    }
}
