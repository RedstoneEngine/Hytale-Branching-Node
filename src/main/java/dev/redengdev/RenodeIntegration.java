package dev.redengdev;

import java.util.ArrayList;
import java.util.List;

import io.github.itsverday.renode.builder.NodeBuilder;
import io.github.itsverday.renode.builder.Renode;
import io.github.itsverday.renode.vanilla.HytaleGeneratorNodes;

public class RenodeIntegration {
    private static final List<NodeBuilder> nodes = new ArrayList<>();

    //Add Main Branching Node
    public static final NodeBuilder NODE_DENSITY_BRANCHING = addNode(HytaleGeneratorNodes.VARIANT_DENSITY.variantNode("BranchingDensity", "BranchingDensityNode", "Branching Density"))
        .addContent(HytaleGeneratorNodes.CONTENT_EXPORT_AS)
        .addContent(HytaleGeneratorNodes.CONTENT_SKIP)
        .addVariantOutput("Positions", "Positions", false, HytaleGeneratorNodes.VARIANT_POSITIONS)
        .addVariantOutput("PositionNoiseValues", "PositionNoiseValues", false, HytaleGeneratorNodes.VARIANT_DENSITY)
        .addContent(Renode.smallStringContent("PathType", "PathType").withDefaultValue("ALL").withWidth(125))
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
