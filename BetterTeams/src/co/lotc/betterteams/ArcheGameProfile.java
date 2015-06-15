package co.lotc.betterteams;

import com.comphenix.protocol.wrappers.*;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;

import java.util.*;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class ArcheGameProfile extends WrappedGameProfile
{
    @SuppressWarnings("deprecation")
	private ArcheGameProfile(final String arg0, final String arg1) {
        super(arg0, arg1);
    }
    
    public static WrappedGameProfile rewrap(final WrappedGameProfile profile, final String name, final UUID uuid) {
        final BetterGameProfile better = new BetterGameProfile((GameProfile)profile.getHandle(), name, uuid);
        return WrappedGameProfile.fromHandle((Object)better);
    }
    
    private static class BetterGameProfile extends GameProfile
    {
        private final GameProfile parent;
        
        private BetterGameProfile(final GameProfile parent, final String name, final UUID uuid) {
            super(uuid, name);
            this.parent = parent;
        }
        
        public PropertyMap getProperties() {
            return this.parent.getProperties();
        }
        
        public String toString() {
            return new ToStringBuilder((Object)this).append("id", (Object)this.getId()).append("name", (Object)this.getName()).append("properties", (Object)this.parent.getProperties()).append("legacy", this.parent.isLegacy()).toString();
        }
    }
}
