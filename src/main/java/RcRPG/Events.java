package RcRPG;

import RcNPC.NPC.RcNPC;
import RcRPG.AttrManager.LittleMonsterAttr;
import RcRPG.AttrManager.Manager;
import RcRPG.AttrManager.PlayerAttr;
import RcRPG.AttrManager.RcNPCAttr;
import RcRPG.Form.guildForm;
import RcRPG.RPG.*;
import RcRPG.Society.Money;
import RcRPG.Society.Prefix;
import RcRPG.Society.Shop;
import RcRPG.floatingtext.TextEntity;
import RcRPG.guild.Guild;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockSignPost;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityRegainHealthEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseData;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;
import com.smallaswater.littlemonster.entity.IEntity;
import com.smallaswater.npc.entitys.EntityRsNPC;
import healthapi.PlayerHealth;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import static RcRPG.Handle.getProbabilisticResults;

public class Events implements Listener {

    public static LinkedHashMap<Player,String> playerShop = new LinkedHashMap<>();

    public static LinkedHashMap<Player,Boolean> playerMessage = new LinkedHashMap<>();

    public static final boolean hasHealthAPI = Server.getInstance().getPluginManager().getPlugin("HealthAPI") != null;
    public static final boolean hasRsNPC = Server.getInstance().getPluginManager().getPlugin("RsNPC") != null;
    public static final boolean hasLittleMonster = Server.getInstance().getPluginManager().getPlugin("LittleMonster") != null;
    public static final boolean hasRcEntity = Server.getInstance().getPluginManager().getPlugin("RcEntity") != null;
    public static final boolean hasRcNPC = Server.getInstance().getPluginManager().getPlugin("RcNPC") != null;
    public static final List<String> ProjectileWeapons = Arrays.asList("minecraft:bow", "minecraft:crossbow", "minecraft:trident");

    public Events(){
    }

    @EventHandler
    public void interact(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Item item = event.getItem();

        if (event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR && Ornament.isOrnament(item)) {
            player.sendActionBar(RcRPGMain.getI18n().tr(player.getLanguageCode(), "rcrpg.event.item_consume"));
            event.setCancelled();
        }

        Block block = event.getBlock();
        if (item != null && !item.isNull() && Handle.getShopByPos(block) == null) {
            if (Magic.isMagic(item)) Magic.useMagic(player, item);
            else if (Box.isBox(item)) Box.useBox(player, item);
        }
        if(block instanceof BlockSignPost && Events.playerShop.containsKey(player)){
            Config config = Shop.addShopConfig(Events.playerShop.get(player),block.x + ":" + block.y + ":" + block.z + ":" +block.level.getName());
            Shop.loadShop(Events.playerShop.get(player),config);
            Events.playerShop.remove(player);
            player.sendMessage("创建成功");
        }
        if (Handle.getShopByPos(block) != null) {
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
            RcRPGMain.loadShop.remove(shop.getName());
            player.sendMessage("拆除成功");
        }
    }

    @EventHandler
    public void onHeld(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        Item item = event.getItem();
        if (item.isNull() || !Weapon.isWeapon(item)) {
            return;
        }
        Weapon weapon = RcRPGMain.loadWeapon.get(item.getNamedTag().getString("name"));

        if (weapon == null) return;// TODO: 可能要做无效装备的清除？

        if (Level.enable ? Level.getLevel(player) < weapon.getLevel() : player.getExperienceLevel() < weapon.getLevel()) {
            player.sendMessage(RcRPGMain.getI18n().tr(player.getLanguageCode(), "rcrpg.events.insufficient_level_for_weapon"));
            event.setCancelled();
        }
    }

    @EventHandler
    public void onFormResponded(PlayerFormRespondedEvent event){
        Player player = event.getPlayer();
        if(!event.wasClosed()){
            switch (event.getFormID()) {
                case 10000 -> {
                    FormResponseCustom response7 = (FormResponseCustom) event.getResponse();
                    FormResponseData response8 = response7.getDropdownResponse(0);
                    if (response8.getElementID() != 0) {
                        Prefix.setPrefix(player, response8.getElementContent());
                    }
                }
                case 20000 -> {
                    FormResponseSimple response9 = (FormResponseSimple) event.getResponse();
                    if (response9.getClickedButtonId() == 0) guildForm.make_join(player);
                    else guildForm.make_create(player);
                }
                case 20001 -> {
                    FormResponseCustom response21 = (FormResponseCustom) event.getResponse();
                    String guild = response21.getDropdownResponse(0).getElementContent();
                    if (Guild.getMaxSize(guild) > Guild.getSize(guild)) {
                        Guild.appGuild(player, guild);
                        guildForm.a_s(player);
                    } else {
                        guildForm.a_f(player);
                    }
                }
                case 20002 -> {
                    FormResponseCustom response10 = (FormResponseCustom) event.getResponse();
                    if (Handle.getGuilds().contains(response10.getInputResponse(1))) {
                        guildForm.create_failed(player);
                    } else {
                        if (Money.getMoney(player) < RcRPGMain.getInstance().config.getInt("公会创建初始资金")) {
                            guildForm.create_failed(player);
                        } else {
                            Guild.addGuild(player, response10.getInputResponse(1));
                            guildForm.make_one(player);
                        }
                    }
                }
                case 20003, 20009, 20010, 20015, 20016, 20018, 20019, 20021 -> {
                    FormResponseSimple response11 = (FormResponseSimple) event.getResponse();
                    if (response11.getClickedButtonId() == 0) guildForm.make_one(player);
                }
                case 20004 -> {
                    FormResponseSimple response12 = (FormResponseSimple) event.getResponse();
                    if (response12.getClickedButtonId() == 0) guildForm.make_offer(player);
                    if (response12.getClickedButtonId() == 1) guildForm.make_member(player);
                    if (response12.getClickedButtonId() == 2) Guild.tpBase(player);
                    if (response12.getClickedButtonId() == 3) guildForm.make_update(player);
                    if (response12.getClickedButtonId() == 4) guildForm.make_app(player);
                    if (response12.getClickedButtonId() == 5) guildForm.make_assistant(player);
                    if (response12.getClickedButtonId() == 6) Guild.setBase(player);
                    if (response12.getClickedButtonId() == 7) guildForm.make_dismiss(player);
                }
                case 20005 -> {
                    FormResponseCustom response13 = (FormResponseCustom) event.getResponse();
                    if (Money.getMoney(player) < response13.getSliderResponse(0)) guildForm.offer_f(player);
                    else {
                        Money.delMoney(player, (int) response13.getSliderResponse(0));
                        Guild.addMoney(player, (int) response13.getSliderResponse(0));
                        guildForm.offer_s(player);
                    }
                }
                case 20006 -> {
                    if (Guild.isMaster(player) || Guild.isAssistantMaster(player)) {
                        FormResponseSimple response14 = (FormResponseSimple) event.getResponse();
                        if (Guild.isAssistantMaster(player) && response14.getClickedButton().getText().equals(Guild.getMaster(player)))
                            guildForm.member_f(player);
                        else if (response14.getClickedButton().getText().equals(player.getName())) {
                            if (!Guild.isMaster(player)) guildForm.member_m(player);
                            else guildForm.make_dismiss(player);
                        } else guildForm.member_s(player, response14.getClickedButton().getText());
                    }
                }
                case 20007 -> {
                    FormResponseSimple response15 = (FormResponseSimple) event.getResponse();
                    guildForm.app_s(player, response15.getClickedButton().getText());
                }
                case 20008 -> {
                    FormResponseSimple response16 = (FormResponseSimple) event.getResponse();
                    if (response16.getClickedButtonId() == 1) guildForm.make_one(player);
                    else Guild.dismissGuild(player);
                }
                case 20011 -> {
                    FormResponseSimple response17 = (FormResponseSimple) event.getResponse();
                    if (response17.getClickedButtonId() == 0)
                        Guild.kickGuild(player, response17.getClickedButton().getText());
                    else guildForm.make_member(player);
                }
                case 20012 -> {
                    FormResponseSimple response18 = (FormResponseSimple) event.getResponse();
                    if (response18.getClickedButtonId() == 0) guildForm.make_member(player);
                }
                case 20013 -> {
                    FormResponseSimple response19 = (FormResponseSimple) event.getResponse();
                    if (response19.getClickedButtonId() == 0) {
                        Guild.kickGuild(player, player.getName());
                        guildForm.make_one(player);
                    } else if (response19.getClickedButtonId() == 1) {
                        guildForm.make_one(player);
                    }
                }
                case 20014 -> {
                    FormResponseSimple response20 = (FormResponseSimple) event.getResponse();
                    if (response20.getClickedButtonId() == 0) {
                        Guild.acceptApp(player, response20.getClickedButton().getText());
                        guildForm.make_one(player);
                    } else if (response20.getClickedButtonId() == 1) {
                        Guild.rejectApp(player, response20.getClickedButton().getText());
                        guildForm.make_one(player);
                    }
                }
                case 20017 -> {
                    FormResponseSimple response22 = (FormResponseSimple) event.getResponse();
                    if (response22.getClickedButtonId() == 0) {
                        if (Guild.getMoney(player) < Guild.getUpdateMoney(player)) {
                            guildForm.update_f(player);
                        } else {
                            Guild.delMoney(player, Guild.getUpdateMoney(player));
                            Guild.updateGuild(player);
                            guildForm.update_s(player);
                        }
                    }
                }
                case 20020 -> {
                    FormResponseSimple response23 = (FormResponseSimple) event.getResponse();
                    guildForm.as_s(player, response23.getClickedButton().getText());
                    Guild.setAssistantMaster(Server.getInstance().getPlayer(response23.getClickedButton().getText()), Guild.getGuild(player));
                }
            }
        }
    }

    @EventHandler
    public void damageEvent(EntityDamageByEntityEvent event){
        Entity damager = event.getDamager();
        Entity wounded = event.getEntity();

        String damagerName = damager.getNameTag() != null ? damager.getNameTag() : damager.getName();
        String woundedName = wounded.getNameTag() != null ? wounded.getNameTag() : wounded.getName();

        final boolean damagerIsPlayer = damager instanceof Player;
        final boolean woundedIsPlayer = wounded instanceof Player;

        int damage = 1;

        if (hasRsNPC && wounded instanceof EntityRsNPC) {// 特判RsNPC
            return;
        }

        if (damagerIsPlayer) {// 对远程武器，取消近战伤害
            // 判断手持是否为远程攻击装备
            if (ProjectileWeapons.contains(((Player) damager).getInventory().getItemInHand().getNamespaceId())) {
                // 判断伤害原因是否为普攻 ENTITY_ATTACK
                if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                    event.setDamage(1);
                    return;
                }
            }
        }

        Manager DAttr = new Manager();
        Manager WAttr = new Manager();

        if (damagerIsPlayer) {
            DAttr = PlayerAttr.getPlayerAttr((Player) damager);
        } else if (hasLittleMonster && damager instanceof IEntity) {
            DAttr = new LittleMonsterAttr(((IEntity) damager).getConfig());
//        } else if (hasRcEntity && damager instanceof BaseEntity) {
//            String name = ((BaseEntity) damager).getName();
//            if (RcEntity.Main.getInstance().loadEntity.containsKey(name)) {
//                DAttr = new RcEntityAttr(RcEntity.Main.getInstance().loadEntity.get(name).getMonsterAttrMap());
//            }
//        } else if (hasRcEntity && damager instanceof NPC) {
//            String name = ((NPC) damager).getName();
//            if (RcEntity.Main.getInstance().loadEntity.containsKey(name)) {
//                DAttr = new RcEntityAttr(RcEntity.Main.getInstance().loadEntity.get(name).getMonsterAttrMap());
//            }
        } else if (hasRcNPC && damager instanceof RcNPC) {
            DAttr = new RcNPCAttr(((RcNPC) damager).getConfig());
        }

        if (woundedIsPlayer) {
            WAttr = PlayerAttr.getPlayerAttr((Player) wounded);
        } else if (hasLittleMonster && wounded instanceof IEntity) {
            WAttr = new LittleMonsterAttr(((IEntity) wounded).getConfig());
//        } else if (hasRcEntity && wounded instanceof BaseEntity) {
//            String name = ((BaseEntity) wounded).getName();
//            if (RcEntity.Main.getInstance().loadEntity.containsKey(name)) {
//                WAttr = new RcEntityAttr(RcEntity.Main.getInstance().loadEntity.get(name).getMonsterAttrMap());
//            }
//        } else if (hasRcEntity && wounded instanceof NPC) {
//            String name = ((NPC) wounded).getName();
//            if (RcEntity.Main.getInstance().loadEntity.containsKey(name)) {
//                WAttr = new RcEntityAttr(RcEntity.Main.getInstance().loadEntity.get(name).getMonsterAttrMap());
//            }
        } else if (hasRcNPC && wounded instanceof RcNPC) {
            WAttr = new RcNPCAttr(((RcNPC) wounded).getConfig());
        }

        DAttr.updateComp();
        WAttr.updateComp();

        // 实体名字格式化
        if (damagerIsPlayer) {
            damagerName = damager.getName();
        } else {
            if (damagerName.contains("\n")) {
                damagerName = damagerName.split("\n")[0];
            }
        }

        if (woundedIsPlayer) {
            woundedName = wounded.getName();
        } else {
            if (damagerName.contains("\n")) {
                damagerName = damagerName.split("\n")[0];
            } else if (woundedName.contains(" §r")) {
                woundedName = woundedName.split(" §r")[0];
            }
        }

        // Debuff 处理
        if (damager.hasEffect(18) && DAttr.checkFloatArray(DAttr.getCritChance())) {// 虚弱每级减10%暴击率,暴击倍率减半
            Effect weaknessEffect = damager.getEffect(18);
            if (DAttr.critChance > 0) DAttr.critChance -= ((weaknessEffect.getAmplifier() + 1) * 0.1);
            DAttr.criticalStrikeMultiplier *= 0.5;
        }

        if (damager.hasEffect(19) && DAttr.checkFloatArray(DAttr.getLifestealChance())) {// 中毒每级减10%吸血率,吸血倍率x50%
            Effect poisonEffect = damager.getEffect(19);
            DAttr.lifestealChance -= ((poisonEffect.getAmplifier() + 1) * 0.1);
            DAttr.lifestealMultiplier *= 0.5;
        }

        // 闪避率处理
        float dodge = WAttr.dodgeChance > 0 ? WAttr.dodgeChance : 0;
        if (getProbabilisticResults(dodge - DAttr.hitChance)) {
            if (woundedIsPlayer) {
                wounded.getLevel().addSound(wounded, Sound.valueOf("GAME_PLAYER_ATTACK_NODAMAGE"));
                ((Player) wounded).sendMessage(RcRPGMain.getI18n().tr(((Player) wounded).getLanguageCode(), "rcrpg.events.dodge_message_you_evaded", damagerName));
            }
            if (damagerIsPlayer) {
                damager.getLevel().addSound(damager, Sound.valueOf("GAME_PLAYER_ATTACK_NODAMAGE"));
                ((Player) damager).sendMessage(RcRPGMain.getI18n().tr(((Player) damager).getLanguageCode(), "rcrpg.events.dodge_message_enemy_evaded", woundedName));
            }
            event.setCancelled(true);
            return;
        }

        // 生命上限处理
        if (hasHealthAPI && woundedIsPlayer) {// 若存在血量核心
            PlayerHealth playerHealth = PlayerHealth.getPlayerHealth((Player) wounded);
            int max = playerHealth.getMaxHealth();
            double h = playerHealth.getHealth();
            if (h > max) {
                playerHealth.setHealth(max);
            }
        }

        int finalDamage = 0;
        // 三大乘区: 攻击力 * 攻击加成 * 暴击倍率
        double atkValue;
        double atk;
        if (woundedIsPlayer) {
            atkValue = DAttr.pvpAttackPower;
            atk = Double.parseDouble(new DecimalFormat("#.##").format(atkValue * (1 + DAttr.pvpAttackMultiplier)));
        } else {
            atkValue = DAttr.pveAttackPower;
            atk = Double.parseDouble(new DecimalFormat("#.##").format(atkValue * (1 + DAttr.pveAttackMultiplier)));
        }
        double defensePenetrationValue = getProbabilisticResults(DAttr.defensePenetrationChance) ? DAttr.defensePenetrationValue : 0;
        double armorPenetrationValue = getProbabilisticResults(DAttr.armorPenetrationChance) ? DAttr.armorPenetrationValue : 0;
        WAttr.armorStrengthMultiplier = (float) (WAttr.armorStrengthMultiplier - armorPenetrationValue); // TODO: 未实验

        atk = atk * (1 - WAttr.armorStrengthMultiplier);

        double criticalStrikeMultiplier = 0;
        double crtDamage = 0;
        if (getProbabilisticResults(DAttr.critChance - WAttr.critDodgeChance)) {
            //crtDamage = DAttr.爆伤力;
            criticalStrikeMultiplier = DAttr.criticalStrikeMultiplier - WAttr.critResistance;
            if (criticalStrikeMultiplier > 0) {
                crtDamage += Double.parseDouble(new DecimalFormat("#.##").format(atkValue * criticalStrikeMultiplier));
            }
        }

        double lifeSteal = 0;
        double lifestealMultiplier = 0;
        if (getProbabilisticResults(DAttr.lifestealChance)) {
            lifestealMultiplier = DAttr.lifestealMultiplier - WAttr.lifestealResistance;
            if (lifestealMultiplier > 0) {
                //lifeSteal = Double.parseDouble(new DecimalFormat("#.##").format(DAttr.吸血力 + atk * lifestealMultiplier));
                lifeSteal = Double.parseDouble(new DecimalFormat("#.##").format(atk * lifestealMultiplier));
            }
        }

        atk = Double.parseDouble(new DecimalFormat("#.##").format(atk + crtDamage * (1 - WAttr.armorStrengthMultiplier)));

        //double 治疗力 = DAttr.治疗力;

        if (defensePenetrationValue <= atk) {
            atk -= defensePenetrationValue;
        } else {
            defensePenetrationValue = atk;
            atk = 1;
        }
        if (atk > 0) {
            damage = (int) (atk - (WAttr.defense * (1 + WAttr.defenseMultiplier)));
            if (damage < 1) {
                damage = 0;
            }
            damage += defensePenetrationValue;

            // 跳砍判断
            if (!damager.onGround) {
                double addDamage = Double.parseDouble(new DecimalFormat("#.#").format(damage * 0.1));// 跳砍增加0.1倍最终伤害，增益最多不超过3k
                damage += addDamage > 3000 ? 3000 : addDamage;
            }

            finalDamage = damage;
        }

        // TODO: 增加反伤属性
        /** 反伤处理
        if (getProbabilisticResults(WAttr.反伤率)) {
            let 反伤 = defineData(WAttr.反伤倍率 * FinalDamage) + defineData(WAttr.反伤力);
            let WHealth = Wounded.getHealth();
            if (isPlayer(WHealth)) {
                let PlayerHealth = RSHealthAPI.getPlayerHealth(Wounded);
                if (RSHealthAPI) {// 血量核心
                    WHealth = PlayerHealth.getHealth();
                }
            }
            // 反伤不超过受害者现有血量
            if (WHealth < 反伤) 反伤 = WHealth;
            if (isPlayer(Damager)) {
                let PlayerHealth = RSHealthAPI.getPlayerHealth(Damager);
                if (RSHealthAPI) {// 血量核心
                    let H = PlayerHealth.getHealth() - 反伤;
                    PlayerHealth.setHealth(H < 1 ? 0 : H);
                } else {
                    let h = Damager.getHealth() - 反伤;
                    Damager.setHealth(h < 1 ? 0 : h);
                }
                Damager.getLevel().addSound(Damager, Sound.valueOf("ARMOR_EQUIP_CHAIN"));
            }
        }
        if (治疗力 && isPlayer(Wounded)) {
            if (治疗力 > 0) {
                治疗力 = 0;
            }
            let WoundedHealth = Wounded.getHealth();
            let WoundedMaxHealth = Wounded.getMaxHealth();
            let PlayerHealth = RSHealthAPI.getPlayerHealth(Wounded);
            if (RSHealthAPI) {
                WoundedHealth = PlayerHealth.getHealth();
                WoundedMaxHealth = PlayerHealth.getMaxHealth();
            }
            let health = WoundedHealth - 治疗力;
            if (health > WoundedMaxHealth) {
                PlayerHealth.setHealth(WoundedMaxHealth);
            } else {
                PlayerHealth.setHealth(health);
            }
            FinalDamage = 治疗力;
            event.setDamage(0);
        }*/

        // 吸血处理
        if (lifeSteal > 0) {
            float dHealth = damager.getHealth();
            if (hasHealthAPI && damagerIsPlayer) {// 血量核心
                PlayerHealth playerHealth = PlayerHealth.getPlayerHealth((Player) damager);
                dHealth = (float) playerHealth.getHealth();
            }
            if (dHealth - finalDamage < 1) {// 这似乎不需要减去 finalDamage
                // 不处理吸血，因为这会导致血量陷入假死
            } else {
                float wHealth = wounded.getHealth();
                if (hasHealthAPI && woundedIsPlayer) {// 血量核心
                    PlayerHealth playerHealth = PlayerHealth.getPlayerHealth((Player) wounded);
                    wHealth = (float) playerHealth.getHealth();
                }
                // 吸血不超过受害者现有血量
                if (wHealth < lifeSteal) lifeSteal = wHealth;
                if (hasHealthAPI && damagerIsPlayer) {// 若存在血量核心
                    PlayerHealth playerHealth = PlayerHealth.getPlayerHealth((Player) damager);
                    int MaxH = playerHealth.getMaxHealth();
                    double H = playerHealth.getHealth() + lifeSteal;
                    playerHealth.setHealth(H > MaxH ? MaxH : H);
                } else {
                    damager.heal(new EntityRegainHealthEvent(damager, (float) lifeSteal, RegainHealthEnum.LifeSteal.getCode()));
                }
                if (damagerIsPlayer) {
                    ((Player) damager).sendMessage(RcRPGMain.getI18n().tr(((Player) damager).getLanguageCode(), "rcrpg.events.life_steal_message", lifeSteal));
                }
            }
        }

        //if (!治疗力 && finalDamage < 0) {// 没有治疗力但是 伤害为负 设置为0
        //    finalDamage = 0;
        //}

        event.setDamage(finalDamage);
        for(EntityDamageEvent.DamageModifier key : EntityDamageEvent.DamageModifier.values()){
            if(key == EntityDamageEvent.DamageModifier.BASE) continue;
            if(event.isApplicable(key)) event.setDamage(0,key);
        }

        if (damager instanceof Player) {
            // 燃烧、冰冻、雷击 效果处理
            Damage.onDamage((Player) damager, wounded);

            if (crtDamage > 0) {
                ((Player) damager).sendMessage(RcRPGMain.getI18n().tr(((Player) damager).getLanguageCode(), "rcrpg.events.critical_damage_message", woundedName, crtDamage));
            }

            // 击杀提示
            if(finalDamage >= wounded.getHealth() && wounded instanceof Player) {
                finalDamage = (int) wounded.getHealth();
                Item item = ((Player) damager).getInventory().getItemInHand();
                if (!item.isNull() && Weapon.isWeapon(item)) {
                    Weapon weapon = RcRPGMain.loadWeapon.get(item.getNamedTag().getString("name"));
                    if (!weapon.getKillMessage().equals("")) {
                        String text = weapon.getKillMessage();
                        if(text.contains("@damager"))  text = text.replace("@damager", damagerName);
                        if(text.contains("@player"))  text = text.replace("@player", woundedName);
                        if(text.contains("@name"))  text = text.replace("@name", weapon.getShowName());
                        Server.getInstance().broadcastMessage(text);
                    }
                }
            }

            // 伤害 浮空字
            //Vector3 go = Damage.go(damager.yaw, damager.pitch,2);
            TextEntity floatingText = TextEntity.send(wounded, "§c-"+finalDamage);
            //RcRPGMain.getInstance().getServer().getScheduler().scheduleDelayedTask(new removeFloatingText(RcRPGMain.getInstance(), floatingText),15);
        }
    }
    @EventHandler
    public void toggleSprintEvent(PlayerToggleSprintEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        PlayerAttr attr = PlayerAttr.getPlayerAttr(player);
        if (attr == null) return;
        float speedAddition = attr.movementSpeedMultiplier;// 处理移速加成
        //TODO: if (speedAddition > 0) player.sendMovementSpeed(speedAddition);
    }

    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent event) {
        if (event.getRegainReason() != EntityRegainHealthEvent.CAUSE_EATING) return;
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player)) return;
        PlayerAttr manager = PlayerAttr.getPlayerAttr(player);
        if (manager != null) {
            int amount = (int) manager.hpPerNature;
            if (amount < 1) return;
            player.heal(new EntityRegainHealthEvent(player, amount, RegainHealthEnum.HpPerNature.getCode()));
        }
    }

    @EventHandler
    public void chatEvent(PlayerChatEvent event){
        if (RcRPGMain.getInstance().disableChatStyle) return;
        Player player = event.getPlayer();
        String name = player.getName();
        String message = event.getMessage();
        event.setCancelled();
        String text = RcRPGMain.getInstance().config.getString("聊天显示");
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
    public void joinEvent(PlayerPreLoginEvent event){
        Player player = event.getPlayer();
        String name = player.getName();

        PlayerAttr.setPlayerAttr(player);

        File file = new File(RcRPGMain.getInstance().getDataFolder()+"/Players/"+name+".yml");
        if(!file.exists()){
            RcRPGMain.getInstance().saveResource("Player.yml","/Players/"+name+".yml",false);
            Config config = new Config(RcRPGMain.getInstance().getPlayerFile()+"/"+name+".yml");
            config.set("名称",name);
            config.set("公会", RcRPGMain.getInstance().config.getString("初始公会"));
            config.set("称号", RcRPGMain.getInstance().config.getString("初始称号"));
            ArrayList<String> list = (ArrayList<String>) config.getStringList("称号列表");
            list.add(RcRPGMain.getInstance().config.getString("初始称号"));
            config.set("称号列表",list);
            config.save();
        }
        if (RcRPGMain.getInstance().config.exists("顶部显示") && !RcRPGMain.getInstance().config.getString("顶部显示").equals("")) {
            String text = RcRPGMain.getInstance().config.getString("顶部显示");
            if (text.contains("@name")) text = text.replace("@name", player.getName());
            if (text.contains("@hp")) text = text.replace("@hp", String.valueOf(player.getHealth()));
            if (text.contains("@maxhp")) text = text.replace("@maxhp", String.valueOf(player.getMaxHealth()));
            if (text.contains("@exp")) text = text.replace("@exp", String.valueOf(Level.getExp(player)));
            if (text.contains("@maxexp")) text = text.replace("@maxexp", String.valueOf(Level.getMaxExp(player)));
            if (text.contains("@level")) text = text.replace("@level", String.valueOf(Level.getLevel(player)));
            if (text.contains("@prefix"))
                text = text.replace("@prefix", String.valueOf(Handle.getPlayerConfig(name).getString("称号")));
            if (text.contains("@guild"))
                text = text.replace("@guild", String.valueOf(Handle.getPlayerConfig(player.getName()).getString("公会")));
            player.setNameTag(text);
            player.setNameTagVisible();
            player.setNameTagAlwaysVisible();
        }
    }

    @EventHandler
    public void deathEvent(PlayerDeathEvent event) {
        RcRPGMain.getInstance().getLogger().info(event.getDeathMessage().getText());
//        if ("death.attack.mob".equals(event.getTranslationDeathMessage().getText())) {
//            if (event.getTranslationDeathMessage().getParameter(1).isEmpty()) {
//                return;
//            }
//            event.getTranslationDeathMessage().setParameter(1, event.getTranslationDeathMessage().getParameter(1).split("\n")[0]+"§r§f");
//        } else if ("death.attack.arrow".equals(event.getTranslationDeathMessage().getText())) {
//            if (event.getTranslationDeathMessage().getParameter(1).isEmpty()) {
//                return;
//            }
//            event.getTranslationDeathMessage().setParameter(1, event.getTranslationDeathMessage().getParameter(1).split("\n")[0]+"§r§f");
//        }
    }
}

