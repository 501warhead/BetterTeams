package net.lordofthecraft.Persistence;

import net.lordofthecraft.betterteams.BoardManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class APIManager {
    ArrayList<UUID> keep_showhealth = new ArrayList<UUID>();
    ArrayList<UUID> no_nameplates = new ArrayList<UUID>();
    ArrayList<UUID> keep_mcnames = new ArrayList<UUID>();
    public void showhealth(Player p){
        if(!keep_showhealth.contains(p)){
            keep_showhealth.add(p.getUniqueId());
        }
        else {

        }
    }

    public void removeshowhealth(Player p){
        if(keep_showhealth.contains(p)){
            keep_showhealth.remove(p.getUniqueId());
        }
    }

    public void setnameplates(Player p){
        if(!no_nameplates.contains(p)){
            keep_mcnames.add(p.getUniqueId());
        }
    }

    public void removenameplates(Player p){
        if(no_nameplates.contains(p)){
            no_nameplates.remove(p.getUniqueId());
        }
    }

    public void mcnames(Player p){
        if(!keep_mcnames.contains(p)){
            keep_mcnames.add(p.getUniqueId());
        }
    }

    public void removemcnames(Player p){
        if(no_nameplates.contains(p)){
            keep_mcnames.remove(p.getUniqueId());
        }
    }

    public ArrayList<UUID> getKeep_mcnames() {
        return keep_mcnames;
    }

    public ArrayList<UUID> getNo_nameplates() {
        return no_nameplates;
    }

    public ArrayList<UUID> getKeep_showhealth() {
        return keep_showhealth;
    }
}
