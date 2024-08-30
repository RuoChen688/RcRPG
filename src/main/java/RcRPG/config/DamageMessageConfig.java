package RcRPG.config;

import cn.nukkit.utils.ConfigSection;
import lombok.Getter;

@Getter
public class DamageMessageConfig {
    boolean lifeSteal = true;
    boolean criticalDamage = true;
    boolean dodge = true;

    public DamageMessageConfig(ConfigSection enableDamageMessage) {
        if (enableDamageMessage == null) {
            return;
        }
        if (enableDamageMessage.exists("life_steal")) {
            lifeSteal = enableDamageMessage.getBoolean("life_steal");
        }
        if (enableDamageMessage.exists("critical_damage")) {
            criticalDamage = enableDamageMessage.getBoolean("critical_damage");
        }
        if (enableDamageMessage.exists("dodge")) {
            dodge = enableDamageMessage.getBoolean("dodge");
        }
    }
}
