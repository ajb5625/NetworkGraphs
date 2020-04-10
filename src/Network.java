import java.util.*;
/**
 * A simple Network class to build a network
 *
 * @author Vaastav Arora, arora74@purdue.edu
 */
public class Network {
    public class Edge {
        public int startvertex;
        public int endvertex;
        public Edge(int startvertex, int endvertex) {
            this.startvertex = startvertex;
            this.endvertex = endvertex;
        }
    }

    public class Vertex implements Comparator{
        public int name;
        public int distance;

        public Vertex() { }

        public Vertex(int name, int distance) {
            this.distance = distance;
            this.name = name;
        }

        @Override
        public int compare(Object o1, Object o2) {
            Vertex e = (Vertex)o1;
            Vertex f = (Vertex)o2;
            if (e.distance < f.distance) {
                return -1;
            }
            if (f.distance < e.distance) {
                return 1;
            }
            return 0;
        }
    }

   
    /**
     * computerConnections represents list of all inter-computer edges
     * Each edge is an Integer[] of size 3
     * edge[0] = source computer index ( Not IP, it's the Index !)
     * edge[1] = destination computer index ( Not IP, it's the Index !)
     * edge[2] = latency/edge weight
     */
    private LinkedList<Integer[]> computerConnections;
    /**
     * Adjacency List representing computer graph
     */
    private LinkedList<LinkedList<Integer>> computerGraph;
    /**
     * LinkedList of clusters where each cluster is represented as a LinkedList of computer IP addresses
     */
    private LinkedList<LinkedList<Integer>> cluster;
    /**
     * Adjacency List representing router graph
     */
    private LinkedList<LinkedList<Integer[]>> routerGraph;

    private Integer computergraphvertices;

    private HashMap indextoip;


    private Integer numrouters;


    private HashMap riptoindex;


    Scanner s; // Scanner to read Stdin input
    
    //Add your own field variables as required


    /**
     * Default Network constructor, initializes data structures
     * @param s Provided Scanner to be used throughout program
     */
    public Network(Scanner s) {

        //TODO
        this.computerConnections = new LinkedList<Integer[]>();
        this.computerGraph = new LinkedList<LinkedList<Integer>>();
        this.cluster = new LinkedList<LinkedList<Integer>>();
        this.routerGraph = new LinkedList<LinkedList<Integer[]>>();
        this.computergraphvertices = 0;
        this.indextoip = null;
        this.s = s;
        this.numrouters = 0;
        this.riptoindex = null;
    }

    /**
     * Method to parse Stdin input and generate inter-computer edges
     * Edges are stored within computerConnections
     *
     * First line of input => Number of edges
     * All subsequent lines => [IP address of comp 1] [IP address of comp 2] [latency of connection]
     */
    public void buildComputerNetwork() {
        HashMap iptoindex = new HashMap();
        HashMap indextoip = new HashMap();
        int edges = 0;
        int index = 0;
        String input = "";
        int comp1 = 0;
        int comp2 = 0;
        int latency = 0;
        String [] all = new String[3];
        edges = Integer.parseInt(s.nextLine());
        for (int i = 0; i < edges; i++) {
            input = s.nextLine();
            all = input.split(" ");
            comp1 = Integer.parseInt(all[0]);
            comp2 = Integer.parseInt(all[1]);
            latency = Integer.parseInt(all[2]);
            if (!iptoindex.containsKey(comp1)) {
                iptoindex.put(comp1, index);
                indextoip.put(index++, comp1);
            }
            if (!iptoindex.containsKey(comp2)) {
                iptoindex.put(comp2, index);
                indextoip.put(index++, comp2);
            }
            Integer [] toadd = new Integer[3];
            toadd[0] = (Integer) iptoindex.get(comp1);
            toadd[1] = (Integer) iptoindex.get(comp2);
            toadd[2] = latency;
            this.computerConnections.add(i, toadd);
            this.computergraphvertices = index;
        }
        this.indextoip = indextoip;
    }

    /**
     * Method to generate clusters from computer graph
     * Throws Exception when cannot create required clusters
     * @param k number of clusters to be created
     */
    public void buildCluster(int k) throws Exception {
        //TODO
        if (k > this.computergraphvertices) {
            throw new Exception("Cannot create clusters");
        }
        PriorityQueue pq = new PriorityQueue();
        HashMap lat = new HashMap();
        for (int j = 0; j < this.computerConnections.size(); j++) {
            Edge e = new Edge(this.computerConnections.get(j)[0], this.computerConnections.get(j)[1]);
            lat.put(this.computerConnections.get(j)[2], e);
        }
        for (int i = 0; i < this.computerConnections.size(); i++) {
            pq.add(this.computerConnections.get(i)[2]);
        }
        for (int i = 0; i < this.computergraphvertices; i++) {
            LinkedList a = new LinkedList();
            this.computerGraph.add(a);
    }
        UnionFind a = new UnionFind(this.computergraphvertices);
        while (a.components() != k) {
            Edge x = (Edge) lat.get(pq.remove());
            if (a.find(x.startvertex) != a.find(x.endvertex)) {
                this.computerGraph.get(x.startvertex).add(x.endvertex);
                this.computerGraph.get(x.endvertex).add(x.startvertex);
                a.union(x.startvertex,x.endvertex);
            }
        }
        int clusters = 0;
        LinkedList<Integer> prefixes= new LinkedList<>();
        HashMap visited = new HashMap();
        for (int i = 0; i < this.computerGraph.size(); i++) {
            visited.put(i, false);
        }
        int dfscount = 0;
        for (int i = 0; i < computerGraph.size(); i++) {
            if (dfscount >= k) {
                break;
            }
            if (computerGraph.get(i).size() == 0) {
                dfscount++;
                LinkedList<Integer> t = new LinkedList<Integer>();
                t.add((Integer) indextoip.get(i));
                this.cluster.add(clusters++, t);
            }
            for (int j = 0; j < computerGraph.get(i).size(); j++) {
                if (dfscount >= k) {
                    break;
                }
                if (!(boolean) visited.get(this.computerGraph.get(i).get(j))) {
                    LinkedList<Integer> traversal = new LinkedList<Integer>();
                    int prefix = 0;
                    dfscount++;
                    traversal = DFS(this.computerGraph.get(i).get(0), traversal, visited);
                    for (int n = 0; n < traversal.size(); n++) {
                        if (traversal.get(n) > prefix) {
                            prefix = traversal.get(n);
                        }
                    }
                    prefixes.add(prefix);
                    this.cluster.add(clusters++, traversal);
                }
            }
        }
    }
    public LinkedList<Integer> DFS(Integer vertex, LinkedList<Integer> traversal, HashMap visited) {
        traversal.add((Integer) indextoip.get(vertex));
        visited.put(vertex, true);
        for (int i = 0; i < computerGraph.get(vertex).size(); i++) {
            if (!(boolean)visited.get(computerGraph.get(vertex).get(i))) {
                DFS(computerGraph.get(vertex).get(i), traversal,visited);
            }
        }
        return traversal;
    }

    /**
     * Method to parse Stdin input and generate inter-router edges
     * Graph is stored within routerGraph as an adjacency list
     *
     * First line of input => Number of edges
     * All subsequent lines => [IP address of Router 1] [IP address of Router 2] [latency of connection]
     */
    public void connectCluster() {
        int edges = 0;
        int index = 0;
        HashMap r = new HashMap();
        String ui = "";
        int rout1 = 0;
        int rout2 = 0;
        int lat = 0;
        edges = Integer.parseInt(s.nextLine());
        for (int i = 0; i < edges; i++) {
            ui = s.nextLine();
            String [] all = ui.split(" ");
            rout1 = Integer.parseInt(all[0]);
            rout2 = Integer.parseInt(all[1]);
            lat = Integer.parseInt(all[2]);
            if (!r.containsKey(rout1)) {
                r.put(rout1, index);
                index++;
            }
            if (!r.containsKey(rout2)) {
                r.put(rout2, index);
                index++;
            }
            Integer [] toadd = new Integer[2];
            toadd[0] = (Integer) r.get(rout1);
            toadd[1] = lat;
            Integer[] opp = new Integer[2];
            opp[0] = (Integer) r.get(rout2);
            opp[1] = lat;
            if (index - routerGraph.size() == 2) {
                LinkedList memory = new LinkedList<>();
                LinkedList mem2 = new LinkedList();
                this.routerGraph.add(memory);
                this.routerGraph.add(mem2);
                memory.add(opp);
                mem2.add(toadd);
            }
            else if (index - routerGraph.size() == 1) {
                LinkedList n = new LinkedList();
                this.routerGraph.add(n);
                if (toadd[0] > opp[0]) {
                    n.add(opp);
                    routerGraph.get((Integer) r.get(rout2)).add(toadd);
                }
                else {
                    n.add(toadd);
                    routerGraph.get((Integer) r.get(rout1)).add(opp);
                }
            }
            else {
                this.routerGraph.get((Integer) r.get(rout2)).add(toadd);
                this.routerGraph.get((Integer) r.get(rout1)).add(opp);
            }
        }
        this.numrouters = index;
        this.riptoindex = r;
    }

    /**
     * Method to take a traversal request and find the shortest path for that traversal
     * Traversal request passed in through parameter test
     * Format of Request => [IP address of Source Router].[IP address of Source Computer] [IP address of Destination Router].[IP address of Destination Computer]
     * Eg. 123.456 128.192
     *  123 = IP address of Source Router
     *  456 = IP address of Source Computer
     *  128 = IP address of Destination Router
     *  192 = IP address of Destination Computer
     * @param test String containing traversal input
     * @return Shortest traversal distance between Source and Destination Computer
     */
    public int traversNetwork(String test) {
        //TODO
        String [] ip = test.split(" ");
        String[] s = ip[0].split("\\.");
        String[] destination = ip[1].split("\\.");
        Integer sourcerouter = Integer.parseInt(s[0]);
        Integer sourcecomputer = Integer.parseInt(s[1]);
        Integer destrouter = Integer.parseInt(destination[0]);
        Integer destcomputer = Integer.parseInt(destination[1]);
        int tf = 0;
        int r1 = 0;
        int r2 = 0;
        for (int i = 0; i < cluster.size(); i++) {
            for (int j = 0; j < cluster.get(i).size(); j++) {
                if (cluster.get(i).get(j).equals(sourcerouter)) {
                    r1 = 1;
                }
                if (cluster.get(i).get(j).equals(destrouter)) {
                    r2 = 1;
                }
            }
        }
        if (r1 == 0 || r2 == 0) {
            return -1;
        }
        for (int i = 0; i < cluster.size(); i++) {
            for (int j = 0; j < cluster.get(i).size(); j++) {
                if (cluster.get(i).get(j) == sourcerouter) {
                    for (int k = 0; k < cluster.get(i).size(); k++) {
                        if (cluster.get(i).get(k) == sourcecomputer) {
                            tf = 1;
                            break;
                        }
                    }
                    if (tf == 0) {
                        return -1;
                    }
                }
            }
        }
        tf = 0;
        for (int i = 0; i < cluster.size(); i++) {
            for (int j = 0; j < cluster.get(i).size(); j++) {
                if (cluster.get(i).get(j) == destrouter) {
                    for (int k = 0; k < cluster.get(i).size(); k++) {
                        if (cluster.get(i).get(k) == destcomputer) {
                            tf = 1;
                            break;
                        }
                    }
                    if (tf == 0) {
                        return -1;
                    }
                }
            }
        }
        LinkedList<Vertex> vertices = new LinkedList<>();
        Integer[] distances = new Integer[numrouters];
        Integer[] previous = new Integer[numrouters];
        PriorityQueue<Vertex> min = new PriorityQueue<Vertex>(numrouters, new Vertex());
        Vertex source = new Vertex((Integer)riptoindex.get(Integer.parseInt(s[0])), 0);
        distances[source.name] = 0;
        int inf = Integer.MAX_VALUE;
        for (int i = 0; i < numrouters; i++) {
            if (source.name != i) {
                distances[i] = inf;
            }
            previous[i] = -1;
            Vertex v = new Vertex(i, distances[i]);
            vertices.add(v);
            min.add(v);
        }
        int alt = 0;
        while (!min.isEmpty()) {
            Vertex u = min.poll();
            for (int i = 0; i < routerGraph.get(u.name).size(); i++) {
                    alt = routerGraph.get(u.name).get(i)[1] + distances[u.name];
                    if (alt < distances[routerGraph.get(u.name).get(i)[0]]) {
                        distances[routerGraph.get(u.name).get(i)[0]] = alt;
                        previous[routerGraph.get(u.name).get(i)[0]] = u.name;
                        min.add(new Vertex(routerGraph.get(u.name).get(i)[0], alt));
                    }
            }
        }
        return distances[(Integer) riptoindex.get(destrouter)];
    }
    
    //Add your own methods as required


    public LinkedList<Integer[]> getComputerConnections() {
        return computerConnections;
    }

    public LinkedList<LinkedList<Integer>> getComputerGraph() {
        return computerGraph;
    }

    public LinkedList<LinkedList<Integer>> getCluster() {
        return cluster;
    }

    public LinkedList<LinkedList<Integer[]>> getRouterGraph() {
        return routerGraph;
    }

}
