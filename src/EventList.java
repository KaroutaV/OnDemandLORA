import java.util.*;

public class EventList {

    private Queue<Packet> eventQueue;
    private int totalPackets;
    private int nodes;
    private Random random;

    public EventList(int nodes, int totalPackets) {
        this.nodes = nodes;
        this.totalPackets = totalPackets;
        this.eventQueue = new PriorityQueue<>((p1, p2) -> Double.compare(p1.getGeneratedTime(), p2.getGeneratedTime()));
        this.random = new Random();
    }

    // Δημιουργεί πακέτα με βάση την κατανομή Poisson
    public void generatePackets() {
//        int packetCount = 0;
//
//        while (packetCount < totalPackets) {
//            // Δημιουργούμε ένα Poisson δείγμα για το πλήθος των πακέτων από τους κόμβους
//            int packetsPerCycle = poissonDistribution(nodes / 3);
//
//            for (int i = 0; i < packetsPerCycle && packetCount < totalPackets; i++) {
//                int nodeId = random.nextInt(nodes) + 1;  // Τυχαίος κόμβος από τους διαθέσιμους
//                int sequenceNumber = packetCount + 1;
//                long creationTime = (long) (Math.random() * 10000); // Τυχαία στιγμή δημιουργίας
//
//                Packet packet = new Packet(nodeId, creationTime);
//                eventQueue.add(packet);
//                packetCount++;
//            }
//        }

        int packetCount = 0;
        int cycleDuration = 1000; // Διάρκεια κύκλου σε ms
        long cycleStartTime = 0; // Χρόνος έναρξης του κύκλου

        // Συνεχίζουμε να δημιουργούμε πακέτα μέχρι να φτάσουμε τον απαιτούμενο αριθμό
        while (packetCount < totalPackets) {
            // Υπολογισμός του αριθμού των πακέτων για αυτόν τον κύκλο
            int packetsPerCycle = poissonDistribution(nodes / 3); // Χρησιμοποιήστε την κατανομή Poisson
            List<Integer> nodePerCycleList = new ArrayList<>();
            // Δημιουργία τυχαίων κόμβων που θα στείλουν πακέτα
            for (int i = 0; i < packetsPerCycle && packetCount < totalPackets; i++) {
                // Τυχαίος κόμβος από τους διαθέσιμους
                int nodeId;
                boolean unique;
                do {
                    unique = true; // Θεωρούμε ότι ο κόμβος είναι μοναδικός
                    nodeId = random.nextInt(nodes) + 1; // Επιλογή τυχαίου κόμβου

                    // Έλεγχος αν ο κόμβος υπάρχει ήδη
                    for (Integer node : nodePerCycleList) {
                        if (nodeId == node) {
                            unique = false; // Βρέθηκε ίδιος κόμβος
                            break; // Σταματάμε την αναζήτηση
                        }
                    }
                } while (!unique);
                nodePerCycleList.add(nodeId);
                // Δημιουργία πακέτου με τυχαία στιγμή μέσα στον κύκλο
                Packet packet = new Packet(nodeId, cycleStartTime + random.nextInt(cycleDuration));
                eventQueue.add(packet);
                packetCount++;
            }

            // Αναβάθμιση του χρόνου έναρξης του επόμενου κύκλου
            cycleStartTime += cycleDuration; // Μετακίνηση στον επόμενο κύκλο
        }

    }

    // Υπολογισμός κατανομής Poisson
    private int poissonDistribution(double lambda) {
        double l = Math.exp(-lambda);
        int k = 0;
        double p = 1.0;

        do {
            k++;
            p *= random.nextDouble();
        } while (p > l);

        return k - 1;
    }

    // print all the packets from the event list
    public void printEventList() {
        while (!eventQueue.isEmpty()) {
            Packet packet = eventQueue.poll();
            System.out.println(packet);
        }
    }
    public void addSequenceNumber() {
        Queue<Packet> tempQueue = new LinkedList<>();
        while (!eventQueue.isEmpty()) {
            Packet packet = eventQueue.poll();
            packet.setSequenceNumber();
            tempQueue.add(packet);
        }
        eventQueue = tempQueue;

    }
    public boolean isEmpty(){
        return eventQueue.isEmpty();
    }
    public Packet pullPacketFromEventList(){
        return eventQueue.poll();
    }
    public void addPacketToEventList(Packet packet){ eventQueue.add(packet); }

    public Packet findPacketByNodeId(long nodeId) {
        Iterator<Packet> iterator = eventQueue.iterator(); // Δημιουργία iterator για την eventQueue
        while (iterator.hasNext()) {
            Packet packet = iterator.next(); // Παίρνουμε το επόμενο πακέτο
            if (packet.getEndDeviceID() == nodeId) {
                return packet; // Επιστρέφει το πρώτο πακέτο που ταιριάζει
            }
        }
        return null; // Αν δεν βρεθεί, επιστρέφει null
    }
    public void clearEventList(){
        eventQueue.clear();
    }




}
