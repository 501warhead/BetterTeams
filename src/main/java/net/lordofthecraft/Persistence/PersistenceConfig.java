package net.lordofthecraft.Persistence;

import java.util.*;

public class PersistenceConfig {
    private static final List<APIManager> all = new ArrayList<>();

    public static void deserialize(List<Map<String,Object>> mapp){
        for(Map<String, Object> map : mapp){
            ArrayList<String> showhealthString = (ArrayList<String>) map.get("showhealthsave");
            ArrayList<String> mcnamesString = (ArrayList<String>) map.get("mcnamessave");
            ArrayList<String> nameplatesString = (ArrayList<String>) map.get("nameplatessave");

            ArrayList<UUID> keep_showhealth = new ArrayList<>();

            for(String s: showhealthString){
                keep_showhealth.add(UUID.fromString(s));
            }

            ArrayList<UUID> remove_nameplates = new ArrayList<>();

            for(String s : nameplatesString){
                remove_nameplates.add(UUID.fromString(s));
            }

            ArrayList<UUID> show_mcnames = new ArrayList<>();

            for(String s : mcnamesString){
                show_mcnames.add(UUID.fromString(s));

            }

        }

    }
    public static Map<String, Object> serialize(APIManager am){
        Map<String, Object> map = new HashMap<>();

        ArrayList<UUID> keepshowhealth = new ArrayList<>();
        for(UUID a : am.getKeep_showhealth()){
            keepshowhealth.add(UUID.fromString(a.toString()));

        }
        map.put("showhealth", keepshowhealth);

        ArrayList<UUID> showmcnames = new ArrayList<>();

        for(UUID u : am.getKeep_mcnames()){
            showmcnames.add(UUID.fromString(u.toString()));
        }

        map.put("mcnames", showmcnames);

        ArrayList<UUID> nonameplates = new ArrayList<>();

        for(UUID u : am.getNo_nameplates()){
            nonameplates.add(UUID.fromString(u.toString()));
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

