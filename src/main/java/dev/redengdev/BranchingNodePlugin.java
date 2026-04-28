package dev.redengdev;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import dev.redengdev.assets.BranchingNodeAsset;

import javax.annotation.Nonnull;

public class BranchingNodePlugin extends JavaPlugin {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public BranchingNodePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        //this.getCommandRegistry().registerCommand(new ExampleCommand("example", "An example command"));
        //this.getEventRegistry().registerGlobal(PlayerReadyEvent.class, ExampleEvent::onPlayerReady);

        //Hook up new Density Asset
        getCodecRegistry(DensityAsset.CODEC)
            .register("BranchingNode", BranchingNodeAsset.class, BranchingNodeAsset.CODEC);
    }
}