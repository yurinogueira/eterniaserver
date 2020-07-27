package br.com.eterniaserver.eterniaserver.dependencies.eternialib;

import br.com.eterniaserver.eternialib.EFiles;
import br.com.eterniaserver.eterniaserver.Constants;
import br.com.eterniaserver.eterniaserver.EterniaServer;

import br.com.eterniaserver.eterniaserver.Strings;
import org.bukkit.configuration.InvalidConfigurationException;
import java.io.IOException;

public class Files {

    private final EterniaServer plugin;
    private final EFiles messages;

    public Files(EterniaServer plugin) {
        this.plugin = plugin;
        this.messages = plugin.getEFiles();
    }

    public void loadConfigs() {

        try {
            EterniaServer.serverConfig.load(EFiles.fileLoad(plugin, "config.yml"));

            EFiles.fileLoad(plugin, "acf_messages.yml");
        } catch (IOException | InvalidConfigurationException e) {
            messages.sendConsole(Strings.M_ERROR, Constants.ERROR, "problema para encontrar acf_messages.yml dentro da jar");
        }

    }

    public void loadMessages() {

        try {
            EterniaServer.msgConfig.load(EFiles.fileLoad(plugin, "messages.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            messages.sendConsole(Strings.M_ERROR, Constants.ERROR, "arquivo de mensagens faltando | messages.yml");
        }

    }

    public void loadChat() {

        try {
            EterniaServer.chatConfig.load(EFiles.fileLoad(plugin, "chat.yml"));
            EterniaServer.groupConfig.load(EFiles.fileLoad(plugin, "groups.yml"));
            EterniaServer.placeholderConfig.load(EFiles.fileLoad(plugin, "customplaceholders.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            messages.sendConsole(Strings.M_ERROR, Constants.ERROR, "placeholders do chat faltando | customplaceholders.yml");
        }

    }

    public void loadRewards() {

        try {
            EterniaServer.rewardsConfig.load(EFiles.fileLoad(plugin, "rewards.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            messages.sendConsole(Strings.M_ERROR, Constants.ERROR, "configurações de rewards faltando | rewards.yml");
        }

    }

    public void loadKits() {

        try {
            EterniaServer.kitConfig.load(EFiles.fileLoad(plugin, "kits.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            messages.sendConsole(Strings.M_ERROR, Constants.ERROR, "lista de kits faltando | kits.yml");
        }

    }

    public void loadCommands() {

        try {
            EterniaServer.cmdConfig.load(EFiles.fileLoad(plugin, "commands.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            messages.sendConsole(Strings.M_ERROR, Constants.ERROR, "comandos personalizados faltando | commands.yml");
        }

    }

    public void loadBlocksRewards() {

        try {
            EterniaServer.blockConfig.load(EFiles.fileLoad(plugin, "blocks.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            messages.sendConsole(Strings.M_ERROR, Constants.ERROR, "problemas para encontrar a configuração dos blocos | blocks.yml");
        }

    }

    public void loadCashGui() {

        try {
            EterniaServer.cashConfig.load(EFiles.fileLoad(plugin, "cashgui.yml"));
        } catch (IOException | InvalidConfigurationException e) {
            messages.sendConsole(Strings.M_ERROR, Constants.ERROR, "sistema de cash não configurado | cashgui.yml");
        }

    }

    public void loadDatabase() {

        new Table();

    }

}
