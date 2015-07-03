package co.lotc.betterteams;

import net.lordofthecraft.arche.persona.*;

import org.bukkit.plugin.*;

import net.lordofthecraft.arche.*;

import com.comphenix.protocol.wrappers.*;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;

import net.lordofthecraft.arche.interfaces.*;

import com.comphenix.protocol.events.*;

import java.lang.reflect.Field;
import java.util.*;

import org.bukkit.*;
import org.bukkit.event.*;

import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;

import org.bukkit.entity.*;

import com.comphenix.protocol.*;
import com.google.common.collect.Lists;

public class TeamPacketListener implements Listener
{
	private final BoardManager boards;
	private final ArchePersonaHandler handler;
	private final Plugin plugin;
	private Field LIST_FIELD;

	public TeamPacketListener(final BoardManager sboards) {
		
		super();
		this.boards = sboards;
		this.handler = ArcheCore.getPlugin().getPersonaHandler();
		this.plugin = (Plugin)BetterTeams.Main;/*
		final Set<PacketType> packets = new HashSet<PacketType>();
		packets.add(PacketType.Play.Server.PLAYER_INFO);
		packets.add(PacketType.Play.Server.SCOREBOARD_SCORE);
		packets.add(PacketType.Play.Server.SCOREBOARD_TEAM);
		ProtocolLibrary.getProtocolManager().addPacketListener((PacketListener)new PacketAdapter(this.plugin, packets) {
			public void onPacketSending(final PacketEvent event) {
				final Player p = event.getPlayer();
				try {
					if(TeamPacketListener.this.boards.isSeeingMinecraftNames(p)) return;
				}
				
				catch (UnsupportedOperationException e) {
					this.plugin.getLogger().warning("Was sending packet to temporary player at " + p.getName());
				}
				PacketContainer packet = event.getPacket();
				final PacketType type = packet.getType();
				if (type == PacketType.Play.Server.PLAYER_INFO) {
					PlayerInfoAction at = packet.getPlayerInfoAction().read(0);
					if (at == PlayerInfoAction.ADD_PLAYER || at == PlayerInfoAction.UPDATE_DISPLAY_NAME) {
						final WrappedGameProfile profile = (WrappedGameProfile)packet.getPlayerInfoDataLists().read(0).get(0).getProfile();
						if (!profile.getName().equals("" + ChatColor.BOLD)) {
							final Persona ps = (Persona)TeamPacketListener.this.handler.getPersona(profile.getUUID());
							if (ps != null) {
								String name = ps.getName();
								if (name.length() > 16) {
									name = name.substring(0, 16);
								}
								packet.getPlayerInfoDataLists().write(0,
										Lists.newArrayList(new PlayerInfoData(ArcheGameProfile.rewrap(profile, name, profile.getUUID()), 
												packet.getPlayerInfoDataLists().read(0).get(0).getPing(), NativeGameMode.SURVIVAL, WrappedChatComponent.fromText(name))));
							}
						}
					}
				}
				else if (type == PacketType.Play.Server.SCOREBOARD_SCORE) {

					final int flag = (int)packet.getIntegers().read(0);
					if (flag != 1) {
						String name2 = (String)packet.getStrings().read(0);
						if (name2.equals(p.getName())) {
							return;
						}
						name2 = TeamPacketListener.this.getPersonaName(name2);
						if (name2 != null) {
							packet.getStrings().write(0, name2);
						}
					}
				} else {
					final int flag = (int)packet.getIntegers().read(0);
					if (flag == 1 || flag == 2) {
						return;
					}
					PacketPlayOutScoreboardTeam nmspacket = (PacketPlayOutScoreboardTeam) packet.getHandle();

					try {
						if (LIST_FIELD == null) {
							LIST_FIELD = nmspacket.getClass().getDeclaredField("g");
							LIST_FIELD.setAccessible(true);
						}

						@SuppressWarnings("unchecked")
						final ArrayList<String> names = (ArrayList<String>) LIST_FIELD.get(nmspacket);
						final ArrayList<String> newna = Lists.newArrayListWithExpectedSize(names.size());
						boolean changed = false;
						for (final String name3 : names) {
							final String pname = TeamPacketListener.this.getPersonaName(name3);
							if (pname != null) {
								newna.add(pname);
								changed = true;
							}
							else {
								newna.add(name3);
							}
						}
						if (changed) {
							LIST_FIELD.set(nmspacket, newna);
							packet = PacketContainer.fromPacket(nmspacket);
							event.setPacket(packet);
						}
					} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					}
				}
			}
		});*/
	}

	private String getPersonaName(final String playerName) {
		final Player p = Bukkit.getPlayerExact(playerName);
		if (p == null) {
			return null;
		}
		final Persona ps = (Persona)this.handler.getPersona(p);
		if (ps == null) {
			return null;
		}
		final String personaName = ps.getName();
		if (personaName.length() > 16) {
			return personaName.substring(0, 16);
		}
		return personaName;
	}
}
