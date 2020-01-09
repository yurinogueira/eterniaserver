package commands.staff;

import center.vars;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class spectator implements CommandExecutor
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
                player.sendMessage(vars.getString("escondido"));
            }
            else
            {
                player.sendMessage(vars.getString("sem-permissao"));
                return true;
            }
        }
        return false;
    }
}