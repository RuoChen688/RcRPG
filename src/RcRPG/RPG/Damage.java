package RcRPG.RPG;

import RcRPG.Handle;
import RcRPG.Main;
import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.potion.Effect;

import java.util.LinkedHashMap;
import java.util.LinkedList;

public class Damage {

    public static LinkedHashMap<Player, LinkedList<Item>> playerMap = new LinkedHashMap<>();

    public static void getItem(Player player){
        Item item = player.getInventory().getItemInHand();
        LinkedList<Item> list = new LinkedList<>();
        if(!item.isNull() && Weapon.isWeapon(item)){
            list.add(item);
        }
        for(Item armour : player.getInventory().getArmorContents()){
            if(Armour.isArmour(armour)) list.add(armour);
        }
        Damage.playerMap.put(player,list);
    }

    public static int getDamage(Player damager, Entity entity){
        int damage = 0;

        if(entity instanceof Player){
            Player damaged = (Player) entity;
            Damage.getItem(damager);
            Damage.getItem(damaged);
            LinkedList<Item> damagerItem = Damage.playerMap.get(damager);
            for(Item item : damagerItem){
                if(Weapon.isWeapon(item)){
                    Weapon weapon = Main.loadWeapon.get(item.getNamedTag().getString("name"));
                    damage += weapon.getDamage();
                    if(weapon.getCritRound() != 0){
                        if(Handle.random(1,100) <= weapon.getCritRound()){
                            damage = (int) Math.round(damage * weapon.getCrit());
                        }
                    }
                }else if(Armour.isArmour(item)){
                    damage += Main.loadArmour.get(item.getNamedTag().getString("name")).getDamage();
                }
            }
            LinkedList<Item> damagedItem = Damage.playerMap.get(damaged);
            for(Item item : damagedItem){
                if(Weapon.isWeapon(item)){
                    Weapon weapon = Main.loadWeapon.get(item.getNamedTag().getString("name"));
                    if(weapon.getReDamageRound() != 0){
                        if(Handle.random(1,100) <= weapon.getReDamageRound()) damage -= weapon.getReDamage();
                    }
                }else if(Armour.isArmour(item)){
                    damage -= Main.loadArmour.get(item.getNamedTag().getString("name")).getReDamage();
                }
            }
        }else{
            Damage.getItem(damager);
            LinkedList<Item> damagerItem = Damage.playerMap.get(damager);
            for(Item item : damagerItem){
                if(Weapon.isWeapon(item)){
                    Weapon weapon = Main.loadWeapon.get(item.getNamedTag().getString("name"));
                    damage += weapon.getDamage();
                    if(weapon.getCritRound() != 0){
                        if(Handle.random(1,100) <= weapon.getCritRound()){
                            damage = (int) Math.round(damage * weapon.getCrit());
                        }
                    }
                }else if(Armour.isArmour(item)){
                    damage += Main.loadArmour.get(item.getNamedTag().getString("name")).getDamage();
                }
            }
        }

        if(damage < 0) damage = 0;

        return damage;
    }

    public static void onDamage(Player damager,Entity entity){
        Item item = damager.getInventory().getItemInHand();
        if(!item.isNull() && Weapon.isWeapon(item)){
            Weapon weapon = Main.loadWeapon.get(item.getNamedTag().getString("name"));
            if(weapon.getSuckRound() != 0){
                if(Handle.random(1,100) <= weapon.getSuckRound()){
                    if((damager.getMaxHealth() - damager.getHealth()) < weapon.getSuck()){
                        damager.setHealth(damager.getMaxHealth());
                    }else{
                        damager.setHealth(damager.getHealth()+weapon.getSuck());
                    }
                }
            }
            if(weapon.getGroupRound() != 0){
                if(Handle.random(1,100) <= weapon.getGroupRound()){
                    for(Player player:Damage.getPlayerAround(damager,5,true)){
                        if((player.getMaxHealth() - player.getHealth()) < weapon.getGroup()){
                            player.setHealth(player.getMaxHealth());
                        }else{
                            player.setHealth(player.getHealth()+weapon.getGroup());
                        }
                    }
                }
            }
            if(!weapon.getDamagerEffect().isEmpty()){
                for(Effect effect : weapon.getDamagerEffect()){
                    effect.add(damager);
                }
            }
            if(!weapon.getGroupEffect().isEmpty()){
                for(Player player:Damage.getPlayerAround(damager,5,true)){
                    for(Effect effect :weapon.getGroupEffect()){
                        effect.add(player);
                    }
                }
            }
            if(entity instanceof Player){
                if(weapon.getFireRound() != 0){
                    if(Handle.random(1,100) <= weapon.getFireRound()){
                        entity.setOnFire(weapon.getFire());
                    }
                }
                if(weapon.getLightRound() != 0){
                    if(Handle.random(1,100) <= weapon.getLightRound()){
                        Damage.light(entity,weapon.getLighting());
                    }
                }
                if(!weapon.getDamagedEffect().isEmpty()){
                    for(Effect effect : weapon.getDamagedEffect()){
                        effect.add(entity);
                    }
                }
            }
        }
    }

    public static void light(Entity entity,double damage){
        AddEntityPacket pk = new AddEntityPacket();

        pk.type = 93;

        pk.x = (float) entity.x;
        pk.y = (float) entity.y;
        pk.z = (float) entity.z;

        for(Player player : entity.level.getPlayers().values()){
            player.dataPacket(pk);
        }

        EntityDamageEvent ev = new EntityDamageEvent(entity, EntityDamageEvent.DamageCause.LIGHTNING, (float) damage);
        entity.attack(ev);
    }

    public static LinkedList<Player> getPlayerAround(Entity entity,double dis,boolean flag){
        LinkedList<Player> list = new LinkedList<>();
        for(Player player : entity.level.getPlayers().values()){
            if(entity.distance(player) <= dis) list.add(player);
        }
        if(flag) list.add((Player) entity);
        return list;
    }

    public static Vector3 go(double yaw,double pitch,int go)
    {
        yaw = yaw % 360;
        double x = 0.0;
        double z = 0.0;
        if (0.0 <= yaw && yaw <= 90.0) { //第二象限
            x = -go * (yaw / 90.0);
            z = go * ((90.0 - yaw) / 90.0);
        } else if (90.0 < yaw && yaw <= 180.0) { //第三象限
            yaw = yaw % 90.0;
            x = -go * ((90.0 - yaw) / 90.0);
            z = -go * (yaw / 90.0);
        } else if (180.0 < yaw && yaw <= 270.0) { //第四象限
            yaw = yaw % 90.0;
            x = go * (yaw / 90.0);
            z = -go * ((90.0 - yaw) / 90.0);
        } else if (270.0 < yaw && yaw <= 360.0) { //第一象限
            yaw = yaw % 90.0;
            x = go * ((90.0 - yaw) / 90.0);
            z = go * (yaw / 90.0);
        }
        double y = 0.0;
        if (pitch < 0.0) { //向上
            pitch = -1.0 * pitch;
            y = go * (pitch / 90.0);
        }
        if (pitch > 0.0) { //向下
            pitch = -1.0 * pitch;
            y = go * (pitch / 90.0);
        }
        return new Vector3(x, y, z);
    }

}
