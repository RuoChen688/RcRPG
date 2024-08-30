package RcRPG.AttrManager;

import RcRPG.RcRPGMain;

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
}
