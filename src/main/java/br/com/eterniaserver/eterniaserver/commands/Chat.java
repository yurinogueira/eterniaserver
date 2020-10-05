package br.com.eterniaserver.eterniaserver.commands;

import br.com.eterniaserver.acf.annotation.CommandAlias;
import br.com.eterniaserver.acf.annotation.CommandPermission;
import br.com.eterniaserver.acf.annotation.Optional;
import br.com.eterniaserver.acf.annotation.Syntax;
import br.com.eterniaserver.acf.annotation.CommandCompletion;
import br.com.eterniaserver.eternialib.UUIDFetcher;
import br.com.eterniaserver.acf.BaseCommand;
import br.com.eterniaserver.acf.bukkit.contexts.OnlinePlayer;
import br.com.eterniaserver.eterniaserver.EterniaServer;
import br.com.eterniaserver.eterniaserver.core.APIChat;
import br.com.eterniaserver.eterniaserver.core.APIPlayer;
import br.com.eterniaserver.eterniaserver.core.APIServer;
import br.com.eterniaserver.eterniaserver.enums.Messages;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class Chat extends BaseCommand {

    private final EterniaServer plugin;

    public Chat(EterniaServer plugin) {
        this.plugin = plugin;
    }

    @CommandAlias("limparchat|chatclear|clearchat")
    @CommandPermission("eternia.clearchat")
    public void onClearChat() {
        for (int i = 0; i < 150; i ++) Bukkit.broadcastMessage("");
    }

    @CommandAlias("broadcast|advice|aviso")
    @CommandPermission("eternia.advice")
    public void onBroadcast(String message) {
        Bukkit.broadcastMessage(EterniaServer.msg.getMessage(Messages.CHAT_BROADCAST, true, APIServer.getColor(message)));
    }

    @CommandAlias("vanish|chupadadimensional")
    @CommandPermission("eternia.vanish")
    public void onVanish(Player player) {
        Bukkit.broadcastMessage(EterniaServer.msg.getMessage(Messages.SERVER_LOGOUT, true, player.getName(), player.getDisplayName()));
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.hidePlayer(plugin, player);
        }
    }

    @CommandAlias("unvanish|chupadadimensionalreversa")
    @CommandPermission("eternia.vanish")
    public void onUnVanish(Player player) {
        Bukkit.broadcastMessage(EterniaServer.msg.getMessage(Messages.SERVER_LOGIN, true, player.getName(), player.getDisplayName()));
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(plugin, player);
        }
    }

    @CommandAlias("ignore")
    @CommandPermission("eternia.ignore")
    public void onIgnore(Player player, OnlinePlayer targetOnline) {
        final Player target = targetOnline.getPlayer();
        if (target != null && target.isOnline()) {
            final String targetName = target.getName();
            List<Player> ignoreds;
            if (!APIChat.hasIgnores(targetName)) {
                ignoreds = new ArrayList<>();
            } else {
                ignoreds = APIChat.getIgnores(targetName);
                if (ignoreds.contains(player)) {
                    EterniaServer.msg.sendMessage(player, Messages.CHAT_UNIGNORE, targetName, target.getDisplayName());
                    ignoreds.remove(player);
                    return;
                }
            }
            ignoreds.add(player);
            APIChat.putIgnored(targetName, ignoreds);
            EterniaServer.msg.sendMessage(player, Messages.CHAT_IGNORE, targetName, target.getDisplayName());
        }
    }

    @CommandAlias("spy|socialspy")
    @CommandPermission("eternia.spy")
    public void onSpy(Player player) {
        final String playerName = player.getName();
        if (APIChat.isSpying(playerName)) {
            APIChat.disableSpy(playerName);
            EterniaServer.msg.sendMessage(player, Messages.CHAT_SPY_DISABLED);
        } else {
            APIChat.putSpy(playerName);
            EterniaServer.msg.sendMessage(player, Messages.CHAT_SPY_ENABLED);
        }
    }

    @CommandAlias("resp|r|w|reply")
    @Syntax("<mensagem>")
    @CommandPermission("eternia.tell")
    public void onResp(Player sender, String msg) {
        if (isMuted(sender)) return;

        final String playerName = sender.getName();
        if (APIChat.receivedTell(playerName) && msg != null) {
            final Player target = Bukkit.getPlayer(APIChat.getTellSender(playerName));
            if (target != null && target.isOnline()) {
                if (APIChat.hasIgnores(playerName) && APIChat.areIgnored(playerName, target)) {
                    EterniaServer.msg.sendMessage(sender, Messages.CHAT_ARE_IGNORED);
                    return;
                }
                APIChat.sendPrivate(sender, target, msg);
                return;
            }
        }
        EterniaServer.msg.sendMessage(sender, Messages.CHAT_NO_ONE_TO_RESP);
    }

    @CommandAlias("tell|msg|whisper|emsg")
    @Syntax("<jogador> <mensagem>")
    @CommandCompletion("@players Oi.")
    @CommandPermission("eternia.tell")
    public void onTell(Player player, @Optional OnlinePlayer targets, @Optional String msg) {
        if (isMuted(player)) return;

        final String playerName = player.getName();
        final UUID uuid = UUIDFetcher.getUUIDOf(playerName);

        if (targets == null) {
            APIChat.setChannel(uuid, 0);
            EterniaServer.msg.sendMessage(player, Messages.CHAT_CHANNEL_CHANGED, EterniaServer.configs.chLocal);
            return;
        }

        if (APIChat.isTell(playerName)) {
            APIChat.removeTelling(playerName);
            EterniaServer.msg.sendMessage(player, Messages.CHAT_TELL_UNLOCKED, targets.getPlayer().getName(), targets.getPlayer().getDisplayName());
            return;
        }

        final Player target = targets.getPlayer();

        if (msg == null || msg.length() == 0) {
            APIChat.setTelling(playerName, target.getName());
            APIChat.setChannel(uuid, 3);
            EterniaServer.msg.sendMessage(player, Messages.CHAT_TELL_LOCKED, targets.getPlayer().getName(), targets.getPlayer().getDisplayName());
            return;
        }

        if (APIChat.hasIgnores(playerName) && APIChat.areIgnored(player.getName(), target)) {
            EterniaServer.msg.sendMessage(player, Messages.CHAT_ARE_IGNORED);
            return;
        }

        APIChat.sendPrivate(player, target, msg);
    }

    private boolean isMuted(Player player) {
        final UUID uuid = UUIDFetcher.getUUIDOf(player.getName());
        final long time = APIPlayer.getMutedTime(uuid);
        if (APIServer.isInFutureCooldown(time)) {
            EterniaServer.msg.sendMessage(player, Messages.CHAT_ARE_MUTED, APIServer.getTimeLeftOfCooldown(time));
            return true;
        }
        return false;
    }

}
