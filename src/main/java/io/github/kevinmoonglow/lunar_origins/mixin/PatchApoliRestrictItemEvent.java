package io.github.kevinmoonglow.lunar_origins.mixin;

import io.github.edwinmindcraft.apoli.common.ApoliPowerEventHandler;
import io.github.kevinmoonglow.lunar_origins.item.GlassBowl;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ApoliPowerEventHandler.class)
public class PatchApoliRestrictItemEvent {
    @Inject(method = "preventItemUsage(Lnet/minecraftforge/event/entity/player/PlayerInteractEvent$RightClickItem;)V",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraftforge/event/entity/player/PlayerInteractEvent$RightClickItem.getItemStack() Lnet/minecraft/world/item/ItemStack;"
            ),
            cancellable = true,
            remap = false
    )
    private static void fixArmorItemsWithRightClickBehavior(PlayerInteractEvent.RightClickItem event, CallbackInfo ci) {
        // Forge Apoli bans all right click actions with restricted armor. This is a difference from Fabric's behavior.
        // We need the right click functionality on glass bowls and their subclasses when it doesn't result in the armor
        // being equipped, even when they're restricted as armor by a power, so we force the interaction to be allowed
        // if it's a Glass Bowl type. We can check for the armor restriction manually in the Glass Bowl code later.
        // Note that this is injected after the check for Restrict Item Usage, so that power can still ban right click
        // actions with the Glass Bowl.
        if(event.getItemStack().getItem() instanceof GlassBowl)
            ci.cancel();
    }
    @Inject(method = "preventBlockInteraction(Lnet/minecraftforge/event/entity/player/PlayerInteractEvent$RightClickBlock;)V",
            at = @At(
                value = "INVOKE",
                target = "net/minecraftforge/event/entity/player/PlayerInteractEvent$RightClickBlock.getItemStack ()Lnet/minecraft/world/item/ItemStack;"
            ),
            cancellable = true,
            remap = false
    )
    private static void fixArmorItemsWithRightClickAgainstBlockBehavior(PlayerInteractEvent.RightClickBlock event, CallbackInfo ci) {
        // As explained above with block interactions.
        if(event.getItemStack().getItem() instanceof GlassBowl)
            ci.cancel();
    }

}
