package br.com.eterniaserver.modules.homesmanager.commands;

import br.com.eterniaserver.EterniaServer;
import br.com.eterniaserver.configs.Vars;
import br.com.eterniaserver.configs.methods.ConsoleMessage;
import br.com.eterniaserver.configs.methods.PlayerMessage;
import br.com.eterniaserver.modules.homesmanager.sql.Queries;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Home implements CommandExecutor {

    private final EterniaServer plugin;

    public Home(EterniaServer plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("eternia.home")) {
                if (args.length == 1) {
                    final Location location = Queries.getHome(args[0].toLowerCase(), player.getName());
                    if (location != Vars.error) {
                        if (player.hasPermission("eternia.timing.bypass")) {
                            player.teleport(location);
                            new PlayerMessage("home.suc", player);
                        } else {
                            new PlayerMessage("teleport.timing", EterniaServer.configs.getInt("server.cooldown"), player);
                            Vars.playerposition.put(player.getName(), player.getLocation());
                            Vars.moved.put(player.getName(), false);
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                if (!Vars.moved.get(player.getName())) {
                                    player.teleport(location);
                                    new PlayerMessage("home.suc", player);
                                } else {
                                    new PlayerMessage("warps.move", player);
                                }
                            }, 20 * EterniaServer.configs.getInt("server.cooldown"));
                        }
                    } else {
                        new PlayerMessage("home.no", player);
                    }
                } else {
                    new PlayerMessage("home.use2", player);
                }
            } else {
                new PlayerMessage("sem-permissao", player);
            }
        } else {
            new ConsoleMessage("server.only-player");
        }
        return true;
    }

}