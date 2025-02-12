package RcRPG.RPG;

import RcRPG.AttrManager.ItemAttr;
import RcRPG.RcRPGMain;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

@Getter
@Setter
public class Stone extends ItemAttr {

    private Config config;

    private String name;

    private String label;

    private String showName;

    private Item item;

    private int health;

    private int damage;

    private int reDamage;

    private Map<String, Object> attr;

    private String tipText;

    private String myMessage;

    private String serverMessage;

    private String message;

    private String type;

    private ArrayList<String> loreList = new ArrayList<>();

    public Stone(String name,Config config){
        this.name = name;
        this.config = config;
    }

    public static Stone loadStone(String name,Config config){
        try{
            Stone stone = new Stone(name,config);

            stone.setLabel(config.getString("标签"));
            stone.setShowName(config.getString("显示名称"));
            stone.setItem(Item.fromString(config.getString("物品ID")));
            stone.setType(config.getString("宝石类型"));
            if (config.exists("属性")) {
                stone.setAttr((Map<String, Object>) config.get("属性"));
            }

            stone.setMessage(config.getString("介绍", ""));
            stone.setTipText(config.getString("底部显示"));
            stone.setMyMessage(config.getString("个人通知"));
            stone.setServerMessage(config.getString("全服通知"));

            ArrayList<String> loreList = new ArrayList<>(config.getStringList("显示"));
            stone.setLoreList(loreList);

            return stone;
        }catch(Exception e){
            RcRPGMain.getInstance().getLogger().error("加载宝石"+name+"配置文件失败");
            return null;
        }
    }

    public static Config getStoneConfig(String name){
        File file = new File(RcRPGMain.getInstance().getDataFolder()+"/Stone/"+name+".yml");
        Config config;
        if(file.exists()){
            config = new Config(file,Config.YAML);
        }else{
            return null;
        }
        return config;
    }

    public static Config addStoneConfig(String name,String id){
        if(getStoneConfig(name) == null){
            RcRPGMain.getInstance().saveResource("Stone/Stone.yml","/Stone/"+name+".yml",false);
            Config config = new Config(RcRPGMain.getInstance().getStoneFile()+"/"+name+".yml");
            config.set("物品ID",id);
            config.save();
            return config;
        }
        return null;
    }

    public static boolean delStoneConfig(String name){
        if(getStoneConfig(name) == null) return false;
        File file = new File(RcRPGMain.getInstance().getStoneFile(), name+".yml");
        file.delete();
        return true;
    }

    public static Item setStoneLore(Item item) {
        if (Stone.isStone(item)) {
            Stone stone = RcRPGMain.loadStone.get(item.getNamedTag().getString("name"));
            ArrayList<String> lore = (ArrayList<String>) stone.getLoreList().clone();
            for (int i = 0;i < lore.size();i++) {
                String s = lore.get(i);
                if(s.contains("@message")) s = s.replace("@message",stone.getMessage());
                s = stone.replaceAttrTemplate(s);// 替换属性的值
                lore.set(i, s);
            }
            item.setLore(lore.toArray(new String[0]));
        }
        return item;
    }
    public static Item getItem(String name, int count) {
        if(!RcRPGMain.loadStone.containsKey(name)) {
            return Item.AIR_ITEM;
        }
        Stone stone = RcRPGMain.loadStone.get(name);
        Item item = stone.getItem();
        item.setCount(count);
        CompoundTag tag = item.hasCompoundTag() ? item.getNamedTag() : new CompoundTag();
        tag.putString("type", "stone");
        tag.putString("name", name);
        tag.putByte("Unbreakable", 1);
        item.setNamedTag(tag);
        item.setCustomName(stone.getShowName());
        Stone.setStoneLore(item);
        return item;
    }
    public static boolean giveStone(Player player, String name, int count){
        if (!RcRPGMain.loadStone.containsKey(name)) {
            return false;
        }
        Stone stone = RcRPGMain.loadStone.get(name);
        Item item = getItem(name, count);
        if (item.isNull()) {
            return false;
        }
        player.getInventory().addItem(item);
        if(!stone.getMyMessage().isEmpty()){
            String text = stone.getMyMessage();
            if(text.contains("@player")) text = text.replace("@player", player.getName());
            if(text.contains("@item")) text = text.replace("@item", stone.getLabel());
            player.sendMessage(text);
        }
        if(!stone.getServerMessage().isEmpty()){
            String text = stone.getServerMessage();
            if(text.contains("@player")) text = text.replace("@player", player.getName());
            if(text.contains("@item")) text = text.replace("@item", stone.getLabel());
            RcRPGMain.getInstance().getServer().broadcastMessage(text);
        }
        return true;
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

    /**
     * 通过 label 获取玩家背包中所有该类型的宝石
     * @param player 玩家对象
     * @param type 宝石类型
     * @return 宝石的yamlName列表
     */
    public static LinkedList<String> getStonesViaType(Player player, String type){
        LinkedList<String> list = new LinkedList<>();
        Item item;
        for(int i = 0;i < player.getInventory().getSize();i++){
            item = player.getInventory().getItem(i);
            if(Stone.isStone(item)){
                String stoneName = item.getNamedTag().getString("name");
                if (!RcRPGMain.loadStone.containsKey(stoneName)) {
                    continue;
                }
                if(!list.contains(stoneName) && RcRPGMain.loadStone.get(stoneName).getType().equals(type)){
                    list.add(stoneName);
                }
            }
        }
        return list;
    }

    public void setAttr(Map<String, Object> attr) {
        this.attr = attr;
        setItemAttrConfig(attr);
    }

}
