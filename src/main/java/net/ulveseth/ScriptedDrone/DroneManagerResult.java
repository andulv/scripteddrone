package net.ulveseth.ScriptedDrone;

public class DroneManagerResult {
	
	final String resultText;
	final boolean success;
	
	
	public DroneManagerResult(boolean success, String resultText){
		this.resultText=resultText;
		this.success=success;
	}
	
	public boolean getSuccess() {
		return success;
	}
	
	public String getResultText(){
		return resultText;
	}
}
