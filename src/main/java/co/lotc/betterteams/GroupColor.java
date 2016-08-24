package co.lotc.betterteams;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum GroupColor
{
    ADMIN("ADMIN", 0, String.valueOf(ChatColor.DARK_RED) + ChatColor.BOLD, "Admin"),
    TECHNICIAN("TECHNICIAN", 1, String.valueOf(ChatColor.GOLD) + ChatColor.BOLD, "Technician"),
    MODERATOR("MODERATOR", 2, String.valueOf(ChatColor.DARK_BLUE) + ChatColor.BOLD, "Moderator"),
    LORE("LORE", 3, String.valueOf(ChatColor.GREEN) + ChatColor.BOLD, "Lore"),
    MAT("MAT", 4, String.valueOf(ChatColor.DARK_AQUA), "MAT"),
    ET("ET", 5, String.valueOf(ChatColor.DARK_GREEN), "ET-Base"),
    FM("FM", 6, String.valueOf(ChatColor.RED), "FM"),
    AT("AT", 7, String.valueOf(ChatColor.GREEN), "AT"),
    WIKI("WIKI", 8, String.valueOf(ChatColor.YELLOW), "Wiki"),
   
    VIP500("VIP500", 9, String.valueOf(ChatColor.YELLOW) + ChatColor.BOLD, "VIP500"),
    VIP300("VIP300", 10, String.valueOf(ChatColor.DARK_PURPLE), "VIP300"),
    VIP200("VIP200", 11, String.valueOf(ChatColor.LIGHT_PURPLE), "VIP200"),
    VIP100("VIP100", 12, String.valueOf(ChatColor.AQUA), "VIP100"),
    VIP50("VIP50", 13, String.valueOf(ChatColor.GOLD), "VIP50"),
    VIP25("VIP25", 14, String.valueOf(ChatColor.GRAY), "VIP25"),
    VIP10("VIP10", 15, String.valueOf(ChatColor.DARK_GRAY), "VIP10"), 
    NORMAL("NORMAL", 16, String.valueOf(ChatColor.WHITE), "NORMAL");
    
    private final String color;
    private final String group;

    GroupColor(final String s, final int n, final String color, final String group) {
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
        return null;
    }
    
    public String toString() {
        return this.color;
    }
    
    public String group() {
        return this.group;
    }
}
