package net.lordofthecraft.betterteams;

import com.google.common.collect.Lists;
import net.lordofthecraft.arche.ArcheCore;
import net.lordofthecraft.arche.interfaces.Persona;
import net.lordofthecraft.arche.interfaces.PersonaHandler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;

public class BoardManager
{
    private final ScoreboardManager manager;
    private final PersonaHandler handler;
    private final Scoreboard[] boards;
    
    BoardManager() {
        super();
        this.manager = Bukkit.getScoreboardManager();
        this.handler = ArcheCore.getControls().getPersonaHandler();
        this.boards = new Scoreboard[4];
        for (int i = 0; i < this.boards.length; ++i) {
            this.boards[i] = this.manager.getNewScoreboard();
            if (i == 1 || i == 3) {
                final Objective o = this.boards[i].registerNewObjective("showhealth", "health");
                o.setDisplaySlot(DisplaySlot.BELOW_NAME);
                o.setDisplayName(ChatColor.DARK_RED + "\u2764");
            }
        }
    }

    public List<Player> getStatusedPlayers(Status st) {
    	List<Player> players = Lists.newArrayList();
    	Affixes status;
    	for (Player pl : Bukkit.getOnlinePlayers()) {
    		status = new Affixes(pl);
            if (status.getStatus() == st) {
                players.add(pl);
            }
        }
    	return players;
    }
    
    public void close(final Player p) {
        if (this.isGhosting(p)) {
            this.removeGhost(p);
            return;
        }
        final String name = p.getName();
        for (Scoreboard board : this.boards) {
            final Team t = board.getTeam(name);
            if (t != null) {
                t.unregister();
            }
        }
    }
    
    public void unregister() {
        Scoreboard[] boards;
        for (int length = (boards = this.boards).length, i = 0; i < length; ++i) {
            final Scoreboard s = boards[i];
            for (final Team t : s.getTeams()) {
                t.unregister();
            }
        }
    }
    
    public boolean hasTeam(final Player p) {
        return p.getScoreboard().getTeam(p.getName()) != null;
    }
    
    @SuppressWarnings("deprecation")
	public boolean isGhosting(final Player p) {
        final Team t = p.getScoreboard().getPlayerTeam(p);
        return t != null && (t.getSize() > 1 && !t.getName().equals(p.getName()));
    }
    
    @SuppressWarnings("deprecation")
	public boolean isGhosted(final Player p) {
        final Team t = p.getScoreboard().getPlayerTeam(p);
        return t != null && (t.getSize() > 1 && t.getName().equals(p.getName()));
    }
    
    public void toggleShowingHealth(final Player p) {
        final Scoreboard b = p.getScoreboard();
        if (b == this.boards[0]) {
            p.setScoreboard(this.boards[1]);
        }
        else if (b == this.boards[1]) {
            p.setScoreboard(this.boards[0]);
        }
        else if (b == this.boards[3]) {
            p.setScoreboard(this.boards[2]);
        }
        else if (b == this.boards[2]) {
            p.setScoreboard(this.boards[3]);
        }
    }
    
    public boolean isShowingHealth(final Player p) {
        final Scoreboard b = p.getScoreboard();
        return b == this.boards[1] || b == this.boards[3];
    }
    
    public void apply(final Affixes a) {
        final Player p = a.getPlayer();
        final Scoreboard b = this.boards[(a.isSeeingMinecraftNames() ? 2 : 0) + (a.isShowingHealth() ? 1 : 0)];
        if (b != p.getScoreboard()) {
            p.setScoreboard(b);
        }
        final String suffix = a.getSuffix();
        final String prefix = a.getPrefix();
        if (StringUtils.isEmpty(suffix) && StringUtils.isEmpty(prefix)) {
            this.deleteTeams(p);
        }
        else {
            this.createTeams(p, prefix, suffix);
        }
    }
    
    @SuppressWarnings("deprecation")
	void createTeams(final Player p, ChatColor color) {
    	
    	
        final String name = p.getName();
        if (suffix == null) {
            suffix = "";
        }
        if (prefix == null) {
            prefix = "";
        }
        for (int i = 0; i < this.boards.length; ++i) {
            Team t = this.boards[i].getTeam(name);
            if (t == null) {
                t = this.boards[i].registerNewTeam(name);
                t.addPlayer(p);
                t.setColor(ChatColor.GREEN);
            }
            if (i > 1) {
                t.setPrefix(String.valueOf(prefix) + ChatColor.ITALIC);
            }
            else {
                t.setPrefix(prefix);
                t.setSuffix(suffix);
            }
        }
    }
    
    void deleteTeams(final Player p) {
        final String name = p.getName();
        for (Scoreboard board : this.boards) {
            final Team t = board.getTeam(name);
            if (t != null && t.getSize() <= 1) {
                t.unregister();
            }
        }
    }
    
    @SuppressWarnings("deprecation")
	public void addGhost(final Player c, final Player o) {
        Scoreboard[] boards;
        for (int length = (boards = this.boards).length, i = 0; i < length; ++i) {
            final Scoreboard s = boards[i];
            Team t = s.getTeam(o.getName());
            if (t == null) {
                t = s.registerNewTeam(o.getName());
                t.addPlayer(o);
            }
            t.addPlayer(c);
            t.setCanSeeFriendlyInvisibles(true);
        }
    }
    
    @SuppressWarnings("deprecation")
	public void removeGhost(final Player p) {
        Scoreboard[] boards;
        for (int length = (boards = this.boards).length, i = 0; i < length; ++i) {
            final Scoreboard s = boards[i];
            final Team t = s.getPlayerTeam(p);
            if (t != null) {
                t.removePlayer(p);
                if (t.getSize() == 0) {
                    t.unregister();
                }
                else if (t.getSize() == 1) {
                    if (StringUtils.isEmpty(t.getPrefix()) && StringUtils.isEmpty(t.getSuffix())) {
                        t.unregister();
                    }
                    else {
                        t.setCanSeeFriendlyInvisibles(false);
                    }
                }
            }
        }
    }
}
