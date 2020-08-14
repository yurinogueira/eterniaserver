package br.com.eterniaserver.eterniaserver.generics;

import br.com.eterniaserver.eternialib.EQueries;
import br.com.eterniaserver.eterniaserver.configs.Configs;
import br.com.eterniaserver.eterniaserver.configs.Constants;

import java.util.UUID;

public class APICash implements Constants {

    private APICash() {
        throw new IllegalStateException("Utility class");
    }

    public static int getCash(UUID uuid) {
        if (Vars.cash.containsKey(uuid)) {
            return Vars.cash.get(uuid);
        } else {
            EQueries.executeQuery(Constants.getQueryInsert(Configs.TABLE_CASH, UUID_STR, uuid.toString(), BALANCE_STR, 0));
            Vars.cash.put(uuid, 0);
            return 0;
        }
    }

    public static boolean hasCash(UUID uuid, int amount) {
        return getCash(uuid) >= amount;
    }

    public static void setCash(UUID uuid, int amount) {
        if (Vars.cash.containsKey(uuid)) {
            Vars.cash.put(uuid, amount);
            EQueries.executeQuery(Constants.getQueryUpdate(Configs.TABLE_CASH, BALANCE_STR, amount, UUID_STR, uuid.toString()));
        } else {
            EQueries.executeQuery(Constants.getQueryInsert(Configs.TABLE_CASH, UUID_STR, uuid.toString(), BALANCE_STR, 0));
            Vars.cash.put(uuid, 0);
            setCash(uuid, amount);
        }
    }

    public static void addCash(UUID uuid, int amount) {
        setCash(uuid, getCash(uuid) + amount);
    }

    public static void removeCash(UUID uuid, int amount) {
        setCash(uuid, getCash(uuid) - amount);
    }

}
