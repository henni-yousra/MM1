package mm1withMeanBatchMethod;
 
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;


import mm1withMeanBatchMethod.Client.Client_Class;
import mm1withMeanBatchMethod.Event.Event_type;
import mm1withMeanBatchMethod.StatCalculations.*;
 

class MM1 {
	static int number_of_cycles=0;
	
	
	
	static double Nc;//number of cycles 
	
	static double ECI = 10; // Energy consumption during an idle state
	static double ECb = 500; // Energy consumption during a busy state
	static double ECTx = 5; // Energy consumption for holding each packet in the buffer
	static double ECs = 300; // Energy wasted as a sensor node transitions between states
	
	static double Mean_no_customer_in_buffer_Lq;//longueur moyenne de la file d'attente Q

	static int totalEvents;
	static double totalEnergyConsumption;
	
	static double idle_start;
	static double totalIdleDuration = 0; // Total duration of idle period
	static double idle_prob ;

	static double EC;

	enum Server_state {busy, idle} ;
	
	static LinkedList<Client> clients_queue;
	static PriorityQueue<Event> events_queue ;
	
	static double service_time;
	static double  arrival_time;
	static double Tnow;
	static float mean_waiting_time,mean_response_time ;
	
	static long clients_served_in_each_batch;
	static int new_client_id;
	
	static Server_state server;
	
	static double λ = 0.25;
	static double mu= 2 ;

	static int number_of_batches = 1000;
	
	static double[] batchMeans_of_mean_response_time = new double[1000];
	static double[] batchMeans_of_mean_waiting_time = new double[1000];
	static double[] batchMeans_of_throughput = new double[1000];
	static double[] batchMeans_of_totalEnergyConsumption  = new double[1000];
	static double[] batchMeans_of_idle_prob  = new double[1000];
	static double[] batchMeans_of_Q  = new double[1000];
	static double[] batchMeans_of_number_of_cycles = new double[1000];


	static double overall_clients_served = 0;



	private static int number_of_events;



	static double exponential_distribution(double parameter) {
    	Random rand = new Random(); 
    	double R = rand.nextDouble();
    	
        return  Math.log(1-R)/(-parameter);
    }

    static void init() {

        Tnow = 0;
        EC = 0;
        
        server = Server_state.idle;
    	idle_start =0;
        
        clients_queue = new LinkedList<Client>();
        
        events_queue= new PriorityQueue<Event>(100, new EventComparator() );

        service_time =  exponential_distribution(mu); 
        arrival_time = exponential_distribution(λ);

        mean_response_time = 0;
        mean_waiting_time = 0;

        new_client_id = 0;
        /* Create the first primary event: Arrival */
        Create_Event( Event_type.arrival, Tnow);
    }

    public static void main( String[] args){

    	//variation of λ
    	for(λ=0.1; λ<2;λ+= 0.1) {
        	overall_clients_served = 0;
    		//collecting results for each batch
    		for(int i=0; i<number_of_batches; i++) {
    		
    	        init();
    	        
    	        clients_served_in_each_batch = 0;
		        
	        	Event event;
		        Client client = null;
		        totalEvents=0;
		        Mean_no_customer_in_buffer_Lq=0;
		        Nc =0;
		        
		    	idle_prob = 0;
		    	totalIdleDuration= 0;
		    	idle_start =0;

		    	number_of_cycles=0;
		    	
		    	number_of_events=0;
		        while (Tnow <= 10000) {
		        	event = events_queue.remove();

		             //taking the number of packets at each event and adding it to Q
		        	
		            Tnow = event.time;
		            //System.out.printf("Systeme clock :%f \n \n", Tnow);
		
		            switch (event.type) {
		                case arrival:
		                    process_arrival(); 
		                break;
		                case start_service:  
		                     client = process_start_service(); 
		               	 
		                break;
		                case departure: 
		                    process_departure(client); 
		                break;

		             }//end of switch case

		            delete_event(event);

		        }//end of while loop
	            
			    //print_statistics();

		        idle_prob = totalIdleDuration/Tnow;

		        Mean_no_customer_in_buffer_Lq = Mean_no_customer_in_buffer_Lq/number_of_events; //longueur moyenne de la file d'attente Q

		        Nc = number_of_cycles/Tnow;

		        //Energy Formula
		        EC = (idle_prob * ECI) + ((1 - idle_prob) * ECb) + (ECs*Nc) + (Mean_no_customer_in_buffer_Lq * ECTx);

		        overall_clients_served += clients_served_in_each_batch;
		        
		        batchMeans_of_mean_response_time[i] =  mean_response_time/clients_served_in_each_batch ;
		        batchMeans_of_mean_waiting_time[i] = mean_waiting_time/clients_served_in_each_batch;
		        batchMeans_of_throughput[i] = clients_served_in_each_batch/Tnow ;
		        //fix it in other cases loss rate 
		        

		        batchMeans_of_idle_prob [i] =idle_prob;
		        
		        batchMeans_of_Q[i] = Mean_no_customer_in_buffer_Lq;//longueur moyenne de la file d'attente Q
		        
		        batchMeans_of_number_of_cycles[i] = Nc;
		        
		        batchMeans_of_totalEnergyConsumption[i] =  EC; //
    		}//end of batch loop
    		
	        System.out.printf("%f\n", overall_clients_served);
    		//StatCalculations.display_results(batchMeans_of_totalEnergyConsumption);
    	}//end of lambda variation loop
    
    }

    static void process_endVacancy(){
    	
    }
	static void process_arrival() {
    	Mean_no_customer_in_buffer_Lq +=  clients_queue.size();
    	number_of_events++;
		//System.out.printf("new client has arrived \n ");
		new_client_id++;
		
        //System.out.printf("service_time:%f  \t", service);
        Client client = new Client( new_client_id, Client_Class.C1, Tnow, 0,exponential_distribution(mu));
        
        clients_queue.addLast(client);	

        //System.out.printf("elements of clients queue: \n ");
        //System.out.println(clients_queue);
    
        if (server == Server_state.idle)
            Create_Event( Event_type.start_service, Tnow);

        
        //System.out.printf("arrival_time:%f  \n", arrival);
        Create_Event( Event_type.arrival, Tnow + exponential_distribution(λ));
        
        
    }
    
	static Client process_start_service() {
		
		if(server == Server_state.idle) {     
	        totalIdleDuration += Tnow - idle_start;// Add the duration to the total

		}

	        server = Server_state.busy;


	        Client client = clients_queue.removeFirst();
	        
	        client.t_start_service = Tnow;
	        //client.print_client();
	        
	        Create_Event( Event_type.departure, Tnow+ client.service_time);
	        return client;

	    
    }
    
	public static void process_departure(Client client ) {
		//server = Server_state.idle;
     
		//Collect Statistics

    	
        mean_response_time += Tnow - client.t_arrival;
        mean_waiting_time += client.t_start_service - client.t_arrival;
        clients_served_in_each_batch++;
        
        delete_client(client);
        
        //System.out.printf("client departure \n ");
        if(!(clients_queue.isEmpty())) {
        	
        	Create_Event(Event_type.start_service, Tnow);

        }else {
        	//System.out.printf("the queue became empty \n");
        	number_of_cycles++;
            idle_start = Tnow;
            server = Server_state.idle;
        }
        
    }

	public static void Create_Event(Event_type type, double time){
        Event event = new Event(  type,  time);
        events_queue.add(event);
    }
    
	static void delete_event(Event event ){
        event = null ;

    }
	static void delete_client(Client client){
        client = null;

    }
    
	static void print_statistics() {
        System.out.printf("number of clients served: %d\n", clients_served_in_each_batch);
        System.out.printf("mean response time: %f\n", mean_response_time/clients_served_in_each_batch);
        System.out.printf("mean waiting time: %f\n", mean_waiting_time/clients_served_in_each_batch);
        System.out.printf("throughput: %f\n", (float)clients_served_in_each_batch/Tnow);
        System.out.printf("Total energy consumption: %f\n", totalEnergyConsumption);
        System.out.printf("-------------------------------------\n");
 
	}




}
