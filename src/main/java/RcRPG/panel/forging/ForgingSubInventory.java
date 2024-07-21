package RcRPG.panel.forging;

import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import me.iwareq.fakeinventories.FakeInventory;

public class ForgingSubInventory extends FakeInventory {

    public long id;

    public ForgingSubInventory(String name) {
        super(InventoryType.CHEST, name);
    }

    @Override
    public void onClose(Player who) {
        RemoveEntityPacket pk = new RemoveEntityPacket();
        pk.eid = id;
        who.dataPacket(pk);
        super.onClose(who);
        //Map<Integer, Item> content = this.getContents();
        //List<Item> invItemList = content.values().stream().toList();
    }

}
