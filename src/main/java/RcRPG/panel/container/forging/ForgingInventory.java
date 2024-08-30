package RcRPG.panel.container.forging;

import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import me.iwareq.fakeinventories.FakeInventory;

public class ForgingInventory extends FakeInventory {

    public long id;

    public ForgingInventory(String name) {
        super(InventoryType.CHEST, name);
    }

    @Override
    public void onClose(Player who) {
        RemoveEntityPacket pk = new RemoveEntityPacket();
        pk.eid = id;
        who.dataPacket(pk);
        super.onClose(who);

    }

}
