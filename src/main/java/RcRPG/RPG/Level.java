package RcRPG.RPG;

import RcRPG.Handle;
import RcRPG.config.MainConfig;
import cn.nukkit.Player;
import cn.nukkit.utils.Config;

public class Level {

    public static Level instance;
    public static boolean enable = true;

    public Level(){
        instance = this;
    }

    public static boolean addExp(Player player,int exp){
        String name = player.getName();
        if(Handle.getPlayerConfig(name) == null) {
            return false;
        }
        Config config = Handle.getPlayerConfig(name);
        int newExp = config.getInt("经验", 0) + exp;
        int level = config.getInt("等级", 0);
        int oldLevel = level;
        while(newExp >= level * MainConfig.getExpIncrement()){
            newExp -= level * MainConfig.getExpIncrement();
            level++;
        }
        config.set("经验",newExp);
        String text = MainConfig.getExpGainMessage();
        if(!text.isEmpty()){
            if(text.contains("@player")) text = text.replace("@player",name);
            if(text.contains("@exp")) text = text.replace("@exp",String.valueOf(exp));
            player.sendMessage(text);
        }
        config.save();
        addLevel(player,level - oldLevel);
        return true;
    }

    public static void addLevel(Player player, int level){
        if(level == 0) return;
        String name = player.getName();
        Config config = Handle.getPlayerConfig(name);
        int newLevel = config.getInt("等级", 0) + level;
        if(!MainConfig.getExpGainMessage().isEmpty()){
            String text = MainConfig.getExpGainMessage();
            if(text.contains("@player")) text = text.replace("@player",name);
            if(text.contains("@level")) text = text.replace("@level",String.valueOf(level));
            if(text.contains("@newLevel")) text = text.replace("@newLevel",String.valueOf(newLevel));
            player.sendMessage(text);
        }
        config.set("等级",newLevel);
        config.save();

    }

    public static int getLevel(Player player){
        Config config = Handle.getPlayerConfig(player.getName());
        if (config == null) return 0;
        return config.getInt("等级", 0);
    }

    public static int getExp(Player player){
        Config config = Handle.getPlayerConfig(player.getName());
        if (config == null) return 0;
        return config.getInt("经验", 0);
    }

    public static int getMaxExp(Player player){
        Config config = Handle.getPlayerConfig(player.getName());
        if (config == null) return 0;
        int level = config.getInt("等级", 0);
        return level * MainConfig.getExpIncrement();
    }

}
