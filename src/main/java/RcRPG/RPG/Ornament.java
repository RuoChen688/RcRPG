package RcRPG.RPG;

import RcRPG.AttrManager.ItemAttr;
import RcRPG.RcRPGMain;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Config;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Ornament extends ItemAttr {

    private Config config;

    private String name;

    /**
     * -- SETTER --
     * 仅作为属性分类的标识
     *
     * @param label
     */
    @Setter
    private String label;

    /**
     * -- GETTER --
     * 物品名，替代源label用法
     *
     * @return
     */
    @Getter
    private String showName;

    private Item item;

    private int level;

    @Getter
    private Map<String, Object> attr;

    private String tipText;

    private String myMessage;

    private String serverMessage;

    private String message;

    private int effectiveSlot;

    private ArrayList<String> loreList = new ArrayList<>();
    /**
     * 套装方案
     */
    private List<String> suit = new ArrayList<>();

    public Ornament(String name, Config config) {
        this.name = name;
        this.config = config;
    }

    public static Ornament loadOrnament(String name, Config config) {
        try {
            Ornament ornament = new Ornament(name, config);

            ornament.setLabel(config.getString("标签"));
            ornament.setShowName(config.getString("显示名称"));
            ornament.setItem(Item.fromString(config.getString("物品ID")));
            ornament.setLevel(config.getInt("最低使用等级"));

            if (config.exists("属性")) {
                ornament.setAttr((Map<String, Object>) config.get("属性"));
            }

            ornament.setMessage(config.getString("介绍", ""));
            ornament.setShowName(config.getString("显示名称"));

            ornament.setLoreList(new ArrayList<>(config.getStringList("显示")));

            if (config.exists("套装")) {
                List<String> suitList;
                if (config.isList("套装")) {
                    suitList = config.getStringList("套装");
                } else {
                    suitList = new ArrayList<>(Arrays.asList(config.getString("套装", "").split(",")));
                }
                ornament.setSuit(suitList);
            }

            ornament.setTipText(config.getString("底部显示"));
            ornament.setMyMessage(config.getString("个人通知"));
            ornament.setServerMessage(config.getString("全服通知"));
            ornament.setEffectiveSlot(config.getInt("生效槽", -1));
            return ornament;
        } catch (Exception e) {
            RcRPGMain.getInstance().getLogger().error("加载饰品" + name + "配置文件失败");
            return null;
        }
    }

    public static Config getOrnamentConfig(String name) {
        File file = new File(RcRPGMain.getInstance().getDataFolder() + "/Ornament/" + name + ".yml");
        Config config;
        if (file.exists()) {
            config = new Config(file, Config.YAML);
        } else {
            return null;
        }
        return config;
    }

    public static Config addOrnamentConfig(String name, String id) {
        if (getOrnamentConfig(name) == null) {
            RcRPGMain.getInstance().saveResource("Ornament/Ornament.yml", "/Ornament/" + name + ".yml", false);
            Config config = new Config(RcRPGMain.getInstance().getOrnamentFile() + File.separator + name + ".yml");
            config.set("物品ID", id);
            config.save();
            return config;
        }
        return null;
    }

    public static boolean delOrnamentConfig(String name) {
        if (getOrnamentConfig(name) != null) {
            File file = new File(RcRPGMain.getInstance().getOrnamentFile(), File.separator + name + ".yml");
            file.delete();
            return true;
        }
        return false;
    }

    public static Item getItem(String name, int count) {
        Ornament ornament = RcRPGMain.loadOrnament.get(name);
        if (ornament == null) return Item.AIR_ITEM;
        Item item = ornament.getItem();
        item.setCount(count);
        CompoundTag tag = item.hasCompoundTag() ? item.getNamedTag() : new CompoundTag();
        tag.putString("type", "ornament");
        tag.putString("name", name);
        tag.putByte("Unbreakable", 1);

        item.setNamedTag(tag);
        item.setCustomName(ornament.getShowName());
        Ornament.setOrnamentLore(item);
        return item;
    }

    public static boolean giveOrnament(Player player, String name, int count) {
        if (!RcRPGMain.loadOrnament.containsKey(name)) {
            return false;
        }
        Ornament ornament = RcRPGMain.loadOrnament.get(name);
        Item item = getItem(name, count);
        if (item.isNull()) {
            return false;
        }
        player.getInventory().addItem(item);
        if (!ornament.getMyMessage().isEmpty()) {
            String text = ornament.getMyMessage();
            if (text.contains("@player")) text = text.replace("@player", player.getName());
            if (text.contains("@item")) text = text.replace("@item", ornament.getLabel());
            player.sendMessage(text);
        }
        if (!ornament.getServerMessage().isEmpty()) {
            String text = ornament.getServerMessage();
            if (text.contains("@player")) text = text.replace("@player", player.getName());
            if (text.contains("@item")) text = text.replace("@item", ornament.getLabel());
            RcRPGMain.getInstance().getServer().broadcastMessage(text);
        }
        return true;
    }

    public static boolean isOrnament(Item item) {
        if (item.getNamedTag() == null) {
            return false;
        }
        if (!item.getNamedTag().contains("type")) {
            return false;
        }
        return item.getNamedTag().getString("type").equals("ornament");
    }

    public static Item setOrnamentLore(Item item) {
        if (Ornament.isOrnament(item)) {
            Ornament ornament = RcRPGMain.loadOrnament.get(item.getNamedTag().getString("name"));
            ArrayList<String> lore;
            lore = (ArrayList<String>) ornament.getLoreList().clone();
            for (int i = 0; i < lore.size(); i++) {
                String s = lore.get(i);
                if (s.contains("@message")) s = s.replace("@message", ornament.getMessage());
                s = ornament.replaceAttrTemplate(s);// 替换属性的值
                lore.set(i, s);
            }
            item.setLore(lore.toArray(new String[0]));
        }
        return item;
    }

    public void setAttr(Map<String, Object> attr) {
        this.attr = attr;
        setItemAttrConfig(attr);
    }

    public boolean isValidSlot(int slot) {
        if (this.effectiveSlot == -1) return true;
        if (this.effectiveSlot == slot) return true;
        return false;
    }

}
