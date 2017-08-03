package net.lordofthecraft.betterteams;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.UUID;

public class BetterTeams extends JavaPlugin {
    static int ghostTaskId;
    static BetterTeams Main;
    static Scoreboard ghostBoard;
    static TeamPacketListener packetListener;
    
    
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
        
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                Affixes a = Affixes.onJoin(p, null);
                boards.createTeams(a);
            }
        });
    }
    
    public void onDisable() {
        this.boards.unregister();
    }

    public BoardManager getBoardManager() {
        return this.boards;
    }
}
