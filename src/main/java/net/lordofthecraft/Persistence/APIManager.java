package net.lordofthecraft.Persistence;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class APIManager {
    ArrayList<Player> keep_showhealth = new ArrayList<>();
    ArrayList<Player> no_nameplates = new ArrayList<>();
    ArrayList<Player> keep_mcnames = new ArrayList<>();
    public void showhealth(Player p){
        if(!keep_showhealth.contains(p)){
            keep_showhealth.add(p);
        }
        else {
            keep_showhealth.remove(p.getUniqueId());
            return;

        }
    }

    public void setnameplates(Player p){
        if(!no_nameplates.contains(p)){
            keep_mcnames.add(p);
        }
        else {
            no_nameplates.remove(p.getUniqueId());
        }
    }

    public void mcnames(Player p){
        if(!keep_mcnames.contains(p)){
            keep_mcnames.add(p);
        }
        else {
            keep_mcnames.remove(p.getUniqueId());
        }
    }
    public ArrayList<Player> getKeep_mcnames() {
        return keep_mcnames;
    }

    public ArrayList<Player> getNo_nameplates() {
        return no_nameplates;
    }

    public ArrayList<Player> getKeep_showhealth() {
        return keep_showhealth;
    }
}
