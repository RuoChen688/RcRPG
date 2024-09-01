package RcRPG.config;

import RcRPG.RcRPGMain;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
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
    public static int[] hpIncreasePerLevel = new int[]{};
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
    public static DamageMessageConfig enableDamageMessage = new DamageMessageConfig(new ConfigSection());

    /**
     * 以百分比显示的属性列表
     */
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
        readHpIncreasePerLevel();
        moneyGainMessage = config.getString("金币增加提示");
        pointGainMessage = config.getString("点券增加提示");
        ornamentEffectSlots = config.getInt("饰品生效格数");
        initialPrefix = config.getString("初始称号");
        initialGuild = config.getString("初始公会");
        initialGuildMembers = config.getInt("公会初始人数");
        initialGuildCreationFunds = config.getInt("公会创建初始资金");
        guildUpgradeCosts = config.getStringList("公会升级金币");
        guildUpgradeMembers = config.getStringList("公会升级人数");
        enableDamageMessage = new DamageMessageConfig(config.getSection("enableDamageMessage"));
        readAttrDisplayPercent();// 初始化以百分比显示的属性列表

    }

    public static void readHpIncreasePerLevel() {
        String[] cfgValue = config.getString("等级增加血量", "10:0").split(":");
        if (cfgValue.length < 2 || cfgValue[1].isEmpty()) {
            hpIncreasePerLevel = new int[]{};
        } else {
            hpIncreasePerLevel = new int[]{Integer.parseInt(cfgValue[0]), Integer.parseInt(cfgValue[1])};
        }
    }

    public static void readAttrDisplayPercent() {
        if (config.exists("AttrDisplayPercent")) {
            attrDisplayPercent.clear();
            attrDisplayPercent = config.getStringList("AttrDisplayPercent");
        } else {
            attrDisplayPercent.clear();

            // 添加激进向 (12)的百分比属性
            attrDisplayPercent.add("暴击率");
            attrDisplayPercent.add("暴击倍率");
            attrDisplayPercent.add("吸血率");
            attrDisplayPercent.add("吸血倍率");
            attrDisplayPercent.add("破防率");
            attrDisplayPercent.add("破甲率");
            attrDisplayPercent.add("破甲强度");
            attrDisplayPercent.add("命中率");
            attrDisplayPercent.add("伤害加成");
            attrDisplayPercent.add("PVP攻击加成");
            attrDisplayPercent.add("PVE攻击加成");

            // 添加保守向 (10)的百分比属性
            attrDisplayPercent.add("反伤率");
            attrDisplayPercent.add("闪避率");
            attrDisplayPercent.add("暴击闪避");
            attrDisplayPercent.add("暴击抵抗");
            attrDisplayPercent.add("吸血抵抗");
            attrDisplayPercent.add("血量加成");
            attrDisplayPercent.add("防御加成");
            attrDisplayPercent.add("护甲强度");
            attrDisplayPercent.add("生命加成");

            // 添加辅助增益向 (4)的百分比属性
            attrDisplayPercent.add("经验加成");
            attrDisplayPercent.add("移速加成");

            // 添加特殊效果 (6)的百分比属性
            attrDisplayPercent.add("燃烧概率");
            attrDisplayPercent.add("雷击概率");
            attrDisplayPercent.add("冰冻概率");
        }
    }
}
