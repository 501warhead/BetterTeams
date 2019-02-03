package net.lordofthecraft.betterteams;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum GroupColor
{
    TECH	(String.valueOf(ChatColor.RED				+""+ ChatColor.BOLD)),
    ADMIN	(String.valueOf(ChatColor.DARK_RED 			+""+ ChatColor.BOLD)),
    DEV		(String.valueOf(ChatColor.GOLD				+""+ ChatColor.BOLD)),
    MOD		(String.valueOf(ChatColor.BLUE 				+""+ ChatColor.BOLD)),
    C		(String.valueOf(ChatColor.LIGHT_PURPLE 		+""+ ChatColor.BOLD)),
   
    VIP500	(String.valueOf(ChatColor.YELLOW 			+""+ ChatColor.BOLD)),
    VIP300	(String.valueOf(ChatColor.DARK_PURPLE 		+""+ ChatColor.BOLD)),
    VIP200	(String.valueOf(ChatColor.DARK_PURPLE)							),
    VIP100	(String.valueOf(ChatColor.AQUA) 								),
    VIP50	(String.valueOf(ChatColor.GOLD) 								),
    VIP25	(String.valueOf(ChatColor.GRAY)									),
    VIP10	(String.valueOf(ChatColor.DARK_GRAY) 							), 
    NORMAL	(String.valueOf(ChatColor.WHITE) 								);
    
    private final String color;
    private final String group;

    GroupColor(final String color) {
        this.color = color;
        this.group = this.name().toLowerCase();
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
