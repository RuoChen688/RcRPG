package RcRPG.RPG;

import RcRPG.RcRPGMain;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;

@Getter
@Setter
public class Magic {

    private Config config;

    private String name;

    private String label;

    private Item item;

    private boolean consume;

    private ArrayList<Effect> effects = new ArrayList<>();

    private ArrayList<String> commands = new ArrayList<>();

    private String tipText;

    private String myMessage;

    private String serverMessage;

    private String message;

    public Magic(String name,Config config){
        this.name = name;
        this.config = config;
    }

    public static Magic loadMagic(String name,Config config){
        try{
            Magic magic = new Magic(name,config);

            magic.setLabel(config.getString("标签"));
            magic.setItem(Item.fromString(config.getString("物品ID")));
            magic.setConsume(config.getBoolean("是否为消耗品"));
            magic.setMessage(config.getString("介绍"));
            magic.setTipText(config.getString("底部显示"));
            magic.setMyMessage(config.getString("个人通知"));
            magic.setServerMessage(config.getString("全服通知"));

            ArrayList<Effect> list1 = new ArrayList<>();
            for(String effect : config.getStringList("药水效果")){
                String[] o = effect.split(":");
                list1.add(Effect.getEffect(Integer.parseInt(o[0])).setAmplifier(Integer.parseInt(o[1])-1).setDuration(Integer.parseInt(o[2])*20));
            }
            magic.setEffects(list1);
            ArrayList<String> list2 = new ArrayList<>();
            for(String command : config.getStringList("指令")){
                list2.add(command);
            }
            magic.setCommands(list2);

            return magic;
        }catch(Exception e){
            RcRPGMain.getInstance().getLogger().error("加载魔法物品"+name+"配置文件失败");
            return null;
        }
    }

    public static Config getMagicConfig(String name){
        File file = new File(RcRPGMain.getInstance().getDataFolder()+"/Magic/"+name+".yml");
        Config config;
        if(file.exists()){
            config = new Config(file,Config.YAML);
        }else{
            return null;
        }
        return config;
    }

    public static Config addMagicConfig(String name,String id){
        if(getMagicConfig(name) == null){
            RcRPGMain.getInstance().saveResource("Magic/Magic.yml","/Magic/"+name+".yml",false);
            Config config = new Config(RcRPGMain.getInstance().getMagicFile()+"/"+name+".yml");
            config.set("物品ID",id);
            config.save();
            return config;
        }
        return null;
    }

    public static boolean delMagicConfig(String name){
        if(getMagicConfig(name) != null){
            File file = new File(RcRPGMain.getInstance().getMagicFile(),"/"+name+".yml");
            file.delete();
            return true;
        }
        return false;
    }

    public static boolean giveMagic(Player player, String name, int count){
        if(RcRPGMain.loadMagic.get(name) != null){
            Magic magic = RcRPGMain.loadMagic.get(name);
            Item item = magic.getItem();
            item.setCount(count);
            CompoundTag tag = item.getNamedTag();
            if(tag == null){
                tag = new CompoundTag();
            }
            tag.putString("type","magic");
            tag.putString("name",name);
            tag.putByte("Unbreakable",1);
            item.setNamedTag(tag);
            item.setLore(magic.lore());
            item.setCustomName(magic.getLabel());
            player.getInventory().addItem(item);
            if(!magic.getMyMessage().equals("")){
                String text = magic.getMyMessage();
                if(text.contains("@player")) text = text.replace("@player", player.getName());
                if(text.contains("@item")) text = text.replace("@item", magic.getLabel());
                player.sendMessage(text);
            }
            if(!magic.getServerMessage().equals("")){
                String text = magic.getServerMessage();
                if(text.contains("@player")) text = text.replace("@player", player.getName());
                if(text.contains("@item")) text = text.replace("@item", magic.getLabel());
                RcRPGMain.getInstance().getServer().broadcastMessage(text);
            }
            return true;
        }
        return false;
    }

    public static boolean isMagic(Item item){
        if(item.getNamedTag() != null){
            if(item.getNamedTag().contains("type")){
                return item.getNamedTag().getString("type").equals("magic");
            }
            return false;
        }
        return false;
    }

    public static void useMagic(Player player,Item item){
        Magic magic = RcRPGMain.loadMagic.get(item.getNamedTag().getString("name"));
        if(!magic.getEffects().isEmpty()){
            for(Effect effect : magic.getEffects()){
                effect.add(player);
            }
        }
        if(!magic.getCommands().isEmpty()){
            for(String command : magic.getCommands()){
                if(command.contains("@player")) command = command.replace("@player",player.getName());
                Server.getInstance().dispatchCommand(new ConsoleCommandSender(), command);
            }
        }
        if(magic.isConsume()){
            if(item.getCount() == 1){
                player.getInventory().remove(item);
            }else{
                item.setCount(item.getCount()-1);
                player.getInventory().setItemInHand(item);
            }
        }
    }

    public String[] lore() {
        ArrayList<String> lore = new ArrayList();
        lore.add("§r§f═§7╞════════════╡§f═");
        lore.add("§r§6◈§7类型§6◈§a " + "魔法物品");
        lore.add("§r§f═§7╞════════════╡§f═");
        lore.add("§r§6◈§7介绍§6◈ §a" + this.message);
        lore.add("§r§f═§7╞════════════╡§f═");
        return (String[])lore.toArray(new String[0]);
    }

}
