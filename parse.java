// Developed by Malay Biswal on 09-02-2016 to parse jmeter result file (.jtl) and insert rows into cassandra table
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/*import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;*/
import java.sql.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
//import java.sql.Date;
import java.text.SimpleDateFormat;

public class parse {
	//static Cluster cluster = null;
	//static Session session = null;
	static PreparedStatement stmt =null;
	static java.sql.Connection conn = null;
	static Socket socket = null;
        static  BufferedReader bufferedReader = null;
        static FileReader fileReader = null;
        static PrintWriter out = null;
        static BufferedWriter bw = null;
	static FileWriter fw = null;
	static int count = 0,verifycount;
	public static void main(String[] args) {
		int line=0;String fname="cec";
		String str="",label="",responseMessage="",threadName="",dataType="",success="",failureMessage="",responseCode="";
        long timestamp=0; int elapsed=0,bytes=0,grpThreads=0,allThreads=0,Latency=0,IdleTime=0,ec=0;
	long ts = 0;
	File file = new File("/root/malay/jmeter/result/res.txt");
	Charset charset = Charset.forName("US-ASCII");
	//PreparedStatement stmt =null;
//	ResultSet rs =  null;
		try{
			if (!file.exists()) {
                                file.createNewFile();
                        }
			fw = new FileWriter(file.getAbsoluteFile());
                        bw = new BufferedWriter(fw);
			
			socket = new Socket ("a-uslvm2.ord1.rackspace.com", 2003);
			out = new PrintWriter (socket.getOutputStream(), true);

			Date myDate = new Date();
			System.out.println(myDate);
			String dt = new SimpleDateFormat("MMddyy").format(myDate);
			System.out.println(dt);
	          String myDirectoryPath = "/root/malay/jmeter/result/";
		        Class.forName("com.mysql.jdbc.Driver");
                        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/synthetics","synthetics","synthetics123");
			String sql = "INSERT INTO transactions "+
			"(id,application,created_t,timestamp,elapsed,label,responsecode,responsemessage,threadName,datatype,success,failuremessage,bytes,grpthreads,allthreads,latency,idletime) " +
			"VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);"	;
			String verifysql = "SELECT count(*) from down where appname=? and issue=?";
			/*Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/synthetics","synthetics","synthetics123"); 
			stmt = conn.prepareStatement(sql);*/
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
				ts = getTime(conn,filename1);
			    	System.out.println("BEFORE IF LOOP got timestamp for application "+filename1+" is "+ts);
			        
			    	if(filename.indexOf(dt) > -1){
			    		System.out.println("INSIDE IF LOOP");
			    		FileInputStream fstream = new FileInputStream(child);
				    	//FileInputStream fstream = new FileInputStream("/malay/jmeter/scripts/synthetics/x.jtl");
						DataInputStream in = new DataInputStream(fstream);
				          BufferedReader br = new BufferedReader(new InputStreamReader(in));
				          Timestamp currTime = getTimeStamp();

				          while((str = br.readLine()) != null){
				        	  line++;
				        	  //System.out.println(line);
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
						   //ec = Integer.parseInt(responseCode);
				        	   //session.execute(query);
				        	   long ts1 = timestamp-610000;// THis is 11 minutes old timestamp, assuming each script executing at 10 min frequency.
				        	   java.sql.Timestamp sqlTime = new java.sql.Timestamp(timestamp);
						   //System.out.println("####"+responseCode+"####");
						   responseCode.replaceAll("\\s+","");
						   //System.out.println("####"+responseCode+"####");
						   /*java.util.Date date = (java.util.Date) timestamp;
						   long epoch = (date.getTime()/1000);
						   String e = " "+epoch;*/
                        			   //count++;
						   //stmt = conn.prepareStatement(sql);
						   java.sql.Timestamp sqlTime1 = new java.sql.Timestamp(ts1);
						   if(timestamp>(ts+10000)){// Check for only new records in jtl file, timestamp newer than what's available in transactions table
							verifycount = verify(conn,verifysql,filename1,sqlTime1);
							if(responseCode.equals("200") ){//Check if HTTP code is other than 200
								System.out.println("INSIDE IF LOOP VERIFYCOUNT:"+verifycount);
								if(verifycount>0){
                                                                        updatedown(conn,filename1,sqlTime,responseCode);
									SendEmailClose s = new SendEmailClose();
                                                                        s.email(filename1,responseCode);
                                                                }
							}
							else if(responseCode.equals("401")){
								System.out.println("INSIDE IF LOOP VERIFYCOUNT:"+verifycount);
								if(verifycount>0){
                                                                        updatedown(conn,filename1,sqlTime,responseCode);
									SendEmailClose s = new SendEmailClose();
                                                                        s.email(filename1,responseCode);
                                                                }
							}
							else if(responseCode.equals("403")){
								System.out.println("INSIDE IF LOOP VERIFYCOUNT:"+verifycount);
								if(verifycount>0){
                                                                        updatedown(conn,filename1,sqlTime,responseCode);
									SendEmailClose s = new SendEmailClose();
									s.email(filename1,responseCode);
                                                                }
							}
							else{
								System.out.println("INSIDE ELSE LOOP VERIFYCOUNT:"+verifycount);
								if(verifycount==0){
                                                                        //Send EMail Logic Goes HERE.
                                                                        //ec = Integer.parseInt(responseCode);
                                                                        insertdown(conn,filename1,sqlTime,responseCode);
									SendEmail s = new SendEmail();
                        						s.email(filename1,responseCode);
								}
							}
							System.out.println("VERIFYCOUNT:"+verifycount);
							stmt = conn.prepareStatement(sql);
							count++;
						   	java.util.Date date = new java.util.Date (timestamp);
                                                   	long epoch = (date.getTime()/1000);
                                                   	String e = " "+epoch;
						   	String app = filename1+".resp-time ";
						   	out.println (app+elapsed+e);	
						   	bw.write(app+elapsed+e);
						   	bw.newLine();
						   	stmt.setInt(1,line);
						   	stmt.setString(2,filename1);
						   	stmt.setTimestamp(3,currTime);
						   	stmt.setTimestamp(4,sqlTime);
						   	stmt.setInt(5,elapsed);	
						   	stmt.setString(6,label);
                                                   	stmt.setString(7,responseCode);	
						   	stmt.setString(8,responseMessage);
						   	stmt.setString(9,threadName);
						   	stmt.setString(10,dataType);
						   	stmt.setString(11,success);
						   	stmt.setString(12,failureMessage);
						   	stmt.setInt(13,bytes);	
						   	stmt.setInt(14,grpThreads);
						   	stmt.setInt(15,allThreads);
						   	stmt.setInt(16,Latency);
						   	stmt.setInt(17,IdleTime);	
						   	stmt.execute();	   	        	   
							System.out.println(timestamp+"-> "+elapsed+"-> "+label+"-> "+responseCode+"-> "+responseMessage+"-> "+threadName+"-> "+dataType+"-> "+success+"-> "+failureMessage+"-> "+bytes+"-> "+grpThreads+"-> "+allThreads+"-> "+Latency+"-> "+IdleTime); 
			    	}
				else{//System.out.println("NO ACTION");
				}
			    		//System.out.println("#####################COUNT:"+count);
			        //	   System.out.println(timestamp+"-> "+elapsed+"-> "+label+"-> "+responseCode+"-> "+responseMessage+"-> "+threadName+"-> "+dataType+"-> "+success+"-> "+failureMessage+"-> "+bytes+"-> "+grpThreads+"-> "+allThreads+"-> "+Latency+"-> "+IdleTime);
			          }
			          
			    }
			}
			  } else {
			    System.out.println("No FILE IN "+myDirectoryPath+" Directory");
			  }
			
			
          
          stmt.close();
      	//conn.close();
	out.close ();
	bw.close();
		} /*catch(Exception e){
			e.printStackTrace();
			stmt.close();
			conn.close();
			bw.close();
			out.close();
		}*/
		catch(SQLException e1){
		e1.printStackTrace();
			try{
                        stmt.close();
                       // conn.close();
                        bw.close();
                        out.close();} catch(Exception e){e.printStackTrace();}
		}
		catch(IOException e2){
                e2.printStackTrace();
			try{
                        stmt.close();
                 //       conn.close();
                        bw.close();
                        out.close();} catch(Exception e){e.printStackTrace();}
                }
		catch(Exception e){
                        e.printStackTrace();
			try{
                        stmt.close();
                 //       conn.close();
                        bw.close();
                        out.close();} catch(Exception e3){e3.printStackTrace();}
                }

		finally {
			try{
			stmt.close();
                 //       conn.close();
			out.close();
			bw.close();} catch(Exception e){e.printStackTrace();}
		}
	}
	public static Timestamp getTimeStamp(){
		java.util.Date date= new java.util.Date();
		 //System.out.println(new Timestamp(date.getTime()));
		 Timestamp currTime = new Timestamp(date.getTime());
		 return currTime;
	}
	
	public static long getTime(Connection conn,String app) throws ClassNotFoundException,SQLException{
        String sql = "Select max(timestamp) from transactions where application=?";
	//Class.forName("com.mysql.jdbc.Driver");
        //Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/synthetics","synthetics","synthetics123");
//	PreparedStatement stmt =null;
//	ResultSet rs =  null; 
	long ts=0;
	try{

//		Class.forName("com.mysql.jdbc.Driver");
//		conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/synthetics","synthetics","synthetics123");
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setString(1,app);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		ts = rs.getTimestamp(1).getTime();
		rs.close();
		stmt.close();
		//conn.close();
	//	return ts;
	} /*catch(Exception e){
		e.printStackTrace();
		rs.close();
		stmt.close();
		conn.close();
	}*/
	catch(SQLException e){
                e.printStackTrace();
		try{
                //rs.close();
                stmt.close();
                //conn.close();
                } catch(Exception e2){e2.printStackTrace();}
        }
	catch(Exception e){
                e.printStackTrace();
		try{
         //       rs.close();
                stmt.close();
            //    conn.close();
                } catch(Exception e1){e1.printStackTrace();}
        }
	finally {
		try{
	//	rs.close();
		stmt.close();
		
		//conn.close();
		} catch(Exception e){e.printStackTrace();}
		}

	return ts;
	}
//SELECT count(*) from down where appname=? and timestamp>=? and issue=?
	public static int verify(Connection c,String sql, String app, java.sql.Timestamp timestamp) throws SQLException{
		System.out.println("MALAY:"+timestamp+"*******");
		int count=0;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try{
			stmt = c.prepareStatement(sql);
			stmt.setString(1,app);
			//stmt.setTimestamp(2,timestamp);	
			stmt.setString(2,"YES");
			rs = stmt.executeQuery();
			rs.next();
			count = rs.getInt(1);
			rs.close();
			stmt.close();
		}catch(Exception e){
			e.printStackTrace();			
			rs.close();
			stmt.close();
			
		}
		finally{
			rs.close();
			stmt.close();
		}
		
		return count;
	}
//Insert into DOWN table. DOWN table has information if there is any ongoing issue
	public static void insertdown(Connection c1,String app, java.sql.Timestamp timestamp, String ec) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String insertsql = "INSERT INTO down "+"(timestamp,appname,errorcode,issue) values(?,?,?,?);";
		try{
			stmt = c1.prepareStatement(insertsql);
			stmt.setTimestamp(1,timestamp);
			stmt.setString(2,app);
			stmt.setString(3,ec);
			stmt.setString(4,"YES");	
			stmt.execute();
		}catch(Exception e){
			e.printStackTrace();
			stmt.close();
		}
		finally{
			stmt.close();
		}
		
	}

	public static void updatedown(Connection c1,String app, java.sql.Timestamp timestamp, String ec) throws SQLException{
		PreparedStatement stmt = null;
		ResultSet rs = null; String no="NO";
		PreparedStatement stmt1 = null;
		ResultSet rs1 = null;Timestamp ts1=null;Timestamp ts2 = getTimeStamp();
		String findsql = "select max(timestamp) from down where appname=?";
		try{
			stmt1 = c1.prepareStatement(findsql);
			stmt1.setString(1,app);
			rs1 = stmt1.executeQuery();
                        rs1.next();
                        ts1 = rs1.getTimestamp(1);
                        rs1.close();
                        stmt1.close();
		}catch(Exception e1){
                        e1.printStackTrace();
                        stmt1.close();
			rs1.close();
                }
                finally{
                        stmt1.close();
			rs1.close();
                }
		java.util.Date date = new java.util.Date();
		java.sql.Timestamp tsmp = new Timestamp(date.getTime());
		String updatesql = "update down set issue=?,mod_t=? where appname=? and timestamp=? ";
		System.out.println("issue:"+no+" mod_t:"+tsmp+" appname:"+app+" errorcode:"+ec+" Timestamp:"+ts1);
		try{
			stmt = c1.prepareStatement(updatesql);
			stmt.setString(1,no);
			stmt.setTimestamp(2,tsmp);
			stmt.setString(3,app);
			stmt.setTimestamp(4,ts1);
			//stmt.setTimestamp(4,timestamp);
			stmt.executeUpdate();
		}catch(Exception e){
			e.printStackTrace();
			stmt.close();
		}
		finally{
			stmt.close();
		}
	}
}
