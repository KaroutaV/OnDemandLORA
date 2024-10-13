import java.util.ArrayList;
import java.util.List;

public class Sink {
    private long id;
    private ClusterHead clusterHead;
    private List<Packet> receivedPackets = new ArrayList<>();
    private List<Long> receivedPacketTimeslot = new ArrayList<>();
    private int currentCycle;
    private int latency;
    private List<Integer> listOfLatencies = new ArrayList<>();

    public Sink(long id, ClusterHead clusterHead) {
        this.id = id;
        this.clusterHead = clusterHead;
    }

    public void sendCommand(long endDeviceID, int currentCycle){
        this.currentCycle = currentCycle;
        clusterHead.receiveCommand(endDeviceID);
        latency = 0 ;
    }
    public void receivePacket(Packet packet) {
        receivedPackets.add(packet);

        latency = (int) (latency + packet.getTimeslotStart() + clusterHead.getTimeOnAir());

        // Αν το currentCycle είναι μεγαλύτερο από το μέγεθος της λίστας, συμπλήρωσε κενές τιμές
        while (listOfLatencies.size() < currentCycle) {
            listOfLatencies.add(0);  // Προσθήκη default τιμών (π.χ. 0)
        }

        // Ενημέρωση της λίστας με την καθυστέρηση για το currentCycle
        listOfLatencies.set(currentCycle - 1, latency);  // currentCycle - 1 λόγω 0-based indexing
    }

    public void printLatencies(){
        int i= 1;
        for(Integer latency: listOfLatencies){
            System.out.println("Cycle " + i + " latency " + latency);
            i++;
        }
    }

}
