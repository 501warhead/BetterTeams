package net.lordofthecraft.Persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
public class APIManager {

    @Getter @Setter ArrayList<UUID> keepShowHealth = new ArrayList<>();
    @Getter @Setter ArrayList<UUID> noNameplates = new ArrayList<>();
    @Getter @Setter ArrayList<UUID> keepMCNames = new ArrayList<>();

}
