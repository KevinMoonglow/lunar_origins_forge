package io.github.kevinmoonglow.lunar_origins.item;

import io.github.edwinmindcraft.apoli.api.IDynamicFeatureConfiguration;
import io.github.edwinmindcraft.apoli.api.component.IPowerContainer;
import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredPower;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.kevinmoonglow.lunar_origins.LunarOrigins;
import io.github.kevinmoonglow.lunar_origins.entity.GlowItemEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = LunarOrigins.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GlowItem extends Item {
    public GlowItem(Properties pProperties) {
        super(pProperties);
    }

    @SubscribeEvent
    public static void collected(EntityItemPickupEvent event) {
        Entity entity = event.getEntity();

        if(entity.level().isClientSide) return;

        if(entity instanceof ServerPlayer player) {
            ItemEntity itemEntity = event.getItem();
            ItemStack stack = itemEntity.getItem();
            Item item = stack.getItem();
            if(item instanceof GlowItem) {
                event.setCanceled(true);

                LazyOptional<IPowerContainer> optional = IPowerContainer.get(player);
                if(!optional.isPresent()) return;

                IPowerContainer powers = optional.resolve().orElseThrow();
                if(!powers.hasPower(ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "wopol/glow")))
                    return;

                @Nullable Holder<ConfiguredPower<IDynamicFeatureConfiguration, PowerFactory<IDynamicFeatureConfiguration>>> glowHolder = powers.getPower(ResourceLocation.fromNamespaceAndPath(LunarOrigins.MOD_ID, "wopol/glow"));

                if(glowHolder == null) return;

                ConfiguredPower<?, ?> glow = glowHolder.get();
                //ResourceConfiguration powerConf = (ResourceConfiguration) glow.getConfiguration();

                OptionalInt configuredMax = glow.getMaximum(player);
                int max = configuredMax.orElse(100);
                int maxTake = (max - glow.getValue(player).orElse(0))/10;
                int actualTake = Math.min(maxTake, stack.getCount());

                if(actualTake > 0) {
                    stack.shrink(actualTake);
                    glow.change(player, 10 * actualTake);

                    player.take(itemEntity, actualTake);

                    if(stack.getCount() <= 0)
                        itemEntity.discard();
                }
            }
        }

    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.translatable("item.lunar_origins.glow.tooltip.1")
                .withStyle(ChatFormatting.GRAY));
        pTooltipComponents.add(Component.translatable("item.lunar_origins.glow.tooltip.2")
                .withStyle(ChatFormatting.GRAY));

    }
    /*
    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        // Replace vanilla item entity with our custom one
        if (!(entity instanceof GlowItemEntity) && !entity.level().isClientSide) {
            GlowItemEntity flat = new GlowItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), stack);
            flat.setPickUpDelay(0);
            if(entity.getOwner() != null)
                flat.setThrower(entity.getOwner().getUUID());
            entity.discard();
            entity.level().addFreshEntity(flat);
            return true;
        }
        return false;
    }*/

    @Override
    public @Nullable Entity createEntity(Level level, Entity entity, ItemStack stack) {
        if(entity instanceof ItemEntity itemEntity) {
            if (!(itemEntity instanceof GlowItemEntity)) {
                GlowItemEntity glowItemEntity = new GlowItemEntity(itemEntity.level(), itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), stack);
                glowItemEntity.setPickUpDelay(0);
                if (itemEntity.getOwner() != null)
                    glowItemEntity.setThrower(itemEntity.getOwner().getUUID());
                return glowItemEntity;
            }
        }
        return null;
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

}
