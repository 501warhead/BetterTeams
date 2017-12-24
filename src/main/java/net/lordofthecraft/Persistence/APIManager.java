package net.lordofthecraft.Persistence;

import java.util.ArrayList;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
public class APIManager {

  ArrayList<UUID> keepShowHealth = new ArrayList<>();
  ArrayList<UUID> noNameplates = new ArrayList<>();
  ArrayList<UUID> keepMCNames = new ArrayList<>();

  public void showhealth(Player p) {
    if (!keepShowHealth.contains(p.getUniqueId())) {
      keepShowHealth.add(p.getUniqueId());
    } else {
      keepShowHealth.remove(p.getUniqueId());


    }
  }

  public void setnameplates(Player p) {
    if (!noNameplates.contains(p.getUniqueId())) {
      keepMCNames.add(p.getUniqueId());
    } else {
      noNameplates.remove(p.getUniqueId());
    }
  }

  public void mcnames(Player p) {
    if (!keepMCNames.contains(p.getUniqueId())) {
      keepMCNames.add(p.getUniqueId());
    } else {
      keepMCNames.remove(p.getUniqueId());
    }
  }
}
