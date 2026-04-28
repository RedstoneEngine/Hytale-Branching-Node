package dev.redengdev.test;

import javax.annotation.Nullable;

import com.hypixel.hytale.procedurallib.NoiseFunction;
import com.hypixel.hytale.procedurallib.logic.CellNoise.CellFunction;
import com.hypixel.hytale.procedurallib.logic.cell.CellDistanceFunction;
import com.hypixel.hytale.procedurallib.logic.cell.evaluator.PointEvaluator;
import com.hypixel.hytale.procedurallib.logic.point.PointConsumer;
import com.hypixel.hytale.procedurallib.property.NoiseProperty;

//This is not what is used anymore, was scrubbing through code and figuring things out

public class BranchingNode implements NoiseFunction {
    protected final CellDistanceFunction distanceFunction;
    protected final PointEvaluator pointEvaluator;
    protected final CellFunction cellFunction;
    @Nullable
    protected final NoiseProperty noiseLookup;

    public BranchingNode(CellDistanceFunction distanceFunction, PointEvaluator pointEvaluator, CellFunction cellFunction, @Nullable NoiseProperty noiseLookup) {
        this.distanceFunction = distanceFunction;
        this.pointEvaluator = pointEvaluator;
        this.cellFunction = cellFunction;
        this.noiseLookup = noiseLookup;
    }

    @Override
    public double get(int seed, int offsetSeed, double x, double y) {
        //For right now, just call 3D (Eventual Performance Improvements, Woo!)
        return get(seed, offsetSeed, x, y, 0);
    }

    @Override
    public double get(int seed, int offsetSeed, double x, double y, double z) {
        return 0;
    }
    
    public class PointsToEval implements PointConsumer<Double>
    {
        

        @Override
        public void accept(double cellCentreX, double cellCentreY, Double noiseValue) {

        }
        
    }
}
