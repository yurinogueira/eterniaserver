package br.com.eterniaserver.eterniaserver.modules.economymanager.commands;

import br.com.eterniaserver.eterniaserver.API.Money;
import br.com.eterniaserver.eterniaserver.configs.Messages;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import org.bukkit.command.CommandSender;

@CommandAlias("eco")
@CommandPermission("eternia.eco")
public class EcoChange extends BaseCommand {

    private final Messages messages;
    private final Money moneyx;

    public EcoChange(Messages messages, Money moneyx) {
        this.messages = messages;
        this.moneyx = moneyx;
    }

    @Subcommand("set|definir")
    @CommandCompletion("@players 100")
    @Syntax("<jogador> <quantia>")
    public void onSet(CommandSender sender, OnlinePlayer target, Double money) {
        moneyx.setMoney(target.getPlayer().getName(), money);
        messages.sendMessage("eco.eco-set", "%money%", money, "%target_name%", target.getPlayer().getName(), sender);
        messages.sendMessage("eco.eco-rset", "%money%", money, "%target_name%", sender.getName(), target.getPlayer());
    }

    @Subcommand("remove|remover")
    @CommandCompletion("@players 100")
    @Syntax("<jogador> <quantia>")
    public void onRemove(CommandSender sender, OnlinePlayer target, Double money) {
        moneyx.removeMoney(target.getPlayer().getName(), money);
        messages.sendMessage("eco.eco-remove", "%money%" ,money, "%target_name%", target.getPlayer().getName(), sender);
        messages.sendMessage("eco.eco-rremove", "%money%", money, "%target_name%", sender.getName(), target.getPlayer());
    }

    @Subcommand("give|dar")
    @CommandCompletion("@players 100")
    @Syntax("<jogador> <quantia>")
    public void onGive(CommandSender sender, OnlinePlayer target, Double money) {
        moneyx.addMoney(target.getPlayer().getName(), money);
        messages.sendMessage("eco.eco-give", "%money%", money, "%target_name%", target.getPlayer().getName(), sender);
        messages.sendMessage("eco.eco-receive", "%money%", money, "%target_name%", sender.getName(), target.getPlayer());
    }

}
