package com.aos.hw1_1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;



public class Node {
	static int receivedRepliesCount = 0;
	static int nodeId;
	static int criticalSectionCounter = 0;
	static int messageSentCounter = 0;
	static int messagesReceivedCounter = 0;
	static int msgSentPerCS = 0;
	
	
	
	static boolean isController = false;
	static boolean inCriticalSection = false;
	static boolean isRequestRaised = false;
	static boolean executionAllowed = false;
//	static String inMessage;
	static int[] nodeStatus = new int[10];
	static boolean[] permissionRequired = {true,true,true,true,true,true,true,true,true,true};
	static boolean[] isNodeAlive = {true,true,true,true,true,true,true,true,true,true};
	static long lastRequestRaisedAt = Long.MAX_VALUE;
	static long lastEnterecCriticalSectionAt = Long.MAX_VALUE;
	static long maxtime = Long.MAX_VALUE;
	static long mintime = Long.MIN_VALUE;
	static boolean amIUp = false;

		
	static List<Integer> deferredReplies = new ArrayList<Integer>();


	public static void main(String[] args) throws IOException {
		String node = " ";		
		amIUp = true;
		// initializing nodeId
		try {
			node = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {			
			e.printStackTrace();
		}
		
		// Code to read from config file
		
		/*File fin = new File("config.txt");
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(fin);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		//Construct BufferedReader from InputStreamReader
		BufferedReader br = new BufferedReader(new FileReader(fin));
		 
		String line = null;
		
		// get number of nodes
		line=br.readLine();
		line = line.substring(0,line.indexOf('#')).trim();
		System.out.println(line);
		
		while ((line = br.readLine()) != null) {
			if(line.charAt(0)=='#')
				continue;
			else{
				String[] arr = line.split(" ");
				int n = Integer.parseInt(arr[0]);
				Util.allServers[n] = arr[1].trim();
				Util.serverPorts[n] = Integer.parseInt(arr[2].trim());
			}
		}
	 
		br.close();*/

		
		nodeId = Integer.parseInt(node.substring(4,5));
		//nodeId=0;
		if(nodeId==3)
			nodeId=0;
		if(nodeId==2)
			nodeId=1;
		if(nodeId==1)
			nodeId=2;
		if(nodeId==4)
			nodeId=3;
		if(nodeId==5)
			nodeId=4;
		if(nodeId==6)
			nodeId=5;
		if(nodeId==7)
			nodeId=6;
		if(nodeId == 0)
			isController = true;
		
		ExecThread exec = new ExecThread();
		ServerThread server = new ServerThread();
		ControllerThread controller = new ControllerThread();
		
		Thread serverThread = new Thread(server);
		Thread execThread = new Thread(exec);		
		Thread controllerThread = new Thread(controller);
		
		serverThread.start();
		if(Node.isController)
			controllerThread.start();
		execThread.start();		
		
	}
}
