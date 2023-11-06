package RcRPG.Panel;

import RcRPG.Panel.lib.AbstractFakeInventory;
import RcRPG.Society.Trade;
import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.StringTag;

import java.util.LinkedHashMap;
import java.util.Map;

public class DisplayPanel implements InventoryHolder {

    private AbstractFakeInventory inventory;

    /**
     * 交易面板
     * */
    public static Map<Integer, Item> Panel(){
        Map<Integer,Item> panel = new LinkedHashMap<>();
        CompoundTag tag = new CompoundTag();
        tag.put("type",new StringTag("py","py"));
        Item item1 = Item.get(102);
        item1.setNamedTag(tag);
        item1.setCustomName("边界");
        for(int i = 4;i <= 49;i += 9){
            panel.put(i,item1);
        }
        Item item2 = Item.get(152);
        item2.setNamedTag(tag);
        item2.setCustomName("拒绝交易");
        for(int i = 47;i <= 51;i += 4){
            panel.put(i,item2);
        }
        Item item3 = Item.get(133);
        item3.setNamedTag(tag);
        item3.setCustomName("同意交易");
        for(int i = 48;i <= 50;i += 2){
            panel.put(i,item3);
        }
        return panel;
    }

    public Map<Integer, Item> getPanel(Player player){
        Map<Integer, Item> panel = DisplayPanel.Panel();
        Player to = Trade.playerTrade.get(player);
        if(Trade.player.get(player) == 1){
            for(int i = 0;i < Trade.playItems.get(player).size();i++){
                panel.put(((i+1) / 5) * 9 + i % 4 - 1,Trade.playItems.get(player).get(i));
            }
        }else{
            for(int i = 0;i < Trade.playItems.get(player).size();i++){
                panel.put(((i+1) / 5) * 9 + i % 4 + 5,Trade.playItems.get(player).get(i));
            }
        }
        return panel;
    }

    public void displayPlayer(Player player,Map<Integer, Item> itemMap,String name){

        ChestInventoryPanel panel = new ChestInventoryPanel(this,name);
        panel.setContents(itemMap);
        panel.id = Entity.entityCount++;
        inventory = panel;
        player.addWindow(panel);

    }


    public void displayPlayer(Player player,String name){
        ChestInventoryPanel panel = new ChestInventoryPanel(this,name);
        panel.setContents(Panel());
        panel.id = Entity.entityCount++;
        inventory = panel;
        player.addWindow(panel);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
