package eternia.commands.teleports;

import eternia.configs.MVar;
import eternia.configs.Vars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawn implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("eternia.setspawn")) {
                Vars.setLocation("world-n", "x-n", "y-n", "z-n", "yaw-n", "pitch-n", player);
                MVar.playerMessage("warps.spawn-set", player);
            } else {
                MVar.playerMessage("server.no-perm", player);
            }
        } else {
            MVar.consoleMessage("server.only-player");
        }
        return true;
    }
}