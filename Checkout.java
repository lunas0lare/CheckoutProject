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
   
    int maxLineLength;
    int totalWaitTime;
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

for(int modelPicked : MODEL){
    //loop through models
    //because we loop through models in the main, we have to clean a data  before go to the next model.
    lines.clear();
    stations.clear();

    setupSystem(modelPicked,lines, stations, NUM_LINE);
    Station.resetTotalCustomerServed();
    maxLineLength = -1;
    totalWaitTime = 0;

    //clone customer list
    ArrayList<Customer> customers = cloneCustomerList(originalCustomers);
    //use index to access the arraylist, reset after each model
    int customerIndex = 0;

    for(int tick = 0; tick < SECONDS_RUN; tick++){
         //start ticking for each station.
         //this model is tick based, so it run on ticks when the model doesn't have anything to do.
         //event-based model with jump to the tick that station is available or customer arrive.
         
         //for function check each station avalability: if station is busy then decrement each second
         //until the station remaining time(the time remaining when customer finish checking) is 0 then it is available again
         for(Station station : stations){
             if(!station.getIsAvailable()){
                 station.setTimeRemaining(station.getTimeRemaining() - 1);
                 if(station.getTimeRemaining() <= 0){
                     station.setIsAvailable(true);
                 }
             }
         }
         
        
         //customer arrive every 30 second. 
         if(tick % ARRIVAL_INTERVAL == 0){
            Customer newCustomer = customers.get(customerIndex++);
             //calculate line picked based on model pick, see function for details.
             int resultPositionLinePicked = pickLine(modelPicked, lines, rand);
             //put a customer in a picked queue.
             lines.get(resultPositionLinePicked).enqueue(newCustomer);
            
         }
         
         if(modelPicked == 1){
             Queue<Customer> sharedLine = lines.get(0);
             
             //loop through each station, check available station to jump in, then if the
             //line is not empty then process the customer
             for(Station station : stations){
                 if(!station.getIsAvailable()) continue;
                 if(!sharedLine.isEmpty()){
                     //move a customer from a line to the station
                     Customer curCustomer = sharedLine.dequeue();
                     stationProcess(station, curCustomer, tick);
                     int waitTime =  tick - curCustomer.getArrivalTime();
                     totalWaitTime += waitTime;
                 }
             }
         }
         else{
             for(int i = 0; i < stations.size(); i++){
                 //because model 2 and 3 have the same num station and queue
                 Station station = stations.get(i);
                 Queue<Customer> line = lines.get(i);
                 if(station.getIsAvailable() && !line.isEmpty()){
                     //move a customer from a line to the station
                     Customer curCustomer = line.dequeue();
                     stationProcess(station, curCustomer, tick);
                     int waitTime =  tick - curCustomer.getArrivalTime();
                     totalWaitTime += waitTime;
                 }          
             }
     }
         //calculate max line lenth
     maxLineLength = Math.max(maxLineLength, getLineLength(lines));
 
     // for(Station station : stations){
     //     System.out.println(station + "\n");
     // }
    }
    double averageWaitTime = (double)totalWaitTime / Station.getTotalCustomerServed();
    System.out.println("total customer served: " + Station.getTotalCustomerServed());
    System.out.println("Max line length: " + maxLineLength);
    System.out.println("average wait time: " + averageWaitTime + "\n");
    
    
 }
}
}