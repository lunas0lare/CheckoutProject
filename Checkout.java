import java.util.ArrayList;
import java.util.Random;
public class Checkout {

    static final int SECONDS_RUN = 7200;
    static final int ARRIVAL_INTERVAL = 30;
    static final int LEAST_ITEM = 5;
    static final int MOST_ITEM = 40;
    static final int MIN_SCAN_TIME = 5;
    static final int MAX_SCAN_TIME = 15;
    static final int FASTEST_PAY_TIME = 3;
    static final int SLOWEST_PAY_TIME = 10;
    static final int[] MODEL = {1, 2, 3};
    static final int NUM_LINE = 5;
    static final int NUM_SIM = 10;
    static Random rand = new Random();
    

    public static int getLineLength(ArrayList<Queue<Customer>> lines){
       int max = 0;
        for(Queue<Customer> line : lines){
            if(line.size() > max)
                max = line.size();
        }
        return max;
    }
    
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
    
    public static int pickLine(int model, ArrayList<Queue<Customer>> lines, Random rand){
        switch(model){
            case 1: return 0;
            case 2: return shortestLine(lines);
            case 3: return rand.nextInt(lines.size());
            default: return 0;
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

    public static Customer createCustomer(Customer newCustomer, int arrivalTime){
        newCustomer.setItem(rand.nextInt(MOST_ITEM - LEAST_ITEM) + LEAST_ITEM);
        newCustomer.setArrivalTime(arrivalTime);
        newCustomer.setScanTimePerItem(rand.nextInt(MAX_SCAN_TIME - MIN_SCAN_TIME) + MIN_SCAN_TIME);
        newCustomer.setPayTime(rand.nextInt(SLOWEST_PAY_TIME - FASTEST_PAY_TIME) + FASTEST_PAY_TIME);
        return newCustomer;
    } 

    public static void stationProcess(Station station, Customer curCustomer, int tick){
        station.setCurCustomer(curCustomer);

        //waitTime means current time - time when customer arrive. Ex: 500 sec current, arrived at 450
        //wait time is 50
        int waitTime =  tick - curCustomer.getArrivalTime();
        station.getCurCustomer().setTimeWaitInLine(waitTime);

        //remaining time is when customer finish waiting and get in line to checkout
        //see newCustomer.calculateCheckoutTime(); for more information. 
        //set availability of station to false(becaseu it is processing)
        station.setTimeRemaining(curCustomer.getCheckoutTime());
        station.setIsAvailable(false);
    }

    public static ArrayList<Customer> cloneCustomerList(ArrayList<Customer> original){
        //this is for using all the list of customer in 3 different models
        ArrayList<Customer> clone = new ArrayList<>();
        for(Customer c : original){
            clone.add(c.clone());
        }
        return clone;
    }

    public static void main(String[] args) {
    ArrayList<Station> stations = new ArrayList<>();
    ArrayList<Queue<Customer>> lines = new ArrayList<>();
   
    int maxLineLength = 0;
    int totalWaitTime = 0;
    double[] averageWaitTime = {0, 0, 0};
    double[] averageCustomerServed = {0, 0, 0};
    //get number of customer created
    final int NUM_CUSTOMERS = SECONDS_RUN / ARRIVAL_INTERVAL;
   
    ArrayList<Customer> originalCustomers = new ArrayList<>();
    
    //create the original customer list
    for(int i = 0; i < NUM_CUSTOMERS; i++){
        Customer c = new Customer();
        //each customer arrive at ARRIVAL_INTERVAL Ex: 0, 30, 60, 90 if interval is 30.
        c = createCustomer(c, i * ARRIVAL_INTERVAL);
        c.calculateCheckoutTime();
        c.calculateFinishTime();
        originalCustomers.add(c);
    }

    double[] max = {0, 0, 0};
    //----------MAIN CODE----------
    for(int modelPicked : MODEL){
        for(int run_num = 0; run_num < NUM_SIM; run_num++){
            lines.clear();
            stations.clear();
            setupSystem(modelPicked, lines, stations, NUM_LINE);
            Station.resetTotalCustomerServed();
            maxLineLength = -1;
            totalWaitTime = 0;
            ArrayList<Customer> customers = cloneCustomerList(originalCustomers);
            int customerIndex = 0;
    
            for(int tick = 0; tick < SECONDS_RUN; tick++){
                for(Station station : stations){
                    if(!station.getIsAvailable()){
                        station.setTimeRemaining(station.getTimeRemaining() - 1);
                        if(station.getTimeRemaining() <= 0){
                            station.setIsAvailable(true);
                        }
                    }
                }
    
                if(tick % ARRIVAL_INTERVAL == 0){
                    Customer newCustomer = customers.get(customerIndex++);
                    int resultPositionLinePicked = pickLine(modelPicked, lines, rand);
                    lines.get(resultPositionLinePicked).enqueue(newCustomer);
                }
    
                if(modelPicked == 1){
                    Queue<Customer> sharedLine = lines.get(0);
                    for(Station station : stations){
                        if(!station.getIsAvailable()) continue;
                        if(!sharedLine.isEmpty()){
                            Customer curCustomer = sharedLine.dequeue();
                            stationProcess(station, curCustomer, tick);
                            int waitTime = tick - curCustomer.getArrivalTime();
                            totalWaitTime += waitTime;
                        }
                    }
                } else {
                    for(int i = 0; i < stations.size(); i++){
                        Station station = stations.get(i);
                        Queue<Customer> line = lines.get(i);
                        if(station.getIsAvailable() && !line.isEmpty()){
                            Customer curCustomer = line.dequeue();
                            stationProcess(station, curCustomer, tick);
                            int waitTime = tick - curCustomer.getArrivalTime();
                            totalWaitTime += waitTime;
                        }
                    }
                }
    
                maxLineLength = Math.max(maxLineLength, getLineLength(lines));
            }
    
            max[modelPicked - 1] += maxLineLength;
            averageWaitTime[modelPicked - 1] += (double)totalWaitTime / Station.getTotalCustomerServed();
            averageCustomerServed[modelPicked - 1] += Station.getTotalCustomerServed();
        }
    
        
        System.out.println("Model " + modelPicked + ":");
        System.out.println("Average customer served: " + (averageCustomerServed[modelPicked - 1] / NUM_SIM));
        System.out.println("Average line length: " + (max[modelPicked - 1] / NUM_SIM));
        System.out.println("Average wait time: " + (averageWaitTime[modelPicked - 1] / NUM_SIM) + "\n");
    }
}
}