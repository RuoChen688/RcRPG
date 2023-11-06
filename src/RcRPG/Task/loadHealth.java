package RcRPG.Task;

import RcRPG.Handle;
import RcRPG.Main;
import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;

public class loadHealth extends PluginTask {

    public loadHealth(Main main){
        super(main);
    }

    @Override
    public void onRun(int i) {
        for(Player player:Main.instance.getServer().getOnlinePlayers().values()){
            if(Handle.getPlayerConfig(player.getName()) == null) continue;
            if(player.getHealth() == player.getMaxHealth()) continue;

            if(player.getMaxHealth() != Handle.getMaxHealth(player)){
                player.setMaxHealth(Handle.getMaxHealth(player));
                if(player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());
            }
        }
    }
}
