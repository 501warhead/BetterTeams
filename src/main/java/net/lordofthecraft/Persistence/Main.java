package net.lordofthecraft.Persistence;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class Main extends JavaPlugin implements Listener {
    private static Main instance;
    public APIManager apiManager;

    public static Main getPlugin(){
        return instance;
    }

    public void onEnable(){
        instance = this;
        File folder = getDataFolder();
        if(!folder.exists()){
            folder.mkdirs();
        }

        PersistenceFile.init(new File(folder, "lists.yml"));



        this.getServer().getPluginManager().registerEvents(this, this);
        read();

    }

    public void onDisable(){
        save();

    }

    private void save(){

        PersistenceFile.getConfig().set("PollConfig", PersistenceConfig.getConfigList());
        PersistenceFile.save();

    }

    private void read(){
        List<Map<String,Object>> list = (List<Map<String, Object>>) PersistenceFile.getConfig().get("Persistence Config");

        if(list != null){
            PersistenceConfig.deserialize(list);
        }
        else {
            System.out.println("Nothing found!");
        }
    }


}
