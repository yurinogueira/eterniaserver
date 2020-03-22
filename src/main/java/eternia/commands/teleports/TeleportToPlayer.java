package eternia.commands.teleports;

import eternia.configs.MVar;
import eternia.configs.Vars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("NullableProblems")
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
                            MVar.playerReplaceMessage("pediu-tpa", player.getName(), target);
                            MVar.playerReplaceMessage("enviou-tpa", target.getName(), player);
                        } else {
                            MVar.playerMessage("tpa-voce", player);
                        }
                    } else {
                        MVar.playerMessage("jogador-offline", player);
                    }
                } else {
                    MVar.playerMessage("tpa-errado", player);
                }
            } else {
                MVar.playerMessage("sem-permissao", player);
            }
        } else {
            MVar.consoleMessage("somente-jogador");
        }
        return true;
    }
}