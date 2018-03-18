package net.lordofthecraft.betterteams;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.collect.Maps;

import net.lordofthecraft.arche.event.persona.PersonaCreateEvent;
import net.lordofthecraft.arche.event.persona.PersonaRenameEvent;
import net.lordofthecraft.arche.event.persona.PersonaSwitchEvent;
import net.md_5.bungee.api.ChatColor;

public class TeamPlayerListener implements Listener {

	private final BoardManager boards;
	private HashMap<UUID, Status> statusCache;

	public TeamPlayerListener(final BoardManager sboards) {
		this.boards = sboards;
		this.statusCache = Maps.newHashMap();
	}
	
	@EventHandler
	public void onJoin(final PlayerJoinEvent e) {
		final Player p = e.getPlayer();
        Status cachedStatus = statusCache.get(p.getUniqueId());
        BetterTeams.packetListener.newPlayerNameMapping(p);
        Affixes a = Affixes.onJoin(p, cachedStatus);
        statusCache.remove(p.getUniqueId());
        boards.updateHealth(p, p.getHealth());
        p.setPlayerListName(a.getTabName());
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent e) {
		Affixes a = Affixes.fromExistingTeams(e.getPlayer());
		
		if (a.getStatus() != null) {
			this.statusCache.put(e.getPlayer().getUniqueId(), a.getStatus());
		}
		
		this.boards.close(e.getPlayer());
		BetterTeams.packetListener.clearPlayerMapping(e.getPlayer());
	}

	@EventHandler (priority = EventPriority.LOW)
	public void onDamage(EntityDamageByEntityEvent e) {
		Player p = null;
		Player t = null;
		if (e.getEntity() instanceof Player) {
			t = (Player) e.getEntity();
			if (e.getDamager() instanceof Player) {
				p = (Player) e.getDamager();
			} else if (e.getDamager() instanceof Arrow) {
				if (((Arrow)e.getDamager()).getShooter() instanceof Player) {
					p = (Player) ((Arrow)e.getDamager()).getShooter();
				}
			}
			if (p != null) {
				Affixes pa = Affixes.fromExistingTeams(p);
				Affixes ta = Affixes.fromExistingTeams(t);
				if (pa.getStatus() != null && ta.getStatus() != null) {
					if (pa.getStatus() == ta.getStatus()) {
						e.setCancelled(true);
						p.sendMessage(ChatColor.RED + "You may not damage apiManager player with the same status as yourself.");
					}
				}
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Affixes a = Affixes.fromExistingTeams(e.getEntity());
		if (a.getStatus() != null) {
			a.setStatus(null);
			this.boards.apply(a);
			e.getEntity().sendMessage(ChatColor.AQUA + "Cleared your status due to death.");
		}
		BetterTeams.Main.statusCooldown.remove(e.getEntity().getUniqueId());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void event(final EntityDamageEvent e) {
		updateHealthLater(e);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void event(final EntityRegainHealthEvent e) {
		updateHealthLater(e);
	}
	
	private void updateHealthLater(EntityEvent e) {
		if(e.getEntityType() == EntityType.PLAYER) {
			final Player p = (Player) e.getEntity();
			Bukkit.getScheduler().scheduleSyncDelayedTask(BetterTeams.Main, () -> {
				double hp = p.getHealth();
				boards.updateHealth(p, hp);
	        });
		}
	}


	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void event(final PersonaSwitchEvent e) {
		this.resend(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void event(final PersonaRenameEvent e) {
		if(e.getPlayer() != null) {
			this.resend(e.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void event(final PersonaCreateEvent e) {
		if(e.getPlayer() != null) {
			this.resend(e.getPlayer());
		}
		
	}
	
	private void resend(Player p) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(BetterTeams.Main, () -> {
			Affixes a = Affixes.fromExistingTeams(p);
			boards.apply(a);
        }); //This delay is necessary

	}

}
