package me.uksspy.SpawnCannon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.bukkit.ChatColor.*;

import me.uksspy.SpawnCannon.config.Config;
import me.uksspy.SpawnCannon.config.ConfigSound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;




public class SpawnCannon extends JavaPlugin{

    Config conf;

    List<Player> launched;

    public void onEnable(){
        conf = new Config(this);
        new ListenerClass(this, conf);
        this.saveDefaultConfig();

        launched = new ArrayList<>();
    }

    public void onDisable(){
        this.saveDefaultConfig();
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("sc")){
            if(sender.hasPermission("spawncannon.admin")){
                if(args.length == 2 && args[0].equalsIgnoreCase("launchblock")){

                    //sc launchblock add
                    if(args[1].equalsIgnoreCase("add")){
                        if(sender instanceof Player){
                            Player p = (Player) sender;
                            Block b = p.getTargetBlock((Set<Material>)null, 7);
                            if(b.getType() != Material.AIR){
                                if(!isLaunchBlock(b)){
                                    Location loc = b.getLocation();
                                    conf.launchblocks.add(loc);
                                    String locStr = loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
                                    List<String> locs = getConfig().getStringList("launchblocks");
                                    locs.add(locStr);
                                    getConfig().set("launchblocks", locs);
                                    this.saveConfig();
                                    p.sendMessage(GREEN + "" + b.getType() + "" + GOLD + " at "
                                            + "X: " + GREEN + loc.getBlockX() + GOLD + " "
                                            + "Y: " + GREEN + loc.getBlockY() + GOLD + " "
                                            + "Z: " + GREEN + loc.getBlockZ() + GOLD + " added to config as a launch block.");
                                }else{
                                    p.sendMessage(RED + "That block is already a launch block.");
                                }
                            }else{
                                p.sendMessage(RED + "No block found in range.");
                            }

                        }else{
                            sender.sendMessage(RED + "That command must be run by a player.");
                        }
                        //sc launchblock del
                    }else if(args[1].equalsIgnoreCase("del")){
                        if(sender instanceof Player){
                            Player p = (Player) sender;
                            Block b = p.getTargetBlock((Set<Material>)null, 7);
                            if(b.getType() != Material.AIR){
                                if(isLaunchBlock(b)){
                                    Location loc = b.getLocation();
                                    conf.launchblocks.add(loc);
                                    String locStr = loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
                                    List<String> locs = getConfig().getStringList("launchblocks");
                                    locs.remove(locStr);
                                    getConfig().set("launchblocks", locs);
                                    this.saveConfig();
                                    p.sendMessage(GREEN + "Launch block at "
                                            + "X: " + GREEN + loc.getBlockX() + GOLD + " "
                                            + "Y: " + GREEN + loc.getBlockY() + GOLD + " "
                                            + "Z: " + GREEN + loc.getBlockZ() + GOLD + " removed from config.");
                                }else{
                                    p.sendMessage(RED + "That block is not a launch block.");
                                }
                            }
                        }else{
                            sender.sendMessage(RED + "That command must be run by a player.");
                        }

                        //sc launchblock show
                    }else if(args[1].equalsIgnoreCase("show")){
                        if(sender instanceof Player){
                            Player p = (Player) sender;
                            showLaunchBlocks(p);
                            p.sendMessage(GREEN + "Displaying nearby launch blocks.");
                        }else{
                            sender.sendMessage(RED + "That command must be run by a player.");
                        }
                    }else{
                        sender.sendMessage(RED + "Unknown argument '" + DARK_RED + args[1] + RED + "'");
                        sendUsageMsg(sender);
                    }
                }else if(args.length == 1 && args[0].equalsIgnoreCase("reload")){
                    if(args[0].equalsIgnoreCase("reload")){
                        reloadConfig();
                        conf = new Config(this);
                        sender.sendMessage(GREEN + "SpawnCannon reloaded.");
                    }else{
                        sender.sendMessage(RED + "Unknown argument '" + DARK_RED + args[1] + RED + "'");
                        sendUsageMsg(sender);
                    }
                }else{
                    sendUsageMsg(sender);
                }
            }else{
                sender.sendMessage(RED + "No permission.");
            }
        }else if(cmd.getName().equalsIgnoreCase("launch")){
            if(args.length == 0){
                if(sender instanceof Player){
                    Player player = (Player) sender;
                    if(player.hasPermission("spawncannon.cmd")){
                        launchPlayer(player);
                    }else{
                        player.sendMessage(conf.nopermmsg);
                    }
                }else{
                    sender.sendMessage(RED + "That command must be run by a player.");
                }
            }else if(args.length == 1){
                if(sender.hasPermission("spawncannon.cmd.others")){
                    Player target = Bukkit.getPlayer(args[0]);
                    if(target != null){
                        if(!target.hasPermission("spawncannon.ignore")){
                            launchPlayer(target);
                            sender.sendMessage(GREEN + "Player " + GOLD + target.getName() + GREEN + " launched.");
                        }else{
                            sender.sendMessage(RED + "Player " + DARK_RED + target.getName() + RED + " cannot be launched.");
                        }
                    }else{
                        sender.sendMessage(RED + "Player " + DARK_RED + args[0] + RED + " not found.");
                    }
                }else{
                    sender.sendMessage(RED + "No permission.");
                }
            }else{
                sendUsageMsg(sender);
            }
        }

        return false;
    }



    public void launchPlayer(Player player){
        Random r = new Random();
        int x = conf.center.getBlockX() + r.nextInt(conf.range*2) - conf.range;
        int y = conf.height;
        int z = conf.center.getBlockZ() + r.nextInt(conf.range*2) - conf.range;
        float pitch = player.getLocation().getPitch();
        float yaw = player.getLocation().getYaw();

        Location tp = new Location(conf.world, x,y,z, yaw, pitch);
        player.setVelocity(new Vector(0, -2, 0));
        player.teleport(tp);
        player.sendMessage(Config.replaceLaunchStr(conf.launchmsg, player, tp));
        for(ConfigSound ls : conf.launchSounds){
            player.playSound(player.getLocation(), ls.sound, ls.volume, ls.pitch);
        }

        if(!launched.contains(player))launched.add(player);
    }

    public boolean isLaunchBlock(Block b){
        for(Location loc : conf.launchblocks){
            if(b.getLocation().equals(loc)) return true;
        }

        return false;
    }

    @SuppressWarnings("deprecation")
    public void showLaunchBlocks(Player p){
        List<Block> blocks = new ArrayList<>();

        for(Location loc : conf.launchblocks){
            if(p.getWorld().equals(loc.getWorld()) && p.getLocation().distance(loc) < 100){
                blocks.add(loc.getBlock());
                p.sendBlockChange(loc, Material.WOOL, (byte) 5);
            }
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
            public void run() {
                hideLaunchBlocks(p, blocks);
            }
        },100L);

    }

    @SuppressWarnings("deprecation")
    public void hideLaunchBlocks(Player p, List<Block> blocks){
        for(Block block : blocks){
            p.sendBlockChange(block.getLocation(), block.getType(), block.getData());
        }
    }

    private void sendUsageMsg(CommandSender sender){
        sender.sendMessage(DARK_RED + "Usage:");
        sender.sendMessage(RED + "/sc launchblock add");
        sender.sendMessage(RED + "/sc launchblock del");
        sender.sendMessage(RED + "/sc launchblock show");
        sender.sendMessage(RED + "/sc reload");
    }
}
