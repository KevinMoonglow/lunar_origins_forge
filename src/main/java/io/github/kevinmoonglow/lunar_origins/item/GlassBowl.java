package io.github.kevinmoonglow.lunar_origins.item;
import io.github.apace100.apoli.util.PowerGrantingItem;
import io.github.edwinmindcraft.apoli.common.registry.ApoliCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GlassBowl extends ArmorItem {


    // Water is in fabric's units because that's how it was originally balanced. We'll convert to millibuckets before writing out any forge fluid tags.
    public static final long MAX_WATER = 72000;
    public static final int EYE_LEVEL = (int) (MAX_WATER * 0.6);
    public static final int BASE_DRAIN_PER_TICK = 6;
    public static final int CRACK_LOSS_AMOUNT = 2;
    public static final int BREATHE_LEVEL = 20;
    public static final int FISHBOWL_LEVEL = (int) (MAX_WATER * 0.2);
    public static final float VIEW_LOOK_MIN_DELTA = 0.008f;
    public static final int MAX_SEALANT = 72000;
    public final int[] SEALANT_DRAIN_STEPS;

    protected String FULL_KEY = "item.lunar_origins.glass_bowl.full";


    public GlassBowl(ArmorMaterial pMaterial, Type pType, Properties pProperties, int[] sealantDrainSteps) {
        super(pMaterial, pType, pProperties);

        SEALANT_DRAIN_STEPS = sealantDrainSteps;
    }

    public String getFullKey() {
        return FULL_KEY;
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        if(stack.hasTag()) {
            CompoundTag nbt = stack.getTag();
            assert nbt != null;
            long waterLevel = nbt.getLong("waterLevel");
            if(waterLevel >= FISHBOWL_LEVEL) {
                return Component.translatable(getFullKey());
            }
        }
        return super.getName(stack);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {
            private final LazyOptional<IFluidHandlerItem> fluidHandler =
                    LazyOptional.of(() -> new GlassBowlFluidItemStack(stack, FluidType.BUCKET_VOLUME));
            private final LazyOptional<PowerGrantingItem> powerHandler =
                    LazyOptional.of(GlassBowlPowerProvider::new);
            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if(cap == ForgeCapabilities.FLUID_HANDLER_ITEM) {
                    return fluidHandler.cast();
                }
                else if(cap == ApoliCapabilities.POWER_GRANTING_ITEM) {
                    return powerHandler.cast();
                }
                else return LazyOptional.empty();
            }
        };
    }




    public void setStackWaterLevelUnits(ItemStack stack, long units) {
        CompoundTag nbt = stack.getOrCreateTag();
        if(units > MAX_WATER) units = MAX_WATER;
        nbt.putLong("waterLevel", units);
        nbt.putBoolean("hasWater", units > 0);

        CompoundTag fluidTag = new CompoundTag();
        FluidStack fluid = new FluidStack(Fluids.WATER, (int)units/72);
        fluid.writeToNBT(fluidTag);
        nbt.put(GlassBowlFluidItemStack.FLUID_NBT_KEY, fluidTag);
    }
    public long getStackWaterLevelUnits(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if(nbt == null) return 0;
        return nbt.getLong("waterLevel");
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, Player playerEntity, @NotNull InteractionHand hand) {
        ItemStack itemStack = playerEntity.getItemInHand(hand);

        LazyOptional<IFluidHandlerItem> optional = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        if(!optional.isPresent()) return super.use(world, playerEntity, hand);

        IFluidHandlerItem handler = optional.orElseThrow(IllegalStateException::new);
        FluidStack contents = handler.getFluidInTank(0);
        if(contents.getAmount() < handler.getTankCapacity(0)) {
            BlockHitResult hit = getPlayerPOVHitResult(world, playerEntity, ClipContext.Fluid.SOURCE_ONLY);
            BlockPos pos = hit.getBlockPos();
            Direction clickedFace = hit.getDirection();

            FluidActionResult scooped = FluidUtil.tryPickUpFluid(itemStack, playerEntity, world, pos, clickedFace);
            if (scooped.isSuccess()) {
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, scooped.result);
            }
        }
        return super.use(world, playerEntity, hand);
    }
    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        //if(world.isClientSide) return InteractionResult.SUCCESS;

        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);

        LazyOptional<IFluidHandlerItem> optional = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        if(!optional.isPresent()) return InteractionResult.PASS;

        Player player = context.getPlayer();
        if(player == null) return InteractionResult.FAIL;

        Direction clickedFace = context.getClickedFace();


        IFluidHandlerItem handler = optional.orElseThrow(IllegalStateException::new);
        FluidStack drained = handler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
        if(drained.getAmount() >= FluidType.BUCKET_VOLUME) {
            FluidActionResult placed = FluidUtil.tryPlaceFluid(player, world, context.getHand(), pos, stack, drained);
            if(!placed.isSuccess()) {
                BlockPos adjacent = pos.relative(clickedFace);
                placed = FluidUtil.tryPlaceFluid(player, world, context.getHand(), adjacent, stack, drained);
            }
            if(placed.isSuccess()) {
                player.setItemInHand(context.getHand(), placed.getResult());
                world.playSound(player, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        }

        FluidActionResult scooped = FluidUtil.tryPickUpFluid(stack, player, world, pos, clickedFace);
        if(!scooped.isSuccess()) {
            BlockPos adjacent = pos.relative(clickedFace);
            scooped = FluidUtil.tryPickUpFluid(stack, player, world, adjacent, clickedFace);
        }

        if(scooped.isSuccess()) {
            player.setItemInHand(context.getHand(), scooped.getResult());
            return InteractionResult.SUCCESS;
        }
        else return InteractionResult.PASS;
    }

    public boolean isCracked(ItemStack stack) {
        return (float) stack.getDamageValue() / stack.getMaxDamage() >= 0.5;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private static @Nullable Player getPlayer(UseOnContext context) {
        return context.getPlayer();
    }

    public boolean applySealant(ItemStack stack, int amount) {
        return true;
    }

    public boolean applySuperSealant(ItemStack stack) {
        return true;
    }
}

