package co.lotc.betterteams;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum GroupColor
{
    ADMIN("ADMIN", 0, String.valueOf(ChatColor.DARK_RED) + ChatColor.BOLD, "Admin"),
    DEV("DEV", 1, String.valueOf(ChatColor.GOLD) + ChatColor.BOLD, "Dev"),
    GM("GM", 2, String.valueOf(ChatColor.BLUE) + ChatColor.BOLD, "GM"),
    HOTPINK("HotPink", 3, String.valueOf(ChatColor.LIGHT_PURPLE) + ChatColor.BOLD, "HotPink"),
    LEADET("LeadET", 4, String.valueOf(ChatColor.GREEN) + ChatColor.BOLD, "LeadET"),
    LEADFM("LeadFM", 5, String.valueOf(ChatColor.RED) + ChatColor.BOLD, "LeadFM"),
    LEADMT("LeadMT", 6, String.valueOf(ChatColor.DARK_AQUA) + ChatColor.BOLD, "LeadMT"),
    LEADAT("LeadAT", 7, String.valueOf(ChatColor.DARK_GREEN) + ChatColor.BOLD, "LeadAT"),
    LT("LT", 8, String.valueOf(ChatColor.YELLOW), "LT"),
    MT("MT", 9, String.valueOf(ChatColor.DARK_AQUA), "MT"),
    ET("ET", 10, String.valueOf(ChatColor.GREEN), "ET"),
    FM("FM", 11, String.valueOf(ChatColor.RED), "FM"),
    AT("AT", 12, String.valueOf(ChatColor.DARK_GREEN), "AT"),
    WT("WT", 13, String.valueOf(ChatColor.LIGHT_PURPLE), "WT"),
   
    VIP500("VIP500", 14, String.valueOf(ChatColor.YELLOW) + ChatColor.BOLD, "VIP500"),
    VIP300("VIP300", 15, String.valueOf(ChatColor.DARK_PURPLE) + ChatColor.BOLD, "VIP300"),
    VIP200("VIP200", 16, String.valueOf(ChatColor.DARK_PURPLE), "VIP200"),
    VIP100("VIP100", 17, String.valueOf(ChatColor.AQUA), "VIP100"),
    VIP50("VIP50", 18, String.valueOf(ChatColor.GOLD), "VIP50"),
    VIP25("VIP25", 19, String.valueOf(ChatColor.GRAY), "VIP25"),
    VIP10("VIP10", 20, String.valueOf(ChatColor.DARK_GRAY), "VIP10"), 
    NORMAL("NORMAL", 21, String.valueOf(ChatColor.WHITE), "Normal");
    
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
