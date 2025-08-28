package io.github.kevinmoonglow.lunar_origins.power;

import io.github.edwinmindcraft.apoli.api.configuration.NoConfiguration;
import io.github.edwinmindcraft.apoli.api.power.factory.PowerFactory;

public class CrawlingPower extends PowerFactory<NoConfiguration> {

    protected CrawlingPower() {
        super(NoConfiguration.CODEC);
    }
}
