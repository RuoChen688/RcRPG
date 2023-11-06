package RcRPG.Society;

import RcRPG.Handle;
import cn.nukkit.Player;
import cn.nukkit.utils.Config;

import java.util.ArrayList;
import java.util.List;

public class Prefix {

    public static boolean givePrefix(Player player, String prefix){
        String name = player.getName();
        if(Handle.getPlayerConfig(name) != null){
            Config config = Handle.getPlayerConfig(name);
            if(config.getList("称号列表").contains(prefix)){
                return false;
            }else{
                List list = config.getList("称号列表");
                list.add(prefix);
                config.set("称号列表",list);
                config.save();
                return true;
            }
        }
        return false;
    }

    public static ArrayList<String> getPrefix(Player player){
        String name = player.getName();
        if(Handle.getPlayerConfig(name) != null){
            Config config = Handle.getPlayerConfig(name);
            ArrayList<String> list = new ArrayList<>();
            for(String prefix : config.getStringList("称号列表")){
                list.add(prefix);
            }
            return list;
        }
        return new ArrayList<>();
    }

    public static boolean delPrefix(Player player,String prefix){
        String name = player.getName();
        if(Handle.getPlayerConfig(name) != null){
            Config config = Handle.getPlayerConfig(name);
            if(!config.getList("称号列表").contains(prefix)){
                return false;
            }else{
                List list = config.getList("称号列表");
                list.remove(prefix);
                config.set("称号列表",list);
                config.save();
                return true;
            }
        }
        return false;
    }

    public static void setPrefix(Player player,String prefix){
        String name = player.getName();
        if(Handle.getPlayerConfig(name) != null){
            Config config = Handle.getPlayerConfig(name);
            config.set("称号",prefix);
            config.save();
        }
    }

}
