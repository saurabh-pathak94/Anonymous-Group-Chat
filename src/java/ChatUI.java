import javax.swing.*;
import javax.swing.border.*;
 
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.rmi.Naming;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;
 
public class ChatUI{
  private ChatClient client;
  private ChatServerInt server;
  // JDBC driver name and database URL  
        static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
        static final String DB_URL = "jdbc:mysql://localhost/ajp_lab";  
   //  Database credentials   
        static final String USER = "root";  
        static final String PASS = ""; 
        static Connection conn = null;
        static Statement stmt = null;
        int max = 5000, min=1000;
        String token;
        
  public void doConnect(){
	    if (connect.getText().equals("Connect")){
	    	if (name.getText().length()<2){JOptionPane.showMessageDialog(frame, "You need to type a name."); return;}
	    	if (ip.getText().length()<2){JOptionPane.showMessageDialog(frame, "You need to type an IP."); return;}	    	
	    	try{
				client=new ChatClient(name.getText());
	    		client.setGUI(this);
				server=(ChatServerInt)Naming.lookup("rmi://"+ip.getText()+"/myabc");
				server.login(client);
				updateUsers(server.getConnected());
                                
                            Class.forName("com.mysql.jdbc.Driver");
                            conn = DriverManager.getConnection(DB_URL,USER,PASS);
                            stmt = conn.createStatement();
                            Random rand = new Random();
                            int num = rand.nextInt((max-min)+1)+min;
                            String sql = "Insert into users values('"+name.getText()+"','"+name.getText()+num+"','"+getCurrentTimeStamp()+"','"+getCurrentTimeStamp()+"',1)";
                            stmt.executeUpdate(sql);
                            token = name.getText()+num;
                            JOptionPane.showMessageDialog(frame, "Your token number is : "+name.getText()+num);
                            writeMsg("TOKEN NUMBER: "+name.getText()+num);
			    connect.setText("Disconnect");	
                            System.out.println(getCurrentTimeStamp());
	    	}catch(Exception e){e.printStackTrace();JOptionPane.showMessageDialog(frame, "ERROR, we couldn't connect....");}		  
	      }else{  //Disconnect User
	    	  	updateUsers(null);
	    	  	connect.setText("Connect");
                        try{
                        String sql = "UPDATE users SET updated_at='"+getCurrentTimeStamp()+"',active=0 WHERE token='"+token+"'";
                        stmt.executeUpdate(sql);
                        }catch(Exception e){}
	    	  	
		}
	  }  
  private static java.sql.Timestamp getCurrentTimeStamp() {

	java.util.Date today = new java.util.Date();
	return new java.sql.Timestamp(today.getTime());

  }
  public void sendText(){
    if (connect.getText().equals("Connect")){
        System.out.println(getCurrentTimeStamp());
    	JOptionPane.showMessageDialog(frame, "You need to connect first."); return;	
    }
      String st=tf.getText();
      if(st.length()==0){
          JOptionPane.showMessageDialog(frame, "Write something."); return;
      }
      st="["+name.getText()+"] "+st;
      
      tf.setText("");
      //Remove if you are going to implement for remote invocation
      try{
    	  	server.publish(st);
                String sql = "Insert into message values('"+token+"','"+st+"','"+getCurrentTimeStamp()+"')";
                stmt.executeUpdate(sql);
  	  	}catch(Exception e){e.printStackTrace();}
  }
 
  public void writeMsg(String st){  tx.setText(tx.getText()+"\n"+st);  }
 
  public void updateUsers(Vector v){
      DefaultListModel listModel = new DefaultListModel();
      if(v!=null) for (int i=0;i<v.size();i++){
    	  try{  String tmp=((ChatClientInt)v.get(i)).getName();
    	  		listModel.addElement(tmp);
    	  }catch(Exception e){e.printStackTrace();}
      }
      lst.setModel(listModel);
  }
  
  public static void main(String [] args){
	System.out.println("Hello World !");
	ChatUI c=new ChatUI();
  }  
  
  //User Interface code.
  public ChatUI(){
	    frame=new JFrame("Group Chat");
	    JPanel main =new JPanel();
	    JPanel top =new JPanel();
	    JPanel cn =new JPanel();
	    JPanel bottom =new JPanel();
	    ip=new JTextField();
	    tf=new JTextField();
	    name=new JTextField();
	    tx=new JTextArea();
	    connect=new JButton("Connect");
            Archives = new JButton("Archives");
	    JButton bt=new JButton("Send");
	    lst=new JList();        
	    main.setLayout(new BorderLayout(5,5));         
	    top.setLayout(new GridLayout(1,0,5,5));   
	    cn.setLayout(new BorderLayout(5,5));
	    bottom.setLayout(new BorderLayout(5,5));
	    top.add(new JLabel("Your name: "));top.add(name);    
	    top.add(new JLabel("Server Address: "));top.add(ip);
	    top.add(connect);
            top.add(Archives);
	    cn.add(new JScrollPane(tx), BorderLayout.CENTER);        
	    cn.add(lst, BorderLayout.EAST);    
	    bottom.add(tf, BorderLayout.CENTER);    
	    bottom.add(bt, BorderLayout.EAST);
	    main.add(top, BorderLayout.NORTH);
	    main.add(cn, BorderLayout.CENTER);
	    main.add(bottom, BorderLayout.SOUTH);
	    main.setBorder(new EmptyBorder(10, 10, 10, 10) );
	    //Events
	    connect.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e){ 
                  doConnect(); 
                  //JOptionPane.showMessageDialog(frame, "Entered in db");
                  } 
            });
	    bt.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e){ sendText();   }  });
	    tf.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e){ sendText();   }  });
	    
            Archives.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
              try{
                /*URL url = new URL("http://localhost:8087/");
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream())); 
			String line = in.readLine(); 

			System.out.println( line );	

			in.close*/
                  Desktop d=Desktop.getDesktop();
                  d.browse(new URI("http://localhost:8087/"));
              }catch(Exception e1){}
                  

 
            
            }});
	    
            frame.setContentPane(main);
	    frame.setSize(600,600);
	    frame.setVisible(true);  
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                 try{
                        String sql = "UPDATE users SET updated_at='"+getCurrentTimeStamp()+"',active=0 WHERE token='"+token+"'";
                        stmt.executeUpdate(sql);
                        }catch(Exception e){}
           /* if (JOptionPane.showConfirmDialog(frame, 
            "Are you sure to close this window?", "Really Closing?", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
            System.exit(0);
            }*/
          }
        });
	  }
	  JTextArea tx;
	  JTextField tf,ip, name;
	  JButton connect,Archives;
	  JList lst;
	  JFrame frame;
}