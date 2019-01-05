package net.lordofthecraft.betterteams;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum GroupColor
{
    VIP500(ChatColor.YELLOW+""+ ChatColor.BOLD,"VIP500"),
    VIP300(ChatColor.DARK_PURPLE+""+ ChatColor.BOLD,"VIP300"),
    VIP200(""+(ChatColor.DARK_PURPLE),"VIP200"),
    VIP100(""+(ChatColor.AQUA),"VIP100"),
    VIP50	(""+(ChatColor.GOLD),"VIP50"),
    VIP25	(""+(ChatColor.GRAY),"VIP25"),
    VIP10	(""+(ChatColor.DARK_GRAY),"VIP10"),
    NORMAL(""+ChatColor.WHITE,"Normal");
    
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
    
    @Override
		public String toString() {
        return this.color;
    }
    
    public String group() {
        return this.group;
    }
}
