//customer class
public class Customer {
    private int item;
    private int arrivalTime;
    private int timeWaitInLine; 
    private int checkoutTime;
    private int finishTime;
    private int scanTimePerItem;
    private int payTime;

    //constructor

     // Setters
     public void setItem(int item) {
        this.item = item;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }


    public void setTimeWaitInLine(int timeWait) {
        this.timeWaitInLine = timeWait;
    }

    public void setCheckoutTime(int checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public void setScanTimePerItem(int scanTimePerItem) {
        this.scanTimePerItem = scanTimePerItem;
    }

    public void setPayTime(int payTime) {
        this.payTime = payTime;
    }

    // Getters
    public int getItem() {
        return item;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getTimeWaitInLine() {
        return timeWaitInLine;
    }

    public int getCheckoutTime() {
        return checkoutTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public int getScanTimePerItem() {
        return scanTimePerItem;
    }

    public int getPayTime() {
        return payTime;
    }

    public void calculateCheckoutTime(){
        this.checkoutTime = (this.item * this.scanTimePerItem) + this.payTime;
    }

    public void calculateFinishTime(){
        this.finishTime = this.arrivalTime + this.timeWaitInLine + this.checkoutTime;
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "items=" + item +
                ", arrivalTime=" + arrivalTime +
                ", waitTime=" + timeWaitInLine +
                ", checkoutTime=" + checkoutTime +
                ", finishTime=" + finishTime +
                ", scanTimePerItem=" + scanTimePerItem +
                ", payTime=" + payTime +
                '}';
    }
}
