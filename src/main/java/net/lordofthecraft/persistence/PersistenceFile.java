package net.lordofthecraft.persistence;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PersistenceFile {

    static FileConfiguration fg;
    private static File file;

    public static void init(File file){
        if(file == null){
            throw new IllegalStateException("file cannot be null!");
        }
        if(file.isDirectory()){
            throw new IllegalStateException("file can't be a directory!");
        }
        if(!file.exists()) {
            try{
                file.createNewFile();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        PersistenceFile.file = file;

        fg = YamlConfiguration.loadConfiguration(file);
        fg.options().header("Persistence File");
        fg.options().copyHeader();
        fg.options().copyDefaults(true);
        save();

    }

    public static void save(){
        try{
            fg.save(file);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static FileConfiguration getConfig(){
        return fg;
    }

}