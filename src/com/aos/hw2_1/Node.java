package com.aos.hw2_1;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;





public class Node {	

	static int nodeId;										// nodeId of the server/client
	static char nodeType;									// takes value s-server  c-client   n-neutral
	static boolean[] isConnectionUp = new boolean[12];		// indicates if connection to server/client is up or not
	// First 5 for servers 0 thru 4 and rest for clients 0 thru 6
	static String objectName = " ";							// name of the object to be written on servers
	static String value;									// value of the object to be written on servers
	static int primaryServer;								// number of the primary server to be calculated from object name. takes value 0 thru 6

	static String[] servers = new String[5];				// hostname of servers 0 thru 4
	static String[] clients = new String[7];				// hostname of clients 0 thru 6
	static int port = 7654;

	public static void main(String[] args){

		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		System.out.println(" Enter the object name: ");	// reading object name from console
		try {
			objectName = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		System.out.println(" Enter value: ");			// reading object value from console
		try {
			value = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}

		primaryServer = getHash(objectName);			// getting primary server for the object

		if(canWrite(primaryServer)){
			
			// send object to primaryServer, if allowed
			if(isConnectionUp[primaryServer]){
				sendMessage(primaryServer, objectName, value, true);
			}
			if(isConnectionUp[(primaryServer+1)%5]){
				sendMessage((primaryServer+1)%5, objectName, value, false);
			}
			if(isConnectionUp[(primaryServer+2)%5]){
				sendMessage((primaryServer+2)%5, objectName, value, false);
			}

		}else{
			System.out.println("Two or more servers are inaccessible. Aborting write.");
		}

	}


	private static void sendMessage(int serverId, String objectName, String value, boolean isPrimary) {
		Socket s1 = null;

		try{
			s1 = new Socket(servers[serverId],port);
			DataOutputStream dOut = new DataOutputStream(s1.getOutputStream());
			dOut.writeByte(1);
			String sendObject =  objectName+" "+value+" "+Boolean.toString(isPrimary);
			dOut.writeUTF(sendObject);
			dOut.flush(); 									// Send off the data				
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				s1.close();
			} catch (IOException e) {					
				e.printStackTrace();					
			}
		}
	}
	


	/**
	 * takes object name as a string and returns an integer value between 0 to 6
	 *  
	 * @param objName
	 * @return
	 */
	public static int getHash(String objName){
		int hash;
		hash = Integer.parseInt(objName) % 7;
		return hash;
	}

	/**
	 * takes integer value of server number and returns true if more than two servers are accessible.
	 * @param s1
	 * @return
	 */
	public static boolean canWrite(int s1){
		int ctr = 0;
		if(isConnectionUp[primaryServer])
			ctr++;
		if(isConnectionUp[(primaryServer+1)%5])
			ctr++;
		if(isConnectionUp[(primaryServer+2)%5])
			ctr++;

		if(ctr>=2)
			return true;
		else
			return false;
	}
}
