package net.lordofthecraft.Persistence;

import com.comphenix.protocol.PacketType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class PersistenceConfig {
    private static final List<APIManager> all = new ArrayList<>();

    public static void deserialize(List<Map<String,Object>> mapp){
        for(Map<String, Object> map : mapp){
            ArrayList<String> showhealthString = (ArrayList<String>) map.get("showhealthsave");
            ArrayList<String> mcnamesString = (ArrayList<String>) map.get("mcnamessave");
            ArrayList<String> nameplatesString = (ArrayList<String>) map.get("nameplatessave");

            ArrayList<Player> keep_showhealth = new ArrayList<>();

            for(String s: showhealthString){
                UUID t = UUID.fromString(s);
                keep_showhealth.add(Bukkit.getPlayer(t));
            }

            ArrayList<Player> remove_nameplates = new ArrayList<>();

            for(String s : nameplatesString){
                UUID u = UUID.fromString(s);
                remove_nameplates.add(Bukkit.getPlayer(u));
            }

            ArrayList<Player> show_mcnames = new ArrayList<>();

            for(String s : mcnamesString){
                UUID u = UUID.fromString(s);
                show_mcnames.add(Bukkit.getPlayer(u));

            }

        }

    }
    public static Map<String, Object> serialize(APIManager am){
        Map<String, Object> map = new HashMap<>();

        ArrayList<Player> keepshowhealth = new ArrayList<>();
        for(Player a : am.getKeep_showhealth()){
            keepshowhealth.add(a);

        }

        map.put("showhealth", keepshowhealth);

        ArrayList<Player> showmcnames = new ArrayList<>();

        for(Player u : am.getKeep_mcnames()){
            showmcnames.add(u);
        }

        map.put("mcnames", showmcnames);

        ArrayList<Player> nonameplates = new ArrayList<>();

        for(Player u : am.getNo_nameplates()){
            nonameplates.add(u);
        }

        map.put("nameplates", nonameplates);

        return map;



    }

    public static List<APIManager> getInstances() {
        return all;
    }

    public static List<Map<String, Object>> getConfigList(){
        List<Map<String, Object>> list = new ArrayList<>();
        for(APIManager a : all){
            list.add(serialize(a));
        }
        return list;
    }
}

