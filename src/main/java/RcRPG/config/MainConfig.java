package RcRPG.config;

import RcRPG.RcRPGMain;
import cn.nukkit.utils.Config;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainConfig {
    public static Config config;

    @Getter
    public static String chatFormat = "";
    @Getter
    public static String topFormat = "";
    @Getter
    public static String bottomFormat = "";
    @Getter
    public static boolean expSystemDisabled;
    @Getter
    public static boolean prefixSystemDisabled;
    @Getter
    public static String expGainMessage = "";
    @Getter
    public static String levelUpMessage = "";
    @Getter
    public static int expIncrement;
    @Getter
    public static int maxLevel;
    @Getter
    public static String hpIncreasePerLevel = "";
    @Getter
    public static String moneyGainMessage = "";
    @Getter
    public static String pointGainMessage = "";
    @Getter
    public static int ornamentEffectSlots;
    @Getter
    public static String initialPrefix;
    @Getter
    public static String initialGuild;
    @Getter
    public static int initialGuildMembers;
    @Getter
    public static int initialGuildCreationFunds;
    @Getter
    public static List<String> guildUpgradeCosts = new ArrayList<>();
    @Getter
    public static List<String> guildUpgradeMembers = new ArrayList<>();
    @Getter
    public static List<String> attrDisplayPercent = new ArrayList<>();

    public static void init() {
        if (!new File(RcRPGMain.getInstance().getDataFolder(), "config.yml").exists()) {
            RcRPGMain.getInstance().saveResource("config.yml");
        }
        config = new Config(new File(RcRPGMain.getInstance().getDataFolder(), "config.yml"), Config.YAML);

        // 读取配置文件中的值
        chatFormat = config.getString("聊天显示");
        topFormat = config.getString("顶部显示");
        bottomFormat = config.getString("底部显示");
        expSystemDisabled = config.getBoolean("RcRPG经验.disable");
        prefixSystemDisabled = config.getBoolean("称号.disable");
        expGainMessage = config.getString("经验增加提示");
        levelUpMessage = config.getString("等级增加提示");
        expIncrement = config.getInt("经验增量");
        maxLevel = config.getInt("最大等级");
        hpIncreasePerLevel = config.getString("等级增加血量");
        moneyGainMessage = config.getString("金币增加提示");
        pointGainMessage = config.getString("点券增加提示");
        ornamentEffectSlots = config.getInt("饰品生效格数");
        initialPrefix = config.getString("初始称号");
        initialGuild = config.getString("初始公会");
        initialGuildMembers = config.getInt("公会初始人数");
        initialGuildCreationFunds = config.getInt("公会创建初始资金");
        guildUpgradeCosts = config.getStringList("公会升级金币");
        guildUpgradeMembers = config.getStringList("公会升级人数");
        attrDisplayPercent = config.getStringList("AttrDisplayPercent");

    }
}
