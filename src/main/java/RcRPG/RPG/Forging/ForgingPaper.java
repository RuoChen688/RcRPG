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
public class ForgingPaper {

    private Config config;

    private String name;

    private String label;

    private String showName;

    private Item item;

    private String message;

    private ArrayList<String> loreList = new ArrayList<>();

    public static ForgingPaper loadForgingPaper(String name, Config config) {
        try {
            ForgingPaper forgingPaper = new ForgingPaper(name, config);
            forgingPaper.setLabel(config.getString("标签"));
            forgingPaper.setShowName(config.getString("显示名称"));
            forgingPaper.setItem(Item.fromString(config.getString("物品ID")));
            return forgingPaper;
        } catch (Exception e) {
            RcRPGMain.getInstance().getLogger().error("加载锻造图 " + name + " 配置文件失败");
            return null;
        }
    }

    public ForgingPaper(String name, Config config) {
        this.name = name;
        this.config = config;
    }

    public static Item getItem(String name, int count) {
        ForgingPaper forgingPaper = RcRPGMain.loadForgingPaper.get(name);
        Item item = forgingPaper.getItem();
        item.setCount(count);
        CompoundTag tag = item.getNamedTag();
        if (tag == null) {
            tag = new CompoundTag();
        }
        tag.putString("type", "forgingPaper");
        tag.putString("name", name);

        item.setNamedTag(tag);
        item.setCustomName(forgingPaper.getShowName());
        ForgingPaper.setForgingPaperLore(item);
        return item;
    }

    public static Item setForgingPaperLore(Item item) {
        if (ForgingPaper.isForgingPaper(item)) {
            ForgingPaper forgingPaper = RcRPGMain.loadForgingPaper.get(item.getNamedTag().getString("name"));
            ArrayList<String> lore;
            lore = (ArrayList<String>) forgingPaper.getLoreList().clone();
            for (int i = 0; i < lore.size(); i++) {
                String s = lore.get(i);
                if (s.contains("@message")) s = s.replace("@message", forgingPaper.getMessage());
                lore.set(i, s);
            }
            item.setLore(lore.toArray(new String[0]));
        }
        return item;
    }

    public static boolean isForgingPaper(Item item) {
        if (item.getNamedTag() == null) {
            return false;
        }
        if (!item.getNamedTag().contains("type")) {
            return false;
        }
        return item.getNamedTag().getString("type").equals("forgingPaper");
    }
}
