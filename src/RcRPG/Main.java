package RcRPG;

import RcRPG.RPG.Armour;
import RcRPG.RPG.Magic;
import RcRPG.RPG.Stone;
import RcRPG.RPG.Weapon;
import RcRPG.Society.Shop;
import RcRPG.Task.Tip;
import RcRPG.Task.loadHealth;
import cn.nukkit.Server;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.util.LinkedHashMap;

public class Main extends PluginBase implements Listener {

    public static Main instance;

    public Config config;

    public static boolean money;

    public static boolean point;

    public static ScriptEngine jse;

    public static LinkedHashMap<String, Weapon> loadWeapon = new LinkedHashMap<>();
    public static LinkedHashMap<String, Armour> loadArmour = new LinkedHashMap<>();
    public static LinkedHashMap<String, Stone> loadStone = new LinkedHashMap<>();
    public static LinkedHashMap<String, Magic> loadMagic = new LinkedHashMap<>();
    public static LinkedHashMap<String, Shop> loadShop = new LinkedHashMap<>();

    public Main(){}

    public void onEnable(){
        jse = new ScriptEngineManager().getEngineByName("JavaScript");
        instance = this;
        this.getNewFile();
        init();
        this.saveResource("Config.yml","/Config.yml",false);
        config = new Config(this.getDataFolder() + "/Config.yml");
        this.getServer().getPluginManager().registerEvents(new Events(),this);
        this.getServer().getScheduler().scheduleRepeatingTask(new Tip(this),20);
        this.getServer().getScheduler().scheduleRepeatingTask(new loadHealth(this),10);
        this.getServer().getCommandMap().register("rpg", new Commands());
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
        this.getLogger().info("插件加载成功，作者：若尘");
    }

    public void init(){
        this.getLogger().info("开始读取武器信息");
        for(String name: Handle.getDefaultFiles("Weapon")){
            this.getLogger().info("读取 "+name+".yml");
            Weapon weapon = null;
            try {
                weapon = Weapon.loadWeapon(name,new Config(this.getDataFolder()+"/Weapon/"+name+".yml",Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(weapon != null){
                loadWeapon.put(name,weapon);
                this.getLogger().info(name+"武器数据读取成功");
            }else{
                this.getLogger().warning(name+"武器数据读取失败");
            }
        }
        this.getLogger().info("开始读取盔甲信息");
        for(String name: Handle.getDefaultFiles("Armour")){
            this.getLogger().info("读取 "+name+".yml");
            Armour armour = null;
            try {
                armour = Armour.loadArmour(name,new Config(this.getDataFolder()+"/Armour/"+name+".yml",Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(armour != null){
                loadArmour.put(name,armour);
                this.getLogger().info(name+"盔甲数据读取成功");
            }else{
                this.getLogger().warning(name+"盔甲数据读取失败");
            }
        }
        this.getLogger().info("开始读取宝石信息");
        for(String name: Handle.getDefaultFiles("Stone")){
            this.getLogger().info("读取 "+name+".yml");
            Stone stone = null;
            try {
                stone = Stone.loadStone(name,new Config(this.getDataFolder()+"/Stone/"+name+".yml",Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(stone != null){
                loadStone.put(name,stone);
                this.getLogger().info(name+"宝石数据读取成功");
            }else{
                this.getLogger().warning(name+"宝石数据读取失败");
            }
        }
        this.getLogger().info("开始读取魔法物品信息");
        for(String name: Handle.getDefaultFiles("Magic")){
            this.getLogger().info("读取 "+name+".yml");
            Magic magic = null;
            try {
                magic = Magic.loadMagic(name,new Config(this.getDataFolder()+"/Magic/"+name+".yml",Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(magic != null){
                loadMagic.put(name,magic);
                this.getLogger().info(name+"魔法物品数据读取成功");
            }else{
                this.getLogger().warning(name+"魔法物品数据读取失败");
            }
        }
        this.getLogger().info("开始读取商店信息");
        for(String name: Handle.getDefaultFiles("Shop")){
            this.getLogger().info("读取 "+name+".yml");
            Shop shop = null;
            try {
                shop = Shop.loadShop(name,new Config(this.getDataFolder()+"/Shop/"+name+".yml",Config.YAML));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if(shop != null){
                loadShop.put(name,shop);
                this.getLogger().info(name+"商店数据读取成功");
            }else{
                this.getLogger().warning(name+"商店数据读取失败");
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
    public File getMagicFile() {
        return new File(this.getDataFolder() + "/Magic");
    }
    public File getStoneFile() {
        return new File(this.getDataFolder() + "/Stone");
    }
    public File getGuildFile() {
        return new File(this.getDataFolder() + "/Guild");
    }
    public File getShopFile() {
        return new File(this.getDataFolder() + "/Shop");
    }

    public void getNewFile(){
        File playerFile = this.getPlayerFile();
        if (!playerFile.exists() && !playerFile.mkdirs()) {
            Server.getInstance().getLogger().info("/Players文件夹创建失败");
        }
        File weaponFile = this.getWeaponFile();
        if (!weaponFile.exists() && !weaponFile.mkdirs()) {
            Server.getInstance().getLogger().info("/Weapon文件夹创建失败");
        }
        File armourFile = this.getArmourFile();
        if (!armourFile.exists() && !armourFile.mkdirs()) {
            Server.getInstance().getLogger().info("/Armour文件夹创建失败");
        }
        File magicFile = this.getMagicFile();
        if (!magicFile.exists() && !magicFile.mkdirs()) {
            Server.getInstance().getLogger().info("/Magic文件夹创建失败");
        }
        File stoneFile = this.getStoneFile();
        if (!stoneFile.exists() && !stoneFile.mkdirs()) {
            Server.getInstance().getLogger().info("/Stone文件夹创建失败");
        }
        File guildFile = this.getGuildFile();
        if (!guildFile.exists() && !guildFile.mkdirs()) {
            Server.getInstance().getLogger().info("/Guild文件夹创建失败");
        }
        File shopFile = this.getShopFile();
        if (!shopFile.exists() && !shopFile.mkdirs()) {
            Server.getInstance().getLogger().info("/Shop文件夹创建失败");
        }
    }

}
