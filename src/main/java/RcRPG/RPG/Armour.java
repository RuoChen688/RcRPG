package RcRPG.RPG;

import RcRPG.AttrManager.ItemAttr;
import RcRPG.Handle;
import RcRPG.RcRPGMain;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.lang.LangCode;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.StringTag;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

@Getter
@Setter
public class Armour extends ItemAttr implements Cloneable {

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

    private int health;

    private int damage;

    private int reDamage;

    private ArrayList<Effect> effects = new ArrayList<>();

    @Getter
    private Map<String, Object> attr;

    private int stone;

    private ColorRGB color;

    /**
     * 分解方案
     */
    private String dismantle;
    /**
     * 套装方案
     */
    private List<String> suit = new ArrayList<>();

    private String tipText;

    private String myMessage;

    private String serverMessage;

    private String message;

    private ArrayList<String> stoneList = new ArrayList<>();

    private ArrayList<String> loreList = new ArrayList<>();

    public Armour(String name, Config config) {
        this.name = name;
        this.config = config;
    }

    public Armour initArmour() {
        this.setLabel(config.getString("标签"));
        this.setShowName(config.getString("显示名称"));
        this.setItem(Item.fromString(config.getString("物品ID")));
        if (config.exists("属性")) {
            this.setAttr((Map<String, Object>) config.get("属性"));
        }
        this.setMessage(config.getString("介绍", ""));

        this.setColor(loadColorFromConfig(config));

        this.setDismantle(config.getString("分解", ""));

        if (config.exists("套装")) {
            List<String> suitList;
            if (config.isList("套装")) {
                suitList = config.getStringList("套装");
            } else {
                suitList = new ArrayList<>(Arrays.asList(config.getString("套装", "").split(",")));
            }
            this.setSuit(suitList);
        }

        this.setTipText(config.getString("底部显示"));
        this.setMyMessage(config.getString("个人通知"));
        this.setServerMessage(config.getString("全服通知"));
        this.setStone(config.getInt("宝石孔数"));

        this.setEffects(loadEffectsFromConfig(config));

        ArrayList<String> loreList = new ArrayList<>(config.getStringList("显示"));
        this.setLoreList(loreList);
        ArrayList<String> stoneList = new ArrayList<>(config.getStringList("宝石槽"));
        this.setStoneList(stoneList);

        return this;
    }

    @Override
    public Armour clone() {
        return new Armour(this.name, this.config).initArmour();
    }

    public static Armour loadArmour(String name, Config config) {
        try {

            return new Armour(name, config).initArmour();
        } catch (Exception e) {
            e.printStackTrace();
            RcRPGMain.getInstance().getLogger().error("加载盔甲" + name + "配置文件失败");
            return null;
        }
    }

    @NotNull
    private static ColorRGB loadColorFromConfig(Config config) {
        if (!config.exists("染色")) {
            return new ColorRGB(-1, -1, -1);
        }
        ConfigSection rgb = config.getSection("染色");

        return new ColorRGB(rgb.getInt("r", -1), rgb.getInt("g", -1), rgb.getInt("b", -1));
    }

    @NotNull
    private static ArrayList<Effect> loadEffectsFromConfig(Config config) {
        ArrayList<Effect> effects = new ArrayList<>();
        if (!config.exists("药水效果")) {
            return effects;
        }
        for (String effect : config.getStringList("药水效果")) {
            String[] parts = effect.split(":");
            if (parts.length == 3) {
                effects.add(Effect.getEffect(Integer.parseInt(parts[0]))
                        .setAmplifier(Integer.parseInt(parts[1]) - 1)
                        .setDuration(Integer.parseInt(parts[2]) * 20));
            }
        }
        return effects;
    }


    public static Config getArmourConfig(String name) {
        File file = new File(RcRPGMain.getInstance().getDataFolder() + "/Armour/" + name + ".yml");
        Config config;
        if (file.exists()) {
            config = new Config(file, Config.YAML);
        } else {
            return null;
        }
        return config;
    }

    public static Config addArmourConfig(String name, String id) {
        if (getArmourConfig(name) == null) {
            RcRPGMain.getInstance().saveResource("Armour/Armour.yml", "/Armour/" + name + ".yml", false);
            Config config = new Config(RcRPGMain.getInstance().getArmourFile() + "/" + name + ".yml");
            config.set("物品ID", id);
            config.save();
            return config;
        }
        return null;
    }

    public static boolean delArmourConfig(String name) {
        if (getArmourConfig(name) == null) return false;
        File file = new File(RcRPGMain.getInstance().getArmourFile(), name + ".yml");
        file.delete();
        return true;
    }

    public static Item getItem(String name, int count, LangCode langCode) {
        Armour armour = RcRPGMain.loadArmour.get(name);
        Item item = armour.getItem().clone();
        item.setCount(count);
        CompoundTag tag = item.hasCompoundTag() ? item.getNamedTag() : new CompoundTag();
        tag.putString("type", "armour");
        tag.putString("name", name);
        tag.putByte("Unbreakable", 1);

        if (!armour.getColor().isEmpty()) {
            tag.putInt("customColor", armour.getColor().getRgb());
        }

        item.setNamedTag(tag);
        item.setCustomName(armour.getShowName());
        Armour.setArmourLore(item, langCode);
        return item;

    }

    public static Item getItem(String name, int count) {
        return getItem(name, count, LangCode.en_US);
    }

    public static boolean giveArmour(Player player, String name, int count) {
        if (!RcRPGMain.loadArmour.containsKey(name)) {
            return false;
        }
        Armour armour = RcRPGMain.loadArmour.get(name);
        Item item = getItem(name, count);
        if (item.isNull()) {
            return false;
        }
        player.getInventory().addItem(item);
        if (!armour.getMyMessage().isEmpty()) {
            String text = armour.getMyMessage();
            if (text.contains("@player")) text = text.replace("@player", player.getName());
            if (text.contains("@item")) text = text.replace("@item", armour.getLabel());
            player.sendMessage(text);
        }
        if (!armour.getServerMessage().isEmpty()) {
            String text = armour.getServerMessage();
            if (text.contains("@player")) text = text.replace("@player", player.getName());
            if (text.contains("@item")) text = text.replace("@item", armour.getLabel());
            RcRPGMain.getInstance().getServer().broadcastMessage(text);
        }
        return true;
    }

    public static boolean isArmour(Item item) {
        if (item.getNamedTag() == null) {
            return false;
        }
        if (!item.getNamedTag().contains("type")) {
            return false;
        }
        return item.getNamedTag().getString("type").equals("armour");
    }

    public static LinkedList<Stone> getStones(Item item) {
        LinkedList<Stone> list = new LinkedList<>();
        if (!isArmour(item) || item.getNamedTag() == null) return list;
        ListTag<StringTag> tags = item.getNamedTag().getList("stone", StringTag.class);
        for (StringTag tag : tags.getAll()) {
            list.add(Handle.getStoneViaName(tag.parseValue()));
        }
        Armour armour = RcRPGMain.loadArmour.get(item.getNamedTag().getString("name"));
        while (list.size() < armour.getStone()) {
            list.add(null);
        }
        return list;
    }

    public static int getStoneSize(Item item) {
        LinkedList<Stone> list = Armour.getStones(item);
        int i = 0;
        for (Stone stone : list) {
            if (stone != null) i++;
        }
        return i;
    }

    @Deprecated
    public static boolean canInlay(Item item) {
        if (Armour.isArmour(item)) {
            Armour armour = RcRPGMain.loadArmour.get(item.getNamedTag().getString("name"));
            return Armour.getStoneSize(item) < armour.getStone();
        } else {
            return false;
        }
    }

    public static void setStone(Player player, Item item, LinkedList<Stone> list) {
        ListTag<StringTag> stoneList = new ListTag<>("stone");
        for (Stone stone : list) {
            if (stone == null) {
                stoneList.add(new StringTag("", ""));
                continue;
            }
            stoneList.add(new StringTag(stone.getName(), stone.getName()));
        }
        CompoundTag tag = item.getNamedTag();
        tag.putList(stoneList);
        item.setNamedTag(tag);
        player.getInventory().setItemInHand(Armour.setArmourLore(item, player.getLanguageCode()));
    }

    public static int getStoneHealth(Item item) {
        if (Armour.isArmour(item)) {
            LinkedList<Stone> list = Armour.getStones(item);
            int damage = 0;
            for (Stone stone : list) {
                if (stone == null) continue;
                damage += stone.getItemAttr("血量值");
            }
            return damage;
        }
        return 0;
    }

    public static int getStoneDamage(Item item) {
        if (Armour.isArmour(item)) {
            LinkedList<Stone> list = Armour.getStones(item);
            int damage = 0;
            for (Stone stone : list) {
                if (stone == null) continue;
                damage += stone.getItemAttr("PVE攻击力");
            }
            return damage;
        }
        return 0;
    }

    public static int getStoneReDamage(Item item) {
        if (Armour.isArmour(item)) {
            LinkedList<Stone> list = Armour.getStones(item);
            int damage = 0;
            for (Stone stone : list) {
                if (stone == null) continue;
                damage += stone.getItemAttr("防御力");
            }
            return damage;
        }
        return 0;
    }

    public static Item setArmourLore(Item item, LangCode langCode) {
        if (Armour.isArmour(item)) {
            Armour armour;

            boolean isForgingItem = ForgingItem.isForgingItem(item);
            if (isForgingItem) {
                // 锻造物品属性处理
                armour = RcRPGMain.loadArmour.get(item.getNamedTag().getString("name")).clone();
                armour.mainAttr = new HashMap<>();

                for (Map.Entry<String, float[]> entry : ForgingItem.fromNBT(item.getNamedTag().getCompound("attr")).entrySet()) {
                    armour.mainAttr.put(entry.getKey(), entry.getValue());
                }
            } else {
                // 非锻造物品不使用深拷贝
                armour = RcRPGMain.loadArmour.get(item.getNamedTag().getString("name"));
            }

            ArrayList<String> lore = (ArrayList<String>) armour.getLoreList().clone();
            for (int i = 0; i < lore.size(); i++) {
                String s = lore.get(i);
                s = s.replace("@message", armour.getMessage());
                s = s.replace("@stoneHealth", String.valueOf(Armour.getStoneHealth(item)));
                s = s.replace("@stoneDamage", String.valueOf(Armour.getStoneDamage(item)));
                s = s.replace("@stoneReDamage", String.valueOf(Armour.getStoneReDamage(item)));
                s = s.replace("@gemLore", RcRPGMain.getInstance().getGemTemplateConfig().getTemplateText(langCode, item.getNamedTag(), armour.getStone(), armour.getStoneList()));

                // 替换属性的值
                if (isForgingItem) {
                    s = s.replace("@attrLore", armour.attrInfo(langCode));
                } else {
                    s = armour.replaceAttrTemplate(s);
                }

                lore.set(i, s);
            }
            item.setLore(lore.toArray(new String[0]));
        }
        return item;
    }

    public static Item setArmourLore(Item item) {
        return setArmourLore(item, LangCode.en_US);
    }

    public void setAttr(Map<String, Object> attr) {
        this.attr = attr;
        setItemAttrConfig(attr);
    }

    @Getter
    @Setter
    public static class ColorRGB {
        private int rgb;

        public ColorRGB(int r, int g, int b) {
            this.setRgb((r << 16) | (g << 8) | b);
        }

        public boolean isEmpty() {
            return getRgb() == -1;
        }
    }

}
