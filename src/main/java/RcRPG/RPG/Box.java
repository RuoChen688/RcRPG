package RcRPG.RPG;

import RcRPG.RcRPGMain;
import RcRPG.PlayerStatus;
import RcRPG.Society.Money;
import RcRPG.Society.Points;
import RcRPG.Society.Prefix;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

@Getter
@Setter
public class Box {

    private Config config;

    private String name;

    private String label;

    private Item item;

    private int size;

    private int time;

    private ArrayList<String> reward;

    private String tipText;

    private String myMessage;

    private String serverMessage;

    private String message;

    public Box(String name,Config config){
        this.name = name;
        this.config = config;
    }

    public static Box loadBox(String name,Config config){
        try{
            Box box = new Box(name,config);

            box.setLabel(config.getString("标签"));
            box.setItem(Item.fromString(config.getString("物品ID")));
            box.setSize(config.getInt("容量"));
            box.setTime(config.getInt("时间"));
            ArrayList<String> list = new ArrayList<>(config.getStringList("奖励"));
            box.setReward(list);
            box.setMessage(config.getString("介绍", ""));
            box.setTipText(config.getString("底部显示"));
            box.setMyMessage(config.getString("个人通知"));
            box.setServerMessage(config.getString("全服通知"));

            return box;
        }catch(Exception e){
            RcRPGMain.getInstance().getLogger().error("加载箱子"+name+"配置文件失败");
            return null;
        }
    }

    public static Config getBoxConfig(String name){
        File file = new File(RcRPGMain.getInstance().getDataFolder()+"/Box/"+name+".yml");
        Config config;
        if(file.exists()){
            config = new Config(file,Config.YAML);
        }else{
            return null;
        }
        return config;
    }

    public static Config addBoxConfig(String name,String id){
        if(getBoxConfig(name) == null){
            RcRPGMain.getInstance().saveResource("Box/Box.yml","/Box/"+name+".yml",false);
            Config config = new Config(RcRPGMain.getInstance().getBoxFile()+"/"+name+".yml");
            config.set("物品ID",id);
            config.save();
            return config;
        }
        return null;
    }

    public static boolean delBoxConfig(String name){
        if(getBoxConfig(name) == null) return false;
        File file = new File(RcRPGMain.getInstance().getBoxFile(), name+".yml");
        file.delete();
        return true;
    }

    public static boolean giveBox(Player player, String name, int count){
        if(RcRPGMain.loadBox.containsKey(name)){
            Box box = RcRPGMain.loadBox.get(name);
            Item item = RcRPGMain.loadBox.get(name).getItem();
            item.setCount(count);
            item.setLore(box.lore());
            CompoundTag tag = item.getNamedTag();
            if(tag == null){
                tag = new CompoundTag();
            }
            tag.putString("type","box");
            tag.putString("name",name);
            tag.putByte("Unbreakable",1);
            item.setNamedTag(tag);
            item.setCustomName(box.getLabel());
            player.getInventory().addItem(item);
            if(!box.getMyMessage().isEmpty()){
                String text = box.getMyMessage();
                if(text.contains("@player")) text = text.replace("@player", player.getName());
                if(text.contains("@item")) text = text.replace("@item", box.getLabel());
                player.sendMessage(text);
            }
            if(!box.getServerMessage().isEmpty()){
                String text = box.getServerMessage();
                if(text.contains("@player")) text = text.replace("@player", player.getName());
                if(text.contains("@item")) text = text.replace("@item", box.getLabel());
                RcRPGMain.getInstance().getServer().broadcastMessage(text);
            }
            return true;
        }
        return false;
    }

    public static boolean isBox(Item item){
        if(item.getNamedTag() != null){
            if(item.getNamedTag().contains("type")){
                return item.getNamedTag().getString("type").equals("box");
            }
            return false;
        }
        return false;
    }

    public String[] lore() {
        ArrayList<String> lore = new ArrayList();
        lore.add("§r§f═§7╞════════════╡§f═");
        lore.add("§r§6◈§7类型§6◈§a " + "箱子");
        lore.add("§r§f═§7╞════════════╡§f═");
        lore.add("§r§6◈§7介绍§6◈ §a" + this.message);
        lore.add("§r§f═§7╞════════════╡§f═");
        lore.add("§r§6◈§7容量§6◈ §a" + this.size);
        lore.add("§r§6◈§7解锁时间§6◈ §a" + this.time);
        lore.add("§r§6◈§f═§7╞════════════╡§f═");
        return (String[])lore.toArray(new String[0]);
    }

    public static void useBox(Player player,Item item){
        Box box = RcRPGMain.loadBox.get(item.getNamedTag().getString("name"));
        if(PlayerStatus.getSize(player) < box.getSize()){
            player.sendMessage("箱子槽数不够啦，你只有"+PlayerStatus.getSize(player)+"格啦");
            return;
        }
        LinkedHashMap<Box, ArrayList<Integer>> boxes;
        if(PlayerStatus.playerBox.containsKey(player)){
            boxes = PlayerStatus.playerBox.get(player);
            ArrayList<Integer> list;
            if(boxes.containsKey(box)){
                list = boxes.get(box);
            }else{
                list = new ArrayList<>();
            }
            list.add(box.getTime());
            boxes.put(box,list);
        }else{
            boxes = new LinkedHashMap<>();
            ArrayList<Integer> list = new ArrayList<>();
            list.add(box.getTime());
            boxes.put(box,list);
        }
        PlayerStatus.playerBox.put(player,boxes);
        if(item.getCount() == 1){
            player.getInventory().remove(item);
        }else{
            item.setCount(item.getCount()-1);
            player.getInventory().setItemInHand(item);
        }
    }

    public static void gainPlayer(Player player, Box box){
        for(String cost : box.getReward()){
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

}