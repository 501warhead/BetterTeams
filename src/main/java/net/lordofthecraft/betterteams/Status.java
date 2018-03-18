package net.lordofthecraft.betterteams;

import org.bukkit.ChatColor;

public enum Status
{
    AFFECTIONATE(	ChatColor.DARK_RED 		+ "\u2764"), 
    CRIPPLED(		ChatColor.BLUE 			+ "\u267F"), 
    PIRATING(		ChatColor.DARK_GRAY 	+ "\u2620"),
    FIGHTING(		ChatColor.RED 			+ "\u2694"), 
    MOUNTED(		ChatColor.BOLD 			+ "\u265e"), 
    MUSICAL(		ChatColor.GREEN 		+ "\u266b"), 
    CASTING(		ChatColor.GOLD 			+ "\u272e"), 
    BLINDED(		ChatColor.LIGHT_PURPLE 	+ "\u29de"),
    FLORAL(			ChatColor.DARK_PURPLE 	+ "\u2740"),
    FROZEN(			ChatColor.AQUA			+ "\u2744"),
    SLEEPING(		ChatColor.DARK_GREEN 	+ "\u262a"),
    CRUSADING(		ChatColor.YELLOW 		+ "\u2021"),
    FLAGGED(		ChatColor.DARK_AQUA		+ "\u2691"),
    RECORDING(		ChatColor.GRAY			+ "\u25CF");
    //UNDEAD(       ChatColor.BLACK         + "\u2742");
    
    private final String icon;

    Status(final String icon) {
        this.icon = icon;
    }
    
    public String toString() {
        return this.icon;
    }
    
    public String getName() {
        return this.name().toLowerCase();
    }
    
    public char getSymbol() {
    	return icon.charAt(2);
    }
    
    public static Status fromName(String name) {
    	for(Status s : Status.values()) {
    		if(s.getName().equalsIgnoreCase(name))
    			return s;
    	}
    	return null;
    }
    
    public static Status fromSymbol(char symbol) {
    	for(Status s : Status.values()) {
    		if(s.getSymbol() == symbol) return s;
    	}
    	return null;
    }
    
    public static String getAllNames() {
		final StringBuilder names = new StringBuilder(75);
		for(Status s : values()) {
			names.append(s.getName()).append(" ");
		}
    	return names.toString();
    }

    //legacy method
    public int getId(){return this.ordinal();}
}
