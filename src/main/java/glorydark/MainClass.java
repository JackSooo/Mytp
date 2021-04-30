package glorydark;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.*;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.sun.org.apache.xpath.internal.functions.FuncFalse;
import me.onebone.economyapi.EconomyAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
public class MainClass extends PluginBase implements Listener {
    public static final int MainMenu = 100100;
    public static final int TeleportInitiativeMENU = 120100;
    public static final int TeleportPassiveMENU = 120101;
    public static final int AcceptListInitiativeMENU = 120102;
    public static final int AcceptListPassiveMENU = 120103;
    public static final int WarpMenu = 120104;
    public static final int SETTINGMENU = 120105;
    public static final int ErrorMenu = 120999;
    public static final int WarpsSettingMenu = 120106;
    public static final int WarpSettingDownMenu = 120107;
    public static final int WarpsSettingManageMenu = 120108;
    public static final int WarpsSettingCreateMenu = 120109;
    public static final int WarpsSettingDeleteMenu = 120110;
    public static final int HomeMainMenu = 120111;
    public static final int HomeTeleportMenu = 120112;
    public static final int HomeDeleteMenu = 120113;
    public static final int HomeCreateMenu = 120114;
    public static final int MainSETTINGMENU = 120115;
    public static final int WorldTeleportMenu = 120116;
    public double cost = 0;

    @Override
    public void onLoad(){
        saveDefaultConfig();
        this.getLogger().info("DEssential Onloaded!");
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this); // 注册Event
        this.getLogger().info("DEssential Enabled!");
        Config config = new Config(this.getDataFolder()+"/config.yml",Config.YAML);
        if(!config.exists("设置重生点花费")){
            config.set("设置重生点花费","1000.000000");
            config.save();
        }
    }

    @Override
    public void onDisable() {
        this.getLogger().info("DEssential Disabled!");
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(command.getName().equals("mytp") && args.length >= 1) {
            switch (args[0]) {
                case "help":
                    sender.sendMessage("------ Mytp Manual ------");
                    sender.sendMessage("打开菜单 /mytp open");
                    sender.sendMessage("设置白名单 /mytp 设置白名单 玩家昵称 (后台进行)");
                    sender.sendMessage("删除白名单 /mytp 删除白名单 玩家昵称 (后台进行)");
                    sender.sendMessage("------ Mytp Manual ------");
                    break;
                case "open":
                    if (sender instanceof Player) {
                        this.mainmenu(this.getServer().getPlayer(sender.getName()));
                    } else {
                        sender.sendMessage("请在游戏内使用本指令!");
                    }
                    break;
                case "添加白名单":
                    if (!(sender instanceof Player)) {
                        if(args.length == 2) {
                            if(this.addtrust(args[1])) {
                                sender.sendMessage("给予玩家【"+args[1]+"】白名单成功!");
                            }else{
                                sender.sendMessage("设置失败，请检查是否为指令书写问题。格式为 /mytp 添加白名单 <玩家昵称>");
                            }
                        }else{
                            sender.sendMessage("请填写玩家名字!");
                        }
                    }else{
                        sender.sendMessage("请在控制台使用本指令!");
                    }
                    break;
                case "删除白名单":
                    if (!(sender instanceof Player)) {
                        if(args.length == 2) {
                            if(this.removetrust(args[1])) {
                                sender.sendMessage("移除玩家【"+args[1]+"】白名单成功!");
                            }else{
                                sender.sendMessage("设置失败，请检查是否为指令书写问题。格式为 /mytp 添加白名单 <玩家昵称>");
                            }
                        }else{
                            sender.sendMessage("请填写玩家名字!");
                        }
                    }else{
                        sender.sendMessage("请在控制台使用本指令!");
                    }
                    break;
                default:
                    sender.sendMessage("------ Mytp Manual ------");
                    sender.sendMessage("打开菜单 /mytp open");
                    sender.sendMessage("设置白名单 /mytp 设置白名单 玩家昵称 (后台进行)");
                    sender.sendMessage("删除白名单 /mytp 删除白名单 玩家昵称 (后台进行)");
                    sender.sendMessage("查看帮助 /mytp help 玩家昵称");
                    sender.sendMessage("------ Mytp Manual ------");
                    break;
            }
            return true;
        }
        return false;
    }
    public Boolean addtrust(String pn){
        Config trustlist = new Config(this.getDataFolder()+"/trust.yml",Config.YAML);
        List<String> arrayList = new ArrayList<>(trustlist.getStringList("list"));
        if(arrayList.contains(pn)) {
            return false;
        }else{
            arrayList.add(pn);
            trustlist.set("list",arrayList);
            trustlist.save();
            return true;
        }
    }
    public Boolean removetrust(String pn){
        Config trustlist = new Config(this.getDataFolder()+"/trust.yml",Config.YAML);
        List<String> arrayList = new ArrayList<>(trustlist.getStringList("list"));
        if(arrayList.contains(pn)) {
            for (int i = 0;i<arrayList.size();i++){
                if(arrayList.get(i).equals(pn)){
                    arrayList.remove(i);
                    trustlist.set("list",arrayList);
                    trustlist.save();
                }
            }
            return true;
        }else{
            return false;
        }
    }

    public void mainmenu(Player player) {
        if(!player.isOnline()){ return; }
        FormWindowSimple form = new FormWindowSimple("传送系统", "您好，玩家"+player.getName()+"，欢迎使用本传送系统，您的货币剩余"+EconomyAPI.getInstance().myMoney(player));
        form.addButton(new ElementButton("传送点",new ElementButtonImageData("path","textures/items/diamond.png")));
        form.addButton(new ElementButton("设置家",new ElementButtonImageData("path","textures/items/iron_ingot.png")));
        form.addButton(new ElementButton("传送到玩家",new ElementButtonImageData("path","textures/blocks/shroomlight.png")));
        form.addButton(new ElementButton("将玩家传送到你",new ElementButtonImageData("path","textures/blocks/ender_chest_front.png")));
        form.addButton(new ElementButton("我的设置",new ElementButtonImageData("path","textures/items/villagebell.png")));
        form.addButton(new ElementButton("世界传送"));
        if(player.isOp()){
            form.addButton(new ElementButton("全体传送"));
            form.addButton(new ElementButton("传送点设置"));
            form.addButton(new ElementButton("传送系统设置"));
        }
        player.showFormWindow(form, MainMenu);
    }

    public void warpsmenu(Player player) { //传送点系统
        if(!player.isOnline()){ return; }
        FormWindowSimple form = new FormWindowSimple("§e传送系统", "§e§l传送点系统");
        Config warpconfig = new Config(this.getDataFolder()+"/warps.yml",Config.YAML);
        if(warpconfig.get("list") != null) {
            List<String> warplist = new ArrayList<>(warpconfig.getStringList("list"));
            for (String wpn : warplist) {
                form.addButton(new ElementButton(wpn));
            }
        }
        form.addButton(new ElementButton("返回"));
        player.showFormWindow(form, WarpMenu);
    }

    public void teleportmenu(Player player,int type) {
        switch (type){
            case 0:
                if (!player.isOnline()) {
                    return;
                }
                FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e选择你要传送到的玩家");
                Map<java.util.UUID, Player> pl = this.getServer().getOnlinePlayers();
                List<Player> list = new ArrayList<>(pl.values());
                for (Player p : list) {
                    form.addButton(new ElementButton(p.getName()));
                }
                player.showFormWindow(form, TeleportInitiativeMENU);
                break;
            case 1:
                if (!player.isOnline()) {
                    return;
                }
                FormWindowSimple form1 = new FormWindowSimple("§e§l传送系统", "§e选择你要邀请玩家");
                Map<java.util.UUID, Player> pl1 = this.getServer().getOnlinePlayers();
                List<Player> list1 = new ArrayList<>(pl1.values());
                for (Player p : list1) {
                    form1.addButton(new ElementButton(p.getName()));
                }
                player.showFormWindow(form1, TeleportPassiveMENU);
                break;
            default:
                break;
        }
    }

    public void acceptmenu(Player asker,Player player,int type) {
        switch (type){
            case 0:
                Config config = new Config(this.getDataFolder()+"/config.yml",Config.YAML);
                cost = config.getDouble("传送邀请花费");
                if(EconomyAPI.getInstance().myMoney(asker) < cost){asker.sendMessage("§e您的货币不足");return;}
                EconomyAPI.getInstance().reduceMoney(asker, cost);
                if(!player.isOnline()){ return; }
                FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e§l申请列表");
                form.addButton(new ElementButton(asker.getName()));
                player.showFormWindow(form, AcceptListInitiativeMENU);
                break;
            case 1:
                Config config1 = new Config(this.getDataFolder()+"/config.yml",Config.YAML);
                cost = config1.getDouble("传送邀请花费");
                if(EconomyAPI.getInstance().myMoney(asker) < cost){asker.sendMessage("§e您的货币不足");return;}
                EconomyAPI.getInstance().reduceMoney(asker, cost);
                if(!player.isOnline()){ return; }
                FormWindowSimple form1 = new FormWindowSimple("§e§l传送系统", "§e§l申请列表");
                form1.addButton(new ElementButton(asker.getName()));
                player.showFormWindow(form1, AcceptListPassiveMENU);
                break;
            default:
                break;
        }
    }

    public void settingmenu(Player player) {
        Config pconfig = new Config(this.getDataFolder()+"/player/"+player.getName()+".yml",Config.YAML);
        FormWindowCustom form = new FormWindowCustom("§e§l传送系统 - 个人设置");
        form.addElement(new ElementToggle("§e开/关自动接受申请",pconfig.getBoolean("自动接受传送请求")));
        form.addElement(new ElementToggle("§e重新设置出生点(需花费一定金币)", false));
        player.showFormWindow(form, SETTINGMENU);
    }
    public void mainsettingmenu(Player player) {
        if(checktrust(player)){ return; }
        Config pconfig = new Config(this.getDataFolder()+"/config.yml",Config.YAML);
        FormWindowCustom form = new FormWindowCustom("§e§l传送系统 - 系统设置");
        form.addElement(new ElementInput("§e传送邀请花费",pconfig.getString("传送邀请花费")));
        form.addElement(new ElementInput("§e设置家花费",pconfig.getString("设置家花费")));
        form.addElement(new ElementInput("§e设置重生点",pconfig.getString("设置重生点花费")));
        player.showFormWindow(form, MainSETTINGMENU);
    }
    public boolean checktrust(Player p){
        Config trustlist = new Config(this.getDataFolder()+"/trust.yml",Config.YAML);
        List<String> arrayList = new ArrayList<>(trustlist.getStringList("list"));
        if(arrayList.contains(p.getName())) {
            return false;
        }else{
            FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§c§l您未取得信任！");
            form.addButton(new ElementButton("返回"));
            p.showFormWindow(form, ErrorMenu);
            return true;
        }
    }
    public void warpssettingmenu(Player player) {
        if(checktrust(player)){ return; }
        Config warpconfig = new Config(this.getDataFolder()+"/warps.yml",Config.YAML);
        FormWindowSimple form = new FormWindowSimple("§e§l传送系统 - 公共传送点设置","请选择您所需要编辑的传送点");
        if(warpconfig.get("list") != null) {
            List<String> warplist = new ArrayList<>(warpconfig.getStringList("list"));
            for (String wpn : warplist) {
                form.addButton(new ElementButton(wpn));
            }
        }
        player.showFormWindow(form, WarpsSettingMenu);
    }

    public void homemainmenu(Player player){
        FormWindowSimple form = new FormWindowSimple("§e§l传送系统 - 设置家","请选择需要使用的功能");
        form.addButton(new ElementButton("新建家于此处"));
        form.addButton(new ElementButton("传送到家"));
        form.addButton(new ElementButton("删除家"));
        form.addButton(new ElementButton("返回"));
        player.showFormWindow(form, HomeMainMenu);
    }
    public void HomeTeleportMenu(Player player){
        FormWindowSimple form = new FormWindowSimple("§e§l传送系统 - 你的家","请选择需要使用的功能");
        Config hc = new Config(this.getDataFolder()+"/homes/"+player.getName()+".yml",Config.YAML);
        List<String> arr = new ArrayList<>(hc.getStringList("list"));
        for(String n : arr){
            Config pointc = new Config(this.getDataFolder()+"/homes/"+player.getName()+"/"+n+".yml",Config.YAML);
            String intro = pointc.getString("简介");
            form.addButton(new ElementButton("名称:"+ n +"\n"+"简介"+intro));
        }
        player.showFormWindow(form, HomeTeleportMenu);
    }
    public void HomeCreateMenu(Player player){
        FormWindowCustom form = new FormWindowCustom("§e§l传送系统 - 创建家");
        form.addElement(new ElementLabel("当前坐标:"+player.getX()+","+player.getY()+","+player.getZ()+"\n"+"所在世界:"+player.getLevel().getName()));
        form.addElement(new ElementInput("名称","这里填传送点名，不要重复！"));
        form.addElement(new ElementInput("简介","无"));
        player.showFormWindow(form, HomeCreateMenu);
    }
    public void warpsmanagedownmenu(Player player){
        FormWindowSimple form = new FormWindowSimple("§e§l传送系统 - 传送点系统","请选择您需要的功能");
        form.addButton(new ElementButton("设置传送点"));
        form.addButton(new ElementButton("创建传送点"));
        form.addButton(new ElementButton("删除传送点"));
        form.addButton(new ElementButton("返回"));
        player.showFormWindow(form, WarpsSettingManageMenu);
    }
    public void homedeletemenu(Player player){
        Config warpscfg = new Config(this.getDataFolder()+"/homes/"+player.getName()+".yml",Config.YAML);
        FormWindowCustom form = new FormWindowCustom("§e§l传送系统 - 家删除");
        List<String> arr = new ArrayList<>(warpscfg.getStringList("list"));
        form.addElement(new ElementDropdown("请选择要删除的内容",arr));
        player.showFormWindow(form, HomeDeleteMenu);
    }
    public void warpssettingdownmenu(Player player, String name) {
        if(checktrust(player)){ return; }
        Config warpscfg = new Config(this.getDataFolder()+"/warps/"+ name +".yml",Config.YAML);
        if(!warpscfg.exists("x") || !warpscfg.exists("y") || !warpscfg.exists("z") || !warpscfg.exists("world") || !warpscfg.exists("state")){
            FormWindowSimple form = new FormWindowSimple("§e§l传送系统 - 公共传送点设置", "§c§l该传送点配置出错，请在后台修改！");
            form.addButton(new ElementButton("返回"));
            player.showFormWindow(form, ErrorMenu);
            return;
        }
        FormWindowCustom form = new FormWindowCustom("§e§l传送系统 - 公共传送点设置");
        form.addElement(new ElementLabel(name));
        form.addElement(new ElementInput("§e传送点名称",warpscfg.getString("name")));
        form.addElement(new ElementInput("§ex轴坐标",String.valueOf(warpscfg.getDouble("x"))));
        form.addElement(new ElementInput("§ey轴坐标",String.valueOf(warpscfg.getDouble("y"))));
        form.addElement(new ElementInput("§ez轴坐标",String.valueOf(warpscfg.getDouble("z"))));
        form.addElement(new ElementInput("§e世界名称",warpscfg.getString("world")));
        form.addElement(new ElementToggle("§e是否允许进入",warpscfg.getBoolean("state")));
        player.showFormWindow(form, WarpSettingDownMenu);
    }
    public void warpscreatemenu(Player player) {
        if(checktrust(player)){ return; }
        FormWindowCustom form = new FormWindowCustom("§e§l传送系统 - 公共传送点创建");
        form.addElement(new ElementInput("§e传送点标记","这是后台配置时的文件名"));
        form.addElement(new ElementInput("§e传送点名称","这里填名字"));
        form.addElement(new ElementInput("§ex轴坐标","0.000000"));
        form.addElement(new ElementInput("§ey轴坐标","60.000000"));
        form.addElement(new ElementInput("§ez轴坐标","130.000000"));
        form.addElement(new ElementInput("§e世界名称","world"));
        form.addElement(new ElementToggle("§e是否允许进入",false));
        player.showFormWindow(form, WarpsSettingCreateMenu);
    }
    public void warpsdeletemenu(Player player) {
        Config warpscfg = new Config(this.getDataFolder()+"/warps.yml",Config.YAML);
        if(checktrust(player)){ return; }
        FormWindowCustom form = new FormWindowCustom("§e§l传送系统 - 公共传送点删除");
        List<String> arr = new ArrayList<>(warpscfg.getStringList("list"));
        form.addElement(new ElementDropdown("请选择要删除的内容",arr));
        player.showFormWindow(form, WarpsSettingDeleteMenu);
    }
    public void worldmenu(Player player){
        Config config = new Config(this.getDataFolder()+"/config.yml",Config.YAML);
        List<String> whiteworld = config.getStringList("世界白名单");
        FormWindowSimple form = new FormWindowSimple("§e§l传送系统 - 多世界传送","所有允许传送的世界已经展示在此");
        Map<Integer, Level> levels = this.getServer().getLevels();
        Collection<Level> wlist = levels.values();
        for(Level level: wlist){
            String LevelName = level.getName();
            if(whiteworld.contains(LevelName)) {
                form.addButton(new ElementButton(LevelName));
            }
        }
        form.addButton(new ElementButton("返回"));
        player.showFormWindow(form, WorldTeleportMenu);
    }
    public void worldteleport(Player p,Level level){
        if(this.getServer().getLevels().containsValue(level)) {
            Position spawnpos = level.getSpawnLocation();
            Position pos = new Position(spawnpos.x, spawnpos.y, spawnpos.z, level);
            p.teleport(pos);
            p.sendMessage("正在传送，请稍后...");
        }else{
            p.sendMessage("世界不存在！");
        }
    }

    public void tp(Player asker,Player player){
        if(!player.isOnline()){ return; }
        if(!asker.isOnline()){ return; }
        Position position = player.getPosition();
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();
        Level level = player.getLevel();
        asker.setLevel(level);
        asker.setPosition(new Position(x,y,z));
        player.sendMessage("§e成功同意该玩家请求");
    }

    @EventHandler
    public void onFormResponse(PlayerFormRespondedEvent event) {
        Player p = event.getPlayer();
        int formID = event.getFormID();
        if(formID == MainMenu){ //传送点
            FormResponseSimple response = (FormResponseSimple) event.getResponse();
            if(response == null){ return; }
            int buttonId = response.getClickedButtonId();
            switch (buttonId){
                case 0:
                    this.warpsmenu(p);
                    break;
                case 1:
                    this.homemainmenu(p);
                    break;
                case 2:
                    this.teleportmenu(p,0);
                    break;
                case 3:
                    this.teleportmenu(p,1);
                    break;
                case 4:
                    this.settingmenu(p);
                    break;
                case 5:
                    this.worldmenu(p);
                    break;
                case 6:
                    if(this.checktrust(p)){ return; }
                    Map<java.util.UUID, Player> pl = this.getServer().getOnlinePlayers();
                    List<Player> list = new ArrayList<>(pl.values());
                    for (Player p1 : list) {
                        this.tp(p1,p);
                        p1.sendMessage("§e"+p.getName()+"强制传送了你!");
                        p.sendMessage("§e"+p.getName()+"被您的强制请求传送到你的位置!");
                    }
                    break;
                case 7:
                    this.warpsmanagedownmenu(p);
                    break;
                case 8:
                    this.mainsettingmenu(p);
                default:
                    break;
            }
        }
        if(formID == HomeMainMenu){
            FormResponseSimple response = (FormResponseSimple) event.getResponse();
            if(response == null){ return; }
            switch(response.getClickedButtonId()){
                case 0: //create
                    this.HomeCreateMenu(p);
                    break;
                case 1: //delete
                    this.HomeTeleportMenu(p);
                    break;
                case 2: //return
                    this.homedeletemenu(p);
                    break;
                case 3: //return
                    this.mainmenu(p);
                    break;
            }
        }
        if(formID == HomeDeleteMenu){
            FormResponseCustom response = (FormResponseCustom) event.getResponse();
            if(response == null){ return; }
            if(response.getDropdownResponse(0).getElementContent() == null || response.getDropdownResponse(0).getElementContent().equals("")){
                p.sendMessage("设置有误！");
                return;
            }
            Config wlcfg = new Config(this.getDataFolder()+"/homes/"+p.getName()+".yml",Config.YAML);
            List<String> arr = new ArrayList<>(wlcfg.getStringList("list"));
            arr.remove(response.getDropdownResponse(0).getElementContent());
            wlcfg.set("list",arr);
            wlcfg.save();
            FormWindowSimple form1 = new FormWindowSimple("§e§l传送系统", "§e已移除点【"+response.getDropdownResponse(0).getElementContent()+"】");
            String path = this.getDataFolder()+"/homes/"+p.getName()+".yml";
            File file = new File(path);
            file.delete();
            form1.addButton(new ElementButton("返回"));
            p.showFormWindow(form1, ErrorMenu);
            return;
        }
        if(formID == HomeTeleportMenu){
            FormResponseSimple response = (FormResponseSimple) event.getResponse();
            if(response == null){ return; }
            String[] strArr = response.getClickedButton().getText().split("\n");
            String[] sArr = strArr[0].split(":");
            p.sendMessage("正在传送到家:" + sArr[1]);
            Config pointc = new Config(this.getDataFolder()+"/homes/"+p.getName()+"/"+sArr[1]+".yml",Config.YAML);
            List<Double> position = pointc.getDoubleList("坐标");
            String world = pointc.getString("世界");
            String intro = pointc.getString("简介");
            p.setPosition(new Position(position.get(0),position.get(1),position.get(2)));
            p.setLevel(this.getServer().getLevelByName(world));
            return;
        }
        if (formID == HomeCreateMenu){
            Config config1 = new Config(this.getDataFolder()+"/config.yml",Config.YAML);
            cost = config1.getDouble("设置家花费");
            if(EconomyAPI.getInstance().myMoney(p) < cost){p.sendMessage("§e您的货币不足");return;}
            EconomyAPI.getInstance().reduceMoney(p, cost);
            Config pointc = null;
            FormResponseCustom response = (FormResponseCustom) event.getResponse();
            if(response == null){ return; }
            if(response.getInputResponse(1) != null && !response.getInputResponse(1).equals("")){
                pointc = new Config(this.getDataFolder()+"/homes/"+p.getName()+"/"+response.getInputResponse(1)+".yml",Config.YAML);
            }
            for (int i=0;i<4;i++) {
                if (response.getInputResponse(i) != null && !response.getInputResponse(i).equals("")) {
                    if (i == 2) {
                        pointc.set("简介", response.getInputResponse(2));
                    }
                }
            }
            Config plc = new Config(this.getDataFolder()+"/homes/"+p.getName()+".yml",Config.YAML);
            List<String> plcarr = new ArrayList<String>(plc.getStringList("list"));
            plcarr.add(response.getInputResponse(1));
            plc.set("list",plcarr);
            plc.save();
            List<Double> arr = new ArrayList<Double>();
            arr.add(p.getX());
            arr.add(p.getY());
            arr.add(p.getZ());
            pointc.set("坐标",arr);
            pointc.set("世界",p.getLevel().getName());
            pointc.save();
            FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e创建传送点["+response.getInputResponse(1)+"]成功,本次花费了货币"+cost);
            form.addButton(new ElementButton("返回"));
            p.showFormWindow(form, ErrorMenu);
            return;
        }
        if(formID == ErrorMenu){
            this.mainmenu(p);
        }
        if(formID == WarpMenu){ //Warp系统
            FormResponseSimple response = (FormResponseSimple) event.getResponse();
            if(response == null){ return; }
            String selected = response.getClickedButton().getText();
            Config warpconfig = new Config(this.getDataFolder()+"/warps/"+selected+".yml",Config.YAML);
            String placename = warpconfig.getString("name");
            if(selected.equals("返回")){
                this.mainmenu(p);
            }else{
                Double x,y,z;
                String world,name;
                Boolean state;
                if(warpconfig.exists("x")){
                    x = warpconfig.getDouble("x");
                }else{
                    FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e该传送点未设置完毕，无法进行传送");
                    form.addButton(new ElementButton("返回"));
                    p.showFormWindow(form, ErrorMenu);
                    return;
                }
                if(warpconfig.exists("y")){
                    y = warpconfig.getDouble("y");
                }else{
                    FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e该传送点未设置完毕，无法进行传送");
                    form.addButton(new ElementButton("返回"));
                    p.showFormWindow(form, ErrorMenu);
                    return;
                }
                if(warpconfig.exists("z")){
                    z = warpconfig.getDouble("z");
                }else{
                    FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e该传送点未设置完毕，无法进行传送");
                    form.addButton(new ElementButton("返回"));
                    p.showFormWindow(form, ErrorMenu);
                    return;
                }
                if(warpconfig.exists("world")){
                    world = warpconfig.getString("world");
                }else{
                    FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e该传送点未设置完毕，无法进行传送");
                    form.addButton(new ElementButton("返回"));
                    p.showFormWindow(form, ErrorMenu);
                    return;
                }
                if(warpconfig.exists("name")){
                    name = warpconfig.getString("name");
                }else{
                    FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e该传送点未设置完毕，无法进行传送");
                    form.addButton(new ElementButton("返回"));
                    p.showFormWindow(form, ErrorMenu);
                    return;
                }
                if(warpconfig.exists("state")){
                    state = warpconfig.getBoolean("state");
                }else{
                    FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e该传送点未设置完毕，无法进行传送");
                    form.addButton(new ElementButton("返回"));
                    p.showFormWindow(form, ErrorMenu);
                    return;
                }
                if(!state) {
                    FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e传送点【"+placename+"】暂时关闭!");
                    form.addButton(new ElementButton("返回"));
                    p.showFormWindow(form, ErrorMenu);
                    return;
                }
                p.sendMessage("正在传送到传送点:"+placename);
                p.setLevel(this.getServer().getLevelByName(world));
                p.setPosition(new Position(x,y,z));
                return;
            }
        }
        if(formID == SETTINGMENU){ //设置系统
            FormResponseCustom  response = (FormResponseCustom) event.getResponse();
            Config pconfig = new Config(this.getDataFolder()+"/player/"+p.getName()+".yml",Config.YAML);
            if(response == null){ return; }
            Boolean state = response.getToggleResponse(0);
            pconfig.set("自动接受传送请求",state);
            pconfig.save();
            if(response.getToggleResponse(1)){
                Config cfg = new Config(this.getDataFolder()+"/config.yml",Config.YAML);
                cost = cfg.getDouble("设置重生点花费");
                if(EconomyAPI.getInstance().myMoney(p) < cost){p.sendMessage("§e您的货币不足");return;}
                EconomyAPI.getInstance().reduceMoney(p, cost);
                event.getPlayer().setSpawn(p.getPosition());
            }
            FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e设置已保存");
            form.addButton(new ElementButton("返回"));
            p.showFormWindow(form, ErrorMenu);
            return;
        }
        if(formID == MainSETTINGMENU){ //设置系统
            FormResponseCustom  response = (FormResponseCustom) event.getResponse();
            Config pconfig = new Config(this.getDataFolder()+"/config.yml",Config.YAML);
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
            FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e设置已保存");
            form.addButton(new ElementButton("返回"));
            p.showFormWindow(form, ErrorMenu);
            return;
        }
        if(formID == WarpsSettingManageMenu){
            FormResponseSimple response = (FormResponseSimple) event.getResponse();
            if(response == null){ return; }
            int selected = response.getClickedButtonId();
            switch (selected){
                case 0:
                    this.warpssettingmenu(p);
                    break;
                case 1:
                    this.warpscreatemenu(p);
                    break;
                case 2:
                    this.warpsdeletemenu(p);
                    break;
                case 3:
                    this.mainmenu(p);
                default:
                    return;
            }
        }
        if(formID == WarpsSettingDeleteMenu){
            FormResponseCustom response = (FormResponseCustom) event.getResponse();
            Config wlcfg = new Config(this.getDataFolder()+"/warps.yml",Config.YAML);
            List<String> arr = new ArrayList<String>(wlcfg.getStringList("list"));
            arr.remove(response.getDropdownResponse(0).getElementContent());
            wlcfg.set("list",arr);
            wlcfg.save();
            String path = this.getDataFolder()+"/warps/"+response.getDropdownResponse(0).getElementContent()+".yml";
            File file = new File(path);
            file.delete();
            FormWindowSimple form1 = new FormWindowSimple("§e§l传送系统", "§e已移除点【"+response.getDropdownResponse(0).getElementContent()+"】");
            form1.addButton(new ElementButton("返回"));
            p.showFormWindow(form1, ErrorMenu);
            return;
        }
        if(formID == WarpSettingDownMenu){
            Config cachecfg = new Config(this.getDataFolder()+"/warpsettingcache.yml",Config.YAML);
            String defaultn = cachecfg.getString(p.getName());
            FormResponseCustom response = (FormResponseCustom) event.getResponse();
            String name,world;
            Double x,y,z;
            if(response == null){
                FormWindowSimple form1 = new FormWindowSimple("§e§l传送系统", "§e设置无变动！");
                form1.addButton(new ElementButton("返回"));
                p.showFormWindow(form1, ErrorMenu);
                return;
            }
            Config cfg = new Config(this.getDataFolder()+"/warps/"+defaultn+".yml",Config.YAML);
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
                            x = Double.valueOf(response.getInputResponse(2));
                            cfg.set("x", x);
                            cfg.save();
                            break;
                        case 3:
                            y = Double.valueOf(response.getInputResponse(3));
                            cfg.set("y", y);
                            cfg.save();
                            break;
                        case 4:
                            z = Double.valueOf(response.getInputResponse(4));
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
            FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e设置已保存");
            form.addButton(new ElementButton("返回"));
            p.showFormWindow(form, ErrorMenu);
            return;
        }
        if(formID == WarpsSettingCreateMenu){
            FormResponseCustom response = (FormResponseCustom) event.getResponse();
            String name,world;
            Double x,y,z;
            if(response == null){
                FormWindowSimple form1 = new FormWindowSimple("§e§l传送点系统", "§e您输入的信息不完全！");
                form1.addButton(new ElementButton("返回"));
                p.showFormWindow(form1, ErrorMenu);
                return;
            }
            Config wlcfg = new Config(this.getDataFolder()+"/warps.yml",Config.YAML);
            List<String> Arr = new ArrayList<String>(wlcfg.getStringList("list"));
            if(Arr.contains(response.getInputResponse(0))){
                FormWindowSimple form1 = new FormWindowSimple("§e§l传送点系统", "§e您已经创建过这个传送点了！");
                form1.addButton(new ElementButton("返回"));
                p.showFormWindow(form1, ErrorMenu);
                return;
            }
            if(response.getInputResponse(0) == null || response.getInputResponse(0).equals("")){
                FormWindowSimple form1 = new FormWindowSimple("§e§l传送点系统", "§e您输入的信息有误！");
                form1.addButton(new ElementButton("返回"));
                p.showFormWindow(form1, ErrorMenu);
                return;
            }
            Arr.add(response.getInputResponse(0));
            wlcfg.set("list",Arr);
            wlcfg.save();
            Config cfg = new Config(this.getDataFolder()+"/warps/"+response.getInputResponse(0)+".yml",Config.YAML);
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
                            x = Double.valueOf(response.getInputResponse(2));
                            cfg.set("x", x);
                            cfg.save();
                            break;
                        case 3:
                            y = Double.valueOf(response.getInputResponse(3));
                            cfg.set("y", y);
                            cfg.save();
                            break;
                        case 4:
                            z = Double.valueOf(response.getInputResponse(4));
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
                            name = response.getInputResponse(1);
                            cfg.set("name", "default");
                            cfg.save();
                            break;
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
            FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e创建传送点【"+response.getInputResponse(0)+"】成功");
            form.addButton(new ElementButton("返回"));
            p.showFormWindow(form, ErrorMenu);
            return;
        }
        if(formID == TeleportInitiativeMENU){ //传送系统
            FormResponseSimple response = (FormResponseSimple) event.getResponse();
            if(response == null){ return; }
            String selected = response.getClickedButton().getText();
            Player selectedp = this.getServer().getPlayer(selected);
            Config pconfig = new Config(this.getDataFolder()+"/player/"+selected+".yml",Config.YAML);
            Boolean bool = pconfig.getBoolean("自动接受传送请求");
            if(p.isOp() || bool){ this.tp(p,selectedp); return; }
            this.acceptmenu(p,selectedp,0);
            return;
        }
        if(formID == TeleportPassiveMENU){ //传送系统
            FormResponseSimple response = (FormResponseSimple) event.getResponse();
            if(response == null){ return; }
            String selected = response.getClickedButton().getText();
            Player selectedp = this.getServer().getPlayer(selected);
            Config pconfig = new Config(this.getDataFolder()+"/player/"+selected+".yml",Config.YAML);
            Boolean bool = pconfig.getBoolean("自动接受传送请求");
            if(p.isOp() || bool){ this.tp(selectedp,p); return; }
            this.acceptmenu(p,selectedp,1);
            return;
        }
        if(formID == AcceptListInitiativeMENU){ //接受邀请
            FormResponseSimple response = (FormResponseSimple) event.getResponse();
            if(response == null){ return; }
            int buttonId = response.getClickedButtonId();
            String name = response.getClickedButton().getText();
            if(buttonId == 0){
                this.tp(this.getServer().getPlayer(name), p);
                this.getServer().getPlayer(name).sendMessage("§e已被传送至该玩家处！本次传送花费了货币"+cost);
            }
            return;
        }
        if(formID == AcceptListPassiveMENU){ //将玩家传送到你 你接受
            FormResponseSimple response = (FormResponseSimple) event.getResponse();
            if(response == null){ return; }
            int buttonId = response.getClickedButtonId();
            String name = response.getClickedButton().getText();
            if(buttonId == 0){
                this.tp(this.getServer().getPlayer(name),p);
                this.getServer().getPlayer(name).sendMessage("§e已传送该玩家到你的位置！本次传送花费了货币"+cost);
            }
        }
        if(formID == WorldTeleportMenu){
            FormResponseSimple response = (FormResponseSimple) event.getResponse();
            if(response == null){ return; }
            if(response.getClickedButton().getText().equals("返回")) {
                this.mainmenu(p);
            }else{
                this.worldteleport(p, this.getServer().getLevelByName(response.getClickedButton().getText()));
            }
        }
    }
}