package net.lordofthecraft.betterteams;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.google.common.collect.HashBiMap;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import net.md_5.bungee.api.ChatColor;

public class TeamPacketListener implements Listener
{
	
	//Purpose: Set MC name to string of color codes
	//Name is reinserted with the team prefixes/suffixes
	//Why?  Because why not :D
	private final Plugin plugin;
	
	private final Map<UUID, String> playerNameMappings = HashBiMap.create(300); //how optimistic about lotcs playercount are you ;) // we have 300 cap we are making it 300 - we do get over 200 during warclaims
	private final int[] tokens = new int[4];
	
	public TeamPacketListener(final BoardManager sboards) {
		this.plugin = (Plugin)BetterTeams.Main;
		
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, PacketType.Play.Server.PLAYER_INFO) {
			public void onPacketSending(final PacketEvent event) {
				
				PacketContainer packet = event.getPacket();
				PlayerInfoAction at = packet.getPlayerInfoAction().read(0);
				
				if (at == PlayerInfoAction.ADD_PLAYER) {
					List<PlayerInfoData> pidl = packet.getPlayerInfoDataLists().read(0);
					pidl = pidl.stream()
							.map(info -> info.getProfile().getName().length() < 3? info :
									new PlayerInfoData(
									ArcheGameProfile.rewrap(info.getProfile(), 
											playerNameMappings.get(info.getProfile().getUUID()),
											info.getProfile().getUUID()), 
									info.getLatency(), 
									info.getGameMode(), 
									//display name must now be MC name so tab menu looks right
									WrappedChatComponent.fromText((Affixes.fromExistingTeams(info.getProfile().getUUID()) != null ? Affixes.fromExistingTeams(info.getProfile().getUUID()).getColor().toString() : "")+info.getProfile().getName())
									//info.getDisplayName()
									))
							.collect(Collectors.toList());

					packet.getPlayerInfoDataLists().write(0, pidl);
				}
			}
		});
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
