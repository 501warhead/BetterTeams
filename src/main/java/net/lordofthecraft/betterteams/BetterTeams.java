package net.lordofthecraft.betterteams;

import com.google.common.collect.Maps;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;
import net.lordofthecraft.Persistence.APIManager;
import net.lordofthecraft.Persistence.PersistenceFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import static org.bukkit.Bukkit.getLogger;

public class BetterTeams extends JavaPlugin implements Listener {

  static int ghostTaskId;
  static BetterTeams Main;
  static Scoreboard ghostBoard;
  static TeamPacketListener packetListener;

  static  APIManager apiManager;


  static {
    BetterTeams.ghostTaskId = -1;
  }

  public HashMap<UUID, Long> statusCooldown;
  private BoardManager boards;

  public static BetterTeams getMain() {
    return Main;
  }

  public void onEnable() {
    apiManager = new APIManager();

    BetterTeams.Main = this;
    File folder = getDataFolder();
    if (!folder.exists()) {
      folder.mkdirs();
    }
    PersistenceFile.init(new File(folder, "persistence.yml"));
    this.getServer().getPluginManager().registerEvents(this, this);

    this.statusCooldown = Maps.newHashMap();
    this.boards = new BoardManager();

    final PluginManager man = Bukkit.getPluginManager();
    packetListener = new TeamPacketListener(this.boards);
    man.registerEvents(new TeamPlayerListener(this.boards), this);

    final TeamCommandHandler handler = new TeamCommandHandler();
    this.getCommand("showhealth").setExecutor(handler);
    this.getCommand("status").setExecutor(handler);
    this.getCommand("appearto").setExecutor(handler);
    this.getCommand("tagcolor").setExecutor(handler);
    this.getCommand("showrpnames").setExecutor(handler);
    this.getCommand("hidenameplates").setExecutor(handler);
    this.getCommand("affixes").setExecutor(handler);
    read();

    Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
      for (Player p : Bukkit.getOnlinePlayers()) {
        Affixes a = Affixes.onJoin(p, null);
        boards.createTeams(a);


        if (apiManager.getKeepShowHealth().contains(p.getUniqueId())) {
          getLogger().info("adding to list in scheduler");
          boards.toggleShowingHealth(p);
        }
      }

    });
  }

  public void onDisable() {
    this.boards.unregister();
    save();
  }

  public BoardManager getBoardManager() {
    return this.boards;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    if (apiManager.getKeepShowHealth().contains(e.getPlayer().getUniqueId())) {
      getLogger().info("Toggling in BetterTeams(PlayerJoin)");
    }

    if (apiManager.getNoNameplates().contains(e.getPlayer().getUniqueId())) {
      boards.toggleHideNameplates(e.getPlayer());
      getLogger().info("Toggling in BetterTeams(PlayerJoin)");
    }
    if (apiManager.getKeepMCNames().contains(e.getPlayer().getUniqueId())) {
      boards.toggleShowingRPNames(e.getPlayer());
      getLogger().info("Toggling in BetterTeams(PlayerJoin)");
    }
    try {

    } catch (ArrayIndexOutOfBoundsException exc){
      getLogger().info("IndexOutOfBonds error occured");
    }
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent e) {
    Player p = e.getPlayer();
    save();
    if (apiManager.getKeepShowHealth().contains(p.getUniqueId())
        || apiManager.getKeepMCNames().contains(p.getUniqueId())
        || apiManager.getNoNameplates().contains(p.getUniqueId())) {
      save();
    }
  }

  private void save() {
    PersistenceFile.getConfig().set("keepMCName", apiManager.getNoNameplates().stream().map(UUID::toString).collect
        (Collectors.toList()));
    PersistenceFile.getConfig().set("noNamePlates", apiManager.getNoNameplates().stream().map(UUID::toString).collect
        (Collectors.toList()));
    PersistenceFile.getConfig().set("showHealth", apiManager.getKeepShowHealth().stream().map(UUID::toString).collect
        (Collectors.toList()));
    PersistenceFile.save();
  }

  private void read() {
    ArrayList<UUID> keepMCName = new ArrayList<>();
    ArrayList<UUID> noNamePlates = new ArrayList<>();
    ArrayList<UUID> showHealth = new ArrayList<>();

    for (String uuid : PersistenceFile.getConfig().getStringList("keepMCName")) {
      keepMCName.add(UUID.fromString(uuid));
    }

    for (String uuid : PersistenceFile.getConfig().getStringList("noNamePlates")) {
      noNamePlates.add(UUID.fromString(uuid));
    }

    for (String uuid : PersistenceFile.getConfig().getStringList("showHealth")) {
      showHealth.add(UUID.fromString(uuid));
    }

    apiManager.setKeepMCNames(keepMCName);
    apiManager.setKeepShowHealth(noNamePlates);
    apiManager.setKeepShowHealth(showHealth);

  }
}
