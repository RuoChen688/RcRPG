package RcRPG.panel.container.forging;

import RcRPG.RPG.Forging.ForgingPaper;
import RcRPG.RcRPGMain;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.inventory.transaction.action.SlotChangeAction;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.PluginTask;
import me.iwareq.fakeinventories.FakeInventory;

import java.util.LinkedHashMap;
import java.util.Map;

public class ForgingPanel implements InventoryHolder {

    public static String titleStateOne = "锻造 - 选择锻造方案";

    public static Map<Integer, Item> getPanel(Player player) {
        Map<Integer, Item> panel = new LinkedHashMap<>();
        RcRPGMain.loadForgingPaper.forEach((name, paper) -> {
            panel.put(panel.size(), ForgingPaper.getItem(name, 1));
        });
        return panel;
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
                        event.setCancelled(true);
                        if (!ForgingPaper.isForgingPaper(sourceItem)) {
                            return;
                        }
                        String paperName = sourceItem.getNamedTag().getString("name");

                        inv.close(player);

                        Server.getInstance().getScheduler().scheduleDelayedTask(new PluginTask<>(RcRPGMain.getInstance()) {
                            @Override
                            public void onRun(int i) {
                                new ForgingSubPanel(RcRPGMain.loadForgingPaper.get(paperName)).sendPanel(player);
                            }
                        }, 17);
                        RcRPGMain.loadForgingPaper.get(paperName);
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
