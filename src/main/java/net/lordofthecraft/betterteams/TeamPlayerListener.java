package net.lordofthecraft.betterteams;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.collect.Maps;

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
		Bukkit.getScheduler().scheduleSyncDelayedTask(BetterTeams.Main, () -> {
            Status cachedStatus = statusCache.get(p.getUniqueId());
            Affixes.onJoin(p, cachedStatus);
            statusCache.remove(p.getUniqueId());
        }, 60L);
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent e) {
		Affixes a = Affixes.fromExistingTeams(e.getPlayer(), true, false);
		
		if (a.getStatus() != null) {
			this.statusCache.put(e.getPlayer().getUniqueId(), a.getStatus());
		}
		
		this.boards.close(e.getPlayer());
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
				Affixes pa = Affixes.fromExistingTeams(p, true, false);
				Affixes ta = Affixes.fromExistingTeams(t, true, false);
				if (pa.getStatus() != null && ta.getStatus() != null) {
					if (pa.getStatus() == ta.getStatus()) {
						e.setCancelled(true);
						p.sendMessage(ChatColor.RED + "You may not damage a player with the same status as yourself.");
					}
				}
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		Affixes a = Affixes.fromExistingTeams(e.getEntity(), true, true);
		if (a.getStatus() != null) {
			a.setStatus(null);
			this.boards.apply(a);
			e.getEntity().sendMessage(ChatColor.AQUA + "Cleared your status due to death.");
		}
		BetterTeams.Main.statusCooldown.remove(e.getEntity().getUniqueId());
	}
}
