package co.lotc.betterteams;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.plugin.java.*;
import org.bukkit.scoreboard.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.scheduler.*;
import org.bukkit.entity.*;
import org.bukkit.plugin.*;

import com.google.common.collect.Maps;

public class BetterTeams extends JavaPlugin
{
    public static final int MAX_NAME_LENGTH = 20;
    static int ghostTaskId;
    static BetterTeams Main;
    static Scoreboard ghostBoard;
    private BoardManager boards;
    //public HashMap<UUID, Boolean> toggling;
	public HashMap<UUID, Long> statusCooldown;
    
    static {
        BetterTeams.ghostTaskId = -1;
    }
    
    public void onEnable() {
        BetterTeams.Main = this;
        this.statusCooldown = Maps.newHashMap();
       // Main.toggling = Maps.newHashMap();
        this.boards = new BoardManager();
        final PluginManager man = Bukkit.getPluginManager();
        man.registerEvents(new TeamPacketListener(this.boards), this);
        man.registerEvents(new TeamPlayerListener(this.boards), this);
        final TeamCommandHandler handler = new TeamCommandHandler();
        this.getCommand("showhealth").setExecutor((CommandExecutor)handler);
        this.getCommand("status").setExecutor((CommandExecutor)handler);
        this.getCommand("appearto").setExecutor((CommandExecutor)handler);
        this.getCommand("tagcolor").setExecutor((CommandExecutor)handler);
        this.getCommand("showmcnames").setExecutor((CommandExecutor)handler);
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                BetterTeams.this.boards.init(p);
                final double h = Math.min(p.getHealth(), p.getMaxHealth());
                p.setHealth(h);
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
