package RcRPG.panel.forging;

import RcRPG.RPG.Armour;
import RcRPG.RPG.Weapon;
import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import me.iwareq.fakeinventories.FakeInventory;

import java.util.LinkedHashMap;
import java.util.Map;

public class ForgingPanel implements InventoryHolder {

    public static String titleStateOne = "锻造 - 请放入图纸";
    public static String titleStateTwo = "锻造 - 请放入原初之石";
    public static String titleStateThree = "锻造 - 请放入主素材";
    public static String titleStateFour = "锻造 - 请放入副素材";
    public static Map<Integer, Item> getPanel(Player player) {
        Map<Integer, Item> panel = new LinkedHashMap<>();
        Item tipItem = Item.fromString("minecraft:oak_sign");
        tipItem.setCustomName(titleStateOne);
        tipItem.setLore(
                "将武器、防具放入`分解炉`中关闭即可",
                "请确保背包空闲空间充足"
        );
        panel.put(0, tipItem);
        return panel;
    }

    public void sendPanel(Player player) {
        FakeInventory inv = new ForgingInventory(titleStateOne);
        inv.setContents(getPanel(player));
        inv.setDefaultItemHandler((item, event) -> {
            for (InventoryAction action : event.getTransaction().getActions()) {
                Item sourceItem = action.getSourceItem();
                Item targetItem = action.getTargetItem();
                if (action instanceof SlotChangeAction slotChange) {
                    if (slotChange.getInventory() instanceof FakeInventory) {
                        if (slotChange.getSlot() == 1) {// 放图纸至炉子
                            if (targetItem.isNull()) {
                                event.setCancelled();
                                return;
                            }
                        }
                        if (sourceItem.isNull()) {// 放装备至炉子
                            if (!Armour.isArmour(targetItem) && !Weapon.isWeapon(targetItem)) {
                                event.setCancelled();
                                return;
                            }
                        } else {
                            if (!Armour.isArmour(sourceItem) && !Weapon.isWeapon(sourceItem)) {
                                event.setCancelled();
                                return;
                            }
                        }
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
