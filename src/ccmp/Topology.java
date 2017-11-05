/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ccmp;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
import java.io.File;
import java.util.Hashtable;

/**
 *
 * @author liem
 */
public class Topology extends BaseTopology {

    final double INITIALIZE_WEIGHT = 1.0;
    ArrayList<Node> nodes;
    ArrayList<Link> links;
    ArrayList<Demand> demands;
    ArrayList<Function> functions;
    ArrayList<Time> timewindows;
    double Fcurrent = 0; //F()
    double Fbest = 0; //Fbest
    double Fbest2 = 0; //Fbest
    int acceptedDemand = 0;
    int acceptedDemandCurrent = 0;
    int acceptedDemandBest = 0;
    int acceptedDemandNeighbor = 0;
    int count = 0;
    int functionNum;
    int demandNum;
    int maxNumberShortestPaths = 3;

    public Topology() {
    }

    public Topology(String nodefile, String inputfile) {
        nodes = new ArrayList<Node>();
        links = new ArrayList<Link>();
        demands = new ArrayList<Demand>();
        functions = new ArrayList<Function>();
        timewindows = new ArrayList<Time>();

        readInputFile(nodefile);
        addReverseLinks();

//        generateFunctions();
//        generateDemands();
        setInitialWeightForLinks();
//        demands = sortDemandsDecreaseAccordingToVolume(demands);
    }

    public void readInputFile(String nodefile) {
        int nodeNum;
        int functionNum;
        int timeNum;
        int linkNum;
        int demandNum;
        double volumn;
        int functionDemandNum;

        ArrayList<Integer> functionIds = new ArrayList<Integer>();
        int functionId;
        int srcNodeIndex;
        int destNodeIndex;
        try {
            //function number
            //functionId functionDemandVolume
            File f = new File(nodefile);
            Scanner reader = new Scanner(f);

            //so time time window
            timeNum = Integer.parseInt(reader.nextLine());
            reader.nextLine();
            for (int i = 0; i < timeNum; i++) {
                Time t = new Time(i);
                timewindows.add(t);
            }
            //lay danh sach cac function
            functionNum = Integer.parseInt(reader.nextLine());

            for (int i = 0; i < functionNum; i++) {
                Function func = new Function(reader.nextInt());
                func.setRequireResource(reader.nextInt());
                if (!functions.contains(func)) {
                    functions.add(func);
                }

            }

            reader.nextLine();
            reader.nextLine();
            //lấy danh sách các node và computing capacity
            nodeNum = Integer.parseInt(reader.nextLine());
//            System.out.println("node Num: " + nodeNum);

            //nodes number
            //nodeId nodeCapacity
            for (int i = 0; i < nodeNum; i++) {
                Node node = new Node();
                node.setId(reader.nextInt());
                node.setComputingCapacity(reader.nextInt());
                nodes.add(node);
            }

            reader.nextLine();
            reader.nextLine();
            //lấy danh sách các link
            linkNum = Integer.parseInt(reader.nextLine());
            System.err.println("linkNum: " + linkNum);
            // lấy danh sách các link
            for (int i = 0; i < linkNum; i++) {
                int id = reader.nextInt();
                srcNodeIndex = reader.nextInt();
                destNodeIndex = reader.nextInt();
                // link (src, dest, bandwidth)
                double bandwidth = reader.nextInt();
                Link link = new Link(getNodeById(srcNodeIndex), getNodeById(destNodeIndex), bandwidth);
                getNodeById(srcNodeIndex).getOutLinks().add(link);
                getNodeById(destNodeIndex).getInLinks().add(link);
                double routingcost;
                for (Time t : timewindows) {
                    routingcost = reader.nextInt();
                    link.routingCost.put(t, routingcost);
                }
                links.add(link);

            }
            reader.nextLine();

            //lấy danh sách các demnad
            reader.nextLine();
            demandNum = Integer.parseInt(reader.nextLine());
            // lấy danh sách các demand
            for (int i = 0; i < demandNum; i++) {
                srcNodeIndex = reader.nextInt();
                destNodeIndex = reader.nextInt();
                Demand demand = new Demand(getNodeById(srcNodeIndex), getNodeById(destNodeIndex));
                for (Time t : timewindows) {
                    volumn = reader.nextInt();
                    demand.demandVolume.put(t, volumn);
                }
                functionDemandNum = reader.nextInt();
                //get danh sach function
                for (int j = 0; j < functionDemandNum; j++) {
                    functionId = reader.nextInt();
                    for (Function func : functions) {
                        if (func.getId() == functionId) {
                            demand.functions.add(func);
                        }
                    }
                }
                demands.add(demand);
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void addReverseLinks() {
        ArrayList<Link> reverseLinks = new ArrayList<Link>();
        for (Link e : links) {
            Link re = new Link(e.getDestNode(), e.getSrcNode(), e.getBandwidthCapacity());
            reverseLinks.add(re);
            e.getDestNode().getOutLinks().add(re);
            e.getSrcNode().getInLinks().add(re);
            for (Time t : timewindows) {
                re.routingCost.put(t, e.getRoutingCost(t));
            }

        };
        for (Link e : reverseLinks) {
            links.add(e);
        };
    }

    public void setInitialWeightForLinks() {
        for (Link v : links) {
            v.setWeight(INITIALIZE_WEIGHT);
        }
    }

    public void findAllPathsForAllDemands() {
        for (Demand d : demands) {
            d.allPaths = findAllPathsFromSrcToNode(d.getSrcNode(), d.getDestNode());
        }
    }

    public void getAllPathsForAllDemands(String pathfile) {

        try {
            File f = new File(pathfile);
            Scanner reader = new Scanner(f);
            //lay so demand
            int dnum = Integer.parseInt(reader.nextLine());
//            if(dnum!= demands.size()){
//                System.err.print("Demand number not match");
//                System.exit(-1);
//            }
            for (Demand d : demands) {
                int ds = reader.nextInt();
                int dd = reader.nextInt();
                if (d.srcNode.getId() != ds || d.destNode.getId() != dd) {
                    System.err.print("Demand not match");
                    System.exit(-1);
                }
                reader.nextLine();
                int pnum = Integer.parseInt(reader.nextLine());
                for (int p = 0; p < pnum; p++) {
                    Path newpath = new Path();
                    int nnum = reader.nextInt();
                    int vid;
                    for (int v = 0; v < nnum; v++) {
                        vid = reader.nextInt();
                        newpath.nodes.add(getNodeById(vid));
                        if (v == 0) {
                            continue;
                        }
                        Link e = getLinkBySrcDestId(newpath.nodes.get(v - 1).getId(), vid);
                        if (e == null) {
                            System.err.print("Link not found");
                            System.exit(-1);
                        }
                        newpath.links.add(e);
                    }
                    d.allPaths.add(newpath);
                    reader.nextLine();

                }
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void ExecuteMultihour() {
        if (demands.size() == 0 || timewindows.size() == 0) {
            System.err.println("There is no demands");
            return;
        }

        //initial paths list for each demand d at time t
        for (Time t : timewindows) {
            for (Demand d : demands) {
                d.paths.put(t, new ArrayList<Path>());
            }
        }

        //execute routing at time t
        for (Time t : timewindows) {
            _ExecuteOneTimeWindow(t);
        }
        Fcurrent = calculateObjectiveF();
        Fbest = Fcurrent;

    }

    //routing for all demand at time t
    public void _ExecuteOneTimeWindow(Time t) {
        //reset capacity for node, link, path list of demand
        resetTopology(t);
        demands = sortDemandsDecreaseAccordingToVolume(t, demands);

        //routing for each demand at time t
        for (Demand d : demands) {
            _ExecuteOneTimeWindowForEachDemand(t, d);
        }
    }

    public void _ExecuteOneTimeWindowForEachDemand(Time t, Demand d) {
        //tim tat ca cac duong di ngan nhat
        findMultiShortestPathFor(d);
        //neu map =true chay thu tuc gan node
        System.out.println("Map satisfied Paths");
        boolean mapSatisfiedPaths = false;
        boolean mapFunctionNodeForDemand = false;

        //check and map multi paths for demand d
        //return true if map sucessfull
        //return false if not map sucessfull
        mapSatisfiedPaths = d.mapSatisfiedMultiPaths(t, maxNumberShortestPaths);

        if (mapSatisfiedPaths == true) {
            mapFunctionNodeForDemand = d.mapFunctionNodeForDemand(t);
            if (mapFunctionNodeForDemand == false) {
                System.out.println("demand is rejected");
                d.rollbackLinkResource(t);
                d.getPaths(t).clear();
            }
        }
    }

    //check if demand d is satisfy at time t
    public boolean isSatisfiedDemands(Time t) {
        for (Demand d : demands) {
            if (d.isSatisfied(t) == false) {
                return false;
            }
        }
        return true;
    }

    //objective function
    public double calculateObjectiveF() {
        return calculateMaxRoutingCostAllTime();
    }

    //calculate max routing cost 
    public double calculateMaxRoutingCostAllTime() {
        double F;
        double maxF = 0;
        for (Time t : timewindows) {
            F = calculateRoutingCostAtTime(t);
            if (maxF < F) {
                maxF = F;
            }
        }
        return maxF;
    }

    //calculate routing cost at time t
    public double calculateRoutingCostAtTime(Time t) {
        double F = 0;
        for (Link e : links) {
            F += e.getRoutingCost(t) * calculateTotalTrafficOnLink(t, e);
        }
        return F;
    }

    public void resetOutPathForLink() {
        for (Node v : nodes) {
            v.getOutPaths().clear();
        }
    }

//    //update used resource for demand d at time t
//    public void updateResource(Time t, Demand d) {
//        for (Path p : d.getPaths(t)) {
//            Hashtable<Function, Boolean> isExecuted = new Hashtable<Function, Boolean>();
//            for (Function f : functions) {
//                isExecuted.put(f, false);
//            }
//            //assgin and update used computing capacity for nodes
//            for (Node v : p.getNodes()) {
//                for (Function f : d.getFunctions()) {
//                    if (isExecuted.get(f) == false && (v.getComputingCapacity() - v.getUsedComputingCapacity(t)) >= p.getFlowRate() * f.requiredResource) {
//                        v.setUsedComputingCapacity(t, v.getUsedComputingCapacity(t) + p.getFlowRate() * f.getRequireResource());
//                        isExecuted.put(f, true);
//                    }
//                }
//
//            }
//            //update bandwidth capacity for links
//            for (Link e : p.getLinks()) {
//                e.setUsedBandwidthCapacity(t, e.getUsedBandwidthCapacity(t) + p.getFlowRate());
//            }
//        }
//    }

    public void calculateMultiShortestPathsTo(Node dest) {

        for (Node v : nodes) {
            v.setDistance(-1);
            v.getOutPaths().clear();
            if (v == dest) {
                v.setDistance(0);
            }
        }

//        ArrayList<Node> visitedNodes = new ArrayList<Node>();
        ArrayList<Node> nextNodes = new ArrayList<Node>();
        nextNodes.add(dest);
        while (!nextNodes.isEmpty()) {
            Node v = nextNodes.get(0);
            nextNodes.remove(0);

            for (Link e : v.getInLinks()) {
                if (!e.getEnabled()) {
                    continue;
                }
                Node src = e.getSrcNode();
//                if (!src.getEnabled()) {
//                    continue;
//                }
                double newdist = v.getDistance() + e.getWeight();

                if (src.getDistance() == -1 || src.getDistance() > newdist) {
                    src.setDistance(newdist);
                    src.getOutPaths().clear();
                    src.getOutPaths().add(e);
                    nextNodes.add(src);
                } else if (src.getDistance() == newdist) {
                    if (!src.getOutPaths().contains(e)) {
                        src.getOutPaths().add(e);
                    }
                }

            }

        }

    }

    public void resetTopology(Time t) {
        for (Link e : links) {
            e.reset(t);
        }
        for (Node v : nodes) {
            v.reset(t);
        }
        for (Demand d : demands) {
            d.reset(t);
        }
        acceptedDemand = 0;

    }
    //find all paths for demand

    public Demand findMultiShortestPathFor(Demand d) {
        d.getAllShortestPaths();
        return d;
    }

    //duyet tat ca cac duong di tu nguon den dich cua demand d bang BFS
    public ArrayList<Path> findAllPathsFromSrcToNode(Node srcNode, Node destNode) {
        ArrayList<Path> paths = new ArrayList<Path>();
        System.out.println(" begin find All paths for demand d");
        //tập các đỉnh chờ xét
        Node src = destNode;
        Node dest = srcNode;
        ArrayList<Node> nextNodes = new ArrayList<Node>();
        ArrayList<Node> visitedNodes = new ArrayList<Node>();
        nextNodes.add(src);
        Node v;
        while (!nextNodes.isEmpty()) {
            v = nextNodes.get(0);
            nextNodes.remove(0);
            if (v == dest || visitedNodes.contains(v)) {
                continue;
            }
            for (Link e : v.inLinks) {
                if (!links.contains(e)) {
                    break;
                }
                if (!e.getSrcNode().outPaths.contains(e) && e.getSrcNode() != src) {
                    e.getSrcNode().outPaths.add(e);
                }
                if (!visitedNodes.contains(e.getSrcNode())) {//) && e.overload == false) {
                    nextNodes.add(e.getSrcNode());

                }
            }

            visitedNodes.add(v);
        }
//        System.out.println(" phase1 find All paths for demand d");
        ArrayList<Node> nextNode = new ArrayList<Node>();
        nextNode.add(srcNode);
        ArrayList<Path> tempPath = new ArrayList<Path>();

        //thiết lập đường đi dầu tiên từ src node
        Path path = new Path();
        path.getNodes().add(srcNode);
        //thêm đường đi đầu tiên vào tập các đường đi
        paths.add(path);
        //clone một tập các đường đi trung gian
        Path newp = new Path(path);
        tempPath.add(newp);
        ArrayList<Node> passedNodes = new ArrayList<Node>();
        int numberPathFound = 0;
        while (!nextNode.isEmpty()) {
            Node src2 = nextNode.get(0);
            nextNode.remove(0);
//            passedNodes.add(src2);
            if (foundAllPaths(paths, destNode)) {
                break;
            }
            if (src2 == destNode || src2.getOutPaths().size() < 1) {
                continue;
            }

            //duyet tat ca cac path cua d
            for (Path p : paths) {
                //neu p da den dest hoặc khong di qua src thi bỏ qua, hoặc nút cuối của path không phải là src thì bỏ qua
                if (p.getNodes().contains(destNode) || !p.getNodes().contains(src2) || p.getNodes().get(p.getNodes().size() - 1) != src2) {
                    if (p.getNodes().contains(destNode)) {
                        numberPathFound++;
                    }
                    continue;
                }
//                if (numberPathFound >= 100) {//|| ((p.getLinks().size() >=(links.size()/5)) && ((links.size()/5) >= 10))) {
//                    tempPath = removePath(tempPath, p);
//                    continue;
//                }

//                int numberp = d.getPaths().size();
                //nut cuối của path là src2
                if (p.getNodes().get(p.getNodes().size() - 1) == src2) {// p.getNodes().contains(src2)) {
                    boolean addnewpath = false;
                    //xet tung luong ra e
                    for (Link e : src2.getOutPaths()) {
//                        if (p.hasNode(e.getDestNode()) == 0) {
                        //nếu nút đích của link e không nằm trền path = cả link e không nằm trên path
                        if ((!p.getNodes().contains(e.getDestNode()))) {
                            //tao mot path moi  = path cu + link e
                            Path newpe = new Path(p);
                            newpe.getLinks().add(e);
                            newpe.getNodes().add(e.getDestNode());
                            addnewpath = true;
                            // them path moi vao
                            tempPath.add(newpe);
                            // nếu những nút đã duyệt không có nút đích của e = nút đích của e chưa duyệt
//                            if (!p.getNodes().contains(e.getDestNode()) ){//&& 
//                            if(!nextNode.contains(e.getDestNode()) && !passedNodes.contains(e.getDestNode())) {
                            if (e.getDestNode() != destNode && !nextNode.contains(e.getDestNode())) {
                                nextNode.add(e.getDestNode());
                            }
                        }
                    }
                    //loai bo path p cu
                    if (addnewpath) //                        
                    {
                        tempPath = removePath(tempPath, p);
                    }
                }
                passedNodes.add(src2);

            }
            passedNodes.add(src2);
            paths.clear();
            paths = clonePaths(tempPath);

        }

        for (Path p : paths) {
            if (!p.getNodes().contains(destNode)) {
                tempPath = removePath(tempPath, p);
            }
        }
        paths.clear();
        paths = clonePaths(tempPath);
        System.out.println(" end find All paths for demand d");
        return paths;
    }

//    public Demand calculateECMPFor(Time t, Demand d) {
//        System.out.println(" begin calculateECMPFor");
//        if (d.getPaths(t) == null) {
//            return d;
//        }
//
//        resetOutPathForLink();
//        d.setOutPathForLink(t);
//        d.getPaths(t).clear();
//        ArrayList<Node> nextNode = new ArrayList<Node>();
//        nextNode.add(d.getSrcNode());
//        ArrayList<Path> tempPath = new ArrayList<Path>();
//
//        Path path = new Path();
//        path.setFlowRate(d.getDemandVolume(t));
//        path.getNodes().add(d.getSrcNode());
//        d.getPaths(t).add(path);
//        Path newp = new Path(path);
//        tempPath.add(newp);
//
//        while (!nextNode.isEmpty()) {
//            Node src = nextNode.get(0);
//            nextNode.remove(0);
//            if (src == d.getDestNode() || src.getOutPaths().size() < 1) {
//                continue;
//            }
////            int numberNewPath = src.getOutPaths().size();
////            if (numberNewPath < 1) {
////                continue;
////            }
//            //duyet tat ca cac path cua d
//            for (Path p : d.getPaths(t)) {
//                int numberp = d.getPaths(t).size();
//                //nut nam tren path
//                if (p.getNodes().contains(src) && p.getNodes().get(p.getNodes().size() - 1) == src && !p.getNodes().contains(d.getDestNode())) {
//                    //chia luu luong cua path cho so luong (ECMP)
////                    double newFlowRate = p.getFlowRate() / numberNewPath;
////                    p.setFlowRate(p.getFlowRate() / numberNewPath);
//                    //xet tung luong ra e
//                    int numberNewPath = 0;
//                    boolean addNewPath = false;
//                    for (Link e : src.getOutPaths()) {
//                        if (p.hasNode(e.getDestNode()) == 0) {
//                            numberNewPath++;
//                        }
//                    }
//                    double newFlowRate = p.getFlowRate() / numberNewPath;
//                    for (Link e : src.getOutPaths()) {
//
//                        if (p.hasNode(e.getDestNode()) == 0) {
//                            //tao mot path moi  = path cu + link e
//                            addNewPath = true;
//                            Path newpe = new Path(p);
//                            newpe.setFlowRate(newFlowRate);
//                            newpe.getLinks().add(e);
//                            newpe.getNodes().add(e.getDestNode());
//                            newpe.demand = d;
//                            // them path moi vao
//                            tempPath.add(newpe);
//                            if (e.getDestNode() != d.getDestNode() && !nextNode.contains(e.getDestNode())) {
//                                nextNode.add(e.getDestNode());
//                            }
//                        }
//                    }
//                    //loai bo path p cu
//                    if (addNewPath) {
//                        tempPath = removePath(tempPath, p);
//                    }
//                }
//
//            }
//            d.getPaths(t).clear();
//            d.setPaths(t, tempPath);
//
//        }
//        if (d.getPaths(t).size() == 1 && d.getPaths(t).get(0) == path) {
//            d.getPaths(t).clear();
//        }
//        System.out.println("calculate ECMP");
//        return d;
//    }

//    //GanNode()
//    public void mapNodeForPathsOfDemand(Time t, Demand d) {
//        for (Path p : d.getPaths(t)) {
//            p.mappingFunctionNode(t);
//        }
//    }
    public double randomNumber() {
        Random rn = new Random();
        int random = rn.nextInt(2);
        if (random > 0) {
            return 1;
        }
        return -1;

    }

    //random from 0 to max
    public int randomNumber(int max) {
        Random rn = new Random();
        return rn.nextInt(max);
    }

    //random double from min to max
    public double randomNumber(double rangeMin, double rangeMax) {
        Random r = new Random();
        return rangeMin + (rangeMax - rangeMin) * r.nextDouble();
    }

    public double calculateTotalComputingUtilizationOnNode(Time t, Node v) {
        return v.getUsedComputingCapacity(t) / v.getComputingCapacity();
    }

    // calculate ye sum of traffic rate of all data through link e
    public double calculateTotalTrafficOnLink(Time t, Link e) {
        return e.getUsedBandwidthCapacity(t);// + getReverseLink(e).usedCapacity;
    }
    //calculate r

    public double calculateUtilizationLink(Time t, Link e) {
        return calculateTotalTrafficOnLink(t, e) / e.getBandwidthCapacity();
    }
    //calcualte max r

    public double calculateMaxUtilizationLink(Time t) {

        double r = 0;
        double u = 0;

        for (Link e : links) {
            u = calculateUtilizationLink(t, e);
            if (r < u) {
                r = u;
            }
        }
        return r;
    }

    //kiem tra co link overload
    public boolean hasOverLoadLinks(Time t) {
        for (Link e : links) {
            if (e.isOverLoad(t)) {
                return true;
            }
        }
        return false;
    }

    //kiem tra demand d co thoa man dieu kien link
    public boolean checkOverLoadLinks(Time t, Demand d) {
        for (Link e : links) {
            double used = e.getUsedBandwidthCapacity(t);
            used += getReverseLink(e).getUsedBandwidthCapacity(t);
            for (Path p : d.getPaths(t)) {
                if (p.getLinks().contains(e) || p.getLinks().contains(getReverseLink(e))) {
                    used += p.getFlowRate();
                }
            }
            if (e.getBandwidthCapacity() < used) {
                return true;
            }
        }
        return false;
    }

    public boolean hasOverLoadNodes(Time t) {
        for (Node v : nodes) {
            if (v.isOverLoad(t)) {
                return true;
            }
        }
        return false;
    }

    public Link getReverseLink(Link e) {
        for (Link re : links) {
            if (re.getSrcNode() == e.getDestNode() && re.getDestNode() == e.getSrcNode()) {
                return re;
            }
        }
        return null;
    }

    public int calNumberScheduledDemands(Time t) {
        int satifyDemand = 0;
        for (Demand d : demands) {
            if (d.isSatisfied(t)) {
                satifyDemand++;
            }
        }
        return satifyDemand;
    }

    public double calSatisfiedDemandsVolume(Time t) {
        double total = 0;
        for (Demand d : demands) {
            if (d.isSatisfied(t)) {
                total += d.getDemandVolume(t);
            }
        }
        return total;
    }

    public double calTotalDemandsVolume(Time t) {
        double total = 0;
        for (Demand d : demands) {
            total += d.getDemandVolume(t);
        }
        return total;
    }

    public void printBestUtilizationLink() {
        for (Time t : timewindows) {
            printBestUtilizationLink(t);
        }
    }

    public void printWeightLink() {
        for (Link e : links) {
            System.out.println(e.getSrcNode().getId() + " -> " + e.getDestNode().getId() + " : " + e.weight);
        }
    }
    //calculate R(v,f): total traffic of all data flows through node v and require f

    public void printBestUtilizationLink(Time t) {

        System.out.println("Best Utilization Link: " + Fbest + " at time " + t.getId());
        System.out.println("Routing Cost : " + calculateRoutingCostAtTime(t));
        System.out.println("Accepted Demand: " + calNumberScheduledDemands(t));
        System.out.println("Total traffic link: ");
        for (Link e : links) {
            System.out.println(e.getSrcNode().getId() + " -> " + e.getDestNode().getId() + " : " + e.weight + " : " + calculateTotalTrafficOnLink(t, e) + " ~ " + calculateUtilizationLink(t, e));
        }
    }

    public void printBestRoutingCost() {
        System.out.println("Best RoutingCost: " + Fbest);
    }

    public void printPathsForDemands(Time t) {
        System.out.println("Paths for Demand a time: " + t.getId());
        int numDemand = 0;
        for (Demand d : demands) {
            numDemand++;
            System.out.println("Demand: " + numDemand);
            d.printPaths(t);
        }
    }

    public void printAllShortestPathsForDemands() {
        System.out.println("All Shortest Paths for Demand: ");
        int numDemand = 0;
        for (Demand d : demands) {
            numDemand++;
            System.out.println("Demand: " + numDemand);
            d.printAllShortestPaths();
        }
    }

    public void printTopology() {

        System.out.println(" node number: " + nodes.size());
        for (Node node : nodes) {
            System.out.print("node index: " + node.getId() + " cC: " + node.getComputingCapacity());
            node.printInLinks();
            node.printOutLinks();
            System.out.println();
        };
        System.out.println();
        System.out.println("link number: " + links.size());
        for (Link link : links) {
            System.out.println(link.getSrcNode().getId() + " -> " + link.getDestNode().getId() + " : " + link.getBandwidthCapacity());
            System.out.print("routing cost: ");
            for (Time t : timewindows) {
                System.out.print(link.getRoutingCost(t) + " ");
            }
            System.out.println();
        };

        System.out.println("function number: " + functions.size());
        for (Function func : functions) {
            System.out.print("function id: " + func.getId() + " require : " + func.getRequireResource());
            System.out.println();
        };
        System.out.println();

        printDemands();

    }

    public void printUsedCapacityNodes(Time t) {
        System.out.println("Used Capacity at time : " + t.getId());
        for (Node v : nodes) {
            System.out.println("node: " + v.getId() + " : " + v.getUsedComputingCapacity(t));

        }

    }

    public void printTotalUsedCapacityNodes(Time t) {
        double total = 0;
        for (Node v : nodes) {
            total += v.getUsedComputingCapacity(t);
        }
        System.out.println("Total Capacity at time " + t.getId() + ": " + total);
    }

    public void printTotalBandwidth() {
        double total = 0;
        for (Link e : links) {
            total += e.bandwidthCapacity;
        }
        System.out.println("total bandwidth: " + total);

    }

    public void printTotalDemandVolume(Time t) {
        System.out.println("total demands volume: " + calTotalDemandsVolume(t));

    }

    public void printDemands() {
        System.out.println("demand number: " + demands.size());
        for (Demand demand : demands) {
            System.out.print(demand.getSrcNode().getId() + " -> " + demand.getDestNode().getId());
            System.out.print(" | fcs: " + demand.getFunctions().size() + " | : ");
            for (Function func : demand.getFunctions()) {
                System.out.print(" " + func.getId());
            }
            System.out.println();
            for (Time t : timewindows) {
                System.out.print("Time: " + t.getId());
                System.out.print(" Volume : " + demand.getDemandVolume(t));
//                System.out.println(" accepted: " + demand.isSatisfied(t));
            }
            System.out.println();
        };
    }

    public void printAllDemandPaths() {
        System.out.println("demand number: " + demands.size());
        for (Demand demand : demands) {
            demand.printPathList(demand.getAllPaths());
        };
    }

    public Link getLinkBySrcDestId(int sid, int did) {
        for (Link e : links) {
            if (e.getSrcNode().getId() == sid && e.getDestNode().getId() == did) {
                return e;
            }
        }
        return null;
    }

    public Node getNodeById(int nodeId) {

        for (Node v : nodes) {
            if (v.getId() == nodeId) {
                return v;
            }
        }
        return null;
    }
}
