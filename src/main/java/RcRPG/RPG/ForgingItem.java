package RcRPG.RPG;

import RcRPG.AttrManager.FootageAttr;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;

import java.util.Map;

public class ForgingItem {
    public static boolean isForgingItem(Item item) {
        if (item.getNamedTag() == null) {
            return false;
        }
        return item.getNamedTag().containsCompound("attr");
    }

    public static Map<String, float[]> fromNBT(CompoundTag compoundTag) {
        return FootageAttr.fromNBT(compoundTag);
    }
}
