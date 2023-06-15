package mm1withMeanBatchMethod;

public class Event {
    enum Event_type {arrival, start_service, departure, acknowledgement} ;

    public  Event_type type; 
    public  double time;

    public Event(Event_type type, double time){  
        this.type= type; 
        this.time = time;
    }
    
    public void print_event() {
        System.out.printf("Event type: %s \t \t", type);
        System.out.printf("Event time: %f \n",time);

      
    }


}
