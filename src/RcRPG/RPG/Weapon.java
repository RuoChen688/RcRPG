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

public class Weapon {

    private Config config;

    private String name;

    private String label;

    private Item item;

    private boolean unBreak;

    private boolean offHand;

    private int level;

    private int minDamage;

    private int maxDamage;

    private int suck;

    private int suckRound;

    private double crit;

    private int critRound;

    private int group;

    private int groupRound;

    private int reDamage;

    private int reDamageRound;

    private int fire;

    private int fireRound;

    private int frozen;

    private int frozenRound;

    private int lighting;

    private int lightRound;

    private int stone;

    private ArrayList<Effect> damagerEffect = new ArrayList<>();

    private ArrayList<Effect> damagedEffect = new ArrayList<>();

    private ArrayList<Effect> groupEffect = new ArrayList<>();

    private String killMessage;

    private String tipText;

    private String myMessage;

    private String serverMessage;

    private String message;

    private ArrayList<String> loreList = new ArrayList<>();

    public Weapon(String name,Config config){
        this.name = name;
        this.config = config;
    }

    public static Weapon loadWeapon(String name, Config config){
        try{
            Weapon weapon = new Weapon(name,config);

            weapon.setLabel(config.getString("标签"));
            weapon.setItem(Item.get(Integer.parseInt(config.getString("物品ID").split(":")[0]),Integer.parseInt(config.getString("物品ID").split(":")[1])));
            weapon.setUnBreak(config.getBoolean("无限耐久"));
            weapon.setOffHand(config.getBoolean("可副手"));
            weapon.setLevel(config.getInt("最低使用等级"));
            weapon.setMinDamage(config.getInt("min攻击"));
            weapon.setMaxDamage(config.getInt("max攻击"));
            weapon.setSuck(config.getInt("吸血"));
            weapon.setSuckRound(config.getInt("吸血概率"));
            weapon.setCrit(config.getDouble("暴击"));
            weapon.setCritRound(config.getInt("暴击概率"));
            weapon.setGroup(config.getInt("群回"));
            weapon.setGroupRound(config.getInt("群回概率"));
            weapon.setReDamage(config.getInt("减伤"));
            weapon.setReDamageRound(config.getInt("减伤概率"));
            weapon.setFire(config.getInt("燃烧时间"));
            weapon.setFireRound(config.getInt("燃烧概率"));
            weapon.setFrozen(config.getInt("冰冻时间"));
            weapon.setFrozenRound(config.getInt("冰冻概率"));
            weapon.setLighting(config.getInt("雷击"));
            weapon.setLightRound(config.getInt("雷击概率"));
            weapon.setStone(config.getInt("宝石孔数"));
            weapon.setMessage(config.getString("介绍"));

            ArrayList<Effect> list1 = new ArrayList<>();
            for(String effect : config.getStringList("攻击者药水效果")){
                list1.add(Handle.StringToEffect(effect));
            }
            weapon.setDamagerEffect(list1);
            ArrayList<Effect> list2 = new ArrayList<>();
            for(String effect : config.getStringList("受击者药水效果")){
                list2.add(Handle.StringToEffect(effect));
            }
            weapon.setDamagedEffect(list2);
            ArrayList<Effect> list3 = new ArrayList<>();
            for(String effect : config.getStringList("群体药水效果")){
                list3.add(Handle.StringToEffect(effect));
            }
            weapon.setGroupEffect(list3);
            ArrayList<String> list4 = new ArrayList<>();
            for(String lore : config.getStringList("显示")){
                list4.add(lore);
            }
            weapon.setLoreList(list4);

            weapon.setKillMessage(config.getString("击杀提示"));
            weapon.setTipText(config.getString("底部显示"));
            weapon.setMyMessage(config.getString("个人通知"));
            weapon.setServerMessage(config.getString("全服通知"));

            return weapon;
        }catch(Exception e){
            Main.instance.getLogger().error("加载武器"+name+"配置文件失败");
            return null;
        }
    }

    public static Config getWeaponConfig(String name){
        File file = new File(Main.instance.getDataFolder()+"/Weapon/"+name+".yml");
        Config config;
        if(file.exists()){
            config = new Config(file,Config.YAML);
        }else{
            return null;
        }
        return config;
    }

    public static Config addWeaponConfig(String name,int id,int subid){
        if(getWeaponConfig(name) == null){
            Main.instance.saveResource("Weapon.yml","/Weapon/"+name+".yml",false);
            Config config = new Config(Main.instance.getWeaponFile()+"/"+name+".yml");
            config.set("物品ID",id+":"+subid);
            config.save();
            return config;
        }
        return null;
    }

    public static boolean delWeaponConfig(String name){
        if(getWeaponConfig(name) != null){
            File file = new File(Main.instance.getWeaponFile(),"/"+name+".yml");
            file.delete();
            return true;
        }
        return false;
    }

    public static boolean giveWeapon(Player player,String name,int count){
        if(Main.loadWeapon.containsKey(name)){
            Weapon weapon = Main.loadWeapon.get(name);
            Item item = Main.loadWeapon.get(name).getItem();
            item.setCount(count);
            CompoundTag tag = item.getNamedTag();
            if(tag == null){
                tag = new CompoundTag();
            }
            tag.putString("type","weapon");
            tag.putString("name",name);
            if(weapon.isUnBreak()){
                tag.putByte("Unbreakable",1);
            }
            ListTag<StringTag> stoneList = new ListTag<>("stone");
            tag.putList(stoneList);
            item.setNamedTag(tag);
            item.setCustomName(weapon.getLabel());
            Weapon.setWeaponLore(item);
            player.getInventory().addItem(item);
            if(!weapon.getMyMessage().equals("")){
                String text = weapon.getMyMessage();
                if(text.contains("@player")) text = text.replace("@player", player.getName());
                if(text.contains("@item")) text = text.replace("@item", weapon.getLabel());
                player.sendMessage(text);
            }
            if(!weapon.getServerMessage().equals("")){
                String text = weapon.getServerMessage();
                if(text.contains("@player")) text = text.replace("@player", player.getName());
                if(text.contains("@item")) text = text.replace("@item", weapon.getLabel());
                Main.instance.getServer().broadcastMessage(text);
            }
            return true;
        }
        return false;
    }

    public static boolean isWeapon(Item item){
        if(item.getNamedTag() != null){
            if(item.getNamedTag().contains("type")){
                return item.getNamedTag().getString("type").equals("weapon");
            }
            return false;
        }
        return false;
    }

    public int getDamage(){
        return Handle.random(this.getMinDamage(),this.getMaxDamage());
    }

    public static LinkedList<Stone> getStones(Item item){
        LinkedList<Stone> list = new LinkedList<>();
        if(Weapon.isWeapon(item)){
            Weapon weapon = Main.loadWeapon.get(item.getNamedTag().getString("name"));
            ListTag<StringTag> tags = item.getNamedTag().getList("stone",StringTag.class);
            for(StringTag tag : tags.getAll()){
                list.add(Handle.getStoneByLabel(tag.parseValue()));
            }
            while(list.size() < weapon.getStone()){
                list.add(null);
            }
        }
        return list;
    }

    public static int getStoneSize(Item item){
        LinkedList<Stone> list = Weapon.getStones(item);
        int i = 0;
        for(Stone stone : list){
            if(stone != null) i++;
        }
        return i;
    }

    @Deprecated
    public static boolean canInlay(Item item){
        if(Weapon.isWeapon(item)){
            Weapon weapon = Main.loadWeapon.get(item.getNamedTag().getString("name"));
            return Weapon.getStoneSize(item) < weapon.getStone();
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
        player.getInventory().setItemInHand(Weapon.setWeaponLore(item));
    }

    public static int getStoneDamage(Item item){
        if(Weapon.isWeapon(item)){
            LinkedList<Stone> list = Weapon.getStones(item);
            int damage = 0;
            for(Stone stone : list){
                if(stone != null) damage += stone.getDamage();
            }
            return damage;
        }
        return 0;
    }

    public static int getStoneReDamage(Item item){
        if(Weapon.isWeapon(item)){
            LinkedList<Stone> list = Weapon.getStones(item);
            int damage = 0;
            for(Stone stone : list){
                if(stone != null) damage += stone.getReDamage();
            }
            return damage;
        }
        return 0;
    }

    public static Item setWeaponLore(Item item){
        if(Weapon.isWeapon(item)){
            Weapon weapon = Main.loadWeapon.get(item.getNamedTag().getString("name"));
            ArrayList<String> lore = (ArrayList<String>) weapon.getLoreList().clone();
            for(int i = 0;i < lore.size();i++){
                String s = lore.get(i);
                if(s.contains("@unBreak")) s = s.replace("@unBreak",weapon.unBreak ? "§a无限耐久" : (weapon.item.getMaxDurability() != -1 ? "§c会损坏" : "§a无耐久"));
                if(s.contains("@message")) s = s.replace("@message",weapon.getMessage());
                if(s.contains("@minDamage")) s = s.replace("@minDamage",String.valueOf(weapon.getMinDamage()));
                if(s.contains("@maxDamage")) s = s.replace("@maxDamage",String.valueOf(weapon.getMaxDamage()));
                if(s.contains("@reDamage")) s = s.replace("@reDamage",String.valueOf(weapon.getReDamage()));
                if(s.contains("@stoneDamage")) s = s.replace("@stoneDamage",String.valueOf(Weapon.getStoneDamage(item)));
                if(s.contains("@stoneReDamage")) s = s.replace("@stoneReDamage",String.valueOf(Weapon.getStoneReDamage(item)));
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

    public boolean isUnBreak() {
        return unBreak;
    }

    public void setUnBreak(boolean unBreak) {
        this.unBreak = unBreak;
    }

    public boolean isOffHand() {
        return offHand;
    }

    public void setOffHand(boolean offHand) {
        this.offHand = offHand;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public void setMinDamage(int minDamage) {
        this.minDamage = minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    public int getSuck() {
        return suck;
    }

    public void setSuck(int suck) {
        this.suck = suck;
    }

    public int getSuckRound() {
        return suckRound;
    }

    public void setSuckRound(int suckRound) {
        this.suckRound = suckRound;
    }

    public double getCrit() {
        return crit;
    }

    public void setCrit(double crit) {
        this.crit = crit;
    }

    public int getCritRound() {
        return critRound;
    }

    public void setCritRound(int critRound) {
        this.critRound = critRound;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getGroupRound() {
        return groupRound;
    }

    public void setGroupRound(int groupRound) {
        this.groupRound = groupRound;
    }

    public int getReDamage() {
        return reDamage;
    }

    public void setReDamage(int reDamage) {
        this.reDamage = reDamage;
    }

    public int getReDamageRound() {
        return reDamageRound;
    }

    public void setReDamageRound(int reDamageRound) {
        this.reDamageRound = reDamageRound;
    }

    public int getFire() {
        return fire;
    }

    public void setFire(int fire) {
        this.fire = fire;
    }

    public int getFireRound() {
        return fireRound;
    }

    public void setFireRound(int fireRound) {
        this.fireRound = fireRound;
    }

    public int getFrozen() {
        return frozen;
    }

    public void setFrozen(int frozen) {
        this.frozen = frozen;
    }

    public int getFrozenRound() {
        return frozenRound;
    }

    public void setFrozenRound(int frozenRound) {
        this.frozenRound = frozenRound;
    }

    public int getLighting() {
        return lighting;
    }

    public void setLighting(int lighting) {
        this.lighting = lighting;
    }

    public int getLightRound() {
        return lightRound;
    }

    public void setLightRound(int lightRound) {
        this.lightRound = lightRound;
    }

    public int getStone() {
        return stone;
    }

    public void setStone(int stone) {
        this.stone = stone;
    }

    public ArrayList<Effect> getDamagerEffect() {
        return damagerEffect;
    }

    public void setDamagerEffect(ArrayList<Effect> damagerEffect) {
        this.damagerEffect = damagerEffect;
    }

    public ArrayList<Effect> getDamagedEffect() {
        return damagedEffect;
    }

    public void setDamagedEffect(ArrayList<Effect> damagedEffect) {
        this.damagedEffect = damagedEffect;
    }

    public ArrayList<Effect> getGroupEffect() {
        return groupEffect;
    }

    public void setGroupEffect(ArrayList<Effect> groupEffect) {
        this.groupEffect = groupEffect;
    }

    public String getKillMessage() {
        return killMessage;
    }

    public void setKillMessage(String killMessage) {
        this.killMessage = killMessage;
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
}
