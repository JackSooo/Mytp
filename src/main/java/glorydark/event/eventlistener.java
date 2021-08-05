package glorydark.event;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.utils.Config;
import glorydark.MainClass;

import java.util.TimerTask;

import static glorydark.Tools.getLang;

public class eventlistener implements Listener {
    @EventHandler
    public void PlayerRespawnEvent(PlayerRespawnEvent event){
        if(!event.isFirstSpawn()) {
            Player player = event.getPlayer();
            if (player == null) {
                return;
            }
            Config playerconfig = new Config(MainClass.path + "/player/" + player.getName() + ".yml", Config.YAML);
            if(playerconfig.exists("x") && playerconfig.exists("y") && playerconfig.exists("z") && playerconfig.exists("level")) {
                double x = playerconfig.getDouble("x", player.getSpawn().x);
                double y = playerconfig.getDouble("y", player.getSpawn().y);
                double z = playerconfig.getDouble("z", player.getSpawn().z);
                String levelname = playerconfig.getString("level", Server.getInstance().getDefaultLevel().getName());
                if(Server.getInstance().getLevelByName(levelname) != null) {
                    player.teleportImmediate(new Location(x, y, z, player.getServer().getLevelByName(levelname)));
                    player.sendMessage(getLang("Tips", "back_to_spawnpoint"));
                }else{
                    player.sendMessage(getLang("Tips","world_is_not_loaded"));
                }
            }
            if (!MainClass.godPlayer.contains(player)) {
                MainClass.godPlayer.add(player);
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        MainClass.godPlayer.remove(player);
                        player.sendMessage(getLang("Tips", "god_effect_dissolve"));
                    }
                };
                MainClass.timer.schedule(timerTask, 3000);
                player.sendMessage(getLang("Tips", "god_effect_given"));
            }
        }
    }

    @EventHandler
    public void PlayerLocallyInitializedEvent(PlayerLocallyInitializedEvent event){
        if (event.getPlayer() == null) {
            return;
        }
        Config config = new Config(MainClass.path+"/config.yml",Config.YAML);
        if(config.exists("是否使用快捷工具") && config.getBoolean("是否使用快捷工具",true)) {
            Item convenience = new Item(config.getInt("快捷工具ID", 347));
            convenience.setLore(getLang("Convenient_Tool","lore"));
            convenience.setCustomName(getLang("Convenient_Tool","nametag"));
            convenience.setDamage(0);
            if(!(event.getPlayer().getInventory().getContents().containsValue(convenience))) {
                event.getPlayer().getInventory().addItem(convenience);
                event.getPlayer().sendMessage(getLang("Tips", "given_convenient_tool"));
            }
        }
        if (config.exists("强制回主城") && config.getBoolean("强制回主城")) {
            event.getPlayer().teleportImmediate(Server.getInstance().getDefaultLevel().getSafeSpawn().getLocation());
            event.getPlayer().sendMessage(getLang("Tips", "back_to_lobby"));
        }
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event){
        if (event.getPlayer() == null) {
            return;
        }
        Config config = new Config(MainClass.path+"/config.yml",Config.YAML);
        if(config.exists("是否使用快捷工具") && config.getBoolean("是否使用快捷工具")) {
            if (event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_AIR)) {
                Item i = event.getPlayer().getInventory().getItemInHand();
                if (i.getId() == 347 && i.getCustomName().equals(getLang("Convenient_Tool","nametag"))) {
                    event.getPlayer().getServer().dispatchCommand(event.getPlayer(), "mytp open");
                }
            }
        }
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event){
        if(event.getEntity() == null){return; }
        Config playerconfig = new Config(MainClass.path+"/player/"+ event.getEntity().getName()+".yml",Config.YAML);
        playerconfig.set("lastdeath.x",event.getEntity().getX());
        playerconfig.set("lastdeath.y",event.getEntity().getY());
        playerconfig.set("lastdeath.z",event.getEntity().getZ());
        playerconfig.set("lastdeath.level",event.getEntity().getLevel().getName());
        playerconfig.save();
        MainClass.godPlayer.remove(event.getEntity());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerDamageEvent(EntityDamageEvent event){
        if (event.getEntity() == null) {
            return;
        }
        if(event.getEntity() instanceof Player) {
            if (MainClass.godPlayer.contains(((Player) event.getEntity()).getPlayer())){
                event.setCancelled(true);
            }
        }
    }
}
