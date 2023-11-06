package RcRPG.RPG;

import RcRPG.Main;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class Stone {

    private Config config;

    private String name;

    private String label;

    private Item item;

    private int health;

    private int damage;

    private int reDamage;

    private String tipText;

    private String myMessage;

    private String serverMessage;

    private String message;

    public Stone(String name,Config config){
        this.name = name;
        this.config = config;
    }

    public static Stone loadStone(String name,Config config){
        try{
            Stone stone = new Stone(name,config);

            stone.setLabel(config.getString("标签"));
            stone.setItem(Item.get(Integer.parseInt(config.getString("物品ID").split(":")[0]),Integer.parseInt(config.getString("物品ID").split(":")[1])));
            stone.setHealth(config.getInt("血量"));
            stone.setDamage(config.getInt("攻击"));
            stone.setReDamage(config.getInt("减伤"));
            stone.setMessage(config.getString("介绍"));
            stone.setTipText(config.getString("底部显示"));
            stone.setMyMessage(config.getString("个人通知"));
            stone.setServerMessage(config.getString("全服通知"));

            return stone;
        }catch(Exception e){
            Main.instance.getLogger().error("加载宝石"+name+"配置文件失败");
            return null;
        }
    }

    public static Config getStoneConfig(String name){
        File file = new File(Main.instance.getDataFolder()+"/Stone/"+name+".yml");
        Config config;
        if(file.exists()){
            config = new Config(file,Config.YAML);
        }else{
            return null;
        }
        return config;
    }

    public static Config addStoneConfig(String name,int id,int subid){
        if(getStoneConfig(name) == null){
            Main.instance.saveResource("Stone.yml","/Stone/"+name+".yml",false);
            Config config = new Config(Main.instance.getStoneFile()+"/"+name+".yml");
            config.set("物品ID",id+":"+subid);
            config.save();
            return config;
        }
        return null;
    }

    public static boolean delStoneConfig(String name){
        if(getStoneConfig(name) != null){
            File file = new File(Main.instance.getStoneFile(),"/"+name+".yml");
            file.delete();
            return true;
        }
        return false;
    }

    public static boolean giveStone(Player player, String name, int count){
        if(Main.loadStone.containsKey(name)){
            Stone stone = Main.loadStone.get(name);
            Item item = Main.loadStone.get(name).getItem();
            item.setCount(count);
            item.setLore(stone.lore());
            CompoundTag tag = item.getNamedTag();
            if(tag == null){
                tag = new CompoundTag();
            }
            tag.putString("type","stone");
            tag.putString("name",name);
            tag.putByte("Unbreakable",1);
            item.setNamedTag(tag);
            item.setCustomName(stone.getLabel());
            player.getInventory().addItem(item);
            if(!stone.getMyMessage().equals("")){
                String text = stone.getMyMessage();
                if(text.contains("@player")) text = text.replace("@player", player.getName());
                if(text.contains("@item")) text = text.replace("@item", stone.getLabel());
                player.sendMessage(text);
            }
            if(!stone.getServerMessage().equals("")){
                String text = stone.getServerMessage();
                if(text.contains("@player")) text = text.replace("@player", player.getName());
                if(text.contains("@item")) text = text.replace("@item", stone.getLabel());
                Main.instance.getServer().broadcastMessage(text);
            }
            return true;
        }
        return false;
    }

    public static boolean isStone(Item item){
        if(item.getNamedTag() != null){
            if(item.getNamedTag().contains("type")){
                return item.getNamedTag().getString("type").equals("stone");
            }
            return false;
        }
        return false;
    }

    public static LinkedList<String> getStones(Player player,String df){
        LinkedList<String> list = new LinkedList<>();
        Item item;
        for(int i = 0;i < player.getInventory().getSize();i++){
            item = player.getInventory().getItem(i);
            if(Stone.isStone(item)){
                if(!list.contains(Main.loadStone.get(item.getNamedTag().getString("name")).getLabel()) && !Main.loadStone.get(item.getNamedTag().getString("name")).getLabel().equals(df)){
                    list.add(Main.loadStone.get(item.getNamedTag().getString("name")).getLabel());
                }
            }
        }
        return list;
    }

    public String[] lore() {
        ArrayList<String> lore = new ArrayList();
        lore.add("§r§f═§7╞════════════╡§f═");
        lore.add("§r§6◈§7类型§6◈§a " + "宝石");
        lore.add("§r§f═§7╞════════════╡§f═");
        lore.add("§r§6◈§7介绍§6◈ §a" + this.message);
        lore.add("§r§f═§7╞════════════╡§f═");
        lore.add("§r§6◈§7血量§6◈ §a" + this.health);
        lore.add("§r§6◈§7攻击§6◈ §a" + this.damage);
        lore.add("§r§6◈§7减伤§6◈ §a" + this.reDamage);
        lore.add("§r§6◈§f═§7╞════════════╡§f═");
        return (String[])lore.toArray(new String[0]);
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getReDamage() {
        return reDamage;
    }

    public void setReDamage(int reDamage) {
        this.reDamage = reDamage;
    }

    public String getTipText() {
        return tipText;
    }

    public void setTipText(String tipText) {
        this.tipText = tipText;
    }

    public String getMyMessage() {
        return myMessage;
    }

    public void setMyMessage(String myMessage) {
        this.myMessage = myMessage;
    }

    public String getServerMessage() {
        return serverMessage;
    }

    public void setServerMessage(String serverMessage) {
        this.serverMessage = serverMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
