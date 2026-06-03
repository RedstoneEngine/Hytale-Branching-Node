package dev.redengdev;

import java.util.ArrayList;
import java.util.List;

import dev.redengdev.density.BranchingNodeDensity;
import io.github.itsverday.renode.builder.NodeBuilder;
import io.github.itsverday.renode.builder.Renode;
import io.github.itsverday.renode.vanilla.HytaleGeneratorNodes;

public class RenodeIntegration {
    private static final List<NodeBuilder> nodes = new ArrayList<>();

    //Add Main Branching Node
    public static final NodeBuilder NODE_DENSITY_BRANCHING = addNode(HytaleGeneratorNodes.VARIANT_DENSITY.variantNode("BranchingDensity", "BranchingDensityNode", "Branching Density"))
        .withDescription("Outputs the distance to Branches created from Positions based on PathType")
        .addContent(HytaleGeneratorNodes.CONTENT_EXPORT_AS)
        .addContent(HytaleGeneratorNodes.CONTENT_SKIP)
        .addVariantOutput("Positions", "Positions", false, HytaleGeneratorNodes.VARIANT_POSITIONS)
        .addVariantOutput("PositionNoiseValues", "PositionNoiseValues", false, HytaleGeneratorNodes.VARIANT_DENSITY)
        .addContent(Renode.enumContent("PathType", "PathType").withEnumValues(BranchingNodeDensity.PathType.VALUES).withDefaultValue("ALL").withWidth(175).withDescription(
            "ALL - Creates a Branch between all Positions within the MaxDistance.\n" +
            "MIN - Creates a Branch from each Position to the Lowest PositionNoiseValue within its MaxDistance.\n" +
            "RESTRICTEDMIN - Special type of MIN that creates better interconnected Branches for use for Caves, etc."
        ))
        .addNodeOutput("DistanceFunction", "DistanceFunction", false, () -> HytaleGeneratorNodes.NODE_DENSITY_POSITIONS_CELL_NOISE_DISTANCE_FUNCTION)
        .addContent(Renode.floatContent("MaxDistance", "MaxDistance").withDefaultValue(10.0).withWidth(100))
        .addCategory(HytaleGeneratorNodes.CATEGORY_DENSITY);

    private static NodeBuilder addNode(NodeBuilder node) {
        nodes.add(node);
        return node;
    }

    public static void registerAllNodes() {
        for (NodeBuilder node: nodes) {
            Renode.registerNode(node);
        }
    }
}
