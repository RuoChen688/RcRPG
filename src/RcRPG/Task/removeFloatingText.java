package RcRPG.Task;

import RcRPG.FloatingText;
import RcRPG.Main;
import cn.nukkit.scheduler.PluginTask;

public class removeFloatingText extends PluginTask {

    protected FloatingText floatingText;

    public removeFloatingText(Main main, FloatingText floatingText){
        super(main);
        this.floatingText = floatingText;
    }

    @Override
    public void onRun(int i) {
        this.floatingText.respawnAll();
        this.cancel();
    }
}
