package RcRPG;

import RcRPG.Form.guildForm;
import RcRPG.Form.inlayForm;
import RcRPG.RPG.*;
import RcRPG.Society.Guild;
import RcRPG.Society.Money;
import RcRPG.Society.Prefix;
import RcRPG.Society.Shop;
import RcRPG.Task.removeFloatingText;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSignPost;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseData;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Events implements Listener {

    public static LinkedHashMap<Player,String> playerShop = new LinkedHashMap<>();

    public static LinkedHashMap<Player,Boolean> playerMessage = new LinkedHashMap<>();

    public Events(){
    }

    @EventHandler
    public void interact(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Item item = event.getItem();
        Block block = event.getBlock();
        if(!item.isNull() && Magic.isMagic(item) && Handle.getShopByPos(block) == null){
            Magic.useMagic(player,item);
        }
        if(block instanceof BlockSignPost && Events.playerShop.containsKey(player)){
            Config config = Shop.addShopConfig(Events.playerShop.get(player),block.x + ":" + block.y + ":" + block.z + ":" +block.level.getName());
            Shop.loadShop(Events.playerShop.get(player),config);
            Events.playerShop.remove(player);
            player.sendMessage("创建成功");
        }
        if(Handle.getShopByPos(block) != null){
            Shop shop = Handle.getShopByPos(block);
            if(Events.playerMessage.containsKey(player)){
                if(Shop.costPlayer(player,shop)) {
                    Shop.gainPlayer(player, shop);
                    player.sendMessage(shop.getsMessage());
                }else{
                    player.sendMessage(shop.getNoMessage());
                }
                Events.playerMessage.remove(player);
            }else{
                Events.playerMessage.put(player,true);
                player.sendMessage(shop.getMessage());
            }
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event){
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if(block instanceof BlockSignPost && Handle.getShopByPos(block) != null){
            if(!player.isOp()) {
                event.setCancelled();
                return;
            }
            Shop shop = Handle.getShopByPos(block);
            Shop.delShopConfig(shop.getName());
            Main.loadShop.remove(shop.getName());
            player.sendMessage("拆除成功");
        }
    }

    @EventHandler
    public void onHeld(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        Item item = event.getItem();
        if(!item.isNull() && Weapon.isWeapon(item)){
            Weapon weapon = Main.loadWeapon.get(item.getNamedTag().getString("name"));
            if(Level.getLevel(player) < weapon.getLevel()){
                player.sendMessage("等级不足武器使用等级");
                event.setCancelled();
            }
        }
    }

    @EventHandler
    public void onFormResponded(PlayerFormRespondedEvent event){
        Player player = event.getPlayer();
        if(!event.wasClosed()){
            switch(event.getFormID()){
                case 10086:
                    FormResponseSimple response1 = (FormResponseSimple) event.getResponse();
                    inlayForm.makeForm_two_weapon(player,player.getInventory().getItemInHand(),response1);
                    break;
                case 10087:
                    FormResponseSimple response6 = (FormResponseSimple) event.getResponse();
                    inlayForm.makeForm_two_armour(player,player.getInventory().getItemInHand(),response6);
                    break;
                case 10010:
                    FormResponseCustom response2 = (FormResponseCustom) event.getResponse();
                    FormResponseData response3 = response2.getDropdownResponse(0);
                    if(response3.getElementID() == 0){
                        inlayForm.playerStone.remove(player);
                    }else{
                        LinkedList<Stone> list = Weapon.getStones(player.getInventory().getItemInHand());
                        for(int i = 0;i < list.size();i++){
                            if(i == inlayForm.playerClick.get(player)){
                                if(!response3.getElementContent().equals("无")){
                                    list.set(i,Handle.getStoneByLabel(response3.getElementContent()));
                                }else{
                                    list.remove(i);
                                }
                                break;
                            }
                        }
                        Weapon.setStone(player,player.getInventory().getItemInHand(),list);
                        if(!response3.getElementContent().equals("无")) Handle.removeStoneByLabel(player,response3.getElementContent());
                        if(inlayForm.playerStone.get(player) != null) Stone.giveStone(player,inlayForm.playerStone.get(player).getName(),1);
                        inlayForm.playerStone.remove(player);
                    }
                    inlayForm.playerClick.remove(player);
                    inlayForm.makeForm_one_weapon(player,player.getInventory().getItemInHand());
                    break;
                case 10011:
                    FormResponseCustom response4 = (FormResponseCustom) event.getResponse();
                    FormResponseData response5 = response4.getDropdownResponse(0);
                    if(response5.getElementID() == 0){
                        inlayForm.playerStone.remove(player);
                    }else{
                        LinkedList<Stone> list = Armour.getStones(player.getInventory().getItemInHand());
                        for(int i = 0;i < list.size();i++){
                            if(i == inlayForm.playerClick.get(player)){
                                if(!response5.getElementContent().equals("无")){
                                    list.set(i,Handle.getStoneByLabel(response5.getElementContent()));
                                }else{
                                    list.remove(i);
                                }
                                break;
                            }
                        }
                        Armour.setStone(player,player.getInventory().getItemInHand(),list);
                        if(!response5.getElementContent().equals("无")) Handle.removeStoneByLabel(player,response5.getElementContent());
                        if(inlayForm.playerStone.get(player) != null) Stone.giveStone(player,inlayForm.playerStone.get(player).getName(),1);
                        inlayForm.playerStone.remove(player);
                    }
                    inlayForm.playerClick.remove(player);
                    inlayForm.makeForm_one_armour(player,player.getInventory().getItemInHand());
                    break;
                case 10000:
                    FormResponseCustom response7 = (FormResponseCustom) event.getResponse();
                    FormResponseData response8 = response7.getDropdownResponse(0);
                    if(response8.getElementID() != 0){
                        Prefix.setPrefix(player,response8.getElementContent());
                    }
                    break;
                case 20000:
                    FormResponseSimple response9 = (FormResponseSimple) event.getResponse();
                    if(response9.getClickedButtonId() == 0) guildForm.make_join(player);
                    else guildForm.make_create(player);
                    break;
                case 20001:
                    FormResponseCustom response21 = (FormResponseCustom) event.getResponse();
                    String guild = response21.getDropdownResponse(0).getElementContent();
                    if(Guild.getMaxSize(guild) > Guild.getSize(guild)){
                        Guild.appGuild(player,guild);
                        guildForm.a_s(player);
                    }else{
                        guildForm.a_f(player);
                    }
                    break;
                case 20002:
                    FormResponseCustom response10 = (FormResponseCustom) event.getResponse();
                    if(Handle.getGuilds().contains(response10.getInputResponse(1))){
                        guildForm.create_failed(player);
                    }else{
                        if(Money.getMoney(player) < Main.instance.config.getInt("公会创建初始资金")){
                            guildForm.create_failed(player);
                        }else{
                            Guild.addGuild(player,response10.getInputResponse(1));
                            guildForm.make_one(player);
                        }
                    }
                    break;
                case 20003:
                case 20009:
                case 20010:
                case 20015:
                case 20016:
                case 20018:
                case 20019:
                case 20021:
                    FormResponseSimple response11 = (FormResponseSimple) event.getResponse();
                    if(response11.getClickedButtonId() == 0) guildForm.make_one(player);
                    break;
                case 20004:
                    FormResponseSimple response12 = (FormResponseSimple) event.getResponse();
                    if(response12.getClickedButtonId() == 0) guildForm.make_offer(player);
                    if(response12.getClickedButtonId() == 1) guildForm.make_member(player);
                    if(response12.getClickedButtonId() == 2) Guild.tpBase(player);
                    if(response12.getClickedButtonId() == 3) guildForm.make_update(player);
                    if(response12.getClickedButtonId() == 4) guildForm.make_app(player);
                    if(response12.getClickedButtonId() == 5) guildForm.make_assistant(player);
                    if(response12.getClickedButtonId() == 6) Guild.setBase(player);
                    if(response12.getClickedButtonId() == 7) guildForm.make_dismiss(player);
                    break;
                case 20005:
                    FormResponseCustom response13 = (FormResponseCustom) event.getResponse();
                    if(Money.getMoney(player) < response13.getSliderResponse(0)) guildForm.offer_f(player);
                    else{
                        Money.delMoney(player, (int) response13.getSliderResponse(0));
                        Guild.addMoney(player,(int) response13.getSliderResponse(0));
                        guildForm.offer_s(player);
                    }
                    break;
                case 20006:
                    if(Guild.isMaster(player) || Guild.isAssistantMaster(player)){
                        FormResponseSimple response14 = (FormResponseSimple) event.getResponse();
                        if(Guild.isAssistantMaster(player) && response14.getClickedButton().getText().equals(Guild.getMaster(player))) guildForm.member_f(player);
                        else if(response14.getClickedButton().getText().equals(player.getName())){
                            if(!Guild.isMaster(player)) guildForm.member_m(player);
                            else guildForm.make_dismiss(player);
                        }
                        else guildForm.member_s(player,response14.getClickedButton().getText());
                    }
                    break;
                case 20007:
                    FormResponseSimple response15 = (FormResponseSimple) event.getResponse();
                    guildForm.app_s(player,response15.getClickedButton().getText());
                    break;
                case 20008:
                    FormResponseSimple response16 = (FormResponseSimple) event.getResponse();
                    if(response16.getClickedButtonId() == 1) guildForm.make_one(player);
                    else Guild.dismissGuild(player);
                    break;
                case 20011:
                    FormResponseSimple response17 = (FormResponseSimple) event.getResponse();
                    if(response17.getClickedButtonId() == 0) Guild.kickGuild(player,response17.getClickedButton().getText());
                    else guildForm.make_member(player);
                    break;
                case 20012:
                    FormResponseSimple response18 = (FormResponseSimple) event.getResponse();
                    if(response18.getClickedButtonId() == 0) guildForm.make_member(player);
                    break;
                case 20013:
                    FormResponseSimple response19 = (FormResponseSimple) event.getResponse();
                    if(response19.getClickedButtonId() == 0) {
                        Guild.kickGuild(player,player.getName());
                        guildForm.make_one(player);
                    }else if(response19.getClickedButtonId() == 1){
                        guildForm.make_one(player);
                    }
                    break;
                case 20014:
                    FormResponseSimple response20 = (FormResponseSimple) event.getResponse();
                    if(response20.getClickedButtonId() == 0) {
                        Guild.acceptApp(player,response20.getClickedButton().getText());
                        guildForm.make_one(player);
                    }else if(response20.getClickedButtonId() == 1){
                        Guild.rejectApp(player,response20.getClickedButton().getText());
                        guildForm.make_one(player);
                    }
                    break;
                case 20017:
                    FormResponseSimple response22 = (FormResponseSimple) event.getResponse();
                    if(response22.getClickedButtonId() == 0){
                        if(Guild.getMoney(player) < Guild.getUpdateMoney(player)){
                            guildForm.update_f(player);
                        }else{
                            Guild.delMoney(player,Guild.getUpdateMoney(player));
                            Guild.updateGuild(player);
                            guildForm.update_s(player);
                        }
                    }
                    break;
                case 20020:
                    FormResponseSimple response23 = (FormResponseSimple) event.getResponse();
                    guildForm.as_s(player,response23.getClickedButton().getText());
                    Guild.setAssistantMaster(Server.getInstance().getPlayer(response23.getClickedButton().getText()),Guild.getGuild(player));
                    break;
            }
        }
    }

    @EventHandler
    public void damageEvent(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        if(damager instanceof Player){
            int damage = Damage.getDamage((Player) damager,entity);
            Damage.onDamage((Player) damager,entity);
            if(damage >= entity.getHealth() && entity instanceof Player){
                damage = (int) entity.getHealth();
                Item item = ((Player) damager).getInventory().getItemInHand();
                if(!item.isNull() && Weapon.isWeapon(item)){
                    Weapon weapon = Main.loadWeapon.get(item.getNamedTag().getString("name"));
                    if(!weapon.getKillMessage().equals("")){
                        String text = weapon.getKillMessage();
                        if(text.contains("@damager"))  text = text.replace("@damager",damager.getName());
                        if(text.contains("@player"))  text = text.replace("@player",entity.getName());
                        Server.getInstance().broadcastMessage(text);
                    }
                }
            }
            event.setDamage(damage);
            Vector3 go = Damage.go(damager.yaw,damager.pitch,2);
            FloatingText floatingText = new FloatingText(new Location(damager.x + go.x, damager.y+1, damager.z+ go.z, damager.level), "§c-"+damage);
            floatingText.spawnToAll();
            Main.instance.getServer().getScheduler().scheduleDelayedTask(new removeFloatingText(Main.instance,floatingText),15);
        }
    }

    @EventHandler
    public void chatEvent(PlayerChatEvent event){
        Player player = event.getPlayer();
        String name = player.getName();
        String message = event.getMessage();
        event.setCancelled();
        String text = Main.instance.config.getString("聊天显示");
        if(text.contains("@name")) text = text.replace("@name", player.getName());
        if(text.contains("@hp")) text = text.replace("@hp",String.valueOf(player.getHealth()));
        if(text.contains("@maxhp")) text = text.replace("@maxhp",String.valueOf(player.getMaxHealth()));
        if(text.contains("@exp")) text = text.replace("@exp", String.valueOf(Level.getExp(player)));
        if(text.contains("@maxexp")) text = text.replace("@maxexp", String.valueOf(Level.getMaxExp(player)));
        if(text.contains("@level")) text = text.replace("@level", String.valueOf(Level.getLevel(player)));
        if(text.contains("@prefix")) text = text.replace("@prefix", String.valueOf(Handle.getPlayerConfig(name).getString("称号")));
        if(text.contains("@message")) text = text.replace("@message", message);
        if(text.contains("@guild")) text = text.replace("@guild", String.valueOf(Handle.getPlayerConfig(player.getName()).getString("公会")));
        Server.getInstance().broadcastMessage(text);
    }

    @EventHandler
    public void joinEvent(PlayerJoinEvent event){
        Player player = event.getPlayer();
        String name = player.getName();
        File file = new File(Main.instance.getDataFolder()+"/Players/"+name+".yml");
        if(!file.exists()){
            Main.instance.saveResource("Player.yml","/Players/"+name+".yml",false);
            Config config = new Config(Main.instance.getPlayerFile()+"/"+name+".yml");
            config.set("名称",name);
            config.set("公会",Main.instance.config.getString("初始公会"));
            config.set("称号",Main.instance.config.getString("初始称号"));
            ArrayList<String> list = (ArrayList<String>) config.getStringList("称号列表");
            list.add(Main.instance.config.getString("初始称号"));
            config.set("称号列表",list);
            config.save();
        }
        String text = Main.instance.config.getString("顶部显示");
        if(text.contains("@name")) text = text.replace("@name", player.getName());
        if(text.contains("@hp")) text = text.replace("@hp",String.valueOf(player.getHealth()));
        if(text.contains("@maxhp")) text = text.replace("@maxhp",String.valueOf(player.getMaxHealth()));
        if(text.contains("@exp")) text = text.replace("@exp", String.valueOf(Level.getExp(player)));
        if(text.contains("@maxexp")) text = text.replace("@maxexp", String.valueOf(Level.getMaxExp(player)));
        if(text.contains("@level")) text = text.replace("@level", String.valueOf(Level.getLevel(player)));
        if(text.contains("@prefix")) text = text.replace("@prefix", String.valueOf(Handle.getPlayerConfig(name).getString("称号")));
        if(text.contains("@guild")) text = text.replace("@guild", String.valueOf(Handle.getPlayerConfig(player.getName()).getString("公会")));
        player.setNameTag(text);
        player.setNameTagVisible();
        player.setNameTagAlwaysVisible();
        player.setMaxHealth(Handle.getMaxHealth(player));
    }

}
