package co.lotc.betterteams;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class GhostRunnable extends BukkitRunnable
{
    private final Scoreboard board;
    
    public GhostRunnable(final Scoreboard board) {
        super();
        this.board = board;
    }
    
    @SuppressWarnings("deprecation")
	public void run() {
        final Set<Team> teams = this.board.getTeams();
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
                    t.addPlayer(ghost);
                    final List<Entity> ents = ghost.getNearbyEntities(10.0, 8.0, 10.0);
                    for (final Entity ent : ents) {
                        if (ent instanceof Player) {
                            final Player p = (Player)ent;
                            if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                continue;
                            }
                            t.addPlayer(p);
                            p.setScoreboard(this.board);
                        }
                    }
                }
            }
        }
    }
}
