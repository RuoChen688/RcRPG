package RcRPG.RPG;

import RcRPG.Handle;
import RcRPG.Main;
import cn.nukkit.Player;
import cn.nukkit.utils.Config;

public class Level {

    public static Level instance;

    public Level(){
        instance = this;
    }

    public static boolean addExp(Player player,int exp){
        String name = player.getName();
        if(Handle.getPlayerConfig(name) != null){
            Config config = Handle.getPlayerConfig(name);
            int newExp = config.getInt("经验") + exp;
            int level = config.getInt("等级");
            int oldLevel = level;
            while(newExp >= level * Main.instance.config.getInt("经验增量")){
                newExp -= level * Main.instance.config.getInt("经验增量");
                level++;
            }
            config.set("经验",newExp);
            String text = Main.instance.config.getString("经验增加提示");
            if(!text.equals("")){
                if(text.contains("@player")) text = text.replace("@player",name);
                if(text.contains("@exp")) text = text.replace("@exp",String.valueOf(exp));
                player.sendMessage(text);
            }
            config.save();
            addLevel(player,level - oldLevel);
            return true;
        }
        return false;
    }

    public static void addLevel(Player player, int level){
        if(level == 0) return;
        String name = player.getName();
        Config config = Handle.getPlayerConfig(name);
        int newLevel = config.getInt("等级") + level;
        if(!Main.instance.config.getString("等级增加提示").equals("")){
            String text = Main.instance.config.getString("等级增加提示");
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
        return config.getInt("等级");
    }

    public static int getExp(Player player){
        Config config = Handle.getPlayerConfig(player.getName());
        return config.getInt("经验");
    }

    public static int getMaxExp(Player player){
        Config config = Handle.getPlayerConfig(player.getName());
        int level = config.getInt("等级");
        return level * Main.instance.config.getInt("经验增量");
    }

}
