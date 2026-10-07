package io.github.kevinmoonglow.lunar_origins.item;
import io.github.apace100.apoli.util.PowerGrantingItem;
import io.github.edwinmindcraft.apoli.common.power.RestrictArmorPower;
import io.github.edwinmindcraft.apoli.common.registry.ApoliCapabilities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
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

import java.util.List;

public class GlassBowl extends ArmorItem {


    /** The maximum amount of water to fill the bowl */
    public static final long MAX_WATER = 72000;
    /** How many water units convert into 1 bucket */
    public static final int WATER_PER_BUCKET = 72000;
    /** The number of Fluid units needed to equal 1 of the mod platform's fluid units. In this case,
     * Forge Millibuckets.
     */
    public static final int WATER_PER_LOCAL_UNIT = WATER_PER_BUCKET / FluidType.BUCKET_VOLUME;
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
                    LazyOptional.of(() -> new GlassBowlFluidItemStack(stack,
                            (int) (MAX_WATER/WATER_PER_BUCKET * FluidType.BUCKET_VOLUME)));
            private final LazyOptional<PowerGrantingItem> powerHandler =
                    LazyOptional.of(GlassBowlPowerProvider::new);
            @Override
            public @NotNull <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
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
        setStackWaterLevelUnits(nbt, units);
    }
    public void setStackWaterLevelUnits(CompoundTag nbt, long units) {
        if(units > MAX_WATER) units = MAX_WATER;

        CompoundTag fluidTag = new CompoundTag();
        FluidStack fluid = new FluidStack(Fluids.WATER, (int)(units/WATER_PER_LOCAL_UNIT));
        fluid.writeToNBT(fluidTag);
        nbt.put(GlassBowlFluidItemStack.FLUID_NBT_KEY, fluidTag);

        nbt.putLong("waterLevel", units);
        nbt.putBoolean("hasWater", units > 0);
    }
    public long getStackWaterLevelUnits(ItemStack stack) {
        CompoundTag nbt = stack.getTag();
        if(nbt == null) return 0;
        return getStackWaterLevelUnits(nbt);
    }
    public long getStackWaterLevelUnits(CompoundTag nbt) {
        return nbt.getLong("waterLevel");
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level world, Player playerEntity, InteractionHand hand) {
        ItemStack itemStack = playerEntity.getItemInHand(hand).copy();

        LazyOptional<IFluidHandlerItem> optional = itemStack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM);
        if(!optional.isPresent()) return super.use(world, playerEntity, hand);

        IFluidHandlerItem handler = optional.orElseThrow(IllegalStateException::new);
        FluidStack contents = handler.getFluidInTank(0);
        if(contents.getAmount() < handler.getTankCapacity(0)) {
            BlockHitResult hit = getPlayerPOVHitResult(world, playerEntity, ClipContext.Fluid.SOURCE_ONLY);
            BlockPos pos = hit.getBlockPos();
            Direction clickedFace = hit.getDirection();

            int diff = handler.getTankCapacity(0) - contents.getAmount();
            if(diff < FluidType.BUCKET_VOLUME)
                handler.drain(FluidType.BUCKET_VOLUME - diff, IFluidHandler.FluidAction.EXECUTE);

            FluidActionResult scooped = FluidUtil.tryPickUpFluid(itemStack, playerEntity, world, pos, clickedFace);
            if (scooped.isSuccess()) {
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, scooped.result);
            }
        }
        // We're bypassing Apoli's armor restriction elsewhere with a mixin, so we have to manually enforce it
        // here ourselves.
        if(RestrictArmorPower.isForbidden(playerEntity, this.getEquipmentSlot(), itemStack)) {
            return new InteractionResultHolder<>(InteractionResult.FAIL, itemStack);
        }
        return super.use(world, playerEntity, hand);
    }
    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        //if(world.isClientSide) return InteractionResult.SUCCESS;

        ItemStack stack = context.getItemInHand().copy();
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

        FluidStack contents = handler.getFluidInTank(0);
        if(contents.getAmount() < handler.getTankCapacity(0)) {
            int diff = handler.getTankCapacity(0) - contents.getAmount();
            if (diff < FluidType.BUCKET_VOLUME)
                handler.drain(FluidType.BUCKET_VOLUME - diff, IFluidHandler.FluidAction.EXECUTE);

            FluidActionResult scooped = FluidUtil.tryPickUpFluid(stack, player, world, pos, clickedFace);
            if (!scooped.isSuccess()) {
                BlockPos adjacent = pos.relative(clickedFace);
                scooped = FluidUtil.tryPickUpFluid(stack, player, world, adjacent, clickedFace);
            }

            if (scooped.isSuccess()) {
                player.setItemInHand(context.getHand(), scooped.getResult());
                return InteractionResult.SUCCESS;
            }
        }
        // We're bypassing Apoli's armor restriction elsewhere with a mixin, so we have to manually enforce it
        // here ourselves.
        if(RestrictArmorPower.isForbidden(player, this.getEquipmentSlot(), stack)) {
            return InteractionResult.FAIL;
        }

        return InteractionResult.PASS;
    }

    public boolean isCracked(ItemStack stack) {
        return (float) stack.getDamageValue() / stack.getMaxDamage() >= 0.5;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if(entity.isAlive() && slot == EquipmentSlot.HEAD.getIndex()) {
            LivingEntity p = (LivingEntity)entity;

            long waterLevel = 0;
            int sealant = 0;
            boolean superSealant = false;

            CompoundTag nbt = null;
            ItemStack headStack = p.getItemBySlot(EquipmentSlot.HEAD);

            if(stack.hasTag()) {
                nbt = stack.getTag();
                assert nbt != null;
                waterLevel = nbt.getLong("waterLevel");
                sealant = nbt.getInt("bowlSealant");
                superSealant = nbt.getBoolean("bowlSuperSealant");
            }
            else if(entity.isUnderWater() && headStack == stack) {
                nbt = new CompoundTag();
                stack.setTag(nbt);
            }

            if(headStack == stack && nbt != null) {
                float sealantPercent = (float)sealant / MAX_SEALANT;

                int sealantLossStep = (int)Math.ceil(sealantPercent * (SEALANT_DRAIN_STEPS.length - 1));
                int sealantLossAmount;

                if(superSealant) sealantLossAmount = 0;
                else sealantLossAmount = SEALANT_DRAIN_STEPS[sealantLossStep];

                boolean cracked = isCracked(stack);

                if(!p.isUnderWater()) {
                    if(waterLevel > 0) {
                        if(sealant > 0 && !superSealant) {
                            sealant -= 1;
                            nbt.putInt("bowlSealant", sealant);
                        }

                        waterLevel -= cracked ? BASE_DRAIN_PER_TICK * CRACK_LOSS_AMOUNT : 0;
                        waterLevel -= (long) BASE_DRAIN_PER_TICK * sealantLossAmount;
                        if(waterLevel < 0) waterLevel = 0;
                        setStackWaterLevelUnits(stack, waterLevel);
                    }
                }
                else {
                    if(waterLevel < MAX_WATER) {
                        if(sealant > 0 && !superSealant) {
                            sealant -= 1;
                            nbt.putInt("bowlSealant", sealant);
                        }

                        waterLevel += cracked ? (long) BASE_DRAIN_PER_TICK * sealantLossAmount : 0;
                        waterLevel += (long) BASE_DRAIN_PER_TICK * sealantLossAmount;
                        if(waterLevel > MAX_WATER) waterLevel = MAX_WATER;
                        setStackWaterLevelUnits(stack, waterLevel);
                    }
                }

                float viewLevelP = entity.getXRot() / 90f;
                float viewWaterScaling = viewLevelP > 0f ? viewLevelP * 2.0f + 1.0f : viewLevelP * 2.0f - 1.0f;

                float waterLevelP = (float) waterLevel / MAX_WATER;
                float eyeLevelP = (float) EYE_LEVEL / MAX_WATER;

                float waterLevelAdjusted;
                if(viewWaterScaling > 0)
                    waterLevelAdjusted = waterLevelP * viewWaterScaling;
                else
                    waterLevelAdjusted = 1 - ((1 - waterLevelP) * -viewWaterScaling);

                float waterDiff = waterLevelAdjusted - eyeLevelP;
                float waterDiffScaled = waterDiff > 0f ? waterDiff / (1.0f - eyeLevelP) : waterDiff / eyeLevelP;

                nbt.putFloat("waterViewHeight", waterDiffScaled);

                if(waterDiff > 0 - VIEW_LOOK_MIN_DELTA)
                    nbt.putBoolean("waterEyeLevel", true);
                else if(waterDiff < 0 + VIEW_LOOK_MIN_DELTA) {
                    nbt.putBoolean("waterEyeLevel", false);
                }
            }
        }
    }

    public boolean applySealant(ItemStack stack, int amount) {
        CompoundTag nbt = stack.getOrCreateTag();
        int sealant = nbt.getInt("bowlSealant");
        boolean superSealant = nbt.getBoolean("bowlSuperSealant");

        if(sealant >= MAX_SEALANT || superSealant) {
            return false;
        }

        sealant += amount;
        if(sealant > MAX_SEALANT)
            sealant = MAX_SEALANT;

        nbt.putInt("bowlSealant", sealant);
        return true;
    }
    public boolean applySuperSealant(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        boolean superSealant = nbt.getBoolean("bowlSuperSealant");

        if(superSealant) return false;

        nbt.putInt("bowlSealant", MAX_SEALANT);
        nbt.putBoolean("bowlSuperSealant", true);
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag pIsAdvanced) {
        super.appendHoverText(stack, level, components, pIsAdvanced);
        CompoundTag nbt = stack.getTag();
        if(nbt != null) {
            if (nbt.getBoolean("bowlSuperSealant"))
                components.add(Component.translatable("item.lunar_origins.glass_bowl.tooltip.super_seal").withStyle(ChatFormatting.GRAY));
            else if (nbt.getInt("bowlSealant") > 0)
                components.add(Component.translatable("item.lunar_origins.glass_bowl.tooltip.seal").withStyle(ChatFormatting.GRAY));
            else
                components.add(Component.translatable("item.lunar_origins.glass_bowl.tooltip.no_seal").withStyle(ChatFormatting.GRAY));
        }
        else components.add(Component.translatable("item.lunar_origins.glass_bowl.tooltip.no_seal").withStyle(ChatFormatting.GRAY));

        if(nbt != null) {
            boolean superSealant = nbt.getBoolean("bowlSuperSealant");
            int sealant = nbt.getInt("bowlSealant");
            int segments = (int)Math.floor((float)sealant / MAX_SEALANT * 5f);
            String s = "█".repeat(segments) + "░".repeat(5 - segments);
            MutableComponent t = Component.literal(s);

            if(sealant <= 0) t = t.withStyle(ChatFormatting.DARK_GRAY);
            else if(superSealant) t = t.withStyle(ChatFormatting.YELLOW);
            else t = t.withStyle(ChatFormatting.GREEN);
            components.add(t);
        }
    }

    /**
     * Transfer a given quantity of water from one bowl to another
     * @param source {@code ItemStack} of the source bowl.
     * @param sourceNbt NBT To use for the source stack. Fetched/generated from {@code source} if null.
     * @param target {@code ItemStack} of the target bowl.
     * @param targetNbt NBT To use for the source stack. Fetched/generated from {@code source} if null.
     * @param maxAmount Amount to transfer. Or as much as possible if null. Uses internal units, not Forge millbuckets.
     * @return The amount actually transferred (In internal units, not Forge Millibuckets)
     */
    private long pourToBowlStack(
            ItemStack source, @Nullable CompoundTag sourceNbt,
            ItemStack target, @Nullable CompoundTag targetNbt,
            @Nullable Long maxAmount) {
        long sourceWaterLevel = sourceNbt != null ? getStackWaterLevelUnits(sourceNbt) : getStackWaterLevelUnits(source);
        long targetWaterLevel = targetNbt != null ? getStackWaterLevelUnits(targetNbt) : getStackWaterLevelUnits(target);

        long maxTransfer = Math.max(MAX_WATER - targetWaterLevel, 0);
        long fullTransfer = Math.min(maxTransfer, sourceWaterLevel);
        long actualTransfer = maxAmount != null ? Math.min(maxAmount, fullTransfer) : fullTransfer;

        if(sourceNbt!=null) setStackWaterLevelUnits(sourceNbt, sourceWaterLevel - actualTransfer);
        else setStackWaterLevelUnits(source, sourceWaterLevel - actualTransfer);

        if(targetNbt!=null) setStackWaterLevelUnits(targetNbt, targetWaterLevel + actualTransfer);
        else setStackWaterLevelUnits(target, targetWaterLevel + actualTransfer);
        return actualTransfer;
    }
    private long pourToBowlStack(
            ItemStack source, ItemStack target, @SuppressWarnings("SameParameterValue") @Nullable Long maxAmount
    ) {
        CompoundTag sourceNbt = source.getTag();
        CompoundTag targetNbt = target.getTag();
        return pourToBowlStack(source, sourceNbt, target, targetNbt, maxAmount);
    }
    @SuppressWarnings("UnusedReturnValue")
    private long pourToBowlStack(
            ItemStack source, @Nullable CompoundTag sourceNbt,
            ItemStack target, @Nullable CompoundTag targetNbt) {
        return pourToBowlStack(source, sourceNbt, target, targetNbt, null);
    }
    private long pourToBowlStack(ItemStack source, ItemStack target) {
        return pourToBowlStack(source, target, null);
    }

    private boolean pourToContainer(ItemStack bowl, ItemStack container, Player player, Object targetSlot, Item containerItemType, Item filledItemType, long amount) {
        long waterLevel = getStackWaterLevelUnits(bowl);
        Item containerItem = container.getItem();
        if(containerItem == containerItemType) {
            if(waterLevel < amount) return true;

            ItemStack newContainer = new ItemStack(filledItemType);
            if(filledItemType instanceof PotionItem)
                PotionUtils.setPotion(newContainer, Potions.WATER);
            container.shrink(1);

            if(container.getCount() <= 0) {
                if(targetSlot instanceof Slot) ((Slot)targetSlot).set(newContainer);
                else if(targetSlot instanceof SlotAccess) ((SlotAccess)targetSlot).set(newContainer);
                else assert false;
            }
            else if(!player.getInventory().add(newContainer)) {
                player.drop(newContainer, false);
            }

            setStackWaterLevelUnits(bowl, waterLevel - amount);
            return true;
        }
        return false;
    }
    private boolean pourFromContainer(ItemStack bowl, ItemStack container, Player player, Object sourceSlot, Item containerItemType, Item filledItemType, long amount) {
        CompoundTag nbt = bowl.getTag();
        long waterLevel = nbt != null ? nbt.getLong("waterLevel") : 0;
        Item containerItem = container.getItem();
        if(containerItem == filledItemType) {
            if(waterLevel + amount > MAX_WATER) return true;
            if (filledItemType instanceof PotionItem && PotionUtils.getPotion(container) != Potions.WATER) {
                return false;
            }
            if(sourceSlot instanceof Slot) ((Slot)sourceSlot).set(new ItemStack(containerItemType));
            else if(sourceSlot instanceof SlotAccess) ((SlotAccess)sourceSlot).set(new ItemStack(containerItemType));
            else assert false;

            setStackWaterLevelUnits(bowl,waterLevel + amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
        if(action == ClickAction.SECONDARY && slot.allowModification(player)) {
            ItemStack target  = slot.getItem();
            Item targetItem = target.getItem();
            CompoundTag nbt = stack.getTag();
            long waterLevel = nbt != null ? getStackWaterLevelUnits(nbt) : 0;

            if(pourToContainer(stack, target, player, slot, Items.GLASS_BOTTLE, Items.POTION, WATER_PER_BUCKET/3))
                return true;
            else if(pourFromContainer(stack, target, player, slot, Items.GLASS_BOTTLE, Items.POTION, WATER_PER_BUCKET/3))
                return true;
            else if(pourToContainer(stack, target, player, slot, Items.BUCKET, Items.WATER_BUCKET, WATER_PER_BUCKET))
                return true;
            else if(pourFromContainer(stack, target, player, slot, Items.BUCKET, Items.WATER_BUCKET, WATER_PER_BUCKET))
                return true;
            else if(targetItem instanceof GlassBowl) {
                ItemStack headGear = player.getItemBySlot(EquipmentSlot.HEAD);

                // We can't pour into a bowl while wearing it, so abort the action. Return true here because the user
                // probably didn't want to swap the equipped items in this case, especially since this is a right click.
                // Left click still works if they did want to swap.
                if(headGear == target) return true;

                if(waterLevel > 0)
                    pourToBowlStack(stack, nbt, target, null);
                else
                    pourToBowlStack(target, null, stack, nbt);
                return true;
            }
        }
        return super.overrideStackedOnOther(stack, slot, action, player);
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if(action == ClickAction.SECONDARY && slot.allowModification(player)) {
            ItemStack headGear = player.getItemBySlot(EquipmentSlot.HEAD);
            if (headGear == stack) return true;

            if (pourToContainer(stack, other, player, access, Items.GLASS_BOTTLE, Items.POTION, WATER_PER_BUCKET / 3))
                return true;
            else if (pourFromContainer(stack, other, player, access, Items.GLASS_BOTTLE, Items.POTION, WATER_PER_BUCKET / 3))
                return true;
            else if (pourToContainer(stack, other, player, access, Items.BUCKET, Items.WATER_BUCKET, WATER_PER_BUCKET))
                return true;
            else if (pourFromContainer(stack, other, player, access, Items.BUCKET, Items.WATER_BUCKET, WATER_PER_BUCKET))
                return true;
        }
        return super.overrideOtherStackedOnMe(stack, other, slot, action, player, access);
    }

    @Override
    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        if(pPlayer.isUnderWater()) {
            setStackWaterLevelUnits(pStack, MAX_WATER);
        }
    }
}

