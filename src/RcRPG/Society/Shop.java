package RcRPG.Society;

import RcRPG.Handle;
import RcRPG.Main;
import RcRPG.RPG.Armour;
import RcRPG.RPG.Magic;
import RcRPG.RPG.Stone;
import RcRPG.RPG.Weapon;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.LinkedList;

public class Shop {

    private Config config;

    private String name;

    private Position position;

    private LinkedList<String> cost;

    private LinkedList<String> gain;

    private String message;

    private String noMessage;

    private String sMessage;

    public Shop(String name,Config config){
        this.name = name;
        this.config = config;
    }

    public static Shop loadShop(String name,Config config){
        try{
            Shop shop = new Shop(name,config);

            String[] s = config.getString("位置").split(":");
            shop.setPosition(new Position(Double.parseDouble(s[0]),Double.parseDouble(s[1]),Double.parseDouble(s[2]), Server.getInstance().getLevelByName(s[3])));
            shop.setMessage(config.getString("消息提示"));
            shop.setNoMessage(config.getString("物品不足消息提示"));
            shop.setsMessage(config.getString("购买成功消息提示"));

            LinkedList<String> list1 = new LinkedList<>();
            for(String cost : config.getStringList("消耗")){
                list1.add(cost);
            }
            shop.setCost(list1);
            LinkedList<String> list2 = new LinkedList<>();
            for(String gain : config.getStringList("获得")){
                list2.add(gain);
            }
            shop.setGain(list2);

            return shop;
        }catch (Exception e){
            Main.instance.getLogger().error("加载商店"+name+"配置文件失败");
            return null;
        }
    }

    public static Config getShopConfig(String name){
        File file = new File(Main.instance.getDataFolder()+"/Shop/"+name+".yml");
        Config config;
        if(file.exists()){
            config = new Config(file,Config.YAML);
        }else{
            return null;
        }
        return config;
    }

    public static Config addShopConfig(String name,String pos){
        if(getShopConfig(name) == null){
            Main.instance.saveResource("Shop.yml","/Shop/"+name+".yml",false);
            Config config = new Config(Main.instance.getShopFile()+"/"+name+".yml");
            config.set("位置",pos);
            config.save();
            return config;
        }
        return null;
    }

    public static boolean delShopConfig(String name){
        if(getShopConfig(name) != null){
            File file = new File(Main.instance.getShopFile(),"/"+name+".yml");
            file.delete();
            return true;
        }
        return false;
    }

    public static boolean costPlayer(Player player,Shop shop){
        for(String cost : shop.getCost()){
            String[] s = cost.split(":");
            switch (s[0]){
                case "weapon":
                case "armour":
                case "stone":
                case "magic":
                    if(!Handle.canRemove(player,s)) return false;
                    break;
                case "money":
                    if(Integer.parseInt(s[1]) > Money.getMoney(player)) return false;
                    break;
                case "point":
                    if(Integer.parseInt(s[1]) > Points.getPoint(player)) return false;
                    break;
            }
        }
        for(String cost : shop.getCost()){
            String[] s = cost.split(":");
            switch (s[0]){
                case "weapon":
                case "armour":
                case "stone":
                case "magic":
                    Handle.remove(player,s);
                    break;
                case "money":
                    Money.delMoney(player,Integer.parseInt(s[1]));
                    break;
                case "point":
                    Points.delPoint(player,Integer.parseInt(s[1]));
                    break;
            }
        }
        return true;
    }

    public static void gainPlayer(Player player,Shop shop){
        for(String cost : shop.getGain()){
            String[] s = cost.split(":");
            switch (s[0]){
                case "weapon":
                    Weapon.giveWeapon(player,s[1],Integer.parseInt(s[2]));
                    break;
                case "armour":
                    Armour.giveArmour(player,s[1],Integer.parseInt(s[2]));
                    break;
                case "stone":
                    Stone.giveStone(player,s[1],Integer.parseInt(s[2]));
                    break;
                case "magic":
                    Magic.giveMagic(player,s[1],Integer.parseInt(s[2]));
                    break;
                case "money":
                    Money.addMoney(player,Integer.parseInt(s[1]));
                    break;
                case "point":
                    Points.addPoint(player,Integer.parseInt(s[1]));
                    break;
                case "prefix":
                    Prefix.givePrefix(player,s[1]);
                    break;
            }
        }
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public LinkedList<String> getCost() {
        return cost;
    }

    public void setCost(LinkedList<String> cost) {
        this.cost = cost;
    }

    public LinkedList<String> getGain() {
        return gain;
    }

    public void setGain(LinkedList<String> gain) {
        this.gain = gain;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNoMessage() {
        return noMessage;
    }

    public void setNoMessage(String noMessage) {
        this.noMessage = noMessage;
    }

    public String getsMessage() {
        return sMessage;
    }

    public void setsMessage(String sMessage) {
        this.sMessage = sMessage;
    }
}
