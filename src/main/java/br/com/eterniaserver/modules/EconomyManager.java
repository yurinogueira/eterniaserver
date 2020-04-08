package br.com.eterniaserver.modules;

import br.com.eterniaserver.EterniaServer;
import br.com.eterniaserver.configs.CVar;
import br.com.eterniaserver.configs.methods.ConsoleMessage;
import br.com.eterniaserver.modules.economymanager.commands.*;

import java.util.Objects;

public class EconomyManager {

    public EconomyManager(EterniaServer plugin) {
        if (CVar.getBool("modules.economy")) {
            Objects.requireNonNull(plugin.getCommand("pay")).setExecutor(new Pay());
            Objects.requireNonNull(plugin.getCommand("money")).setExecutor(new Money());
            Objects.requireNonNull(plugin.getCommand("baltop")).setExecutor(new Baltop());
            Objects.requireNonNull(plugin.getCommand("eco")).setExecutor(new Eco());
            new ConsoleMessage("modules.enable", "Economy");
        } else {
            new ConsoleMessage("modules.disable", "Economy");
        }
    }

}
