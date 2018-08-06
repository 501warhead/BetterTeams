package net.lordofthecraft.betterteams;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.google.common.collect.HashBiMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class TeamPacketListener implements Listener
{
	
	//Purpose: Set MC name to string of color codes
	//Name is reinserted with the team prefixes/suffixes
	//Why?  Because why not :D
	private final BetterTeams plugin;
	
	private final Map<UUID, String> playerNameMappings = HashBiMap.create(200); 
	private final int[] tokens = new int[4];
	
	public TeamPacketListener(final BoardManager sboards) {
		this.plugin = BetterTeams.Main;
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, PacketType.Play.Server.PLAYER_INFO) {
			public void onPacketSending(final PacketEvent event) {
				
				PacketContainer packet = event.getPacket();
				PlayerInfoAction at = packet.getPlayerInfoAction().read(0);
				
				if (at == PlayerInfoAction.ADD_PLAYER) {
					List<PlayerInfoData> pidl = packet.getPlayerInfoDataLists().read(0);
					
					//This fixes apiManager vanish conflict with players logging off while already unregistered
					//Vanish sends this packet before logging apiManager player off, which causes crashes
					if(pidl.size() == 1 && !BetterTeams.Main.getBoardManager().hasTeam(pidl.get(0).getProfile().getName()))
						return;
					
					pidl = pidl.stream()
							.map(info -> info.getProfile().getName().length() < 3 ? info :
									new PlayerInfoData(
									ArcheGameProfile.rewrap(info.getProfile(), 
											playerNameMappings.get(info.getProfile().getUUID()),
											info.getProfile().getUUID()), 
									info.getLatency(), 
									info.getGameMode(),
									info.getDisplayName()
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
