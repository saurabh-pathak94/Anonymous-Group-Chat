/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author student
 */
import java.sql.*;
public class Validate {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/ajp_lab"; 
    static final String USER = "root";  
    static final String PASS = ""; 
    static Connection conn = null;
    static Statement stmt = null;
     public static boolean checkUser(String name,String token) 
     {
      boolean st =false;
      try{

	 //loading driver 
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(DB_URL,USER,PASS);
        stmt = conn.createStatement();
 	 //creating connection with the database 
         PreparedStatement ps =conn.prepareStatement("select * from users where name=? and token=?");
ps.setString(1, name);
         ps.setString(2, token);
         ResultSet rs =ps.executeQuery();
         st = rs.next();
        
      }catch(Exception e)
      {
          e.printStackTrace();
      }
         return st;     }
}
