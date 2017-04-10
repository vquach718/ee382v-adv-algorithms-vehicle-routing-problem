package vhr.RuinAndRecreateAlgorithm;

import vhr.RuinAndRecreateAlgorithm.InitializeSolution.GenerateClusteringInitialSolutionStrategy;
import vhr.RuinAndRecreateAlgorithm.InitializeSolution.IGenerateInitialSolutionStrategy;
import vhr.RuinAndRecreateAlgorithm.Recreate.GreedyInsertionStrategy;
import vhr.RuinAndRecreateAlgorithm.Recreate.IRecreateStrategy;
import vhr.RuinAndRecreateAlgorithm.Ruin.IRuinStrategy;
import vhr.RuinAndRecreateAlgorithm.Ruin.RandomRuinStrategy;
import vhr.SolutionAcceptor.SimulationAnnealingSolutionAcceptor;
import vhr.core.*;
import vhr.utils.DataSetReader;

/**
 * Created by dinhvan5481 on 3/28/17.
 */
public class Main {
    public static void main(String[] args) {
        String fileName = "./data/A-VRP/A-n37-k6.vrp";
        String routeSolutionFileName = "./solutions/A-VRP/A-n37-k6.csv";
        String logCostFileName = "./solutions/A-VRP/A-n37-k6-cost.csv";
        long randomSeed = 0;
        IDataExtract dataExtract = new DataSetReader();
        IDistanceCalculator distanceCalulator = new Euclid2DDistanceCalculator();
        ICostCalculator costCalculator = new CVRPCostCalculator(distanceCalulator);
        VRPInstance cvrpInstance = null;
        try {
            cvrpInstance = new VRPInstance.Builder(dataExtract)
                    .setDataFileName(fileName)
                    .setCostCalculator(costCalculator)
                    .setDistanceCalculator(distanceCalulator)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        GenerateClusteringInitialSolutionStrategy generateInitialSolution = new GenerateClusteringInitialSolutionStrategy(distanceCalculator, costCalculator);
//        cvrpInstance.toCSV(fileName.replace(".vrp", ".csv"));
//        VRPSolution vrpSolution = generateInitialSolution.generateSolution(cvrpInstance);
//        vrpSolution.toCSV(routeSolutionFileName);
        IGenerateInitialSolutionStrategy generateInitialSolutionStrategy =
                new GenerateClusteringInitialSolutionStrategy
                        .Builder(randomSeed).build();
        IRuinStrategy ruinStrategy = new RandomRuinStrategy.Builder(randomSeed).build();
        IRecreateStrategy recreateStrategy = new GreedyInsertionStrategy.Builder(cvrpInstance, costCalculator, distanceCalulator).build();
        int maxRun = 40000;

        // TODO: need to work on intial temp
        IVRPAlgorithm ruinAndRecreateAlg = new RuinAndRecreateAlgorithm.Builder(costCalculator, distanceCalulator)
                .setInitializeSolutionStrategy(generateInitialSolutionStrategy)
                .setRuinStrategy(ruinStrategy)
                .setRecreateStrategy(recreateStrategy)
                .setSolutionAcceptor(new SimulationAnnealingSolutionAcceptor())
                .setMaxRun(maxRun)
                .setLogSolution(logCostFileName)
                .build();
        VRPSolution result = null;
        try {
            result = ruinAndRecreateAlg.solve(cvrpInstance);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        result.toCSV(routeSolutionFileName);
    }
}
