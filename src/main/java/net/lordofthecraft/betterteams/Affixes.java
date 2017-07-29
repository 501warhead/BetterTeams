package net.lordofthecraft.betterteams;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.lordofthecraft.arche.ArcheCore;
import net.lordofthecraft.arche.interfaces.Persona;
import net.lordofthecraft.arche.interfaces.PersonaHandler;

public class Affixes
{
	private static final char ELLIPSES = '\u2026';
    private static BoardManager boards;
    private static PersonaHandler handler;

    static {
        Affixes.boards = null;
        Affixes.handler = null;
    }

    private final Player player;

    private String prefix_rp = "";
    private String suffix_rp = "";

    private String prefix_mc = "";
    private String suffix_mc = ""; //should always be mc_name
    
    
    private Status status;
    private GroupColor color;
    
    public static Affixes onJoin(Player p, Status cached) {
    	//Player just joined. Have no teams. Find out what
    	//Team settings player should be entitled to
    	Affixes a = new Affixes(p);
    	
    	a.setStatus(cached);
    	a.setGroupColor(GroupColor.getHighest(p));
    	
    	a.handleSuffix();
    	a.handlePrefix();
    	
    	boards.createTeams(a);
    	
    	return a;
    }
    
    public static Affixes fromExistingTeams(Player p) {
    	//Player already online. Should have 4 teams in 4 boards
    	//Parse teams to find out the status, color, suffix, etc
    	Affixes a = new Affixes(p);
    	
    	Team t = p.getScoreboard().getTeam(p.getName());
    	//Player has status and tag colors in all teams
    	a.prefix_mc = t.getPrefix();
    	
        a.parseStatus();
        a.parseGroupColor();
        
        return a;
    }
    


    private Affixes(final Player p) {
        if (Affixes.boards == null) {
            Affixes.boards = BetterTeams.Main.getBoardManager();
            Affixes.handler = ArcheCore.getControls().getPersonaHandler();
        }
        
        this.player = p;
    }
 
  //NB: Returns whether or not the prefix should be changed
  //based on changes made, specifically if the RP name overflows
    public void handleSuffix() {
    	final String colorPart = color == null? "" : color.toString();
    	
    	//MC Part
    	String namepart = player.getName();
    	if(namepart.length() > 12) {
    		namepart = namepart.substring(namepart.length()-12, namepart.length());
    	}
    	if(color != null && !color.isStylized()) suffix_mc = colorPart + ChatColor.ITALIC + namepart;
    	else suffix_mc = colorPart + namepart;
    	
    	
    	//RP Name part
    	Persona ps = handler.getPersona(player);
    	if(ps == null) {
    		suffix_rp = suffix_mc; //Replace nonexistent RP name with MC name
    	} else {
    		String persName = ps.getName();
    		
    		int len = persName.length();
    		if(len <= 12) { 
    			suffix_rp = persName;
    		}else { //RP name will overflow space available in suffix
    			int maxlen = status == null? 20 : 16; //Status takes up 4/8 characters
    			//Will name overflow? If so take max allowable characters + add ellipses
    			if(len > maxlen) {
    				int fitsInPrefix = status == null? 8:4;
    				suffix_rp = persName.substring(fitsInPrefix, fitsInPrefix+11) + ELLIPSES;
    			}else { //Just take the last 12 characters; they fit entirely
    				suffix_rp = persName.substring(persName.length() - 12, persName.length());
    			}
    		}
    		
    		suffix_rp = colorPart + suffix_rp;
    	}
    	
    }
    
    public void handlePrefix() {
    	
    	
    	boolean hasColor = color !=null;
    	boolean hasStatus = status != null;
    	
    	
    	String prefix = (hasStatus? "[" + status.toString() + ChatColor.RESET + "] " : "") +
    				(hasColor? color.toString() : "");
    	
    	//MC Part
    	prefix_mc = hasColor && color.isStylized()? prefix : prefix + ChatColor.ITALIC;
    	String playerName = player.getName();
    	if(playerName.length() > 12) {
    		prefix_mc += playerName.substring(0,playerName.length()-12);
    	}
    	
    	Persona ps = handler.getPersona(player);
    	if(ps == null) {
    		//RP-board name should be MC name since no Persona
    		//Add italicized chars if possible
    		prefix_rp = hasColor && color.isStylized()? prefix : prefix + ChatColor.ITALIC;
    	}else if(ps.getName().length() <= 12) {
    		prefix_rp = prefix;
    		return; //RP name fits in suffix, no prefix adjustment needed
    	} else {
        	int fitsInPrefix = hasStatus? 4:8;
        	int bound = Math.min(fitsInPrefix, ps.getName().length() - 12	);
        	String persPrefix = ps.getName().substring(0,bound);
        	prefix_rp = prefix + persPrefix;
    	}

    }
    
    private Status parseStatus() {
    	String prefix = prefix_mc;
        if (parseHasStatus()) {
        	this.status = Status.fromSymbol(prefix.charAt(3));
        } else {
        	this.status = null;
        }
        
        return this.status;
    }
    
    private boolean parseHasStatus() {
    	String prefix = prefix_mc;
    	return (StringUtils.isNotEmpty(prefix) && prefix.startsWith("["));
    }
    
    private GroupColor parseGroupColor(){
    	String prefix = prefix_mc;
    	final int offset = parseHasStatus()? 8 : 0;
    	if(prefix == null || prefix.length() == offset) return null;
    	final String colorCode = prefix.substring(offset, offset + 
    		((prefix.length() > offset + 3 && prefix.charAt(offset + 2) == ChatColor.COLOR_CHAR)? 4 : 2));
    	color = GroupColor.match(colorCode);
    	return color;
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public Status getStatus() {
        return this.status;
    }
    
    public void setStatus(final Status status) {
        this.status = status;
    }
    
    public GroupColor getColor() {
        return this.color;
    }
    
    public String getPrefixMC() { return prefix_mc; }
    public String getSuffixMC() { return suffix_mc; }
    public String getPrefixRP() { return prefix_rp; }
    public String getSuffixRP() { return suffix_rp; }
    
    public void setGroupColor(final GroupColor color) {
        this.color = color;
    }
    
    
    void apply(Scoreboard[] bb) {
    	handlePrefix();
    	handleSuffix();
    	
    	for(Scoreboard b : bb) {
    		Team t = b.getTeam(player.getName());
    		if(boards.boardShowsRPNames(b)){
    			t.setPrefix(prefix_rp);
    			t.setSuffix(suffix_rp);
    		} else {
    			t.setPrefix(prefix_mc);
    			t.setSuffix(suffix_mc);
    		}
    	}
    }
    
}
