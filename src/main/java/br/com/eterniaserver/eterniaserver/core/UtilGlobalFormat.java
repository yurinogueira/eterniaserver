package br.com.eterniaserver.eterniaserver.core;

import br.com.eterniaserver.eterniaserver.EterniaServer;
import br.com.eterniaserver.eterniaserver.objects.CustomPlaceholders;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UtilGlobalFormat {

	public void filter(Player player, String message) {
		BaseComponent[] baseComponents = customPlaceholder(player, EterniaServer.chat.globalFormat, message);
		Bukkit.spigot().broadcast(baseComponents);
	}

	private BaseComponent[] customPlaceholder(Player player, String format, String message) {
		if (player.hasPermission("eternia.chat.color")) {
			message = message.replace('&', '§');
		}
		Map<Integer, TextComponent> textComponentMap = new TreeMap<>();
		EterniaServer.chat.customPlaceholdersObjectsMap.forEach((placeholder, object) -> {
			if (format.contains("{" + placeholder + "}") && player.hasPermission(object.getPermission())) {
				textComponentMap.put(object.getPriority(), getText(player, object));
			}
		});

		String[] messageSplited = message.split(" ");

		for (int i = 0; i < messageSplited.length; i++) {
			if (i > 0) {
				messageSplited[i] = ChatColor.getLastColors(messageSplited[i - 1]) + messageSplited[i];
			}
		}

		BaseComponent[] baseComponents = new BaseComponent[textComponentMap.size() + messageSplited.length];

		AtomicInteger integer = new AtomicInteger(0);
		textComponentMap.forEach((id, component) -> baseComponents[integer.getAndIncrement()] = component);

		for (String actualMsg : messageSplited) {
			baseComponents[integer.getAndIncrement()] = getComponent(actualMsg, player);
		}

		return baseComponents;
	}

	private TextComponent getComponent(String actualMsg, Player player) {
		if (actualMsg.contains("@") && PluginVars.playersName.containsKey(actualMsg)) {
			Player target = Bukkit.getPlayer(PluginVars.playersName.get(actualMsg));
			actualMsg = PluginVars.colors.get(3) + actualMsg + PluginVars.colors.get(15);
			if (target != null && target.isOnline()) {
				target.playNote(target.getLocation(), Instrument.PIANO, Note.natural(1, Note.Tone.F));
				target.sendTitle(APIServer.getColor(player.getDisplayName()), PluginVars.colors.get(7) + "mencionou você" + PluginVars.colors.get(8) + "!", 10, 40, 10);
			}
			return new TextComponent(actualMsg + " ");
		} else if (actualMsg.equals("[item]")) {
			ItemStack itemStack = player.getInventory().getItemInMainHand();
			if (!itemStack.getType().equals(Material.AIR)) {
				return sendItemInHand(actualMsg + " ", itemStack);
			} else {
				return new TextComponent(actualMsg + " ");
			}
		} else {
			return new TextComponent(actualMsg + " ");
		}
	}

	private	TextComponent sendItemInHand(String string, ItemStack itemStack) {
		if (APIServer.getVersion() >= 116) {
			HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, Bukkit.getItemFactory().hoverContentOf(itemStack));
			TextComponent component = new TextComponent(string.replace("[item]", PluginVars.colors.get(3) + "x" + itemStack.getAmount() + " " + itemStack.getI18NDisplayName() + PluginVars.colors.get(15)));
			component.setHoverEvent(event);
			return component;
		}
		return new TextComponent("sem suporte");
	}

	private TextComponent getText(Player player, CustomPlaceholders objects) {
		TextComponent textComponent = new TextComponent(APIServer.getColor(APIChat.setPlaceholders(player, objects.getValue())));
		if (!objects.getHoverText().equals("")) {
			List<TextComponent> textComponentList = new ArrayList<>();
			textComponentList.add(new TextComponent(APIServer.getColor(APIChat.setPlaceholders(player, objects.getHoverText()))));
			textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(textComponentList.toArray(new TextComponent[textComponentList.size() - 1]))));
		}
		if (!objects.getSuggestCmd().equals("")) {
			textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, APIServer.getColor(APIChat.setPlaceholders(player, objects.getSuggestCmd()))));
		}
		return textComponent;
	}

}
