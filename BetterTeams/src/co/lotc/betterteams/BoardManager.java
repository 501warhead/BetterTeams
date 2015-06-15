package co.lotc.betterteams;

import net.lordofthecraft.arche.*;
import org.bukkit.entity.*;
import net.lordofthecraft.arche.interfaces.*;
import org.bukkit.scoreboard.*;
import org.bukkit.*;
import java.util.*;
import org.apache.commons.lang.*;

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
    
    public void init(final Player p) {
        p.setScoreboard(this.boards[0]);
        final GroupColor color = GroupColor.getHighest(p);
        final Persona ps = this.handler.getPersona(p);
        final String name = (ps == null) ? null : ((/*ps.getName()*/p.getName().length() <= 16) ? null : ((/*ps.getName()*/p.getName().length() > 20) ? (String.valueOf(/*ps.getName()*/p.getName().substring(16, 20)) + "\u2026") : /*ps.getName()*/p.getName().substring(16, /*ps.getName()*/p.getName().length())));
        if (color != null || name != null) {
            this.createTeams(p, (color == null) ? "" : color.toString(), name);
        }
    }
    
    public void close(final Player p) {
        if (this.isGhosting(p)) {
            this.removeGhost(p);
            return;
        }
        final String name = p.getName();
        for (int i = 0; i < this.boards.length; ++i) {
            final Team t = this.boards[i].getTeam(name);
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
    
    public boolean isGhosting(final Player p) {
        final Team t = p.getScoreboard().getPlayerTeam((OfflinePlayer)p);
        return t != null && (t.getSize() > 1 && !t.getName().equals(p.getName()));
    }
    
    public boolean isGhosted(final Player p) {
        final Team t = p.getScoreboard().getPlayerTeam((OfflinePlayer)p);
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
    
    public void toggleShowingNames(final Player p) {
        final Scoreboard b = p.getScoreboard();
        if (b == this.boards[0]) {
            p.setScoreboard(this.boards[2]);
        }
        else if (b == this.boards[2]) {
            p.setScoreboard(this.boards[0]);
        }
        else if (b == this.boards[1]) {
            p.setScoreboard(this.boards[3]);
        }
        else if (b == this.boards[3]) {
            p.setScoreboard(this.boards[1]);
        }
    }
    
    public boolean isShowingHealth(final Player p) {
        final Scoreboard b = p.getScoreboard();
        return b == this.boards[1] || b == this.boards[3];
    }
    
    public boolean isSeeingMinecraftNames(final Player p) {
        final Scoreboard b = p.getScoreboard();
        return b == this.boards[2] || b == this.boards[3];
    }
    
    public void setSuffix(final Player p) {
        final Persona ps = ArcheCore.getControls().getPersonaHandler().getPersona(p);
        String suffix;
        if (ps != null && /*ps.getName()*/p.getName().length() > 16) {
            suffix = ((/*ps.getName()*/p.getName().length() > 20) ? (String.valueOf(/*ps.getName()*/p.getName().substring(16, 20)) + "\u2026") : /*ps.getName()*/p.getName().substring(16, /*ps.getName()*/p.getName().length()));
        }
        else {
            suffix = "";
        }
        Set<OfflinePlayer> plays = null;
        final String name = p.getName();
        for (int i = 0; i < this.boards.length; ++i) {
            Team t = this.boards[i].getTeam(name);
            if (t == null) {
                t = this.boards[i].registerNewTeam(name);
                t.addPlayer((OfflinePlayer)p);
            }
            else {
                if (plays == null) {
                    plays = (Set<OfflinePlayer>)t.getPlayers();
                }
                final String prefix = t.getPrefix();
                t.unregister();
                t = this.boards[i].registerNewTeam(name);
                t.setPrefix(prefix);
                for (final OfflinePlayer o : plays) {
                    t.addPlayer(o);
                }
            }
            if (i < 2) {
                t.setSuffix(suffix);
            }
        }
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
    
    void createTeams(final Player p, String prefix, String suffix) {
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
                t.addPlayer((OfflinePlayer)p);
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
        for (int i = 0; i < this.boards.length; ++i) {
            final Team t = this.boards[i].getTeam(name);
            if (t != null && t.getSize() <= 1) {
                t.unregister();
            }
        }
    }
    
    public void addGhost(final Player c, final Player o) {
        Scoreboard[] boards;
        for (int length = (boards = this.boards).length, i = 0; i < length; ++i) {
            final Scoreboard s = boards[i];
            Team t = s.getTeam(o.getName());
            if (t == null) {
                t = s.registerNewTeam(o.getName());
                t.addPlayer((OfflinePlayer)o);
            }
            t.addPlayer((OfflinePlayer)c);
            t.setCanSeeFriendlyInvisibles(true);
        }
    }
    
    public void removeGhost(final Player p) {
        Scoreboard[] boards;
        for (int length = (boards = this.boards).length, i = 0; i < length; ++i) {
            final Scoreboard s = boards[i];
            final Team t = s.getPlayerTeam((OfflinePlayer)p);
            if (t != null) {
                t.removePlayer((OfflinePlayer)p);
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
