package RcRPG.config;

import RcRPG.AttrManager.PlayerAttr;
import cn.nukkit.lang.LangCode;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class AttrTemplateConfig {

    private final Map<LangCode, Map<String, AttrTemplateData>> multiLanguage = new HashMap<>();

    public AttrTemplateConfig(Config cfg) {
        for (String langKey : cfg.getKeys(false)) {
            LangCode langCode = LangCode.valueOf(langKey);
            Map<String, AttrTemplateData> attrTemplates = new HashMap<>();

            ConfigSection langSection = cfg.getSection(langKey);
            for (String attrType : langSection.keySet()) {
                // 获取具体的属性配置
                String positive = langSection.getString(attrType + ".positive");
                String negative = cfg.getString(attrType + ".negative");

                TemplateData templateData = null;
                if (cfg.exists(langKey + "." + attrType + ".template")) {
                    String singleTemplate = langSection.getString(attrType + ".template.single");
                    String doubleTemplate = langSection.getString(attrType + ".template.double");
                    templateData = new TemplateData(singleTemplate, doubleTemplate);
                }

                AttrTemplateData attrData = new AttrTemplateData(templateData, positive, negative);
                attrTemplates.put(attrType, attrData);
            }

            multiLanguage.put(langCode, attrTemplates);
        }
    }

    /**
     * 根据语言代码和属性类型获取模板信息，带有回退机制。
     *
     * @param langCode  语言代码
     * @param attrName  属性名称
     * @param min       最小值
     * @param max       最大值
     * @return 格式化后的属性字符串
     */
    public String getAttributeText(LangCode langCode, String attrName, float max, float min) {
        // 获取指定语言的属性配置
        Map<String, AttrTemplateData> langData = multiLanguage.getOrDefault(langCode, multiLanguage.get(LangCode.en_US));

        // 获取指定的属性类型配置，如果没有则回退到 default
        AttrTemplateData attrData = langData.getOrDefault(attrName, langData.get("default"));

        String maxValueStr = PlayerAttr.valueToString(new float[]{max}, attrName);
        String minValueStr = PlayerAttr.valueToString(new float[]{min}, attrName);
        String template;
        if (min != max) {
            template = attrData.getTemplate().getDoubleTemplate();
        } else {
            template = attrData.getTemplate().getSingleTemplate();
        }

        // 根据值的正负应用不同的格式
        String maxFormatted = max >= 0 ? attrData.getPositive().replace("@value", maxValueStr)
                : attrData.getNegative().replace("@value", maxValueStr);
        String minFormatted = min >= 0 ? attrData.getPositive().replace("@value", minValueStr)
                : attrData.getNegative().replace("@value", minValueStr);

        // 替换模板中的占位符
        template = template.replace("@attrName", attrName)
                .replace("@max", maxFormatted)
                .replace("@min", minFormatted);

        return template;
    }

}

@Getter
class AttrTemplateData {
    private final TemplateData template;
    private final String positive;
    private final String negative;

    public AttrTemplateData(TemplateData template, String positive, String negative) {
        this.template = template;
        this.positive = positive;
        this.negative = negative;
    }
}

@Getter
class TemplateData {
    private final String singleTemplate;
    private final String doubleTemplate;

    public TemplateData(String singleTemplate, String doubleTemplate) {
        this.singleTemplate = singleTemplate;
        this.doubleTemplate = doubleTemplate;
    }
}
