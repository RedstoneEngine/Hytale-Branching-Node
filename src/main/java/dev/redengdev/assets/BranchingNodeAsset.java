package dev.redengdev.assets;

import javax.annotation.Nonnull;

import com.hypixel.hytale.builtin.hytalegenerator.assets.density.ConstantDensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.positions.distancefunctions.DistanceFunctionAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.positions.distancefunctions.EuclideanDistanceFunctionAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.ListPositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.positionproviders.PositionProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.positions.distancefunctions.DistanceFunction;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import dev.redengdev.density.BranchingNodeDensity;


public class BranchingNodeAsset extends DensityAsset {

    //Node Inputs
    @Nonnull
    public static final BuilderCodec<BranchingNodeAsset> CODEC = BuilderCodec.builder(
        BranchingNodeAsset.class, BranchingNodeAsset::new, DensityAsset.ABSTRACT_CODEC
    )
    .append(new KeyedCodec<>("Positions", PositionProviderAsset.CODEC, true), (asset, v) -> asset.positions = v, (asset) -> asset.positions)
    .add()
    .append(new KeyedCodec<>("PointNoiseValues", DensityAsset.CODEC, false), (asset, v) -> asset.pointNoiseValues = v, (asset) -> asset.pointNoiseValues)
    .add()
    .append(new KeyedCodec<>("PathType", Codec.STRING), (asset, v) -> asset.pathType = v, (asset) -> asset.pathType)
    .add()
    .append(new KeyedCodec<>("DistanceFunction", DistanceFunctionAsset.CODEC, true), (asset, v) -> asset.distanceFunction = v, (asset) -> asset.distanceFunction)
    .add()
    .append(new KeyedCodec<>("MaxDistance", Codec.DOUBLE, true), (asset, v) -> asset.maxDistance = v, (asset) -> asset.maxDistance)
    .add()
    .build();

    //Node Variables
    private PositionProviderAsset positions = new ListPositionProviderAsset();
    private DensityAsset pointNoiseValues = new ConstantDensityAsset();
    private String pathType = "All";
    private DistanceFunctionAsset distanceFunction = new EuclideanDistanceFunctionAsset();
    private double maxDistance = (double)10.0F;

    BranchingNodeAsset() {
    }

    //NodeAsset -> Density
    @Nonnull
    public Density build(@Nonnull DensityAsset.Argument argument) {
        if (this.isSkipped()) {
            return new ConstantValueDensity((double)0.0F);
        } else {
            PositionProvider positions = this.positions.build(new PositionProviderAsset.Argument(argument.parentSeed, argument.referenceBundle, argument.workerId));
            Density pointNoiseValues = this.pointNoiseValues.build(argument);
            DistanceFunction distanceFunction = this.distanceFunction.build(argument.parentSeed, this.maxDistance);
            return new BranchingNodeDensity(positions, pointNoiseValues, this.pathType, distanceFunction, this.maxDistance);
        }
    }
    
}
