package mm1withMeanBatchMethod;

import java.lang.*;

public class Validation {
	//parameters
	static double λ;
	static double µ = 2;

	static double ρ;
	static double K = 10;
	
	
	static double ECI = 10; // Energy consumption during an idle state
	static double ECb = 500; // Energy consumption during a busy state
	static double ECTx = 5; // Energy consumption for holding each packet in the buffer
	static double ECs = 300; // Energy wasted as a sensor node transitions between states

	//metrics
	static double mean_response_time ;
	static double mean_waiting_time ;
	static double throughput ;
	static double prob_system_is_idle;
	static double Mean_no_customer_in_buffer_Lq;
	static double Mean_no_customer_in_buffer_K;
	static double Mean_no_customer_in_system;
	static double Mean_no_customer_in_system_K;
	static double blocking_prob;
	static double loss_rate ;
	static double EC ;
	static double Nc ;
	static double Waiting_in_the_buffer ;
	static double Waiting_in_the_System ;
	
	
	public static void main(String[] args) {
    	for(λ=0.1; λ<2;λ+= 0.1) {
    		
    		ρ = (λ/µ);

    		mean_response_time = 1/(µ-λ) + 1/µ;
    		
    		mean_waiting_time =  ρ/(µ*(1-ρ));
    		

    		
    		prob_system_is_idle = (1-ρ) / (1- Math.pow(ρ, K)) ;
    		
    		Mean_no_customer_in_system = ρ / ( 1-ρ);
    		Mean_no_customer_in_buffer_Lq = Math.pow(ρ, 2) / ( 1-ρ);
    		
    		Nc=1 ;
    		
	        EC = (prob_system_is_idle * ECI) + ((1 - prob_system_is_idle) * ECb) + (ECs*Nc) + (Mean_no_customer_in_buffer_Lq * ECTx);

    		//for mm1 with buffer K
    		throughput = λ * ((1- Math.pow(ρ, K)) /(1- Math.pow(ρ, K+1)) );
    		
    		Mean_no_customer_in_system_K = ( ρ / ( 1-ρ) ) - ( (K +1 )* Math.pow(ρ, K+1) / (1- Math.pow(ρ, K+1)));
    		
    		Mean_no_customer_in_buffer_K = ( ρ / ( 1-ρ) ) - (ρ * (1 + K* Math.pow(ρ, K)) / (1- Math.pow(ρ, K+1)));
    		
    		Waiting_in_the_buffer = Mean_no_customer_in_buffer_K /λ;
    		
    		Waiting_in_the_System = Waiting_in_the_buffer + 1/µ ;
    		
    		blocking_prob =( 1-ρ)*Math.pow(ρ, K)/ (1- Math.pow(ρ, K+1)); 
    		loss_rate  =  λ * blocking_prob  ;
    		
    		
    		System.out.printf("%f\n", loss_rate);
    		
    	}

	}

}
