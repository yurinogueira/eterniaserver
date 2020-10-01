package br.com.eterniaserver.eterniaserver.configurations;

import br.com.eterniaserver.eterniaserver.core.APIServer;
import br.com.eterniaserver.eterniaserver.Constants;
import br.com.eterniaserver.eterniaserver.objects.CashItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CashCfg {

    public final List<ItemStack> menuGui = new ArrayList<>();
    public final Map<Integer, String> guis = new HashMap<>();
    public final Map<String, Integer> guisInvert = new HashMap<>();
    public final Map<Integer, List<CashItem>> othersGui = new HashMap<>();

    public CashCfg() {

        FileConfiguration cashGui = YamlConfiguration.loadConfiguration(new File(Constants.CASHGUI_FILE_PATH));
        FileConfiguration outCash = new YamlConfiguration();

        loadDefaultValues();

        List<ItemStack> menuGuiTemp = new ArrayList<>();
        Map<Integer, String> guisTemp = new HashMap<>();
        Map<String, Integer> guisTempInvert = new HashMap<>();
        Map<Integer, List<CashItem>> othersGuiTemp = new HashMap<>();

        loadMapFromArchive(cashGui, othersGuiTemp, menuGuiTemp, guisTemp, guisTempInvert);

        if (othersGuiTemp.isEmpty()) {
            menuGuiTemp = new ArrayList<>(this.menuGui);
            guisTemp = new HashMap<>(guis);
            guisTempInvert = new HashMap<>(guisInvert);
            othersGuiTemp = new HashMap<>(othersGui);
        }

        this.menuGui.clear();
        menuGui.addAll(menuGuiTemp);
        this.guis.clear();
        guisTemp.forEach(this.guis::put);
        this.guisInvert.clear();
        guisTempInvert.forEach(this.guisInvert::put);
        othersGui.clear();
        othersGuiTemp.forEach(this.othersGui::put);

        outCash.options().header("Caso precise de ajuda acesse https://github.com/EterniaServer/EterniaServer/wiki");
        outCash.set("menu.size", menuGuiTemp.size());
        for (int i = 0; i < menuGuiTemp.size(); i++) {
            ItemStack itemStack = menuGuiTemp.get(i);
            if (!itemStack.isSimilar(getGlass())) {
                outCash.set(Constants.MENU + i + Constants.MATERIAL, itemStack.getType().name());
                outCash.set(Constants.MENU + i + Constants.NAME, itemStack.getItemMeta().getDisplayName());
                List<String> listando = itemStack.getLore();
                outCash.set(Constants.MENU + i + Constants.LORE, listando);
                String guiName = guisTemp.get(i);
                outCash.set(Constants.MENU + i + ".gui", guiName);
                List<CashItem> cashItems = othersGuiTemp.get(i);
                outCash.set(Constants.GUIS + guiName + ".size", cashItems.size());
                for (int j = 0; j < cashItems.size(); j++) {
                    CashItem cashItem = cashItems.get(j);
                    if (!cashItem.isGlass()) {
                        outCash.set(Constants.GUIS + guiName + "." + j + ".cost", cashItem.getCost());
                        outCash.set(Constants.GUIS + guiName + "." + j + Constants.MATERIAL, cashItem.getItemStack().getType().name());
                        outCash.set(Constants.GUIS + guiName + "." + j + Constants.NAME, cashItem.getItemStack().getItemMeta().getDisplayName());
                        outCash.set(Constants.GUIS + guiName + "." + j + ".commands", cashItem.getCommands());
                        List<String> lore = cashItem.getItemStack().getLore();
                        outCash.set(Constants.GUIS + guiName + "." + j + Constants.LORE, lore);
                        List<String> msgs = cashItem.getMessages();
                        outCash.set(Constants.GUIS + guiName + "." + j + ".messages", msgs);
                    }
                }
            }
        }

        try {
            outCash.save(Constants.CASHGUI_FILE_PATH);
        } catch (IOException exception) {
            APIServer.logError("Impossível de criar arquivos em " + Constants.DATA_LAYER_FOLDER_PATH, 3);
        }

    }

    private void loadDefaultValues() {
        for (int i = 0; i < 27; i++) {
            if (i == 10) {
                ItemStack itemStack = new ItemStack(Material.OAK_SIGN);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName("$7Permissões$8.".replace('$', (char) 0x00A7));
                itemMeta.setLore(List.of( "$7Compre permissões".replace('$', (char) 0x00A7), "$7para lhe ajudar".replace('$', (char) 0x00A7), "$7nessa jornada$8.".replace('$', (char) 0x00A7)));
                itemStack.setItemMeta(itemMeta);
                guis.put(10, "Perm");
                guisInvert.put("Perm", 10);
                menuGui.add(itemStack);
                List<CashItem> tempList = new ArrayList<>();
                for (int j = 0; j < 36; j++) {
                    if (j == 10) {
                        ItemStack is = new ItemStack(Material.EXPERIENCE_BOTTLE);
                        ItemMeta iss = is.getItemMeta();
                        iss.setDisplayName("$725% Bônus de XP no McMMO!".replace('$', (char) 0x00A7));
                        iss.setLore(List.of("$aUpe mais rápido na sua especialidade! Não acumlativos!".replace('$', (char) 0x00A7),"$230 dias de duração!".replace('$', (char) 0x00A7), "", "$2Preço C$ 70".replace('$', (char) 0x00A7)));
                        is.setItemMeta(iss);
                        tempList.add(new CashItem(is, 70, List.of( "$8[$aE$9S$8] $7parabéns pela aquisição$8!".replace('$', (char) 0x00A7)), List.of("lp user %player_name% permission settemp mcmmo.perks.xp.customboost.all true 30d"), false));
                    } else {
                        tempList.add(new CashItem(getGlass(), 0, null, null, true));
                    }
                }
                this.othersGui.put(i, tempList);
            } else {
                this.menuGui.add(getGlass());
            }
        }
    }

    private void loadMapFromArchive(FileConfiguration cashGui, Map<Integer, List<CashItem>> othersGuiTemp, List<ItemStack> menuGuiTemp, Map<Integer, String> guisTemp, Map<String, Integer> guisTempInvert) {
        for (int i = 0; i < cashGui.getInt("menu.size"); i++) {
            if (cashGui.contains(Constants.MENU + i)) {
                ItemStack itemStack = new ItemStack(Material.valueOf(cashGui.getString(Constants.MENU + i + Constants.MATERIAL)));
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName(cashGui.getString(Constants.MENU + i + Constants.NAME).replace('$', (char) 0x00A7));
                List<String> listando = cashGui.getStringList(Constants.MENU + i + Constants.LORE);
                APIServer.putColorOnList(listando);
                itemMeta.setLore(listando);
                itemStack.setItemMeta(itemMeta);
                menuGuiTemp.add(itemStack);
                String guiName = cashGui.getString(Constants.MENU + i + ".gui");
                guisTemp.put(i, guiName);
                guisTempInvert.put(guiName, i);
                List<CashItem> tempList = new ArrayList<>();
                for (int j = 0; j < cashGui.getInt(Constants.GUIS + guiName + ".size"); j++) {
                    if (cashGui.contains(Constants.GUIS + guiName + "." + j)) {
                        ItemStack guiItem = new ItemStack(Material.valueOf(cashGui.getString(Constants.GUIS + guiName + "." + j + Constants.MATERIAL)));
                        ItemMeta guiMeta = guiItem.getItemMeta();
                        guiMeta.setDisplayName(cashGui.getString(Constants.GUIS + guiName + "." + j + Constants.NAME).replace('$', (char) 0x00A7));
                        List<String> listandoNovo = cashGui.getStringList(Constants.GUIS + guiName + "." + j + Constants.LORE);
                        APIServer.putColorOnList(listandoNovo);
                        guiMeta.setLore(listandoNovo);
                        guiItem.setItemMeta(guiMeta);
                        List<String> commands = cashGui.getStringList(Constants.GUIS + guiName + "." + j + ".commands");
                        List<String> msgs = cashGui.getStringList(Constants.GUIS + guiName + "." + j + ".messages");
                        APIServer.putColorOnList(msgs);
                        tempList.add(new CashItem(guiItem, cashGui.getInt(Constants.GUIS + guiName + "." + j + ".cost"), msgs, commands, false));
                    } else {
                        tempList.add(new CashItem(getGlass(), 0, null, null, true));
                    }
                }
                othersGuiTemp.put(i, tempList);
            } else {
                menuGuiTemp.add(getGlass());
            }
        }
    }

    private ItemStack getGlass() {
        ItemStack itemStack = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("$7Loja de $aCash$8.".replace('$', (char) 0x00A7));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
