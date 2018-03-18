package net.lordofthecraft.persistence;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Getter @Setter
public class PersistenceConfig {
    private static final List<APIManager> all = new ArrayList<>();
    
    public static Map<String, ArrayList<UUID>> serialize(APIManager am){
        Map<String, ArrayList<UUID>> map = new HashMap<>();

        ArrayList<UUID> keepshowhealth = new ArrayList<>(am.keepShowHealth);
        map.put("showhealth", keepshowhealth);

        ArrayList<UUID> showmcnames = new ArrayList<UUID>(am.keepMCNames);
        map.put("mcnames", showmcnames);

        ArrayList<UUID> nonameplates = new ArrayList<UUID>(am.noNameplates);
        map.put("nameplates", nonameplates);

        return map;



    }

    public static List<APIManager> getInstances() {
        return all;
    }

    public static List<Map<String, ArrayList<UUID>>> getConfigList(){
        List<Map<String, ArrayList<UUID>>> list = new ArrayList<>();
        for(APIManager a : all){
            list.add(serialize(a));
        }
        return list;
    }
}

