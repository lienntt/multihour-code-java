/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccmp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author liem
 */
public class CCMP {

    static Topology topo;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // TODO code application logic here
        if (args.length != 1) {
            System.err.print("Tham so khong hop le: can có 1 tham so.");
            System.err.println(args.length);
            System.out.print("Tham so gom : onlineMPmode");
            System.exit(-1);
        }
        System.out.println("Tham so dau vao");
        //datafile
        System.out.println(args[0]);

        String datafile = "test/data.txt";
        String resultfile = "test/multihour.txt";
        String inputfile = "test/params.txt";
        String pathfile = "test/paths.txt";

        switch (args[0]) {
            case "findpaths":
                topo = new Topology(datafile, inputfile);
                topo.printTopology();
                topo.findAllPathsForAllDemands();
                writePaths(pathfile);
                break;
            case "test":
                topo = new Topology(datafile, inputfile);
                topo.getAllPathsForAllDemands(pathfile);
                topo.printTopology();
                topo.printAllDemandPaths();
                topo.ExecuteMultihour();
                topo.printBestUtilizationLink();
                topo.printWeightLink();
                topo.printBestRoutingCost();
//                writeOneLineResult(resultfile);
                break;
        }

    }

    public static void writeResults(String outputfile) {
        try {
            // writer.write("# accepted demand / maximum link utilization\n");
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputfile), true));
            writer.write(String.valueOf(topo.demands.size()) + " " + String.valueOf(topo.acceptedDemand) + " " + String.valueOf(topo.Fbest2) + "\n");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void writePaths(String outputfile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputfile), true));
            writer.write(String.valueOf(topo.demands.size()) + "\n");
            for (Demand d : topo.demands) {
                writer.write(String.valueOf(d.srcNode.getId()) + " " + String.valueOf(d.destNode.getId()) + "\n");
                writer.write(String.valueOf(d.getAllPaths().size()) + "\n");
                for (Path p : d.getAllPaths()) {
                    writer.write(String.valueOf(p.nodes.size()) + " ");
                    for (Node v : p.nodes) {
                        writer.write(String.valueOf(v.getId()) + " ");
                    }
                    writer.write("\n");
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void writeOneLineResult(String outputfile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputfile), true));
            writer.write(String.valueOf(topo.demands.size())
//                    + " " + String.valueOf(topo.numberScheduledDemands())
//                    + " " + String.valueOf(topo.calSatisfiedDemandsVolume())
                    + " " + String.valueOf("\n"));

            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
