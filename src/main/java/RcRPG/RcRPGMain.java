package RcRPG;

import RcRPG.RPG.*;
import RcRPG.Task.BoxTimeTask;
import RcRPG.Task.PlayerAttrUpdateTask;
import RcRPG.Task.Tip;
import RcRPG.command.Commands;
import RcRPG.floatingtext.TextEntity;
import RcRPG.tips.TipsVariables;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Listener;
import cn.nukkit.lang.LangCode;
import cn.nukkit.lang.PluginI18n;
import cn.nukkit.lang.PluginI18nManager;
import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import lombok.Getter;
import tip.utils.Api;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class RcRPGMain extends PluginBase implements Listener {

    @Getter
    public static RcRPGMain instance;

    @Getter
    public static PluginI18n i18n;

    public static LangCode serverLangCode;

    public boolean disableChatStyle;
    public static boolean disablePrefix = false;

    /**
     * 插件主配置
     */
    public Config config;

    public Config ornamentConfig;

    /**
     * 装备分解配置
     */
    public Config dismantleConfig;

    /**
     * 以百分比显示的属性列表
     */
    public List<String> attrDisplayPercentList;
    public static boolean money;

    public static boolean point;


    public static LinkedHashMap<String, Weapon> loadWeapon = new LinkedHashMap<>();
    public static LinkedHashMap<String, Armour> loadArmour = new LinkedHashMap<>();
    public static LinkedHashMap<String, Stone> loadStone = new LinkedHashMap<>();

    public static LinkedHashMap<String, Box> loadBox = new LinkedHashMap<>();

    public static LinkedHashMap<String, Ornament> loadOrnament = new LinkedHashMap<>();

    public RcRPGMain(){}

    public void onLoad(){
        //save Plugin Instance
        instance = this;
        //register the plugin i18n
        i18n = PluginI18nManager.register(this);
        initServerLangCode();
    }

    public void onEnable(){
        Entity.registerEntity("TextEntity", TextEntity.class);

        this.getNewFile();
        this.saveResource("config.yml","/config.yml",false);
        config = new Config(this.getDataFolder() + "/config.yml");

        init();

        if (config.exists("称号.disable") && config.getBoolean("称号.disable")) {
            disablePrefix = true;
        }
        if (config.exists("RcRPG经验.disable") && config.getBoolean("RcRPG经验.disable")) {
            Level.enable = false;
        }

        this.getServer().getPluginManager().registerEvents(new Events(),this);
        if (config.exists("底部显示") && !config.getString("底部显示").isEmpty()) {
            this.getServer().getScheduler().scheduleRepeatingTask(new Tip(this), 20);
        }
        this.getServer().getScheduler().scheduleRepeatingTask(new BoxTimeTask(this),20);
        //this.getServer().getScheduler().scheduleRepeatingTask(new loadHealth(this), 10);
        this.getServer().getScheduler().scheduleRepeatingTask(new PlayerAttrUpdateTask(this),20);

        this.getServer().getPluginManager().addPermission(new Permission("plugin.rcrpg", "rcrpg 命令权限", "true"));
        this.getServer().getPluginManager().addPermission(new Permission("plugin.rcrpg.admin", "rcrpg 管理员命令权限", "op"));
        this.getServer().getCommandMap().register("rpg", new Commands("rpg"));

        if(Server.getInstance().getPluginManager().getPlugin("EconomyAPI") == null){
            this.getLogger().info("检测到未安装核心，将使用默认的经济核心");
            money = false;
        }else{
            money = true;
        }
        if(Server.getInstance().getPluginManager().getPlugin("playerPoints") == null){
            this.getLogger().info("检测到未安装点券插件，将使用默认的点券核心");
            point = false;
        }else{
            point = true;
        }
        if(Server.getInstance().getPluginManager().getPlugin("Tips") != null){
            Api.registerVariables("AyearTipsApi", TipsVariables.class);
        }
        this.getLogger().info("插件加载成功，作者：若尘");
    }

    public void init() {
        this.saveResource("OrnamentConfig.yml","/OrnamentConfig.yml",false);
        ornamentConfig = new Config(this.getDataFolder() + "/OrnamentConfig.yml");

        this.saveResource("DismantlePlan.yml","/DismantlePlan.yml",false);
        dismantleConfig = new Config(this.getDataFolder() + "/DismantlePlan.yml");

        this.saveResource("SuitPlan.yml","/SuitPlan.yml",false);
        Suit.init();

        initAttrDisplayPercent();
        disableChatStyle = !config.exists("底部显示") || config.getString("底部显示").isEmpty();

        this.getLogger().info("开始读取武器信息");
        for(String name : Handle.getDefaultFiles("Weapon")){
            Weapon weapon = null;
            try {
                weapon = Weapon.loadWeapon(name,new Config(this.getDataFolder()+"/Weapon/"+name+".yml",Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(weapon != null) {
                loadWeapon.put(name,weapon);
                this.getLogger().info(name+".yml 武器数据读取成功");
            } else {
                this.getLogger().warning(name+".yml 武器数据读取失败");
            }
        }
        this.getLogger().info("开始读取盔甲信息");
        for(String name: Handle.getDefaultFiles("Armour")){
            Armour armour = null;
            try {
                armour = Armour.loadArmour(name,new Config(this.getDataFolder()+"/Armour/"+name+".yml",Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(armour != null){
                loadArmour.put(name,armour);
                this.getLogger().info(name+".yml 盔甲数据读取成功");
            }else{
                this.getLogger().warning(name+".yml 盔甲数据读取失败");
            }
        }
        this.getLogger().info("开始读取宝石信息");
        for(String name: Handle.getDefaultFiles("Stone")){
            Stone stone = null;
            try {
                stone = Stone.loadStone(name,new Config(this.getDataFolder()+"/Stone/"+name+".yml",Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(stone != null){
                loadStone.put(name,stone);
                this.getLogger().info(name+".yml 宝石数据读取成功");
            }else{
                this.getLogger().warning(name+".yml 宝石数据读取失败");
            }
        }
        this.getLogger().info("开始读取箱子信息");
        for(String name: Handle.getDefaultFiles("Box")){
            Box box = null;
            try {
                box = Box.loadBox(name,new Config(this.getDataFolder()+"/Box/"+name+".yml",Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(box != null){
                loadBox.put(name,box);
                this.getLogger().info(name+".yml 箱子数据读取成功");
            }else{
                this.getLogger().warning(name+".yml 箱子数据读取失败");
            }
        }
        this.getLogger().info("开始读取饰品信息");
        for(String name: Handle.getDefaultFiles("Ornament")){
            Ornament ornament = null;
            try {
                ornament = Ornament.loadOrnament(name,new Config(this.getDataFolder()+"/Ornament/"+name+".yml",Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(ornament != null){
                loadOrnament.put(name,ornament);
                this.getLogger().info(name+".yml 饰品数据读取成功");
            }else{
                this.getLogger().warning(name+".yml 饰品数据读取失败");
            }
        }
    }

    public File getPlayerFile() {
        return new File(this.getDataFolder() + "/Players");
    }
    public File getWeaponFile() {
        return new File(this.getDataFolder() + "/Weapon");
    }
    public File getArmourFile() {
        return new File(this.getDataFolder() + "/Armour");
    }
    public File getStoneFile() {
        return new File(this.getDataFolder() + "/Stone");
    }
    public File getGuildFile() {
        return new File(this.getDataFolder() + "/Guild");
    }
    public File getBoxFile() {
        return new File(this.getDataFolder() + "/Box");
    }
    public File getOrnamentFile() {
        return new File(this.getDataFolder() + "/Ornament");
    }

    public void getNewFile(){
        File playerFile = this.getPlayerFile();
        if (!playerFile.exists() && !playerFile.mkdirs()) {
            this.getLogger().info("/Players 文件夹创建失败");
        }
        File weaponFile = this.getWeaponFile();
        if (!weaponFile.exists() && !weaponFile.mkdirs()) {
            this.getLogger().info("/Weapon 文件夹创建失败");
        }
        File armourFile = this.getArmourFile();
        if (!armourFile.exists() && !armourFile.mkdirs()) {
            this.getLogger().info("/Armour 文件夹创建失败");
        }
        File stoneFile = this.getStoneFile();
        if (!stoneFile.exists() && !stoneFile.mkdirs()) {
            this.getLogger().info("/Stone 文件夹创建失败");
        }
        File guildFile = this.getGuildFile();
        if (!guildFile.exists() && !guildFile.mkdirs()) {
            this.getLogger().info("/Guild 文件夹创建失败");
        }
        File boxFile = this.getBoxFile();
        if (!boxFile.exists() && !boxFile.mkdirs()) {
            this.getLogger().info("/Box 文件夹创建失败");
        }
        File ornamentFile = this.getOrnamentFile();
        if (!ornamentFile.exists() && !ornamentFile.mkdirs()) {
            this.getLogger().info("/Ornament 文件夹创建失败");
        }
    }

    public void initServerLangCode() {
        switch (Server.getInstance().getLanguage().getLang()) {
            case "eng" -> {
                serverLangCode = LangCode.en_US;
            }
            case "chs" -> {
                serverLangCode = LangCode.zh_CN;
            }
            case "deu" -> {
                serverLangCode = LangCode.de_DE;
            }
            case "rus" -> {
                serverLangCode = LangCode.ru_RU;
            }
            default -> {
                try {
                    serverLangCode = LangCode.valueOf(Server.getInstance().getLanguage().getLang());
                } catch (IllegalArgumentException e) {
                    serverLangCode = LangCode.en_US;
                }
            }
        }
    }

    public void initAttrDisplayPercent () {
        List<String> attrDisplayPercent = new ArrayList<>();

        // 添加激进向 (12)的百分比属性
        attrDisplayPercent.add("暴击率");
        attrDisplayPercent.add("暴击倍率");
        attrDisplayPercent.add("吸血率");
        attrDisplayPercent.add("吸血倍率");
        attrDisplayPercent.add("破防率");
        attrDisplayPercent.add("破甲率");
        attrDisplayPercent.add("破甲强度");
        attrDisplayPercent.add("命中率");
        attrDisplayPercent.add("伤害加成");
        attrDisplayPercent.add("PVP攻击加成");
        attrDisplayPercent.add("PVE攻击加成");

        // 添加保守向 (10)的百分比属性
        attrDisplayPercent.add("反伤率");
        attrDisplayPercent.add("闪避率");
        attrDisplayPercent.add("暴击闪避");
        attrDisplayPercent.add("暴击抵抗");
        attrDisplayPercent.add("吸血抵抗");
        attrDisplayPercent.add("血量加成");
        attrDisplayPercent.add("防御加成");
        attrDisplayPercent.add("护甲强度");
        attrDisplayPercent.add("生命加成");

        // 添加辅助增益向 (4)的百分比属性
        attrDisplayPercent.add("经验加成");
        attrDisplayPercent.add("移速加成");

        // 添加特殊效果 (6)的百分比属性
        attrDisplayPercent.add("燃烧概率");
        attrDisplayPercent.add("雷击概率");
        attrDisplayPercent.add("冰冻概率");
        if (RcRPGMain.getInstance().config.exists("AttrDisplayPercent")) {
            attrDisplayPercentList = RcRPGMain.getInstance().config.getStringList("AttrDisplayPercent");
        } else {
            attrDisplayPercentList = attrDisplayPercent;
        }
    }

}