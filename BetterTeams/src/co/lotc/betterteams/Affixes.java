package co.lotc.betterteams;

import org.bukkit.entity.*;
import net.lordofthecraft.arche.*;
import org.apache.commons.lang.*;
import net.lordofthecraft.arche.interfaces.*;
import org.bukkit.scoreboard.*;
import org.bukkit.*;

public class Affixes
{
    private static BoardManager boards;
    private static PersonaHandler handler;
    private final Player player;
    private final String suffix;
    private Status status;
    private GroupColor color;
    private boolean showHealth;
    private boolean mcNames;
    
    static {
        Affixes.boards = null;
        Affixes.handler = null;
    }
    
    public Affixes(final Player p) {
        super();
        this.status = null;
        this.color = null;
        if (Affixes.boards == null) {
            Affixes.boards = BetterTeams.Main.getBoardManager();
            Affixes.handler = ArcheCore.getControls().getPersonaHandler();
        }
        this.player = p;
        final Persona ps = Affixes.handler.getPersona(p);
        final String n = (ps == null) ? null : /*ps.getName()*/p.getName();
        if (ps == null || n.length() <= 16) {
            this.suffix = null;
        }
        else if (n.length() <= 20) {
            this.suffix = n.substring(16, n.length());
        }
        else {
            this.suffix = String.valueOf(n.substring(16, 20)) + "\u2026";
        }
        this.showHealth = Affixes.boards.isShowingHealth(p);
        this.mcNames = Affixes.boards.isSeeingMinecraftNames(p);
        final String name = p.getName();
        final Team t = p.getScoreboard().getTeam(name);
        if (t != null) {
            final String prefix = t.getPrefix();
            if (StringUtils.isNotEmpty(prefix)) {
                final boolean hasStatus = prefix.startsWith("[");
                if (hasStatus) {
                    final String symbol = prefix.substring(1, 4);
                    Status[] values;
                    for (int length = (values = Status.values()).length, i = 0; i < length; ++i) {
                        final Status s = values[i];
                        if (s.toString().equals(symbol)) {
                            this.status = s;
                            break;
                        }
                    }
                }
                final int offset = hasStatus ? 8 : 0;
                if (prefix.length() > offset + 1 && prefix.charAt(1) != 'o') {
                    final String colorCode = prefix.substring(offset, offset + ((prefix.length() > offset + 3 && prefix.charAt(offset + 3) == 'l') ? 4 : 2));
                    GroupColor[] values2;
                    for (int length2 = (values2 = GroupColor.values()).length, j = 0; j < length2; ++j) {
                        final GroupColor c = values2[j];
                        if (c.toString().equals(colorCode)) {
                            this.color = c;
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public String getSuffix() {
        return this.suffix;
    }
    
    public boolean isShowingHealth() {
        return this.showHealth;
    }
    
    public void setShowHealth(final boolean value) {
        this.showHealth = value;
    }
    
    public boolean isSeeingMinecraftNames() {
        return this.mcNames;
    }
    
    public void setSeeingMinecraftNames(final boolean value) {
        this.mcNames = value;
    }
    
    public Status getStatus() {
        return this.status;
    }
    
    public void setStatus(final Status status) {
        this.status = status;
    }
    
    public GroupColor getColor() {
        return this.color;
    }
    
    public void setGroupColor(final GroupColor color) {
        this.color = color;
    }
    
    public String getPrefix() {
        final StringBuilder prefix = new StringBuilder(16);
        if (this.status != null) {
            prefix.append("[" + this.status + ChatColor.RESET + "] ");
        }
        if (this.color != null) {
            prefix.append(this.color.toString());
        }
        if (prefix.length() == 0) {
            return null;
        }
        return prefix.toString();
    }
}
