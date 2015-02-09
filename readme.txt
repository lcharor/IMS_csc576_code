In this folder please find 3 Java file:-
1.	Customer.java
2.	Event.java
3.	IMS_Simulator.java
Also please find one file to give input parameters:-
1.	Input.txt
This file will contain input parameters in following order:-
<Lambda>  <mean_service_time_µP>  <mean_service_time_µS>  <mean_service_time_µAS> <total number of customers> <total_no_of_batches>

Each parameter is separated by space from other.
For e.g:-

1 0.1 0.2 0.5 30100 30                         For lambda equal=1
0.5 0.1 0.2 0.5 30100 30                       for lambda equal=0.5

If you want to make changes to input please open the input.txt and change the input.

Initially Input.txt is set with “1 0.1 0.2 0.5 30100 30” as input.
Steps to run:-
1.Type “make” ( this will create all the class and object files)
2.Type “java  IMS_Simulator”
3.Output will be redirected to output.txt as well as on the console. 

If you want to change input parameters, please open the input.txt file and change the parameters.

Please also find "results.txt" which contains results of simulation on my local machine:-
1)Lambda=0.5, Total Number of departures= 30,100
2)Lambda=1, Total Number of departures= 30,100
3)Lambda=0.5, Total Number of departures= 9,100
4)Lambda=1, Total Number of departures= 9,100













