package net.lordofthecraft.betterteams;

import org.bukkit.ChatColor;

public enum Status
{
    LOVE(			"LOVE", 			0, 	ChatColor.DARK_RED 		+ "\u2764", "affectionate"), 
    BOUND(			"BOUND", 			1, 	ChatColor.GRAY 			+ "\u2716", "immobilized"), 
    INCAPACITATED(	"INCAPACITATED",	2, 	ChatColor.DARK_GRAY 	+ "\u2620", "unconscious"),
    FIGHTING(		"FIGHTING",			3,	ChatColor.RED 			+ "\u2694", "fighting"), 
    MOUNTED(		"MOUNTED",		 	4, 	ChatColor.BOLD 			+ "\u265e", "mounted"), 
    PERFORMING(		"PERFORMING", 		5, 	ChatColor.GREEN 		+ "\u266b", "performing"), 
    CASTING(		"CASTING", 			6, 	ChatColor.GOLD 			+ "\u272e", "casting"), 
    BLIND(			"BLIND", 			7, 	ChatColor.LIGHT_PURPLE 	+ "\u29de", "blinded"),
    KAWAII(			"KAWAII", 			8, 	ChatColor.DARK_PURPLE 	+ "\u2740", "kawaii"),
    FROZEN(			"FROZEN", 			9, 	ChatColor.AQUA			+ "\u2744", "frozen"),
    SLEEPING(		"SLEEPING", 		10, ChatColor.DARK_GREEN 	+ "\u262a", "sleeping"),
    CRUSADING(		"CRUSADING", 		11,	ChatColor.YELLOW 		+ "\u2021", "crusading"),
    FLAGGED(		"FLAGGED",			12,	ChatColor.DARK_AQUA		+ "\u2691",	"flagged"),
    RECORDING(		"RECORDING",		13,	ChatColor.DARK_BLUE		+ "\u2022",	"recording"),
    UNDEAD(         "UNDEAD",           14, ChatColor.BLACK         + "\u2742", "undead");
    
    private final String icon;
    private final String name;

    Status(final String s, final int n, final String icon, final String name) {
        this.icon = icon;
        this.name = name;
    }
    
    public String toString() {
        return this.icon;
    }
    
    public String getName() {
        return this.name;
    }
}
