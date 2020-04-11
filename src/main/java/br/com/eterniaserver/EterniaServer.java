package br.com.eterniaserver;

import br.com.eterniaserver.events.*;
import br.com.eterniaserver.modules.*;
import br.com.eterniaserver.storages.Configs;
import br.com.eterniaserver.storages.MessagesConfig;
import br.com.eterniaserver.storages.sql.Connections;
import br.com.eterniaserver.storages.DatabaseType;
import br.com.eterniaserver.vault.VaultHook;

import br.com.eterniaserver.vault.VaultUnHook;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class EterniaServer extends JavaPlugin {

    public static FileConfiguration blocks;
    public static FileConfiguration configs;
    public static FileConfiguration messages;

    public static Connections sqlcon;

    @Override
    public void onEnable() {

        saveDefaultConfigs();
        saveDefaultMessages();
        saveDefaultBlocks();

        databaseType();

        teleportsManager();
        spawnersManager();
        antiNetherTrapManager();
        economyManager();
        experienceManager();
        genericManager();
        bedManager();
        homesManager();

        vaultHook();

        this.getServer().getPluginManager().registerEvents(new OnBlockBreak(this), this);
        this.getServer().getPluginManager().registerEvents(new OnBlockPlace(), this);
        this.getServer().getPluginManager().registerEvents(new OnChat(), this);
        this.getServer().getPluginManager().registerEvents(new OnDamage(), this);
        this.getServer().getPluginManager().registerEvents(new OnExpDrop(), this);
        this.getServer().getPluginManager().registerEvents(new OnInventoryClick(), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerInteract(this), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerLeave(), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerMove(), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerTeleport(), this);

    }

    @Override
    public void onDisable() {

        vaultUnHook();

    }

    private void vaultUnHook() {
        new VaultUnHook();
    }

    private void vaultHook() {
        new VaultHook(this);
    }

    private void homesManager() {
        new HomesManager(this);
    }

    private void bedManager() {
        new BedManager(this);
    }

    private void genericManager() {
        new GenericManager(this);
    }

    private void experienceManager() {
        new ExperienceManager(this);
    }

    private void economyManager() {
        new EconomyManager(this);
    }

    private void antiNetherTrapManager() {
        new AntiNetherTrapManager(this);
    }

    private void spawnersManager() {
        new SpawnersManager(this);
    }

    private void teleportsManager() {
        new TeleportsManager(this);
    }

    private void databaseType() {
        new DatabaseType(this);
    }

    private void saveDefaultBlocks(){
        new BlockRewardManager(this);
    }

    private void saveDefaultMessages() {
        new MessagesConfig(this);
    }

    private void saveDefaultConfigs() {
        new Configs(this);
    }

}