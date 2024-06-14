package RcRPG.AttrManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Manager implements AttrInterface {
    public Manager(){}
    public float pvpAttackPower = 0.0f;
    public float pveAttackPower = 0.0f;
    public float pvpAttackMultiplier = 0.0f;
    public float pveAttackMultiplier = 0.0f;
    public float critChance = 0.0f;
    public float criticalStrikeMultiplier = 0.0f;
    public float lifestealMultiplier = 0.0f;
    public float defensePenetrationChance = 0.0f;
    public float defensePenetrationValue = 0.0f;
    public float armorPenetrationChance = 0.0f;
    public float armorPenetrationValue = 0.0f;
    public float hitChance = 0.0f;
    public float damageMultiplier = 0.0f;
    public float dodgeChance = 0.0f;
    public float critDodgeChance = 0.0f;
    public float critResistance = 0.0f;
    public float lifestealChance = 0.0f;
    public float lifestealResistance = 0.0f;
    public float hp = 0.0f;
    public float defense = 0.0f;
    public float maxHpMultiplier = 0.0f;
    public float defenseMultiplier = 0.0f;
    public float hpRegenMultiplier = 0.0f;
    public float armorStrengthMultiplier = 0.0f;
    public float experienceGainMultiplier = 0.0f;
    public float hpPerSecond = 0.0f;
    public float hpPerNature = 0.0f;
    public float movementSpeedMultiplier = 0.0f;

    public void updateComp() {
        // 激进向 14
        pvpAttackPower = getRandomNum(getPvpAttackPower());
        pveAttackPower = getRandomNum(getPveAttackPower());
        pvpAttackMultiplier = getRandomNum(getPvpAttackMultiplier());
        pveAttackMultiplier = getRandomNum(getPveAttackMultiplier());
        critChance = getRandomNum(getCritChance());
        criticalStrikeMultiplier = getRandomNum(getCriticalStrikeMultiplier());
        lifestealChance = getRandomNum(getLifestealChance());
        lifestealMultiplier = getRandomNum(getLifestealMultiplier());
        defensePenetrationChance = getRandomNum(getDefensePenetrationChance());
        defensePenetrationValue = getRandomNum(getDefensePenetrationValue());
        armorPenetrationChance = getRandomNum(getArmorPenetrationChance());
        armorPenetrationValue = getRandomNum(getArmorPenetrationValue());
        hitChance = getRandomNum(getHitChance());
        damageMultiplier = getRandomNum(getDamageMultiplier());
        // 保守向 10
        dodgeChance = getRandomNum(getDodgeChance());
        critDodgeChance = getRandomNum(getCritDodgeChance());
        critResistance = getRandomNum(getCritResistance());
        lifestealResistance = getRandomNum(getLifestealResistance());
        hp = getRandomNum(getHp());
        defense = getRandomNum(getDefense());
        maxHpMultiplier = getRandomNum(getMaxHpMultiplier());
        defenseMultiplier = getRandomNum(getDefenseMultiplier());
        hpRegenMultiplier = getRandomNum(getHpRegenMultiplier());
        armorStrengthMultiplier = getRandomNum(getArmorStrengthMultiplier());
        // 辅助增益 3
        experienceGainMultiplier = getRandomNum(getExperienceGainMultiplier());
        hpPerSecond = getRandomNum(getHpPerSecond());
        hpPerNature = getRandomNum(getHpPerNature());
        movementSpeedMultiplier = getRandomNum(getMovementSpeedMultiplier());
        // ...继续更新其他变量的值
    }
    @Override
    public AttrComp getComp() {
        return null;
    }

    /**
     * 返回 [最小值, 最大值] 的随机值
     * @param array 数组
     * @return 随机值
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

    protected Map<String, float[]> deepCopyMap(Map<String, float[]> originalMap) {
        Map<String, float[]> copiedMap = new HashMap<>();
        if (originalMap == null) return copiedMap;

        for (Map.Entry<String, float[]> entry : originalMap.entrySet()) {
            String key = entry.getKey();
            float[] value = entry.getValue();
            float[] copiedValue = Arrays.copyOf(value, value.length);
            copiedMap.put(key, copiedValue);
        }

        return copiedMap;
    }

    /**
     * 检查 float[] 是否为空
     * @param array float[]
     * @return boolean
     */
    @Override
    public boolean checkFloatArray(float[] array) {
        return !Arrays.equals(array, new float[]{ 0.0f, 0.0f });
    }


    @Override
    public float getItemAttr(String attrName) {
        return 0;
    }

    /**
     * PVP攻击力
     * @return
     */
    @Override
    public float[] getPvpAttackPower() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * PVE攻击力
     * @return
     */
    @Override
    public float[] getPveAttackPower() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * PVP攻击加成
     * @return
     */
    @Override
    public float[] getPvpAttackMultiplier() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * PVE攻击加成
     * @return
     */
    @Override
    public float[] getPveAttackMultiplier() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 暴击率
     * @return
     */
    @Override
    public float[] getCritChance() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 暴击倍率
     * @return
     */
    @Override
    public float[] getCriticalStrikeMultiplier() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 吸血率
     * @return
     */
    @Override
    public float[] getLifestealChance() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 吸血倍率
     * @return
     */
    @Override
    public float[] getLifestealMultiplier() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 破防率
     * @return
     */
    @Override
    public float[] getDefensePenetrationChance() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 破防攻击
     * @return
     */
    @Override
    public float[] getDefensePenetrationValue() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 破甲率
     * @return
     */
    @Override
    public float[] getArmorPenetrationChance() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 破甲强度 - 百分比
     * @return
     */
    @Override
    public float[] getArmorPenetrationValue() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 命中率
     * @return
     */
    @Override
    public float[] getHitChance() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 伤害加成
     * @return
     */
    @Override
    public float[] getDamageMultiplier() {
        return new float[]{ 0.0f, 0.0f };
    }

    // 保守向

    /**
     * 闪避率
     * @return
     */
    @Override
    public float[] getDodgeChance() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 暴击闪避
     * @return
     */
    @Override
    public float[] getCritDodgeChance() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 暴击抵抗
     * @return
     */
    @Override
    public float[] getCritResistance() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 吸血抵抗
     * @return
     */
    @Override
    public float[] getLifestealResistance() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 血量值
     * @return
     */
    @Override
    public float[] getHp() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 防御力
     * @return
     */
    @Override
    public float[] getDefense() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 血量加成
     * @return
     */
    @Override
    public float[] getMaxHpMultiplier() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 防御加成
     * @return
     */
    @Override
    public float[] getDefenseMultiplier() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 生命加成
     * @return
     */
    @Override
    public float[] getHpRegenMultiplier() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 护甲强度
     * @return
     */
    @Override
    public float[] getArmorStrengthMultiplier() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 经验加成
     * @return
     */
    @Override
    public float[] getExperienceGainMultiplier() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 每秒恢复
     * @return
     */
    @Override
    public float[] getHpPerSecond() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 生命恢复（自然恢复时的额外恢复血量）
     * @return
     */
    @Override
    public float[] getHpPerNature() {
        return new float[]{ 0.0f, 0.0f };
    }

    /**
     * 移速加成
     * @return
     */
    @Override
    public float[] getMovementSpeedMultiplier() {
        return new float[]{ 0.0f, 0.0f };
    }
}
