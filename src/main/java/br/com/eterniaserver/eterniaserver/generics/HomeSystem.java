package br.com.eterniaserver.eterniaserver.generics;

import br.com.eterniaserver.eternialib.EFiles;
import br.com.eterniaserver.eternialib.EQueries;
import br.com.eterniaserver.eterniaserver.Constants;
import br.com.eterniaserver.eterniaserver.EterniaServer;
import br.com.eterniaserver.eterniaserver.Strings;
import br.com.eterniaserver.eterniaserver.utils.PlayerTeleport;

import br.com.eterniaserver.acf.BaseCommand;
import br.com.eterniaserver.acf.annotation.*;
import br.com.eterniaserver.acf.bukkit.contexts.OnlinePlayer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;

public class HomeSystem extends BaseCommand {

    private final EterniaServer plugin;
    private final EFiles messages;

    public HomeSystem(EterniaServer plugin) {
        this.plugin = plugin;
        this.messages = plugin.getEFiles();

        HashMap<String, String> temp = EQueries.getMapString(Constants.getQuerySelectAll(Constants.TABLE_HOMES), Strings.NAME, Strings.LOC);
        temp.forEach((k, v) -> {
            final String[] split = v.split(":");
            final Location loc = new Location(Bukkit.getWorld(split[0]),
                    Double.parseDouble(split[1]),
                    (Double.parseDouble(split[2]) + 1),
                    Double.parseDouble(split[3]),
                    Float.parseFloat(split[4]),
                    Float.parseFloat(split[5]));
            Vars.homes.put(k, loc);
        });
        messages.sendConsole(Strings.M_LOAD_DATA, Constants.MODULE, "Home", Constants.AMOUNT, temp.size());

        temp = EQueries.getMapString(Constants.getQuerySelectAll(Constants.TABLE_HOME), Strings.PNAME, Strings.HOMES);
        temp.forEach((k, v) -> {
            final String[] homess = v.split(":");
            Vars.home.put(k, homess);
        });
        messages.sendConsole(Strings.M_LOAD_DATA, Constants.MODULE, "PlayerHomes", Constants.AMOUNT, temp.size());

    }

    @CommandAlias("delhome|delhouse|delcasa")
    @Syntax("<home>")
    @CommandPermission("eternia.delhome")
    public void onDelHome(Player player, String nome) {
        final String playerName = player.getName();
        if (existHome(nome.toLowerCase(), playerName)) {
            delHome(nome.toLowerCase(), playerName);
            messages.sendMessage(Strings.M_HOME_DELETED, player);
        } else {
            messages.sendMessage(Strings.M_HOME_NO, player);
        }
    }

    @CommandAlias("home|house|casa|h")
    @Syntax("<home> <jogador>")
    @CommandPermission("eternia.home")
    public void onHome(Player player, String nome, @Optional OnlinePlayer target) {
        if (target == null) {
            Location location = getHome(nome.toLowerCase(), player.getName());
            if (locationExists(location, player) && !Vars.teleports.containsKey(player)) {
                Vars.teleports.put(player, new PlayerTeleport(player, location, Strings.M_HOME_DONE));
            } else if (Vars.teleports.containsKey(player)) {
                messages.sendMessage(Strings.M_TELEP, player);
            }
        } else if (player.hasPermission("eternia.home.other")) {
            Location location = getHome(nome.toLowerCase(), target.getPlayer().getName());
            if (locationExists(location, player) && !Vars.teleports.containsKey(player)) {
                Vars.teleports.put(player, new PlayerTeleport(player, location, Strings.M_HOME_DONE));
            } else if (Vars.teleports.containsKey(player)) {
                messages.sendMessage(Strings.M_TELEP, player);
            }
        } else {
            messages.sendMessage(Strings.M_NO_PERM, player);
        }
    }

    @CommandAlias("homes|houses|casas")
    @Syntax("<jogador>")
    @CommandPermission("eternia.homes")
    public void onHomes(Player player, @Optional OnlinePlayer target) {
        StringBuilder accounts = new StringBuilder();
        if (target != null) {
            if (player.hasPermission("eternia.homes.other")) {
                final String[] values = getHomes(target.getPlayer().getName());
                for (String line : values) if (!accounts.toString().equals("")) accounts.append(line).append("&8, &3");
                messages.sendMessage(Strings.M_HOME_LIST, Constants.HOMES, messages.getColor(accounts.toString()), player);
            } else {
                messages.sendMessage(Strings.M_NO_PERM, player);
            }
        } else {
            final String[] values = getHomes(player.getName());
            for (String line : values) accounts.append(line).append(plugin.colors.get(8)).append(", ").append(plugin.colors.get(3));
            messages.sendMessage(Strings.M_HOME_LIST, Constants.HOMES, messages.getColor(accounts.toString()), player);
        }
    }

    @CommandAlias("sethome|sethouse|setcasa")
    @Syntax("<nome>")
    @CommandPermission("eternia.sethome")
    public void onSetHome(Player player, String nome) {
        int i = 4;
        for (int v = 5; v <= 30; v++) if (player.hasPermission("eternia.sethome." + v)) i = v;
        final String playerName = player.getName();
        if (nome.length() <= i) {
            if (canHome(playerName) < i || (existHome(nome.toLowerCase(), playerName))) {
                setHome(player.getLocation(), nome.toLowerCase(), playerName);
                messages.sendMessage(Strings.M_HOME_CREATED, player);
            } else {
                ItemStack item = new ItemStack(Material.COMPASS);
                ItemMeta meta = item.getItemMeta();
                final Location loc = player.getLocation();
                final String saveloc = loc.getWorld().getName() + ":" + ((int) loc.getX()) + ":" + ((int) loc.getY()) +
                        ":" + ((int) loc.getZ()) + ":" + ((int) loc.getYaw()) + ":" + ((int) loc.getPitch());
                meta.setLore(Collections.singletonList(saveloc));
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&8[&e" + nome.toLowerCase() + "&8]"));
                item.setItemMeta(meta);
                PlayerInventory inventory = player.getInventory();
                inventory.addItem(item);
                messages.sendMessage(Strings.M_HOME_LIMIT, player);
            }
        } else {
            messages.sendMessage(Strings.M_HOME_EXCEEDED, player);
        }
    }

    private boolean locationExists(final Location location, final Player player) {
        if (location == plugin.error) {
            messages.sendMessage(Strings.M_HOME_NO, player);
            return false;
        }
        return true;
    }

    public void setHome(Location loc, String home, String jogador) {
        final String homeName = home + "." + jogador;
        Vars.homes.put(homeName, loc);
        boolean t = false;
        StringBuilder result = new StringBuilder();
        String[] values = getHomes(jogador);
        for (String line : values) {
            if (line.equals(home)) {
                result.append(line).append(":");
                t = true;
            } else {
                result.append(line).append(":");
            }
        }
        final String saveloc = loc.getWorld().getName() + ":" + ((int) loc.getX()) + ":" + ((int) loc.getY()) +
                ":" + ((int) loc.getZ()) + ":" + ((int) loc.getYaw()) + ":" + ((int) loc.getPitch());
        if (!t) {
            result.append(home).append(":");
            EQueries.executeQuery(Constants.getQueryUpdate(Constants.TABLE_HOME, Strings.HOMES, result, Strings.PNAME, jogador));
            values = result.toString().split(":");
            Vars.home.put(jogador, values);
            EQueries.executeQuery(Constants.getQueryInsert(Constants.TABLE_HOMES, Strings.NAME, homeName, Strings.LOC, saveloc));
        } else {
            EQueries.executeQuery(Constants.getQueryUpdate(Constants.TABLE_HOMES, Strings.LOC, saveloc, Strings.NAME, homeName));
        }
    }

    public void delHome(String home, String jogador) {
        final String homeName = home + "." + jogador;
        Vars.homes.remove(homeName);
        StringBuilder nova = new StringBuilder();
        String[] values = getHomes(jogador);
        boolean t = true;
        for (String line : values) {
            if (!line.equals(home)) {
                nova.append(line).append(":");
                t = false;
            }
        }
        final String novaString = nova.toString();
        values = novaString.split(":");
        Vars.home.put(jogador, values);
        if (t) {
            EQueries.executeQuery(Constants.getQueryUpdate(Constants.TABLE_HOME, Strings.HOMES, "", Strings.PNAME, jogador));
        } else {
            EQueries.executeQuery(Constants.getQueryUpdate(Constants.TABLE_HOME, Strings.HOMES, novaString, Strings.PNAME, jogador));
        }
        EQueries.executeQuery(Constants.getQueryDelete(Constants.TABLE_HOMES, Strings.NAME, homeName));
    }

    public Location getHome(String home, String jogador) {
        final String homeName = home + "." + jogador;
        return Vars.homes.containsKey(homeName) ? Vars.homes.get(homeName) : plugin.error;
    }

    public boolean existHome(String home, String jogador) {
        String[] homes = getHomes(jogador);
        for (String line : homes) if (line.equals(home)) return true;
        return false;
    }

    public int canHome(String jogador) {
        return getHomes(jogador) != null ? getHomes(jogador).length : 0;
    }

    public String[] getHomes(String jogador) {
        return Vars.home.get(jogador) != null ? Vars.home.get(jogador) : "".split(":");
    }

}
