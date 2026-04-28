package dev.redengdev.density;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.positions.distancefunctions.DistanceFunction;
import com.hypixel.hytale.builtin.hytalegenerator.pipe.Pipe;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.math.vector.Vector3d;

import dev.redengdev.BranchingNodePlugin;


public class BranchingNodeDensity extends Density {

    //Density Input Variables
    private final PositionProvider positions;
    private final Density pointNoiseValues;
    private final PathType pathType;
    private final DistanceFunction distanceFunction;
    private final double maxDistance;

    //Density Calc Variables
    private final double maxDistanceSqrd;
    private Vector3d minBounds;
    private Vector3d maxBounds;
    private Vector3d localPoint;
    private double minDistance;
    private List<Vector3d> allLocalCells;
    private Map<Integer, Double> localCells;
    private Map<Integer, Double> neighborCells;
    private Map<Integer, Double> pathCells;
    private HashSet<String> generatedPaths;

    //PathType Variables
    //(This will all be put in a different class)
    private Context cellContext;
    private int minIndex;
    private double minValue;
    private int rMinIndex;
    private double rMinValue;
    private double compareValue;

    public BranchingNodeDensity(PositionProvider positions, Density pointNoiseValues, String pathType, DistanceFunction distanceFunction, double maxDistance) {
        if (maxDistance < (double)0.0F)
            throw new IllegalArgumentException("negative distance");
        //Import Variables
        this.positions = positions;
        this.pointNoiseValues = pointNoiseValues;
        this.distanceFunction = distanceFunction;
        this.maxDistance = maxDistance;
        switch (pathType.toLowerCase()) {
            case "all":
                this.pathType = PathType.ALL;
                break;
            case "min":
                this.pathType = PathType.MIN;
                break;
            case "restrictedmin":
                this.pathType = PathType.RESTRICTED_MIN;
                break;
            default:
                throw new IllegalArgumentException("invalid path type selected");
        }
        //Basic Variable Setup
        maxDistanceSqrd = maxDistance * maxDistance;
        this.minBounds = new Vector3d();
        this.maxBounds = new Vector3d();
        this.localPoint = new Vector3d();
        this.minDistance = 0;
        this.allLocalCells = new ArrayList<>();
        this.localCells = new HashMap<>();
        this.neighborCells = new HashMap<>();
        this.pathCells = new HashMap<>();
        this.generatedPaths = new HashSet<>();
        this.cellContext = new Context();
    }

    @Override
    public double process(@Nonnull Density.Context context) {
        //Setup Variables for Processing
        this.minBounds.assign(context.position).subtract(this.maxDistance * 2);
        this.maxBounds.assign(context.position).add(this.maxDistance * 2);
        this.minDistance = Double.MAX_VALUE;
        this.allLocalCells.clear();
        this.localCells.clear();
        this.localPoint.assign((double)0.0F, (double)0.0F, (double)0.0F);

        //Find all Close Points and append to List
        Pipe.One<Vector3d> positionsPipe = (providedPoint, control) -> {
            allLocalCells.add(new Vector3d().assign(providedPoint));
            this.localPoint.x = providedPoint.x - context.position.x;
            this.localPoint.y = providedPoint.y - context.position.y;
            this.localPoint.z = providedPoint.z - context.position.z;
            double newDistance = this.distanceFunction.getDistance(this.localPoint);
            if (!(this.maxDistanceSqrd < newDistance)) {
                //Valid Close Point
                localCells.put(allLocalCells.size() - 1, newDistance);
                //Standard Cellular Distance Output
                if (newDistance < this.minDistance) {
                    this.minDistance = newDistance;
                }
            }
        };

        //Run Calculations
        PositionProvider.Context positionsContext = new PositionProvider.Context();
        positionsContext.bounds.min.assign(this.minBounds);
        positionsContext.bounds.max.assign(this.maxBounds);
        positionsContext.pipe = positionsPipe;
        this.positions.generate(positionsContext);

        //BranchingNodePlugin.LOGGER.atInfo().log("Test1: " + allLocalCells.size() + ", " + localCells.size());

        generatedPaths.clear();
        cellContext.assign(context);

        //Find Positions per Close Positions to create Paths Towards
        localCells.forEach((cellIndex, cellDist) -> {
            this.neighborCells.clear();
            this.localPoint.assign((double)0.0F, (double)0.0F, (double)0.0F);

            //Find all Close Points and append to List
            for (int i = 0; i < allLocalCells.size(); i++) {
                if (i != cellIndex) {
                    this.localPoint.assign(allLocalCells.get(i));
                    this.localPoint.subtract(allLocalCells.get(cellIndex));
                    double newDistance = this.distanceFunction.getDistance(this.localPoint);
                    if (!(this.maxDistanceSqrd < newDistance)) {
                        //Valid Close Point
                        this.neighborCells.put(i, newDistance);
                    }
                }
            }

            //Find which ones to Path to
            //(These each will be put into a different class. This will also support enums for the AssetEditor!)
            if (neighborCells.size() > 0) {
                switch (pathType) {
                    case ALL:
                        pathCells = neighborCells;
                        break;
                    case MIN:
                        pathCells.clear();
                        minIndex = -1;
                        minValue = Double.MAX_VALUE;
                        neighborCells.forEach((neighborIndex, cellToCellDist) -> {
                            cellContext.position = allLocalCells.get(neighborIndex);
                            double newValue = pointNoiseValues.process(cellContext);
                            //Find minimum
                            if (newValue < minValue) {
                                minValue = newValue;
                                minIndex = neighborIndex;
                            }
                        });
                        pathCells.put(minIndex, neighborCells.get(minIndex));
                        break;
                    case RESTRICTED_MIN:
                        pathCells.clear();
                        minIndex = -1;
                        minValue = Double.MAX_VALUE;
                        rMinIndex = -1;
                        rMinValue = Double.MAX_VALUE;
                        cellContext.position = allLocalCells.get(cellIndex);
                        compareValue = pointNoiseValues.process(cellContext);
                        neighborCells.forEach((neighborIndex, cellToCellDist) -> {
                            cellContext.position = allLocalCells.get(neighborIndex);
                            double newValue = pointNoiseValues.process(cellContext);
                            //Find backup minimum
                            if (newValue < minValue) {
                                minValue = newValue;
                                minIndex = neighborIndex;
                            }
                            //Find minimum if value is greater than center cell
                            if (newValue > compareValue && newValue < rMinValue) {
                                rMinValue = newValue;
                                rMinIndex = neighborIndex;
                            }
                        });
                        if (rMinIndex == -1)
                            pathCells.put(minIndex, neighborCells.get(minIndex));
                        else
                            pathCells.put(rMinIndex, neighborCells.get(rMinIndex));
                        break;
                }
            }

            //Find Minimum Dist from Context Point to Connected Cells
            pathCells.forEach((neighborIndex, cellToCellDist) -> {
                //Check if path has been generated before
                String path = Math.min(cellIndex, neighborIndex) + " " + Math.max(cellIndex, neighborIndex);
                if (!generatedPaths.contains(path)) {
                    generatedPaths.add(path);
                    //Check if cell is close to Context Point
                    if (localCells.containsKey(neighborIndex)) {
                        //Check if point is between the 2 cells using all pre-calculated values
                        //(Could also do dot-product for better precision)
                        if (Math.max(cellDist, localCells.get(neighborIndex)) < cellToCellDist) {
                            //Only doing the 2D distance for now, 3D will be later...
                            double newDistance = distanceToLine2D(context.position, allLocalCells.get(cellIndex), allLocalCells.get(neighborIndex));
                            //New Minimum based on Paths
                            if (newDistance < this.minDistance) {
                                this.minDistance = newDistance;
                            }
                        }
                    }
                }
            });
        });

        return Math.sqrt(this.minDistance);
    }
    
    //2D Distance Calculater of p to the line between p1 and p2 (Results is squared)
    //https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
    private double distanceToLine2D(Vector3d p, Vector3d p1, Vector3d p2) {
        //Only does eucludian for now which may conflict if user sets the selection to Manhattan
        return Math.pow((p2.z - p1.z) * p.x - (p2.x - p1.x) * p.z + p2.x * p1.z - p2.z * p1.x, 2) /
            (Math.pow(p2.z - p1.z, 2) + Math.pow(p2.x - p1.x, 2));
    }

    //Valid Pathing Values
    private enum PathType {
        ALL, MIN, RESTRICTED_MIN
    }
}
