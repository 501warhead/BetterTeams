package co.lotc.betterteams;

import org.bukkit.scheduler.*;
import org.bukkit.scoreboard.*;
import org.bukkit.potion.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import java.util.*;

class GhostRunnable extends BukkitRunnable
{
    private final Scoreboard board;
    
    public GhostRunnable(final Scoreboard board) {
        super();
        this.board = board;
    }
    
    public void run() {
        final Set<Team> teams = (Set<Team>)this.board.getTeams();
        if (this.board.getTeams().isEmpty()) {
            final int id = BetterTeams.ghostTaskId;
            BetterTeams.ghostTaskId = -1;
            Bukkit.getScheduler().cancelTask(id);
        }
        else {
            for (final Team t : teams) {
                final Player ghost = Bukkit.getServer().getPlayer(t.getName());
                if (ghost == null || !ghost.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                    t.unregister();
                }
                else {
                    for (final OfflinePlayer op : new ArrayList<OfflinePlayer>(t.getPlayers())) {
                        t.removePlayer(op);
                        if (op.getPlayer() != null) {
                            BetterTeams.Main.getBoardManager().init(op.getPlayer());
                        }
                    }
                    t.addPlayer((OfflinePlayer)ghost);
                    final List<Entity> ents = (List<Entity>)ghost.getNearbyEntities(10.0, 8.0, 10.0);
                    for (final Entity ent : ents) {
                        if (ent instanceof Player) {
                            final Player p = (Player)ent;
                            if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                continue;
                            }
                            t.addPlayer((OfflinePlayer)p);
                            p.setScoreboard(this.board);
                        }
                    }
                }
            }
        }
    }
}
