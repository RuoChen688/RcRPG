package RcRPG.Society;

import RcRPG.Handle;
import RcRPG.Main;
import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import net.player.api.Point;

public class Points {

    public static int getPoint(Player player){
        if(!Main.point){
            String name = player.getName();
            if(Handle.getPlayerConfig(name) != null){
                Config config = Handle.getPlayerConfig(name);
                return config.getInt("点券");
            }
            return -1;
        }else{
            return (int) Point.getPoint(player);
        }
    }

    public static boolean addPoint(Player player,int point){
        if(!Main.point){
            String name = player.getName();
            if(Handle.getPlayerConfig(name) != null){
                Config config = Handle.getPlayerConfig(name);
                config.set("点券",config.getInt("点券") + point);
                config.save();
                if(!Main.instance.config.getString("点券增加提示").equals("")){
                    String text = Main.instance.config.getString("点券增加提示");
                    if(text.contains("@player")) text = text.replace("@player",name);
                    if(text.contains("@point")) text = text.replace("@point",String.valueOf(point));
                    player.sendMessage(text);
                }
                return true;
            }
            return false;
        }else{
            Point.addPoint(player,point);
            return true;
        }
    }

    public static boolean delPoint(Player player,int point){
        if(!Main.point){
            String name = player.getName();
            if(Handle.getPlayerConfig(name) != null){
                Config config = Handle.getPlayerConfig(name);
                if(config.getInt("点券") >= point){
                    config.set("点券",config.getInt("点券")-point);
                }else{
                    config.set("点券",0);
                }
                config.save();
                return true;
            }
            return false;
        }else{
            Point.reducePoint(player,point);
            return true;
        }
    }

}
