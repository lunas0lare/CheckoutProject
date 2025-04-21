

public class Station {
    private Boolean isAvailable;
    private static int totalCustomerServed = 0;
    private int customerServed;
    private Customer curCustomer;
    private int timeRemaining;

    public Station(){
        this.isAvailable = true;
        //need to set to true because default is false. If not method calTime Remainin will be called
        //in the first place and can't operate because curCustomer is null -> curCustomer.getFinishTime()
        //does not exist
        this.curCustomer = null;
        this.timeRemaining = 0;
    }
    
    //setters
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    
    public void setCurCustomer(Customer curCustomer) {
        this.curCustomer = curCustomer;
        Station.totalCustomerServed++;
        this.customerServed++;
    }
    
    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    //getters
    public Boolean getIsAvailable() {
        return isAvailable;
    }
    
    public static int getTotalCustomerServed() {
        return totalCustomerServed;
    }

    public static void resetTotalCustomerServed() {
        totalCustomerServed = 0;
    }
    
    public int getNumCustomerServed() {
        return customerServed;
    }

    public Customer getCurCustomer() {
        return curCustomer;
    }
    
    public int getTimeRemaining() {
        return timeRemaining;
    }

    @Override
    public String toString() {
        return "Station{" +
                "isAvailable= " + isAvailable +
                ", currentCustomer= " + (curCustomer != null ? curCustomer.toString() : "None") +
                ", timeRemaining= " + timeRemaining +
                ", CustomersServed= " + customerServed +
                '}';
    }
}
