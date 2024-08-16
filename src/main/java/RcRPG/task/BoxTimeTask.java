package RcRPG.task;

import RcRPG.RcRPGMain;
import RcRPG.PlayerStatus;
import RcRPG.RPG.Box;
import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;

public class BoxTimeTask extends PluginTask<RcRPGMain> {
    public BoxTimeTask(RcRPGMain rcRPGMain){
        super(rcRPGMain);
    }

    @Override
    public void onRun(int i) {
        if(PlayerStatus.playerBox.isEmpty()) return;
        for(Player player : PlayerStatus.playerBox.keySet()){
            if(PlayerStatus.playerBox.get(player).isEmpty()){
                PlayerStatus.playerBox.remove(player);
                continue;
            }
            for(Box box : PlayerStatus.playerBox.get(player).keySet()){
                if(PlayerStatus.playerBox.get(player).get(box).isEmpty()){
                    PlayerStatus.playerBox.get(player).remove(box);
                    continue;
                }
                for(int j = 0;j < PlayerStatus.playerBox.get(player).get(box).size();j++){
                    int time = PlayerStatus.playerBox.get(player).get(box).get(j);
                    if(time == 0){
                        PlayerStatus.playerBox.get(player).get(box).remove(j);
                        Box.gainPlayer(player,box);
                    }else{
                        PlayerStatus.playerBox.get(player).get(box).set(j,time-1);
                    }
                }
            }
        }
    }
}