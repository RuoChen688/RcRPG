package RcRPG.Society;

import RcRPG.Events;
import RcRPG.Handle;
import RcRPG.config.MainConfig;
import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import me.onebone.economyapi.EconomyAPI;

public class Money {

    public static int getMoney(Player player) {
        if (Events.hasEconomyAPI) {
            return (int) EconomyAPI.getInstance().myMoney(player);
        } else {
            String name = player.getName();
            if (Handle.getPlayerConfig(name) != null) {
                Config config = Handle.getPlayerConfig(name);
                return config.getInt("金币");
            }
            return -1;
        }
    }

    public static boolean addMoney(Player player, int money) {
        if (Events.hasEconomyAPI) {
            EconomyAPI.getInstance().addMoney(player, money);
            return true;
        } else {
            String name = player.getName();
            if (Handle.getPlayerConfig(name) != null) {
                Config config = Handle.getPlayerConfig(name);
                config.set("金币", config.getInt("金币") + money);
                config.save();
                if (!MainConfig.getMoneyGainMessage().isEmpty()) {
                    String text = MainConfig.getMoneyGainMessage();
                    if (text.contains("@player")) text = text.replace("@player", name);
                    if (text.contains("@money")) text = text.replace("@money", String.valueOf(money));
                    player.sendMessage(text);
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public static boolean delMoney(Player player, int money) {
        if (Events.hasEconomyAPI) {
            EconomyAPI.getInstance().reduceMoney(player, money);
            return true;
        } else {
            String name = player.getName();
            if (Handle.getPlayerConfig(name) != null) {
                Config config = Handle.getPlayerConfig(name);
                if (config.getInt("金币") >= money) {
                    config.set("金币", config.getInt("金币") - money);
                } else {
                    config.set("金币", 0);
                }
                config.save();
                return true;
            }
            return false;
        }
    }


}
