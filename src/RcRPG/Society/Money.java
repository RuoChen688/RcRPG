package RcRPG.Society;

import RcRPG.Handle;
import RcRPG.Main;
import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import me.onebone.economyapi.EconomyAPI;

public class Money {

    public static int getMoney(Player player){
        if(!Main.money){
            String name = player.getName();
            if(Handle.getPlayerConfig(name) != null){
                Config config = Handle.getPlayerConfig(name);
                return config.getInt("金币");
            }
            return -1;
        }else{
            return (int) EconomyAPI.getInstance().myMoney(player);
        }
    }

    public static boolean addMoney(Player player,int money){
        if(!Main.money){
            String name = player.getName();
            if(Handle.getPlayerConfig(name) != null){
                Config config = Handle.getPlayerConfig(name);
                config.set("金币",config.getInt("金币") + money);
                config.save();
                if(!Main.instance.config.getString("金币增加提示").equals("")){
                    String text = Main.instance.config.getString("金币增加提示");
                    if(text.contains("@player")) text = text.replace("@player",name);
                    if(text.contains("@money")) text = text.replace("@money",String.valueOf(money));
                    player.sendMessage(text);
                }
                return true;
            }else{
                return false;
            }
        }else{
            EconomyAPI.getInstance().addMoney(player,money);
            return true;
        }
    }

    public static boolean delMoney(Player player,int money){
        if(!Main.money){
            String name = player.getName();
            if(Handle.getPlayerConfig(name) != null){
                Config config = Handle.getPlayerConfig(name);
                if(config.getInt("金币") >= money){
                    config.set("金币",config.getInt("金币")-money);
                }else{
                    config.set("金币",0);
                }
                config.save();
                return true;
            }
            return false;
        }else{
            EconomyAPI.getInstance().reduceMoney(player,money);
            return true;
        }
    }


}
