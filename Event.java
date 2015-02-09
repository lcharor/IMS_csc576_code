public class Event {
double time;
String type;//AP,CP,CS,CA
int typevalue;
public Event(double time, String type) {
	super();
	this.time = time;
	this.type = type;
	if(type.equalsIgnoreCase("AP"))
		this.typevalue=1;
	if(type.equalsIgnoreCase("CP"))
		this.typevalue=2;
	if(type.equalsIgnoreCase("CS"))
		this.typevalue=3;
	if(type.equalsIgnoreCase("CA"))
		this.typevalue=4;
}
public Event() {
	// TODO Auto-generated constructor stub
}

}
