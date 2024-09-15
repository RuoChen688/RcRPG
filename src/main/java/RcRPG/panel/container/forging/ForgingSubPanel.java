package RcRPG.panel.container.forging;

import RcRPG.RPG.forging.ForgingPaper;
import RcRPG.RPG.forging.ForgingStone;
import RcRPG.RcRPGMain;
import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import me.iwareq.fakeinventories.FakeInventory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ForgingSubPanel implements InventoryHolder {
    public static int MAX_QUALITY = 5;
    public static Item AIR_PLACEHOLDER = Item.fromString("minecraft:barrier");

    public static Map<Integer, Item> getPanel(Player player) {
        Map<Integer, Item> panel = new LinkedHashMap<>();
        Item stoneTipItem = Item.fromString("minecraft:oak_sign");
        stoneTipItem.setCustomName("§r§7一一一 §e放入「原初之石」 §7一一一");
        stoneTipItem.setLore(
                "§r§f原初之石会确定锻造素材的容许品质",
                "§r§f同时也决定可放入的素材数量"
        );
        panel.put(0, stoneTipItem);
        for (int i = 1; i < 27; i++) {
            panel.put(i, AIR_PLACEHOLDER);
        }
        return panel;
    }

    public ForgingPaper forgingPaper;
    public ForgingStone forgingStone;
    public Item mainFootage = Item.AIR_ITEM;

    public ForgingSubPanel(ForgingPaper paper) {
        this.forgingPaper = paper;
        AIR_PLACEHOLDER.setCustomName("§r§7一一一 §c不可放入§7 一一一");
    }

    public Item forgingStoneItem = Item.AIR_ITEM;

    public void sendPanel(Player player) {
        ForgingSubInventory inv = new ForgingSubInventory("锻造 - 「" + forgingPaper.getShowName() + "」");
        inv.origin = forgingPaper.getOrigin();
        inv.setContents(getPanel(player));
        inv.setDefaultItemHandler((item, event) -> {
            boolean isNotAction = false;
            Item cursorItem = Item.AIR_ITEM;
            for (InventoryAction action : event.getTransaction().getActions()) {
                Item sourceItem = action.getSourceItem();
                Item targetItem = action.getTargetItem();
                if (action instanceof SlotChangeAction slotChange) {
                    if (!(slotChange.getInventory() instanceof FakeInventory)) {
                        continue;
                    }
                    cursorItem = targetItem.clone();
                    player.getCursorInventory().setItem(0, Item.AIR_ITEM);// 清空浮标物品（For Windows）
                    event.setCancelled();
                    player.getInventory().removeItem(targetItem);// 清理背包里的物品（For Mobile）

                    // 操作 - 回退
                    if (targetItem.isNull()) {
                        if (sourceItem.getNamedTag() == null || AIR_PLACEHOLDER.deepEquals(sourceItem)) break;
                        if (slotChange.getSlot() == 1) {// 回退原初之石
                            player.getInventory().addItem(sourceItem);

                            Item stoneTipItem = Item.fromString("minecraft:oak_sign");
                            stoneTipItem.setCustomName("§r§7一一一 §e放入「原初之石」 §7一一一");
                            stoneTipItem.setLore(
                                    "§r§f原初之石会确定锻造素材的容许品质",
                                    "§r§f同时也决定可放入的素材数量"
                            );
                            inv.setItem(0, stoneTipItem);

                            Item barrierItem = Item.fromString("minecraft:barrier");
                            barrierItem.setCustomName("§r§7一一一 §c不可放入§7 一一一");
                            inv.setItem(1, barrierItem);

                            // 清理主素材
                            if (!AIR_PLACEHOLDER.deepEquals(inv.getItemFast(2))) {
                                player.getInventory().addItem(inv.getItem(2));
                                inv.setItem(2, AIR_PLACEHOLDER);
                            }
                            // 清理普通素材
                            player.getInventory().addItem(inv.normalFootageList.toArray(Item[]::new));
                            for (int i = 3; i < 27; i++) {
                                if (AIR_PLACEHOLDER.deepEquals(inv.getItemFast(i))) {
                                    break;// 如果不行请改成 continue
                                }
                                inv.setItem(i, AIR_PLACEHOLDER);
                            }
                            inv.normalFootageList.clear();
                        } else if (slotChange.getSlot() == 2) {// 回退主素材
                            player.getInventory().addItem(sourceItem);

                            Item stoneTipItem = Item.fromString("minecraft:oak_sign");
                            stoneTipItem.setCustomName("§r§7一一一 §e放入「主素材」 §7一一一");
                            stoneTipItem.setLore(
                                    "§r§f主素材，请放入素材：" + this.forgingPaper.getMainFootage().getName(),
                                    "§r§f素材品质范围：§a" + (Math.max(this.forgingStone.getQuality(), 0)) + "~" + Math.min(this.forgingStone.getQuality() + 2, MAX_QUALITY) + "级"
                            );
                            inv.setItem(0, stoneTipItem);

                            Item barrierItem = Item.fromString("minecraft:barrier");
                            barrierItem.setCustomName("§r§7一一一 §c不可放入§7 一一一");
                            inv.setItem(2, barrierItem);

                            // 清理普通素材
                            player.getInventory().addItem(inv.normalFootageList.toArray(Item[]::new));
                            for (int i = 3; i < 27; i++) {
                                if (AIR_PLACEHOLDER.deepEquals(inv.getItemFast(i))) {
                                    break;// 如果不行请改成 continue
                                }
                                inv.setItem(i, AIR_PLACEHOLDER);
                            }
                            inv.normalFootageList.clear();
                        } else if (slotChange.getSlot() > 2) {
                            player.getInventory().addItem(inv.normalFootageList.get(slotChange.getSlot() - 3));
                            inv.normalFootageList.remove(slotChange.getSlot() - 3);
                            // 修改箱内物品状态
                            Item stoneTipItem = Item.fromString("minecraft:oak_sign");
                            stoneTipItem.setCustomName("§r§7一一一 §e放入「素材」 §7一一一");
                            stoneTipItem.setLore(
                                    "§r§f素材剩余容量：§a" + (this.forgingStone.getVolume() - inv.normalFootageList.size()),
                                    "§r§f素材品质范围：§a" + (Math.max(this.forgingStone.getQuality(), 0)) + "~" + Math.min(this.forgingStone.getQuality() + 2, MAX_QUALITY) + "级",
                                    !inv.normalFootageList.isEmpty() ? "§l§a>> 关闭进行锻造 <<" : "§c锻造需放入至少一个素材"
                            );
                            inv.setItem(0, stoneTipItem);

                            // 清理普通素材
                            // 0x, 1y, 2y, 3z, 4z
                            for (int i = 3; i < inv.normalFootageList.size() + 3; i++) {
                                inv.setItem(i, inv.normalFootageList.get(i - 3));
                            }
                            for (int i = inv.normalFootageList.size() + 3; i < 27; i++) {
                                if (AIR_PLACEHOLDER.deepEquals(inv.getItemFast(i))) {
                                    break;// 如果不行请改成 continue
                                }
                                inv.setItem(i, AIR_PLACEHOLDER);
                            }
                        }
                        break;
                    }

                    // 操作 - 如果放入的是原初之石
                    if (ForgingStone.isForgingStone(targetItem)) {
                        String stoneName = targetItem.getNamedTag().getString("name");
                        this.forgingStone = RcRPGMain.loadForgingStone.get(stoneName);
                        // 修改箱内物品状态
                        removeAndReturnItem(player, targetItem, inv, 1);
                        Item stoneTipItem = Item.fromString("minecraft:oak_sign");
                        stoneTipItem.setCustomName("§r§7一一一 §e放入「主素材」 §7一一一");
                        stoneTipItem.setLore(
                                "§r§f主素材，请放入素材：" + this.forgingPaper.getMainFootage().getName(),
                                "§r§f素材品质范围：§a" + (Math.max(this.forgingStone.getQuality(), 0)) + "~" + Math.min(this.forgingStone.getQuality() + 2, MAX_QUALITY) + "级",
                                !inv.normalFootageList.isEmpty() ? "§l§a>> 关闭进行锻造 <<" : "§c锻造需放入至少一个素材"
                        );
                        inv.setItem(0, stoneTipItem);
                        break;
                    }

                    // 操作 - 放入的如果是素材
                    if (targetItem.getNamedTag() != null) {
                        if (this.forgingStone == null) {
                            break;
                        }
                        CompoundTag tag = targetItem.getNamedTag();
                        if (!tag.contains("yamlName")) {
                            break;
                        }
                        if (!tag.contains("sell")) {
                            break;
                        }
                        if (!tag.contains("quality")) {
                            break;
                        }
                        int quality = tag.getInt("quality");
                        if (outOfQualityRange(quality)) {
                            break;
                        }
                        if (AIR_PLACEHOLDER.deepEquals(inv.getItem(2))) {// 主素材为空，替换
                            if (AIR_PLACEHOLDER.deepEquals(inv.getItem(1))) break;
                            if (forgingPaper.getMainFootage().getYamlName().equals(tag.getString("yamlName"))) {
                                removeAndReturnItem(player, targetItem, inv, 2);
                                // 修改箱内物品状态
                                Item stoneTipItem = Item.fromString("minecraft:oak_sign");
                                stoneTipItem.setCustomName("§r§7一一一 §e放入「素材」 §7一一一");
                                stoneTipItem.setLore(
                                        "§r§f素材剩余容量：§a" + this.forgingStone.getVolume(),
                                        "§r§f素材品质范围：§a" + (Math.max(this.forgingStone.getQuality(), 0)) + "~" + Math.min(this.forgingStone.getQuality() + 2, MAX_QUALITY) + "级",
                                        !inv.normalFootageList.isEmpty() ? "§l§a>> 关闭进行锻造 <<" : "§c锻造需放入至少一个素材"
                                );
                                inv.setItem(0, stoneTipItem);
                                break;
                            }
                        } else {
                            addFootage(player, targetItem, inv, slotChange.getSlot());
                            break;
                        }
                    }

                    isNotAction = true;
                    break;
                }
            }
            // 若没有进行任何操作则返还物品
            if (isNotAction) {
                player.getInventory().addItem(cursorItem);
            }

        });
        player.addWindow(inv);
    }

    /**
     * 检查值是否 不在合适的范围 内
     *
     * @param quality 需要检查的值（品质）
     * @return 如果品质质不在范围内，返回true；否则返回false
     */
    public boolean outOfQualityRange(int quality) {
        return quality < Math.max(this.forgingStone.getQuality(), 0) ||
                quality > Math.min(this.forgingStone.getQuality() + 2, MAX_QUALITY);
    }

    public void addFootage(Player player, Item targetItem, ForgingSubInventory inv, int actionSlot) {
        if (this.forgingStone == null) {
            return;
        }
        if (AIR_PLACEHOLDER.deepEquals(inv.getItemFast(2))) {
            return;
        }
        CompoundTag tag = targetItem.getNamedTag();
        if (!tag.contains("yamlName")) {
            return;
        }
        if (!tag.contains("sell")) {
            return;
        }
        if (!tag.contains("quality")) {
            return;
        }
        int quality = tag.getInt("quality");
        if (outOfQualityRange(quality)) {
            return;
        }

        // 如果素材已经满了
        if (inv.normalFootageList.size() >= this.forgingStone.getVolume()) {
            return;
        }
        for (int i = 3; i < 27; i++) {
            if (i > this.forgingStone.getVolume() + 2) {// 到达最大容量限制
                break;
            }

            if (AIR_PLACEHOLDER.deepEquals(inv.getItemFast(i))) {
                if (i > this.forgingStone.getVolume() + 2) {
                    break;
                }
                Item addItem = targetItem.clone();
                addItem.setCount(1);
                inv.normalFootageList.add(addItem);
                removeAndReturnItem(player, targetItem, inv, i);
                // 修改箱内物品状态
                Item stoneTipItem = Item.fromString("minecraft:oak_sign");
                stoneTipItem.setCustomName("§r§7一一一 §e放入「素材」 §7一一一");
                stoneTipItem.setLore(
                        "§r§f素材剩余容量：§a" + (this.forgingStone.getVolume() - inv.normalFootageList.size()),
                        "§r§f素材品质范围：§a" + (Math.max(this.forgingStone.getQuality(), 0)) + "~" + Math.min(this.forgingStone.getQuality() + 2, MAX_QUALITY) + "级"
                );
                inv.setItem(0, stoneTipItem);
                break;
            }
        }
//        for (int i = 3; i < inv.normalFootageList.size() + 2; i++) {
//            RcRPGMain.getInstance().getLogger().info("设置 "+ (i+1)+" 格子为"+inv.normalFootageList.get(i - 2).getName());
//            inv.setItem(i, inv.normalFootageList.get(i - 2));
//        }
//        for (int i = inv.normalFootageList.size() + 3; i < 27; i++) {
//            if (AIR_PLACEHOLDER.deepEquals(inv.getItemFast(i))) {
//                break;// 如果不行请改成 continue
//            }
//            RcRPGMain.getInstance().getLogger().info("将 "+ (i+1)+" 格子置空");
//            inv.setItem(i, AIR_PLACEHOLDER);
//        }
    }

    public void removeAndReturnItem(Player player, Item targetItem, FakeInventory inv, int invIndex) {
        // 移除物品
        Item removeItem = targetItem.clone();
        removeItem.setCount(1);
        Item sourceItem = inv.getItem(invIndex);
        if (sourceItem.deepEquals(removeItem)) {
            return;
        } else if (!sourceItem.isNull()) {
            if (sourceItem.getNamedTag() != null) {
                if (!AIR_PLACEHOLDER.deepEquals(sourceItem)) {
                    player.getInventory().addItem(sourceItem);
                }
            }
        }

        // 返还物品
        if (targetItem.getCount() - 1 > 0) {
            Item giveItem = targetItem.clone();
            giveItem.setCount(targetItem.getCount() - 1);
            player.getInventory().addItem(giveItem);
        }

        inv.setItem(invIndex, removeItem);
    }

    public void returnInvStone() {}

    public void returnInvMainFootage() {}

    @Override
    public Inventory getInventory() {
        return null;
    }
}
