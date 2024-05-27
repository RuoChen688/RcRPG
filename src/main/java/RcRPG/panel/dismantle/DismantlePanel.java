package RcRPG.panel.dismantle;

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

public class DismantlePanel implements InventoryHolder {

    public static Map<Integer, Item> getPanel(Player player) {
        Map<Integer, Item> panel = new LinkedHashMap<>();
        Item tipItem = Item.fromString("minecraft:oak_sign");
        tipItem.setCustomName("提示");
        tipItem.setLore(
                "将武器、防具放入`分解炉`中关闭即可",
                "请确保背包空闲空间充足"
        );
        panel.put(0, tipItem);
        return panel;
    }

    public void sendPanel(Player player) {
        FakeInventory inv = new DismantleInventory("分解炉");
        inv.setContents(getPanel(player));
        inv.setDefaultItemHandler((item, event) -> {
            for (InventoryAction action : event.getTransaction().getActions()) {
                Item sourceItem = action.getSourceItem();
                Item targetItem = action.getTargetItem();
                if (action instanceof SlotChangeAction slotChange) {
                    if (slotChange.getInventory() instanceof FakeInventory) {

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
