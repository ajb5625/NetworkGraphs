import java.io.ByteArrayInputStream;
import java.util.LinkedList;
import java.util.Scanner;

public class NetworkTest {
    public static void main(String[] args) {
        String in = "6\n" +
                "47 53 40\n40 26 90\n53 13 5\n40 53 25\n26 47 23\n47 40 48\n";
       ByteArrayInputStream bais = new ByteArrayInputStream(in.getBytes());
//        System.setIn(bais);
        System.setIn(bais);


        Scanner s = new Scanner(System.in);
        Network network = new Network(s);

        network.buildComputerNetwork();
        System.out.println("buildComputerNetwork() complete.");

        try {
            network.buildCluster(2);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        System.out.println("buildCluster() complete.");
        String c = "1\n47 53 10\n";
        ByteArrayInputStream b = new ByteArrayInputStream(c.getBytes());
        System.setIn(b);
        network.connectCluster();
        LinkedList<LinkedList<Integer>> cluster = network.getCluster();
        System.out.println("Clusters: ");
        for (LinkedList clust : cluster) {
            for (int i = 0; i < clust.size(); i++) {
                System.out.print(clust.get(i) + " ");
            }
            System.out.println();
        }

        System.out.println("connectCluster() complete.");
        System.out.println("Router graph: ");
        for (int i = 0; i < network.getRouterGraph().size(); i++) {
            for (int j = 0; j < network.getRouterGraph().get(i).size(); j++) {
                System.out.println(network.getRouterGraph().get(i).get(j)[0] + " " + network.getRouterGraph().get(i).get(j)[1]);
            }
            System.out.println();
        }
        System.out.println("Shortest distance: " + network.traversNetwork("47.26 53.13"));
    }
}