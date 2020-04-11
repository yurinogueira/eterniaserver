package br.com.eterniaserver.storages.sql;

import br.com.eterniaserver.EterniaServer;

public class Tables {

    public Tables() {
        String createTable = "CREATE TABLE IF NOT EXISTS " + EterniaServer.configs.getString("sql.table-xp") + " (player_name varchar(32), xp int(11));";
        EterniaServer.sqlcon.Update(createTable);
        createTable = "CREATE TABLE IF NOT EXISTS " + EterniaServer.configs.getString("sql.table-money") + " (player_name varchar(32), balance double(22));";
        EterniaServer.sqlcon.Update(createTable);
        createTable = "CREATE TABLE IF NOT EXISTS " + EterniaServer.configs.getString("sql.table-warp") + " (name varchar(16), location varchar(128));";
        EterniaServer.sqlcon.Update(createTable);
        createTable = "CREATE TABLE IF NOT EXISTS " + EterniaServer.configs.getString("sql.table-shop") + " (name varchar(16), location varchar(128));";
        EterniaServer.sqlcon.Update(createTable);
        createTable = "CREATE TABLE IF NOT EXISTS " + EterniaServer.configs.getString("sql.table-home") + " (name varchar(16), many int(6), homes varchar(255));";
        EterniaServer.sqlcon.Update(createTable);
        createTable = "CREATE TABLE IF NOT EXISTS " + EterniaServer.configs.getString("sql.table-homes") + " (name varchar(32), location varchar(128));";
        EterniaServer.sqlcon.Update(createTable);
    }

}
