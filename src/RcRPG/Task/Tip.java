package RcRPG.Task;

import RcRPG.Handle;
import RcRPG.Main;
import RcRPG.RPG.*;
import RcRPG.Society.Money;
import RcRPG.Society.Points;
import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.PluginTask;

public class Tip extends PluginTask {

    public Tip(Main main){
        super(main);
    }

    @Override
    public void onRun(int i) {
        for(Player player:Main.instance.getServer().getOnlinePlayers().values()){
            if(Handle.getPlayerConfig(player.getName()) == null) return;
            if(player.getInventory().getItemInHand().isNull() || player.getInventory().getItemInHand().getNamedTag() == null){
                String text = Main.instance.config.getString("底部显示");
                if(text.contains("@name")) text = text.replace("@name", player.getName());
                if(text.contains("@hp")) text = text.replace("@hp",String.valueOf(player.getHealth()));
                if(text.contains("@maxhp")) text = text.replace("@maxhp",String.valueOf(player.getMaxHealth()));
                if(text.contains("@money")) text = text.replace("@money", String.valueOf(Money.getMoney(player)));
                if(text.contains("@point")) text = text.replace("@point", String.valueOf(Points.getPoint(player)));
                if(text.contains("@exp")) text = text.replace("@exp", String.valueOf(Level.getExp(player)));
                if(text.contains("@maxexp")) text = text.replace("@maxexp", String.valueOf(Level.getMaxExp(player)));
                if(text.contains("@level")) text = text.replace("@level", String.valueOf(Level.getLevel(player)));
                if(text.contains("@prefix")) text = text.replace("@prefix", String.valueOf(Handle.getPlayerConfig(player.getName()).getString("称号")));
                if(text.contains("@guild")) text = text.replace("@guild", String.valueOf(Handle.getPlayerConfig(player.getName()).getString("公会")));
                if(text.contains("@n")){
                    text = text.replace("@n","\n");
                    player.sendTip(text);
                }else{
                    player.sendTip(text);
                }
            }else{
                Item item = player.getInventory().getItemInHand();
                CompoundTag tag = item.getNamedTag();
                String text = "";
                switch (tag.getString("type")){
                    case "weapon":
                        Weapon weapon = Main.loadWeapon.get(item.getNamedTag().getString("name"));
                        text = weapon.getTipText();
                        if(text.contains("@n")) text = text.replace("@n","\n");
                        break;
                    case "armour":
                        Armour armour = Main.loadArmour.get(item.getNamedTag().getString("name"));
                        text = armour.getTipText();
                        if(text.contains("@n")) text = text.replace("@n","\n");
                        break;
                    case "stone":
                        Stone stone = Main.loadStone.get(item.getNamedTag().getString("name"));
                        text = stone.getTipText();
                        if(text.contains("@n")) text = text.replace("@n","\n");
                        break;
                    case "magic":
                        Magic magic = Main.loadMagic.get(item.getNamedTag().getString("name"));
                        text = magic.getTipText();
                        if(text.contains("@n")) text = text.replace("@n","\n");
                        break;    
                }
                player.sendTip(text);
            }
        }
    }
}
