package others;

import center.Vars;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Spectator implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            if(player.hasPermission("eternia.spectator"))
            {
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(Vars.getMessage("escondido"));
            }
            else
            {
                player.sendMessage(Vars.getMessage("sem-permissao"));
                return true;
            }
        }
        return false;
    }
}