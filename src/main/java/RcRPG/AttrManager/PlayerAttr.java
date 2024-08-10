package RcRPG.AttrManager;

import RcRPG.RPG.*;
import RcRPG.RcRPGMain;
import RcRPG.config.MainConfig;
import RcRPG.panel.container.ornament.OrnamentPanel;
import cn.nukkit.Player;
import cn.nukkit.form.element.Element;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.item.Item;

import java.text.DecimalFormat;
import java.util.*;

public class PlayerAttr extends Manager {

    private final Player player;
    private ArrayList<String> labelList = new ArrayList<>();

    public PlayerAttr(Player player) {
        this.player = player;
        myAttr.put("Main", new HashMap<>());
        myAttr.put("Base", new HashMap<>() {{
            put("SP", new float[]{0f, 0f});// 最大蓝量、蓝量
            put("Absorption", new float[]{0f, 0f});// 持续时间、盾量
        }});
    }

    public static LinkedHashMap<Player, PlayerAttr> playerlist = new LinkedHashMap<>();

    public static PlayerAttr getPlayerAttr(Player player) {
        if (!playerlist.containsKey(player)) {
            return null;
        }
        return playerlist.get(player);
    }

    public static void setPlayerAttr(Player player) {
        playerlist.put(player, new PlayerAttr(player));
    }

    /**
     * 用来存效果的持续时间
     */
    public HashMap<String, Long> effectDuration = new LinkedHashMap<>() {{
        put("Absorption", 0L);
    }};

    /**
     * 获取 Base SP 的值
     *
     * @return 浮点数数组，表示与指定属性名相关联的属性值。
     */
    public float[] getBaseSPAttr() {
        return myAttr.get("Base").getOrDefault("Absorption", new float[]{0f, 0f});
    }

    /**
     * 设置 Base SP 的值
     * @param value 浮点数数组，属性值。
     * @param max 整数，生效时间（秒）
     */
    public void setBaseSPAttr(float value, int max) {
        myAttr.get("Base").put("SP", new float[]{max, value});
    }

    /**
     * 获取 Base Absorption 的值
     * @return 浮点数数组，表示与指定属性名相关联的属性值。
     */
    public float[] getBaseAbsorptionAttr() {
        return myAttr.get("Base").getOrDefault("SP", new float[]{0f, 0f});
    }

    /**
     * 设置 Base Absorption 的值
     * @param value 浮点数数组，属性值。
     * @param duration 整数，生效时间（秒）
     */
    public void setBaseAbsorptionAttr(float value, int duration) {
        myAttr.get("Base").put("Absorption", new float[]{duration, value});
        player.setAbsorption(value);
        long currentTimestampInSeconds = System.currentTimeMillis() / 1000;
        effectDuration.put("Absorption", currentTimestampInSeconds + duration);
    }

    public void setEffectAttr(String flag, String attrName, float value, int duration) {
        String keyId = "Effect-"+flag;
        if (myAttr.containsKey(keyId)) {
            Map<String, float[]> attr = deepCopyMap(myAttr.get(keyId));
            attr.put(attrName, new float[]{value, value});
            setItemAttrConfig(keyId, attr);
        } else {
            Map<String, float[]> attr = new HashMap<>();
            attr.put(attrName, new float[]{value, value});
            setItemAttrConfig(keyId, attr);
        }
        long currentTimestampInSeconds = System.currentTimeMillis() / 1000;
        effectDuration.put(flag, currentTimestampInSeconds + duration);
    }

    public void update() {
        // 等级加点
        if (!MainConfig.getHpIncreasePerLevel().isEmpty()) {
            String[] s = MainConfig.getHpIncreasePerLevel().split(":");
            int lvAddHealth;
            if (Level.enable) {
                lvAddHealth = Level.getLevel(player) / Integer.parseInt(s[0]) * Integer.parseInt(s[1]);
            } else {
                lvAddHealth = player.getExperienceLevel() / Integer.parseInt(s[0]) * Integer.parseInt(s[1]);
            }
            if (lvAddHealth > 0) {
                PlayerAttr pAttr = PlayerAttr.getPlayerAttr(player);
                Map<String, float[]> attr = new HashMap<>();
                attr.put("血量值", new float[]{lvAddHealth, lvAddHealth});
                if (pAttr != null) {
                    pAttr.setItemAttrConfig(RcRPGMain.getI18n().tr(player.getLanguageCode(), "rcrpg.playerattr.lv"), attr);
                }
            }
        }

        ArrayList<String> beforeLabel = new ArrayList<>(labelList);
        labelList.clear();

        Map<String, Integer> suitMap = new HashMap<>();// _声明套装Map

        ArrayList<Item> itemList = new ArrayList<>();
        // 主手
        itemList.add(player.getInventory().getItemInHand());
        // 副手
        itemList.add(player.getOffhandInventory().getItem(0));
        for (Item rcItem : itemList) {
            if (rcItem.getNamedTag() == null) continue;
            Weapon weapon = RcRPGMain.loadWeapon.get(rcItem.getNamedTag().getString("name"));
            if (weapon == null) continue;

            if (!weapon.getSuit().isEmpty()) {// 套装
                weapon.getSuit().forEach(v -> {
                    int count = suitMap.getOrDefault(v, 0) + 1;
                    suitMap.put(v, count);
                });
            }

            setItemAttrConfig(weapon.getLabel(), weapon.getMainAttr());
            checkItemStoneAttr(weapon.getLabel(), Weapon.getStones(rcItem), beforeLabel, labelList);

            beforeLabel.remove(weapon.getLabel());
            labelList.add(weapon.getLabel());
        }
        // 护甲栏
        for (Item rcItem : player.getInventory().getArmorContents()) {
            if (rcItem.getNamedTag() == null) continue;
            Armour armour = RcRPGMain.loadArmour.get(rcItem.getNamedTag().getString("name"));
            if (armour == null) continue;
            setItemAttrConfig(armour.getLabel(), armour.getMainAttr());
            checkItemStoneAttr(armour.getLabel(), Armour.getStones(rcItem), beforeLabel, labelList);

            if (!armour.getSuit().isEmpty()) {// 套装
                armour.getSuit().forEach(v -> {
                    int count = suitMap.getOrDefault(v, 0) + 1;
                    suitMap.put(v, count);
                });
            }

            beforeLabel.remove(armour.getLabel());
            labelList.add(armour.getLabel());
        }
        // 饰品
        Map<Integer, Item> map = OrnamentPanel.getPanel(player);
        if (!map.isEmpty()) {
            Map<String, float[]> attr = new HashMap<>();
            for (int i = 0; i < Math.min(MainConfig.getOrnamentEffectSlots(), map.size()); i++) {
                if (!map.get(i).hasCompoundTag()) continue;
                Ornament ornament = RcRPGMain.loadOrnament.get(map.get(i).getNamedTag().getString("name"));
                if (ornament == null) continue;
                if (!ornament.isValidSlot(i)) continue;
                OverAttr(attr, ornament.getMainAttr());
                setItemAttrConfig(ornament.getLabel(), attr);

                if (!ornament.getSuit().isEmpty()) {// 套装
                    ornament.getSuit().forEach(v -> {
                        int count = suitMap.getOrDefault(v, 0) + 1;
                        suitMap.put(v, count);
                    });
                }

                beforeLabel.remove(ornament.getLabel());
                labelList.add(ornament.getLabel());
            }
        }

        // 套装
        suitMap.keySet().forEach(name -> {
            int count = suitMap.get(name);
            ItemAttr suitAttr = Suit.getSuitAttr(name, count);
            if (suitAttr == null) return;
            String label = RcRPGMain.getI18n().tr(player.getLanguageCode(), "rcrpg.playerattr.suit.label", name, count);
            setItemAttrConfig(label, suitAttr.getMainAttr());
            if (!beforeLabel.contains(label)) {
                player.sendActionBar(RcRPGMain.getI18n().tr(player.getLanguageCode(), "rcrpg.playerattr.set_suit_message", name, count));
            }
            beforeLabel.remove(label);
            labelList.add(label);
        });

        beforeLabel.forEach(label -> {
            setItemAttrConfig(label, new HashMap<String, float[]>());
        });
    }

    public void OverAttr(Map<String, float[]> map1, Map<String, float[]> map2) {
        for (Map.Entry<String, float[]> entry : map2.entrySet()) {
            String key = entry.getKey();
            float[] value = entry.getValue();
            if (value.length == 1) {
                value = new float[]{value[0], value[0]};
            }
            if (!map1.containsKey(key)) {
                map1.put(key, value);
            } else {
                map1.put(key, new float[]{map1.get(key)[0] + value[0], map1.get(key)[1] + value[1]});
            }
        }
    }

    public void checkItemStoneAttr(String mainItemName, LinkedList<Stone> list, ArrayList<String> beforLabel, ArrayList<String> labelList) {
        LinkedHashMap<String, Map<String, float[]>> map = new LinkedHashMap<>();
        Map<String, float[]> attr = new HashMap<>();
        for (Stone stone : list) {
            if (stone == null) continue;
            if (!map.containsKey(stone.getLabel())) {
                attr.clear();
                OverAttr(attr, stone.getMainAttr());
                setItemAttrConfig(mainItemName + " -> " + stone.getLabel(), attr);
                map.put(stone.getLabel(), attr);
            } else {
                OverAttr(map.get(stone.getLabel()), stone.getMainAttr());
                setItemAttrConfig(mainItemName + " -> " + stone.getLabel(), map.get(stone.getLabel()));
            }
            beforLabel.remove(mainItemName + " -> " + stone.getLabel());
            labelList.add(mainItemName + " -> " + stone.getLabel());
        }
    }

    /**
     * 属性结构
     * {
     * "Main": {
     * "攻击力": [1,3]
     * }
     * }
     */
    public Map<String, Map<String, float[]>> myAttr = new HashMap<>();

    public void setItemAttrConfig(String id, Object newAttr) {
        Map<String, float[]> attrMap = new HashMap<>();
        Map<String, Object> attr = (Map<String, Object>) newAttr;
        for (Map.Entry<String, Object> entry : attr.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof List<?> values) {
                float[] floatValue = new float[values.size()];
                for (int i = 0; i < values.size(); i++) {
                    if (values.get(i) instanceof Double) {
                        floatValue[i] = ((Double) values.get(i)).floatValue();
                    } else if (values.get(i) instanceof Integer) {
                        floatValue[i] = ((Integer) values.get(i)).floatValue();
                    }
                }
                if (floatValue.length < 2) {
                    floatValue = new float[]{floatValue[0], floatValue[0]};
                }
                attrMap.put(key, floatValue);
            } else if (value instanceof float[] floatValue) {
                if (floatValue.length < 2) {
                    floatValue = new float[]{floatValue[0], floatValue[0]};
                }
                attrMap.put(key, floatValue);
            } else {
                RcRPGMain.getInstance().getLogger().warning(key + "不知道是啥类型");
            }
        }

        Map<String, float[]> mainAttrMap = myAttr.get("Main");
        Map<String, float[]> oldAttrMap = deepCopyMap(myAttr.get(id));
        myAttr.put(id, attrMap);
        // 处理newAttr属性
        for (Map.Entry<String, float[]> entry : attrMap.entrySet()) {
            String key = entry.getKey();
            float[] mainValues = new float[]{0.0f, 0.0f};
            if (mainAttrMap.containsKey(key)) {
                mainValues = mainAttrMap.get(key);
            }
            if (mainValues.length < 2) {
                mainValues = new float[]{mainValues[0], mainValues[0]};
            }

            float[] values = attrMap.get(key);
            mainValues[0] = mainValues[0] - (oldAttrMap.containsKey(key) ? oldAttrMap.get(key)[0] : 0) + values[0];
            mainValues[1] = mainValues[1] - (oldAttrMap.containsKey(key) ? oldAttrMap.get(key)[1] : 0) + values[1];

            mainAttrMap.put(key, mainValues);
        }

        // 副作用回收
        // 处理 oldAttr 有但是 newAttr 没有的属性
        for (Map.Entry<String, float[]> entry : oldAttrMap.entrySet()) {
            String key = entry.getKey();
            if (!attrMap.containsKey(key)) {
                float[] mainValues = mainAttrMap.get(key);
                float[] values = oldAttrMap.get(key);
                mainValues[0] = mainValues[0] - values[0];
                mainValues[1] = mainValues[1] - values[1];
                mainAttrMap.put(key, mainValues);
            }
        }
    }

    public float getItemAttr(String attrName) {
        Map<String, float[]> mainAttrMap = myAttr.get("Main");
        float[] data;
        if (mainAttrMap.containsKey(attrName)) {
            data = mainAttrMap.get(attrName);
        } else {
            data = new float[]{0, 0};
        }
        return getRandomNum(data);
    }

    /**
     * 获取指定属性的原始值
     *
     * @param attrName 属性名
     * @param index    索引，0为min，1为max。内部可能传入-1
     * @return 属性值
     */
    public float getItemAttr(String attrName, int index) {
        return getItemAttr("Main", attrName, index);
    }

    /**
     * 向 p 显示本属性窗口
     * @param p
     */
    public void showAttrWindow(Player p) {
        StringBuilder str = new StringBuilder();
        Map<String, Map<String, float[]>> data = this.myAttr;

        ArrayList<Element> list = new ArrayList<>();

        for (String i : data.get("Main").keySet()) {
            float[] value = data.get("Main").get(i);
            String valueString = valueToString(value, i);

            if (valueString.equals("0")) {
                continue;
            }

            str.append(" ").append(i).append(": ").append(valueString).append("\n");
        }
        list.add(new ElementLabel("§l§a### "+RcRPGMain.getI18n().tr(p.getLanguageCode(), "rcrpg.window.attr.info.text.totalAttr")+"§r\n" + str));

        for (String i : data.keySet()) {
            if (i.equals("Main") || i.equals("Base")) {
                continue;
            }

            str = new StringBuilder();

            for (String n : data.get(i).keySet()) {
                float[] value = data.get(i).get(n);
                String valueString = valueToString(value, n);

                if (valueString.equals("0")) {
                    continue;
                }

                str.append("  ").append(n).append(": ").append(valueString).append("\n");
            }

            if (!str.toString().isEmpty()) {// 如果没有属性就不显示了
                list.add(new ElementLabel(" §a# " + i + "§r\n" + str));
            }
        }

        /*
         str = "";
         long nowTime = (System.currentTimeMillis() / 1000);
         for (String i : data.get("Effect").keySet()) {
         float[] effectData = data.get("Effect").get(i);
         long time = (long) effectData[0];
         int level = (int) effectData[1];
         str += "  " + i + " (" + (time - nowTime) + "s): " + level + "\n";
         }

         if (!str.isEmpty()) {
         list.add(new ElementLabel(" §a# 临时效果§r\n" + str));
         }
         */

        FormWindowCustom win = new FormWindowCustom(RcRPGMain.getI18n().tr(p.getLanguageCode(), "rcrpg.window.attr.info.title", p.getName()), list);
        p.showFormWindow(win);
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("属性结构:\n");
        result.append("{\n");

        for (Map.Entry<String, Map<String, float[]>> entry : myAttr.entrySet()) {
            String outerKey = entry.getKey();
            Map<String, float[]> innerMapValue = entry.getValue();

            result.append("    \"").append(outerKey).append("\": {\n");

            for (Map.Entry<String, float[]> innerEntry : innerMapValue.entrySet()) {
                String innerKey = innerEntry.getKey();
                float[] innerArray = innerEntry.getValue();

                result.append("        \"").append(innerKey).append("\": ").append("[").append(innerArray[0]).append(", ").append(innerArray[1]).append("]\n");
            }

            result.append("    }\n");
        }
        result.append("}\n");

        return result.toString();
    }

    protected static String lessZero(float value) {
        String strValue = Float.toString(value);
        if (strValue.endsWith(".0")) {
            return Integer.toString((int) value);
        }
        return Float.toString(value);
    }

    /**
     * 将数据可视化，输入data,属性输出min-max或x%
     */
    public static String valueToString(float[] data, String attribute) {
        List<String> attrDisplayPercent = RcRPGMain.getInstance().attrDisplayPercentList;
        String back = "";
        if (data.length == 2) {
            if (data[0] == data[1]) {
                data = new float[]{data[0]};
            } else {
                return lessZero(data[0]) + " - " + lessZero(data[1]);
            }
        }
        if (attrDisplayPercent.contains(attribute)) {
            // 百分比的值
            DecimalFormat decimalFormat = new DecimalFormat("#.00");
            decimalFormat.setMinimumIntegerDigits(1); // 确保整数部分至少有一位数字
            String formattedData = decimalFormat.format(data[0] * 100);
            if (formattedData.endsWith(".00")) {
                back = (int) (data[0] * 100) + "%%";
            } else if (formattedData.endsWith("0")) {
                back = formattedData.substring(0, formattedData.length() - 1) + "%%";
            } else {
                back = formattedData + "%%";
            }
        } else {
            back = lessZero(data[0]);
        }
        if (data[0] == 0) {
            back = "0";
        }
        return back;
    }

    /**
     * 获取指定属性的原始值
     *
     * @param label    标签名
     * @param attrName 属性名
     * @param index    索引，0为min，1为max。内部可能传入-1
     * @return 属性值
     */
    public float getItemAttr(String label, String attrName, int index) {
        if (index == -1) {
            return getItemAttr(attrName);
        }
        Map<String, float[]> mainAttrMap = myAttr.get(label);
        float[] data;
        if (mainAttrMap.containsKey(attrName)) {
            data = mainAttrMap.get(attrName);
        } else {
            data = new float[]{0, 0};
        }
        assert index == 0 || index == 1 : "Index should be 0 or 1";
        return data[index];
    }

    public Map<String, float[]> getItemAttrMap() {
        return getItemAttrMap("Main");
    }

    public Map<String, float[]> getItemAttrMap(String label) {
        Map<String, float[]> data;
        data = myAttr.getOrDefault(label, null);
        return data;
    }

    //激进向 (9)
    @Override
    public float[] getPvpAttackPower() {
        if (getItemAttrMap().containsKey("PVP攻击力")) {
            return getItemAttrMap().get("PVP攻击力");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getPveAttackPower() {
        if (getItemAttrMap().containsKey("PVE攻击力")) {
            return getItemAttrMap().get("PVE攻击力");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getPvpAttackMultiplier() {
        if (getItemAttrMap().containsKey("PVP攻击加成")) {
            return getItemAttrMap().get("PVP攻击加成");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getPveAttackMultiplier() {
        if (getItemAttrMap().containsKey("PVE攻击加成")) {
            return getItemAttrMap().get("PVE攻击加成");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getCritChance() {
        if (getItemAttrMap().containsKey("暴击率")) {
            return getItemAttrMap().get("暴击率");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getCriticalStrikeMultiplier() {
        if (getItemAttrMap().containsKey("暴击倍率")) {
            return getItemAttrMap().get("暴击倍率");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getLifestealChance() {
        if (getItemAttrMap().containsKey("吸血率")) {
            return getItemAttrMap().get("吸血率");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getLifestealMultiplier() {
        if (getItemAttrMap().containsKey("吸血倍率")) {
            return getItemAttrMap().get("吸血倍率");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getDefensePenetrationChance() {
        if (getItemAttrMap().containsKey("破防率")) {
            return getItemAttrMap().get("破防率");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getDefensePenetrationValue() {
        if (getItemAttrMap().containsKey("破防攻击")) {
            return getItemAttrMap().get("破防攻击");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getArmorPenetrationChance() {
        if (getItemAttrMap().containsKey("破甲率")) {
            return getItemAttrMap().get("破甲率");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getArmorPenetrationValue() {
        if (getItemAttrMap().containsKey("破甲强度")) {
            return getItemAttrMap().get("破甲强度");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getHitChance() {
        if (getItemAttrMap().containsKey("命中率")) {
            return getItemAttrMap().get("命中率");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getDamageMultiplier() {
        if (getItemAttrMap().containsKey("伤害加成")) {
            return getItemAttrMap().get("伤害加成");
        }
        return new float[]{0.0f, 0.0f};
    }

    //保守向 (9)
    @Override
    public float[] getDodgeChance() {
        if (getItemAttrMap().containsKey("闪避率")) {
            return getItemAttrMap().get("闪避率");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getCritResistance() {
        if (getItemAttrMap().containsKey("暴击抵抗")) {
            return getItemAttrMap().get("暴击抵抗");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getLifestealResistance() {
        if (getItemAttrMap().containsKey("吸血抵抗")) {
            return getItemAttrMap().get("吸血抵抗");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getHp() {
        if (getItemAttrMap().containsKey("血量值")) {
            return getItemAttrMap().get("血量值");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getDefense() {
        if (getItemAttrMap().containsKey("防御力")) {
            return getItemAttrMap().get("防御力");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getMaxHpMultiplier() {
        if (getItemAttrMap().containsKey("血量加成")) {
            return getItemAttrMap().get("血量加成");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getDefenseMultiplier() {
        if (getItemAttrMap().containsKey("防御加成")) {
            return getItemAttrMap().get("防御加成");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getHpRegenMultiplier() {
        if (getItemAttrMap().containsKey("生命加成")) {
            return getItemAttrMap().get("生命加成");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getArmorStrengthMultiplier() {
        if (getItemAttrMap().containsKey("护甲强度")) {
            return getItemAttrMap().get("护甲强度");
        }
        return new float[]{0.0f, 0.0f};
    }

    //辅助增益向 (3)
    @Override
    public float[] getExperienceGainMultiplier() {
        if (getItemAttrMap().containsKey("经验加成")) {
            return getItemAttrMap().get("经验加成");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getHpPerSecond() {
        if (getItemAttrMap().containsKey("每秒恢复")) {
            return getItemAttrMap().get("每秒恢复");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getHpPerNature() {
        if (getItemAttrMap().containsKey("生命恢复")) {
            return getItemAttrMap().get("生命恢复");
        }
        return new float[]{0.0f, 0.0f};
    }

    @Override
    public float[] getMovementSpeedMultiplier() {
        if (getItemAttrMap().containsKey("移速加成")) {
            return getItemAttrMap().get("移速加成");
        }
        return new float[]{0.0f, 0.0f};
    }


}
