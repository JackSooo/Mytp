package glorydark;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.Listener;
import cn.nukkit.form.element.*;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import glorydark.event.eventlistener;
import glorydark.gui.guilistener;
import glorydark.gui.guitype;
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
    public static String path = null;
    public static double cost = 0;

    @Override
    public void onLoad(){
        saveDefaultConfig();
        this.getLogger().info("DEssential Onloaded!");
        path = this.getDataFolder().getPath();
    }

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this); // 注册Event
        this.getServer().getPluginManager().registerEvents(new guilistener(), this); // 注册菜单监听Event
        this.getServer().getPluginManager().registerEvents(new eventlistener(), this); // 注册事件监听Event
        this.getLogger().info("DEssential Enabled!");
        this.saveResource("config.yml",false);
        loadLevel();
        Config config = new Config(path+"/config.yml",Config.YAML);
        if(!config.exists("设置重生点花费")){
            config.set("设置重生点花费","1000.000000");
        }
        if(!config.exists("版本号")){
            config.set("版本号",20210530);
        }else{
            if(!config.getString("版本号").equals("20210530")){
                ArrayList<String> strl = new ArrayList<String>();
                strl.add("------ Mytp Manual ------");
                strl.add("打开菜单 /mytp open");
                strl.add("添加白名单 /mytp 添加白名单 玩家昵称 (后台进行)");
                strl.add("删除白名单 /mytp 删除白名单 玩家昵称 (后台进行)");
                strl.add("添加世界白名单 /mytp addwwl 地图名称 玩家昵称 (后台进行)");
                strl.add("删除世界白名单 /mytp delwwl 地图名称 玩家昵称 (后台进行)");
                strl.add("------ Mytp Manual ------");
                config.set("帮助", strl);
            }
        }
        if(!config.exists("帮助")) {
            ArrayList<String> strl = new ArrayList<String>();
            strl.add("------ Mytp Manual ------");
            strl.add("打开菜单 /mytp open");
            strl.add("添加白名单 /mytp 添加白名单 玩家昵称 (后台进行)");
            strl.add("删除白名单 /mytp 删除白名单 玩家昵称 (后台进行)");
            strl.add("添加世界白名单 /mytp addwwl 地图名称 玩家昵称 (后台进行)");
            strl.add("删除世界白名单 /mytp delwwl 地图名称 玩家昵称 (后台进行)");
            strl.add("------ Mytp Manual ------");
            config.set("帮助", strl);
        }
        if(!config.exists("是否使用快捷工具")){
            config.set("是否使用快捷工具", true);
        }
        if(!config.exists("快捷工具ID")){
            config.set("快捷工具ID", 347);
        }
        if(!config.exists("是否启用打开音效")){
            config.set("是否启用打开音效", true);
        }
        config.save();
    }

    public void loadLevel(){
        for(String worldName: getWorlds()){
            if(!this.getServer().isLevelLoaded(worldName)){
                if(this.getServer().isLevelGenerated(worldName)){
                    this.getLogger().info("地图加载中，地图名:"+worldName);
                    this.getServer().loadLevel(worldName);
                }
            }
        }
    }

    public ArrayList<String> getWorlds(){
        ArrayList<String> worlds = new ArrayList<>();
        File file = new File(this.getServer().getFilePath()+"/worlds");
        File[] s = file.listFiles();
        if(s != null) {
            for (File file1 : s) {
                if (file1.isDirectory()) {
                    worlds.add(file1.getName());

                }
            }
        }
        return worlds;
    }

    @Override
    public void onDisable() {
        this.getLogger().info("DEssential Disabled!");
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(command.getName().equals("mytp") && args.length >= 1) {
            switch (args[0]) {
                case "help":
                    Config config = new Config(path+"/config.yml",Config.YAML);
                    if(config.exists("帮助")){
                        if(!config.getStringList("帮助").isEmpty()) {
                            for (String s : config.getStringList("帮助")) {
                                sender.sendMessage(s);
                            }
                        }
                    }else{
                        sender.sendMessage("帮助文件信息缺少,请删除原有config.yml重启服务器!");
                    }
                    break;
                case "open":
                    if (sender instanceof Player) {
                        this.mainmenu(this.getServer().getPlayer(sender.getName()));
                        Config config1 = new Config(path+"/config.yml",Config.YAML);
                        if(config1.getBoolean("是否启用打开音效",true)) {
                            this.getServer().getPlayer(sender.getName()).getLevel().addSound(this.getServer().getPlayer(sender.getName()), Sound.RANDOM_LEVELUP);
                        }
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
                case "addwwl":
                    if (!(sender instanceof Player)) {
                        if(args.length == 3) {
                            if(this.addWorldtrust(args[1],args[2])) {
                                sender.sendMessage("给予玩家【"+args[2]+"】 世界【"+args[1]+"白名单成功!");
                            }else{
                                sender.sendMessage("设置失败，请检查是否为指令书写问题。格式为 /mytp 添加世界白名单 世界名称 玩家昵称");
                            }
                        }else{
                            sender.sendMessage("请填写玩家名字!");
                        }
                    }else{
                        sender.sendMessage("请在控制台使用本指令!");
                    }
                    break;
                case "delwwl":
                    if (!(sender instanceof Player)) {
                        if(args.length == 3) {
                            if(this.removeWorldtrust(args[1],args[2])) {
                                sender.sendMessage("移除玩家【"+args[2]+"】 世界【"+args[1]+"】白名单成功!");
                            }else{
                                sender.sendMessage("设置失败，请检查是否为指令书写问题。格式为 /mytp 添加世界白名单 世界名称 玩家昵称");
                            }
                        }else{
                            sender.sendMessage("请填写玩家名字!");
                        }
                    }else{
                        sender.sendMessage("请在控制台使用本指令!");
                    }
                    break;
                default:
                    sender.sendMessage(TextFormat.YELLOW+"------ Mytp Manual ------");
                    sender.sendMessage(TextFormat.YELLOW+"打开菜单 /mytp open");
                    sender.sendMessage(TextFormat.YELLOW+"设置白名单 /mytp 设置白名单 玩家昵称 (后台进行)");
                    sender.sendMessage(TextFormat.YELLOW+"删除白名单 /mytp 删除白名单 玩家昵称 (后台进行)");
                    sender.sendMessage(TextFormat.YELLOW+"查看帮助 /mytp help 玩家昵称");
                    sender.sendMessage(TextFormat.YELLOW+"------ Mytp Manual ------");
                    break;
            }
            return true;
        }
        return false;
    }
    public Boolean addtrust(String pn){
        Config trustlist = new Config(path+"/trust.yml",Config.YAML);
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
        Config trustlist = new Config(path+"/trust.yml",Config.YAML);
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
    public Boolean addWorldtrust(String LevelName,String pn){
        Config worldwhitelist = new Config(path+"/worldwhitelist.yml",Config.YAML);
        List<String> arrayList = new ArrayList<>(worldwhitelist.getStringList(LevelName));
        if(arrayList.contains(pn)) {
            return false;
        }else{
            arrayList.add(pn);
            worldwhitelist.set(LevelName,arrayList);
            worldwhitelist.save();
            return true;
        }
    }
    public Boolean removeWorldtrust(String LevelName,String pn){
        Config worldwhitelist = new Config(path+"/worldwhitelist.yml",Config.YAML);
        List<String> arrayList = new ArrayList<>(worldwhitelist.getStringList(LevelName));
        if(arrayList.contains(pn)) {
            for (int i = 0;i<arrayList.size();i++){
                if(arrayList.get(i).equals(pn)){
                    arrayList.remove(i);
                    worldwhitelist.set(LevelName,arrayList);
                    worldwhitelist.save();
                }
            }
            return true;
        }else{
            return false;
        }
    }

    public static void mainmenu(Player player) {
        if(!player.isOnline()){ return; }
        FormWindowSimple form = new FormWindowSimple("传送系统", "您好，玩家"+player.getName()+"，欢迎使用本传送系统，您的货币剩余"+EconomyAPI.getInstance().myMoney(player));
        form.addButton(new ElementButton(TextFormat.RED+"§l公共传送点 \n [ 选择前往的公共传送点 ]",new ElementButtonImageData("path","textures/items/diamond.png")));
        form.addButton(new ElementButton(TextFormat.BLUE+"§l设置家 \n [ 设置自己的家的传送 ]",new ElementButtonImageData("path","textures/items/iron_ingot.png")));
        form.addButton(new ElementButton(TextFormat.DARK_GREEN+"§l传送到玩家",new ElementButtonImageData("path","textures/blocks/shroomlight.png")));
        form.addButton(new ElementButton(TextFormat.DARK_PURPLE+"§l将玩家传送到你",new ElementButtonImageData("path","textures/blocks/ender_chest_front.png")));
        form.addButton(new ElementButton(TextFormat.DARK_AQUA+"§l我的设置 \n [ 设置自动接受邀请或重生点 [",new ElementButtonImageData("path","textures/items/villagebell.png")));
        form.addButton(new ElementButton(TextFormat.DARK_BLUE+"§l世界传送 \n [ 在世界中来回穿梭 ]"));
        form.addButton(new ElementButton(TextFormat.DARK_BLUE+"§l返回上次死亡点 \n [ 回到死亡点，重新开始 ]"));
        if(player.isOp()){
            form.addButton(new ElementButton(TextFormat.BOLD+"§l[OP]全体传送"));
            form.addButton(new ElementButton(TextFormat.BOLD+"§l[OP]传送点设置"));
            form.addButton(new ElementButton(TextFormat.BOLD+"§l[OP]传送系统设置"));
        }
        guilistener.showFormWindow(player, form, guitype.MainMenu);
    }

    public static void warpsmenu(Player player) { //传送点系统
        if(!player.isOnline()){ return; }
        FormWindowSimple form = new FormWindowSimple("§e传送系统", "§e§l传送点系统");
        Config warpconfig = new Config(path+"/warps.yml",Config.YAML);
        if(warpconfig.get("list") != null) {
            List<String> warplist = new ArrayList<>(warpconfig.getStringList("list"));
            for (String wpn : warplist) {
                form.addButton(new ElementButton(wpn));
            }
        }
        form.addButton(new ElementButton("返回"));
        guilistener.showFormWindow(player, form, guitype.WarpMenu);
    }

    public static void teleportmenu(Player player, int type) {
        switch (type){
            case 0:
                if (!player.isOnline()) {
                    return;
                }
                FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e选择你要传送到的玩家");
                Map<java.util.UUID, Player> pl = player.getServer().getOnlinePlayers();
                List<Player> list = new ArrayList<>(pl.values());
                for (Player p : list) {
                    form.addButton(new ElementButton(p.getName()));
                }
                guilistener.showFormWindow(player, form, guitype.TeleportInitiativeMENU);
                break;
            case 1:
                if (!player.isOnline()) {
                    return;
                }
                FormWindowSimple form1 = new FormWindowSimple("§e§l传送系统", "§e选择你要邀请玩家");
                Map<java.util.UUID, Player> pl1 = player.getServer().getOnlinePlayers();
                List<Player> list1 = new ArrayList<>(pl1.values());
                for (Player p : list1) {
                    form1.addButton(new ElementButton(p.getName()));
                }
                guilistener.showFormWindow(player, form1, guitype.TeleportPassiveMENU);
                break;
            default:
                break;
        }
    }

    public static void acceptmenu(Player asker, Player player, int type) {
        switch (type){
            case 0:
                Config config = new Config(path+"/config.yml",Config.YAML);
                cost = config.getDouble("传送邀请花费");
                if(EconomyAPI.getInstance().myMoney(asker) < cost){asker.sendMessage("§e您的货币不足");return;}
                EconomyAPI.getInstance().reduceMoney(asker, cost);
                if(!player.isOnline()){ return; }
                FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§e§l申请列表");
                form.addButton(new ElementButton(asker.getName()));
                guilistener.showFormWindow(player, form, guitype.AcceptListInitiativeMENU);
                break;
            case 1:
                Config config1 = new Config(path+"/config.yml",Config.YAML);
                cost = config1.getDouble("传送邀请花费");
                if(EconomyAPI.getInstance().myMoney(asker) < cost){asker.sendMessage("§e您的货币不足");return;}
                EconomyAPI.getInstance().reduceMoney(asker, cost);
                if(!player.isOnline()){ return; }
                FormWindowSimple form1 = new FormWindowSimple("§e§l传送系统", "§e§l申请列表");
                form1.addButton(new ElementButton(asker.getName()));
                guilistener.showFormWindow(player, form1, guitype.AcceptListPassiveMENU);
                break;
            default:
                break;
        }
    }

    public static void settingmenu(Player player) {
        Config pconfig = new Config(path+"/player/"+player.getName()+".yml",Config.YAML);
        FormWindowCustom form = new FormWindowCustom("§e§l传送系统 - 个人设置");
        form.addElement(new ElementToggle("§e开/关自动接受申请",pconfig.getBoolean("自动接受传送请求")));
        form.addElement(new ElementToggle("§e重新设置出生点(需花费一定金币)", false));
        guilistener.showFormWindow(player, form, guitype.SETTINGMENU);
    }
    public static void mainsettingmenu(Player player) {
        if(checktrust(player)){ return; }
        Config pconfig = new Config(path+"/config.yml",Config.YAML);
        FormWindowCustom form = new FormWindowCustom("§e§l传送系统 - 系统设置");
        form.addElement(new ElementInput("§e传送邀请花费",pconfig.getString("传送邀请花费")));
        form.addElement(new ElementInput("§e设置家花费",pconfig.getString("设置家花费")));
        form.addElement(new ElementInput("§e设置重生点",pconfig.getString("设置重生点花费")));
        guilistener.showFormWindow(player, form, guitype.MainSETTINGMENU);
    }
    public static boolean checktrust(Player p){
        Config trustlist = new Config(path+"/trust.yml",Config.YAML);
        List<String> arrayList = new ArrayList<>(trustlist.getStringList("list"));
        if(arrayList.contains(p.getName())) {
            return false;
        }else{
            FormWindowSimple form = new FormWindowSimple("§e§l传送系统", "§c§l您未取得信任！");
            form.addButton(new ElementButton("返回"));
            guilistener.showFormWindow(p, form, guitype.ErrorMenu);
            return true;
        }
    }
    public static void warpssettingmenu(Player player) {
        if(checktrust(player)){ return; }
        Config warpconfig = new Config(path+"/warps.yml",Config.YAML);
        FormWindowSimple form = new FormWindowSimple("§e§l传送系统 - 公共传送点设置","请选择您所需要编辑的传送点");
        if(warpconfig.get("list") != null) {
            List<String> warplist = new ArrayList<>(warpconfig.getStringList("list"));
            for (String wpn : warplist) {
                form.addButton(new ElementButton(wpn));
            }
        }
        guilistener.showFormWindow(player, form, guitype.WarpsSettingMenu);
    }

    public static void homemainmenu(Player player){
        FormWindowSimple form = new FormWindowSimple("§e§l传送系统 - 设置家","请选择需要使用的功能");
        form.addButton(new ElementButton("新建家于此处"));
        form.addButton(new ElementButton("传送到家"));
        form.addButton(new ElementButton("删除家"));
        form.addButton(new ElementButton("返回"));
        guilistener.showFormWindow(player, form, guitype.HomeMainMenu);
    }
    public static void HomeTeleportMenu(Player player){
        FormWindowSimple form = new FormWindowSimple("§e§l传送系统 - 你的家","请选择需要使用的功能");
        Config hc = new Config(path+"/homes/"+player.getName()+".yml",Config.YAML);
        List<String> arr = new ArrayList<>(hc.getStringList("list"));
        for(String n : arr){
            Config pointc = new Config(path+"/homes/"+player.getName()+"/"+n+".yml",Config.YAML);
            String intro = pointc.getString("简介");
            form.addButton(new ElementButton("名称:"+ n +"\n"+"简介"+intro));
        }
        guilistener.showFormWindow(player, form, guitype.HomeTeleportMenu);
    }
    public static void HomeCreateMenu(Player player){
        FormWindowCustom form = new FormWindowCustom("§e§l传送系统 - 创建家");
        form.addElement(new ElementLabel("当前坐标:"+player.getX()+","+player.getY()+","+player.getZ()+"\n"+"所在世界:"+player.getLevel().getName()));
        form.addElement(new ElementInput("名称","这里填传送点名，不要重复！"));
        form.addElement(new ElementInput("简介","无"));
        guilistener.showFormWindow(player, form, guitype.HomeCreateMenu);
    }
    public static void warpsmanagedownmenu(Player player){
        FormWindowSimple form = new FormWindowSimple("§e§l传送系统 - 传送点系统","请选择您需要的功能");
        form.addButton(new ElementButton("设置传送点"));
        form.addButton(new ElementButton("创建传送点"));
        form.addButton(new ElementButton("删除传送点"));
        form.addButton(new ElementButton("返回"));
        guilistener.showFormWindow(player, form, guitype.WarpsSettingManageMenu);
    }
    public static void homedeletemenu(Player player){
        Config warpscfg = new Config(path+"/homes/"+player.getName()+".yml",Config.YAML);
        FormWindowCustom form = new FormWindowCustom("§e§l传送系统 - 家删除");
        List<String> arr = new ArrayList<>(warpscfg.getStringList("list"));
        form.addElement(new ElementDropdown("请选择要删除的内容",arr));
        guilistener.showFormWindow(player, form, guitype.HomeDeleteMenu);
    }
    public void warpssettingdownmenu(Player player, String name) {
        if(checktrust(player)){ return; }
        Config warpscfg = new Config(path+"/warps/"+ name +".yml",Config.YAML);
        if(!warpscfg.exists("x") || !warpscfg.exists("y") || !warpscfg.exists("z") || !warpscfg.exists("world") || !warpscfg.exists("state")){
            FormWindowSimple form = new FormWindowSimple("§e§l传送系统 - 公共传送点设置", "§c§l该传送点配置出错，请在后台修改！");
            form.addButton(new ElementButton("返回"));
            guilistener.showFormWindow(player, form, guitype.ErrorMenu);
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
        guilistener.showFormWindow(player, form, guitype.WarpSettingDownMenu);
    }
    public static void warpscreatemenu(Player player) {
        if(checktrust(player)){ return; }
        FormWindowCustom form = new FormWindowCustom("§e§l传送系统 - 公共传送点创建");
        form.addElement(new ElementInput("§e传送点标记","这是后台配置时的文件名"));
        form.addElement(new ElementInput("§e传送点名称","这里填名字"));
        form.addElement(new ElementInput("§ex轴坐标","0.000000"));
        form.addElement(new ElementInput("§ey轴坐标","60.000000"));
        form.addElement(new ElementInput("§ez轴坐标","130.000000"));
        form.addElement(new ElementInput("§e世界名称","world"));
        form.addElement(new ElementToggle("§e是否允许进入",false));
        guilistener.showFormWindow(player, form, guitype.WarpsSettingCreateMenu);
    }
    public static void warpsdeletemenu(Player player) {
        Config warpscfg = new Config(path+"/warps.yml",Config.YAML);
        if(checktrust(player)){ return; }
        FormWindowCustom form = new FormWindowCustom("§e§l传送系统 - 公共传送点删除");
        List<String> arr = new ArrayList<>(warpscfg.getStringList("list"));
        form.addElement(new ElementDropdown("请选择要删除的内容",arr));
        guilistener.showFormWindow(player, form, guitype.WarpsSettingDeleteMenu);
    }
    public static void worldmenu(Player player){
        Config config = new Config(path+"/config.yml",Config.YAML);
        List<String> whiteworld = config.getStringList("世界白名单");
        FormWindowSimple form = new FormWindowSimple("§e§l传送系统 - 多世界传送","所有允许传送的世界已经展示在此");
        Map<Integer, Level> levels = player.getServer().getLevels();
        Collection<Level> wlist = levels.values();
        for(Level level: wlist){
            String LevelName = level.getName();
            if(whiteworld.contains(LevelName)) {
                form.addButton(new ElementButton(LevelName));
            }
        }
        form.addButton(new ElementButton("返回"));
        guilistener.showFormWindow(player, form, guitype.WorldTeleportMenu);
    }
    public static void worldteleport(Player p, Level level){
        if(p.getServer().getLevels().containsValue(level)) {
            Config worldwhitelist = new Config(path+"/worldwhitelist.yml",Config.YAML);
            if(worldwhitelist.exists(level.getName())) {
                if(!worldwhitelist.getStringList(level.getName()).contains(p.getName())){
                    p.sendMessage("对不起，您不在该世界的白名单内，无法进行传送！");
                    return;
                }
            }
            Position spawnpos = level.getSpawnLocation();
            Location pos = new Location(spawnpos.x, spawnpos.y, spawnpos.z, level);
            p.teleportImmediate(pos);
            p.sendMessage("正在传送，请稍后...");
        }else{
            p.sendMessage("世界不存在！");
        }
    }

    public static void tp(Player asker, Player player){
        if(!player.isOnline()){ return; }
        if(!asker.isOnline()){ return; }
        Position position = player.getPosition();
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();
        Level level = player.getLevel();
        if(!asker.getServer().isLevelGenerated(level.getName())){
            player.sendMessage("对不起，该地图尚未加载");
            return;
        }
        asker.teleportImmediate(new Location(x,y,z,level));
        player.sendMessage("§e成功同意该玩家请求");
    }
}