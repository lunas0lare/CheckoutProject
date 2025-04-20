import java.util.ArrayList;
import java.util.Random;
public class Checkout {

    static final int SECONDS_RUN = 7200;
    static final int ARRIVAL_CHANCE = 30;
    static final int LEAST_ITEM = 10;
    static final int MOST_ITEM = 40;
    static final int SCAN_TIME = 5;
    static final int FASTEST_PAY_TIME = 20;
    static final int SLOWEST_PAY_TIME = 40;
    static final int[] MODEL = {1, 2, 3};

    public static void setupSystem(int model, ArrayList<Queue<Customer>> lines, ArrayList<Station> stations,int numLines){
        
        if(model != 1){
            for(int i = 0; i < numLines; i++){
                lines.add(new Queue<>());
            }
        }
        else{
            lines.add(new Queue<>());
        }
       
        if(model != 1){
            for(int i = 0; i < numLines; i++){
                stations.add(new Station());
            }
           
        }
        else{
            for(int i = 0; i < numLines; i++){
                stations.add(new Station());
            }
        }
        
    }

    public static int shortestLine(ArrayList<Queue<Customer>> lines){
        int min = 0;
        int minSize = lines.get(0).size();
        for(int i = 1; i < lines.size(); i++){
            if(lines.get(i).size() < minSize){
                minSize = lines.get(i).size();
                min = i;
            }
            
        }
        return min;
    }

    public static int pickLine(int model, ArrayList<Queue<Customer>> lines, Random rand){
        switch(model){
            case 1: return 0;
            case 2: return shortestLine(lines);
            case 3: return rand.nextInt(lines.size());
            default: return 0;
        }
    }

    public static int getLineLength(ArrayList<Queue<Customer>> lines){
       int max = 0;
        for(Queue<Customer> line : lines){
            if(line.size() > max)
                max = line.size();
        }
        return max;
    }
    public static void main(String[] args) {

    ArrayList<Station> stations = new ArrayList<>();
    ArrayList<Queue<Customer>> lines = new ArrayList<>();
    Random rand = new Random();
    int modelPicked = MODEL[0];
    int maxLineLength = -1;
    
    setupSystem(modelPicked,lines, stations, 5);
    
    for(int tick = 0; tick < SECONDS_RUN; tick++){
        //start ticking for each station.
        int resultPositionLinePicked = pickLine(modelPicked, lines, rand);
        for(Station station : stations){
            station.tick(tick);
        }
        //rand(30) == 0 means 1 in 30 chances there's a 0.
        //-> at least 1 person in 30 sec
        
        if(rand.nextInt(ARRIVAL_CHANCE) == 0){
            Customer newCustomer = new Customer();
            newCustomer.setItem(rand.nextInt(MOST_ITEM - LEAST_ITEM) + LEAST_ITEM);
            newCustomer.setArrivalTime(tick);
            newCustomer.setScanTimePerItem(SCAN_TIME);
            newCustomer.setPayTime(rand.nextInt(SLOWEST_PAY_TIME - FASTEST_PAY_TIME) + FASTEST_PAY_TIME);
            //HAVE TO set all the above first before call 2 below method. See method for details.
            newCustomer.calculateCheckoutTime();
            newCustomer.calculateFinishTime();
            
            lines.get(resultPositionLinePicked).enqueue(newCustomer);
            
        }
        
        if(modelPicked == 1){
            Queue<Customer> sharedLine = lines.get(0);
            
            for(Station station : stations){
                if(!station.getIsAvailable()) continue;

                if(!sharedLine.isEmpty()){
                    Customer curCustomer = sharedLine.peek();
                    int waitTime = curCustomer.getFinishTime() - curCustomer.getCheckoutTime() - curCustomer.getArrivalTime();
                    station.setCurCustomer(sharedLine.dequeue());
                    station.getCurCustomer().setTimeWaitInLine(waitTime);
                    station.setIsAvailable(false);
                }
            }
        }
        else{
            for(int i = 0; i < stations.size(); i++){
                Station station = stations.get(i);
                Queue<Customer> line = lines.get(i);
                if(station.getIsAvailable() && !line.isEmpty()){
                    Customer curCustomer = line.peek();
                    int waitTime = curCustomer.getFinishTime() -curCustomer.getCheckoutTime() - curCustomer.getArrivalTime();
                    station.setCurCustomer(line.dequeue());
                    station.getCurCustomer().setTimeWaitInLine(waitTime);
                    station.setIsAvailable(false);
                }          
            }
        }
    maxLineLength = Math.max(maxLineLength, getLineLength(lines));
    }

    // for(Station station : stations){
    //     System.out.println(station + "\n");
    // }
    System.out.println(Station.getTotalCustomerServed());
    System.out.println(maxLineLength);
    }
}
