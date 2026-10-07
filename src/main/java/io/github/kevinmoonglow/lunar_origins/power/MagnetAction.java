package io.github.kevinmoonglow.lunar_origins.power;

import io.github.edwinmindcraft.apoli.api.power.configuration.ConfiguredItemCondition;
import io.github.edwinmindcraft.apoli.api.power.factory.EntityAction;
import io.github.kevinmoonglow.lunar_origins.power.configuration.MagnetConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.function.Predicate;

public class MagnetAction extends EntityAction<MagnetConfiguration> {

    protected MagnetAction() {
        super(MagnetConfiguration.CODEC);
    }

    @Override
    public void execute(MagnetConfiguration configuration, Entity entity) {
        if(entity instanceof Player player)
            doMagnet(player, configuration.radius(), configuration.bypassDelay(),
                    stack -> ConfiguredItemCondition.check(configuration.itemCondition(), entity.level(), stack));
    }

    public static void doMagnet(Player player, float radius, boolean bypassDelay, Predicate<ItemStack> itemCondition) {
        Level level = player.level();

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class,
                player.getBoundingBox().inflate(radius), item -> !item.isRemoved());
        
        for(ItemEntity item : items) {
            if(bypassDelay) item.setNoPickUpDelay();
            ItemStack stack = item.getItem();
            boolean itemCheck = itemCondition.test(stack);
            if (itemCheck) {
                item.playerTouch(player);
            }
        }
    }
}
