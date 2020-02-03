package others;

import center.Vars;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Fly implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            if(player.hasPermission("eternia.fly"))
            {
                if (args.length == 0)
                {
                    if(player.getAllowFlight())
                    {
                        player.setAllowFlight(false);
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_HIT, 1, 1);
                        player.sendMessage(Vars.getMessage("desativar-voar"));
                        return true;
                    }
                    else
                    {
                        player.setAllowFlight(true);
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_HIT, 1, 1);
                        player.sendMessage(Vars.getMessage("ativar-voar"));
                        return true;
                    }
                }
                else if (args.length == 1)
                {
                    if (player.hasPermission("eternia.comandos.staff.fly.other"))
                    {
                        String targetS = args[0];
                        Player target = Bukkit.getPlayer(targetS);
                        assert target != null;
                        if(target.isOnline())
                        {
                            if (target.getAllowFlight())
                            {
                                target.setAllowFlight(false);
                                target.playSound(target.getLocation(), Sound.BLOCK_ANVIL_HIT, 1, 1);
                                target.sendMessage(Vars.replaceMessage("desativaram-voar", player.getName()));
                                player.sendMessage(Vars.replaceMessage("desativar-voar-de", target.getName()));
                                return true;
                            }
                            else
                            {
                                target.setAllowFlight(true);
                                target.playSound(target.getLocation(), Sound.BLOCK_ANVIL_HIT, 1, 1);
                                target.sendMessage(Vars.replaceMessage("ativaram-voar", player.getName()));
                                player.sendMessage(Vars.replaceMessage("ativar-voar-de", target.getName()));
                                return true;
                            }
                        }
                        else
                        {
                            player.sendMessage(Vars.getMessage("jogador-offline"));
                            return true;
                        }
                    }
                    else
                    {
                        player.sendMessage(Vars.getMessage("sem-permissao"));
                        return true;
                    }
                }
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