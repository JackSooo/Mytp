package glorydark;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.Listener;
import cn.nukkit.form.element.*;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import glorydark.commands.MytpCommand;
import glorydark.commands.WildCommand;
import glorydark.commands.WorldListCommand;
import glorydark.commands.WorldTpCommand;
import glorydark.event.eventlistener;
import glorydark.gui.guilistener;
import glorydark.gui.guitype;
import me.onebone.economyapi.EconomyAPI;
import sun.applet.Main;

import java.io.File;
import java.util.*;

import static glorydark.Tools.buildButton;
import static glorydark.Tools.getLang;

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
    public static MainClass plugin;
    public static double cost = 0;
    public static Timer timer = new Timer();
    public static List<Player> godPlayer = new ArrayList<>();
    public static HashMap<Player, String> editTeleportPoint = new HashMap<>();

    @Override
    public void onLoad(){
        saveDefaultConfig();
        this.getLogger().info("DEssential Onloaded!");
        path = this.getDataFolder().getPath();
        plugin = this;
    }

    @Override
    public void onEnable() {
        Tools.upgradeConfig();
        this.getServer().getPluginManager().registerEvents(this, this); // 注册Event
        this.getServer().getPluginManager().registerEvents(new guilistener(), this); // 注册菜单监听Event
        this.getServer().getPluginManager().registerEvents(new eventlistener(), this); // 注册事件监听Event
        this.getServer().getCommandMap().register("",new MytpCommand());
        this.getServer().getCommandMap().register("",new WildCommand());
        this.getServer().getCommandMap().register("",new WorldListCommand());
        this.getServer().getCommandMap().register("",new WorldTpCommand());
        this.getLogger().info("DEssential Enabled!");
        this.saveResource("config.yml",false);
        this.saveResource("lang.yml",false);
        loadLevel();
        Config config = new Config(path+"/config.yml",Config.YAML);
        if(!config.exists("设置重生点花费")){
            config.set("设置重生点花费","1000.000000");
        }
        if(!config.exists("强制回主城")){
            config.set("强制回主城",false);
            config.save();
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
        if(!config.exists("最多同时存在家")){
            config.set("最多同时存在家", 10);
        }
        if(!config.exists("wild_maxX")){
            config.set("wild_maxX", 100);
        }
        if(!config.exists("wild_minX")){
            config.set("wild_minX", 10);
        }
        if(!config.exists("wild_maxZ")){
            config.set("wild_maxZ", 100);
        }
        if(!config.exists("wild_minZ")){
            config.set("wild_minZ", 10);
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

    public static Boolean addtrust(String pn){
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
    public static Boolean removetrust(String pn){
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

    public static void mainmenu(Player player) {
        if(!player.isOnline()){ return; }
        FormWindowSimple form = new FormWindowSimple(getLang("MainMenu","title"), getLang("MainMenu","content").replace("%player%",player.getName()).replace("%money%",String.valueOf(EconomyAPI.getInstance().myMoney(player))));
        form.addButton(buildButton(getLang("MainMenu","button1_text"),getLang("MainMenu","button1_pic_path")));
        form.addButton(buildButton(getLang("MainMenu","button2_text"),getLang("MainMenu","button2_pic_path")));
        form.addButton(buildButton(getLang("MainMenu","button3_text"),getLang("MainMenu","button3_pic_path")));
        form.addButton(buildButton(getLang("MainMenu","button4_text"),getLang("MainMenu","button4_pic_path")));
        form.addButton(buildButton(getLang("MainMenu","button5_text"),getLang("MainMenu","button5_pic_path")));
        form.addButton(buildButton(getLang("MainMenu","button6_text"),getLang("MainMenu","button6_pic_path")));
        form.addButton(buildButton(getLang("MainMenu","button7_text"),getLang("MainMenu","button7_pic_path")));
        if(MainClass.checktrust(player,false)){
            form.addButton(buildButton(getLang("MainMenu","button8_text"),getLang("MainMenu","button8_pic_path")));
            form.addButton(buildButton(getLang("MainMenu","button9_text"),getLang("MainMenu","button9_pic_path")));
            form.addButton(buildButton(getLang("MainMenu","button10_text"),getLang("MainMenu","button10_pic_path")));
            form.addButton(buildButton(getLang("MainMenu","button11_text"),getLang("MainMenu","button11_pic_path")));
        }
        guilistener.showFormWindow(player, form, guitype.MainMenu);
    }

    public static void warpsmenu(Player player) { //传送点系统
        if(!player.isOnline()){ return; }
        FormWindowSimple form = new FormWindowSimple(getLang("Warp_TeleportMenu","title"), getLang("Warp_TeleportMenu","content"));
        Config warpconfig = new Config(path+"/warps.yml",Config.YAML);
        if(warpconfig.get("list") != null) {
            List<String> warplist = new ArrayList<>(warpconfig.getStringList("list"));
            for (String wpn : warplist) {
                if(getLang("Warp_TeleportMenu",wpn+"_name").equals("Key Not Found!")) {
                    form.addButton(buildButton(wpn, getLang("Warp_TeleportMenu", wpn + "_picpath")));
                }else{
                    form.addButton(buildButton(getLang("Warp_TeleportMenu",wpn+"_name"), getLang("Warp_TeleportMenu", wpn + "_picpath")));
                }
            }
        }
        form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
        guilistener.showFormWindow(player, form, guitype.WarpMenu);
    }

    public static void tpMenu(Player player){
        if(!player.isOnline()){ return; }
        FormWindowSimple form = new FormWindowSimple(getLang("Teleport_Main","title"), getLang("Teleport_Main","content"));
        form.addButton(buildButton(getLang("Teleport_Main","button1_text"),getLang("Teleport_Main","button1_pic_path")));
        form.addButton(buildButton(getLang("Teleport_Main","button2_text"),getLang("Teleport_Main","button2_pic_path")));
        guilistener.showFormWindow(player, form, guitype.TeleportMainMenu);
    }

    public static void teleportmenu(Player player, int type) {
        switch (type){
            case 0:
                if (!player.isOnline()) {
                    return;
                }
                FormWindowSimple form = new FormWindowSimple(getLang("Teleport_ToPlayer","title"), getLang("Teleport_ToPlayer","content"));
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
                FormWindowSimple form1 = new FormWindowSimple(getLang("Teleport_PlayerToYou","title"), getLang("Teleport_PlayerToYou","content"));
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
                if(EconomyAPI.getInstance().myMoney(asker) < cost){asker.sendMessage(getLang("Tips","short_of_money"));return;}
                EconomyAPI.getInstance().reduceMoney(asker, cost);
                if(!player.isOnline()){ return; }
                FormWindowSimple form = new FormWindowSimple(getLang("Teleport_ToPlayerAccept","title"), getLang("Teleport_ToPlayerAccept","content"));
                form.addButton(new ElementButton(asker.getName()));
                guilistener.showFormWindow(player, form, guitype.AcceptListInitiativeMENU);
                break;
            case 1:
                Config config1 = new Config(path+"/config.yml",Config.YAML);
                cost = config1.getDouble("传送邀请花费");
                if(EconomyAPI.getInstance().myMoney(asker) < cost){asker.sendMessage(getLang("Tips","short_of_money"));return;}
                EconomyAPI.getInstance().reduceMoney(asker, cost);
                if(!player.isOnline()){ return; }
                FormWindowSimple form1 = new FormWindowSimple(getLang("Teleport_ToPlayerAccept","title"), getLang("Teleport_ToPlayerAccept","content"));
                form1.addButton(new ElementButton(asker.getName()));
                guilistener.showFormWindow(player, form1, guitype.AcceptListPassiveMENU);
                break;
            default:
                break;
        }
    }

    public static void settingmenu(Player player) {
        Config pconfig = new Config(path+"/player/"+player.getName()+".yml",Config.YAML);
        FormWindowCustom form = new FormWindowCustom(getLang("PersonalSetting","title"));
        form.addElement(new ElementToggle(getLang("PersonalSetting","toggle1_text"),pconfig.getBoolean("自动接受传送请求")));
        Config config = new Config(path+"/config.yml",Config.YAML);
        if(config.getBoolean("开启设置重生点",false)) {
            form.addElement(new ElementToggle(getLang("PersonalSetting", "toggle2_text"), false));
        }
        guilistener.showFormWindow(player, form, guitype.SettingMenu);
    }
    public static void mainsettingmenu(Player player) {
        if(!checktrust(player,false)){ return; }
        Config pconfig = new Config(path+"/config.yml",Config.YAML);
        FormWindowCustom form = new FormWindowCustom(getLang("SystemSettingMenu","title"));
        form.addElement(new ElementInput(getLang("SystemSettingMenu","input1_text"),pconfig.getString("传送邀请花费")));
        form.addElement(new ElementInput(getLang("SystemSettingMenu","input2_text"),pconfig.getString("设置家花费")));
        form.addElement(new ElementInput(getLang("SystemSettingMenu","input3_text"),pconfig.getString("设置重生点花费")));
        form.addElement(new ElementInput(getLang("SystemSettingMenu","input4_text"),pconfig.getString("最多同时存在家")));
        form.addElement(new ElementToggle(getLang("SystemSettingMenu","toggle5_text"),pconfig.getBoolean("强制回主城")));
        form.addElement(new ElementToggle(getLang("SystemSettingMenu","toggle6_text"),pconfig.getBoolean("开启设置重生点")));
        form.addElement(new ElementInput(getLang("SystemSettingMenu","input7_text"),pconfig.getString("返回死亡点花费")));
        form.addElement(new ElementInput(getLang("SystemSettingMenu","input8_text"),pconfig.getString("随机传送花费")));
        guilistener.showFormWindow(player, form, guitype.MainSETTINGMENU);
    }
    public static boolean checktrust(Player p,Boolean openGui){
        Config trustlist = new Config(path+"/trust.yml",Config.YAML);
        List<String> arrayList = new ArrayList<>(trustlist.getStringList("list"));
        if(arrayList.contains(p.getName())) {
            return true;
        }else{
            if(openGui){
                FormWindowSimple form = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","operation_is_not_authorized"));
                form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
                guilistener.showFormWindow(p, form, guitype.ErrorMenu);
            }
            return false;
        }
    }
    public static void warpssettingmenu(Player player) {
        if(!checktrust(player,true)){ return; }
        Config warpconfig = new Config(path+"/warps.yml",Config.YAML);
        FormWindowSimple form = new FormWindowSimple(getLang("Warp_SettingSelectMenu","title"),getLang("Warp_SettingSelectMenu","content"));
        if(warpconfig.get("list") != null) {
            List<String> warplist = new ArrayList<>(warpconfig.getStringList("list"));
            for (String wpn : warplist) {
                form.addButton(new ElementButton(wpn));
            }
        }
        guilistener.showFormWindow(player, form, guitype.WarpsSettingMenu);
    }

    public static void homemainmenu(Player player){
        FormWindowSimple form = new FormWindowSimple(getLang("Home_Main","title"),getLang("Home_Main","content"));
        form.addButton(buildButton(getLang("Home_Main","button1_text"),getLang("Home_Main","button1_pic_path")));
        form.addButton(buildButton(getLang("Home_Main","button2_text"),getLang("Home_Main","button2_pic_path")));
        form.addButton(buildButton(getLang("Home_Main","button3_text"),getLang("Home_Main","button3_pic_path")));
        form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
        guilistener.showFormWindow(player, form, guitype.HomeMainMenu);
    }
    public static void HomeTeleportMenu(Player player){
        FormWindowSimple form = new FormWindowSimple(getLang("Home_TeleportMenu","title"),getLang("Home_TeleportMenu","content"));
        Config hc = new Config(path+"/homes/"+player.getName()+".yml",Config.YAML);
        List<String> arr = new ArrayList<>(hc.getStringList("list"));
        for(String n : arr){
            Config pointc = new Config(path+"/homes/"+player.getName()+"/"+n+".yml",Config.YAML);
            String intro = pointc.getString("简介");
            form.addButton(new ElementButton("名称:"+n+"\n简介:"+intro));
        }
        guilistener.showFormWindow(player, form, guitype.HomeTeleportMenu);
    }
    public static void HomeCreateMenu(Player player){
        if(!((List<String>)Tools.getDefaultConfig("设置家世界")).contains(player.level.getName())){
            FormWindowSimple returnForm = new FormWindowSimple(getLang("Tips", "menu_default_title"), getLang("Tips", "world_not_allowed"));
            returnForm.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
            guilistener.showFormWindow(player, returnForm, guitype.ErrorMenu);
            return;
        }
        FormWindowCustom form = new FormWindowCustom(getLang("Home_CreateMenu","title"));
        form.addElement(new ElementInput(getLang("Home_CreateMenu","input1_text"),getLang("Home_CreateMenu","input1_tip")));
        form.addElement(new ElementInput(getLang("Home_CreateMenu","input2_text"),getLang("Home_CreateMenu","input2_tip")));
        guilistener.showFormWindow(player, form, guitype.HomeCreateMenu);
    }
    public static void warpsmanagedownmenu(Player player){
        FormWindowSimple form = new FormWindowSimple(getLang("Warp_MainMenu","title"),getLang("Warp_MainMenu","content"));
        form.addButton(buildButton(getLang("Warp_MainMenu","button1_text"),getLang("Warp_MainMenu","button1_pic_path")));
        form.addButton(buildButton(getLang("Warp_MainMenu","button2_text"),getLang("Warp_MainMenu","button2_pic_path")));
        form.addButton(buildButton(getLang("Warp_MainMenu","button3_text"),getLang("Warp_MainMenu","button3_pic_path")));
        form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
        guilistener.showFormWindow(player, form, guitype.WarpsSettingManageMenu);
    }
    public static void homedeletemenu(Player player){
        Config warpscfg = new Config(path+"/homes/"+player.getName()+".yml",Config.YAML);
        List<String> arr = new ArrayList<>(warpscfg.getStringList("list"));
        if(arr.size() >= 1) {
            FormWindowCustom form = new FormWindowCustom(getLang("Home_DeleteMenu","title"));
            form.addElement(new ElementDropdown(getLang("Home_DeleteMenu", "dropdown_title"), arr));
            guilistener.showFormWindow(player, form, guitype.HomeDeleteMenu);
        }else{
            FormWindowSimple returnForm = new FormWindowSimple(getLang("Tips", "menu_default_title"), getLang("Tips", "have_no_home"));
            returnForm.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
            guilistener.showFormWindow(player, returnForm, guitype.ErrorMenu);
        }
    }
    public static void warpssettingdownmenu(Player player, String name) {
        if(!checktrust(player,true)){ return; }
        MainClass.editTeleportPoint.put(player,name);
        Config warpscfg = new Config(path+"/warps/"+ name +".yml",Config.YAML);
        if(!warpscfg.exists("x") || !warpscfg.exists("y") || !warpscfg.exists("z") || !warpscfg.exists("world") || !warpscfg.exists("state")){
            FormWindowSimple form = new FormWindowSimple(getLang("Tips","menu_default_title"), getLang("Tips","settings_error"));
            form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
            guilistener.showFormWindow(player, form, guitype.ErrorMenu);
            return;
        }
        FormWindowCustom form = new FormWindowCustom(getLang("Warp_SettingMenu","title"));
        form.addElement(new ElementLabel(name));
        form.addElement(new ElementInput(getLang("Warp_SettingMenu","input1_title"),warpscfg.getString("name")));
        form.addElement(new ElementInput(getLang("Warp_SettingMenu","input2_title"),String.valueOf(warpscfg.getDouble("x"))));
        form.addElement(new ElementInput(getLang("Warp_SettingMenu","input3_title"),String.valueOf(warpscfg.getDouble("y"))));
        form.addElement(new ElementInput(getLang("Warp_SettingMenu","input4_title"),String.valueOf(warpscfg.getDouble("z"))));
        form.addElement(new ElementInput(getLang("Warp_SettingMenu","input5_title"),warpscfg.getString("world")));
        form.addElement(new ElementToggle(getLang("Warp_SettingMenu","toggle6_title"),warpscfg.getBoolean("state")));
        String[] strings = warpscfg.getString("title").split(":");
        form.addElement(new ElementInput(getLang("Warp_SettingMenu","input7_title"),strings[0]));
        form.addElement(new ElementInput(getLang("Warp_SettingMenu","input8_title"),strings[1]));
        guilistener.showFormWindow(player, form, guitype.WarpSettingDownMenu);
    }
    public static void warpscreatemenu(Player player) {
        if(!checktrust(player,true)){ return; }
        FormWindowCustom form = new FormWindowCustom(getLang("Warp_CreateMenu","title"));
        form.addElement(new ElementInput(getLang("Warp_CreateMenu","input1_text"),getLang("Warp_CreateMenu","input1_tip")));
        form.addElement(new ElementInput(getLang("Warp_CreateMenu","input2_text"),getLang("Warp_CreateMenu","input2_tip")));
        form.addElement(new ElementInput(getLang("Warp_CreateMenu","input3_text"), String.valueOf(player.getX())));
        form.addElement(new ElementInput(getLang("Warp_CreateMenu","input4_text"),String.valueOf(player.getY())));
        form.addElement(new ElementInput(getLang("Warp_CreateMenu","input5_text"),String.valueOf(player.getZ())));
        form.addElement(new ElementInput(getLang("Warp_CreateMenu","input6_text"),player.getLevel().getName()));
        form.addElement(new ElementToggle(getLang("Warp_CreateMenu","toggle7_text"),false));
        form.addElement(new ElementInput(getLang("Warp_CreateMenu","input8_text"),getLang("Warp_CreateMenu","input8_tip")));
        form.addElement(new ElementInput(getLang("Warp_CreateMenu","input9_text"),getLang("Warp_CreateMenu","input9_tip")));
        guilistener.showFormWindow(player, form, guitype.WarpsSettingCreateMenu);
    }
    public static void warpsdeletemenu(Player player) {
        Config warpscfg = new Config(path+"/warps.yml",Config.YAML);
        if(!checktrust(player,true)){ return; }
        FormWindowCustom form = new FormWindowCustom(getLang("Warp_DeleteMenu","title"));
        List<String> arr = new ArrayList<>(warpscfg.getStringList("list"));
        form.addElement(new ElementDropdown(getLang("Warp_DeleteMenu","dropdown_title"),arr));
        guilistener.showFormWindow(player, form, guitype.WarpsSettingDeleteMenu);
    }
    public static void worldmenu(Player player){
        Config config = new Config(path+"/config.yml",Config.YAML);
        List<String> whiteworld = new ArrayList<>(config.getStringList("世界白名单"));
        FormWindowSimple form = new FormWindowSimple(getLang("World_TeleportMenu","title"),getLang("World_TeleportMenu","content"));
        for (String level : whiteworld) {
            if(Server.getInstance().getLevelByName(level) != null) {
                if (getLang("World_TeleportMenu", level + "_name").equals("Key Not Found!")) {
                    form.addButton(buildButton(level, getLang("World_TeleportMenu", level + "_picpath")));
                } else {
                    form.addButton(buildButton(getLang("World_TeleportMenu", level + "_name"), getLang("World_TeleportMenu", level + "_picpath")));
                }
            }
        }
        form.addButton(buildButton(getLang("Tips","menu_button_return_text"),getLang("Tips","menu_button_return_pic_path")));
        guilistener.showFormWindow(player, form, guitype.WorldTeleportMenu);
    }
    public static void worldteleport(Player p, Level level){
        if(level == null){
            p.sendMessage(getLang("Tips","world_is_not_loaded"));
            return;
        }
        if(p.getServer().getLevels().containsValue(level)) {
            Position spawnpos = level.getSafeSpawn();
            p.teleportImmediate(spawnpos.getLocation());
            p.sendMessage(getLang("Tips","on_teleporting"));
        }else{
            p.sendMessage(getLang("Tips","world_is_not_loaded"));
        }
    }

    public static void tp(Player asker, Player player){ //将第一个参数传送到第二个参数
        if(!player.isOnline()){ return; }
        if(!asker.isOnline()){ return; }
        Level level = player.getLevel();
        if(!asker.getServer().isLevelGenerated(level.getName())){
            player.sendMessage(getLang("Tips","world_is_not_loaded"));
            return;
        }
        asker.teleportImmediate(player.getPosition().getLocation());
        asker.sendMessage(getLang("Tips","teleport_to_player").replace("%player%",player.getName()));
    }

    public static void managerWorldTeleportMenu(Player player){
        if(!checktrust(player,true)){ return; }
        FormWindowSimple form = new FormWindowSimple(getLang("World_TeleportMenu","title"),getLang("World_TeleportMenu","content"));
        for(Level level: Server.getInstance().getLevels().values()){
            form.addButton(new ElementButton(level.getName()));
        }
        guilistener.showFormWindow(player,form, guitype.ManagerWorldMenu);
    }
}