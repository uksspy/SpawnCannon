package me.uksspy.SpawnCannon.config;

import java.util.ArrayList;
import java.util.List;

import me.uksspy.SpawnCannon.SpawnCannon;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class Config {

    private FileConfiguration config;

    public World world;
    public Location center;
    public int range;
    public int height;
    public String launchmsg;
    public String nopermmsg;
    public boolean particleeffects;
    public List<ConfigSound> launchSounds = new ArrayList<>();
    public List<ConfigSound> landSounds = new ArrayList<>();

    public List<Location> launchblocks = new ArrayList<>();

    public static String replaceLaunchStr(String str, Player player, Location loc){
        str = str.replace("%x", ""+loc.getBlockX());
        str = str.replace("%y", ""+loc.getBlockY());
        str = str.replace("%z", ""+loc.getBlockZ());
        str = str.replace("%player", player.getName());

        return str;
    }

    public Config(SpawnCannon plugin){
        config = plugin.getConfig();
        loadConfig();
    }

    private void loadConfig(){
        world = Bukkit.getWorld(config.getString("world"));

        String xy = config.getString("center");
        center = new Location(world, Double.parseDouble(xy.split(",")[0]), 0, Double.parseDouble(xy.split(",")[1]));

        range = config.getInt("range");

        height = config.getInt("height");

        launchmsg = config.getString("launchmsg");
        launchmsg = ChatColor.translateAlternateColorCodes('&', launchmsg);

        List<String> lb = config.getStringList("launchblocks");
        for(String loc:lb){
            String [] arg = loc.split(",");
            Double [] parsed = new Double[3];
            for(int a = 0;a<3;a++){
                parsed[a] = Double.parseDouble(arg[a+1]);
            }
            launchblocks.add(new Location(Bukkit.getWorld(arg[0]),parsed[0],parsed[1],parsed[2]));
        }

        List<String> ls = config.getStringList("launchsounds");
        try{
            for(String launchsound:ls){
                launchSounds.add(new ConfigSound(Sound.valueOf(launchsound.split(",")[0]),
                        Float.parseFloat(launchsound.split(",")[1]),
                        Float.parseFloat(launchsound.split(",")[2])));
            }
        }catch(Exception ex){
            System.out.println("[SpawnCannon] Error parsing launch sounds. Please check formatting.");
        }

        ls = config.getStringList("landsounds");

        try{
            for(String landsound:ls){
                landSounds.add(new ConfigSound(Sound.valueOf(landsound.split(",")[0]),
                        Float.parseFloat(landsound.split(",")[1]),
                        Float.parseFloat(landsound.split(",")[2])));
            }
        }catch(Exception ex){
            System.out.println("[SpawnCannon] Error parsing land sounds. Please check formatting.");
        }

        particleeffects = config.getBoolean("particleeffects");

        nopermmsg = ChatColor.translateAlternateColorCodes('&', config.getString("nopermmsg"));

    }
}
