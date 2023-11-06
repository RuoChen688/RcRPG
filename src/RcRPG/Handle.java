package RcRPG;

import RcRPG.RPG.Armour;
import RcRPG.RPG.Level;
import RcRPG.RPG.Stone;
import RcRPG.Society.Shop;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

;

public class Handle {

    public static Config getPlayerConfig(String name){
        File file = new File(Main.instance.getDataFolder()+"/Players/"+name+".yml");
        if(file.exists()){
            return new Config(file,Config.YAML);
        }
        return null;
    }

    public static String[] getDefaultFiles(String fileName) {
        List<String> names = new ArrayList<>();
        File files = new File(Main.instance.getDataFolder()+ "/"+fileName);
        if(files.isDirectory()){
            File[] filesArray = files.listFiles();
            if(filesArray != null){
                for (File file : filesArray) {
                    names.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
                }
            }
        }
        return names.toArray(new String[0]);
    }

    public static List<String> getGuilds(){
        List<String> names = new ArrayList<>();
        File files = new File(Main.instance.getDataFolder()+ "/Guild");
        if(files.isDirectory()){
            File[] filesArray = files.listFiles();
            if(filesArray != null){
                for (File file : filesArray) {
                    names.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
                }
            }
        }
        return names;
    }

    public static Effect StringToEffect(String s){
        String[] ss = s.split(":");
        return Effect.getEffect(Integer.parseInt(ss[0])).setAmplifier(Integer.parseInt(ss[1])-1).setDuration(Integer.parseInt(ss[2])*20);
    }

    public static void setEffect(Player player, Effect effect){
        effect.add(player);
    }

    public static Stone getStoneByLabel(String label){
        for(Stone stone : Main.loadStone.values()){
            if(stone.getLabel().equals(label)) return stone;
        }
        return null;
    }

    public static void removeStoneByLabel(Player player,String label){
        for(int i =0;i < player.getInventory().getSize();i++){
            Item item = player.getInventory().getItem(i);
            if(Stone.isStone(item)){
                if(Main.loadStone.get(item.getNamedTag().getString("name")).getLabel().equals(label)){
                    if(item.getCount() == 1){
                        player.getInventory().remove(item);
                    }else{
                        item.setCount(item.getCount()-1);
                        player.getInventory().setItem(i,item);
                    }
                    break;
                }
            }
        }
    }

    public static Shop getShopByPos(Position pos){
        for(Shop shop : Main.loadShop.values()){
            if(shop.getPosition().equals(pos)) return shop;
        }
        return null;
    }

    public static boolean canRemove(Player player,String[] s){
        String type = s[0];
        String name = s[1];
        int count = Integer.parseInt(s[2]);
        for(int i = 0;i < player.getInventory().getSize();i++){
            Item item = player.getInventory().getItem(i);
            if(item.getNamedTag() != null && item.getNamedTag().contains("name") && item.getNamedTag().contains("type")){
                if(item.getNamedTag().getString("type").equals(type) && item.getNamedTag().getString("name").equals(name)){
                    if(count != 0){
                        if(item.getCount() >= count){
                            return true;
                        }else{
                            count -= item.getCount();
                        }
                    }else {
                        break;
                    }
                }
            }
        }
        return count == 0;
    }

    public static void remove(Player player,String[] s){
        String type = s[0];
        String name = s[1];
        int count = Integer.parseInt(s[2]);
        for(int i = 0;i < player.getInventory().getSize();i++){
            Item item = player.getInventory().getItem(i);
            if(item.getNamedTag() != null && item.getNamedTag().contains("name") && item.getNamedTag().contains("type")){
                if(item.getNamedTag().getString("type").equals(type) && item.getNamedTag().getString("name").equals(name)){
                    if(count != 0){
                        if(item.getCount() >= count){
                            item.setCount(item.getCount()-count);
                            player.getInventory().setItem(i,item);
                            break;
                        }else{
                            count -= item.getCount();
                            player.getInventory().remove(item);
                        }
                    }else {
                        break;
                    }
                }
            }
        }
    }

    public static int getMaxHealth(Player player){
        if(Handle.getPlayerConfig(player.getName()) == null) return 20;
        String s = Main.instance.config.getString("等级增加血量");
        int health1 = Level.getLevel(player) / Integer.parseInt(s.split(":")[0]) * Integer.parseInt(s.split(":")[1]);
        int health2 = 0;
        if(player.getInventory().getArmorContents().length != 0){
            for(Item armor : player.getInventory().getArmorContents()){
                if(Armour.isArmour(armor)){
                    Armour a = Main.loadArmour.get(armor.getNamedTag().getString("name"));
                    health2 += (a.getHealth() + Armour.getStoneHealth(armor));
                }
            }
        }
        return health2 + health1 + 20;
    }

    public static int random(int a,int b){
        return new Random().nextInt(b-a+1)+a;
    }


}
