package RcRPG.AttrManager;

import RcRPG.RcRPGMain;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FootageAttr extends Manager {

    /**
     * 属性结构
     * {
     * "Main": {
     * "攻击力": [1,3]
     * }
     * }
     */
    public Map<String, Map<String, float[]>> myAttr = new HashMap<>();

    public void setItemAttrConfig(String id, Map<String, Object> newAttr) {
        Map<String, float[]> attrMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : newAttr.entrySet()) {
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

    /**
     * 将属性转换为 NBT 格式
     * 此方法用于序列化对象的属性，以便可以将其保存至物品
     *
     * @return 返回包含所有属性的 CompoundTag，用于保存至物品
     */
    public CompoundTag toNBT() {
        // 创建一个根CompoundTag来存储所有属性
        CompoundTag nbt = new CompoundTag();

        // 遍历属性映射，将每个属性转换为NBT格式
        for (Map.Entry<String, Map<String, float[]>> entry : myAttr.entrySet()) {
            // 获取当前条目的属性ID
            String id = entry.getKey();
            // 获取当前条目的属性映射
            Map<String, float[]> attrMap = entry.getValue();

            // 为当前属性创建一个CompoundTag
            CompoundTag attrTag = new CompoundTag();
            // 遍历属性映射，将每个属性值转换为ListTag并添加到attrTag
            for (Map.Entry<String, float[]> attrEntry : attrMap.entrySet()) {
                // 获取当前属性的键
                String attrKey = attrEntry.getKey();
                // 获取当前属性的值，是一个浮点数数组
                float[] values = attrEntry.getValue();

                // 创建一个ListTag来存储属性值
                ListTag<FloatTag> valueList = new ListTag<>();
                // 将属性值添加到ListTag中
                valueList.add(new FloatTag("", values[0]));
                valueList.add(new FloatTag("", values[1]));

                // 将ListTag添加到attrTag中
                attrTag.putList(attrKey, valueList);
            }

            // 将attrTag添加到根CompoundTag中
            nbt.putCompound(id, attrTag);
        }

        // 返回包含所有属性的CompoundTag
        return nbt;
    }
}
