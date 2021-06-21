package glorydark.event;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntitySpawnEvent;
import cn.nukkit.event.player.PlayerDeathEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import glorydark.MainClass;

public class eventlistener implements Listener {
    @EventHandler
    public void EntitySpawnEvent(EntitySpawnEvent event){
        if(!(event.getEntity() instanceof Player)) { return; }
        Config playerconfig = new Config(MainClass.path+"/player/"+ event.getEntity().getName()+".yml",Config.YAML);
        if(!playerconfig.exists("world")) { return;}
        double x = playerconfig.getDouble("x", ((Player) event.getEntity()).getSpawn().x);
        double y = playerconfig.getDouble("y", ((Player) event.getEntity()).getSpawn().y);
        double z = playerconfig.getDouble("z",((Player) event.getEntity()).getSpawn().z);
        String levelname = playerconfig.getString("level","world");
        event.getEntity().setPosition(new Position(x,y,z,event.getEntity().getServer().getLevelByName(levelname)));
        ((Player) event.getEntity()).sendActionBar("您已传送至设置的出生地!");
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent event){
        Config config = new Config(MainClass.path+"/config.yml",Config.YAML);
        if(config.exists("是否使用快捷工具") && config.getBoolean("是否使用快捷工具",true)) {
            Item convenience = new Item(config.getInt("快捷工具ID", 347));
            convenience.setLore("§l§eMytp");
            convenience.setCustomName("§l§e快捷打开传送界面");
            convenience.setDamage(0);
            if (event.getPlayer().getInventory().getContents().values().contains(convenience)) {
                return;
            }
            event.getPlayer().getInventory().addItem(convenience);
            event.getPlayer().sendMessage("赠送您一个快捷传送工具！");
        }
    }

    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent event){
        Config config = new Config(MainClass.path+"/config.yml",Config.YAML);
        if(config.exists("是否使用快捷工具") && config.getBoolean("是否使用快捷工具")) {
            if (event.getAction().equals(event.getAction().RIGHT_CLICK_AIR)) {
                Item i = event.getPlayer().getInventory().getItemInHand();
                if (i.getId() == 347 && i.getCustomName().equals("§l§e快捷打开传送界面")) {
                    event.getPlayer().getServer().dispatchCommand(event.getPlayer(), "mytp open");
                }
            }
        }
    }

    @EventHandler
    public void PlayerDeathEvent(PlayerDeathEvent event){
        if(!(event.getEntity() instanceof Player)){return; }
        Config playerconfig = new Config(MainClass.path+"/player/"+ ((Player)event.getEntity()).getName()+".yml",Config.YAML);
        playerconfig.set("lastdeath.x",event.getEntity().getX());
        playerconfig.set("lastdeath.y",event.getEntity().getY());
        playerconfig.set("lastdeath.z",event.getEntity().getZ());
        playerconfig.set("lastdeath.level",event.getEntity().getLevel().getName());
        playerconfig.save();
    }
}
