package commands.player;
import center.main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class withdrawlvl implements CommandExecutor
{
    private final main plugin = main.getPlugin(main.class);
    @EventHandler
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player)
        {
            Player player = (Player) sender;
            UUID uuid = player.getUniqueId();
            try
            {
                int xp1 = player.getLevel();
                player.sendMessage(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + "E" + ChatColor.BLUE +
                        "S" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "Pronto seu level agora é " + ChatColor.DARK_AQUA
                        + xp1 + ChatColor.DARK_GRAY + ".");
                try
                {
                    Statement statement = plugin.getConnection().createStatement();
                    ResultSet results = statement.executeQuery("SELECT XP FROM " + plugin.table + " WHERE UUID = '"+uuid.toString()+"'");
                    String Vu = "";
                    while (results.next())
                    {
                        Vu = results.getString("XP");
                    }
                    results.close();
                    player.giveExp(Integer.parseInt(Vu));
                    int v = 0;
                    statement.executeUpdate("UPDATE " + plugin.table + " SET XP='"+v+"' WHERE UUID='"+uuid.toString()+"'");
                    statement.close();
                }
                catch(SQLException e)
                {
                    e.printStackTrace();
                }
                return true;
            }
            catch (Exception e)
            {
                return true;
            }
        }
        return false;
    }
}