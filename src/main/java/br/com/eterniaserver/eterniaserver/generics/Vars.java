package br.com.eterniaserver.eterniaserver.generics;

import br.com.eterniaserver.eterniaserver.utils.CustomPlaceholder;
import br.com.eterniaserver.eterniaserver.utils.FormatInfo;
import br.com.eterniaserver.eterniaserver.utils.PlayerTeleport;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vars {

    protected static final Map<String, Location> homes = new HashMap<>();
    protected static final Map<String, Location> shops = new HashMap<>();
    protected static final Map<String, Location> warps = new HashMap<>();
    protected static final Map<String, String[]> home = new HashMap<>();

    protected static final Map<String, Long> tpaTime = new HashMap<>();
    protected static final Map<String, String> tpaRequests = new HashMap<>();
    protected static final Map<String, Double> balances = new HashMap<>();
    protected static final Map<String, Integer> xp = new HashMap<>();
    protected static final Map<String, Long> afkTime = new HashMap<>();

    protected static final List<World> skippingWorlds = new ArrayList<>();
    protected static final List<String> god = new ArrayList<>();
    protected static final List<String> afk = new ArrayList<>();
    protected static final List<CustomPlaceholder> customPlaceholders = new ArrayList<>();

    protected static final List<String> playerCooldown = new ArrayList<>();

    protected static final Map<String, Long> playerMuted = new HashMap<>();
    protected static final Map<String, Long> bedCooldown = new HashMap<>();
    protected static final Map<String, String> kitsCooldown = new HashMap<>();
    protected static final Map<String, String> playerLogin = new HashMap<>();
    protected static final Map<String, String> tell = new HashMap<>();
    protected static final Map<Player, Boolean> spy = new HashMap<>();
    protected static final Map<String, Integer> global = new HashMap<>();
    protected static final Map<String, Integer> playersInPortal = new HashMap<>();
    protected static final Map<String, Location> back = new HashMap<>();
    protected static final Map<String, FormatInfo> uufi = new HashMap<>();
    protected static final Map<Player, PlayerTeleport> teleports = new HashMap<>();

}