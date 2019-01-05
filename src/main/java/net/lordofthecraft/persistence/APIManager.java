package net.lordofthecraft.persistence;

import java.util.ArrayList;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
public class APIManager {

    @Getter @Setter ArrayList<UUID> keepShowHealth = new ArrayList<>();
    @Getter @Setter ArrayList<UUID> noNameplates = new ArrayList<>();
    @Getter @Setter ArrayList<UUID> keepMCNames = new ArrayList<>();
    
	public void persist(Player p, boolean active, BoardType board) {
		
		if (board == null) return;
		
		ArrayList<UUID> list = null;
		
		switch (board) {
		case HEALTH:
			list = this.keepShowHealth;
			break;
		case NAMEPLATES:
			list = this.noNameplates;
			break;
		case MCNAMES:
			list = this.keepMCNames;
			break;
		}
		
		boolean contains = list.contains(p.getUniqueId());
		
		if (active) {
			if (!contains) list.add(p.getUniqueId());
		} else {
			if (contains) list.remove(p.getUniqueId());
		}
		
		//p.sendMessage("Updating: " + board.toString() + ", previous: " + contains + ", now: " + active);
		
	}
	
	public enum BoardType {
		HEALTH,
		MCNAMES,
		NAMEPLATES;
	}

	public boolean showMCNames(Player p) {
		return this.getKeepMCNames().contains(p.getUniqueId());
	}
	
	public boolean hideNameplates(Player p) {
		return this.getNoNameplates().contains(p.getUniqueId());
	}
	
	public boolean showHealth(Player p) {
		return this.getKeepShowHealth().contains(p.getUniqueId());
	}

}
