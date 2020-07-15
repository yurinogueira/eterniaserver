package br.com.eterniaserver.eterniaserver.generics;

import br.com.eterniaserver.eternialib.EQueries;
import br.com.eterniaserver.eterniaserver.EterniaServer;

public class APIExperience {

    /**
     * Gets experience of a player on a database.
     * @param playerName to check
     * @return Amount currently held in player's database
     */
    public static Integer getExp(String playerName) {
        if (Vars.xp.containsKey(playerName)) {
            return Vars.xp.get(playerName);
        }

        final String querie = "SELECT xp FROM " + EterniaServer.serverConfig.getString("sql.table-xp") + " WHERE player_name='" + playerName + "';";
        return EQueries.queryInteger(querie, "xp");
    }

    /**
     * Defines the amount experience in player's database.
     * @param playerName to check
     * @param amount to set
     */
    public static void setExp(String playerName, int amount) {
        Vars.xp.put(playerName, amount);
        EQueries.executeQuery("UPDATE " + EterniaServer.serverConfig.getString("sql.table-xp") + " SET xp='" + amount + "' WHERE player_name='" + playerName + "';");
    }

    /**
     * Adds experience of player's database.
     * @param playerName to check
     * @param amount to add
     */
    public static void addExp(String playerName, int amount) {
        setExp(playerName, getExp(playerName) + amount);
    }

    /**
     * Removes experience of player's database.
     * @param playerName to check
     * @param amount to remove
     */
    public static void removeExp(String playerName, int amount) {
        setExp(playerName, getExp(playerName) - amount);
    }

}