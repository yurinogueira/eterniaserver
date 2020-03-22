package eternia.commands.others;

import eternia.configs.MVar;
import eternia.configs.Vars;
import eternia.player.PlayerFlyState;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("NullableProblems")
public class Fly implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("eternia.fly")) {
                if (args.length == 0) {
                    if (player.getWorld() == Bukkit.getWorld("evento")) {
                        if (player.hasPermission("eternia.fly.evento")) {
                            PlayerFlyState.selfFly(player);
                        } else {
                            MVar.playerMessage("sem-permissao", player);
                        }
                    } else {
                        PlayerFlyState.selfFly(player);
                    }
                    return true;
                } else if (args.length == 1) {
                    if (player.hasPermission("eternia.fly.other")) {
                        Player target = Vars.findPlayer(args[0]);
                        if (target.isOnline()) {
                            PlayerFlyState.otherFly(target);
                        } else {
                            MVar.playerMessage("jogador-offline", player);
                        }
                    } else {
                        MVar.playerMessage("sem-permissao", player);
                    }
                } else {
                    MVar.playerMessage("fly-use", player);
                }
            } else {
                MVar.playerMessage("sem-permissao", player);
            }
        } else if (args.length == 1) {
            Player target = Vars.findPlayer(args[0]);
            if (target.isOnline()) {
                PlayerFlyState.otherFly(target);
            } else {
                MVar.consoleMessage("jogador-offline");
            }
        }
        return true;
    }
}