package com.aos.hw1_1;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ClientThread implements Runnable {

	Thread t;
	InetAddress serverAddress;
	int port;
	String outMessage;
	int from;
	Socket clientSocket;
	long time;

	ClientThread(InetAddress serverAddress, int port, String outMessage, int from, long time){
		this.serverAddress =  serverAddress;
		this.port = port;
		this.outMessage = outMessage;
		this.from = from;
		this.time = time;

		t = new Thread(this);
		//t.start();		
	}

	@Override
	public void run() {
		String message;
		if(outMessage!=null){
			try
			{
				clientSocket = new Socket(serverAddress,port);
				DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());	
				System.out.println("sending msg:"+outMessage+" from "+Node.nodeId+" to "+serverAddress);
				message = outMessage + Util.BLANK + String.valueOf(from) + Util.BLANK + String.valueOf(time);
				dOut.writeByte(1);			
				dOut.writeUTF(message);
				dOut.flush();
			}catch(IOException ex){
				System.out.println("could not send "+outMessage+" to node "+String.valueOf(serverAddress));
				ex.printStackTrace();
			}finally{
				try {
					clientSocket.close();
				} catch (IOException e) {			
					e.printStackTrace();
				}
			}
		}else{
			System.out.println("sending null to "+String.valueOf(serverAddress));
		}
	}
}
