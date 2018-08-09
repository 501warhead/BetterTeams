package net.lordofthecraft.betterteams;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.lordofthecraft.arche.ArcheCore;
import net.lordofthecraft.arche.interfaces.Persona;
import net.lordofthecraft.arche.interfaces.PersonaHandler;

import java.util.UUID;

public class Affixes
{
	private static final char ELLIPSES = '\u2026';
	private static net.lordofthecraft.betterteams.BoardManager boards;
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
	private PrefixTag prefixtag;
	private GroupColor color = GroupColor.NORMAL;

	public static Affixes onJoin(Player p, Status cached) {
		//Player just joined. Have no teams. Find out what
		//Team settings player should be entitled to
		Affixes a = new Affixes(p);

		a.setStatus(cached);
		a.setPrefixTag(PrefixTag.getHighest(p));
		a.setGroupColor(GroupColor.getHighest(p));

		a.handleAffixes();

		boards.createTeams(a);

		return a;
	}

	public static Affixes fromExistingTeams(Player p) {
		//Player already online. Should have 4 teams in 4 boards
		//Parse teams to find out the status, color, suffix, etc
		Affixes a = new Affixes(p);

		Team t = p.getScoreboard().getTeam(p.getName());
		//Player has status and tag colors in all teams
		if(t == null) return null;
		a.prefix_mc = t.getPrefix();
		a.suffix_mc = t.getSuffix();

		a.parseStatus();
		a.parsePrefixTag();
		a.parseGroupColor();

		a.handleAffixes();

		return a;
	}

	public static Affixes fromExistingTeams(UUID id) {
		Player pl = Bukkit.getPlayer(id);
		if (pl != null) {
			return fromExistingTeams(pl);
		} else {
			return null;
		}
	}



	private Affixes(final Player p) {
		if (Affixes.boards == null) {
			Affixes.boards = BetterTeams.Main.getBoardManager();
			Affixes.handler = ArcheCore.getControls().getPersonaHandler();
		}

		this.player = p;
	}

	public void handleAffixes() {

		boolean hasColor = color !=null;
		boolean hasPrefix = prefixtag != null;
		boolean hasStatus = status != null;

		String mcprefix = (hasStatus ? "[" + status.toString() + ChatColor.RESET + "] " : "") +
				(hasPrefix && !hasStatus ? prefixtag.toString() + " " : "");
		
		String rpprefix = (hasStatus ? "[" + status.toString() + ChatColor.RESET + "] " : "");

		String color = hasColor ? this.color.toString() : "";

		boolean sameColor = hasPrefix && prefixtag.getColor().equals(color);

		String mcname = player.getName();

		Persona persona = handler.getPersona(player);

		String rpname = persona == null ? mcname : persona.getName();

		int rpextraspace = 16-rpprefix.length() - (sameColor ? 0 : color.length());
		int mcextraspace = 16-mcprefix.length() - (sameColor ? 0 : color.length());

		if (rpname.length() <= 16-color.length()) {
			prefix_rp = rpprefix;
			suffix_rp = color+rpname;
		} else {
			prefix_rp = rpprefix + (sameColor ? "" : color) + rpname.substring(0, rpextraspace);
			if (rpname.length() <= (16-color.length()) + rpextraspace) 
				suffix_rp = color + rpname.substring(rpextraspace);
			else {
				suffix_rp = color + rpname.substring(rpextraspace, rpextraspace+(15-color.length())) + ELLIPSES;
			}
		}

		if (mcname.length() <= 16-color.length()) {
			prefix_mc = mcprefix;
			suffix_mc = color+mcname;
		} else {
			prefix_mc = mcprefix + (sameColor ? "" : color) + mcname.substring(0, mcextraspace);
			suffix_mc = color + mcname.substring(mcextraspace);
		}

	}

	//NB: Returns whether or not the prefix should be changed
	//based on changes made, specifically if the RP name overflows
	/*public void handleSuffix() {
		final String colorPart = color == null? "" : color.toString();

		//MC Part
		String namepart = player.getName();
		if(namepart.length() > 12) {
			namepart = namepart.substring(namepart.length()-12, namepart.length());
		}
		//if(color != null && !color.isStylized()) suffix_mc = colorPart + ChatColor.ITALIC + namepart; else //It's hard to read
		suffix_mc = colorPart + namepart;


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
				int maxlen = 20;
				int extra = 0;
				if (status != null) extra += 4; //Status takes up 4/8 characters
				if (prefixtag != null) extra += prefixtag.toString().length()+1;
				maxlen -= extra;
				//Will name overflow? If so take max allowable characters + add ellipses
				if(len > maxlen) {
					int fitsInPrefix = Math.max(8-extra, 0);
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
		boolean hasPrefix = prefixtag != null;
		boolean hasStatus = status != null;


		String prefix = (hasStatus ? "[" + status.toString() + ChatColor.RESET + "] " : "") +
				(hasPrefix && !hasStatus ? prefixtag.toString() + " " : "") +
				(hasColor ? color.toString() : "");

		//MC Part
		prefix_mc = prefix;
		//prefix_mc = hasColor && color.isStylized()? prefix : prefix + ChatColor.ITALIC;
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
			int extra = 0;
			if (status != null) extra += 4; //Status takes up 4/8 characters
			if (prefixtag != null) extra += prefixtag.toString().length()+1;

			int fitsInPrefix = Math.max(8-extra, 0);
			int bound = Math.min(fitsInPrefix, ps.getName().length() - 12	);
			String persPrefix = ps.getName().substring(0,bound);
			prefix_rp = prefix + persPrefix;
		}

	}*/

	public Status getStatus() {
		return this.status;
	}

	public PrefixTag getPrefixTag() {
		return this.prefixtag;
	}

	public void setStatus(final Status status) {
		this.status = status;
		if (status == null) this.prefixtag = PrefixTag.getHighest(player);
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

	public void setPrefixTag(final PrefixTag prefix) {
		this.prefixtag = prefix;
	}


	void apply(Scoreboard[] bb) {
		handleAffixes();

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

	public Player getPlayer() {
		return this.player;
	}

	public String getTabName() {
		return (prefixtag == null ? "" : prefixtag.toString() + " ") + color.toString() + this.getPlayer().getName();
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
		String suffix = suffix_mc;
		final String colorCode = suffix.substring(0,
				((suffix.charAt(2) == ChatColor.COLOR_CHAR)? 4 : 2));
		color = GroupColor.match(colorCode);
		return color;
	}

	private boolean parseHasPrefixTag(String sub) {
		return (sub.indexOf('[') >= 0);
	}

	private PrefixTag parsePrefixTag() {

		String prefix = prefix_mc;

		final int offset = parseHasStatus() ? 8 : 0;
		if(prefix == null || prefix.length() == offset) return null;

		String sub = prefix.substring(offset);

		if (parseHasPrefixTag(sub)) {
			int end = sub.indexOf(']')+1;
			this.prefixtag = PrefixTag.match(sub.substring(0, end));
		} else {
			this.prefixtag = null;
		}

		return this.prefixtag;
	}

	public String debug() {
		String debug = "Name: " + "'" + player.getName()+ ChatColor.RESET + "'" + "\n" + 
				"Color: " + (color == null ? "'null'" : "'" + color.toString().replace(ChatColor.COLOR_CHAR, '&') + ChatColor.RESET + "'") + "\n" + 
				"Prefix Tag: " + (prefixtag == null ? "'null'" : "'" + prefixtag.toString().replace(ChatColor.COLOR_CHAR, '&')+ ChatColor.RESET + "'") + "\n" + 
				"Tab Name: " + "'" + getTabName() + ChatColor.RESET + "'" + "\n" + 
				"Status: " + (status == null ? "'null'" : "'" + status.toString()+ ChatColor.RESET + "'") + "\n" + 
				"Prefix MC: " + "'" + prefix_mc.replace(ChatColor.COLOR_CHAR, '&')+ ChatColor.RESET + "'" + "\n" + 
				"Suffix MC: " + "'" + suffix_mc.replace(ChatColor.COLOR_CHAR, '&')+ ChatColor.RESET + "'" + "\n" + 
				"Prefix RP: " + "'" + prefix_rp.replace(ChatColor.COLOR_CHAR, '&')+ ChatColor.RESET + "'" + "\n" + 
				"Suffix RP: " + "'" + suffix_rp.replace(ChatColor.COLOR_CHAR, '&')+ ChatColor.RESET + "'" + "\n" + 
				"Name Mapping: " + "'" + BetterTeams.packetListener.getPlayerTeamCode(player).replace(ChatColor.COLOR_CHAR, '&')+ ChatColor.RESET + "'" + "\n" + 
				"Bukkit Custom: " + "'" + player.getCustomName()+ ChatColor.RESET + "'" + "\n" + 
				"Bukkit Display: " + "'" + player.getDisplayName()+ ChatColor.RESET + "'" + "\n" + 
				"Bukkit List: " + "'" + player.getPlayerListName()+ ChatColor.RESET + "'";
		return debug;
	}

}
