package mm1withMeanBatchMethod;

public class Client {

	    public enum Client_Class {C1, C2, C3, C4} ;

	    public int id; 
	    public Client_Class class_type ;
	    public double t_arrival; 
	    public double t_start_service; 
	    public double service_time;

	    
	    public Client(int id, Client_Class c, double t_arrival, float t_start_service, double service_time) {
			this.id = id;
			this.class_type = c;
			this.t_arrival = t_arrival;
			this.t_start_service = t_start_service;
			this.service_time = service_time;
		}

		public void print_client() {
			System.out.printf(" client info : \t" );
	        System.out.printf("id: %d\t",  id);
	        System.out.printf("class type: %s\t",  class_type.toString());
	        System.out.printf("arrival time : %f\t",t_arrival);
	        System.out.printf("time of starting service: %f\t",t_start_service);
	        System.out.printf("service time: %f\n", service_time);
	      
	    }


}

