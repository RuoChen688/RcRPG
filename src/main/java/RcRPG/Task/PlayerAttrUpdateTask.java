package RcRPG.Task;

import RcRPG.AttrManager.PlayerAttr;
import RcRPG.Events;
import RcRPG.RcRPGMain;
import cn.nukkit.Player;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.PluginTask;
import healthapi.PlayerHealth;

import java.util.Objects;

public class PlayerAttrUpdateTask extends PluginTask<RcRPGMain> {
    public PlayerAttrUpdateTask(RcRPGMain rcRPGMain){
        super(rcRPGMain);
    }

    @Override
    public void onRun(int i) {
        for (Player player : RcRPGMain.getInstance().getServer().getOnlinePlayers().values()) {
            if (!player.isAlive()) continue;
            int addHealth = 0;
            if (!PlayerAttr.playerlist.containsKey(player)) continue;
            Objects.requireNonNull(PlayerAttr.getPlayerAttr(player)).update();
            PlayerAttr pAttr = PlayerAttr.getPlayerAttr(player);
            if (pAttr == null) continue;

            pAttr.updateComp();

            int maxh = 20;// 默认血量上限
            if (Events.hasHealthAPI) {// 若存在血量核心
                maxh = 0;
            }

            if (pAttr.hp > 0) {
                maxh = (int) ((maxh + pAttr.hp) * (1 + pAttr.hpRegenMultiplier));
            } else {
                maxh = maxh * (1 + (int) pAttr.hpRegenMultiplier);
            }

            addHealth += pAttr.hpPerSecond;

            // TODO: 这是依赖于 AttrManager 的 Effect 实现
            /*
            effect = GetPlayerAttr(player, 1).Effect;
            const nowTime =  Number((new Date().getTime()/1000).toFixed(0))+0.5;
            for (let n in effect) {
                if (nowTime > effect[n].time) {
                    SetPlayerAttr(player, "Effect", {id: n, level: 0, time: 0});
                    continue;
                }
            }*/

            if (player.hasEffect(20)) {// 凋零每秒减 效果等级 x 1%最大生命
                Effect effect = player.getEffect(20);
                addHealth -= ((effect.getAmplifier() + 1) * Math.ceil(0.01 * maxh));
            }
            if (addHealth != 0) {
                player.heal(addHealth);
            }
            if (Events.hasHealthAPI) {// 若存在血量核心
                PlayerHealth playerHealth = PlayerHealth.getPlayerHealth(player);
                playerHealth.setMaxHealth("rcrpg", maxh);
            } else {
                player.setMaxHealth(maxh);
            }
        }
    }
}