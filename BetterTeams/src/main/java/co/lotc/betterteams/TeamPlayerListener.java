package co.lotc.betterteams;

import java.util.HashMap;
import java.util.UUID;

import net.lordofthecraft.arche.event.PersonaCreateEvent;
import net.lordofthecraft.arche.event.PersonaRenameEvent;
import net.lordofthecraft.arche.event.PersonaSwitchEvent;
import net.md_5.bungee.api.ChatColor;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Maps;

public class TeamPlayerListener implements Listener {

	private final BoardManager boards;
	//private final Plugin plugin;
	private HashMap<UUID, Status> statusCache;

	public TeamPlayerListener(final BoardManager sboards) {
		super();
		this.boards = sboards;
		//this.plugin = (Plugin)BetterTeams.Main;
		this.statusCache = Maps.newHashMap();
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)BetterTeams.Main, (Runnable)new BukkitRunnable() {
			public void run() {
				TeamPlayerListener.this.boards.init(p);
				if (statusCache.containsKey(p.getUniqueId())) {
					Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)BetterTeams.Main, (Runnable)new BukkitRunnable() {
						public void run() {
							Affixes a = new Affixes(p);
							a.setStatus(statusCache.get(p.getUniqueId()));
							statusCache.remove(p.getUniqueId());
							TeamPlayerListener.this.boards.apply(a);
						}
					}, 40L);
				}
			}
		}, 60L);
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent e) {
		Affixes a = new Affixes(e.getPlayer());
		if (a.getStatus() != null) {
			this.statusCache.put(e.getPlayer().getUniqueId(), a.getStatus());
			System.out.println(e.getPlayer().getName() + " " + a.getStatus().getName());
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
				Affixes pa = new Affixes(p);
				Affixes ta = new Affixes(t);
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
		Affixes a = new Affixes(e.getEntity());
		if (a.getStatus() != null) {
			a.setStatus(null);
			this.boards.apply(a);
			e.getEntity().sendMessage(ChatColor.AQUA + "Cleared your status due to death, you may not set a status for 30 minutes.");
			BetterTeams.Main.statusCooldown.put(e.getEntity().getUniqueId(), System.currentTimeMillis());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void event(final PersonaSwitchEvent e) {
		this.resend(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void event(final PersonaRenameEvent e) {
		this.resend(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void event(final PersonaCreateEvent e) {
		this.resend(e.getPlayer());
	}

	public void resend(final Player p) {/*
		new BukkitRunnable() {
			@SuppressWarnings("deprecation")
			public void run() {
				final ProtocolManager man = ProtocolLibrary.getProtocolManager();
				final List<Player> tracked = Lists.newArrayList();
				for (Player x : p.getWorld().getPlayers()) {
					if (x == p) continue;
					if (p.getLocation().distance(x.getLocation()) < 96) {
						tracked.add(x);
					}
				}
				PacketContainer remove = man.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
				remove.getIntegerArrays().write(0, new int[]{p.getEntityId()});

				for (Player x : tracked) {
					try {
						man.sendServerPacket(x, remove);
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}

				PacketContainer update = man.createPacket(PacketType.Play.Server.PLAYER_INFO);
				List<PlayerInfoData> pfile = Arrays.asList(new PlayerInfoData(
						WrappedGameProfile.fromPlayer(p),
						0,
						NativeGameMode.SURVIVAL,
						null));
				update.getPlayerInfoDataLists().write(0, pfile); 
				update.getPlayerInfoAction().write(0, PlayerInfoAction.REMOVE_PLAYER);
				man.broadcastServerPacket(update);
				update.getPlayerInfoAction().write(0, PlayerInfoAction.ADD_PLAYER);
				man.broadcastServerPacket(update);
				TeamPlayerListener.this.boards.setSuffix(p);

				WrapperPlayServerNamedEntitySpawn add = new WrapperPlayServerNamedEntitySpawn();
				add.setEntityId(p.getEntityId());
				add.setPlayerUuid(p.getUniqueId());
				add.setPosition(p.getLocation().toVector());
				add.setCurrentItem(p.getItemInHand().getTypeId());

				Location head = p.getEyeLocation();
				add.setPitch(head.getPitch());
				add.setYaw(head.getYaw());

				WrappedDataWatcher watcher = new WrappedDataWatcher();
				watcher.setObject(10, ((CraftPlayer)p).getHandle().getDataWatcher().getByte(10));
				watcher.setObject(6, (float)p.getHealth());
				add.setMetadata(watcher);

				final WrapperPlayServerNamedEntitySpawn addf = add;

				for (Player x : tracked) {
					addf.sendPacket(x);
				}


			}
		}.runTask(BetterTeams.Main);*/
	}
}
