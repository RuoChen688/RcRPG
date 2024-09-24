package RcRPG.config;

import cn.nukkit.lang.LangCode;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static RcRPG.RcRPGMain.loadStone;

@Getter
public class GemTemplateConfig {

    private final Map<LangCode, Map<String, Map<String, String>>> MULTI_LANGUAGE = new HashMap<>();

    public GemTemplateConfig(Config cfg) {
        // 遍历所有语言代码 (en_US, zh_CN 等)
        for (String langKey : cfg.getKeys(false)) {
            LangCode langCode = LangCode.valueOf(langKey);  // 假设 LangCode 枚举与语言代码匹配
            Map<String, Map<String, String>> gemTemplates = new HashMap<>();

            // 获取该语言的所有宝石类型配置 (default, 攻击宝石, 生命宝石 等)
            ConfigSection langSection = cfg.getSection(langKey);
            for (String gemType : langSection.keySet()) {
                Map<String, String> states = new HashMap<>();

                // 获取具体的`insertable`和`embedded`值
                String insertable = langSection.getString(gemType + ".insertable");
                String embedded = langSection.getString(gemType + ".embedded");

                // 存储这些值到 states 中
                states.put("insertable", insertable);
                states.put("embedded", embedded);

                // 将 states 存储到 gemTemplates 中
                gemTemplates.put(gemType, states);
            }

            // 将语言代码与其模板数据对应，存入 MULTI_LANGUAGE
            MULTI_LANGUAGE.put(langCode, gemTemplates);
        }
    }

    /**
     * 根据语言代码和宝石类型获取模板信息，带有回退机制。
     *
     * @param langCode    语言代码
     * @param gemType     宝石类型
     * @param state       状态 (insertable 或 embedded)
     * @return 模板字符串
     */
    public String templateTr(LangCode langCode, String gemType, String state) {
        // 尝试获取指定语言的宝石配置
        Map<String, Map<String, String>> langData = MULTI_LANGUAGE.getOrDefault(langCode, MULTI_LANGUAGE.get(LangCode.en_US));

        // 尝试获取指定的宝石类型，如果没有则回退到 default
        Map<String, String> gemData = langData.getOrDefault(gemType, langData.get("default"));

        // 获取具体的状态 (insertable 或 embedded) 的值
        return gemData.get(state);
    }

    /**
     * 根据语言代码和宝石类型获取模板信息，带有回退机制。
     *
     * @param langCode    语言代码
     * @param nbt         物品nbt
     * @param stoneMax    最大宝石数量
     * @param stoneSlots  宝石槽位列表
     * @return 模板字符串
     */
    public String getTemplateText(LangCode langCode, CompoundTag nbt, int stoneMax, ArrayList<String> stoneSlots) {
        ListTag<StringTag> stoneList = (ListTag<StringTag>) nbt.getList("stone");
        ArrayList<String> templateList = new ArrayList<>();  // 用于存储每个模板字符串

        for (int i = 0; i < stoneMax; i++) {
            String template;
            if (stoneList.isEmpty() || i >= stoneList.size()) {
                if (i < stoneSlots.size()) {
                    template = templateTr(langCode, stoneSlots.get(i), "insertable")
                            .replace("@stoneType", stoneSlots.get(i));
                } else {
                    template = templateTr(langCode, "default", "insertable")
                            .replace("@stoneType", stoneSlots.get(i));
                }
            } else {
                String stoneName = stoneList.get(i).parseValue();
                if (stoneName.isEmpty()) {
                    template = templateTr(langCode, stoneSlots.get(i), "insertable")
                            .replace("@stoneType", stoneSlots.get(i));
                } else if (i < stoneSlots.size()) {
                    template = templateTr(langCode, stoneSlots.get(i), "embedded")
                            .replace("@stoneName", loadStone.containsKey(stoneName) ? loadStone.get(stoneName).getShowName() : "unknown");
                } else {
                    template = templateTr(langCode, "default", "embedded")
                            .replace("@stoneName", loadStone.containsKey(stoneName) ? loadStone.get(stoneName).getShowName() : "unknown");
                }
            }
            templateList.add(template);  // 将模板字符串添加到列表中
        }

        return String.join("\n", templateList);
    }

}
