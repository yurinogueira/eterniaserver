package center;

import center.sql.*;
import commands.player.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import net.milkbowl.vault.economy.Economy;

public class main extends JavaPlugin
{
    private static Economy econ;
    private Connection connection;
    @Override
    public void onEnable()
    {
        if (!new File(getDataFolder(), "config.yml").exists())
        {
            saveDefaultConfig();
        }
        FileConfiguration c = getConfig();
        long delay = c.getInt("intervalo") * 20;
        new looper(c).runTaskTimer(this, 20L, delay);
        if (!setupEconomy())
        {
            this.getLogger().severe("Opa, vault não encontrado :/");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        boolean mysql = c.getBoolean("mysql");
        if (mysql)
        {
            mysqlSetup();
        }
        else
        {
            sqlitesetup();
        }
        this.getServer().getPluginManager().registerEvents(new sql(), this);
        this.getServer().getPluginManager().registerEvents(new playerlistener(), this);
        Objects.requireNonNull(this.getCommand("suicide")).setExecutor(new commands.player.suicide());
        Objects.requireNonNull(this.getCommand("advice")).setExecutor(new commands.staff.advice());
        Objects.requireNonNull(this.getCommand("discord")).setExecutor(new commands.player.discord());
        Objects.requireNonNull(this.getCommand("donation")).setExecutor(new donation());
        Objects.requireNonNull(this.getCommand("rules")).setExecutor(new rules());
        Objects.requireNonNull(this.getCommand("feed")).setExecutor(new commands.staff.feed());
        Objects.requireNonNull(this.getCommand("hat")).setExecutor(new commands.player.hat());
        Objects.requireNonNull(this.getCommand("nome")).setExecutor(new commands.staff.name());
        Objects.requireNonNull(this.getCommand("fly")).setExecutor(new commands.staff.fly());
        Objects.requireNonNull(this.getCommand("goldenshovel")).setExecutor(new goldenshovel());
        Objects.requireNonNull(this.getCommand("depositlvl")).setExecutor(new depositlvl());
        Objects.requireNonNull(this.getCommand("withdrawlvl")).setExecutor(new withdrawlvl());
        Objects.requireNonNull(this.getCommand("blocks")).setExecutor(new commands.player.blocks());
        Objects.requireNonNull(this.getCommand("back")).setExecutor(new commands.player.back());
        Objects.requireNonNull(this.getCommand("spectator")).setExecutor(new commands.staff.spectator());
        Objects.requireNonNull(this.getCommand("survival")).setExecutor(new commands.staff.survival());
        Objects.requireNonNull(this.getCommand("facebook")).setExecutor(new commands.player.facebook());
        Objects.requireNonNull(this.getCommand("bottlexp")).setExecutor(new commands.player.bottlexp());
        Objects.requireNonNull(this.getCommand("colors")).setExecutor(new commands.player.colors());
        Objects.requireNonNull(this.getCommand("spawn")).setExecutor(new commands.player.spawn());
    }
    private boolean setupEconomy()
    {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null)
        {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null)
        {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }
    public final String table = "eternia";
    private void mysqlSetup()
    {
        try
        {
            synchronized (this)
            {
                if(getConnection() != null && !getConnection().isClosed())
                {
                    return;
                }
                Class.forName("java.sql.Driver");
                String host = center.looper.c.getString("host");
                int port = center.looper.c.getInt("porta");
                String password = center.looper.c.getString("senha");
                String database = center.looper.c.getString("database");
                String username = center.looper.c.getString("usuario");
                setConnection(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password));
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Conectado com sucesso a database");
                String MySQLCreate = "CREATE TABLE IF NOT EXISTS eternia (`UUID` varchar(32) NOT NULL, `NAME` varchar(32) NOT NULL, `XP` int(11) NOT NULL);";
                try
                {
                    Statement s = getConnection().createStatement();
                    s.executeUpdate(MySQLCreate);
                    s.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch(SQLException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    private void sqlitesetup()
    {
        try
        {
            synchronized (this)
            {
                if(getConnection() != null && !getConnection().isClosed())
                {
                    return;
                }
                String dbname = getConfig().getString("SQLite.Filename", "eternia");
                File dataFolder = new File(getDataFolder(), dbname+".db");
                if (!dataFolder.exists())
                {
                    try
                    {
                        dataFolder.createNewFile();
                    }
                    catch (IOException e)
                    {
                        getLogger().severe( "Erro ao escrever arquivo: "+dbname+".db");
                    }
                }
                Class.forName("org.sqlite.JDBC");
                setConnection(DriverManager.getConnection("jdbc:sqlite:" + dataFolder));
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Conectado com sucesso a database");
                String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS eternia (`UUID` varchar(255) NOT NULL, `NAME` varchar(32) NOT NULL, `XP` int(11) NOT NULL);";
                try
                {
                    Statement s = getConnection().createStatement();
                    s.executeUpdate(SQLiteCreateTokensTable);
                    s.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch(SQLException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    public Connection getConnection()
    {
        return connection;
    }
    private void setConnection(Connection connection)
    {
        this.connection = connection;
    }
    public static Economy getEconomy()
    {
        return econ;
    }
}