package RcRPG.panel.container.forging;

import RcRPG.AttrManager.FootageAttr;
import cn.ankele.plugin.MagicItem;
import cn.ankele.plugin.bean.ItemBean;
import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import me.iwareq.fakeinventories.FakeInventory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        Map<Integer, Item> content = this.getContents();
        // get(0); // 原初之石
        // get(1); // 主素材
        // get(2); // 次素材
        List<Item> invItemList = content.values().stream()
                .skip(2).toList();
        var attr = new FootageAttr();
        LinkedHashMap<String, ItemBean> magicItemMap = MagicItem.getItemsMap();
        for (int i = 0; i < invItemList.size(); i++) {
            Item item = invItemList.get(i);
            if (item.deepEquals(ForgingSubPanel.AIR_PLACEHOLDER)) {
                break;
            }
            CompoundTag tag = item.getNamedTag();
            if (!tag.contains("yamlName")) {
                continue;
            }
            var itemBeam = magicItemMap.get(tag.getString("yamlName"));
            if (itemBeam == null) {
                continue;
            }
            attr.setItemAttrConfig("item_i" + i, itemBeam.getAttr());
        }
    }

}
