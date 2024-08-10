package RcRPG.panel.container.ornament;

import RcRPG.RcRPGMain;
import RcRPG.config.MainConfig;
import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import me.iwareq.fakeinventories.FakeInventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrnamentInventory extends FakeInventory {

    public long id;

    public OrnamentInventory(String name) {
        super(InventoryType.CHEST, name);
    }

    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.windowId = who.getWindowId(this);
        pk.entityId = id;
        pk.type = InventoryType.CHEST.getNetworkType();
        who.dataPacket(pk);
    }

    @Override
    public void onClose(Player who) {
        RemoveEntityPacket pk = new RemoveEntityPacket();
        pk.eid = id;
        who.dataPacket(pk);
        super.onClose(who);
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < MainConfig.getOrnamentEffectSlots(); i++) {
            Item item = this.getItem(i);
            if (item.isNull()) {
                list.add(i, "");
            } else {
                list.add(i, item.getNamedTag().getString("name") + ":" + item.getCount());
            }
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            if (list.get(i).isEmpty()) {
                list.remove(i);
            } else {
                break; // 遇到第一个非空字符串，停止移除操作
            }
        }
        String name = who.getName();
        if(RcRPGMain.getInstance().ornamentConfig.exists(name)){
            RcRPGMain.getInstance().ornamentConfig.set(name,list);
        }else {
            Map<String,Object> map = RcRPGMain.getInstance().ornamentConfig.getAll();
            map.put(name,list);
            RcRPGMain.getInstance().ornamentConfig.setAll((LinkedHashMap<String, Object>) map);
        }
        RcRPGMain.getInstance().ornamentConfig.save();
    }


}
