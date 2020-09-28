package br.com.eterniaserver.eterniaserver.core;

public class PluginConstants {

    private PluginConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String MESSAGE = "%message%";
    public static final String PRIMARY_KEY = "(id INT AUTO_INCREMENT NOT NULL PRIMARY KEY, ";
    public static final String NAME_STR = "name";
    public static final String LOCATION_STR = "location";
    public static final String PLAYER_NAME_STR = "player_name";
    public static final String UUID_STR = "uuid";
    public static final String PLAYER_DISPLAY_STR = "player_display";
    public static final String BALANCE_STR = "balance";
    public static final String CODE_STR = "code";
    public static final String CODE_GROUP_STR = "group_name";
    public static final String TIME_STR = "time";
    public static final String COOLDOWN_STR = "cooldown";
    public static final String XP_STR = "xp";
    public static final String HOMES_STR = "homes";
    public static final String CLEAR_STR = "clear";
    public static final String LAST_STR = "last";
    public static final String CASH_STR = "cash";
    public static final String MUTED_STR = "muted";
    public static final String HOURS_STR = "hours";

    public static String getQueryCreateTable(final String table, final String values) {
        return "CREATE TABLE IF NOT EXISTS " + table + " " + values + ";";
    }

    public static String getQuerySelectAll(final String table) {
        return "SELECT * FROM " + table + ";";
    }

    public static String getQueryDelete(final String table, final String type, final String value) {
        return "DELETE FROM " + table + " WHERE " + type + "='" + value + "';";
    }

    public static String getQueryUpdate(final String table, final String type, final Object value, final String type2, final Object value2) {
        return "UPDATE " + table + " SET " + type + "='" + value + "' WHERE " + type2 + "='" + value2 + "';";
    }

    public static String getQueryInsert(final String table, final String type, final Object value, final String type2, final Object value2) {
        return "INSERT INTO " + table + " (" + type + ", " + type2 + ") VALUES ('" + value + "', '" + value2 + "');";
    }

    public static String getQueryInsert(final String table, final String type, final Object value) {
        return "INSERT INTO " + table + " " + type + " VALUES " + value + ";";
    }

}