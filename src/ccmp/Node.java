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
public class Node {

    int id;
    double distance;
    double computingCapacity;
    ArrayList<Link> inLinks;
    ArrayList<Link> outLinks;
    ArrayList<Link> outPaths;
    //c2v used in time t
    Hashtable<Time, Double> usedComputingCapacity;
    static int count = 0;
    //used computing resource for Function at time t
    Hashtable<Time, Hashtable<Function, Double>> compUsedForFunction;
    boolean enabled = true;

    public Node() {
        computingCapacity = 0;
        distance = 0;
        inLinks = new ArrayList<Link>();
        outLinks = new ArrayList<Link>();
        outPaths = new ArrayList<Link>();
        usedComputingCapacity = new Hashtable<Time, Double>();
        compUsedForFunction = new Hashtable<Time, Hashtable<Function, Double>>();
        id = count;
        count++;
    }

//    public Node(Node v) {
//        computingCapacity = v.getComputingCapacity();
//        distance = v.getDistance();
//        inLinks = new ArrayList<Link>();
//        outLinks = new ArrayList<Link>();
//        outPaths = new ArrayList<Link>();
//        compUsedForFunction = new Hashtable<Function, Double>();
//        usedComputingCapacity = 0;
//        for (Link e : v.getInLinks()) {
//            Link ie = new Link(e);
//            inLinks.add(ie);
//        }
//        for (Link e : v.getOutLinks()) {
//            Link ie = new Link(e);
//            outLinks.add(ie);
//        }
//        for (Link e : v.getOutPaths()) {
//            Link ie = new Link(e);
//            outPaths.add(ie);
//        }
//        id = count;
//        count++;
//
//    }

    public void reset() {
        enabled = true;
        outPaths.clear();
        compUsedForFunction.clear();
        usedComputingCapacity.clear();
    }
    
     public void reset(Time t) {
        enabled = true;
        outPaths.clear();
        if(compUsedForFunction.containsKey(t))
             compUsedForFunction.get(t).clear();
        usedComputingCapacity.put(t, 0.0);
    }

    public void setId(int i) {
        id = i;
    }

    public void setEnabled(boolean e) {
        enabled = e;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setDistance(double d) {
        distance = d;
    }

    public void setComputingCapacity(double c) {
        computingCapacity = c;
    }

    public void setUsedComputingCapacity(Time t, double c) {
        usedComputingCapacity.put(t, c);
    }

    public int getNumInLinks() {
        return inLinks.size();
    }

    public int getNumOutLinks() {
        return outLinks.size();
    }

    public double getDistance() {
        return distance;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Link> getInLinks() {
        return inLinks;
    }

    public ArrayList<Link> getOutLinks() {
        return outLinks;
    }

    public ArrayList<Link> getOutPaths() {
        return outPaths;
    }

    public double getComputingCapacity() {
        return computingCapacity;
    }

    public double getUsedComputingCapacity(Time t) {
        return usedComputingCapacity.get(t);
    }

    public double getAvailableComputingCapacity(Time t) {
        return computingCapacity - usedComputingCapacity.get(t);
    }

    public boolean isOverLoad(Time t) {
        if (this.getAvailableComputingCapacity(t) < 0) {
            return true;
        }
        return false;
    }

    public void printInLinks() {
        System.out.println(" Inlinks: ");
        for (Link e : inLinks) {
            System.out.print(" " + e.getSrcNode().getId());
        }
        System.out.println();
    }

    public void printOutLinks() {
        System.out.println(" Outlinks: ");
        for (Link e : outLinks) {
            System.out.print(" " + e.getDestNode().getId());
        }
        System.out.println();
    }

}
