package RcRPG.Society;

import RcRPG.Handle;
import RcRPG.Main;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.ArrayList;

public class Guild {

    public static void addGuild(Player player, String guild){
        Main.instance.saveResource("Guild.yml","/Guild/"+guild+".yml",false);
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        config.set("名称",guild);
        config.set("公会资金",Main.instance.config.getInt("公会创建初始资金"));
        config.set("公会人数",Main.instance.config.getInt("公会初始人数"));
        config.save();
        Guild.setGuild(player,guild);
        Guild.setMaster(player,guild);
    }

    public static void setGuild(Player player,String guild){
        Config config = Handle.getPlayerConfig(player.getName());
        config.set("公会",guild);
        config.save();
    }

    public static void setMaster(Player player,String guild){
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        config.set("会长",player.getName());
        config.save();
    }

    public static void setAssistantMaster(Player player,String guild){
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        config.set("副会长",player.getName());
        config.save();
    }

    public static boolean isMaster(Player player){
        String guild = Guild.getGuild(player);
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        return config.getString("会长").equals(player.getName());
    }

    public static boolean isAssistantMaster(Player player){
        String guild = Guild.getGuild(player);
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        return config.getString("副会长").equals(player.getName());
    }

    public static String getGuild(Player player){
        return Handle.getPlayerConfig(player.getName()).getString("公会");
    }

    public static Config getGuildConfig(String guild){
        return new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
    }

    public static int getSize(String guild){
        int size = 1;
        Config config = Guild.getGuildConfig(guild);
        if(config.get("副会长") != null) size += 1;
        ArrayList<String> list = (ArrayList<String>) config.getStringList("成员");
        size += list.size();
        return size;
    }

    public static String getStatus(Player player){
        String guild = Guild.getGuild(player);
        if(Guild.isMaster(player)) return "会长";
        if(Guild.getGuildConfig(guild).getString("副会长").equals(player.getName())) return "副会长";
        return "成员";
    }

    public static ArrayList<String> getApp(String guild){
        Config config = Guild.getGuildConfig(guild);
        ArrayList<String> list = (ArrayList<String>) config.getStringList("申请者");
        return list;
    }

    public static void addMoney(Player player,int money){
        String guild = Guild.getGuild(player);
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        config.set("公会资金",config.getInt("公会资金")+money);
        config.save();
    }

    public static String getMaster(Player player){
        String guild = Guild.getGuild(player);
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        return config.getString("会长");
    }

    public static String getAssistantMaster(Player player){
        String guild = Guild.getGuild(player);
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        if(config.get("副会长") == null) return null;
        return config.getString("副会长");
    }

    public static ArrayList<String> getAllMember(Player player){
        String guild = Guild.getGuild(player);
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        ArrayList<String> list = new ArrayList<>();
        list.add(config.getString("会长"));
        if(config.get("副会长") != null) list.add(config.getString("副会长"));
        for(String member : config.getStringList("成员")){
            list.add(member);
        }
        return list;
    }

    public static void dismissGuild(Player player){
        String guild = Guild.getGuild(player);
        for(String name : Guild.getAllMember(player)){
            Config pConfig = Handle.getPlayerConfig(name);
            pConfig.set("公会",Main.instance.config.get("初始公会"));
            pConfig.save();
        }
        File file = new File(Main.instance.getGuildFile(),"/"+guild+".yml");
        file.delete();
    }

    public static void kickGuild(Player player,String name){
        String guild = Guild.getGuild(player);
        Config config = Handle.getPlayerConfig(name);
        config.set("公会",Main.instance.config.get("初始公会"));
        config.save();
        config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        if(config.getString("副会长").equals(name)) config.set("副会长",null);
        ArrayList<String> list = (ArrayList<String>) config.getStringList("成员");
        if(list.contains(name)) list.remove(name);
        config.set("成员",list);
        config.save();
    }

    public static void acceptApp(Player player,String name){
        String guild = Guild.getGuild(player);
        Config config = Handle.getPlayerConfig(name);
        config.set("公会",guild);
        config.save();
        config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        ArrayList<String> list = (ArrayList<String>) config.getStringList("成员");
        if(!list.contains(name)) list.add(name);
        config.set("成员",list);
        list = (ArrayList<String>) config.getStringList("申请者");
        if(list.contains(name)) list.remove(name);
        config.set("申请者",list);
        config.save();
    }

    public static void rejectApp(Player player,String name){
        String guild = Guild.getGuild(player);
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        ArrayList<String> list = (ArrayList<String>) config.getStringList("申请者");
        if(list.contains(name)) list.remove(name);
        config.set("申请者",list);
        config.save();
    }

    public static void appGuild(Player player,String guild){
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        ArrayList<String> list = (ArrayList<String>) config.getStringList("申请者");
        if(list.contains(player.getName())) list.add(player.getName());
        config.set("申请者",list);
        config.save();
    }

    public static int getMaxSize(String guild){
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        return config.getInt("公会人数");
    }

    public static void updateGuild(Player player){
        String guild = Guild.getGuild(player);
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        int level = config.getInt("公会等级");
        config.set("公会等级",level + 1);
        config.set("公会人数",Guild.getUpdateSize(player));
        config.save();
    }

    public static int getUpdateMoney(Player player){
        String guild = Guild.getGuild(player);
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        ArrayList<String> list = (ArrayList<String>) Main.instance.config.getStringList("公会升级金币");
        for(String s : list){
            String[] arr = s.split(":");
            if(arr[0].equals(config.getString("公会等级"))) return Integer.parseInt(arr[1]);
        }
        return 0;
    }

    public static int getUpdateSize(Player player){
        String guild = Guild.getGuild(player);
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        ArrayList<String> list = (ArrayList<String>) Main.instance.config.getStringList("公会升级人数");
        for(String s : list){
            String[] arr = s.split(":");
            if(arr[0].equals(String.valueOf(config.getInt("公会等级") + 1))) return Integer.parseInt(arr[1]);
        }
        return 0;
    }

    public static int getMoney(Player player){
        String guild = Guild.getGuild(player);
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        return config.getInt("公会资金");
    }

    public static void delMoney(Player player,int money){
        String guild = Guild.getGuild(player);
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        config.set("公会资金",config.getInt("公会资金") - money);
        config.save();
    }

    public static void tpBase(Player player){
        String guild = Guild.getGuild(player);
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        if(config.get("公会基地") == null){
            player.sendMessage("尚未设置公会基地");
        }else{
            String[] pos = config.getString("公会基地").split(":");
            player.teleport(new Position(Double.parseDouble(pos[0]),Double.parseDouble(pos[1]),Double.parseDouble(pos[2]), Server.getInstance().getLevelByName(pos[3])));
        }
    }

    public static void setBase(Player player){
        String guild = Guild.getGuild(player);
        Config config = new Config(Main.instance.getGuildFile()+"/"+guild+".yml");
        config.set("公会基地",player.x + ":" + player.y + ":" + player.z + ":" + player.level.getName());
        config.save();
        player.sendMessage("设置成功");
    }

}
