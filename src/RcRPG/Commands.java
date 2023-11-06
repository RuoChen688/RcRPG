package RcRPG;

import RcRPG.Form.guildForm;
import RcRPG.Form.inlayForm;
import RcRPG.Form.prefixForm;
import RcRPG.RPG.*;
import RcRPG.Society.Money;
import RcRPG.Society.Points;
import RcRPG.Society.Prefix;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.utils.Config;

import java.util.Arrays;
import java.util.List;

public class Commands extends Command {

    public Commands() {
        super("rpg", "RPG指令", "/rpg help");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        if(!sender.isOp()){
            sender.sendMessage("权限不足");
            return false;
        }
        List<String> args = Arrays.asList(strings);
        switch(args.get(0)){
            case "help":
                sender.sendMessage("/rpg weapon help 武器指令");
                sender.sendMessage("/rpg armour help 盔甲指令");
                sender.sendMessage("/rpg stone help 宝石指令");
                sender.sendMessage("/rpg magic help 魔法物品指令");
                sender.sendMessage("/rpg prefix help 称号指令");
                sender.sendMessage("/rpg guild 公会指令");
                sender.sendMessage("/rpg exp help 经验指令");
                sender.sendMessage("/rpg money help 金币指令");
                sender.sendMessage("/rpg point help 点券指令");
                sender.sendMessage("/rpg shop help 商店指令");
                sender.sendMessage("/rpg py 切换交易模式");
                break;
            case "guild":
                guildForm.make_one((Player) sender);
                break;
            case "shop":
                if(args.size() != 2){
                    sender.sendMessage("参数错误");
                    return false;
                }
                if(args.get(1).equals("help")){
                    sender.sendMessage("/rpg shop [Name] 创建一个名为Name的商店");
                    return true;
                }
                Events.playerShop.put((Player) sender,args.get(1));
                sender.sendMessage("点击一个木牌");
                break;
            case "exp":
                if(args.get(1).equals("give")){
                    if(args.size() != 4){
                        sender.sendMessage("参数错误");
                        return false;
                    }
                    Player player = Server.getInstance().getPlayer(args.get(2));
                    if(player == null) return false;
                    if(Level.addExp(player,Integer.parseInt(args.get(3)))){
                        if(sender.isPlayer()) sender.sendMessage("给予成功");
                    }else{
                        if(sender.isPlayer()) sender.sendMessage("给予失败");
                    }
                } else if (args.get(1).equals("help")) {
                    sender.sendMessage("/rpg exp give [Player] [Exp] 给予玩家经验");
                }
                break;
            case "money":
                switch(args.get(1)){
                    case "help":
                        sender.sendMessage("/rpg money add [Player] [Money] 给予玩家金币");
                        sender.sendMessage("/rpg money del [Player] [Money] 扣除玩家金币");
                        sender.sendMessage("/rpg money my 查看自身金币");
                        break;
                    case "add":
                        Player player = Server.getInstance().getPlayer(args.get(2));
                        if(player == null) return false;
                        if(Money.addMoney(player,Integer.parseInt(args.get(3)))){
                            if(sender.isPlayer()) sender.sendMessage("给予成功");
                        }else{
                            if(sender.isPlayer()) sender.sendMessage("给予失败");
                        }
                        break;
                    case "del":
                        if(Money.delMoney(Main.instance.getServer().getPlayer(args.get(2)),Integer.parseInt(args.get(3)))){
                            if(sender.isPlayer()) sender.sendMessage("扣除成功");
                        }else{
                            if(sender.isPlayer()) sender.sendMessage("扣除失败");
                        }
                        break;
                    case "my":
                        int money = Money.getMoney((Player) sender);
                        sender.sendMessage(String.valueOf(money));
                        break;
                }
                break;
            case "point":
                switch(args.get(1)){
                    case "help":
                        sender.sendMessage("/rpg point add [Player] [Point] 给予玩家点券");
                        sender.sendMessage("/rpg point del [Player] [Point] 扣除玩家点券");
                        sender.sendMessage("/rpg point my 查看自身点券");
                        break;
                    case "add":
                        if(Points.addPoint(Main.instance.getServer().getPlayer(args.get(2)),Integer.parseInt(args.get(3)))){
                            if(sender.isPlayer()) sender.sendMessage("给予成功");
                        }else{
                            if(sender.isPlayer()) sender.sendMessage("给予失败");
                        }
                        break;
                    case "del":
                        if(Points.delPoint(Main.instance.getServer().getPlayer(args.get(2)),Integer.parseInt(args.get(3)))){
                            if(sender.isPlayer()) sender.sendMessage("扣除成功");
                        }else{
                            if(sender.isPlayer()) sender.sendMessage("扣除失败");
                        }
                        break;
                    case "my":
                        int point = Points.getPoint((Player) sender);
                        sender.sendMessage(String.valueOf(point));
                        break;
                }
                break;
            case "prefix":
                switch(args.get(1)){
                    case "help":
                        sender.sendMessage("/rpg prefix give [Player] [Prefix] 给予玩家称号");
                        sender.sendMessage("/rpg prefix remove [Player] [Prefix] 扣除玩家称号");
                        sender.sendMessage("/rpg prefix my 查看自身称号");
                        break;
                    case "give":
                        if(args.size() != 4){
                            sender.sendMessage("参数错误");
                            return false;
                        }
                        if(Prefix.givePrefix(Server.getInstance().getPlayer(args.get(2)), args.get(3))){
                            sender.sendMessage("给予成功");
                        }else{
                            sender.sendMessage("给予失败");
                        }
                        break;
                    case "remove":
                        if(args.size() != 4){
                            sender.sendMessage("参数错误");
                            return false;
                        }
                        if(Prefix.delPrefix(Server.getInstance().getPlayer(args.get(2)), args.get(3))){
                            sender.sendMessage("给予成功");
                        }else{
                            sender.sendMessage("给予失败");
                        }
                        break;
                    case "my":
                        prefixForm.makeForm((Player) sender);
                        break;
                }
                break;
            case "weapon":
                switch(args.get(1)){
                    case "help":
                        sender.sendMessage("/rpg weapon add [Name] 创建一个名为Name的武器配置");
                        sender.sendMessage("/rpg weapon del [Name] 删除一个名为Name的武器配置");
                        sender.sendMessage("/rpg weapon give [Player] [Name] [Count] 给予玩家一定数量的武器");
                        sender.sendMessage("/rpg weapon inlay 打开武器镶嵌面板");
                        break;
                    case "add":
                        if(args.size() != 3){
                            sender.sendMessage("参数错误");
                            return false;
                        }
                        Item item = ((Player) sender).getInventory().getItemInHand();
                        int id,subid;
                        if(item.isNull()){
                            id = 267;
                            subid = 0;
                        }else{
                            id = item.getId();
                            subid = item.getDamage();
                        }
                        Config config;
                        if((config = Weapon.addWeaponConfig(args.get(2),id,subid)) != null){
                            Weapon weapon = Weapon.loadWeapon(args.get(2),config);
                            Main.loadWeapon.put(args.get(2),weapon);
                            sender.sendMessage("添加成功");
                        }else{
                            sender.sendMessage("添加失败");
                        }
                        break;
                    case "del":
                        if(args.size() != 3){
                            sender.sendMessage("参数错误");
                            return false;
                        }
                        if(Weapon.delWeaponConfig(args.get(2))){
                            Main.loadWeapon.remove(args.get(2));
                            sender.sendMessage("删除成功");
                        }else{
                            sender.sendMessage("删除失败");
                        }
                        break;
                    case "give":
                        if(args.size() != 5){
                            sender.sendMessage("参数错误");
                            return false;
                        }
                        Player player = Main.instance.getServer().getPlayer(args.get(2));
                        if(!player.isOnline()){
                            sender.sendMessage("玩家不在线");
                            return false;
                        }
                        if(Weapon.giveWeapon(player,args.get(3), Integer.parseInt(args.get(4)))){
                            if(sender.isPlayer()) sender.sendMessage("给予成功");
                        }else{
                            if(sender.isPlayer()) sender.sendMessage("给予失败");
                        }
                        break;
                    case "inlay":
                        item = ((Player)sender).getInventory().getItemInHand();
                        if(!item.isNull()){
                            if(Weapon.isWeapon(item)){
                                inlayForm.makeForm_one_weapon(((Player) sender),item);
                            }else{
                                sender.sendMessage("请手持武器");
                                return false;
                            }
                        }else{
                            sender.sendMessage("未手持武器");
                            return false;
                        }
                        break;
                }
                break;
            case "armour":
                switch(args.get(1)){
                    case "help":
                        sender.sendMessage("/rpg armour add [Name] 创建一个名为Name的盔甲配置");
                        sender.sendMessage("/rpg armour del [Name] 删除一个名为Name的盔甲配置");
                        sender.sendMessage("/rpg armour give [Player] [Name] [Count] 给予玩家一定数量的盔甲");
                        sender.sendMessage("/rpg armour inlay 打开盔甲镶嵌面板");
                        break;
                    case "add":
                        if(args.size() != 3){
                            sender.sendMessage("参数错误");
                            return false;
                        }
                        Item item = ((Player) sender).getInventory().getItemInHand();
                        int id,subid;
                        if(item.isNull()){
                            id = 307;
                            subid = 0;
                        }else{
                            id = item.getId();
                            subid = item.getDamage();
                        }
                        Config config;
                        if((config = Armour.addArmourConfig(args.get(2),id,subid)) != null){
                            Armour armour = Armour.loadArmour(args.get(2),config);
                            Main.loadArmour.put(args.get(2),armour);
                            sender.sendMessage("添加成功");
                        }else{
                            sender.sendMessage("添加失败");
                        }
                        break;
                    case "del":
                        if(args.size() != 3){
                            sender.sendMessage("参数错误");
                            return false;
                        }
                        if(Armour.delArmourConfig(args.get(2))){
                            Main.loadArmour.remove(args.get(2));
                            sender.sendMessage("删除成功");
                        }else{
                            sender.sendMessage("删除失败");
                        }
                        break;
                    case "give":
                        if(args.size() != 5){
                            sender.sendMessage("参数错误");
                            return false;
                        }
                        Player player = Main.instance.getServer().getPlayer(args.get(2));
                        if(!player.isOnline()){
                            sender.sendMessage("玩家不在线");
                            return false;
                        }
                        if(Armour.giveArmour(player,args.get(3), Integer.parseInt(args.get(4)))){
                            if(sender.isPlayer()) sender.sendMessage("给予成功");
                        }else{
                            if(sender.isPlayer()) sender.sendMessage("给予失败");
                        }
                        break;
                    case "inlay":
                        item = ((Player)sender).getInventory().getItemInHand();
                        if(!item.isNull()){
                            if(Armour.isArmour(item)){
                                inlayForm.makeForm_one_armour(((Player) sender),item);
                            }else{
                                sender.sendMessage("请手持盔甲");
                                return false;
                            }
                        }else{
                            sender.sendMessage("未手持盔甲");
                            return false;
                        }
                        break;
                }
                break;
            case "stone":
                switch(args.get(1)){
                    case "help":
                        sender.sendMessage("/rpg stone add [Name] 创建一个名为Name的宝石配置");
                        sender.sendMessage("/rpg stone del [Name] 删除一个名为Name的宝石配置");
                        sender.sendMessage("/rpg stone give [Player] [Name] [Count] 给予玩家一定数量的宝石");
                        break;
                    case "add":
                        if(args.size() != 3){
                            sender.sendMessage("参数错误");
                            return false;
                        }
                        Item item = ((Player) sender).getInventory().getItemInHand();
                        int id,subid;
                        if(item.isNull()){
                            id = 388;
                            subid = 0;
                        }else{
                            id = item.getId();
                            subid = item.getDamage();
                        }
                        Config config;
                        if((config = Stone.addStoneConfig(args.get(2),id,subid)) != null){
                            Stone stone = Stone.loadStone(args.get(2),config);
                            Main.loadStone.put(args.get(2),stone);
                            sender.sendMessage("添加成功");
                        }else{
                            sender.sendMessage("添加失败");
                        }
                        break;
                    case "del":
                        if(args.size() != 3){
                            sender.sendMessage("参数错误");
                            return false;
                        }
                        if(Stone.delStoneConfig(args.get(2))){
                            Main.loadStone.remove(args.get(2));
                            sender.sendMessage("删除成功");
                        }else{
                            sender.sendMessage("删除失败");
                        }
                        break;
                    case "give":
                        if(args.size() != 5){
                            sender.sendMessage("参数错误");
                            return false;
                        }
                        Player player = Main.instance.getServer().getPlayer(args.get(2));
                        if(!player.isOnline()){
                            sender.sendMessage("玩家不在线");
                            return false;
                        }
                        if(Stone.giveStone(player,args.get(3), Integer.parseInt(args.get(4)))){
                            if(sender.isPlayer()) sender.sendMessage("给予成功");
                        }else{
                            if(sender.isPlayer()) sender.sendMessage("给予失败");
                        }
                        break;
                }
                break;
            case "magic":
                switch(args.get(1)){
                    case "help":
                        sender.sendMessage("/rpg magic add [Name] 创建一个名为Name的魔法物品配置");
                        sender.sendMessage("/rpg magic del [Name] 删除一个名为Name的魔法物品配置");
                        sender.sendMessage("/rpg magic give [Player] [Name] [Count] 给予玩家一定数量的魔法物品");
                        break;
                    case "add":
                        if(args.size() != 3){
                            sender.sendMessage("参数错误");
                            return false;
                        }
                        Item item = ((Player) sender).getInventory().getItemInHand();
                        int id,subid;
                        if(item.isNull()){
                            id = 260;
                            subid = 0;
                        }else{
                            id = item.getId();
                            subid = item.getDamage();
                        }
                        Config config;
                        if((config = Magic.addMagicConfig(args.get(2),id,subid)) != null){
                            Magic magic = Magic.loadMagic(args.get(2),config);
                            Main.loadMagic.put(args.get(2),magic);
                            sender.sendMessage("添加成功");
                        }else{
                            sender.sendMessage("添加失败");
                        }
                        break;
                    case "del":
                        if(args.size() != 3){
                            sender.sendMessage("参数错误");
                            return false;
                        }
                        if(Magic.delMagicConfig(args.get(2))){
                            Main.loadMagic.remove(args.get(2));
                            sender.sendMessage("删除成功");
                        }else{
                            sender.sendMessage("删除失败");
                        }
                        break;
                    case "give":
                        if(args.size() != 5){
                            sender.sendMessage("参数错误");
                            return false;
                        }
                        Player player = Main.instance.getServer().getPlayer(args.get(2));
                        if(!player.isOnline()){
                            sender.sendMessage("玩家不在线");
                            return false;
                        }
                        if(Magic.giveMagic(player,args.get(3), Integer.parseInt(args.get(4)))){
                            if(sender.isPlayer()) sender.sendMessage("给予成功");
                        }else{
                            if(sender.isPlayer()) sender.sendMessage("给予失败");
                        }
                        break;
                }
                break;
        }
        return false;
    }
}
