package eternia.commands.teleports;

import eternia.configs.MVar;
import eternia.configs.Vars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportToPlayer implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("eternia.tpa")) {
                if (args.length == 1) {
                    Player target = Vars.findPlayer(args[0]);
                    if (target.isOnline()) {
                        if (!(target == player)) {
                            Vars.tpa_requests.put(target, player);
                            MVar.playerReplaceMessage("server.receiver", player.getName(), target);
                            MVar.playerReplaceMessage("teleport.send", target.getName(), player);
                        } else {
                            MVar.playerMessage("teleport.auto", player);
                        }
                    } else {
                        MVar.playerMessage("server.player-offline", player);
                    }
                } else {
                    MVar.playerMessage("teleport.use", player);
                }
            } else {
                MVar.playerMessage("server.no-perm", player);
            }
        } else {
            MVar.consoleMessage("server.only-player");
        }
        return true;
    }
}