package co.lotc.betterteams;

import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.UUID;

public class BetterTeams extends JavaPlugin
{
    public static final int MAX_NAME_LENGTH = 20;
    static int ghostTaskId;
    static BetterTeams Main;
    static Scoreboard ghostBoard;

    static {
        BetterTeams.ghostTaskId = -1;
    }

    //public HashMap<UUID, Boolean> toggling;
    public HashMap<UUID, Long> statusCooldown;
    private BoardManager boards;

    public static BetterTeams getMain() {
        return Main;
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
        this.getCommand("showhealth").setExecutor(handler);
        this.getCommand("status").setExecutor(handler);
        this.getCommand("appearto").setExecutor(handler);
        this.getCommand("tagcolor").setExecutor(handler);
        this.getCommand("showmcnames").setExecutor(handler);
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
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
