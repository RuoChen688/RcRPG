package RcRPG.RPG.Forging;

import RcRPG.RcRPGMain;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ForgingStone {

    private Config config;

    private String name;

    private String label;

    private String showName;

    private Item item;

    private String message;

    private ArrayList<String> loreList = new ArrayList<>();

    public static ForgingStone loadForgingStone(String name, Config config) {
        try {
            ForgingStone forgingStone = new ForgingStone(name, config);
            forgingStone.setLabel(config.getString("标签"));
            forgingStone.setShowName(config.getString("显示名称"));
            forgingStone.setItem(Item.fromString(config.getString("物品ID")));
            return forgingStone;
        } catch (Exception e) {
            RcRPGMain.getInstance().getLogger().error("加载锻造图 " + name + " 配置文件失败");
            return null;
        }
    }

    public ForgingStone(String name, Config config) {
        this.name = name;
        this.config = config;
    }

    public static Item getItem(String name, int count) {
        ForgingStone forgingStone = RcRPGMain.loadForgingStone.get(name);
        Item item = forgingStone.getItem();
        item.setCount(count);
        CompoundTag tag = item.getNamedTag();
        if (tag == null) {
            tag = new CompoundTag();
        }
        tag.putString("type", "forgingStone");
        tag.putString("name", name);

        item.setNamedTag(tag);
        item.setCustomName(forgingStone.getShowName());
        ForgingStone.setForgingStoneLore(item);
        return item;
    }

    public static Item setForgingStoneLore(Item item) {
        if (ForgingStone.isForgingStone(item)) {
            ForgingStone forgingStone = RcRPGMain.loadForgingStone.get(item.getNamedTag().getString("name"));
            ArrayList<String> lore;
            lore = (ArrayList<String>) forgingStone.getLoreList().clone();
            for (int i = 0; i < lore.size(); i++) {
                String s = lore.get(i);
                if (s.contains("@message")) s = s.replace("@message", forgingStone.getMessage());
                lore.set(i, s);
            }
            item.setLore(lore.toArray(new String[0]));
        }
        return item;
    }

    public static boolean isForgingStone(Item item) {
        if (item.getNamedTag() == null) {
            return false;
        }
        if (!item.getNamedTag().contains("type")) {
            return false;
        }
        return item.getNamedTag().getString("type").equals("forgingStone");
    }
}
