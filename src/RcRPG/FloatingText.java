package RcRPG;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.AddPlayerPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class FloatingText {

    public Location location;

    public String text;

    public long id;
    
    public boolean invisible;

    public List<Player> players = new ArrayList<>();

    public FloatingText(Location location, String text) {
        this.text = text;
        this.location = location;
        this.id = Entity.entityCount ++;
    }

    public boolean isInvisible(){
        return this.invisible;
    }

    public Position getPos(){
        return this.location;
    }

    public void spawn(Player player){

        AddPlayerPacket pk = new AddPlayerPacket();
        pk.entityRuntimeId = id;
        pk.entityUniqueId = id;
        pk.uuid = UUID.randomUUID();
        pk.x = (float) this.location.x;
        pk.y = (float) this.location.y;
        pk.z = (float) this.location.z;
        pk.speedX = 0;
        pk.speedY = 0;
        pk.speedZ = 0;
        pk.yaw = 0;
        pk.pitch = 0;
        long flags = (
                (1L << Entity.DATA_FLAG_IMMOBILE)
        );
        pk.metadata = new EntityMetadata()
                .putLong(Entity.DATA_FLAGS, flags)
                .putLong(Entity.DATA_LEAD_HOLDER_EID,-1)
                .putFloat(Entity.DATA_SCALE, 0.0001f);
        pk.username = text;

        player.dataPacket(pk);
        if(!players.contains(player)) players.add(player);

    }

    public void spawnToAll(){
        if(players.isEmpty()){
            players.addAll(this.location.level.getPlayers().values());
        }
        for(int i = 0;i < players.size();i++){
            spawn(players.get(i));
        }
    }

    public String getText(){
        return this.text;
    }

    public void setText(String text){
        this.text = text;
        this.spawnToAll();
    }

    public void respawn(Player player){

        RemoveEntityPacket pk = new RemoveEntityPacket();
        pk.eid = this.id;

        player.dataPacket(pk);

    }

    public void respawnAll(){
        Iterator<Player> it = players.iterator();
        while(it.hasNext()){
            respawn(it.next());
            it.remove();
        }
    }

}
