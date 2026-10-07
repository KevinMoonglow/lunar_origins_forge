package io.github.kevinmoonglow.lunar_origins.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GlowItemEntity extends ItemEntity {

    public GlowItemEntity(EntityType<? extends ItemEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }
    public GlowItemEntity(Level level, double posX, double posY, double posZ, ItemStack itemStack) {
        super(LunarOriginsEntities.GLOW_ITEM_ENTITY.get(), level);
        this.setItem(itemStack);
        this.setPos(posX, posY, posZ);
    }

    @Override
    public void tick() {
        super.tick();
        if(level().isClientSide) {
            if(this.tickCount % 8 == 0)
                level().addParticle(ParticleTypes.GLOW, getX(), getY(), getZ(), 0, 0, 0);
        }
    }

}
