package net.lordofthecraft.betterteams;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum PrefixTag
{
    ADMIN(ChatColor.DARK_RED + "" + ChatColor.BOLD, "A"),

	DEV		(String.valueOf(ChatColor.GOLD), 									"D"),
	MOD		(String.valueOf(ChatColor.BLUE), 									"M"),
    W		(String.valueOf(ChatColor.DARK_AQUA),								"W"),
	C		(String.valueOf(ChatColor.LIGHT_PURPLE), 							"C"),
	S		(String.valueOf(ChatColor.GREEN), 									"S");

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
			if(pt.toString().equals(substring)) return pt;
		}
		return null;
	}

}
