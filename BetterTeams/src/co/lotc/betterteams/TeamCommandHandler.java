package co.lotc.betterteams;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.potion.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.*;
import org.bukkit.entity.*;

import com.comphenix.packetwrapper.WrapperPlayServerNamedEntitySpawn;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.*;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TeamCommandHandler implements CommandExecutor
{
	private final BoardManager boards;

	private final int COOLDOWN = 1800000;
	
	public TeamCommandHandler() {
		super();
		this.boards = BetterTeams.Main.getBoardManager();
	}

	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("These commands have no need to be issued by the console.");
			return true;
		}
		final Player p = (Player)sender;
		Affixes a = new Affixes(p);
		if (cmd.getName().equalsIgnoreCase("tagcolor")) {
			if (args.length == 1) {
				if (this.boards.isGhosting(p)) {
					p.sendMessage(ChatColor.DARK_AQUA + "You're a ghost. Type '/appearto' to go into the light.");
				}
				else if (args[0].equalsIgnoreCase("on")) {
					final GroupColor col = GroupColor.getHighest(p);
					if (col != null) {
						p.sendMessage(ChatColor.DARK_AQUA + "You do not have VIP status. DONATE to receive a colored tag.");
					}
					else if (col == a.getColor()) {
						p.sendMessage(ChatColor.DARK_AQUA + "Tag color was already at the highest possible status");
					}
					else {
						p.sendMessage(ChatColor.AQUA + "Set your tag color to your highest possible status.");
						a.setGroupColor(col);
						this.boards.apply(a);
					}
				}
				else if (args[0].equalsIgnoreCase("off")) {
					if (a.getColor() == null) {
						p.sendMessage(ChatColor.DARK_AQUA + "You do not have a colored tag!");
					}
					else {
						a.setGroupColor(null);
						this.boards.apply(a);
						p.sendMessage(ChatColor.AQUA + "Removed your donor tag successfully.");
					}
				}
				else if (sender.hasPermission("betterteams.tag." + args[0].toLowerCase())) {
					GroupColor[] values;
					for (int length = (values = GroupColor.values()).length, i = 0; i < length; ++i) {
						final GroupColor c = values[i];
						if (c.group().equalsIgnoreCase(args[0])) {
							p.sendMessage(ChatColor.AQUA + "Your tag color now matches the group: " + args[0]);
							if (c != a.getColor()) {
								a.setGroupColor(c);
								this.boards.apply(a);
							}
						}
					}
				}
				else {
					p.sendMessage(ChatColor.RED + "You do not have this permission!");
				}
				return true;
			}
		}
		else if (cmd.getName().equalsIgnoreCase("status")) {
			if (args.length >= 1) {
				if (this.boards.isGhosting(p)) {
					p.sendMessage(ChatColor.DARK_AQUA + "You're a ghost. Type '/appearto' to go into the light.");
				}
				else if (args[0].equalsIgnoreCase("help")) {
					final StringBuilder names = new StringBuilder(75);
					Status[] values2;
					for (int length2 = (values2 = Status.values()).length, j = 0; j < length2; ++j) {
						final Status s = values2[j];
						names.append(String.valueOf(s.getName()) + " ");
					}
					p.sendMessage(ChatColor.AQUA + "Usage: Type '/status [status]' to set a status, or '/status off' to clear your status.");
					p.sendMessage(ChatColor.AQUA + "Available Statuses: " + ChatColor.RESET + (Object)names);
				}
				else {
					Player t = null;
					if (args.length > 1 && p.isOp()){
						t = Bukkit.getPlayer(args[1]);
						if (t == null) {
							p.sendMessage("Player not found.");
							return true;
						} else {
							a = new Affixes(t);
							p.sendMessage("Modifying status for " + t.getName());
						}
					}
					if (args[0].equalsIgnoreCase("off")) {
						if (a.getStatus() == null) {
							p.sendMessage(ChatColor.DARK_AQUA + "No status to clear");
						}
						else {
							a.setStatus(null);
							this.boards.apply(a);
							p.sendMessage(ChatColor.AQUA + "Cleared your status");
						}
						return true;
					}
					
					if ((t == null) && BetterTeams.Main.statusCooldown.containsKey(p.getUniqueId())) {
						if ((System.currentTimeMillis() - BetterTeams.Main.statusCooldown.get(p.getUniqueId())) > COOLDOWN) {
							BetterTeams.Main.statusCooldown.remove(p.getUniqueId());
						} else {
							p.sendMessage(ChatColor.YELLOW + "You have recently changed your status. Please wait"
									+ getDurationBreakdown(COOLDOWN - ((System.currentTimeMillis() - BetterTeams.Main.statusCooldown.get(p.getUniqueId())))) + "before setting it again.");
							return true;
						}
					}

					Status[] values3;
					for (int length3 = (values3 = Status.values()).length, k = 0; k < length3; ++k) {
						final Status c2 = values3[k];
						if (args[0].equalsIgnoreCase(c2.getName())) {
							if (a.getStatus() == c2) {
								p.sendMessage(ChatColor.DARK_AQUA + "You already have the status: " + c2.getName());
							}
							else {
								a.setStatus(c2);
								this.boards.apply(a);
								p.sendMessage(ChatColor.AQUA + "Successfully set your status to: " + c2.getName());
								BetterTeams.Main.statusCooldown.put(p.getUniqueId(), System.currentTimeMillis());
							}
							return true;
						}
					}
					p.sendMessage(ChatColor.DARK_AQUA + "This is not a valid status.");
				}
				return true;
			}
		}
		else if (cmd.getName().equalsIgnoreCase("appearto")) {
			if (args.length == 0) {
				if (this.boards.isGhosting(p)) {
					this.boards.removeGhost(p);
					this.boards.init(p);
				}
				if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2400, 5, true), true);
				}
				p.sendMessage(ChatColor.AQUA + "You dissappear into thin air, perpetuating the mystery.");
				return true;
			}
			if (args.length == 1) {
				if (!args[0].equalsIgnoreCase("all")) {
					final Player target = Bukkit.getServer().getPlayer(args[0]);
					if (target != null && target.getLocation().getWorld() == p.getLocation().getWorld() && target.getLocation().distance(p.getLocation()) <= 90.0) {
						p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 24000, 5, true), true);
						if (this.boards.isGhosting(p)) {
							this.boards.removeGhost(p);
						}
						if (!this.boards.isSeeingMinecraftNames(p)) {
							this.toggleShowMcNames(p, a);
						}
						if (!this.boards.isSeeingMinecraftNames(target)) {
							this.toggleShowMcNames(target, new Affixes(target));
						}
						this.boards.deleteTeams(p);
						this.boards.addGhost(p, target);
						p.sendMessage(new StringBuilder().append(ChatColor.AQUA).append(ChatColor.ITALIC).append("wooohoooohooo!").toString());
					}
					else {
						p.sendMessage(ChatColor.DARK_AQUA + "That player is not in reach!");
					}
				}
				return true;
			}
			if (args.length == 0) {
				p.sendMessage(ChatColor.DARK_AQUA + "You have no need to do this.");
				return true;
			}
		}
		else {
			if (cmd.getName().equalsIgnoreCase("showhealth")) {
				final boolean toggle = !a.isShowingHealth();
				if (toggle) {
					p.sendMessage(ChatColor.AQUA + "You are now seeing players' health.");
				}
				else {
					p.sendMessage(ChatColor.AQUA + "You are no longer seeing players' health.");
				}
				this.boards.toggleShowingHealth(p);
				if (toggle) {
					for (Player x : Bukkit.getOnlinePlayers()) {
						final double h = Math.min(x.getHealth(), x.getMaxHealth());
						x.setHealth(h);
					}
				}
				return true;
			}
			if (cmd.getName().equalsIgnoreCase("showmcnames")) {
				if (this.boards.isGhosting(p) || this.boards.isGhosted(p)) {
					p.sendMessage(ChatColor.DARK_AQUA + "An Otherwordly entity prevents you from doing this.");
				}
				else {
					final boolean toggle = this.toggleShowMcNames(p, a);
					if (toggle) {
						p.sendMessage(ChatColor.AQUA + "You are now seeing Minecraft names");
					}
					else {
						p.sendMessage(ChatColor.AQUA + "You are now seeing character names");
					}
				}
				return true;
			}
		}
		return false;
	}

	public static String getDurationBreakdown(long millis)
	{
		if(millis < 0)
		{
			throw new IllegalArgumentException("Duration must be greater than zero!");
		}

		long days = TimeUnit.MILLISECONDS.toDays(millis);
		millis -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

		StringBuilder sb = new StringBuilder();
		sb.append(" ");
		if (days > 0){ sb.append(days);
		sb.append(" days, ");}
		if (hours > 0) {sb.append(hours);
		sb.append(" hours, ");}
		if (minutes > 0){ sb.append(minutes);
		sb.append(" minutes and ");}
		sb.append(seconds);
		sb.append(" seconds ");

		return(sb.toString());
	}



	@SuppressWarnings("deprecation")
	private boolean toggleShowMcNames(final Player p, final Affixes a) {
		return true;
		/*
		final boolean isSwitchingToMcNames = !a.isSeeingMinecraftNames();
		new BukkitRunnable() {
			public void run() {

				final ProtocolManager man = ProtocolLibrary.getProtocolManager();
				final List<Player> tracked = Lists.newArrayList();
				for (Player x : p.getWorld().getPlayers()) {
					if (x == p) continue;
					if (p.getLocation().distance(x.getLocation()) < 96) {

						tracked.add(x);
					}
				}
				List<Integer> toremove = Lists.newArrayList();
				for (Player x : tracked) {
					toremove.add(x.getEntityId());
				}
				PacketContainer remove = man.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
				remove.getIntegerArrays().write(0, ArrayUtils.toPrimitive(toremove.toArray(new Integer[toremove.size()])));
				try {
					man.sendServerPacket(p, remove);
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				boards.toggleShowingNames(p);
				WrapperPlayServerPlayerInfo update = new WrapperPlayServerPlayerInfo();
				List<PlayerInfoData> pfile = Lists.newArrayList();
				for (final Player x : Bukkit.getOnlinePlayers()) {
					pfile.add(new PlayerInfoData(
							WrappedGameProfile.fromPlayer(x),
							0,
							NativeGameMode.SURVIVAL,
							null));
					update.setData(pfile);
				}
				update.setAction(PlayerInfoAction.REMOVE_PLAYER);
				update.sendPacket(p);
				update.setAction(PlayerInfoAction.ADD_PLAYER);
				update.sendPacket(p);
				for (Player x : tracked) {
					WrapperPlayServerNamedEntitySpawn add = new WrapperPlayServerNamedEntitySpawn();
					add.setEntityId(x.getEntityId());
					add.setPlayerUuid(x.getUniqueId());
					add.setPosition(x.getLocation().toVector());
					add.setCurrentItem(x.getItemInHand().getTypeId());

					Location head = x.getEyeLocation();
					add.setPitch(head.getPitch());
					add.setYaw(head.getYaw());

					WrappedDataWatcher watcher = new WrappedDataWatcher();
					watcher.setObject(10, ((CraftPlayer)x).getHandle().getDataWatcher().getByte(10));
					watcher.setObject(6, (float)x.getHealth());
					add.setMetadata(watcher);

					add.sendPacket(p);

				}

			}
		}.runTask(BetterTeams.Main);
		return isSwitchingToMcNames;*/
	}

}
