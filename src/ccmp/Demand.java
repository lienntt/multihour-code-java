/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccmp;

import java.util.ArrayList;
import java.util.Enumeration;
import java.io.File;
import java.util.Hashtable;
import java.util.Scanner;

/**
 *
 * @author liem
 */
public class Demand {

    Node srcNode;
    Node destNode;

    ArrayList<Function> functions;
    ArrayList<Path> allPaths;
    ArrayList<Path> allShortestPaths;
    Hashtable<Time, ArrayList<Path>> paths;

    Hashtable<Time, Double> demandVolume;
    Hashtable<Time, Double> scheduledDemandVolume;

    public Demand(Node src, Node dest) {
        srcNode = src;
        destNode = dest;
        demandVolume = new Hashtable<Time, Double>();
        scheduledDemandVolume = new Hashtable<Time, Double>();
        functions = new ArrayList<Function>();
        allPaths = new ArrayList<Path>();
        paths = new Hashtable<Time, ArrayList<Path>>();
        allShortestPaths = new ArrayList<Path>();
    }

    public Demand(Node src, Node dest, ArrayList<Function> fs) {
        srcNode = src;
        destNode = dest;
        demandVolume = new Hashtable<Time, Double>();
        scheduledDemandVolume = new Hashtable<Time, Double>();
        functions = fs;
        allPaths = new ArrayList<Path>();
        paths = new Hashtable<Time, ArrayList<Path>>();
        allShortestPaths = new ArrayList<Path>();
    }

//    public Demand clone() {
//        Demand d = new Demand(this.srcNode, this.destNode, this.functions);
//        d.demandVolume = (Hashtable<Time, Double>) this.demandVolume.clone();
//        d.allShortestPaths = (ArrayList<Path>) this.allShortestPaths.clone();
//        for (Path p : paths) {
//            d.paths.add(p);
//        }
//        d.scheduledDemandVolume = this.scheduledDemandVolume;
//        return d;
//    }
    public int hasPath(Path p) {
        if (!allPaths.isEmpty() && allPaths.contains(p)) {
            return 1;
        }
        return 0;
    }

    public int hasPath(Time t, Path p) {
        if (!getPaths(t).isEmpty() && getPaths(t).contains(p)) {
            return 1;
        }
        return 0;
    }

    public void reset(Time t) {
        resetPaths(t);
        scheduledDemandVolume.put(t, 0.0);
    }

    public void resetPaths(Time t) {
        if (getPaths(t)!=null) {
            getPaths(t).clear();
        }
    }

    public ArrayList<Path> getAllPaths() {
        return allPaths;
    }

    public ArrayList<Path> getPaths(Time t) {
        if (paths.containsKey(t)) {
            return paths.get(t);
        }
        return new ArrayList<Path>();
    }

    public ArrayList<Path> getAllShortestPaths() {
        if (allShortestPaths.size() > 0) {
            return allShortestPaths;
        }

        ArrayList<Path> tempPaths = new ArrayList<Path>();
        ArrayList<Path> shortestPaths = new ArrayList<Path>();
        tempPaths = getAllPaths();
        if (tempPaths.size() < 1) {
            allShortestPaths = shortestPaths;
            return allShortestPaths;
        }
        double minWeight = tempPaths.get(0).getWeight();
        for (Path p : tempPaths) {
            if (p.getWeight() < minWeight) {
                minWeight = p.getWeight();
            }
        }
        for (Path p : tempPaths) {
            if (p.getWeight() == minWeight) {
//                p.demand = d;
                shortestPaths.add(p);
            }
        }
        allShortestPaths = shortestPaths;
        return allShortestPaths;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

    public void addPath(Time t, Path p) {
        getPaths(t).add(p);
    }

    public void addToShortestPath(Path p) {
        getAllShortestPaths().add(p);
    }

    public void removeFromShortestPath(Path p) {
        getAllShortestPaths().remove(p);
    }

    public void setPaths(Time t, ArrayList<Path> ps) {
        paths.put(t, (ArrayList<Path>) ps.clone());
    }

    public void setAllShortestPath(ArrayList<Path> ps) {
        allShortestPaths = (ArrayList<Path>) ps.clone();
    }

    public void removePath(Time t, Path p) {
        if (getPaths(t).contains(p)) {
            getPaths(t).remove(p);
            return;
        }
        for (Path pi : getPaths(t)) {
            if (pi == p) {
                getPaths(t).remove(p);
            }
        }
        return;
    }

    public double getRequireResource(double trafficRate) {
        double requireResource = 0;
        for (Function f : functions) {
            requireResource += f.requiredResource * trafficRate;
        }
        return requireResource;
    }

    public double getTotalRequireResource(Time t) {
        return getRequireResource(getDemandVolume(t));
    }

    public int getNumPath(Time t) {
        return getPaths(t).size();
    }

    public int hasFunction(Function f) {
        if (functions.contains(f)) {
            return 1;
        }
        return 0;
    }

    public void setDestNode(Node dist) {
        destNode = dist;
    }

    public void setSrcNode(Node src) {
        srcNode = src;

    }

    public Node getDestNode() {
        return destNode;
    }

    public Node getSrcNode() {
        return srcNode;
    }

    public double getDemandVolume(Time t) {
        return demandVolume.get(t);
    }

    public double getScheduledDemandVolume(Time t) {
        return scheduledDemandVolume.get(t);
    }

    public void printPathList(ArrayList<Path> pathlist) {
        int numPath = 0;
        System.out.println("number of path: " + pathlist.size());
        for (Path p : pathlist) {
            numPath++;
            System.out.println("pathid: " + numPath);
            System.out.println("pathweight: " + p.getWeight());
            System.out.println("path flow rate: " + p.getFlowRate());
            System.out.println("link number: " + p.getLinks().size());
            System.out.println("node number: " + p.getNodes().size());
            for (Node v : p.getNodes()) {
                System.out.print(" " + v.getId());
            }
            System.out.println();
            System.out.print(" weight link: ");
            for (Link e : p.links) {
                System.out.print(e.getWeight());
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printPaths(Time t) {
        int numPath = 0;
        System.out.println("All path");
        System.out.println("path number: " + paths.size());
        for (Path p : getPaths(t)) {
            numPath++;
            System.out.println("pathid: " + numPath);
            System.out.println("pathweight: " + p.getWeight());
            System.out.println("path flow rate: " + p.getFlowRate());
            System.out.println("link number: " + p.getLinks().size());
            System.out.println("node number: " + p.getNodes().size());
            for (Node v : p.getNodes()) {
                System.out.print(" " + v.getId() + " fs: ");
//                if (p.mapped) {
//                    for (Function f : p.mappingFunctionNode.get(v)) {
//                        System.out.print(" " + f.getId());
//                    }
//                    System.out.println();
//                }
            }
            System.out.println();
            System.out.print(" weight link: ");
            for (Link e : p.links) {
                System.out.print(e.getWeight());
            }
            System.out.println();
        }
        System.out.println();
    }

    public void printAllShortestPaths() {
        int numPath = 0;
        System.out.println("All shortest path");
        System.out.println("path number: " + allShortestPaths.size());
        for (Path p : allShortestPaths) {
            numPath++;
            System.out.println("pathid: " + numPath);
            System.out.println("pathweight: " + p.getWeight());
            System.out.println("path flow rate: " + p.getFlowRate());
            System.out.println("link number: " + p.getLinks().size());
            System.out.println("node number: " + p.getNodes().size());
            for (Node v : p.getNodes()) {
                System.out.print(" " + v.getId());
            }
            System.out.println();
            System.out.print(" weight link: ");
            for (Link e : p.links) {
                System.out.print(e.getWeight());
            }
            System.out.println();
        }
        System.out.println();
    }

//    //check path for all functions
//    public boolean checkPathForFunctions(Path p) {
//        int numberFunctionSupported = 0;
//        for (Function f : getFunctions()) {
//            for (Node v : p.getNodes()) {
//                if ((v.getComputingCapacity() - v.getUsedComputingCapacity()) > 0) {
//                    numberFunctionSupported++;
//                    break;
//                }
////                if (v.compUsedForFunction.containsKey(f)) {
//////                    if (v.hasFunction(f) && (v.getComputingCapacity() - v.compUsedForFunction.get(f)) > 0) {
////                      if (v.hasFunction(f) && (v.getComputingCapacity() - v.getUsedComputingCapacity()) > 0) {
////                        numberFunctionSupported++;
////                        break;
////                    }
////                } else {
////                    if (v.hasFunction(f)) {
////                        numberFunctionSupported++;
////                        break;
////                    }
////                }
//            }
//        }
//
//        return (numberFunctionSupported == getFunctions().size());
//    }
    //check computing capacity for all node in path, function and flowrate of path.
//    public boolean checkComputingCapacity(Path p) {
////        Hashtable<Function, Double> availableComputingCapacity = new Hashtable<Function, Double>();
//        double availableComputingCapacity = 0.0;
////        for (Function f : functions) {
////            availableComputingCapacity.put(f, 0.0);
////        }
//        // tìm khả năng cung cấp các chức năng lớn nhất có thể trên path đối với từng chức năng
////        for (Node v : p.getNodes()) {
////            for (Function f : functions) {
////                if (v.hasFunction(f) && (v.computingCapacity - v.getUsedComputingCapacity()) > availableComputingCapacity) {
////                    availableComputingCapacity = v.computingCapacity - v.getUsedComputingCapacity();
////                }
////            }
////        }
////        //nếu có 1 chức năng nào k được thực hiện đầy đủ thì trả về false
////        for (Function f : functions) {
////            if (p.flowRate * f.requiredResource > availableComputingCapacity) {
////                return false;
////            }
////        }
////         for (Node v : p.getNodes()) {
////            for (Function f : functions) {
////                if (v.hasFunction(f) && (v.computingCapacity - v.getUsedComputingCapacity()) < p.flowRate * f.requiredResource) {
////                    return false;
////                }
////            }
////        }
//
////                 tìm khả năng cung cấp các chức năng lớn nhất có thể trên path đối với từng chức năng
////        for (Node v : p.getNodes()) {
////            for (Function f : functions) {
////                if (v.hasFunction(f) && (v.computingCapacity - v.getUsedComputingCapacity()) > availableComputingCapacity) {
////                    availableComputingCapacity = v.computingCapacity - v.getUsedComputingCapacity();
////                }
////            }
////        }
//        //nếu có 1 chức năng nào k được thực hiện đầy đủ thì trả về false
//        for (Function f : functions) {
//            for (Node v : p.getNodes()) {
//                //neu v chua chức năng f và computingcapacity còn lại > availael thì cập nhật lại avalilable
//                if ((v.computingCapacity - v.getUsedComputingCapacity()) > availableComputingCapacity) {
//                    availableComputingCapacity = v.computingCapacity - v.getUsedComputingCapacity();
//                }
//            }
//            //nếu có chức năng nào k được thỏa mãn thì trả về false
//            if (p.flowRate * f.requiredResource > availableComputingCapacity) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//    public boolean isAccepted() {
//        if (paths.size() < 1) {
//            return false;
//        }
//        for (Path p : getPaths()) {
//            if (!p.isSatifyAllRequirements() || !p.mappingFunctionNode()) {
//                return false;
//            }
//        }
//        isAccepted = true;
//        return true;
//    }
    public boolean isSatisfied(Time t) {
        if (getUnscheduledDemandVolume(t) <= 0) {
            return true;
        }
        return false;
    }

//    public boolean checkSatifyComputingCapacity() {
//        for (Path p : getPaths()) {
//            if (checkComputingCapacity(p) == false) {
//                return false;
//            }
//        }
//        return true;
//    }
    //set outpath for links for all paths of demand 
    public void setOutPathForLink(Time t) {
        if(getPaths(t)==null){
            System.err.println("no satified path");
            return;
        }
        System.out.println("number path 3: " + getPaths(t).size());
        for (Path p : getPaths(t)) {
            p.setOutPathForLink();
        }
    }

//    public void updateFlowRateForPaths() {
//        double variedVolume = demandVolume - originalDemandVolume;
//        double variedRate = variedVolume / originalDemandVolume;
//        for (Path p : paths) {
//            p.setPrevFlowRate();
//            p.setFlowRate(p.getPrevFlowRate() * (1 + variedRate));
//            p.updateUsedCapacity();
//        }
//    }
//    public void variedDemandVolumeForPaths() {
//        for (Path p : paths) {
//            if (p.hasOverLoad()) {
//                p.decreaseFlowRate();
//                continue;
//            }
//            if (this.getCurrentDemandVolume() < this.demandVolume) {
//                p.increaseFlowRate();
//            }
//        }
//    }
//    protected ArrayList<Path> _sortPathsDecreaseAccordingToMinCapacity(ArrayList<Path> pathList) {
//        ArrayList<Path> sorted = new ArrayList<Path>();
//        for (Path p : pathList) {
//            boolean added = false;
//            for (int i = 0; i < sorted.size(); i++) {
//                if (p.getMinAvailableCapacity() > sorted.get(i).getMinAvailableCapacity()) {
//                    sorted.add(i, p);
//                    added = true;
//                    break;
//                }
//            }
//            if (added == false) {
//                sorted.add(sorted.size(), p);
//            }
//        }
//        pathList.clear();
//        return sorted;
//    }
//    public void calculateMinCapacityForAllPaths() {
//        for (Path p : allShortestPaths) {
//            p.calculateMinAvailableCapacity();
//        }
//    }
//
//    public void sortPathsDecreaseAccordingToMinCapacity() {
//        allShortestPaths = _sortPathsDecreaseAccordingToMinCapacity(allShortestPaths);
//    }
    public double getUnscheduledDemandVolume(Time t) {
        return getDemandVolume(t) - getScheduledDemandVolume(t);
    }

    //GanNode()
    //notdefine
    public void mapNodeForAllPathsOfDemand(Time t) {
        for (Path p : getPaths(t)) {
            p.mappingFunctionNode(t);
        }
    }

//    //scheme 1
//    public boolean mapSatisfiedPaths(int maxNumerShortestPaths) {
//        paths.clear();
//        System.err.println("demand volume: " + getUnscheduledDemandVolume());
//        // xem cos path nao cos cp>bandwidth(yd) gan cuoi danh sach thi gan cho d
//        for (int i = allShortestPaths.size() - 1; i >= 0; i--) {
//            Path p = allShortestPaths.get(i);
//            double unscheduleVolume = getUnscheduledDemandVolume();
//            if (p.getMinAvailableCapacity() >= unscheduleVolume) {
//                p.setFlowRate(unscheduleVolume);
//                scheduledDemandVolume += unscheduleVolume;
//                p.updateBandwidthCapacityForLinks();
//                paths.add(p);
//                isAccepted = true;
//                printPaths();
//                return true;
//            }
//        }
//        //khong co path nao cp>bandwidth(yd)
//        //chon lan luot tu path cos cp lon nhat gan cho yd cho den het
//        int numberPaths = Math.min(maxNumerShortestPaths, allShortestPaths.size());
//        for (int i = 0; i < numberPaths; i++) {
//            Path p = allShortestPaths.get(i);
//            double mapVolume = Math.min(p.getMinAvailableCapacity(), getUnscheduledDemandVolume());
//            p.setFlowRate(mapVolume);
//            scheduledDemandVolume += mapVolume;
//            p.updateBandwidthCapacityForLinks();
//            paths.add(p);
//            if (getUnscheduledDemandVolume() <= 0) {
//                isAccepted = true;
//                printPaths();
//                return true;
//            }
//        }
//        //neu khong du de gan cho demand thi reject demand
//        if (getUnscheduledDemandVolume() > 0) {
//            for (Path p : paths) {
//                scheduledDemandVolume -= p.flowRate;
//                p.rollbackResource();
//            }
//            paths.clear();
//        }
//        printPaths();
//        return false;
//    }
//
    //scheme 2
    public boolean mapSatisfiedMultiPaths(Time t, int maxNumerShortestPaths) {
        paths.clear();
        //chon lan luot tu path cos cp lon nhat gan cho yd cho den het
        int numberPaths = Math.min(maxNumerShortestPaths, allShortestPaths.size());
        double totalAvailable = 0;
        double _unscheduledDemendVolume = getUnscheduledDemandVolume(t);
        double _scheduledDemandVolume = 0;

        for (int i = 0; i < numberPaths; i++) {
            Path p = allShortestPaths.get(i);
            totalAvailable += p.getMinAvailableCapacity(t);
        }
        System.err.println("totalAvailable: " + totalAvailable);
        System.err.println("demand volume: " + getUnscheduledDemandVolume(t));

        if (totalAvailable < _unscheduledDemendVolume) {
            printPaths(t);
            return false;
        }

        for (int i = 0; i < numberPaths; i++) {
            Path p = allShortestPaths.get(i);
            double mapVolume = p.getMinAvailableCapacity(t) * _unscheduledDemendVolume / totalAvailable;

            if (i == numberPaths - 1) {
                mapVolume = _unscheduledDemendVolume - _scheduledDemandVolume;
            }
            p.setFlowRate(mapVolume);
            _scheduledDemandVolume += mapVolume;
            scheduledDemandVolume.put(t, getScheduledDemandVolume(t)+ mapVolume);
            p.updateBandwidthCapacityForLinks(t);
            getPaths(t).add(p);
            if (getUnscheduledDemandVolume(t) <= 0) {
//                isAccepted = true;
                printPaths(t);
                return true;
            }
        }
        //neu khong du de gan cho demand thi reject demand
        if (getUnscheduledDemandVolume(t) > 0) {
            rollbackLinkResource(t);
        }
        printPaths(t);
        return false;
    }
    
    public void rollbackLinkResource(Time t){
        for (Path p : getPaths(t)) {
            scheduledDemandVolume.put(t, getScheduledDemandVolume(t)- p.getFlowRate());
            p.rollbackLinkResource(t);
        }
    }
    //execute map functions for nodes with each demand
    public boolean mapFunctionNodeForDemand(Time t){
        boolean _mapsuccess = true;
         for (Path p : this.getPaths(t)) {
            if( p.mappingFunctionNode(t) ==false){
                _mapsuccess = false;
            }
        }
        //if assign function for node success for all path
        if(_mapsuccess){
            return true;
        }
        for(Path p : getPaths(t)){
            p.rollbackComputingCapacityForNodes(t);
        }
        return false;
    }
}
