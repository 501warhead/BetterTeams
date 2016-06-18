package co.lotc.betterteams;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum GroupColor
{
    ADMIN("ADMIN", 0, new StringBuilder().append(ChatColor.DARK_RED).append(ChatColor.BOLD).toString(), "Admin"),
    TECHNICIAN("TECHNICIAN", 1, new StringBuilder().append(ChatColor.GOLD).append(ChatColor.BOLD).toString(), "Technician"), 
    MODERATOR("MODERATOR", 2, new StringBuilder().append(ChatColor.DARK_BLUE).append(ChatColor.BOLD).toString(), "Moderator"),
    MAT("MAT", 3, new StringBuilder().append(ChatColor.DARK_AQUA).toString(), "MAT"),
    ET("ET", 4, new StringBuilder().append(ChatColor.DARK_GREEN).toString(), "ET"),
    LORE("LORE", 5, new StringBuilder().append(ChatColor.GREEN).toString(), "Lore"),
    VIP500("VIP500", 6, new StringBuilder().append(ChatColor.YELLOW).append(ChatColor.BOLD).toString(), "VIP500"), 
    VIP300("VIP300", 7, new StringBuilder().append(ChatColor.DARK_PURPLE).toString(), "VIP300"), 
    VIP200("VIP200", 8, new StringBuilder().append(ChatColor.LIGHT_PURPLE).toString(), "VIP200"), 
    VIP100("VIP100", 9, new StringBuilder().append(ChatColor.AQUA).toString(), "VIP100"), 
    VIP50("VIP50", 10, new StringBuilder().append(ChatColor.GOLD).toString(), "VIP50"), 
    VIP25("VIP25", 11, new StringBuilder().append(ChatColor.YELLOW).toString(), "VIP25"), 
    VIP10("VIP10", 12, new StringBuilder().append(ChatColor.DARK_GRAY).toString(), "VIP10");
    
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
