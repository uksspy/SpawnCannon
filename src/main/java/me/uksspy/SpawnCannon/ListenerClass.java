package me.uksspy.SpawnCannon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.uksspy.SpawnCannon.config.Config;
import me.uksspy.SpawnCannon.config.ConfigSound;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;



public class ListenerClass implements Listener{

    private SpawnCannon plugin;
    private Config conf;


    public ListenerClass(SpawnCannon plugin, Config conf) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin=plugin;
        this.conf = conf;
    }


    @EventHandler
    public void onPlayerDmg(EntityDamageEvent event){
        if(event.getEntity() instanceof Player){
            Player p = (Player) event.getEntity();
            if(plugin.launched.contains(p) && event.getCause() == DamageCause.FALL) event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        for(Location lLoc : conf.launchblocks){
            if(lLoc.getBlock().equals(player.getLocation().getBlock()) || lLoc.getBlock().equals(player.getEyeLocation().getBlock())){

                if(player.hasPermission("spawncannon.launch")){
                    plugin.launchPlayer(event.getPlayer());
                }else{
                    player.sendMessage(conf.nopermmsg);
                }
            }
        }

        List<Player> pOnGround = new ArrayList<>();

        for(Player p : plugin.launched){
            List<Material> fluids = Arrays.asList(Material.STATIONARY_WATER, Material.STATIONARY_LAVA, Material.WATER, Material.LAVA);
            //If is on ground
            if(p.getVelocity().getY() > -0.1D || fluids.contains(p.getLocation().getBlock().getType())){
                pOnGround.add(p);
            }else{
                if(conf.particleeffects){
                    for(Player spec : Bukkit.getOnlinePlayers()){
                        if(spec.getWorld().equals(p.getWorld()) && spec.getLocation().distance(p.getLocation()) <= 100){
                            spec.spigot().playEffect(p.getLocation(), Effect.EXPLOSION, 0, 0, 0F, 0F, 0F, 0.3F, 5, 100);
                        }
                    }
                }
            }
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
            public void run() {
                plugin.launched.removeAll(pOnGround);
                for(Player p : pOnGround){
                    for(ConfigSound ls : conf.landSounds){
                        for(Player spec : Bukkit.getOnlinePlayers()){
                            if(spec.getWorld().equals(p.getWorld())&& spec.getLocation().distance(p.getLocation()) <= 100){
                                spec.playSound(p.getLocation(), ls.sound, ls.volume, ls.pitch);
                            }
                        }
                    }
                    if(conf.particleeffects){
                        for(Player spec : Bukkit.getOnlinePlayers()){
                            if(spec.getWorld().equals(p.getWorld()) && spec.getLocation().distance(p.getLocation()) <= 100){
                                spec.spigot().playEffect(p.getLocation(), Effect.EXPLOSION_LARGE, 0, 0, 0, 0, 0, 1F, 30, 100);
                            }
                        }
                    }
                }
            }
        }, 1L);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        if(plugin.launched.contains(event.getPlayer())) plugin.launched.remove(event.getPlayer());
    }
}
