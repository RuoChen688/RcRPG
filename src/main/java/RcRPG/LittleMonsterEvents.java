package RcRPG;

import RcRPG.AttrManager.PlayerAttr;
import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
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

        PlayerAttr attr = PlayerAttr.getPlayerAttr((Player)damager);
        if (attr == null) return;

        if (attr.experienceGainMultiplier <= 0) return;

        int addtion = (int) (attr.experienceGainMultiplier * event.getOriginExp());

        event.setStoredExp("rcrpg", addtion);
    }

}
