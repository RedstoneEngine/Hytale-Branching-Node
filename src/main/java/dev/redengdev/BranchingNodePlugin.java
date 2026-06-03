package dev.redengdev;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.common.semver.SemverRange;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.plugin.PluginManager;

import dev.redengdev.assets.BranchingNodeAsset;

import javax.annotation.Nonnull;

public class BranchingNodePlugin extends JavaPlugin {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public BranchingNodePlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    @Override
    protected void setup() {
        //Hook up new Density Asset
        getCodecRegistry(DensityAsset.CODEC)
            .register("BranchingDensity", BranchingNodeAsset.class, BranchingNodeAsset.CODEC);

        //Register Custom Nodes if Plugin Installed
        if (PluginManager.get().hasPlugin(PluginIdentifier.fromString("Verday:Renode"), SemverRange.fromString(">=0.8.0"))) {
            LOGGER.atInfo().log("Renode installed, applying to Asset Node Editor!");
            RenodeIntegration.registerAllNodes();
        }
    }
}