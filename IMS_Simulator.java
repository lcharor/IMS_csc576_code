import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class IMS_Simulator {
//define 3 stages of simulation;
	public static ArrayList<Customer> P_CSCF=new ArrayList<Customer>();
	public static ArrayList<Customer> S_CSCF=new ArrayList<Customer>();
	public static ArrayList<Customer> AS=new ArrayList<Customer>();
	
	public static String inputfilename="input.txt";
//Event List	
	public static ArrayList<Event> eventList=new ArrayList<Event>();
//OutCustomer List
	public static ArrayList<Customer> outCustomerList=new ArrayList<Customer>();
	
//define required variable	
	public static int current_departure=0;
	public static double lambda;
	public static double p_cscf_meantime;
	public static double s_cscf_meantime;
	public static double as_meantime;
	public static int total_simulated_departures,total_no_of_batches,n;
	public static double MC=0.05;
	
	public static void main(String[] simulatorInitializer) throws IOException{
	    
		BufferedReader br=new BufferedReader(new FileReader(inputfilename));
		String curline=br.readLine();
		
		while(curline!=null){
			String[] inputLine=curline.split(" ");
			lambda=Double.parseDouble(inputLine[0]);
			p_cscf_meantime=Double.parseDouble(inputLine[1]);
			s_cscf_meantime=Double.parseDouble(inputLine[2]);
			as_meantime=Double.parseDouble(inputLine[3]);
			total_simulated_departures=Integer.parseInt(inputLine[4]);
			total_no_of_batches=Integer.parseInt(inputLine[5]);
			curline=br.readLine();
		}
		//Input required variables;	
		
		br.close();
		
		n=((total_simulated_departures-100)/total_no_of_batches);
		
	   //initialize the event queue
	    double t=MC-(1/lambda*Math.log(randomGenerator()));
		Event event=new Event(t, "AP");
		eventList.add(event);
	    
		 t=MC-(p_cscf_meantime*Math.log(randomGenerator()));
		 event=new Event(t, "CP");
		 eventList.add(event);
		 
		t=MC-(s_cscf_meantime*Math.log(randomGenerator()));
		event=new Event(t, "CS");
		eventList.add(event);
				
		t=MC-(as_meantime*Math.log(randomGenerator()));
		event=new Event(t, "CA");
		eventList.add(event);
	
			
		while(current_departure<total_simulated_departures){
			
			serve_Event();
		}	
		
		Simulate();
		
	}		
		
static //pseudo random number generator	
double randomGenerator() {
    Random generator = new Random();
    double num = generator.nextDouble() * (0.97);
    return num;
}
static void Arrival_Pcscf(){
	//Find Next Arrival Time;
	double t=MC-(1/lambda*Math.log(randomGenerator()));
	Event event=new Event(t, "AP");
	eventList.add(event);
	
	//Current Arrival
	Customer customer=new Customer();
	customer.State="NEW";
	customer.time_in=MC;
	P_CSCF.add(customer);
}
static void Completion_Pcscf()
{
	//Find Next Completion Time
	double t=MC-(p_cscf_meantime*Math.log(randomGenerator()));
	Event event=new Event(t, "CP");
	eventList.add(event);
	
	Customer customer=new Customer();
	if(P_CSCF.size()!=0){
		customer=P_CSCF.remove(0);
		if(customer.State.equalsIgnoreCase("new"))
			S_CSCF.add(customer);
		else{
			customer.time_out=MC;
			customer.time_served=customer.time_out-customer.time_in;
			//System.out.print("Time In:"+customer.time_in+"-->");
			//System.out.print("Time out:"+customer.time_out+"-->");
			//System.out.println("Mean delay:"+customer.time_served);
			outCustomerList.add(customer);
			current_departure++;
			}
	}
}

static void Completion_Scscf()
{
	//find next completion time 
	double t=MC-(s_cscf_meantime*Math.log(randomGenerator()));
	Event event=new Event(t, "CS");
	eventList.add(event);
	
	Customer customer=new Customer();
	if(S_CSCF.size()!=0)
	{	
	    customer=S_CSCF.remove(0);
	    if(customer.State.equalsIgnoreCase("new"))
	    	AS.add(customer);
	    else{
	    	P_CSCF.add(customer);
	    }
	}
}

static void Completion_AS(){
	//find next completion time
	double t=MC-(as_meantime*Math.log(randomGenerator()));
	Event event=new Event(t, "CA");
	eventList.add(event);
	
	Customer customer=new Customer();
	if(AS.size()!=0){
		customer=AS.remove(0);
		customer.State="old";
		S_CSCF.add(customer);
	}
}

static void serve_Event(){
	int first_event=0;
	double time_t=eventList.get(0).time;
	
	for(int i=0;i<eventList.size();i++)
	{
		if(eventList.get(i).time < time_t)
		{
			first_event=i;
			time_t=eventList.get(first_event).time;
		}
	}

	for(int i=0;i<eventList.size();i++)
	{
		if(eventList.get(first_event).time==eventList.get(i).time)
		{
			if(eventList.get(first_event).typevalue<eventList.get(i).typevalue)
				first_event=i;
		}
	}


	
  Event event=new Event();
  event=eventList.remove(first_event);
  
  MC=event.time;
  
  if(event.type.equalsIgnoreCase("AP")){
	  Arrival_Pcscf();
	 // System.out.println("Switching to Arival:"+MC);
  }
  if(event.type.equalsIgnoreCase("CP")){
	  Completion_Pcscf();
	//  System.out.println("Switching to Completion in Arival:"+MC);
  }
  if(event.type.equalsIgnoreCase("CS")){
	  Completion_Scscf();
	//  System.out.println("Switching to Completion in SCSCF:"+MC);
  }
  if(event.type.equalsIgnoreCase("CA")){
	  Completion_AS();
	// System.out.println("Switching to completion in AS:"+MC);
  }
}

static void Simulate() throws FileNotFoundException, UnsupportedEncodingException{
	PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
	
	int k;
	ArrayList<Double> Percentile_Elements=new ArrayList<Double>();
	k=(int)Math.ceil(0.95*n);

	ArrayList<Double> SERVEDTIME_LIST=new ArrayList<Double>();
//removing first 100 customers;	
	for(int i=0;i<100;i++)
	{
		outCustomerList.remove(0);
	}
	//System.out.println("Out customer List Size:"+outCustomerList.size());
	
	double mean_delay=0;
//simple mean end-to-end delay calculations;	
	for (int i=0;i<outCustomerList.size();i++)
	{
		mean_delay=mean_delay+outCustomerList.get(i).time_served;
	}
	writer.println("Mean of end-to-end Delay (without using batch means):"+String.format("%1.2f", (mean_delay/(total_simulated_departures-100))));
	System.out.println("Mean of end-to-end Delay (without using batch means):"+String.format("%1.2f", (mean_delay/(total_simulated_departures-100))));
//95th percentile calculations(without using batch means);	
	ArrayList<Double> temp_out_list=new ArrayList<Double>();
	for(int i=0;i<outCustomerList.size();i++)
		temp_out_list.add(outCustomerList.get(i).time_served);
	Collections.sort(temp_out_list);
	
	int index=(int) ((0.95)*(total_simulated_departures-100));
	writer.println("95th Percentile(without using batch means):"+String.format("%1.3f", temp_out_list.get(index)));
	System.out.println("95th Percentile(without using batch means):"+String.format("%1.3f", temp_out_list.get(index)));
	
//Mean end-to-end delay using batch means and confidence interval	
	temp_out_list.clear();
	for(int i=0;i<outCustomerList.size();i++)
		temp_out_list.add(outCustomerList.get(i).time_served);
	
	
	for(int batch=1;batch<=total_no_of_batches;batch++)
	{
		for(int i=0;i<n;i++)
		{
			SERVEDTIME_LIST.add(temp_out_list.remove(0));
		}
		double addition=0;
		for(int j=0;j<SERVEDTIME_LIST.size();j++)
		{
			addition=addition+SERVEDTIME_LIST.get(j);
		}
		Percentile_Elements.add(addition/n);
		SERVEDTIME_LIST.clear();
	}
	double mean_delay_with_batch=0;
	for(int j=0;j<Percentile_Elements.size();j++)
	{
		mean_delay_with_batch=mean_delay_with_batch+Percentile_Elements.get(j);
	}
	mean_delay_with_batch=mean_delay_with_batch/Percentile_Elements.size();
	writer.println("Mean end-to-end delay(using batch means):"+String.format("%1.3f",mean_delay_with_batch));
	System.out.println("Mean end-to-end delay(using batch means):"+String.format("%1.3f",mean_delay_with_batch));
	
	double square_add=0;
	for(int i=0;i<Percentile_Elements.size();i++)
	{
		square_add=square_add+(mean_delay_with_batch-Percentile_Elements.get(i))*(mean_delay_with_batch-Percentile_Elements.get(i));
	}
	
	double s_dup=Math.sqrt(square_add/(total_no_of_batches-1));
	writer.println("Confidence Interval(Mean end-to-end delay using Batch means):["+String.format("%1.5f",(mean_delay_with_batch-1.96*(s_dup/Math.sqrt(total_no_of_batches))))+","+String.format("%1.5f",(mean_delay_with_batch+1.96*(s_dup/Math.sqrt(total_no_of_batches))))+"]");
    System.out.println("Confidence Interval(Mean end-to-end delay using Batch means):["+String.format("%1.5f",(mean_delay_with_batch-1.96*(s_dup/Math.sqrt(total_no_of_batches))))+","+String.format("%1.5f",(mean_delay_with_batch+1.96*(s_dup/Math.sqrt(total_no_of_batches))))+"]");

	
//95th Percentile Using Batch means and confidence interval calculations
	SERVEDTIME_LIST.clear();
	Percentile_Elements.clear();
	for (int batch=1;batch<=total_no_of_batches;batch++)
	{
		for(int i=0;i<n;i++)
		{
			SERVEDTIME_LIST.add(outCustomerList.remove(0).time_served);	
		}
		Collections.sort(SERVEDTIME_LIST);
		Percentile_Elements.add(SERVEDTIME_LIST.get(k-1));
		SERVEDTIME_LIST.clear();
	}
	
	double sum=0;
	
	for(int i=0;i<Percentile_Elements.size();i++)
	{
		sum=sum+Percentile_Elements.get(i);
	}
	
	double Tmean=sum/total_no_of_batches;
	writer.println("95th Percentile(with using batch means):"+String.format("%1.3f", Tmean));
	System.out.println("95th Percentile(with using batch means):"+String.format("%1.3f", Tmean));
	double square_sum=0;
	
	for(int i=0;i<Percentile_Elements.size();i++)
	{
		square_sum=square_sum+(Tmean-Percentile_Elements.get(i))*(Tmean-Percentile_Elements.get(i));
	}
	
	double s=Math.sqrt(square_sum/(total_no_of_batches-1));
	//System.out.println("Standard deviation:"+String.format("%1.6f", s));
	System.out.println("Confidence Interval(95th Pecentile using Batch means):["+String.format("%1.5f",(Tmean-1.96*(s/Math.sqrt(total_no_of_batches))))+","+String.format("%1.5f",(Tmean+1.96*(s/Math.sqrt(total_no_of_batches))))+"]");
	writer.println("Confidence Interval(95th Pecentile using Batch means):["+String.format("%1.5f",(Tmean-1.96*(s/Math.sqrt(total_no_of_batches))))+","+String.format("%1.5f",(Tmean+1.96*(s/Math.sqrt(total_no_of_batches))))+"]");
	writer.close();
	}
}
