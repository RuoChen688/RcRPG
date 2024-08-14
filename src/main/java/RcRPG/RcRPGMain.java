package RcRPG;

import RcRPG.RPG.*;
import RcRPG.RPG.Forging.ForgingPaper;
import RcRPG.RPG.Forging.ForgingStone;
import RcRPG.Task.BoxTimeTask;
import RcRPG.Task.PlayerAttrUpdateTask;
import RcRPG.Task.Tip;
import RcRPG.command.Commands;
import RcRPG.config.MainConfig;
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
import java.util.LinkedHashMap;

public class RcRPGMain extends PluginBase implements Listener {

    @Getter
    public static RcRPGMain instance;

    @Getter
    public static PluginI18n i18n;

    public static LangCode serverLangCode;

    public Config ornamentConfig;

    /**
     * 装备分解配置
     */
    public Config dismantleConfig;

    public static LinkedHashMap<String, Weapon> loadWeapon = new LinkedHashMap<>();
    public static LinkedHashMap<String, Armour> loadArmour = new LinkedHashMap<>();
    public static LinkedHashMap<String, Stone> loadStone = new LinkedHashMap<>();

    public static LinkedHashMap<String, Box> loadBox = new LinkedHashMap<>();

    public static LinkedHashMap<String, Ornament> loadOrnament = new LinkedHashMap<>();

    public static LinkedHashMap<String, ForgingPaper> loadForgingPaper = new LinkedHashMap<>();
    public static LinkedHashMap<String, ForgingStone> loadForgingStone = new LinkedHashMap<>();

    public RcRPGMain() {
    }

    public void onLoad() {
        //save Plugin Instance
        instance = this;
        //register the plugin i18n
        i18n = PluginI18nManager.register(this);
        initServerLangCode();
    }

    public void onEnable() {
        if (Server.getInstance().getPluginManager().getPlugin("FakeInventories") == null) {
            this.getLogger().error(i18n.tr(serverLangCode, "rcrpg.missing.plugin", "FakeInventories", "https://github.com/JkqzDev/FakeInventories-MOT/releases"));
            throw new RuntimeException("Missing required plugin FakeInventories!");
        }

        Entity.registerEntity("TextEntity", TextEntity.class);

        this.createConfigDir();
        init();

        if (MainConfig.isPrefixSystemDisabled()) {
            Level.enable = false;
        }

        this.getServer().getPluginManager().registerEvents(new Events(), this);

        // 底部显示不为空时
        if (!MainConfig.getBottomFormat().isEmpty()) {
            this.getServer().getScheduler().scheduleRepeatingTask(new Tip(this), 20);
        }
        this.getServer().getScheduler().scheduleRepeatingTask(new BoxTimeTask(this), 20);
        //this.getServer().getScheduler().scheduleRepeatingTask(new loadHealth(this), 10);
        this.getServer().getScheduler().scheduleRepeatingTask(new PlayerAttrUpdateTask(this), 20);

        this.getServer().getPluginManager().addPermission(new Permission("plugin.rcrpg", "rcrpg 命令权限", "true"));
        this.getServer().getPluginManager().addPermission(new Permission("plugin.rcrpg.admin", "rcrpg 管理员命令权限", "op"));
        this.getServer().getCommandMap().register("rpg", new Commands("rpg"));

        if (Server.getInstance().getPluginManager().getPlugin("EconomyAPI") == null) {
            this.getLogger().warning("未检测到 EconomyAPI 插件，将使用默认的经济核心");
        }
        if (Server.getInstance().getPluginManager().getPlugin("playerPoints") == null) {
            this.getLogger().warning("未检测到 PlayerPoints 插件，将使用默认的点券核心");
        }
        if (Server.getInstance().getPluginManager().getPlugin("Tips") != null) {
            Api.registerVariables("RcRPGTipsApi", TipsVariables.class);
        }
        this.getLogger().info("插件加载成功，作者：若尘、Mcayear");
    }

    public void init() {
        MainConfig.init();

        this.saveResource("OrnamentConfig.yml", "/OrnamentConfig.yml", false);
        ornamentConfig = new Config(this.getDataFolder() + File.separator + "OrnamentConfig.yml");

        this.saveResource("DismantlePlan.yml", "/DismantlePlan.yml", false);
        dismantleConfig = new Config(this.getDataFolder() + File.separator + "DismantlePlan.yml");

        this.saveResource("SuitPlan.yml", "/SuitPlan.yml", false);
        Suit.init();

        this.getLogger().info("开始读取武器信息");
        for (String name : Handle.getDefaultFiles("Weapon")) {
            Weapon weapon = null;
            try {
                weapon = Weapon.loadWeapon(name, new Config(this.getDataFolder() + File.separator + "Weapon/" + name + ".yml", Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (weapon != null) {
                loadWeapon.put(name, weapon);
                this.getLogger().info(name + ".yml 武器数据读取成功");
            } else {
                this.getLogger().warning(name + ".yml 武器数据读取失败");
            }
        }
        this.getLogger().info("开始读取盔甲信息");
        for (String name : Handle.getDefaultFiles("Armour")) {
            Armour armour = null;
            try {
                armour = Armour.loadArmour(name, new Config(this.getDataFolder() + File.separator + "Armour/" + name + ".yml", Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (armour != null) {
                loadArmour.put(name, armour);
                this.getLogger().info(name + ".yml 盔甲数据读取成功");
            } else {
                this.getLogger().warning(name + ".yml 盔甲数据读取失败");
            }
        }
        this.getLogger().info("开始读取宝石信息");
        for (String name : Handle.getDefaultFiles("Stone")) {
            Stone stone = null;
            try {
                stone = Stone.loadStone(name, new Config(this.getDataFolder() + File.separator + "Stone/" + name + ".yml", Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (stone != null) {
                loadStone.put(name, stone);
                this.getLogger().info(name + ".yml 宝石数据读取成功");
            } else {
                this.getLogger().warning(name + ".yml 宝石数据读取失败");
            }
        }
        this.getLogger().info("开始读取箱子信息");
        for (String name : Handle.getDefaultFiles("Box")) {
            Box box = null;
            try {
                box = Box.loadBox(name, new Config(this.getDataFolder() + File.separator + "Box/" + name + ".yml", Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (box != null) {
                loadBox.put(name, box);
                this.getLogger().info(name + ".yml 箱子数据读取成功");
            } else {
                this.getLogger().warning(name + ".yml 箱子数据读取失败");
            }
        }
        this.getLogger().info("开始读取饰品信息");
        for (String name : Handle.getDefaultFiles("Ornament")) {
            Ornament ornament = null;
            try {
                ornament = Ornament.loadOrnament(name, new Config(this.getDataFolder() + File.separator + "Ornament/" + name + ".yml", Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (ornament != null) {
                loadOrnament.put(name, ornament);
                this.getLogger().info(name + ".yml 饰品数据读取成功");
            } else {
                this.getLogger().warning(name + ".yml 饰品数据读取失败");
            }
        }
        this.getLogger().info("开始读取锻造图信息");
        for (String name : Handle.getDefaultFiles("Forging" + File.separator + "Paper")) {
            ForgingPaper forgingPaper;
            try {
                forgingPaper = ForgingPaper.loadForgingPaper(name, new Config(this.getDataFolder() + File.separator + "Forging" + File.separator + "Paper" + File.separator + name + ".yml", Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (forgingPaper != null) {
                loadForgingPaper.put(name, forgingPaper);
                this.getLogger().info(name + ".yml 锻造图数据读取成功");
            } else {
                this.getLogger().warning(name + ".yml 锻造图数据读取成功");
            }
        }
        this.getLogger().info("开始读取锻造石信息");
        for (String name : Handle.getDefaultFiles("Forging" + File.separator + "Stone")) {
            ForgingStone forgingStone;
            try {
                forgingStone = ForgingStone.loadForgingStone(name, new Config(this.getDataFolder() + File.separator + "Forging" + File.separator + "Stone" + File.separator + name + ".yml", Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (forgingStone != null) {
                loadForgingStone.put(name, forgingStone);
                this.getLogger().info(name + ".yml 锻造石数据读取成功");
            } else {
                this.getLogger().warning(name + ".yml 锻造石数据读取成功");
            }
        }
    }

    public File getPlayerFile() {
        return new File(this.getDataFolder() + File.separator + "Players");
    }

    public File getWeaponFile() {
        return new File(this.getDataFolder() + File.separator + "Weapon");
    }

    public File getArmourFile() {
        return new File(this.getDataFolder() + File.separator + "Armour");
    }

    public File getStoneFile() {
        return new File(this.getDataFolder() + File.separator + "Stone");
    }

    public File getGuildFile() {
        return new File(this.getDataFolder() + File.separator + "Guild");
    }

    public File getBoxFile() {
        return new File(this.getDataFolder() + File.separator + "Box");
    }

    public File getOrnamentFile() {
        return new File(this.getDataFolder() + File.separator + "Ornament");
    }

    public File getForgingPaperFile() {
        return new File(this.getDataFolder() + File.separator + "Forging" + File.separator + "Paper");
    }

    public File getForgingStoneFile() {
        return new File(this.getDataFolder() + File.separator + "Forging" + File.separator + "Stone");
    }

    public void createConfigDir() {
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
        File forgingPaperFile = this.getForgingPaperFile();
        if (!forgingPaperFile.exists() && !forgingPaperFile.mkdirs()) {
            this.getLogger().info("/Forging/Paper 文件夹创建失败");
        }
        File forgingStoneFile = this.getForgingStoneFile();
        if (!forgingStoneFile.exists() && !forgingStoneFile.mkdirs()) {
            this.getLogger().info("/Forging/Stone 文件夹创建失败");
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

}