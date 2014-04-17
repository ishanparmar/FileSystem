package com.aos.hw1_1;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;



public class ControllerThread implements Runnable {

	Thread t;
	boolean allNodesExecuting = false;
	

	@Override
	public void run() {
		
		Node.nodeStatus[0]=1; // controller is up
		while(true){
			//System.out.println(Arrays.toString(Node.nodeStatus));
			if(areAllNodesUp() && !allNodesExecuting){
				for(int m=1;m<10;m++){
					if(Node.nodeId!=m){
					//	System.out.println("sending msg to "+ m);
						ClientThread n;
						try {
							n = new ClientThread(InetAddress.getByName(Util.allServers[m]),Util.serverPorts[m],Util.START_EXECUTION,Node.nodeId, new Date().getTime());
							Node.messageSentCounter++;
							Thread t = new Thread(n);
							t.start();
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}catch (Exception e){
							e.printStackTrace();
						}
					}					
				}
				Node.executionAllowed = true;
				allNodesExecuting = true;
				break;
			}
		}		
		
	while(true){
		int ctr =0;
		for(boolean b : Node.isNodeAlive){
			if(!b){
				ctr++;
			}
		}	
		if(ctr==7 && !Node.amIUp){
			for(int m=1;m<10;m++){
				if(Node.nodeId!=m){
				//	System.out.println("sending msg to "+ m);
					ClientThread n;
					try {
						n = new ClientThread(InetAddress.getByName(Util.allServers[m]),Util.serverPorts[m],Util.STOP_EXECUTION,Node.nodeId, new Date().getTime());
						Node.messageSentCounter++;
						Thread t = new Thread(n);
						t.start();
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}catch (Exception e){
						e.printStackTrace();
					}
				}					
			}
			break;
		}
	}
	try {
		Thread.sleep(200);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	System.exit(0);
}

	public boolean areAllNodesUp(){
		for(int i : Node.nodeStatus){
			if(i==0)
				return false;
		}
		return true;
	}
	
	
	
}
