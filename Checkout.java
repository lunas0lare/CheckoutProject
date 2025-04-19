import java.util.ArrayList;
import java.util.Random;
public class Checkout {
    public static void setupSystem(int model, ArrayList<Queue<Customer>> lines, ArrayList<Station> stations,int numLines, int numStations){
        
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

    public static void main(String[] args) {
    final int SECONDS_RUN = 7200;
    final int ARRIVAL_CHANCE = 30;
    final int LEAST_ITEM = 5;
    final int MOST_ITEM = 20;
    final int SCAN_TIME = 3;
    final int FASTEST_PAY_TIME = 3;
    final int SLOWEST_PAY_TIME = 10;
    final int[] MODEL = {1, 2, 3};
    ArrayList<Customer> servedCustomer = new ArrayList<>(); 
    ArrayList<Station> stations = new ArrayList<>();
    ArrayList<Queue<Customer>> lines = new ArrayList<>();
    Random rand = new Random();
    
    setupSystem(MODEL[1],lines, stations, 5, 5);
    
    for(int tick = 0; tick < SECONDS_RUN; tick++){
        //start ticking for each station.
         int resultPositionLinePicked = pickLine(MODEL[1], lines, rand);
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

        for(int i = 0; i < stations.size(); i++){
            Station station = stations.get(i);
            Queue<Customer> line = lines.get(i);
            if(station.getIsAvailable() && !line.isEmpty()){
                Customer curCustomer = line.peek();
                int waitTime = curCustomer.getFinishTime() -curCustomer.getCheckoutTime() - curCustomer.getArrivalTime();
                station.setCurCustomer(line.dequeue());
                station.getCurCustomer().setTimeWaitInLine(waitTime);
                station.setIsAvailable(false);
                servedCustomer.add(station.getCurCustomer());
            }          
        }
    }

    // for(Customer customer : servedCustomer){
    //     System.out.println(customer);
    // }

    // System.out.println(Station.getTotalCustomerServed());

    for(Station station : stations){
        System.out.println(station + "\n");
    }
    }
}
