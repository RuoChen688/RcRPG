package RcRPG.floatingtext;

import cn.nukkit.entity.Entity;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.Random;


public class TextEntity extends Entity {

    @Override
    public int getNetworkId() {
        return 64;
    }

    public boolean close = false;



    public TextEntity(Entity entity, CompoundTag nbt) {
        super(entity.chunk, nbt);
        this.motionX = entity.motionX;
        this.motionY = entity.motionY;
        this.motionZ = entity.motionZ;
        this.onGround = true;

    }


    @Override
    protected void initEntity() {
        super.initEntity();
        this.setMaxHealth(1);
        this.setHealth(1);
        this.setCanBeSavedWithChunk(false);
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
    }


    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        }
        //this.timing.startTiming();

        int tickDiff = currentTick - this.lastUpdate;
        if (tickDiff <= 0 && !this.justCreated) {
            return true;
        }
        this.lastUpdate = currentTick;

        boolean hasUpdate = this.entityBaseTick(tickDiff);

        if (this.isAlive()) {

            if (!this.isCollided) {
                this.motionY -= 0.03;
            }

            this.move(this.motionX, this.motionY, this.motionZ);

            if (!this.onGround || Math.abs(this.motionX) > 0.00001 || Math.abs(this.motionY) > 0.00001 || Math.abs(this.motionZ) > 0.00001) {
                double f = Math.sqrt((this.motionX * this.motionX) + (this.motionZ * this.motionZ));
                this.yaw = (Math.atan2(this.motionX, this.motionZ) * 180 / Math.PI);
                this.pitch = (Math.atan2(this.motionY, f) * 180 / Math.PI);
                hasUpdate = true;
            }

            this.updateMovement();
        }

        if (this.age > 180 || this.isCollided){
            this.kill();
            hasUpdate = false;
            close = true;
        }

        //this.timing.stopTiming();
        //close = true;
        return hasUpdate;
    }

    public static void send(Entity target, String text){
        int yaw = new Random().nextInt(360) + 1;
        double r = (double) yaw / 180;
        int pitch = new Random().nextInt(90) + 85;
        double g = (double) pitch / 180;
        CompoundTag tag = new CompoundTag();
        tag.putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("",target.x))
                .add(new DoubleTag("",target.y + 0.5))
                .add(new DoubleTag("",target.z)));
        tag.putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("", -Math.sin(r * Math.PI) * Math.cos(g * Math.PI)))
                .add(new DoubleTag("",0))
                .add(new DoubleTag("",Math.cos(r * Math.PI) * Math.cos(g * Math.PI))));
        tag.putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("",0))
                .add(new FloatTag("",0)));
        TextEntity entity = new TextEntity(target, tag);
        entity.setNameTag(text);
        entity.spawnToAll();
    }
}