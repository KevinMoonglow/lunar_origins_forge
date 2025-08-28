package io.github.kevinmoonglow.lunar_origins.item;

import net.minecraft.world.item.ArmorMaterial;

public class AmethystGlassBowl extends GlassBowl {

    public AmethystGlassBowl(ArmorMaterial pMaterial, Type pType, Properties pProperties, int[] sealantDrainSteps) {
        super(pMaterial, pType, pProperties, sealantDrainSteps);
        FULL_KEY = "item.lunar_origins.amethyst_bowl.full";
    }
}
