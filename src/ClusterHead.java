import java.util.ArrayList;
import java.util.List;

public class ClusterHead {
    private long clusterHeadID;
    private int timeOnAir;
    private List<EndDevice> endDevicesList;

    public ClusterHead(long clusterHeadID){
        this.clusterHeadID=clusterHeadID;
        endDevicesList = new ArrayList<>();
    }

    public long getClusterHeadID() {
        return clusterHeadID;
    }

    public void setClusterHeadID(long clusterHeadID) {
        this.clusterHeadID = clusterHeadID;
    }

    public int getTimeOnAir() {
        return timeOnAir;
    }

    public List<EndDevice> getEndDevicesList() {
        return endDevicesList;
    }

    public void setEndDevicesList(List<EndDevice> endDevicesList) {
        this.endDevicesList = endDevicesList;
    }

    public void setTimeOnAir(LoraRadioSettings settings) {
        this.timeOnAir = settings.calculateTimeOnAir();
    }

    public void receiveCommand(long endDeviceID){
        if(endDeviceID==(endDevicesList.size()+1)){
            sendBroadcastWakeUpBeacon();
        }
    }

    public void sendBroadcastWakeUpBeacon(){
        for(EndDevice endDevice:endDevicesList){
            long wubArrivalTime = 17 ; //ms
            endDevice.receiveBroadcastWakeUpBeacon(wubArrivalTime);
        }
    }
}
