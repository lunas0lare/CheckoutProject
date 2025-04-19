public class Station {
    private Boolean isAvailable;
    private static int customerServed = 0;
    private Customer curCustomer;
    private int timeRemaining;

    //setters
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    
    public void setCustomerServed(int customerServed) {
        this.customerServed = customerServed;
    }
    
    public void setCurCustomer(Customer curCustomer) {
        this.curCustomer = curCustomer;
    }
    
    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    //getters
    public Boolean getIsAvailable() {
        return isAvailable;
    }
    
    public int getCustomerServed() {
        return customerServed;
    }
    
    public Customer getCurCustomer() {
        return curCustomer;
    }
    
    public int getTimeRemaining() {
        return timeRemaining;
    }

    private void calTimeRemaining(int tick){
        this.timeRemaining = curCustomer.getFinishTime() - tick;
    }
    public void tick(int tick){
        if(!this.isAvailable){
            calTimeRemaining(tick);
        }
        if(this.timeRemaining <= 0){
            
            this.isAvailable = true;
            this.curCustomer = null;
            Station.customerServed++;
        }
    }
}
