package RcRPG.panel.container.forging;

import RcRPG.AttrManager.FootageAttr;
import RcRPG.RPG.Armour;
import RcRPG.RPG.Weapon;
import cn.ankele.plugin.MagicItem;
import cn.ankele.plugin.bean.ItemBean;
import cn.nukkit.Player;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import me.iwareq.fakeinventories.FakeInventory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ForgingSubInventory extends FakeInventory {

    public long id;

    public ArrayList<Item> normalFootageList = new ArrayList<>();

    List<String> origin;

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

        if (normalFootageList.isEmpty()) {
            who.sendMessage("锻造已取消，需至少放入一个素材");
            List<Item> invItemList = content.values().stream()
                    .skip(1).toList();
            for (int i = 0; i < invItemList.size(); i++) {
                Item item = invItemList.get(i);
                if (item.deepEquals(ForgingSubPanel.AIR_PLACEHOLDER)) {
                    break;
                }
                who.getInventory().addItem(item);
            }
            return;
        }
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
            ItemBean itemBeam = magicItemMap.get(tag.getString("yamlName"));
            if (itemBeam == null) {
                continue;
            }
            attr.setItemAttrConfig("item_i" + i, itemBeam.getAttr());
        }
        Item resultItem = Item.AIR_ITEM;
        switch (origin.get(0)) {
            case "Weapon" -> {
                resultItem = Weapon.getItem(origin.get(1), 1);
                resultItem.setNamedTag(resultItem.getNamedTag().putCompound("attr", attr.toNBT()));
                Weapon.setWeaponLore(resultItem, who.getLanguageCode());
            }
            case "Armour" -> {
                resultItem = Armour.getItem(origin.get(1), 1);
                resultItem.setNamedTag(resultItem.getNamedTag().putCompound("attr", attr.toNBT()));
                Armour.setArmourLore(resultItem, who.getLanguageCode());
            }
            default -> who.sendMessage("§c锻造图纸配置了未知的源：§e§l"+origin.get(0)+"/"+origin.get(1));
        }
        who.getInventory().addItem(resultItem);
    }

}
