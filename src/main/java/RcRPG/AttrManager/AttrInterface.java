package RcRPG.AttrManager;

public interface AttrInterface {
    boolean checkFloatArray(float[] array);
    /**
     * 获取指定属性值（随机后）
     * @param attrName 属性名
     * @return
     */
    float getItemAttr(String attrName);

    float[] getPvpAttackPower();

    float[] getPveAttackPower();

    float[] getPvpAttackMultiplier();

    float[] getPveAttackMultiplier();

    float[] getCritChance();

    float[] getCriticalStrikeMultiplier();

    float[] getLifestealChance();

    float[] getLifestealMultiplier();

    float[] getDefensePenetrationChance();

    float[] getDefensePenetrationValue();

    float[] getArmorPenetrationChance();

    float[] getArmorPenetrationValue();

    float[] getHitChance();

    float[] getDamageMultiplier();

    float[] getDodgeChance();

    float[] getCritDodgeChance();

    float[] getCritResistance();

    float[] getLifestealResistance();

    float[] getHp();

    float[] getDefense();

    float[] getMaxHpMultiplier();

    float[] getDefenseMultiplier();

    float[] getHpRegenMultiplier();

    float[] getArmorStrengthMultiplier();

    float[] getExperienceGainMultiplier();

    float[] getHpPerSecond();

    float[] getHpPerNature();

    float[] getMovementSpeedMultiplier();
}
