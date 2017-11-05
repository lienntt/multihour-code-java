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
public class Link {

    static int count = 0;
    int id;
    double bandwidthCapacity;

    Node destNode;
    Node srcNode;
    Link reverseLink;
    double weight;
    boolean enabled = true;
    boolean overload = false;
    int visitedNumber = 0;
    //Cet
    Hashtable<Time, Double> routingCost;
    //yet
    Hashtable<Time, Double> usedCapacity;
    //<demand, rate  >>
//    private Hashtable<Time, Hashtable<Demand, Double>> totalFlowRate;

    public Link(Node src, Node dest, double bCapacity) {
        destNode = dest;
        srcNode = src;
        weight = 0;
        bandwidthCapacity = bCapacity;
        usedCapacity = new Hashtable<Time, Double>();
        routingCost = new Hashtable<Time, Double>();
        count++;
        id = count;
    }

//    public Link(Link e) {
//        destNode = new Node(e.getDestNode());
//        srcNode = new Node(e.getSrcNode());
//        weight = e.getWeight();
//        bandwidthCapacity = e.getBandwidthCapacity();
//        usedCapacity = (Hashtable<Time, Double>) e.usedCapacity.clone();
//        routingCost =(Hashtable<Time, Double>) e.routingCost.clone();
//        count++;
//        id = count;
//    }

    public void setReverseLink(Link e) {
        reverseLink = e;
        return;
    }

    public Link getReverseLink() {
        return reverseLink;
    }

    public void reset() {
        enabled = true;
        overload = false;
        usedCapacity.clear();
//        totalFlowRate.clear();
    }
     public void reset(Time t) {
        enabled = true;
        overload = false;
        usedCapacity.put(t, 0.0);
//        totalFlowRate.get(t).clear();
    }

    public void setDestNode(Node dest) {
        destNode = dest;
    }

    public void setSrcNode(Node src) {
        srcNode = src;

    }

    public void setWeight(double w) {
        weight = w;
    }

    public void setEnabled(boolean e) {
        enabled = e;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setBandwidthCapacity(double c) {
        bandwidthCapacity = c;
    }

    public void setUsedBandwidthCapacity(Time t, double c) {
        usedCapacity.put(t, c);
    }

    public Node getDestNode() {
        return destNode;
    }

    public Node getSrcNode() {
        return srcNode;
    }

    public double getWeight() {
        return weight;
    }
    
    public double getRoutingCost(Time t){
        return routingCost.get(t);
    }

    public double getBandwidthCapacity() {
        return bandwidthCapacity;
    }

    public double getUsedBandwidthCapacity(Time t) {
        return usedCapacity.get(t);
    }

    public double getAvailableCapacity(Time t) {

        return bandwidthCapacity - usedCapacity.get(t);// - getReverseLink().usedCapacity;
    }

    public boolean isOverLoad(Time t) {
        if (this.getAvailableCapacity(t) < 0) {
            return true;
        }
        return false;
    }

//    public Hashtable gettotalFlowRate() {
//        return totalFlowRate;
//    }
    
    public int isOnPath(Path p) {
        return p.hasLink(this);
    }

    public boolean isReverse(Link e) {
        if (this.getDestNode() == e.getSrcNode() && this.getSrcNode() == e.getDestNode()) {
            return true;
        }
        return false;
    }
}
