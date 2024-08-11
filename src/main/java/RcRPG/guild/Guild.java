package RcRPG.guild;

import RcRPG.Handle;
import RcRPG.RcRPGMain;
import RcRPG.config.MainConfig;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Guild {

    public static void addGuild(Player player, String guild) {
        RcRPGMain.getInstance().saveResource("Guild/Guild.yml", "/Guild/" + guild + ".yml", false);
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        config.set("名称", guild);
        config.set("公会资金", MainConfig.getInitialGuildCreationFunds());
        config.set("公会人数", MainConfig.getInitialGuildMembers());
        config.save();
        Guild.setGuild(player, guild);
        Guild.setMaster(player, guild);
    }

    public static void setGuild(Player player, String guild) {
        Config config = Handle.getPlayerConfig(player.getName());
        config.set("公会", guild);
        config.save();
    }

    public static void setMaster(Player player, String guild) {
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        config.set("会长", player.getName());
        config.save();
    }

    public static void setAssistantMaster(Player player, String guild) {
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        config.set("副会长", player.getName());
        config.save();
    }

    public static boolean isMaster(Player player) {
        String guild = Guild.getGuild(player);
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        return config.getString("会长").equals(player.getName());
    }

    public static boolean isAssistantMaster(Player player) {
        String guild = Guild.getGuild(player);
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        return config.getString("副会长").equals(player.getName());
    }

    /**
     * 获取玩家所属的公会名
     *
     * @param player 玩家对象，用于确定需要查询公会信息的玩家
     * @return 返回玩家所属公会的名称，如果未加入公会则返回 null
     */
    public static String getGuild(Player player) {
        return Handle.getPlayerConfig(player.getName()).getString("公会", null);
    }

    /**
     * 根据 公会名 获取 公会配置 对象
     *
     * @param guildName 公会名，用于读取指定的公会配置文件
     * @return 返回一个Config对象，该对象封装了公会的配置信息
     */
    public static Config getGuildConfig(String guildName) {
        return new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guildName + ".yml");
    }


    public static int getSize(String guild) {
        int size = 1;
        Config config = Guild.getGuildConfig(guild);
        if (config.get("副会长") != null) size += 1;
        ArrayList<String> list = (ArrayList<String>) config.getStringList("成员");
        size += list.size();
        return size;
    }

    public static String getStatus(Player player) {
        String guild = Guild.getGuild(player);
        if (Guild.isMaster(player)) return "会长";
        if (Guild.getGuildConfig(guild).getString("副会长").equals(player.getName())) return "副会长";
        return "成员";
    }

    /**
     * 根据公会名称获取申请者列表
     *
     * @param guildName 公会名称，用于定位具体的公会配置
     * @return 返回一个包含玩家名的 ArrayList
     */
    public static ArrayList<String> getAppList(String guildName) {
        // 根据传入的公会名称，获取对应的公会配置对象
        Config config = Guild.getGuildConfig(guildName);
        // 从公会配置对象中获取名为"申请者"的字符串列表，并将其转换为 ArrayList 后返回
        return (ArrayList<String>) config.getStringList("申请者");
    }

    public static void addMoney(Player player, int money) {
        String guild = Guild.getGuild(player);
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        config.set("公会资金", config.getInt("公会资金") + money);
        config.save();
    }

    public static String getMaster(Player player) {
        String guild = Guild.getGuild(player);
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        return config.getString("会长");
    }

    public static String getAssistantMaster(Player player) {
        String guild = Guild.getGuild(player);
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        if (config.get("副会长") == null) return null;
        return config.getString("副会长");
    }

    public static ArrayList<String> getAllMember(Player player) {
        String guild = Guild.getGuild(player);
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        ArrayList<String> list = new ArrayList<>();
        list.add(config.getString("会长"));
        if (config.get("副会长") != null) list.add(config.getString("副会长"));
        for (String member : config.getStringList("成员")) {
            list.add(member);
        }
        return list;
    }

    /**
     * 解散公会
     *
     * @param player
     */
    public static void dismissGuild(Player player) {
        String guild = Guild.getGuild(player);
        for (String name : Guild.getAllMember(player)) {
            Config pConfig = Handle.getPlayerConfig(name);
            pConfig.set("公会", MainConfig.getInitialGuild());
            pConfig.save();
        }
        File file = new File(RcRPGMain.getInstance().getGuildFile(), guild + ".yml");
        file.delete();
    }

    public static void kickGuild(Player player, String name) {
        String guild = Guild.getGuild(player);
        Config config = Handle.getPlayerConfig(name);
        config.set("公会", MainConfig.getInitialGuild());
        config.save();
        config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        if (config.getString("副会长").equals(name)) config.set("副会长", null);
        ArrayList<String> list = (ArrayList<String>) config.getStringList("成员");
        list.remove(name);
        config.set("成员", list);
        config.save();
    }

    /**
     * 接受 √ 玩家的公会加入申请
     *
     * @param player
     * @param name
     */
    public static void acceptApp(Player player, String name) {
        String guildName = Guild.getGuild(player);
        Config playerConfig = Handle.getPlayerConfig(name);
        playerConfig.set("公会", guildName);
        playerConfig.save();

        Config guildConfig = getGuildConfig(guildName);
        ArrayList<String> memberList = (ArrayList<String>) guildConfig.getStringList("成员");
        if (!memberList.contains(name)) memberList.add(name);
        guildConfig.set("成员", memberList);
        ArrayList<String> appList = (ArrayList<String>) guildConfig.getStringList("申请者");
        appList.remove(name);
        guildConfig.set("申请者", appList);
        guildConfig.save();
    }

    /**
     * 拒绝 X 玩家的公会加入申请
     *
     * @param player
     * @param name
     */
    public static void rejectApp(Player player, String name) {
        String guild = Guild.getGuild(player);
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        ArrayList<String> appList = (ArrayList<String>) config.getStringList("申请者");
        appList.remove(name);
        config.set("申请者", appList);
        config.save();
    }

    /**
     * 玩家申请加入公会
     * 如果玩家尚未申请，则将其名称添加到公会配置文件中的申请人列表
     *
     * @param player 申请加入公会的玩家对象
     * @param guildName 公会名称，用于标识玩家申请加入的具体公会
     */
    public static void appGuild(Player player, String guildName) {
        // 初始化公会配置文件
        Config config = getGuildConfig(guildName);

        // 获取并检查申请人列表
        ArrayList<String> list = (ArrayList<String>) config.getStringList("申请者");

        // 如果列表中尚未包含当前玩家的申请，则添加玩家的名称
        if (!list.contains(player.getName())) {
            list.add(player.getName());
        }

        // 更新配置文件中的申请人列表
        config.set("申请者", list);

        // 保存更新后的配置文件
        config.save();
    }


    public static int getMaxSize(String guild) {
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        return config.getInt("公会人数");
    }

    public static void updateGuild(Player player) {
        String guild = Guild.getGuild(player);
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        int level = config.getInt("公会等级");
        config.set("公会等级", level + 1);
        config.set("公会人数", Guild.getUpdateSize(player));
        config.save();
    }

    public static int getUpdateMoney(Player player) {
        String guild = Guild.getGuild(player);
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        List<String> list = MainConfig.getGuildUpgradeCosts();
        for (String s : list) {
            String[] arr = s.split(":");
            if (arr[0].equals(config.getString("公会等级"))) return Integer.parseInt(arr[1]);
        }
        return 0;
    }

    public static int getUpdateSize(Player player) {
        String guild = Guild.getGuild(player);
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        List<String> list = MainConfig.getGuildUpgradeMembers();
        for (String s : list) {
            String[] arr = s.split(":");
            if (arr[0].equals(String.valueOf(config.getInt("公会等级") + 1))) return Integer.parseInt(arr[1]);
        }
        return 0;
    }

    public static int getMoney(Player player) {
        String guild = Guild.getGuild(player);
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        return config.getInt("公会资金");
    }

    public static void delMoney(Player player, int money) {
        String guild = Guild.getGuild(player);
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        config.set("公会资金", config.getInt("公会资金") - money);
        config.save();
    }

    public static void tpBase(Player player) {
        String guild = Guild.getGuild(player);
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        if (config.get("公会基地") == null) {
            player.sendMessage("尚未设置公会基地");
        } else {
            String[] pos = config.getString("公会基地").split(":");
            player.teleport(new Position(Double.parseDouble(pos[0]), Double.parseDouble(pos[1]), Double.parseDouble(pos[2]), Server.getInstance().getLevelByName(pos[3])));
        }
    }

    public static void setBase(Player player) {
        String guild = Guild.getGuild(player);
        Config config = new Config(RcRPGMain.getInstance().getGuildFile() + File.separator + guild + ".yml");
        config.set("公会基地", player.x + ":" + player.y + ":" + player.z + ":" + player.level.getName());
        config.save();
        player.sendMessage("设置成功");
    }

}
