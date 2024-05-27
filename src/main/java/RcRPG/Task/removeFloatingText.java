package RcRPG.Task;

import RcRPG.RcRPGMain;
import RcRPG.floatingtext.TextEntity;
import cn.nukkit.scheduler.PluginTask;

public class removeFloatingText extends PluginTask<RcRPGMain> {

    protected TextEntity floatingText;

    public removeFloatingText(RcRPGMain rcRPGMain, TextEntity floatingText){
        super(rcRPGMain);
        this.floatingText = floatingText;
    }

    @Override
    public void onRun(int i) {
        if(((TextEntity) floatingText).close){
            floatingText.kill();
        }
    }
}
