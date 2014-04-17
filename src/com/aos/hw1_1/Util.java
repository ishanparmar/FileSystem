package com.aos.hw1_1;


public class Util {
	
	
	/*static String[] allServers = {"net20.utdallas.edu",
		"net21.utdallas.edu",
		"net22.utdallas.edu",
		"net23.utdallas.edu",
		"net24.utdallas.edu",
		"net25.utdallas.edu",
		"net26.utdallas.edu",
		"net27.utdallas.edu",
		"net28.utdallas.edu",
		"net29.utdallas.edu"};	*/
	//public static int[] serverPorts = {7274,7274,7274,7274,7274,7274,7274,7274,7274,7274};
	static String[] allServers = {"net03.utdallas.edu","net02.utdallas.edu","net01.utdallas.edu","net04.utdallas.edu","net05.utdallas.edu","net06.utdallas.edu","net07.utdallas.edu"};
	
	public static int[] serverPorts = {52390,52391,52392,52393,52394,52394,52395};
	
	static int CRITICAL_SECTION_EXECUTION_TIME = 20;
	static int CONTROLLING_NODE_ID = 0;
	
	// Control Messages between nodes
	public static String I_AM_UP = "I_AM_UP";
	public static String I_AM_DOWN = "I_AM_DOWN";
	public static String START_EXECUTION = "START_EXECUTION";
	public static String STOP_EXECUTION = "STOP_EXECUTION";	
	public static String REQUEST = "REQUEST";
	public static String REPLY = "REPLY";
	
	public static String BLANK = " ";
	public static String ENTERING = "ENTERING";
	public static String LEAVING = "LEAVING";
	public static String LOGFILE = "log.txt";
	public static final String REPORTLOG = "reportLog.txt";
	
}
