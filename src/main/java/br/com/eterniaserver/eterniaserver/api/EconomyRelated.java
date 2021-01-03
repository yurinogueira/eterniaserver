package br.com.eterniaserver.eterniaserver.api;

import br.com.eterniaserver.eternialib.SQL;
import br.com.eterniaserver.eternialib.UUIDFetcher;
import br.com.eterniaserver.eternialib.sql.queries.Update;
import br.com.eterniaserver.eterniaserver.EterniaServer;
import br.com.eterniaserver.eterniaserver.objects.BalanceTop;
import br.com.eterniaserver.eterniaserver.enums.Booleans;
import br.com.eterniaserver.eterniaserver.enums.Doubles;
import br.com.eterniaserver.eterniaserver.enums.Lists;
import br.com.eterniaserver.eterniaserver.enums.Strings;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class EconomyRelated {

    private static Economy economy;
    private static NumberFormat numberFormat;
    private static UUID baltop;
    private static long baltopTime = 0;
    private static final List<UUID> baltopList = new ArrayList<>();

    private EconomyRelated() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Get the number format of plugin
     * @return the NumberFormat
     */
    public static NumberFormat getNumberFormat() {
        if (numberFormat == null) {
            numberFormat = NumberFormat.getInstance(new Locale(EterniaServer.getString(Strings.MONEY_LANGUAGE), EterniaServer.getString(Strings.MONEY_COUNTRY)));
        }
        return numberFormat;
    }

    /**
     * Set the economy interface of plugin
     * @param econ interface
     */
    public static void setEconomy(Economy econ) {
        economy = econ;
    }

    /**
     * Get the amount formated
     * @param amount to format
     * @return the formatted string value
     */
    public static String format(double amount) {
        if (EterniaServer.getBoolean(Booleans.MODULE_ECONOMY)) {
            return getNumberFormat().format(amount);
        }
        return economy.format(amount);
    }

    /**
     * Get the singular name of current
     * @return the name
     */
    public static String singularName() {
        if (EterniaServer.getBoolean(Booleans.MODULE_ECONOMY)) {
            return EterniaServer.getString(Strings.MONEY_SINGULAR);
        } else {
            return economy.currencyNameSingular();
        }
    }

    /**
     * Get the plural name of current
     * @return the name
     */
    public static String pluralName() {
        if (EterniaServer.getBoolean(Booleans.MODULE_ECONOMY)) {
            return EterniaServer.getString(Strings.MONEY_PLURAL);
        } else {
            return economy.currencyNamePlural();
        }
    }

    /**
     * Check if player has account
     * @param uuid uuid of player
     */
    public static boolean hasAccount(UUID uuid) {
        if (EterniaServer.getBoolean(Booleans.MODULE_ECONOMY)) {
            return PlayerRelated.hasProfile(uuid);
        } else {
            return economy.hasAccount(Bukkit.getOfflinePlayer(uuid));
        }
    }

    /**
     * Create a account for the player
     * @param uuid uuid of player
     */
    public static void createAccount(UUID uuid) {
        if (EterniaServer.getBoolean(Booleans.MODULE_ECONOMY)) {
            final String playerName = UUIDFetcher.getNameOf(uuid);
            PlayerRelated.createProfile(uuid, playerName);
        } else {
            economy.createPlayerAccount(Bukkit.getOfflinePlayer(uuid));
        }
    }

    /**
     * Get the balance of the player
     * @param uuid of player
     * @return the balance
     */
    public static double getMoney(UUID uuid) {
        if (EterniaServer.getBoolean(Booleans.MODULE_ECONOMY)) {
            if (PlayerRelated.hasProfile(uuid)) {
                return PlayerRelated.getProfile(uuid).getBalance();
            } else {
                PlayerRelated.createProfile(uuid, UUIDFetcher.getNameOf(uuid));
                return EterniaServer.getDouble(Doubles.START_MONEY);
            }
        } else {
            return economy.getBalance(Bukkit.getOfflinePlayer(uuid));
        }
    }

    /**
     * Check if the player has money enough
     * @param uuid of player
     * @param amount the amount of money needed
     * @return if has or not
     */
    public static boolean hasMoney(UUID uuid, double amount) {
        if (EterniaServer.getBoolean(Booleans.MODULE_ECONOMY)) {
            return getMoney(uuid) >= amount;
        } else {
            return economy.has(Bukkit.getOfflinePlayer(uuid), amount);
        }
    }

    /**
     * Defines the balance of the player
     * @param uuid of player
     * @param amount the amount of money to set
     */
    public static void setMoney(UUID uuid, double amount) {
        if (EterniaServer.getBoolean(Booleans.MODULE_ECONOMY)) {
            if (PlayerRelated.hasProfile(uuid)) {
                PlayerRelated.getProfile(uuid).setBalance(amount);

                Update update = new Update(EterniaServer.getString(Strings.TABLE_PLAYER));
                update.set.set("balance", amount);
                update.where.set("uuid", uuid.toString());
                SQL.executeAsync(update);
            } else {
                PlayerRelated.createProfile(uuid, UUIDFetcher.getNameOf(uuid));
                setMoney(uuid, amount);
            }
        } else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            economy.withdrawPlayer(offlinePlayer, economy.getBalance(offlinePlayer));
            economy.depositPlayer(offlinePlayer, amount);
        }
    }

    /**
     * Give money to player account
     * @param uuid of player
     * @param amount the amount of money to give
     */
    public static void addMoney(UUID uuid, double amount) {
        if (EterniaServer.getBoolean(Booleans.MODULE_ECONOMY)) {
            setMoney(uuid, getMoney(uuid) + amount);
        } else {
            economy.depositPlayer(Bukkit.getOfflinePlayer(uuid), amount);
        }
    }

    /**
     * Give money to player account
     * @param uuid of player
     * @param amount the amount of money to give
     */
    public static void removeMoney(UUID uuid, double amount) {
        if (EterniaServer.getBoolean(Booleans.MODULE_ECONOMY)) {
            setMoney(uuid, getMoney(uuid) - amount);
        } else {
            economy.withdrawPlayer(Bukkit.getOfflinePlayer(uuid), amount);
        }
    }

    /**
     * Check if the player is the top money
     * @param uuid of player
     * @return if the player was top money or not
     */
    public static boolean isBalanceTop(UUID uuid) {
        if (baltop == null) {
            updateBalanceTop(20);
        }
        return baltop.equals(uuid);
    }

    /**
     * Update the baltop list and return And
     * return if the list was updated or not
     * @param size the size of list
     * @return if the list was updated or not
     */
    public static CompletableFuture<Boolean> updateBalanceTop(int size) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection connection = SQL.getConnection()) {
                PreparedStatement statement = connection.prepareStatement(new BalanceTop(EterniaServer.getString(Strings.TABLE_PLAYER), size).queryString());
                statement.execute();
                ResultSet resultSet = statement.getResultSet();
                final List<UUID> tempList = new ArrayList<>();
                UUID uuid;
                while (resultSet.next()) {
                    if (tempList.size() < size) {
                        uuid = UUID.fromString(resultSet.getString("uuid"));
                        if (!EterniaServer.getStringList(Lists.BLACKLISTED_BALANCE_TOP).contains(UUIDFetcher.getNameOf(uuid))) {
                            tempList.add(uuid);
                        }
                    }
                }
                baltopTime = System.currentTimeMillis();
                baltopList.clear();
                baltopList.addAll(tempList);
                baltop = baltopList.get(0);
                resultSet.close();
                statement.close();
            } catch (SQLException ignored) {
                ServerRelated.logError("Erro ao se conectar com a database", 3);
                return false;
            }
            return true;
        });
    }

    /**
     * Get the baltop list
     * @return the baltop list
     */
    public static List<UUID> getBalanceTop() {
        return baltopList;
    }

    /**
     * Get the time from the last update
     * @return the time in long
     */
    public static long getBalanceTopTime() {
        return baltopTime;
    }

}