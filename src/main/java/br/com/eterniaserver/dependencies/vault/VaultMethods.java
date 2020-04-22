package br.com.eterniaserver.dependencies.vault;

import br.com.eterniaserver.API.Money;
import br.com.eterniaserver.EterniaServer;
import br.com.eterniaserver.player.PlayerManager;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class VaultMethods implements Economy {

    private static final DecimalFormat df2 = new DecimalFormat(".##");

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "EterniaServer";
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 2;
    }

    @Override
    public String format(double amount) {
        return df2.format(amount);
    }

    @Override
    public String currencyNamePlural() {
        return EterniaServer.configs.getString("money.plural");
    }

    @Override
    public String currencyNameSingular() {
        return EterniaServer.configs.getString("money.singular");
    }

    @Override
    public boolean hasAccount(String playerName) {
        return PlayerManager.playerXPExist(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return PlayerManager.playerXPExist(player.getName());
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(String playerName) {
        return Money.getMoney(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return Money.getMoney(player.getName());
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(String playerName, double amount) {
        return Money.hasMoney(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return Money.hasMoney(player.getName(), amount);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        if (has(playerName, amount)) {
            Money.removeMoney(playerName, amount);
            return new EconomyResponse(amount, Money.getMoney(playerName), EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(amount, Money.getMoney(playerName), EconomyResponse.ResponseType.FAILURE, null);
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (has(player, amount)) {
            Money.removeMoney(player.getName(), amount);
            return new EconomyResponse(amount, Money.getMoney(player.getName()), EconomyResponse.ResponseType.SUCCESS, null);
        } else {
            return new EconomyResponse(amount, Money.getMoney(player.getName()), EconomyResponse.ResponseType.FAILURE, null);
        }
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return  withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        Money.addMoney(playerName, amount);
        return new EconomyResponse(amount, Money.getMoney(playerName), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        Money.addMoney(player.getName(), amount);
        return new EconomyResponse(amount, Money.getMoney(player.getName()), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public EconomyResponse createBank(String playerName, String paramString2) {
        return createUnsupportedResponse();
    }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return createUnsupportedResponse();
    }

    @Override
    public EconomyResponse deleteBank(String paramString) {
        return createUnsupportedResponse();
    }

    @Override
    public EconomyResponse bankBalance(String paramString) {
        return createUnsupportedResponse();
    }

    @Override
    public EconomyResponse bankHas(String paramString, double paramDouble) {
        return createUnsupportedResponse();
    }

    @Override
    public EconomyResponse bankWithdraw(String paramString, double paramDouble) {
        return createUnsupportedResponse();
    }

    @Override
    public EconomyResponse bankDeposit(String paramString, double paramDouble) {
        return createUnsupportedResponse();
    }

    @Override
    public EconomyResponse isBankOwner(String paramString1, String paramString2) {
        return createUnsupportedResponse();
    }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return createUnsupportedResponse();
    }

    @Override
    public EconomyResponse isBankMember(String paramString1, String paramString2) {
        return createUnsupportedResponse();
    }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return createUnsupportedResponse();
    }

    EconomyResponse createUnsupportedResponse() {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Sem suporte para bancos");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        if (this.hasAccount(playerName)) {
            return false;
        }
        PlayerManager.playerXPCreate(playerName);
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        if (this.hasAccount(player)) {
            return false;
        }
        PlayerManager.playerXPCreate(player.getName());
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return  createPlayerAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return createPlayerAccount(player);
    }

}