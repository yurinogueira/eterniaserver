package br.com.eterniaserver.eterniaserver.modules.economymanager.commands;

import br.com.eterniaserver.eterniaserver.API.Money;
import br.com.eterniaserver.eterniaserver.EterniaServer;
import br.com.eterniaserver.eterniaserver.configs.Messages;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Economy extends BaseCommand {

    private final EterniaServer plugin;
    private final Messages messages;
    private final br.com.eterniaserver.eterniaserver.API.Money moneyx;

    public Economy(EterniaServer plugin, Messages messages, Money moneyx) {
        this.plugin = plugin;
        this.messages = messages;
        this.moneyx = moneyx;
    }

    @CommandAlias("money|economy|balance|bal")
    @CommandCompletion("@players")
    @Syntax("<quantia>")
    @CommandPermission("eternia.money")
    public void onMoney(Player player, @Optional OnlinePlayer target) {
        DecimalFormat df2 = new DecimalFormat(".##");
        if (target == null) {
            double money = moneyx.getMoney(player.getName());
            messages.sendMessage("eco.money", "%money%", df2.format(money), player);
        } else {
            if (player.hasPermission("eternia.money.other")) {
                double money = moneyx.getMoney(target.getPlayer().getName());
                messages.sendMessage("eco.money-other", "%money%", df2.format(money), player);
            } else {
                messages.sendMessage("server.no-perm", player);
            }
        }
    }

    @CommandAlias("pay|pagar")
    @CommandCompletion("@players 100")
    @Syntax("<nome> <quantia>")
    @CommandPermission("eternia.pay")
    public void onPay(Player player, OnlinePlayer target, Double value) {
        if (!(target.getPlayer().equals(player))) {
            if (moneyx.getMoney(player.getName()) >= value) {
                moneyx.addMoney(target.getPlayer().getName(), value);
                moneyx.removeMoney(player.getName(), value);
                messages.sendMessage("eco.pay", "%amount%", value, "%target_name%", target.getPlayer().getName(), player);
                messages.sendMessage("eco.pay-me", "%amount%", value, "%target_name%", player.getName(), target.getPlayer());
            } else {
                messages.sendMessage("eco.pay-nomoney", player);
            }
        } else {
            messages.sendMessage("eco.auto-pay", player);
        }
    }

    @CommandAlias("baltop|balancetop|moneytop")
    @CommandPermission("eternia.baltop")
    public void onBaltop(CommandSender sender) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final String querie = "SELECT * FROM " + plugin.serverConfig.getString("sql.table-money") + " ORDER BY balance DESC LIMIT " + 10 + ";";
            List<String> accounts = new ArrayList<>();
            plugin.connections.executeSQLQuery(connection -> {
                PreparedStatement getbaltop = connection.prepareStatement(querie);
                ResultSet resultSet = getbaltop.executeQuery();
                while (resultSet.next()) {
                    final String warpname = resultSet.getString("player_name");
                    accounts.add(warpname);
                }
                resultSet.close();
                getbaltop.close();
            });
            DecimalFormat df2 = new DecimalFormat(".##");
            messages.sendMessage("eco.baltop", sender);
            accounts.forEach(name -> messages.sendMessage("eco.ballist", "%position%", (accounts.indexOf(name) + 1), "%player_name%", name, "%money%", df2.format(moneyx.getMoney(name)), sender));
        });
    }

}
