package RcRPG.panel.forging;

import RcRPG.RPG.Forging.ForgingPaper;
import RcRPG.RcRPGMain;
import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import me.iwareq.fakeinventories.FakeInventory;

import java.util.LinkedHashMap;
import java.util.Map;

public class ForgingSubPanel implements InventoryHolder {

    public static String titleStateOne = "锻造 - 请选择锻造方案";
    public static String titleStateTwo = "锻造 - 请放入原初之石";
    public static String titleStateThree = "锻造 - 请放入主素材";
    public static String titleStateFour = "锻造 - 请放入副素材";

    public static Map<Integer, Item> getPanel(Player player) {
        Map<Integer, Item> panel = new LinkedHashMap<>();
        Item stoneTipItem = Item.fromString("minecraft:oak_sign");
        stoneTipItem.setCustomName("§r§7一一一 §e放入「原初之石」 §7一一一");
        stoneTipItem.setLore(
                "§r§f原初之石会确定锻造素材的容许品质",
                "§r§f同时也决定可放入的素材数量"
        );
        panel.put(0, stoneTipItem);
        Item barrierItem = Item.fromString("minecraft:barrier");
        barrierItem.setCustomName("§r§7一一一 §c不可放入§7 一一一");
        for (int i = 1; i < 27; i++) {
            panel.put(i, barrierItem);
        }
        return panel;
    }

    public ForgingPaper forgingPaper;

    public ForgingSubPanel(ForgingPaper paper) {
        this.forgingPaper = paper;
    }

    public Item forgingStoneItem = Item.AIR_ITEM;

    public void sendPanel(Player player) {
        FakeInventory inv = new ForgingInventory(titleStateOne);
        inv.setContents(getPanel(player));
        inv.setDefaultItemHandler((item, event) -> {
            for (InventoryAction action : event.getTransaction().getActions()) {
                Item sourceItem = action.getSourceItem();
                Item targetItem = action.getTargetItem();
                if (action instanceof SlotChangeAction slotChange) {
                    if (slotChange.getInventory() instanceof FakeInventory) {
                        if (ForgingPaper.isForgingPaper(sourceItem)) {
                            String paperName = sourceItem.getNamedTag().getString("name");
                            RcRPGMain.loadForgingPaper.get(paperName);
                        }
                        event.setCancelled(true);
                        break;
                    }
                }
            }

        });
        player.addWindow(inv);
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
