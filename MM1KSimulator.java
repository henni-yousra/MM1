package mm1withMeanBatchMethod;

 
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;


import mm1withMeanBatchMethod.Client;
import mm1withMeanBatchMethod.Client.Client_Class;
import mm1withMeanBatchMethod.Event.Event_type;

import mm1withMeanBatchMethod.StatCalculations.*;
 

class MM1KSimulator {
	static int number_of_cycles=0;
	
	static double totalIdleDuration = 0; // Total duration of idle periods
	/*
	static int totalIdlePeriods = 0; // Total number of idle periods
	static double totalBusyDuration = 0; // Total duration of busy periods
	static int totalBusyPeriods = 0; // Total number of busy periods

	static double busy_start ;
	*/
	static double Nc;
	
	static double ECI = 10; // Energy consumption during an idle state
	static double ECb = 500; // Energy consumption during a busy state
	static double ECTx = 5; // Energy consumption for holding each packet in the buffer
	static double ECs = 300; // Energy wasted as a sensor node transitions between states
	
	static int K = 10;//max queue size

	static double Q;//longueur moyenne de la file d'attente Q

	static int totalEvents;
	static double totalEnergyConsumption;
	
	static double idle_start;
	static double idle_prob ;
	

	static double  start_of_saturation_time=0;
	static double saturation_time = 0;
	static float  nb_rejected_client;

	static double EC;

	static int queue_size;
	enum Server_state {busy, idle} ;
	
	static Queue<Client> clients_queue;
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
	static double[] batchMeans_of_blockage = new double[1000];
	static double[] batchMeans_of_rate_loss  = new double[1000];
	static double[] batchMeans_of_vacation_prob  = new double[1000];
	static double[] batchMeans_of_Q  = new double[1000];
	static double[] batchMeans_of_number_of_cycles = new double[1000];


	static double overall_clients_served = 0;



	static double exponential_distribution(double parameter) {
    	Random rand = new Random(); 
    	double R = rand.nextDouble();
    	
        return  Math.log(1-R)/(-parameter);
    }

    static void init() {
    	Tnow = 0;
    	EC = 0;

    	start_of_saturation_time=0;
    	saturation_time = 0;

        nb_rejected_client =0;
    	
        server = Server_state.idle;
        idle_start = Tnow;

        clients_queue = new LinkedList<Client>() {
            @Override
            public boolean add(Client client) {
                if (size() <= K) {
                    return super.add(client);
                } else {
                    // Handle queue full situation, e.g., reject the client
                    //System.out.println("Queue is full. Rejecting client: " + client.getId());
                    return false;
                }
            }
        };
      
        events_queue= new PriorityQueue<Event>(100, new EventComparator() );

        service_time =   exponential_distribution(mu); 
        arrival_time = exponential_distribution(λ);

        mean_response_time = 0;
        mean_waiting_time = 0;
        clients_served_in_each_batch = 0;


        Create_Event( Event_type.arrival, Tnow);
    }

    public static void main( String[] args){

    	//variation of λ
    	//for( N=2 ; N < K ; N++) {
        for(λ=0.1; λ<2;λ+= 0.1) {
        	overall_clients_served = 0;
    		//collecting results for each batch
    		for(int i=0; i<number_of_batches; i++) {
    		
    	        init();
	        	Event event;
		        Client client = null;
		        
		        totalEvents=0;
		        Q=0;
		        Nc =0;
		        
		    	idle_prob = 0;
		    	totalIdleDuration= 0;
		    	idle_start =0;

		    	number_of_cycles=0;
		        new_client_id = 0;
		        
		        while (Tnow <= 10000 && !events_queue.isEmpty()) {

		        	event = events_queue.remove();
		        	//event.print_event();

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
		             }
		            	delete_event(event);
		            	
		            	
		        }//end of one simulation run while Tnow <= 10000 

		        idle_prob = totalIdleDuration/Tnow;

		        Q = Q/totalEvents;//longueur moyenne de la file d'attente Q

		        Nc = number_of_cycles/Tnow;
		        //Nc = averageIdleLength + meanBusyDuration ;
		        //System.out.printf("vacation_prob= %f\n", vacation_prob);
		        //System.out.printf("Q= %f\n", Q);
		        //System.out.printf("number_of_cycles par unité de temps: %f\n", Nc);

		        //the analitic way
		        EC = (idle_prob * ECI) + ((1 - idle_prob) * ECb) + (ECs*Nc) + (Q * ECTx);
		        
		        //System.out.printf("EC= %f\n", -EC);
		        
			    //print_statistics();



		        //System.out.printf("number_of_cycles: %d\n", number_of_cycles);
		        

		        overall_clients_served += clients_served_in_each_batch;
		        
		        batchMeans_of_mean_response_time[i] =  mean_response_time/clients_served_in_each_batch ;
		        batchMeans_of_mean_waiting_time[i] = mean_waiting_time/clients_served_in_each_batch;
		        batchMeans_of_throughput[i] = clients_served_in_each_batch/Tnow ;
		        //fix it in other cases loss rate 
		        batchMeans_of_rate_loss[i] = nb_rejected_client/new_client_id ;
		        batchMeans_of_blockage[i] = saturation_time/Tnow ;
		        

		        batchMeans_of_vacation_prob [i] =idle_prob;
		        
		        batchMeans_of_Q[i] = Q;//longueur moyenne de la file d'attente Q
		        
		        batchMeans_of_number_of_cycles[i] = Nc;
		        
		        batchMeans_of_totalEnergyConsumption[i] =  EC; //
    		}
    		
	       //System.out.printf("%f\n", overall_clients_served);
    		 StatCalculations.display_results(batchMeans_of_rate_loss);
    	}//end boucle of lambda variation
    
    }//end of main


	static void process_arrival() {
        totalEvents ++;
    	Q +=  clients_queue.size(); //taking the number of packets at each event and adding it to Q

		//System.out.printf("new client has arrived \n ");
		new_client_id++;
		
        //System.out.printf("service_time:%f  \t", service);
        Client client = new Client( new_client_id, Client_Class.C1, Tnow, 0,exponential_distribution(mu));
        
        boolean added = clients_queue.add(client);

        if (!added) {
            // The client was rejected, so skip creating the arrival event
        	
        	nb_rejected_client++;
            return;
        }
        if (clients_queue.size() == K) {
            if (start_of_saturation_time == 0) {
                start_of_saturation_time = Tnow;
            }
            saturation_time = Tnow - start_of_saturation_time;
        }

		if(server == Server_state.idle) {     
			Create_Event( Event_type.start_service, Tnow);
		
		}

            Create_Event(Event_type.arrival, Tnow + exponential_distribution(λ));
        
        
    }
    
	static Client process_start_service() {
		
			if(server == Server_state.idle) {     
		        totalIdleDuration += Tnow - idle_start;// Add the duration to the total
			}

	        server = Server_state.busy;

	        Client client = clients_queue.remove();
	        
	        client.t_start_service = Tnow;
	        //client.print_client();

	        Create_Event( Event_type.departure, Tnow+ client.service_time);
	        return client;
	        

    }
    
	public static void process_departure(Client client ) {

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
               
        if (clients_queue.size() < K  && start_of_saturation_time !=0) {
            saturation_time += Tnow - start_of_saturation_time;
            start_of_saturation_time = 0;
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
        //System.out.printf("number of clients served in this batch: %d\n", clients_served_in_each_batch);
        //System.out.printf("mean response time: %f\n", mean_response_time/clients_served_in_each_batch);
        //System.out.printf("mean waiting time: %f\n", mean_waiting_time/clients_served_in_each_batch);
        //System.out.printf("throughput: %f\n", (float)clients_served_in_each_batch/Tnow);
        //System.out.printf("Total energy consumption: %f\n", totalEnergyConsumption);
        //System.out.printf("-------------------------------------\n");
        //System.out.printf("rate of loss: %f\n",  nb_rejected_client/clients_served_in_each_batch );
        //System.out.printf("blockage: %f\n", saturation_time/Tnow);

        //System.out.printf("vacation_prob: %f\n", total_vacation/Tnow);
	}




}
