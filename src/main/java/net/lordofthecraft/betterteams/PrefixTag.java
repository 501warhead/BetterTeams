package net.lordofthecraft.betterteams;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum PrefixTag
{
	
    DEV		(String.valueOf(ChatColor.GOLD 				+""+ ChatColor.BOLD), 	"T"),
    ADMIN	(String.valueOf(ChatColor.DARK_RED 			+""+ ChatColor.BOLD), 	"A"),
    	
    WD		(String.valueOf(ChatColor.AQUA), 									"WD"),
    GM		(String.valueOf(ChatColor.BLUE), 									"GM"),
    CODER	(String.valueOf(ChatColor.GOLD), 									"C"),
    FM		(String.valueOf(ChatColor.RED), 									"FM"),
    AT		(String.valueOf(ChatColor.LIGHT_PURPLE), 							"AT"),
    LT		(String.valueOf(ChatColor.GREEN), 									"LT"),
    ET		(String.valueOf(ChatColor.DARK_GREEN), 								"ET"),
    WT		(String.valueOf(ChatColor.DARK_AQUA), 								"WT"),
    MT		(String.valueOf(ChatColor.YELLOW), 									"MT");
    
    private final String tag;
    private final String color;

    PrefixTag(final String color, String tag) {
    	this.color = color;
        this.tag = tag;
    }
    
    public String toString() {
        return this.color + "[" + this.tag + "]";
    }
    
    public String getName() {
        return this.name().toLowerCase();
    }
    
    public String getTag() {
    	return tag;
    }
    
    public String getColor() {
    	return color;
    }
    
    public ChatColor getPrimaryColor() {
    	return ChatColor.getByChar(color.charAt(1));
    }
    
    public ChatColor getStyleColor() {
    	if(isStylized()) return ChatColor.getByChar(color.charAt(3));
    	return null;
    }
    
    public boolean isStylized() {
    	return color.length() == 4;
    }
    
    public static PrefixTag fromName(String name) {
    	for(PrefixTag s : PrefixTag.values()) {
    		if(s.getName().equalsIgnoreCase(name))
    			return s;
    	}
    	return null;
    }
    
    public static PrefixTag getHighest(final Player p) {
        PrefixTag[] values = values();
        for (int length = values.length, i = 0; i < length; ++i) {
            final PrefixTag c = values[i];
            if (p.hasPermission("betterteams.prefix." + c.name().toLowerCase())) {
                return c;
            }
        }
        return null;
    }

	public static PrefixTag match(String substring) {
		for(PrefixTag pt : PrefixTag.values()) {
    		if(pt.tag.equals(substring)) return pt;
    	}
    	return null;
	}

}
