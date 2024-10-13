import java.util.List;

public class EndDevice {
    private long endDeviceID;
    private int timeOnAir;
    private boolean isAwake = false; //sleep mode
    private static EventList prevCycleEventList;
    private long guardTime = 6; // ms
    private Sink sink ;

    public EndDevice(long endDeviceID){ this.endDeviceID=endDeviceID;}

    public static EventList getPrevCycleEventList() {
        return prevCycleEventList;
    }
    public static void setPrevCycleEventList(EventList prevCycleEventList) {
        EndDevice.prevCycleEventList = prevCycleEventList;
    }

    public void setTimeOnAir(LoraRadioSettings settings) {
        this.timeOnAir = settings.calculateTimeOnAir();
    }
    public void printToa(){
        System.out.println(timeOnAir);
    }

    public Sink getSink() {
        return sink;
    }

    public void setSink(Sink sink) {
        this.sink = sink;
    }

    public void receiveBroadcastWakeUpBeacon(long wubArrivalTime){
        isAwake = true ;
        long Tslot = wubArrivalTime + (timeOnAir + guardTime)*endDeviceID;
        sendPacket(Tslot);
    }
    public void sendPacket(long Tslot){
        Packet p = prevCycleEventList.findPacketByNodeId(endDeviceID);
        if(p==null){
            System.out.println("The node with id " + endDeviceID + " has no data to transmit in this cycle.");
        }else{
            p.setTimeslotStart(Tslot);
            System.out.println(p);
            System.out.println("The node with id " + endDeviceID + " started transmitting at " + Tslot + " ms.");
            sink.receivePacket(p);
        }
    }

}
