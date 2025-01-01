package RcRPG;

import RcRPG.AttrManager.PlayerAttr;
import RcRPG.RPG.Level;
import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.lang.LangCode;
import com.smallaswater.littlemonster.events.entity.LittleMonsterEntityDeathDropExpEvent;

public class LittleMonsterEvents implements Listener {
    @EventHandler
    public void dropExpEvent(LittleMonsterEntityDeathDropExpEvent event) {
        if (event.getEventDamageEvent().isCancelled()) return;

        Entity damager = null;
        EntityDamageEvent d = event.getEventDamageEvent();
        if (d instanceof EntityDamageByEntityEvent) {
            damager = ((EntityDamageByEntityEvent) d).getDamager();
        }
        if (damager == null) return;

        if (!damager.isPlayer) return;

        Player player = (Player) damager;
        PlayerAttr attr = PlayerAttr.getPlayerAttr(player);
        if (attr == null) return;

        if (attr.experienceGainMultiplier <= 0) return;

        int addition = (int) (attr.experienceGainMultiplier * event.getOriginExp());

        if (Level.enable) {
            Level.addExp(player, event.getTotalExp() + addition);
            String expLabel = player.getLanguageCode().equals(LangCode.zh_CN) ? "经验" : "exp";
            if (addition != 0) {
                player.sendActionBar(expLabel + " +" + event.getTotalExp() + "§a(" + addition + ")");
            } else {
                player.sendActionBar(expLabel + " +" + event.getTotalExp());
            }
            event.setStoredExp("rcrpg-exp", -event.getTotalExp());
        } else {
            event.setStoredExp("rcrpg", addition);
        }
    }

}
