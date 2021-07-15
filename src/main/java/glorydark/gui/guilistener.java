package glorydark.gui;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.utils.Config;
import glorydark.MainClass;
import me.onebone.economyapi.EconomyAPI;

import java.io.File;
import java.util.*;

public class guilistener implements Listener {
    public static final HashMap<Player, HashMap<Integer, guitype>> UI_CACHE = new HashMap<>();
    public static void showFormWindow(Player player, FormWindow window, guitype guiType) {
        UI_CACHE.computeIfAbsent(player, i -> new HashMap<>()).put(player.showFormWindow(window), guiType);
    }

    @EventHandler()
    public void PlayerFormRespondedEvent(PlayerFormRespondedEvent event){
        Player p = event.getPlayer();
        FormWindow window = event.getWindow();
        if (p == null || window == null) {
            return;
        }
        guitype guiType = UI_CACHE.containsKey(p) ? UI_CACHE.get(p).get(event.getFormID()) : null;
        if(guiType == null){
            return;
        }
        UI_CACHE.get(p).remove(event.getFormID());
        if (event.getResponse() == null) {
            return;
        }
        if (event.getWindow() instanceof FormWindowSimple) {
            this.formWindowSimpleOnClick(p, (FormWindowSimple) window, guiType);
        }
        if (event.getWindow() instanceof FormWindowCustom) {
            assert window instanceof FormWindowCustom;
            this.formWindowCustomOnClick(p, (FormWindowCustom) window, guiType);
        }
    }

    private void formWindowCustomOnClick(Player p, FormWindowCustom custom, guitype guiType) {
        switch (guiType) {
            case HomeDeleteMenu:
                if(custom.getResponse() == null){ return; }
                if(custom.getResponse().getDropdownResponse(0).getElementContent() == null || custom.getResponse().getDropdownResponse(0).getElementContent().equals("")){
                    p.sendMessage("设置有误！");
                    return;
                }
                Config wlcfg = new Config(MainClass.path+"/homes/"+p.getName()+".yml",Config.YAML);
                List<String> arr = new ArrayList<>(wlcfg.getStringList("list"));
                arr.remove(custom.getResponse().getDropdownResponse(0).getElementContent());
                wlcfg.set("list",arr);
                wlcfg.save();
                FormWindowSimple form1 = new FormWindowSimple("§e§l传送系统", "§e已移除点【"+custom.getResponse().getDropdownResponse(0).getElementContent()+"】");
                String root = MainClass.path+"/homes/"+p.getName()+".yml";
                File file = new File(root);
                file.delete();
                form1.addButton(new ElementButton("返回"));
                showFormWindow(p ,form1, guitype.ErrorMenu);
                return;

            case HomeCreateMenu:
                Config config1 = new Config(MainClass.path+"/config.yml",Config.YAML);
                MainClass.cost = config1.getDouble("设置家花费");
                if(EconomyAPI.getInstance().myMoney(p) < MainClass.cost){p.sendMessage("§e您的货币不足");return;}
                EconomyAPI.getInstance().reduceMoney(p, MainClass.cost);
                Config pointc = null;
                if(custom.getResponse().getResponses() == null){ return; }
                if(custom.getResponse().getInputResponse(1) != null && !custom.getResponse().getInputResponse(1).equals("")){
                    pointc = new Config(MainClass.path+"/homes/"+p.getName()+"/"+custom.getResponse().getInputResponse(1)+".yml",Config.YAML);
                }
                for (int i=0;i<4;i++) {
                    if (custom.getResponse().getInputResponse(i) != null && !custom.getResponse().getInputResponse(i).equals("")) {
                        if (i == 2) {
                            assert pointc != null;
                            pointc.set("简介", custom.getResponse().getInputResponse(2));
                        }
                    }
                }
                Config plc = new Config(MainClass.path+"/homes/"+p.getName()+".yml",Config.YAML);
                List<String> plcarr = new ArrayList<>(plc.getStringList("list"));
                plcarr.add(custom.getResponse().getInputResponse(1));
                plc.set("list",plcarr);
                plc.save();
                List<Double> arr1;
                arr1 = new ArrayList<>();
                arr1.add(p.getX());
                arr1.add(p.getY());
                arr1.add(p.getZ());
                assert pointc != null;
                pointc.set("坐标",arr1);
                pointc.set("世界",p.getLevel().getName());
                pointc.save();
                FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e创建传送点["+custom.getResponse().getInputResponse(1)+"]成功,本次花费了货币"+MainClass.cost);
                form.addButton(new ElementButton("返回"));
                showFormWindow(p, form, guitype.ErrorMenu);
                return;
            case SETTINGMENU:
                Config pconfig = new Config(MainClass.path+"/player/"+p.getName()+".yml",Config.YAML);
                if(custom.getResponse() == null){ return; }
                Boolean state = custom.getResponse().getToggleResponse(0);
                pconfig.set("自动接受传送请求",state);
                pconfig.save();
                if(custom.getResponse().getToggleResponse(1)){
                    Config cfg = new Config(MainClass.path+"/config.yml",Config.YAML);
                    MainClass.cost = cfg.getDouble("设置重生点花费");
                    if(EconomyAPI.getInstance().myMoney(p) < MainClass.cost){p.sendMessage("§e您的货币不足");return;}
                    EconomyAPI.getInstance().reduceMoney(p, MainClass.cost);
                    pconfig.set("spawnpoint.x",p.x);
                    pconfig.set("spawnpoint.y",p.y);
                    pconfig.set("spawnpoint.z",p.z);
                    pconfig.set("spawnpoint.level",p.getLevel().getName());
                    pconfig.save();
                    p.getPlayer().setSpawn(p.getPosition());
                }
                FormWindowSimple formsetting = new FormWindowSimple("§e§l传送系统", "§e设置已保存");
                formsetting.addButton(new ElementButton("返回"));
                showFormWindow(p,formsetting,guitype.ErrorMenu);
                break;
            case MainSETTINGMENU:
                FormResponseCustom response = custom.getResponse();
                pconfig = new Config(MainClass.path+"/config.yml",Config.YAML);
                if(response == null){ return; }
                if(response.getInputResponse(0) != null && !response.getInputResponse(0).equals("")){
                    Double homecost = Double.valueOf(response.getInputResponse(0));
                    pconfig.set("传送邀请花费",homecost);
                    pconfig.save();
                }
                if(response.getInputResponse(1) != null && !response.getInputResponse(1).equals("")){
                    Double tpcost = Double.valueOf(response.getInputResponse(1));
                    pconfig.set("设置家花费",tpcost);
                    pconfig.save();
                }
                if(response.getInputResponse(2) != null && !response.getInputResponse(2).equals("")){
                    Double spawncost = Double.valueOf(response.getInputResponse(2));
                    pconfig.set("设置重生点花费",spawncost);
                    pconfig.save();
                }
                form = new FormWindowSimple("§e§l传送系统", "§e设置已保存");
                form.addButton(new ElementButton("返回"));
                showFormWindow(p, form, guitype.ErrorMenu);
                break;
            case WarpsSettingDeleteMenu:
                response = custom.getResponse();
                wlcfg = new Config(MainClass.path+"/warps.yml",Config.YAML);
                arr = new ArrayList<>(wlcfg.getStringList("list"));
                arr.remove(response.getDropdownResponse(0).getElementContent());
                wlcfg.set("list",arr);
                wlcfg.save();
                root = MainClass.path+"/warps/"+response.getDropdownResponse(0).getElementContent()+".yml";
                file = new File(root);
                file.delete();
                form1 = new FormWindowSimple("§e§l传送系统", "§e已移除点【"+response.getDropdownResponse(0).getElementContent()+"】");
                form1.addButton(new ElementButton("返回"));
                showFormWindow(p, form1, guitype.ErrorMenu);
                break;
            case WarpSettingDownMenu:
                Config cachecfg = new Config(MainClass.path+"/warpsettingcache.yml",Config.YAML);
                String defaultn = cachecfg.getString(p.getName());
                response = custom.getResponse();
                String name,world;
                double x,y,z;
                if(response == null){
                    form1 = new FormWindowSimple("§e§l传送系统", "§e设置无变动！");
                    form1.addButton(new ElementButton("返回"));
                    showFormWindow(p, form1, guitype.ErrorMenu);
                    return;
                }
                Config cfg = new Config(MainClass.path+"/warps/"+defaultn+".yml",Config.YAML);
                boolean toggle = response.getToggleResponse(6);
                cfg.set("state", toggle);
                cfg.save();
                for (int i=0;i<10;i++){
                    if(response.getInputResponse(i) != null && !response.getInputResponse(i).equals("")){
                        switch (i){
                            case 1:
                                name = response.getInputResponse(1);
                                cfg.set("name", name);
                                cfg.save();
                                break;
                            case 2:
                                x = Double.parseDouble(response.getInputResponse(2));
                                cfg.set("x", x);
                                cfg.save();
                                break;
                            case 3:
                                y = Double.parseDouble(response.getInputResponse(3));
                                cfg.set("y", y);
                                cfg.save();
                                break;
                            case 4:
                                z = Double.parseDouble(response.getInputResponse(4));
                                cfg.set("z", z);
                                cfg.save();
                                break;
                            case 5:
                                world = response.getInputResponse(5);
                                cfg.set("world", world);
                                cfg.save();
                                break;
                            default:
                                break;
                        }
                    }
                }
                form = new FormWindowSimple("§e§l传送系统", "§e设置已保存");
                form.addButton(new ElementButton("返回"));
                showFormWindow(p, form, guitype.ErrorMenu);
                break;
            case WarpsSettingCreateMenu:
                response = custom.getResponse();
                if(response == null){
                    form1 = new FormWindowSimple("§e§l传送点系统", "§e您输入的信息不完全！");
                    form1.addButton(new ElementButton("返回"));
                    showFormWindow(p, form1, guitype.ErrorMenu);
                    return;
                }
                wlcfg = new Config(MainClass.path+"/warps.yml",Config.YAML);
                List<String> Arr = new ArrayList<>(wlcfg.getStringList("list"));
                if(Arr.contains(response.getInputResponse(0))){
                    form1 = new FormWindowSimple("§e§l传送点系统", "§e您已经创建过这个传送点了！");
                    form1.addButton(new ElementButton("返回"));
                    showFormWindow(p, form1, guitype.ErrorMenu);
                    return;
                }
                if(response.getInputResponse(0) == null || response.getInputResponse(0).equals("")){
                    form1 = new FormWindowSimple("§e§l传送点系统", "§e您输入的信息有误！");
                    form1.addButton(new ElementButton("返回"));
                    showFormWindow(p, form1, guitype.ErrorMenu);
                    return;
                }
                Arr.add(response.getInputResponse(0));
                wlcfg.set("list",Arr);
                wlcfg.save();
                cfg = new Config(MainClass.path+"/warps/"+response.getInputResponse(0)+".yml",Config.YAML);
                toggle = response.getToggleResponse(6);
                cfg.set("state", toggle);
                cfg.save();
                for (int i=0;i<10;i++){
                    if(response.getInputResponse(i) != null && !response.getInputResponse(i).equals("")){
                        switch (i){
                            case 1:
                                name = response.getInputResponse(1);
                                cfg.set("name", name);
                                cfg.save();
                                break;
                            case 2:
                                x = Double.parseDouble(response.getInputResponse(2));
                                cfg.set("x", x);
                                cfg.save();
                                break;
                            case 3:
                                y = Double.parseDouble(response.getInputResponse(3));
                                cfg.set("y", y);
                                cfg.save();
                                break;
                            case 4:
                                z = Double.parseDouble(response.getInputResponse(4));
                                cfg.set("z", z);
                                cfg.save();
                                break;
                            case 5:
                                world = response.getInputResponse(5);
                                cfg.set("world", world);
                                cfg.save();
                                break;
                            default:
                                break;
                        }
                    }else{
                        switch (i){
                            case 1:
                                /*
                                name = response.getInputResponse(1);
                                cfg.set("name", "default");
                                cfg.save();
                                break;
                                */
                            case 2:
                                x = 0.000000;
                                cfg.set("x", x);
                                cfg.save();
                                break;
                            case 3:
                                y = 66.000000;
                                cfg.set("y", y);
                                cfg.save();
                                break;
                            case 4:
                                z = 0.000000;
                                cfg.set("z", z);
                                cfg.save();
                                break;
                            case 5:
                                world = "world";
                                cfg.set("world", world);
                                cfg.save();
                                break;
                            default:
                                break;
                        }
                    }
                }
                form = new FormWindowSimple("§e§l传送系统", "§e创建传送点【"+response.getInputResponse(0)+"】成功");
                form.addButton(new ElementButton("返回"));
                showFormWindow(p,form, guitype.ErrorMenu);
                break;
        }
    }

    private void formWindowSimpleOnClick(Player p, FormWindowSimple simple, guitype guiType) {
        FormResponseSimple response = simple.getResponse();
        switch (guiType) {
            case MainMenu:
                if(response == null){ return; }
                int buttonId = response.getClickedButtonId();
                switch (buttonId){
                    case 0:
                        MainClass.warpsmenu(p);
                        break;
                    case 1:
                        MainClass.homemainmenu(p);
                        break;
                    case 2:
                        MainClass.teleportmenu(p,0);
                        break;
                    case 3:
                        MainClass.teleportmenu(p,1);
                        break;
                    case 4:
                        MainClass.settingmenu(p);
                        break;
                    case 5:
                        MainClass.worldmenu(p);
                        break;
                    case 6:
                        Config playerconfig = new Config(MainClass.path+"/player/"+ p.getName()+".yml",Config.YAML);
                        if(playerconfig.exists("lastdeath")){
                            p.sendMessage("正在传送...");
                            double x = playerconfig.getDouble("lastdeath.x");
                            double y = playerconfig.getDouble("lastdeath.y");
                            double z = playerconfig.getDouble("lastdeath.z");
                            Level level = p.getServer().getLevelByName(playerconfig.getString("lastdeath.level"));
                            p.teleportImmediate(new Location(x,y,z,level));
                        }else{
                            FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e上次死亡点不存在！");
                            form.addButton(new ElementButton("返回"));
                            showFormWindow(p, form, guitype.ErrorMenu);
                        }
                        break;
                    case 7:
                        if(MainClass.checktrust(p)){ return; }
                        Map<UUID, Player> pl = p.getServer().getOnlinePlayers();
                        List<Player> list = new ArrayList<>(pl.values());
                        for (Player p1 : list) {
                            MainClass.tp(p1,p);
                            p1.sendMessage("§e"+p.getName()+"强制传送了你!");
                            p.sendMessage("§e"+p.getName()+"被您的强制请求传送到你的位置!");
                        }
                        break;
                    case 8:
                        MainClass.warpsmanagedownmenu(p);
                        break;
                    case 9:
                        MainClass.mainsettingmenu(p);
                    default:
                        break;
                }
                break;
            case HomeMainMenu:
                if(response == null){ return; }
                switch(response.getClickedButtonId()){
                    case 0: //create
                        MainClass.HomeCreateMenu(p);
                        break;
                    case 1: //delete
                        MainClass.HomeTeleportMenu(p);
                        break;
                    case 2: //return
                        MainClass.homedeletemenu(p);
                        break;
                    case 3: //return
                        MainClass.mainmenu(p);
                        break;
                }
                break;
            case TeleportInitiativeMENU:
                response = simple.getResponse();
                if(response == null){ return; }
                String selected = response.getClickedButton().getText();
                Player selectedp = p.getServer().getPlayer(selected);
                Config pconfig = new Config(MainClass.path+"/player/"+selected+".yml",Config.YAML);
                boolean bool = pconfig.getBoolean("自动接受传送请求");
                if(p.isOp() || bool){ MainClass.tp(p,selectedp); return; }
                MainClass.acceptmenu(p,selectedp,0);
                break;
            case ErrorMenu:
                MainClass.mainmenu(p);
                break;
            case HomeTeleportMenu:
                if(response == null){ return; }
                String[] strArr = response.getClickedButton().getText().split("\n");
                String[] sArr = strArr[0].split(":");
                p.sendMessage("正在传送到家:" + sArr[1]);
                Config pointc = new Config(MainClass.path+"/homes/"+p.getName()+"/"+sArr[1]+".yml",Config.YAML);
                List<Double> position = pointc.getDoubleList("坐标");
                String world = pointc.getString("世界");
                String intro = pointc.getString("简介");
                p.teleportImmediate(new Location(position.get(0),position.get(1),position.get(2),p.getServer().getLevelByName(world)));
                break;
            case WarpMenu: //Warp系统
                if(response == null){ return; }
                selected = response.getClickedButton().getText();
                Config warpconfig = new Config(MainClass.path+"/warps/"+selected+".yml",Config.YAML);
                String placename = warpconfig.getString("name");
                if(selected.equals("返回")){
                    MainClass.mainmenu(p);
                }else{
                    double x,y,z;
                    String name;
                    boolean state;
                    if(warpconfig.exists("x")){
                        x = warpconfig.getDouble("x");
                    }else{
                        FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e该传送点未设置完毕，无法进行传送");
                        form.addButton(new ElementButton("返回"));
                        showFormWindow(p, form, guitype.ErrorMenu);
                        return;
                    }
                    if(warpconfig.exists("y")){
                        y = warpconfig.getDouble("y");
                    }else{
                        FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e该传送点未设置完毕，无法进行传送");
                        form.addButton(new ElementButton("返回"));
                        showFormWindow(p, form, guitype.ErrorMenu);
                        return;
                    }
                    if(warpconfig.exists("z")){
                        z = warpconfig.getDouble("z");
                    }else{
                        FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e该传送点未设置完毕，无法进行传送");
                        form.addButton(new ElementButton("返回"));
                        showFormWindow(p, form, guitype.ErrorMenu);
                        return;
                    }
                    if(warpconfig.exists("world")){
                        world = warpconfig.getString("world");
                    }else{
                        FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e该传送点未设置完毕，无法进行传送");
                        form.addButton(new ElementButton("返回"));
                        showFormWindow(p, form, guitype.ErrorMenu);
                        return;
                    }
                    /* if(warpconfig.exists("name")){
                        name = warpconfig.getString("name");
                    }else{
                        FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e该传送点未设置完毕，无法进行传送");
                        form.addButton(new ElementButton("返回"));
                        showFormWindow(p, form, guitype.ErrorMenu);
                        return;
                    } */
                    if(warpconfig.exists("state")){
                        state = warpconfig.getBoolean("state");
                    }else{
                        FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e该传送点未设置完毕，无法进行传送");
                        form.addButton(new ElementButton("返回"));
                        showFormWindow(p, form, guitype.ErrorMenu);
                        return;
                    }
                    if(!state) {
                        FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e传送点【"+placename+"】暂时关闭!");
                        form.addButton(new ElementButton("返回"));
                        showFormWindow(p, form, guitype.ErrorMenu);
                        return;
                    }
                    p.sendMessage("正在传送到传送点:"+placename);
                    if (!p.getServer().isLevelGenerated(world)){
                        p.sendMessage("传送点所在地图不存在！");
                    }
                    p.teleportImmediate(new Location(x,y,z,p.getServer().getLevelByName(world)));
                    return;
                }
                break;
        case WarpsSettingManageMenu:
                if(response == null){ return; }
                int manageselected = response.getClickedButtonId();
                switch (manageselected){
                    case 0:
                        MainClass.warpssettingmenu(p);
                        break;
                    case 1:
                        MainClass.warpscreatemenu(p);
                        break;
                    case 2:
                        MainClass.warpsdeletemenu(p);
                        break;
                    case 3:
                        MainClass.mainmenu(p);
                    default:
                        return;
                }
                break;
            case TeleportPassiveMENU: //传送系统
                if(response == null){ return; }
                String selectedtext = response.getClickedButton().getText();
                selectedp = p.getServer().getPlayer(selectedtext);
                pconfig = new Config(MainClass.path+"/player/"+selectedtext+".yml",Config.YAML);
                bool = pconfig.getBoolean("自动接受传送请求");
                if(p.isOp() || bool){ MainClass.tp(selectedp,p); return; }
                MainClass.acceptmenu(p,selectedp,1);
                break;
            case AcceptListInitiativeMENU: //接受邀请
                if(response == null){ return; }
                int AcceptbuttonId = response.getClickedButtonId();
                String name = response.getClickedButton().getText();
                if(AcceptbuttonId == 0){
                    MainClass.tp(p.getServer().getPlayer(name),p);
                }
                break;
            case AcceptListPassiveMENU: //将玩家传送到你 你接受
                if(response == null){ return; }
                AcceptbuttonId = response.getClickedButtonId();
                name = response.getClickedButton().getText();
                if(AcceptbuttonId == 0){
                    MainClass.tp(p, p.getServer().getPlayer(name));
                }
                break;
            case WorldTeleportMenu:
                if(response == null){ return; }
                if(response.getClickedButton().getText().equals("返回")) {
                    MainClass.mainmenu(p);
                }else{
                    MainClass.worldteleport(p, p.getServer().getLevelByName(response.getClickedButton().getText()));
                }
                break;
        }
    }
}
