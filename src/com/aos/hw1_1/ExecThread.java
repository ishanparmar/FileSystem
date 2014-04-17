package com.aos.hw1_1;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Date;

public class ExecThread implements Runnable {

	Socket s1 = null;
	ClientThread ct ;
	Thread t;
	String content;
	

	@Override
	public void run() {
		//System.out.println("exec thread running");
		int wait;
		Date date = new Date();

		// send I_AM_UP message to controller BEGIN
		if(!Node.isController){
			try{
				s1 = new Socket(Util.allServers[0],Util.serverPorts[0]);
				DataOutputStream dOut = new DataOutputStream(s1.getOutputStream());
				dOut.writeByte(1);
				String iAmUp =  Util.I_AM_UP + Util.BLANK + Node.nodeId + Util.BLANK + String.valueOf(date.getTime());;
				dOut.writeUTF(iAmUp);
				dOut.flush(); // Send off the data				
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


		while(true){
			if(Node.amIUp){
			try{
			String data = String.valueOf(Node.executionAllowed);				
			File file = new File("temp.txt");			
			//if file doesnt exists, then create it
    		if(!file.exists()){
    			file.createNewFile();
    		}
 
    		//true = append file
    		FileWriter fileWritter = new FileWriter(file.getName(),true);
    	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
    	        bufferWritter.write(data);
    	        bufferWritter.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		}

			if(Node.executionAllowed){
			//	System.out.println("exec allowed");
				if(Node.criticalSectionCounter<20){
					wait = getRandom(10, 100);
					try{
						Thread.sleep(wait);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}					
				}else if(Node.criticalSectionCounter<40){
					if(Node.nodeId%2==0){
						wait = getRandom(200,500);
					}else{
						wait = getRandom(10,100);
					}try {
						Thread.sleep(wait);
					}catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				System.out.println(Node.receivedRepliesCount);
				// send REQUEST message to all nodes by spawning 9 threads
				System.out.println("raising request");
				Node.msgSentPerCS=0;
				Node.lastRequestRaisedAt = new Date().getTime();
				Node.isRequestRaised = true;
				System.out.println(Arrays.toString(Node.permissionRequired));
				for(int m=0;m<10;m++){
					if(Node.nodeId!=m && Node.permissionRequired[m] && Node.isNodeAlive[m]){
						++Node.messageSentCounter;
						++Node.msgSentPerCS;						
						try{
							//System.out.println("sending Request to "+m);
							ct = new ClientThread(InetAddress.getByName(Util.allServers[m]),Util.serverPorts[m],Util.REQUEST,Node.nodeId, Node.lastRequestRaisedAt);
						}catch(Exception e){
							e.printStackTrace();
						}
						t = new Thread(ct);
						t.start();
					}else{
						++Node.receivedRepliesCount;
					}
				}

				// enter critical section if all REPLIES received
				while(true){
					if(Node.receivedRepliesCount==10){
						
						try{
							++Node.criticalSectionCounter;
							Node.inCriticalSection = true;
							System.out.println("entering CS:"+Node.criticalSectionCounter+" at time "+new Date().getTime());
							// write to log file
							content = "\n"+String.valueOf(Node.nodeId)+ Util.BLANK + Util.ENTERING+Util.BLANK+new Date().getTime()+"\n";					 
							writeToLog(content, Util.LOGFILE);
							Node.lastEnterecCriticalSectionAt = new Date().getTime();
							Thread.sleep(Util.CRITICAL_SECTION_EXECUTION_TIME);
							
							//writing report stats to file
							Long timeTakenToEnterCS = Node.lastEnterecCriticalSectionAt- Node.lastRequestRaisedAt;
							String report = "\nTime taken by node "+Node.nodeId+" to enter critical section #"+Node.criticalSectionCounter+": "+timeTakenToEnterCS;
							writeToLog(report,Util.REPORTLOG);
							report = "\nNumber of messages sent/received by node "+Node.nodeId+"to enter critical section #"+Node.criticalSectionCounter+": "+Node.msgSentPerCS;
							writeToLog(report,Util.REPORTLOG);
							
							Node.maxtime= (Node.maxtime<timeTakenToEnterCS) ? timeTakenToEnterCS : Node.maxtime;
							Node.mintime= (Node.mintime>timeTakenToEnterCS) ? timeTakenToEnterCS : Node.mintime;
							
							Node.receivedRepliesCount=0;
							refresh();
							Node.inCriticalSection = false;
							Node.isRequestRaised = false;
							Node.lastRequestRaisedAt = Long.MAX_VALUE;
							
							
							System.out.println("exiting CS:"+Node.criticalSectionCounter+" at time "+new Date().getTime());
							//write to log file
							content = "\n"+String.valueOf(Node.nodeId)+ Util.BLANK + Util.LEAVING+Util.BLANK+new Date().getTime()+"\n";								
							writeToLog(content, Util.LOGFILE);
							
							// send any deferred replies
							System.out.println("deferred replies to be sent "+Node.deferredReplies);
							if(!Node.deferredReplies.isEmpty()){
								for(int node : Node.deferredReplies){																				
									
									System.out.println("sending deferred reply to :\n"+ node);
									++Node.messageSentCounter;
									Node.permissionRequired[node]=true;
									try{										
										ct = new ClientThread(InetAddress.getByName(Util.allServers[node]),Util.serverPorts[node],Util.REPLY,Node.nodeId, Node.lastRequestRaisedAt);
									}catch(Exception e){
										e.printStackTrace();
									}
									t = new Thread(ct);
									t.start();
									
								}
							}
							
							Node.deferredReplies.clear();
						} catch (InterruptedException  e) {
							e.printStackTrace();
						}
						break;						
					}else{
						try{
						File file = new File("temp.txt");			
						//if file doesnt exists, then create it
			    		if(!file.exists()){
			    			file.createNewFile();
			    		}
			 
			    		//true = append file
			    		FileWriter fileWritter = new FileWriter(file.getName(),true);
			    	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			    	        bufferWritter.write(Node.receivedRepliesCount);
			    	        bufferWritter.close(); 
					} catch (IOException e) {
						e.printStackTrace();
					}
					}
				}

				// if executed critical section 40 times, send I AM DOWN signal and exit
				if(Node.criticalSectionCounter==40){
					Node.amIUp = false;
					if(Node.isController){
						Node.nodeStatus[0]=0;
						Node.executionAllowed=false;
					}else{
						try{
							/*s1 = new Socket(Util.allServers[0], Util.serverPorts[0]);
							DataOutputStream dOut = new DataOutputStream(s1.getOutputStream());
							dOut.writeByte(1);
							String iAmDown =  Util.I_AM_DOWN + Util.BLANK + Node.nodeId + Util.BLANK + String.valueOf(date.getTime());*/
							for(int m=0;m<10;m++){
								if(Node.nodeId!=m && Node.isNodeAlive[m]){
									++Node.messageSentCounter;
									try{
										ct = new ClientThread(InetAddress.getByName(Util.allServers[m]),Util.serverPorts[m],Util.I_AM_DOWN,Node.nodeId, new Date().getTime());
									}catch(Exception e){
										e.printStackTrace();
									}
									t = new Thread(ct);
									t.start();
								}else{
									
								}
							}
							System.out.println("Node "+ Node.nodeId);
							System.out.println("Messages sent: " + Node.messageSentCounter);
							System.out.println("Message Received: "+ Node.messagesReceivedCounter);
							/*dOut.writeUTF(iAmDown);
							dOut.flush(); */	
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
					
					int totalmsg = Node.messageSentCounter+Node.messagesReceivedCounter;
					String report = "\nTotal number of messages for Node:"+Node.nodeId+": "+totalmsg;
					writeToLog(report, Util.REPORTLOG);
					report = "\nMinimum time taken by Node:"+Node.nodeId+"to enter critical section: "+Node.mintime;
					writeToLog(report, Util.REPORTLOG);
					report = "\nMaximum time taken by Node:"+Node.nodeId+"to enter critical section: "+Node.maxtime;
					writeToLog(report, Util.REPORTLOG);
				}
			}else{Node.executionAllowed = Node.executionAllowed;}
		}else{
			refresh();
		}
		}
	}


	public static int getRandom(int min, int max){
		int range = max-min+1;
		int i = (int)(Math.random()*range)+min;
		return i;
	}

	/*public static void logCriticalSectionExecution(boolean status){
		try {
			String content;
			if(status){
				content = String.valueOf(Node.nodeId) + "\t" + "Entering";
			}else{
				content = String.valueOf(Node.nodeId) + "\t" + "Leaving";
			}

			File file = new File("/users/ixp130130/log.txt");

			
			//if file doesnt exists, then create it
    		if(!file.exists()){
    			file.createNewFile();
    		}
 
    		//true = append file
    		FileWriter fileWritter = new FileWriter(file.getName(),true);
    	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
    	        bufferWritter.write(content);
    	        bufferWritter.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
*/
	public void writeToLog(String content, String filename){
		try {
			File file = new File(filename);		 
			// if file doesnt exists, then create it
			//if file doesnt exists, then create it
    		if(!file.exists()){
    			file.createNewFile();
    		}
 
    		//true = append file
    		FileWriter fileWritter = new FileWriter(file.getName(),true);
    	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
    	        bufferWritter.write(content);
    	        bufferWritter.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void refresh(){
		try{
			File file = new File("temp.txt");			
			//if file doesnt exists, then create it
    		if(!file.exists()){
    			file.createNewFile();
    		}
 
    		//true = append file
    		FileWriter fileWritter = new FileWriter(file.getName(),true);
    	        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
    	        bufferWritter.write(Node.criticalSectionCounter);
    	        bufferWritter.write(Node.messageSentCounter);
    	        bufferWritter.write(Node.messagesReceivedCounter);
    	        bufferWritter.write(String.valueOf(Node.isController));
    	        bufferWritter.write(String.valueOf(Node.inCriticalSection));
    	        bufferWritter.write(String.valueOf(Node.isRequestRaised));
    	        bufferWritter.write(String.valueOf(Node.executionAllowed ));
    	        bufferWritter.write(String.valueOf(Node.permissionRequired));
    	        bufferWritter.write(String.valueOf(Node.isNodeAlive));
    	        bufferWritter.write(String.valueOf(Node.amIUp));
    	        bufferWritter.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
