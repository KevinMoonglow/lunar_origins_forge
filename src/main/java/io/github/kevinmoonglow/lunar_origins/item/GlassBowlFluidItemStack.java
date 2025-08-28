package io.github.kevinmoonglow.lunar_origins.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;

public class GlassBowlFluidItemStack extends FluidHandlerItemStack {
    /**
     * @param container The container itemStack, data is stored on it directly as NBT.
     * @param capacity  The maximum capacity of this fluid tank.
     */
    public GlassBowlFluidItemStack(@NotNull ItemStack container, int capacity) {
        super(container, capacity);
    }

    @Override
    public @NotNull FluidStack getFluid() {
        return super.getFluid();
    }


    @Override
    protected void setFluid(FluidStack fluid) {
        super.setFluid(fluid);
        CompoundTag nbt = container.getOrCreateTag();
        long waterLevel = nbt.getLong("waterLevel");
        waterLevel = (waterLevel % GlassBowl.WATER_PER_LOCAL_UNIT)  + (long) fluid.getAmount() * GlassBowl.WATER_PER_LOCAL_UNIT;

        nbt.putLong("waterLevel", waterLevel);
        nbt.putBoolean("hasWater", waterLevel > 0);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return stack.getFluid().isSame(Fluids.WATER);
    }

    @Override
    protected void setContainerToEmpty() {
        super.setContainerToEmpty();
        CompoundTag nbt = container.getTag();
        if(nbt != null) {
            nbt.putLong("waterLevel", 0);
            nbt.putBoolean("hasWater", false);
        }
    }
}
