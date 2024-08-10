package RcRPG.panel.container.ornament;

import RcRPG.RPG.Ornament;
import RcRPG.RcRPGMain;
import RcRPG.config.MainConfig;
import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import me.iwareq.fakeinventories.FakeInventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrnamentPanel implements InventoryHolder {

    public static Map<Integer, Item> getPanel(Player player) {
        Map<Integer, Item> panel = new LinkedHashMap<>();
        String name = player.getName();
        if (RcRPGMain.getInstance().ornamentConfig.exists(name)) {
            ArrayList<String> list = (ArrayList<String>) RcRPGMain.getInstance().ornamentConfig.getStringList(name);
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null || list.get(i).isEmpty()) {
                    panel.put(i, Item.AIR_ITEM);
                    continue;
                };
                String[] s = list.get(i).split(":");
                Item item = Ornament.getItem(s[0], Integer.parseInt(s[1])).clone();
                panel.put(i, item);
            }
        }
        return panel;
    }

    public void sendPanel(Player player) {
        OrnamentInventory inv = new OrnamentInventory("饰品背包");
        inv.setContents(getPanel(player));
        Item mask = Item.fromString("minecraft:black_stained_glass_pane");
        mask.setCustomName("§r§c饰品仅允许置于空槽位中");
        for (int i = MainConfig.getOrnamentEffectSlots(); i < inv.getSize(); i++) {
            inv.setItem(i, mask);
        }

        inv.setDefaultItemHandler((item, event) -> {
            for (InventoryAction action : event.getTransaction().getActions()) {
                Item sourceItem = action.getSourceItem();
                Item targetItem = action.getTargetItem();
                if (action instanceof SlotChangeAction slotChange) {
                    if (slotChange.getInventory() instanceof FakeInventory) {
                        // 饰品箱子
                        if (!Ornament.isOrnament(sourceItem) && !Ornament.isOrnament(targetItem)) {
                            event.setCancelled();
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
