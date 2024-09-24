package RcRPG.AttrManager;

import RcRPG.RcRPGMain;
import cn.nukkit.lang.LangCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static RcRPG.AttrManager.PlayerAttr.valueToString;

public class ItemAttr extends Manager {

    /** 属性结构
     * {
     *     "攻击力": [1,3]
     * }
     * */
    protected Map<String, float[]> mainAttr;

    public Map<String, float[]> getMainAttr() {
        return mainAttr;
    }
    public void setItemAttrConfig(Map<String, Object> newAttr) {
        Map<String, float[]> attrMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : newAttr.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof List<?> values) {
                float[] floatValues = new float[values.size()];
                for (int i = 0; i < values.size(); i++) {
                    if (values.get(i) instanceof Double) {
                        floatValues[i] = ((Double) values.get(i)).floatValue();
                    } else if (values.get(i) instanceof Integer) {
                        floatValues[i] = ((Integer) values.get(i)).floatValue();
                    }
                }
                attrMap.put(key, floatValues);
            } else if (value instanceof float[] floatValue){
                if (floatValue.length == 1) {
                    float[] newValue = { floatValue[0], floatValue[0] };
                    attrMap.put(key, newValue);
                } else {
                    attrMap.put(key, floatValue);
                }
            } else {
                RcRPGMain.getInstance().getLogger().warning(key + " ItemAttr 中不知道是啥类型");
            }
        }
        mainAttr = attrMap;
    }

    public float getItemAttr(String attrName) {
        Map<String, float[]> mainAttrMap = mainAttr;
        float[] data;
        if (mainAttrMap.containsKey(attrName)) {
            data = mainAttrMap.get(attrName);
        } else {
            data = new float[] {0, 0};
        }
        return getRandomNum(data);
    }

    /**
     * 获取指定属性的原始值
     * @param attrName 属性名
     * @param index 索引，0为min，1为max。-1为min-max的随机值
     * @return
     */
    public float getItemAttr(String attrName, int index) {
        if (index == -1) {
            return getItemAttr(attrName);
        }
        float[] data;
        if (mainAttr.containsKey(attrName)) {
            data = mainAttr.get(attrName);
        } else {
            data = new float[] {0, 0};
        }
        assert index == 0 || index == 1 : "Index should be 0 or 1";
        return data[index];
    }

    /**
     * 返回 min-max 的随机值
     * @param array [最小值, 最大值]
     * @return
     */
    public static float getRandomNum(float[] array) {
        int length = 0;
        if (array.length == 1 || array[0] == array[1]) {
            return array[0];
        }
        for (float v : array) {
            String last = String.valueOf(v).split("\\.")[1];
            if (last != null && length < last.length()) {
                length = last.length();
            }
        }
        length = (int) Math.pow(10, length + 2);
        float minNum = array[0] * length;
        float maxNum = array[1] * length;
        return (float) (Math.random() * (maxNum - minNum + 1) + minNum) / length;
    }

    public String replaceAttrTemplate(String str) {
        Pattern pattern = Pattern.compile("\\{\\{(.*?)}}");
        Matcher matcher = pattern.matcher(str);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            AttrNameParse attrName = AttrNameParse.processString(matcher.group(1));
            String replacement = PlayerAttr.valueToString(new float[]{getItemAttr(attrName.getResult(), attrName.getState())}, attrName.getResult());
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    public String attrInfo(LangCode langCode) {
        StringBuilder sb = new StringBuilder();
        for (String attrName : getMainAttr().keySet()) {
            float[] value = mainAttr.get(attrName);
            sb.append(RcRPGMain.getInstance().attrTemplateConfig.getAttributeText(langCode, attrName, value[0], value[1]));
            sb.append('\n');
        }
        sb.deleteCharAt(sb.length() - 1);// Remove the trailing "\n"
        return sb.toString();
    }

    //激进向 (9)

    @Override
    public float[] getPvpAttackPower() {
        if (mainAttr.containsKey("攻击力")) {
            return mainAttr.get("攻击力");
        }
        if (mainAttr.containsKey("PVP攻击力")) {
            return mainAttr.get("PVP攻击力");
        }
        return new float[0];
    }

    @Override
    public float[] getPveAttackPower() {
        if (mainAttr.containsKey("攻击力")) {
            return mainAttr.get("攻击力");
        }
        if (mainAttr.containsKey("PVE攻击力")) {
            return mainAttr.get("PVE攻击力");
        }
        return new float[0];
    }

    @Override
    public float[] getPvpAttackMultiplier() {
        if (mainAttr.containsKey("攻击加成")) {
            return mainAttr.get("攻击加成");
        }
        if (mainAttr.containsKey("PVP攻击加成")) {
            return mainAttr.get("PVP攻击加成");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getPveAttackMultiplier() {
        if (mainAttr.containsKey("PVE攻击加成")) {
            return mainAttr.get("PVE攻击加成");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getCritChance() {
        if (mainAttr.containsKey("暴击率")) {
            return mainAttr.get("暴击率");
        }
        return new float[0];
    }

    @Override
    public float[] getCriticalStrikeMultiplier() {
        if (mainAttr.containsKey("暴击倍率")) {
            return mainAttr.get("暴击倍率");
        }
        return new float[0];
    }

    @Override
    public float[] getLifestealChance() {
        if (mainAttr.containsKey("吸血率")) {
            return mainAttr.get("吸血率");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getLifestealMultiplier() {
        if (mainAttr.containsKey("吸血倍率")) {
            return mainAttr.get("吸血倍率");
        }
        return new float[0];
    }

    @Override
    public float[] getDefensePenetrationChance() {
        if (mainAttr.containsKey("破防率")) {
            return mainAttr.get("破防率");
        }
        return new float[0];
    }

    @Override
    public float[] getDefensePenetrationValue() {
        if (mainAttr.containsKey("破防攻击")) {
            return mainAttr.get("破防攻击");
        }
        return new float[0];
    }

    @Override
    public float[] getArmorPenetrationChance() {
        if (mainAttr.containsKey("破甲率")) {
            return mainAttr.get("破甲率");
        }
        return new float[0];
    }

    @Override
    public float[] getArmorPenetrationValue() {
        if (mainAttr.containsKey("破甲强度")) {
            return mainAttr.get("破甲强度");
        }
        return new float[0];
    }

    @Override
    public float[] getHitChance() {
        if (mainAttr.containsKey("命中率")) {
            return mainAttr.get("命中率");
        }
        return new float[0];
    }

    @Override
    public float[] getDamageMultiplier() {
        if (mainAttr.containsKey("伤害加成")) {
            return mainAttr.get("伤害加成");
        }
        return new float[0];
    }

//保守向 (9)

    @Override
    public float[] getDodgeChance() {
        if (mainAttr.containsKey("闪避率")) {
            return mainAttr.get("闪避率");
        }
        return new float[0];
    }

    @Override
    public float[] getCritResistance() {
        if (mainAttr.containsKey("暴击抵抗")) {
            return mainAttr.get("暴击抵抗");
        }
        return new float[0];
    }

    @Override
    public float[] getLifestealResistance() {
        if (mainAttr.containsKey("吸血抵抗")) {
            return mainAttr.get("吸血抵抗");
        }
        return new float[0];
    }

    @Override
    public float[] getHp() {
        if (mainAttr.containsKey("血量值")) {
            return mainAttr.get("血量值");
        }
        return new float[0];
    }

    @Override
    public float[] getDefense() {
        if (mainAttr.containsKey("防御力")) {
            return mainAttr.get("防御力");
        }
        return new float[0];
    }

    @Override
    public float[] getMaxHpMultiplier() {
        if (mainAttr.containsKey("血量加成")) {
            return mainAttr.get("血量加成");
        }
        return new float[0];
    }

    @Override
    public float[] getDefenseMultiplier() {
        if (mainAttr.containsKey("防御加成")) {
            return mainAttr.get("防御加成");
        }
        return new float[0];
    }

    @Override
    public float[] getHpRegenMultiplier() {
        if (mainAttr.containsKey("生命加成")) {
            return mainAttr.get("生命加成");
        }
        return new float[0];
    }

    @Override
    public float[] getArmorStrengthMultiplier() {
        if (mainAttr.containsKey("护甲强度")) {
            return mainAttr.get("护甲强度");
        }
        return new float[0];
    }

//辅助增益向 (3)

    @Override
    public float[] getExperienceGainMultiplier() {
        if (mainAttr.containsKey("经验加成")) {
            return mainAttr.get("经验加成");
        }
        return new float[0];
    }

    @Override
    public float[] getHpPerSecond() {
        if (mainAttr.containsKey("每秒恢复")) {
            return mainAttr.get("每秒恢复");
        }
        return new float[0];
    }

    @Override
    public float[] getHpPerNature() {
        if (mainAttr.containsKey("生命恢复")) {
            return mainAttr.get("生命恢复");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getMovementSpeedMultiplier() {
        if (mainAttr.containsKey("移速加成")) {
            return mainAttr.get("移速加成");
        }
        return new float[0];
    }

    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        for (String i : mainAttr.keySet()) {
            float[] value = mainAttr.get(i);
            String valueString = valueToString(value, i);

            if (valueString.equals("0")) {
                continue;
            }

            str.append(" ").append(i).append(": ").append(valueString).append("\n");
        }
        return str.toString();
    }

}
