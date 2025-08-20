package io.github.kevinmoonglow.lunar_origins.power;

import io.github.apace100.apoli.power.Prioritized;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;
import io.github.kevinmoonglow.lunar_origins.power.configuration.ModifyBreathingConfiguration;

public class ModifyBreathingPower extends PowerFactory<ModifyBreathingConfiguration> implements Prioritized<ModifyBreathingConfiguration, ModifyBreathingPower> {

    protected ModifyBreathingPower() {
        super(ModifyBreathingConfiguration.CODEC);
    }


    @Override
    public int getPriority(ModifyBreathingConfiguration modifyBreathingConfiguration) {
        return modifyBreathingConfiguration.priority();
    }
}

