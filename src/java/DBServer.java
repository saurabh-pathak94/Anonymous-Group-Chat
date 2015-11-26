/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Acer
 */
import java.io.*;
import java.net.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
public class DBServer extends Thread{
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/ajp_lab"; 
    static final String USER = "root";  
    static final String PASS = ""; 
    static Connection conn = null;
    static Statement stmt = null;
    int port;
    //Thread t;
    DBServer(int p){
        //t=new Thread(this,"Server Thread");
        super("server");
        port = p;
        start();
    }
    public void run(){
        server();
    }
    public static void main(String[] args){
        //server();
    }  
    public void server(){    
        try{
        ServerSocket ss = new ServerSocket(port);
        Socket s = ss.accept();
        DataInputStream dis = new DataInputStream(s.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        String str="",str2="";
         Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
       
        str = (String)dis.readUTF();
        System.out.println("Client : " + str);
        String[] cred = str.split(":"); //name:token pair
        //Access old chats for this token
       
        stmt = conn.createStatement();
        String sql = "SELECT created_at,updated_at,active from users where token='"+cred[1]+"'";
        ResultSet rs = stmt.executeQuery(sql);
        if(!rs.isBeforeFirst()){
            //no such token ----> indicates database inconstistency!
        }
        else{
            String created="",updated=""; int active;
            while(rs.next()){
                 created = rs.getString("created_at");
                 updated = rs.getString("updated_at");
                 active = rs.getInt("active");
                 if(active==1){
                     updated = getCurrentTimeStamp()+"";
                 }
            }
           sql = "SELECT message,sent_at FROM message where sent_at BETWEEN '"+created+"' AND '"+updated+"'";
           ResultSet rs2 = stmt.executeQuery(sql);
           String result="MESSAGES";
           while(rs2.next()){
               java.sql.Timestamp time = rs2.getTimestamp("sent_at");
               Date date = new Date(time.getTime());
DateFormat formatter = new SimpleDateFormat("mm/dd/yyyy hh-mm-ss");
String t = formatter.format(date);
               result+=":"+rs2.getString("message")+"&nbsp&nbsp&nbsp (<i>sent at "+t+"</i>)";
           } 
            System.out.println(result);
             dos.writeUTF(result);
             dos.flush();
             
        
       
        //str2=br.readLine();
        //dos.writeUTF(str2);
        //dos.flush();
        }
        dis.close();
        dos.close();
        ss.close();
        }catch(Exception e){e.printStackTrace();}
    }
    private static java.sql.Timestamp getCurrentTimeStamp() {

	java.util.Date today = new java.util.Date();
	return new java.sql.Timestamp(today.getTime());

  }
}
