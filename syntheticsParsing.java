// Developed by Malay Biswal on 05-19-2016 to parse jmeter result file (.jtl) and insert rows into cassandra table
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

public class syntheticsParsing {
	static Cluster cluster = null;
	static Session session = null;
	public static void main(String[] args) {
		int line=0;String fname="cec";
		String str="",label="",responseMessage="",threadName="",dataType="",success="",failureMessage="",responseCode="";
        long timestamp=0; int elapsed=0,bytes=0,grpThreads=0,allThreads=0,Latency=0,IdleTime=0;
		try{
			Date myDate = new Date();
			System.out.println(myDate);
			String dt = new SimpleDateFormat("MMddyy").format(myDate);
			System.out.println(dt);
			cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
	          session = cluster.connect("dev");
	          String myDirectoryPath = "/root/malay/jmeter/result/";
			
			PreparedStatement statement = session.prepare("INSERT INTO transactions "+
			"(id,application,created_t,timestamp,elapsed,label,responsecode,responsemessage,threadName,datatype,success,failuremessage,bytes,grpthreads,allthreads,latency,idletime) " +
			"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);")	;
			
			
			File dir = new File(myDirectoryPath);
			  File[] directoryListing = dir.listFiles();
			  if (directoryListing != null) {
			    for (File child : directoryListing) {
			    	System.out.println("child:"+child);
			    	String fnms = child.getName();
			    	System.out.println(fnms+" "+fnms.length());
			    	
			    	boolean retval = fnms.contains(".");
			    	   System.out.println("Method returns : " + retval);
			    	
			    	String[] filenames = fnms.split(Pattern.quote("."));
			    	System.out.println(filenames[0]+" "+filenames[1]);
			    	String filename = filenames[0];
			    	String[] fnm = filenames[0].split("_");
			    	String filename1 = fnm[0];
			    	System.out.println("BEFORE IF LOOP");
			    
			    	if(filename.indexOf(dt) > -1){
			    		System.out.println("INSIDE IF LOOP");
			    		FileInputStream fstream = new FileInputStream(child);
				    	//FileInputStream fstream = new FileInputStream("/malay/jmeter/scripts/synthetics/x.jtl");
						DataInputStream in = new DataInputStream(fstream);
				          BufferedReader br = new BufferedReader(new InputStreamReader(in));
				          Timestamp currTime = getTimeStamp();
				          while((str = br.readLine()) != null){
				        	  line++;
				        	  System.out.println(line);
				        	  String[] tokens = str.split(",");
				        	  if(tokens[0].equals("timeStamp"))
				        		  continue;
				        	  //System.out.println(tokens[1]);
				        	   timestamp = Long.parseLong(tokens[0]);
				        	   elapsed = Integer.parseInt(tokens[1]);
				        	   label = tokens[2];
				        	   responseCode = tokens[3];
				        	   responseMessage = tokens[4];
				        	   threadName = tokens[5];
				        	   dataType = tokens[6];
				        	   success = tokens[7];
				        	   failureMessage = tokens[8];
				        	   bytes = Integer.parseInt(tokens[9]);
				        	   grpThreads = Integer.parseInt(tokens[10]);
				        	   allThreads = Integer.parseInt(tokens[11]);
				        	   Latency = Integer.parseInt(tokens[12]);
				        	   IdleTime = Integer.parseInt(tokens[13]);
				        	   //session.execute(query);
				        	   java.sql.Time sqlTime = new java.sql.Time(timestamp);
				        	   
				        	   BoundStatement bound = statement.bind(line,filename1,currTime,sqlTime,elapsed,label,responseCode,responseMessage,threadName,dataType,success,failureMessage,bytes,grpThreads,allThreads,Latency,IdleTime);
				        	   session.execute(bound);
			    	}
			    	
			        	   System.out.println(timestamp+"-> "+elapsed+"-> "+label+"-> "+responseCode+"-> "+responseMessage+"-> "+threadName+"-> "+dataType+"-> "+success+"-> "+failureMessage+"-> "+bytes+"-> "+grpThreads+"-> "+allThreads+"-> "+Latency+"-> "+IdleTime);
			          }
			          
			    }
			  } else {
			    System.out.println("No FILE IN "+myDirectoryPath+" Directory");
			  }
			
			
          
          session.close();
      	cluster.close();
		}catch(Exception e){
			e.printStackTrace();
			session.close();
			cluster.close();
		}
	}
	public static Timestamp getTimeStamp(){
		java.util.Date date= new java.util.Date();
		 //System.out.println(new Timestamp(date.getTime()));
		 Timestamp currTime = new Timestamp(date.getTime());
		 return currTime;
	}
}

