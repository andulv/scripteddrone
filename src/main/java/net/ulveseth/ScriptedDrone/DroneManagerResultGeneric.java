package net.ulveseth.ScriptedDrone;



//TODO: Gjøre om til custom exceptions?... 	
public class DroneManagerResultGeneric<T> extends DroneManagerResult {
	final T value;
	
	public DroneManagerResultGeneric(boolean success, String resultText, T value) {
		 super(success,resultText);
		 this.value=value;
	 }		
}
