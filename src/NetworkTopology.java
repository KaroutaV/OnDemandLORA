import java.util.ArrayList;
import java.util.List;

public class NetworkTopology {
    private int numberOfNodes ;
    private int numberOfClusterHeads = 1;
    private int totalPackets = 10; //500 ;
    private int cycle = 0 ;
    private long startOfCycle = 0;
    private long endOfCycle = 1000;
    private EventList eventList1;

    public NetworkTopology(int numberOfNodes, int numberOfClusterHeads){
        this.numberOfNodes = numberOfNodes;
        this.numberOfClusterHeads = numberOfClusterHeads;
        eventList1 = new EventList(this.numberOfNodes,totalPackets);
    }
    public void startSimulation(){
        List<EndDevice> endDeviceList = new ArrayList<>();

        LoraRadioSettings lora = new LoraRadioSettings(9,"4/5",500,
                0.976,10,8, 868,8);

        //creating end devices
        for(int i=0; i<numberOfNodes ; i++){
            endDeviceList.add(new EndDevice(i+1));
            endDeviceList.get(i).setTimeOnAir(lora);
        }

        //only one clusterHead
        ClusterHead clusterHead = new ClusterHead(1);
        clusterHead.setEndDevicesList(endDeviceList);
        clusterHead.setTimeOnAir(lora);

        //it works only for one ch
        Sink sink = new Sink(1, clusterHead);
        for(EndDevice endDevice:endDeviceList){
            endDevice.setSink(sink);
        }

        //creating an event list
        EventList eventList = new EventList(numberOfNodes, totalPackets);
        eventList.generatePackets();
        eventList.addSequenceNumber();
        //eventList.printEventList(); // after this the eventlist is empty

        Packet packet = eventList.pullPacketFromEventList();
        long startOfNextCycle = endOfCycle;
        long endOfNextCycle = startOfNextCycle + 1000;

        while(!eventList.isEmpty()){
            if (packet.getGeneratedTime()<=endOfCycle){
                eventList1.addPacketToEventList(packet);
                packet = eventList.pullPacketFromEventList();
                if(eventList.isEmpty()){
                    if(packet.getGeneratedTime()<=endOfCycle) {
                        eventList1.addPacketToEventList(packet);
                        dataCollection(sink);
                    }else{
                        dataCollection(sink);

                        //new cycle
                        startOfCycle = startOfNextCycle;
                        endOfCycle = endOfNextCycle;
                        startOfNextCycle = endOfCycle;
                        endOfNextCycle = startOfNextCycle + 1000;

                        eventList1.addPacketToEventList(packet);

                        dataCollection(sink);
                    }
                }
            }else{
                dataCollection(sink);

                startOfCycle = startOfNextCycle;
                endOfCycle = endOfNextCycle;
                startOfNextCycle = endOfCycle;
                endOfNextCycle = startOfNextCycle + 1000;
            }
        }

        System.out.println();
        System.out.println();
        sink.printLatencies();
    }

    public void dataCollection(Sink sink){
        System.out.println();
        System.out.println();
        System.out.println("sink sends a broadcast command to receive the data of the previous cycle " +
                startOfCycle + " : " + endOfCycle );
        System.out.println();
        System.out.println();

        cycle ++;
        System.out.println("current cycle : " + cycle);

        EndDevice.setPrevCycleEventList(eventList1);
        sink.sendCommand(numberOfNodes+1,cycle);

        EndDevice.getPrevCycleEventList().clearEventList();
    }

}
