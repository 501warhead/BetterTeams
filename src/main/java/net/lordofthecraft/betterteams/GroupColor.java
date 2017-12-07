package net.lordofthecraft.betterteams;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum GroupColor
{
    DEV		(String.valueOf(ChatColor.GOLD 				+""+ ChatColor.BOLD), 	"Dev"),
    ADMIN	(String.valueOf(ChatColor.DARK_RED 			+""+ ChatColor.BOLD), 	"Admin"),
    
    ATLEAD	(String.valueOf(ChatColor.LIGHT_PURPLE 		+""+ ChatColor.BOLD), 	"AT-Lead"),
    LTLEAD	(String.valueOf(ChatColor.GREEN 			+""+ ChatColor.BOLD), 	"LT-Lead"),
    FMLEAD	(String.valueOf(ChatColor.RED 				+""+ ChatColor.BOLD), 	"FM-Lead"),
    WTLEAD	(String.valueOf(ChatColor.DARK_AQUA 		+""+ ChatColor.BOLD), 	"WT-Lead"),
    ETLEAD	(String.valueOf(ChatColor.DARK_GREEN 		+""+ ChatColor.BOLD), 	"ET-Lead"),
    WD(String.valueOf(ChatColor.AQUA + "" + ChatColor.BOLD), "WD"),

    GM(String.valueOf(ChatColor.BLUE + "" + ChatColor.BOLD), "GM"),
    FM		(String.valueOf(ChatColor.RED), 									"FM"),
    AT		(String.valueOf(ChatColor.LIGHT_PURPLE), 							"AT"),
    LT		(String.valueOf(ChatColor.GREEN), 									"LT"),
    ET		(String.valueOf(ChatColor.DARK_GREEN), 								"ET"),
    WT		(String.valueOf(ChatColor.DARK_AQUA), 								"WT"),
    MS(String.valueOf(ChatColor.YELLOW), "MS"),
   
    VIP500	(String.valueOf(ChatColor.YELLOW 			+""+ ChatColor.BOLD), 	"VIP500"),
    VIP300	(String.valueOf(ChatColor.DARK_PURPLE 		+""+ ChatColor.BOLD), 	"VIP300"),
    VIP200	(String.valueOf(ChatColor.DARK_PURPLE),								"VIP200"),
    VIP100	(String.valueOf(ChatColor.AQUA), 									"VIP100"),
    VIP50	(String.valueOf(ChatColor.GOLD), 									"VIP50"),
    VIP25	(String.valueOf(ChatColor.GRAY), 									"VIP25"),
    VIP10	(String.valueOf(ChatColor.DARK_GRAY), 								"VIP10"), 
    NORMAL	(String.valueOf(ChatColor.WHITE), 									"Normal");
    
    private final String color;
    private final String group;

    GroupColor(final String color, final String group) {
        this.color = color;
        this.group = group;
    }
    
    public static GroupColor getHighest(final Player p) {
        GroupColor[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final GroupColor c = values[i];
            if (p.hasPermission("betterteams.tag." + c.group.toLowerCase())) {
                return c;
            }
        }
        return GroupColor.NORMAL;
    }
    
    public static GroupColor match(String chatcolors) {
    	for(GroupColor gc : GroupColor.values()) {
    		if(gc.color.equals(chatcolors)) return gc;
    	}
    	return GroupColor.NORMAL;
    }
    
    public ChatColor getPrimaryColor() {
    	return ChatColor.getByChar(color.charAt(1));
    }
    
    public ChatColor getStyleColor() {
    	if(color.length() == 4) return ChatColor.getByChar(color.charAt(3));
    	return null;
    }
    
    public boolean isStylized() {
    	return color.length() == 4;
    }
    
    public String toString() {
        return this.color;
    }
    
    public String group() {
        return this.group;
    }
}
