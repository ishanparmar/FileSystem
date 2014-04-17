package com.aos.hw1_1;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

public class ServerThread implements Runnable {
	Thread t;
	InetAddress serverAddress;
	int port;
	String inMessage;
	int from;
	ServerSocket serverSock;
	Date date = new Date();

	@Override
	public void run() {
		
		try
		{
			//Create a server socket 
			serverSock = new ServerSocket(Util.serverPorts[Node.nodeId],10);
			
			//Server goes into a permanent loop accepting connections from clients			
			while(true){
				//Listens for a connection to be made to this socket and accepts it
				//The method blocks until a connection is made
				System.out.println("listening");
				System.out.println(Node.executionAllowed);
				Socket sock = serverSock.accept();				
					
			    BufferedReader d = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			    
				inMessage = String.valueOf(d.readLine());
				//inMessage = dIn.readUTF();		
				System.out.println("msg rcvd: "+inMessage);
				if(inMessage==null){
					System.out.println("getting null");
					continue;
					
				}
					
				String[] arr = inMessage.split(Util.BLANK);
				//System.out.println(arr[0]);
				arr[0]=arr[0].trim();
				arr[1]=arr[1].trim();
				arr[2]=arr[2].trim();
				
				if(arr[0].trim().equalsIgnoreCase(Util.REQUEST)){
					System.out.println(arr[0]+" from "+arr[1]);
					++Node.messagesReceivedCounter;
					if(!Node.isRequestRaised || Node.lastRequestRaisedAt > Long.parseLong(arr[2]) || ( Node.lastRequestRaisedAt == Long.parseLong(arr[2]) && Node.nodeId > Integer.parseInt(arr[1]))){
						// send REPLY if currently not requesting or in critical section
						//System.out.println("sending reply to "+arr[1]);
						int from = Integer.parseInt(arr[1]);						
						Node.permissionRequired[from]=true;						
						ClientThread n;
						try {
							n = new ClientThread(InetAddress.getByName(Util.allServers[from]),Util.serverPorts[from],Util.REPLY,Node.nodeId, new Date().getTime());
							Node.messageSentCounter++;
							Thread t = new Thread(n);
							t.start();
						} catch (UnknownHostException e) {
							e.printStackTrace();
						}catch (Exception e){
							e.printStackTrace();
						}
					}else{
						// if not sending REPLY, add it to deferred replies
						System.out.println("request raised status: "+Node.isRequestRaised);
						System.out.println("request raised at "+Node.lastRequestRaisedAt);
						System.out.println("request from "+arr[1]+" at "+arr[2]);
						System.out.println("deferring reply to "+arr[1]);
						Node.deferredReplies.add(Integer.parseInt(arr[1]));
						System.out.println("deferred reply list "+Node.deferredReplies);
					}					
				}else
					
				if(arr[0].trim().equalsIgnoreCase(Util.START_EXECUTION)){
					++Node.messagesReceivedCounter;
					Node.executionAllowed = true;
					System.out.println(arr[0]+" from "+arr[1]);
				}else
				
				if(arr[0].trim().equalsIgnoreCase(Util.STOP_EXECUTION)){
					++Node.messagesReceivedCounter;
					Node.executionAllowed = false;
					System.out.println(arr[0]+" from "+arr[1]);
					System.exit(0);
				}else
				
				if(arr[0].trim().equalsIgnoreCase(Util.I_AM_DOWN)){
					++Node.messagesReceivedCounter;
					Node.nodeStatus[Integer.parseInt(arr[1])] = 0;
					System.out.println(arr[0]+" from "+arr[1]);
					Node.isNodeAlive[Integer.parseInt(arr[1])] = false;
				} else
				
				if(arr[0].trim().equalsIgnoreCase(Util.I_AM_UP)){
					++Node.messagesReceivedCounter;
					System.out.println(arr[0]+" from "+arr[1]);
					Node.nodeStatus[Integer.parseInt(arr[1])] = 1;					
				}else
				
				if(arr[0].trim().equalsIgnoreCase(Util.REPLY)){
					System.out.println(arr[0]+" from "+arr[1]);
					++Node.messagesReceivedCounter;					
					int from = Integer.parseInt(arr[1]);
					Node.permissionRequired[from] = false;
					++Node.receivedRepliesCount;	
					
					System.out.println("replies rcvd"+Node.receivedRepliesCount);
				}
			}

		}catch(IOException ex){
			ex.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				serverSock.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
