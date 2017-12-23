package net.lordofthecraft.betterteams;

import com.google.common.collect.Maps;
import net.lordofthecraft.Persistence.PersistenceFile;
import net.lordofthecraft.Persistence.APIManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import net.lordofthecraft.Persistence.PersistenceConfig;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BetterTeams extends JavaPlugin implements Listener {
    static int ghostTaskId;
    static BetterTeams Main;
    static Scoreboard ghostBoard;
    static TeamPacketListener packetListener;

    APIManager a = new APIManager();
    
    
    static {
        BetterTeams.ghostTaskId = -1;
    }

    public HashMap<UUID, Long> statusCooldown;
    private BoardManager boards;

    public static BetterTeams getMain() {
        return Main;
    }

    public void onEnable() {
        BetterTeams.Main = this;
        File folder = getDataFolder();
        if(!folder.exists()){
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
            }
            for(Player p : a.getKeep_showhealth()){
                boards.toggleShowingHealth(p);
                break;
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
    public void onPlayerJoin(PlayerJoinEvent e){
        if(a.getKeep_showhealth().contains(e.getPlayer())){
            boards.toggleShowingHealth(e.getPlayer());
        }
        if(a.getNo_nameplates().contains(e.getPlayer())){
            boards.toggleHideNameplates(e.getPlayer());
        }
        if(a.getKeep_mcnames().contains(e.getPlayer())){
            boards.toggleShowingRPNames(e.getPlayer());
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        if(a.getKeep_showhealth().contains(p) || a.getKeep_mcnames().contains(p) || a.getNo_nameplates().contains(p)){
            save();
        }
    }

    private void save(){
        PersistenceFile.getConfig().set("PersistenceConfig", PersistenceConfig.getConfigList());
        PersistenceFile.save();
    }

    private void read(){
        List<Map<String,Object>> list = (List<Map<String, Object>>) PersistenceFile.getConfig().get("Persistence Config");

        if(list != null){
            PersistenceConfig.deserialize(list);
        }
        else {
            System.out.println("Nothing found!");
        }
    }
}
