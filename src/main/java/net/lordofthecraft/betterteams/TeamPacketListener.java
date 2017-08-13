package net.lordofthecraft.betterteams;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.collect.HashBiMap;

import net.md_5.bungee.api.ChatColor;

public class TeamPacketListener implements Listener
{
	
	//Purpose: Set MC name to string of color codes
	//Name is reinserted with the team prefixes/suffixes
	//Why?  Because why not :D
	private final Plugin plugin;
	
	private final Map<UUID, String> playerNameMappings = HashBiMap.create(Bukkit.getServer().getMaxPlayers()); //how optimistic about lotcs playercount are you ;) // we have 300 cap we are making it 300 - we do get over 200 during warclaims
	private final int[] tokens = new int[4];
	
	public TeamPacketListener(final BoardManager sboards) {
		this.plugin = (Plugin)BetterTeams.Main;
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, PacketType.Play.Server.PLAYER_INFO) {
			public void onPacketSending(final PacketEvent event) {
				
				PacketContainer packet = event.getPacket();
				PlayerInfoAction at = packet.getPlayerInfoAction().read(0);
				
				if (at == PlayerInfoAction.ADD_PLAYER) {
					List<PlayerInfoData> pidl = packet.getPlayerInfoDataLists().read(0);
					
					//This fixes a vanish conflict with players logging off while already unregistered
					//Vanish sends this packet before logging a player off, which causes crashes
					if(pidl.size() == 1 && Affixes.fromExistingTeams(pidl.get(0).getProfile().getUUID()) == null)
						return;
					
					pidl = pidl.stream()
							.map(info -> info.getProfile().getName().length() < 3 ? info :
									new PlayerInfoData(
									ArcheGameProfile.rewrap(info.getProfile(), 
											playerNameMappings.get(info.getProfile().getUUID()),
											info.getProfile().getUUID()), 
									info.getLatency(), 
									info.getGameMode(),
									WrappedChatComponent.fromText(getColor(info) + info.getProfile().getName())
									))
							.collect(Collectors.toList());

					packet.getPlayerInfoDataLists().write(0, pidl);
				}
			}
		});
	}
	
/*	public void updateDisplayName(GroupColor gc, UUID uuid, String mcname) {
		String displayName = gc + mcname;
		
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO);
		packet.getPlayerInfoAction().write(0, PlayerInfoAction.UPDATE_DISPLAY_NAME);
		PlayerInfoData pid = new PlayerInfoData(new WrappedGameProfile(uuid, mcname), 0, NativeGameMode.NOT_SET, WrappedChatComponent.fromText(displayName));
		
		packet.getPlayerInfoDataLists().write(0, Collections.singletonList(pid));
		
		ProtocolLibrary.getProtocolManager().broadcastServerPacket(packet);
	}*/
	
	protected String getColor(PlayerInfoData info) {
		Affixes a = Affixes.fromExistingTeams(info.getProfile().getUUID());
		if (a == null || a.getColor() == null) return ChatColor.WHITE.toString();
		else return a.getColor().toString();
	}

	public String getPlayerTeamCode(Player player) {
		return playerNameMappings.get(player.getUniqueId());
	}
	
	public void clearPlayerMapping(Player player) {
		playerNameMappings.remove(player.getUniqueId());
	}
	
	public String newPlayerNameMapping(Player player) {
		StringBuilder chatColorCode; 
		String ccc;
		//Make string of ChatColors (4 chatcolors long)
		//with 22 colors thats 22^4 > 200k logins
		do {
			chatColorCode = new StringBuilder("");
			for(int i = 0; i < tokens.length; i++) {
				int token = tokens[i];
				ChatColor c = ChatColor.values()[token];
				chatColorCode.append(c.toString());
			}
			
			//Increase token counter by 1
			int i = 0, token = 0;
			while(token == 0) {
				token = tokens[i];
				if(++token >= ChatColor.values().length) token = 0;
				tokens[i++] = token;
			} 
			
			ccc = chatColorCode.toString();
		} while (playerNameMappings.containsValue(ccc));
		ccc += ChatColor.RESET;
		playerNameMappings.put(player.getUniqueId(), ccc);
		return ccc;
	}
	
}
