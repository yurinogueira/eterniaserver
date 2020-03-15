package teleports;

import center.Main;
import center.Vars;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Spawn implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                if (player.hasPermission("eternia.spawn")) {
                    if (player.hasPermission("eternia.timing.bypass")) {
                        player.teleport(Vars.spawn);
                        Vars.playerMessage("spawn", player);
                    } else {
                        int tempo = Vars.getInt("cooldown");
                        Vars.playerReplaceMessage("teleportando-em", tempo, player);
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getMain(), () ->
                        {
                            player.teleport(Vars.spawn);
                            Vars.playerMessage("spawn", player);
                        }, 20 * tempo);
                    }
                } else {
                    Vars.playerMessage("sem-permissao", player);
                }
            } else if (args.length == 1) {
                if (player.hasPermission("eternia.spawn.other")) {
                    String targetS = args[0];
                    Player target = Bukkit.getPlayer(targetS);
                    assert target != null;
                    if (target.isOnline()) {
                        target.teleport(Vars.spawn);
                        Vars.playerMessage("spawn", target);
                        Vars.playerReplaceMessage("teleportou-ele", target.getName(), player);
                    } else {
                        Vars.playerMessage("jogador-offline", player);
                    }
                } else {
                    Vars.playerMessage("sem-permissao", player);
                }
            }
        } else {
            Vars.consoleMessage("somente-jogador");
        }
        return true;
    }
}