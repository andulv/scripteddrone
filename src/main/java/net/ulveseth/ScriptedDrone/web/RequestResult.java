package net.ulveseth.ScriptedDrone.web;

public class RequestResult {
	public static RequestResult Success(String resultBody) {
		RequestResult result= new RequestResult();
		result.responseCode=200;
		result.responseBody=resultBody;
		return result;
	}
	
	public static RequestResult Error(int responseCode, String errorMessage) {
		RequestResult result= new RequestResult();
		result.responseCode=responseCode;
		result.errorMessage=errorMessage;
		return result;
	}
	
	public int responseCode;
	public String responseBody;
	public String errorMessage;

}
