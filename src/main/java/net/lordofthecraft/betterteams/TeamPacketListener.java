package net.lordofthecraft.betterteams;
import java.util.List;
import java.util.stream.Collectors;

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

public class TeamPacketListener implements Listener
{
	//Purpose: Set MC name to empty string
	//Name is reinserted with the team prefixes/suffixes
	//Why?  Because why not :D
	private final Plugin plugin;

	public TeamPacketListener(final BoardManager sboards) {
		this.plugin = (Plugin)BetterTeams.Main;

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, PacketType.Play.Server.PLAYER_INFO) {
			public void onPacketSending(final PacketEvent event) {
				
				PacketContainer packet = event.getPacket();
				PlayerInfoAction at = packet.getPlayerInfoAction().read(0);
				if (at == PlayerInfoAction.ADD_PLAYER) {
					List<PlayerInfoData> pidl = packet.getPlayerInfoDataLists().read(0);
					pidl = pidl.stream()
							.map(info -> new PlayerInfoData(
									ArcheGameProfile.rewrap(info.getProfile(), "", info.getProfile().getUUID()), 
									info.getLatency(), 
									info.getGameMode(), 
									//display name must now be MC name so tab menu looks right
									WrappedChatComponent.fromText(info.getProfile().getName()))) 
							.collect(Collectors.toList());

					packet.getPlayerInfoDataLists().write(0, pidl);
				}
			}
		});
	}
}
