package eternia.commands.others;

import eternia.configs.MVar;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Hat implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("eternia.hat")) {
                ItemStack capacete = player.getInventory().getHelmet();
                if (capacete != null) {
                    player.getWorld().dropItem(player.getLocation().add(0, 1, 0), capacete);
                }
                set_capacete(player);
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                MVar.playerMessage("other.helmet", player);
            } else {
                MVar.playerMessage("server.no-perm", player);
            }
        } else {
            MVar.consoleMessage("server.only-player");
        }
        return true;
    }

    private void set_capacete(Player player) {
        player.getInventory().setHelmet(player.getInventory().getItemInMainHand());
    }
}