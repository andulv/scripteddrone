package net.ulveseth.ScriptedDrone;

import java.io.IOException;
import java.util.ArrayList;

public class Utils {
	
	public static String readStreamAsStringAndClose(java.io.InputStream is) throws IOException {
		try {		
		    @SuppressWarnings("resource")
			java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
		    return s.hasNext() ? s.next() : "";
		}
		finally {
			is.close();
		}
	}
		
	public static String[] ExtractRemainingArgs(String[] args, int argsToSkip) {
		String[] remainingArgs=null;
		if(args.length>argsToSkip) {
			remainingArgs=new String[args.length-argsToSkip];
			for(int i=0;i<remainingArgs.length;i++) {
				remainingArgs[i]=args[i+argsToSkip];
			}		
		}
		return remainingArgs;
	}
	
	public static String[] RemoveEmptySegments(String[] sourceArray, String emptyString) {
		ArrayList<String> list = new ArrayList<String>();
	    for(String s : sourceArray) {
	        if(s != null && s.length() > 0 && s!=emptyString) {
	           list.add(s);
	        }
	     }

	     return list.toArray(new String[list.size()]);
	}

	
	public static String GetDebugStringFromArgs(String[] args) {
		String argsInfo="null";
		if(args!=null) {
			argsInfo="length: " + args.length;					
			if(args.length>0) {
				argsInfo+=", [0]" + args[0];
			}
		}
		return argsInfo;
	}

}
