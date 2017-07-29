package net.lordofthecraft.betterteams;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;

import com.google.common.collect.Lists;

public class BoardManager
{	
    private final ScoreboardManager manager;
    private final Scoreboard[] boards;
    
    BoardManager() {
    	
        this.manager = Bukkit.getScoreboardManager();
        this.boards = new Scoreboard[4];
        
        for (int i = 0; i < this.boards.length; ++i) {
            this.boards[i] = this.manager.getNewScoreboard();
            if (i == 1 || i == 3) {
                final Objective o = this.boards[i].registerNewObjective("showhealth", "dummy");
                o.setDisplaySlot(DisplaySlot.BELOW_NAME);
                o.setDisplayName(ChatColor.DARK_RED + "\u2764");
            }
            
            //Bit for TownCandy npcs which have name ChatColor.BOLD
            Team x = boards[i].registerNewTeam("towncandy_npc"); //issues if a player "towncandy_npc" shows up
            x.setOption(Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
            x.addEntry(""+ChatColor.BOLD);
        }
    }
    
    public boolean boardShowsHealth(Scoreboard board) {
    	return (board == boards[1] || board == boards[3]);
    }
    
    public boolean boardShowsRPNames(Scoreboard board) {
    	return (board == boards[0] || board == boards[1]);
    }

    public List<Player> getStatusedPlayers(Status st) {
    	List<Player> players = Lists.newArrayList();
    	Affixes status;
    	for (Player pl : Bukkit.getOnlinePlayers()) {
    		status = Affixes.fromExistingTeams(pl);
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
    	for(Scoreboard board : boards) {
    		board.getTeams().stream().forEach(t -> t.unregister());
    	}
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
    
    public boolean toggleShowingHealth(final Player p) {
        final Scoreboard b = p.getScoreboard();
        if (b == this.boards[0]) {
            p.setScoreboard(this.boards[1]);
            return true;
        }
        else if (b == this.boards[1]) {
            p.setScoreboard(this.boards[0]);
            return false;
        }
        else if (b == this.boards[3]) {
            p.setScoreboard(this.boards[2]);
            return false;
        }
        else if (b == this.boards[2]) {
            p.setScoreboard(this.boards[3]);
            return true;
        }
        
        throw new ArrayIndexOutOfBoundsException("More than 4 scoreboards in BetterTeams");
    }
    
    public boolean toggleShowingRPNames(final Player p) {
        final Scoreboard b = p.getScoreboard();
        if (b == this.boards[0]) {
            p.setScoreboard(this.boards[2]);
            return false;
        }
        else if (b == this.boards[1]) {
            p.setScoreboard(this.boards[3]);
            return false;
        }
        else if (b == this.boards[3]) {
            p.setScoreboard(this.boards[1]);
            return true;
        }
        else if (b == this.boards[2]) {
            p.setScoreboard(this.boards[0]);
            return true;
        }
        
        throw new ArrayIndexOutOfBoundsException("More than 4 scoreboards in BetterTeams");
    }
    
    
    public void apply(Affixes a){
    	a.apply(this.boards);
    }
    
    
	void createTeams(Affixes a) {
    	Player p = a.getPlayer();
    	for(Scoreboard board : boards) {
    		Team t = board.getTeam(p.getName());
    		if ( t != null)BetterTeams.Main.getLogger().warning("Player Teams already existed for nascent player:" + p.getName());
    		else t = board.registerNewTeam(p.getName());
    		
    		t.setOption(Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
    		
    		if(this.boardShowsRPNames(board)) {
    			t.setPrefix(a.getPrefixRP());
    			t.setSuffix(a.getSuffixRP());
    		} else {
    			t.setPrefix(a.getPrefixMC());
    			t.setSuffix(a.getSuffixMC());
    		}
    		
    		t.addEntry(BetterTeams.packetListener.getPlayerTeamCode(p));
    	}
    	
    	p.setScoreboard(boards[0]);
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
    
    public void updateHealth(Player p, double health) {
    	int intHP = (int) Math.ceil(health);
    	for(Scoreboard board : boards) {
    		if( this.boardShowsHealth(board) ){
    			String playerCode = BetterTeams.packetListener.getPlayerTeamCode(p);
    			Objective o = board.getObjective(DisplaySlot.BELOW_NAME);
    			Score score = o.getScore(playerCode);
    			score.setScore(intHP);
    		}
    	}
    }
}
