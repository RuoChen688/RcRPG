package RcRPG.AttrManager;

import RcRPG.RcRPGMain;
import com.smallaswater.littlemonster.config.MonsterConfig;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LittleMonsterAttr extends Manager {

    public LittleMonsterAttr(MonsterConfig cfg) {
        if (cfg.getConfig().exists("属性")) {
            setItemAttrConfig(cfg.getConfig().get("属性"));
        } else {
            mainAttr.put("血量值", new float[]{cfg.getHealth(), cfg.getHealth()});
            mainAttr.put("攻击力", new float[]{cfg.getDamage(), cfg.getDamage()});
            mainAttr.put("防御力", new float[]{cfg.getDelDamage(), cfg.getDelDamage()});
        }
    }

    @Getter
    private Map<String, float[]> mainAttr = new HashMap<>();

    public void setItemAttrConfig(Object newAttr) {
        Map<String, float[]> attrMap = new HashMap<>();
        Map<String, Object> attr = (Map<String, Object>) newAttr;
        for (Map.Entry<String, Object> entry : attr.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof List<?> values) {
                float[] floatValues = new float[2];
                for (int i = 0; i < values.size(); i++) {
                    if (values.get(i) instanceof Double) {
                        floatValues[i] = ((Double) values.get(i)).floatValue();
                    } else if (values.get(i) instanceof Integer) {
                        floatValues[i] = ((Integer) values.get(i)).floatValue();
                    }
                }
                if (values.size() == 1) {
                    floatValues[1] = floatValues[0];
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
                RcRPGMain.getInstance().getLogger().warning(key + "不知道是啥类型");
            }
        }

        mainAttr = attrMap;
    }
    /**
     * 返回 [最小值, 最大值] 的随机值
     * @param array
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
     * @param index 索引，0为min，1为max。内部可能传入-1
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

    //激进向 (9)

    @Override
    public float[] getPvpAttackPower() {
        if (mainAttr.containsKey("攻击力")) {
            return mainAttr.get("攻击力");
        } else if (mainAttr.containsKey("PVP攻击力")) {
            return mainAttr.get("PVP攻击力");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getPveAttackPower() {
        if (mainAttr.containsKey("攻击力")) {
            return mainAttr.get("攻击力");
        } else if (mainAttr.containsKey("PVE攻击力")) {
            return mainAttr.get("PVE攻击力");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getPvpAttackMultiplier() {
        if (mainAttr.containsKey("攻击加成")) {
            return mainAttr.get("攻击加成");
        } else if (mainAttr.containsKey("PVP攻击加成")) {
            return mainAttr.get("PVP攻击加成");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getPveAttackMultiplier() {
        if (mainAttr.containsKey("攻击加成")) {
            return mainAttr.get("攻击加成");
        } else if (mainAttr.containsKey("PVE攻击加成")) {
            return mainAttr.get("PVE攻击加成");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getCritChance() {
        if (mainAttr.containsKey("暴击率")) {
            return mainAttr.get("暴击率");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getCriticalStrikeMultiplier() {
        if (mainAttr.containsKey("暴击倍率")) {
            return mainAttr.get("暴击倍率");
        }
        return new float[]{ 0.0f, 0.0f };
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
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getDefensePenetrationChance() {
        if (mainAttr.containsKey("破防率")) {
            return mainAttr.get("破防率");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getDefensePenetrationValue() {
        if (mainAttr.containsKey("破防攻击")) {
            return mainAttr.get("破防攻击");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getArmorPenetrationChance() {
        if (mainAttr.containsKey("破甲率")) {
            return mainAttr.get("破甲率");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getArmorPenetrationValue() {
        if (mainAttr.containsKey("破甲强度")) {
            return mainAttr.get("破甲强度");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getHitChance() {
        if (mainAttr.containsKey("命中率")) {
            return mainAttr.get("命中率");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getDamageMultiplier() {
        if (mainAttr.containsKey("伤害加成")) {
            return mainAttr.get("伤害加成");
        }
        return new float[]{ 0.0f, 0.0f };
    }

//保守向 (9)

    @Override
    public float[] getDodgeChance() {
        if (mainAttr.containsKey("闪避率")) {
            return mainAttr.get("闪避率");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getCritResistance() {
        if (mainAttr.containsKey("暴击抵抗")) {
            return mainAttr.get("暴击抵抗");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getLifestealResistance() {
        if (mainAttr.containsKey("吸血抵抗")) {
            return mainAttr.get("吸血抵抗");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getHp() {
        if (mainAttr.containsKey("血量值")) {
            return mainAttr.get("血量值");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getDefense() {
        if (mainAttr.containsKey("防御力")) {
            return mainAttr.get("防御力");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getMaxHpMultiplier() {
        if (mainAttr.containsKey("血量加成")) {
            return mainAttr.get("血量加成");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getDefenseMultiplier() {
        if (mainAttr.containsKey("防御加成")) {
            return mainAttr.get("防御加成");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getHpRegenMultiplier() {
        if (mainAttr.containsKey("生命加成")) {
            return mainAttr.get("生命加成");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getArmorStrengthMultiplier() {
        if (mainAttr.containsKey("护甲强度")) {
            return mainAttr.get("护甲强度");
        }
        return new float[]{ 0.0f, 0.0f };
    }

//辅助增益向 (3)

    @Override
    public float[] getExperienceGainMultiplier() {
        if (mainAttr.containsKey("经验加成")) {
            return mainAttr.get("经验加成");
        }
        return new float[]{ 0.0f, 0.0f };
    }

    @Override
    public float[] getHpPerSecond() {
        if (mainAttr.containsKey("每秒恢复")) {
            return mainAttr.get("每秒恢复");
        }
        return new float[]{ 0.0f, 0.0f };
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
        return new float[]{ 0.0f, 0.0f };
    }

}