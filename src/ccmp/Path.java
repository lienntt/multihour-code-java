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
public class Path {

    ArrayList<Node> nodes;
    ArrayList<Link> links;
    double weight = -1;
    boolean getDest = false;
    double minCapacity;
    boolean mapped = false;
//    boolean foundBetterMappingFunctionNode = false;
    Hashtable<Node, ArrayList<Function>> mappingFunctionNode;
    //moi p chi support cho 1 demand tai 1 time
//    Hashtable<Time, Demand>   demands;
    Demand demand;
    double flowRate;
    Hashtable<Node, Double> usedComputing = new Hashtable<Node, Double>();

    public Path() {
        nodes = new ArrayList<Node>();
        links = new ArrayList<Link>();
        mappingFunctionNode = new Hashtable<Node, ArrayList<Function>>();
//        demands = new  Hashtable<Time, Demand>();
//        flowRate = new Hashtable<Time, Double>();
        flowRate = 0.0;
        minCapacity = 0;
    }

    public Path(Path p) {
        flowRate = p.flowRate;
//        flowRate = (Hashtable<Time, Double>) p.flowRate.clone();
        nodes = new ArrayList<Node>();
        links = new ArrayList<Link>();
        mappingFunctionNode = new Hashtable<Node, ArrayList<Function>>();
//        demands = (Hashtable<Time, Demand>) p.demands.clone();
        demand = p.demand;
        minCapacity = p.minCapacity;
        for (Node v : p.getNodes()) {
            nodes.add(v);
        }
        for (Link e : p.getLinks()) {
            links.add(e);
        }

    }

    public int hasNode(Node v) {
        if (nodes.contains(v)) {
            return 1;
        }
        return 0;
    }
//    //Jdp
//
//    public int servesDemand(Time t, Demand d) {
//        if (demands.get(t) == d) {
//            return 1;
//        }
//        return 0;
//    }
    //Lep

    public int hasLink(Link e) {
        if (links.contains(e)) {
            return 1;
        }
        return 0;
    }

    public double getFlowRate() {
        return flowRate;
    }

   
    public void setFlowRate( double fr) {
        flowRate = fr;
    }
//    public double getFlowRate(Time t) {
//        return flowRate.get(t);
//    }
//
//   
//    public void setFlowRate(Time t, double fr) {
//        flowRate.put(t, fr);
//    }

//    public void updateUsedCapacityForLinks() {
//        for (Link e : links) {
//            e.usedCapacity += this.flowRate - this.prevFlowRate;
//        }
//    }
//
//    public void updateUsedComputingCapacityForNodes() {
//        for (Node v : this.mappingFunctionNode.keySet()) {
//            for (Function f : this.mappingFunctionNode.get(v).keySet()) {
//                v.usedComputingCapactiy += this.mappingFunctionNode.get(v).get(f) * (this.flowRate - this.prevFlowRate);
//            }
//        }
//    }
//
//    public void updateUsedCapacity() {
//        updateUsedCapacityForLinks();
//        updateUsedComputingCapacityForNodes();
//    }
//
//    public void increaseFlowRate() {
//        double increaseFlowRate = flowRate + Math.abs(flowRate - prevFlowRate);
//        prevFlowRate = flowRate;
//        flowRate = increaseFlowRate;
//        updateUsedCapacity();
//    }
//
//    public void decreaseFlowRate() {
//        double decreaseFlowRate = flowRate - Math.abs(flowRate - prevFlowRate) / 2;
//        prevFlowRate = flowRate;
//        flowRate = decreaseFlowRate;
//        updateUsedCapacity();
//    }
    public boolean isOverLoad(Time t) {
        boolean isOverLoad = false;
        for (Link e : links) {
            if (e.isOverLoad(t)) {
                isOverLoad = true;
            }
        }
        for (Node v : nodes) {
            if (v.isOverLoad(t)) {
                isOverLoad = true;
            }
        }
        return isOverLoad;
    }


    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public ArrayList<Link> getLinks() {
        return links;
    }

    public boolean equals(Path p) {
        if (p.getNodes().size() != nodes.size()) {
            return false;
        }
        for (int i = 1; i < nodes.size(); i++) {
            if (p.getNodes().get(i) != nodes.get(i)) {
                return false;
            }
        }
        return true;
    }
    //calculate weight of path

    public double getWeight() {
        if (weight > 0) {
            return weight;
        }
        double pw = 0;
        for (Link e : links) {
            pw += e.getWeight();
        }
        weight = pw;
        return pw;
    }

    public void setOutPathForLink() {
        for (Link e : getLinks()) {
            if (!e.getSrcNode().getOutPaths().contains(e)) {
                e.getSrcNode().getOutPaths().add(e);
            }
        }
    }

    public double getAvailableCapacity(Time t) {
        double availableCapacity = 0;
        for (Node v : nodes) {
            availableCapacity += v.getAvailableComputingCapacity(t);
        }
        return availableCapacity;
    }

    public double getMinAvailableComputingCapacity(Time t) {
        if (nodes.size() < 1) {
            return 0;
        }
        double minAvailableComputingCapacity = nodes.get(0).getAvailableComputingCapacity(t);
        for (Node v : nodes) {
            if (v.getAvailableComputingCapacity(t) > minAvailableComputingCapacity) {
                minAvailableComputingCapacity = v.getAvailableComputingCapacity(t);
            }
        }
        return minAvailableComputingCapacity;
    }

    public void updateBandwidthCapacityForLinks(Time t) {
        for (Link e : links) {
            e.setUsedBandwidthCapacity(t, e.getUsedBandwidthCapacity(t) + this.getFlowRate());
        }
    }

    public void rollbackLinkResource(Time t) {
        for (Link e : links) {
            e.setUsedBandwidthCapacity(t, e.getUsedBandwidthCapacity(t) - this.getFlowRate());
        }
    }

    public double getAvailableBandwidth(Time t) {
        if (links.size() < 1) {
            return Double.MAX_VALUE;
        }
        double availableBandwidth = links.get(0).getBandwidthCapacity();
        for (Link e : links) {
            if (availableBandwidth > e.getAvailableCapacity(t)) {
                availableBandwidth = e.getAvailableCapacity(t);
            }
        }
        return availableBandwidth;

    }

    public double getMinAvailableBandwidth(Time t) {
        return getAvailableBandwidth(t);
    }

    public double _calculateMinAvailableCapacity(Time t) {

//        if (getMinAvailableComputingCapacity() < getMinAvailableBandwidth()) {
//             minCapacity = getMinAvailableComputingCapacity();
//        }
        return getMinAvailableBandwidth(t);

    }

    public double getMinAvailableCapacity(Time t) {
        return _calculateMinAvailableCapacity(t);
    }

    public boolean isSatisfyNodeCapacity(Time t) {
        for (Node v : nodes) {
            if (v.isOverLoad(t)) {
                return false;
            }
        }

        return true;
    }

    public boolean isSatisfyLinkCapacity(Time t) {
        for (Link e : links) {
            if (e.isOverLoad(t)) {
                return false;
            }
        }
        return true;
    }

    public boolean isSatifyAllRequirements(Time t) {

        return this.isSatisfyLinkCapacity(t) && this.isSatisfyNodeCapacity(t);
    }

    public boolean mappingFunctionNode(Time t) {
        //tim loi giai kha dung
        return _mappingFunctionNode(t);
    }

    public boolean _mappingFunctionNode(Time t) {
//        if (!this.isSatisfyForDemand(demand, flowRate)) {
//            return false;
//        }

        return findSolutionMappingForFunctionNode(t);

//        do {
//            findSolutionMappingForFunctionNode();
//            //tiep tuc lap neu van tim ra chuoi moi tot hon
//        } while (foundBetterMappingFunctionNode == true);

        //phan bo function tren node thanh cong thi cap nhat lai capacity cua node
//        if (this.mapped == true) {
//            updateComputingCapacityForNodes(t);
//        }
//        return false;
    }

    public boolean findSolutionMappingForFunctionNode(Time t) {
        ArrayList<Function> functionsMap = new ArrayList<Function>();
        //cac function can xu ly
        for (Function f : demand.functions) {
            functionsMap.add(f);
        }

        ArrayList<Node> nodesMap = new ArrayList<Node>();
        for (Node v : nodes) {
            nodesMap.add(v);
        }
//        Hashtable<Node, Double> usedComputing = new Hashtable<Node, Double>();
        for (Node v : nodesMap) {
            usedComputing.put(v, 0.0);
            ArrayList<Function> fs = new ArrayList<Function>();
            mappingFunctionNode.put(v, fs);
        }

        //neu chua het node de gan va chua het function can gan
        while (!nodesMap.isEmpty() && !functionsMap.isEmpty()) {
            //xet node tiep theo
            Node v = nodesMap.get(0);
            nodesMap.remove(v);

            //neu node con kha nang xu ly va chua het function can gan
            while (v.getAvailableComputingCapacity(t) - usedComputing.get(v) > 0 && !functionsMap.isEmpty()) {

                //xet function tiep theo
                Function f = functionsMap.get(0);

                //neu node con kha nang cap capacity cho function
                if ((v.getAvailableComputingCapacity(t) - usedComputing.get(v)) >= (flowRate * f.requiredResource)) {
                    //cap nhat cho v
                    usedComputing.put(v, usedComputing.get(v) + flowRate * f.requiredResource);
                    ArrayList<Function> fs = mappingFunctionNode.get(v);
                    fs.add(f);
                    mappingFunctionNode.put(v, fs);
                    functionsMap.remove(f);
                } else {
                    //neu khong du xy ly co function thi thoat khoi vong lap
                    //chuyen sang node tiep theo
                    break;
                }
            }
        }
        if (functionsMap.isEmpty()) {
           updateComputingCapacityForNodes(t);
           return true;
        } 
        
            return false;
    }

//    public void findBetterSolutionMappingForFunctionNode() {
//
//        foundBetterMappingFunctionNode = false;
//
//        ArrayList<Node> nodesList = new ArrayList<Node>();
//        //lau cac node dang duoc gan xu ly function
//        for(Node v: nodes){
//            if(!mappingFunctionNode.get(v).isEmpty())
//                nodesList.add(v);
//        }
//        
//        while(nodesList.size()>= 2){
//            Node src = nodesList.get(0);
//            Node dest = nodesList.get(1);
//            ArrayList<Function> fs = mappingFunctionNode.get(src);
//            if(_findBetterSolutionMappingForFunction(src, dest, fs)==true)
//                foundBetterMappingFunctionNode = true;
//            nodesList.remove(src);
//            nodesList.remove(dest);
//        }
//    }
//
//    //fs hien tai dang chay tren src
//    public boolean _findBetterSolutionMappingForFunction(Node src, Node dest, ArrayList<Function> fs) {
//        double requiredComputingCapacity = 0;
//        for (Function f : fs) {
//            requiredComputingCapacity += f.getRequireResource() * this.getFlowRate();
//        }
//        ArrayList<Node> availableNodes = new ArrayList<Node>();
//        //add cac node tu src den dest
//        availableNodes.add(src);
//        for (Link e : links) {
//            if (availableNodes.contains(e.getSrcNode()) && e.getDestNode() != dest) {
//                availableNodes.add(e.getDestNode());
//            }
//        }
//        //sort nodes tang dan theo available computing capacity
//        availableNodes = sortNodesIncreaseAccordingToAvailableCapacity(availableNodes);
//
//        for (Node v : availableNodes) {
//            //tim thay node nho nhat va du kha nang chay function 
//            if (findAvailableCapacityOfNodeAfterMapping(v) >= requiredComputingCapacity) {
//                mappingFunctionNode.get(v).addAll(fs);
//                mappingFunctionNode.get(src).removeAll(fs);
//                usedComputing.put(src, usedComputing.get(src) - requiredComputingCapacity);
//                usedComputing.put(v, usedComputing.get(v) + requiredComputingCapacity);
//                return true;
//            }
//        }
//        return false;
//    }

//    public ArrayList<Node> sortNodesIncreaseAccordingToAvailableCapacity(ArrayList<Node> nodeList) {
//
//        ArrayList<Node> sorted = new ArrayList<Node>();
//        for (Node v : nodeList) {
//            boolean added = false;
//            for (int i = 0; i < sorted.size(); i++) {
//                if (findAvailableCapacityOfNodeAfterMapping(v) < findAvailableCapacityOfNodeAfterMapping(sorted.get(i))) {
//                    sorted.add(i, v);
//                    added = true;
//                    break;
//                }
//            }
//            if (added == false) {
//                sorted.add(sorted.size(), v);
//            }
//        }
//        return sorted;
//    }

//    public double findAvailableCapacityOfNodeAfterMapping(Node v) {
//        return v.getAvailableComputingCapacity() - usedComputing.get(v);
//    }

    public void updateComputingCapacityForNodes(Time t) {
        for (Node v : nodes) {
            ArrayList<Function> fs = mappingFunctionNode.get(v);
            for (Function f : fs) {
                v.setUsedComputingCapacity(t, v.getUsedComputingCapacity(t) + this.getFlowRate() * f.getRequireResource());
            }
        }
    }
    
     public void rollbackComputingCapacityForNodes(Time t) {
         for (Node v : nodes) {
            ArrayList<Function> fs = mappingFunctionNode.get(v);
            for (Function f : fs) {
                v.setUsedComputingCapacity(t, v.getUsedComputingCapacity(t) - this.getFlowRate() * f.getRequireResource());
            }
        }
    }
}
