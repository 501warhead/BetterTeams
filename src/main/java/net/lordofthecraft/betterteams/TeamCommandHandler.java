package net.lordofthecraft.betterteams;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.lordofthecraft.persistence.APIManager;
import net.lordofthecraft.persistence.APIManager.BoardType;

import static org.bukkit.Bukkit.getLogger;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TeamCommandHandler implements CommandExecutor
{
	private final BoardManager boards;

	public TeamCommandHandler() {
		super();
		this.boards = BetterTeams.Main.getBoardManager();
	}

	public static String getDurationBreakdown(long millis) {
		if (millis < 0) {
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
		if (days > 0) {
			sb.append(days);
			sb.append(" days, ");
		}
		if (hours > 0) {
			sb.append(hours);
			sb.append(" hours, ");
		}
		if (minutes > 0) {
			sb.append(minutes);
			sb.append(" minutes and ");
		}
		sb.append(seconds);
		sb.append(" seconds ");

		return (sb.toString());
	}

	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		if (!(sender instanceof Player)) {
			
			if (cmd.getName().equalsIgnoreCase("status")) {
				if (args.length > 1){
					Affixes a;
					Player t = Bukkit.getPlayer(args[1]);
					if (t == null) {
						sender.sendMessage("Player not found.");
						return true;
					} else {
						a = Affixes.fromExistingTeams(t);
						sender.sendMessage("Modifying status for " + t.getName());
					}
					Status status = Status.fromName(args[0]);
					if(status == null) sender.sendMessage(ChatColor.DARK_AQUA + "This is not apiManager valid status.");
					else if(a.getStatus() == status) sender.sendMessage(ChatColor.DARK_AQUA + "They already have the status: " + status.getName());
					else {
						a.setStatus(status);
						boards.apply(a);
					}
					
					return true;
				}
			}
			sender.sendMessage("These commands have no need to be issued by the console.");
			return true;
		}
		
		
		final Player p = (Player)sender;
		Affixes a = Affixes.fromExistingTeams(p);
		if (cmd.getName().equalsIgnoreCase("tagcolor")) {
			if (args.length == 1) {
				if (this.boards.isGhosting(p)) {
					p.sendMessage(ChatColor.DARK_AQUA + "You're apiManager ghost. Type '/appearto' to go into the light.");
				}
				else if (args[0].equalsIgnoreCase("on")) {
					final GroupColor col = GroupColor.getHighest(p);
					if (col == GroupColor.NORMAL) p.sendMessage(ChatColor.DARK_AQUA + "You do not have VIP status. Purchase VIP to receive apiManager colored tag. Type /store");
					else if (col == a.getColor()) p.sendMessage(ChatColor.DARK_AQUA + "Tag color was already at the highest possible status");
					else {
						p.sendMessage(ChatColor.AQUA + "Set your tag color to your highest possible status.");
						a.setGroupColor(col);
						this.boards.apply(a);
						p.setPlayerListName(a.getTabName());
					}
				}
				else if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("clear")) {
					if (a.getColor() == null) {
						p.sendMessage(ChatColor.DARK_AQUA + "You do not have apiManager colored tag!");
					}
					else {
						a.setGroupColor(GroupColor.NORMAL);
						this.boards.apply(a);
						p.sendMessage(ChatColor.AQUA + "Removed your colored tag successfully.");
						p.setPlayerListName(a.getTabName());
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
								p.setPlayerListName(a.getTabName());
							}
						}
					}
				}
				else {
					p.sendMessage(ChatColor.RED + "You do not have this permission!");
				}
				return true;
			}
		}else if (cmd.getName().equalsIgnoreCase("status")) {
			if (args.length >= 1) {
				if (this.boards.isGhosting(p)) {
					p.sendMessage(ChatColor.DARK_AQUA + "You're apiManager ghost. Type '/appearto' to go into the light.");
				}
				else if (args[0].equalsIgnoreCase("help")) {
					String names = Status.getAllNames();
					if (p.hasPermission("nexus.moderator")) {
						p.sendMessage(ChatColor.AQUA+"Type /status list to see apiManager totals list, or /status list [status] to see the players with that status.");
					}
					p.sendMessage(ChatColor.AQUA + "Usage: Type '/status [status]' to set apiManager status, or '/status off' to clear your status.");
					p.sendMessage(ChatColor.AQUA + "Available Statuses: " + ChatColor.RESET + names);
				}
				else if (args[0].equalsIgnoreCase("list") && p.hasPermission("nexus.moderator")) {
					if (args.length > 1) {
						Status stat = Status.valueOf(args[1].toUpperCase());
						List<Player> pl = boards.getStatusedPlayers(stat);
						String pr = "";
						StringBuilder sb = new StringBuilder();
						p.sendMessage(ChatColor.AQUA+"Players with the status of "+stat.getName()+": ");
						for (Player pp : pl) {
                            sb.append(pr);
                            sb.append(pp.getName());
                            pr = ", ";
                        }
						p.sendMessage(sb.toString());
						return true;
					}
					p.sendMessage(ChatColor.AQUA+"Listing all players with statuses...");
					boolean one = false;
					int count = 0;
					for (Status st : Status.values()) {
						count = boards.getStatusedPlayers(st).size();
						if (count > 0) {
							p.sendMessage(st.getName()+": "+count+" players.");
							one = true;
						}
					}
					if (!one) {
						p.sendMessage(ChatColor.RED+"No one is statused.");
					}
					return true;
				}
				else {
					Player t = null;
					if (args.length > 1 && p.hasPermission("nexus.moderator")){
						t = Bukkit.getPlayer(args[1]);
						if (t == null) {
							p.sendMessage("Player not found.");
							return true;
						} else {
							a = Affixes.fromExistingTeams(t);
							p.sendMessage("Modifying status for " + t.getName());
						}
					}

                    if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("clear") || args[0].equalsIgnoreCase("none") || args[0].equalsIgnoreCase("remove")) {
						if (a.getStatus() == null) {
							p.sendMessage(ChatColor.DARK_AQUA + "No status to clear");
						} else {
							a.setStatus(null);
							this.boards.apply(a);
							p.sendMessage(ChatColor.AQUA + "Cleared your status");
						}
						return true;
					}

					if ((t == null) && !p.isOp() && BetterTeams.Main.statusCooldown.containsKey(p.getUniqueId())) {
						int COOLDOWN = 1800000;
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
							} else {
								a.setStatus(c2);
								this.boards.apply(a);
								p.sendMessage(ChatColor.AQUA + "Successfully set your status to: " + c2.getName());
								BetterTeams.Main.statusCooldown.put(p.getUniqueId(), System.currentTimeMillis());
							}
							return true;
						}
					}
					p.sendMessage(ChatColor.DARK_AQUA + "This is not apiManager valid status.");
				}
				return true;
			}		
		}else if (cmd.getName().equalsIgnoreCase("appearto")) {
			if (args.length == 0) {
				if (this.boards.isGhosting(p)) {
					this.boards.removeGhost(p);
				}
				if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 2400, 5, true, false), true);
				}
				p.sendMessage(ChatColor.AQUA + "You dissappear into thin air, perpetuating the mystery.");
				return true;
			}else if (args.length == 1) {
				final Player target = Bukkit.getServer().getPlayer(args[0]);
				if (target != null && target.getLocation().getWorld() == p.getLocation().getWorld() && target.getLocation().distance(p.getLocation()) <= 90.0) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 24000, 5, true), true);
					if (this.boards.isGhosting(p)) {
						this.boards.removeGhost(p);
					}
					
					this.boards.addGhost(p, target);
					p.sendMessage(String.valueOf(ChatColor.AQUA) + ChatColor.ITALIC + "wooohoooohooo!");
				}
				else {
					p.sendMessage(ChatColor.DARK_AQUA + "That player is not in reach!");
				}
				return true;
			}
		} else {
			if (cmd.getName().equalsIgnoreCase("showhealth")) {
				boolean isNowShowingHealth = this.boards.toggleShowingHealth(p);
				if (isNowShowingHealth) {
					p.sendMessage(ChatColor.AQUA + "You are now seeing players' health.");
				}
				else {
					p.sendMessage(ChatColor.AQUA + "You are no longer seeing players' health.");
				}
				BetterTeams.apiManager.persist(p, isNowShowingHealth, BoardType.HEALTH);
				return true;
			}
			
			if (cmd.getName().equalsIgnoreCase("showrpnames")) {
				boolean isNowShowingRPNames = boards.toggleShowingRPNames(p);

				if (isNowShowingRPNames) {
					p.sendMessage(ChatColor.AQUA + "You are now seeing Roleplay names.");
				}
				else {
					p.sendMessage(ChatColor.AQUA + "You are no longer seeing Roleplay names.");
				}
				BetterTeams.apiManager.persist(p, !isNowShowingRPNames, BoardType.MCNAMES);
				return true;
			}
			
			if (cmd.getName().equalsIgnoreCase("hidenameplates")) {
				boolean isNowHidingNameplates = boards.toggleHideNameplates(p);
				if (isNowHidingNameplates) {
					p.sendMessage(ChatColor.AQUA + "Nameplates are now hidden");
				}
				else {
					p.sendMessage(ChatColor.AQUA + "Nameplates are no longer hidden");
				}
				BetterTeams.apiManager.persist(p, isNowHidingNameplates, BoardType.NAMEPLATES);
				return true;
			}
			
			if (cmd.getName().equalsIgnoreCase("affixes")) {
				if (args.length == 0) {
					return false;
				}
				final Player target = Bukkit.getServer().getPlayer(args[0]);
				if (target != null) {
					Affixes aff = Affixes.fromExistingTeams(target);
					p.sendMessage("Name: " + aff.getPlayer().getName());
					p.sendMessage("Color: " + aff.getColor() == null ? "null" : aff.getColor().toString());
					p.sendMessage("Tab Name: " + aff.getTabName());
					p.sendMessage("Status: " + aff.getStatus() == null ? "null" : aff.getStatus().toString());
					p.sendMessage("Prefix MC: " + aff.getPrefixMC());
					p.sendMessage("Suffix MC: " + aff.getSuffixMC());
					p.sendMessage("Prefix RP: " + aff.getPrefixRP());
					p.sendMessage("Prefix RP: " + aff.getSuffixRP());
					p.sendMessage("Name Mapping: " + BetterTeams.packetListener.getPlayerTeamCode(aff.getPlayer()));	
					p.sendMessage("Bukkit Custom: " + aff.getPlayer().getCustomName());
					p.sendMessage("Bukkit Display: " + aff.getPlayer().getDisplayName());
					p.sendMessage("Bukkit List: " + aff.getPlayer().getPlayerListName());
					return true;
				}
				
			}
		}
		return false;
	}
}
