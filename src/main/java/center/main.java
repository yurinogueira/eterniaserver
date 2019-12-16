package center;

import center.sql.*;
import commands.player.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import net.milkbowl.vault.economy.Economy;

public class main extends JavaPlugin
{
    private static Economy econ;
    private Connection connection;
    @Override
    public void onEnable()
    {
        if (!setupEconomy())
        {
            this.getLogger().severe("Opa, vault não encontrado :/");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        mysqlSetup();
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
                String host = "167.114.85.28";
                int port = 3306;
                String password = "8f949c48dc";
                String database = "hm_5763";
                String username = "hm_5763";
                setConnection(DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password));
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Conectado com sucesso a database");
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