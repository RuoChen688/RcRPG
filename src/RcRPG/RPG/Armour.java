package RcRPG.RPG;

import RcRPG.Handle;
import RcRPG.Main;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

public class Armour {

    private Config config;

    private String name;

    private String label;

    private Item item;

    private int health;

    private int damage;
    
    private int reDamage;

    private ArrayList<Effect> effects = new ArrayList<>();
    
    private int stone;

    private String tipText;

    private String myMessage;

    private String serverMessage;

    private String message;

    private ArrayList<String> loreList = new ArrayList<>();
    
    public Armour(String name,Config config){
        this.name = name;
        this.config = config;
    }

    public static Armour loadArmour(String name,Config config){
        try{
            Armour armour = new Armour(name,config);

            armour.setLabel(config.getString("标签"));
            armour.setItem(Item.get(Integer.parseInt(config.getString("物品ID").split(":")[0]),Integer.parseInt(config.getString("物品ID").split(":")[1])));
            armour.setHealth(config.getInt("血量"));
            armour.setDamage(config.getInt("攻击"));
            armour.setReDamage(config.getInt("减伤"));
            armour.setMessage(config.getString("介绍"));
            armour.setTipText(config.getString("底部显示"));
            armour.setMyMessage(config.getString("个人通知"));
            armour.setServerMessage(config.getString("全服通知"));
            armour.setStone(config.getInt("宝石孔数"));

            ArrayList<Effect> list1 = new ArrayList<>();
            for(String effect : config.getStringList("药水效果")){
                String[] o = effect.split(":");
                list1.add(Effect.getEffect(Integer.parseInt(o[0])).setAmplifier(Integer.parseInt(o[1])-1).setDuration(Integer.parseInt(o[2])*20));
            }
            armour.setEffects(list1);
            ArrayList<String> list2 = new ArrayList<>();
            for(String lore : config.getStringList("显示")){
                list2.add(lore);
            }
            armour.setLoreList(list2);

            return armour;
        }catch(Exception e){
            Main.instance.getLogger().error("加载盔甲"+name+"配置文件失败");
            return null;
        }
    }

    public static Config getArmourConfig(String name){
        File file = new File(Main.instance.getDataFolder()+"/Armour/"+name+".yml");
        Config config;
        if(file.exists()){
            config = new Config(file,Config.YAML);
        }else{
            return null;
        }
        return config;
    }

    public static Config addArmourConfig(String name,int id,int subid){
        if(getArmourConfig(name) == null){
            Main.instance.saveResource("Armour.yml","/Armour/"+name+".yml",false);
            Config config = new Config(Main.instance.getArmourFile()+"/"+name+".yml");
            config.set("物品ID",id+":"+subid);
            config.save();
            return config;
        }
        return null;
    }

    public static boolean delArmourConfig(String name){
        if(getArmourConfig(name) != null){
            File file = new File(Main.instance.getArmourFile(),"/"+name+".yml");
            file.delete();
            return true;
        }
        return false;
    }

    public static boolean giveArmour(Player player, String name, int count){
        if(Main.loadArmour.get(name) != null){
            Armour armour = Main.loadArmour.get(name);
            Item item = armour.getItem();
            item.setCount(count);
            CompoundTag tag = item.getNamedTag();
            if(tag == null){
                tag = new CompoundTag();
            }
            tag.putString("type","armour");
            tag.putString("name",name);
            tag.putByte("Unbreakable",1);
            item.setNamedTag(tag);
            Armour.setArmourLore(item);
            item.setCustomName(armour.getLabel());
            player.getInventory().addItem(item);
            if(!armour.getMyMessage().equals("")){
                String text = armour.getMyMessage();
                if(text.contains("@player")) text = text.replace("@player", player.getName());
                if(text.contains("@item")) text = text.replace("@item", armour.getLabel());
                player.sendMessage(text);
            }
            if(!armour.getServerMessage().equals("")){
                String text = armour.getServerMessage();
                if(text.contains("@player")) text = text.replace("@player", player.getName());
                if(text.contains("@item")) text = text.replace("@item", armour.getLabel());
                Main.instance.getServer().broadcastMessage(text);
            }
            return true;
        }
        return false;
    }

    public static boolean isArmour(Item item){
        if(item.getNamedTag() != null){
            if(item.getNamedTag().contains("type")){
                return item.getNamedTag().getString("type").equals("armour");
            }
            return false;
        }
        return false;
    }

    public static LinkedList<Stone> getStones(Item item){
        LinkedList<Stone> list = new LinkedList<>();
        if(Armour.isArmour(item)){
            Armour armour = Main.loadArmour.get(item.getNamedTag().getString("name"));
            ListTag<StringTag> tags = item.getNamedTag().getList("stone",StringTag.class);
            for(StringTag tag : tags.getAll()){
                list.add(Handle.getStoneByLabel(tag.parseValue()));
            }
            while(list.size() < armour.getStone()){
                list.add(null);
            }
        }
        return list;
    }

    public static int getStoneSize(Item item){
        LinkedList<Stone> list = Armour.getStones(item);
        int i = 0;
        for(Stone stone : list){
            if(stone != null) i++;
        }
        return i;
    }

    @Deprecated
    public static boolean canInlay(Item item){
        if(Armour.isArmour(item)){
            Armour armour = Main.loadArmour.get(item.getNamedTag().getString("name"));
            return Armour.getStoneSize(item) < armour.getStone();
        }else{
            return false;
        }
    }

    public static void setStone(Player player,Item item,LinkedList<Stone> list){
        ListTag<StringTag> stoneList = new ListTag<>("stone");
        for(Stone stone : list){
            if(stone == null) continue;
            stoneList.add(new StringTag(stone.getLabel(),stone.getLabel()));
        }
        CompoundTag tag = item.getNamedTag();
        tag.putList(stoneList);
        item.setNamedTag(tag);
        player.getInventory().setItemInHand(Armour.setArmourLore(item));
    }

    public static int getStoneHealth(Item item){
        if(Armour.isArmour(item)){
            LinkedList<Stone> list = Armour.getStones(item);
            int damage = 0;
            for(Stone stone : list){
                if(stone != null) damage += stone.getHealth();
            }
            return damage;
        }
        return 0;
    }

    public static int getStoneDamage(Item item){
        if(Armour.isArmour(item)){
            LinkedList<Stone> list = Armour.getStones(item);
            int damage = 0;
            for(Stone stone : list){
                if(stone != null) damage += stone.getDamage();
            }
            return damage;
        }
        return 0;
    }

    public static int getStoneReDamage(Item item){
        if(Armour.isArmour(item)){
            LinkedList<Stone> list = Armour.getStones(item);
            int damage = 0;
            for(Stone stone : list){
                if(stone != null) damage += stone.getReDamage();
            }
            return damage;
        }
        return 0;
    }

    public static Item setArmourLore(Item item){
        if(Armour.isArmour(item)){
            Armour armour = Main.loadArmour.get(item.getNamedTag().getString("name"));
            ArrayList<String> lore = (ArrayList<String>) armour.getLoreList().clone();
            for(int i = 0;i < lore.size();i++){
                String s = lore.get(i);
                if(s.contains("@message")) s = s.replace("@message",armour.getMessage());
                if(s.contains("@Health")) s = s.replace("@Health",String.valueOf(armour.getHealth()));
                if(s.contains("@Damage")) s = s.replace("@Damage",String.valueOf(armour.getDamage()));
                if(s.contains("@reDamage")) s = s.replace("@reDamage",String.valueOf(armour.getReDamage()));
                if(s.contains("@stoneHealth")) s = s.replace("@stoneHealth",String.valueOf(Armour.getStoneHealth(item)));
                if(s.contains("@stoneDamage")) s = s.replace("@stoneDamage",String.valueOf(Armour.getStoneDamage(item)));
                if(s.contains("@stoneReDamage")) s = s.replace("@stoneReDamage",String.valueOf(Armour.getStoneReDamage(item)));
                lore.set(i,s);
            }
            item.setLore(lore.toArray(new String[0]));
        }
        return item;
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

    public ArrayList<Effect> getEffects() {
        return effects;
    }

    public void setEffects(ArrayList<Effect> effects) {
        this.effects = effects;
    }

    public int getStone() {
        return stone;
    }

    public void setStone(int stone) {
        this.stone = stone;
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

    public ArrayList<String> getLoreList() {
        return loreList;
    }

    public void setLoreList(ArrayList<String> loreList) {
        this.loreList = loreList;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
