package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class HandleClient implements Runnable {

    private Socket socket;
    ObjectInputStream objectInputStream;

    public HandleClient(Socket socket) 
    {
        this.socket = socket;
    }

    public void run() {
        while (true) {
            try {
                try {
                    objectInputStream = new ObjectInputStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String[] s = (String[]) objectInputStream.readObject();
                //Message message;
                //message= (Message)objectInputStream.readObject();
                System.out.println("Message received");
                //System.out.println(s[0]+s[1]+s[2]+s[3]);
                Class.forName("com.mysql.jdbc.Driver");
                String url ="jdbc:mysql://localhost:3306/project";
                Connection connection = DriverManager.getConnection(url,"root","student");
                if (s[0].equals("signupt")) {
                    //teacher signup
                    String query1 = "INSERT INTO teacher (username,password,name) VALUES(?,?,?);";
                    PreparedStatement preStat = connection.prepareStatement(query1,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    //preStat.setInt(1, 1);
                    preStat.setString(1, s[1]);
                    preStat.setString(2, s[2]);
                    preStat.setString(3, s[3]);
                    preStat.executeUpdate();
                    System.out.println("Sign up of teacher completed.");
                    String query2 = "SELECT * FROM teacher;";
                    preStat = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    ResultSet result = preStat.executeQuery();
                    while (result.next()) {
                        String un = result.getString("username");
                        String ps = result.getString("password");
                        String n = result.getString("name");
                        System.out.println("Name - " + n);
                        System.out.println("username - " + un);
                        System.out.println("Password - " + ps);
                    }
                } else if (s[0].equals("logint")) {
                    //login teacher
                    String query2 = "SELECT * FROM teacher;";
                    PreparedStatement preStat = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    //preStat.setMaxRows(1);
                    ResultSet result = preStat.executeQuery();
                    Boolean flag = false;
                    while(result.next())
                    {
                        if(result.getString("username").equalsIgnoreCase(s[1]) && 
                        result.getString("password").equalsIgnoreCase(s[2]))
                        {
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                            objectOutputStream.writeObject("verified");   
                            flag = true;
                            break;
                        }
                    }
                    if(flag == false)
                    {
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream.writeObject("not verified");
                    }
                } else if (s[0].equals("signups")) {

                    String query1 = "INSERT INTO student ( ) VALUES(?,?,?,?);";
                    PreparedStatement preStat = connection.prepareStatement(query1,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    //preStat.setInt(1, 1);
                    preStat.setString(1, s[1]);
                    preStat.setString(2, s[2]);
                    preStat.setString(3, s[3]);
                    preStat.setString(4, s[4]);
                    preStat.executeUpdate();
                    System.out.println("Sign up of student completed.");
                    String query2 = "SELECT * FROM student;";
                    preStat = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    ResultSet result = preStat.executeQuery();
                    while (result.next()) {
                        String un = result.getString("username");
                        String ps = result.getString("password");
                        String n = result.getString("name");
                        String reg = result.getString("regno");
                        System.out.println("Name - " + n);
                        System.out.println("username - " + un);
                        System.out.println("Password - " + ps);
                        System.out.println("RegNo. - " + reg);
                    }

                } else if (s[0].equals("logins")) {
                    boolean flag = false;
                    String query2 = "SELECT * FROM student;";
                    PreparedStatement preStat = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    //preStat.setMaxRows(1);
                    ResultSet result = preStat.executeQuery();
                    while (result.next()) {
                        if (s[1].equals(result.getString("username")) && s[2].equals(result.getString("password"))) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        objectOutputStream.writeObject("verified");
                    } else {
                        objectOutputStream.writeObject("notverified");
                    }

                } else if (s[0].equals("newsubject")) {
                    int flag = 0;
                    String query0 = "SELECT * FROM subject;";
                    PreparedStatement preStat = connection.prepareStatement(query0,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    ResultSet result = preStat.executeQuery();
                    while (result.next()) {
                        if (s[1].equalsIgnoreCase(result.getString("subjectname"))) {
                            flag = 1;
                            break;
                        }
                    }
                    if (flag == 0) {
                        String query1 = "INSERT INTO subject (subjectname) VALUES(?);";
                        preStat = connection.prepareStatement(query1, ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                        //preStat.setInt(1, 1);
                        preStat.setString(1, s[1]);
                        preStat.executeUpdate();
                        String query = "CREATE TABLE " + s[1] + "(id int NOT NULL AUTO_INCREMENT PRIMARY KEY, quizcode VARCHAR(25), mcqstime int,mcqmtime int,tftime int)";
                        preStat = connection.prepareStatement(query,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                        preStat.executeUpdate();
                        System.out.println("Subject Added");
                        String query2 = "SELECT * FROM subject;";
                        preStat = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                        result = preStat.executeQuery();
                        int i = 0, ct = 0;
                        while (result.next()) {
                            ct++;
                        }
                        result.beforeFirst();
                        String str[] = new String[ct];
                        while (result.next()) {
                            str[i++] = result.getString("subjectname");
                        }
                        System.out.println(str[0]);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream.writeObject(str);
                    } else {
                        String[] str = {"duplicate"};
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        System.out.println(str[0]);
                        objectOutputStream.writeObject(str);
                    }
                } else if (s[0].equals("givesubjects")) {
                    String query2 = "SELECT * FROM subject;";
                    PreparedStatement preStat = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    ResultSet result = preStat.executeQuery();
                    int i = 0, ct = 0;
                    while (result.next()) {
                        ct++;
                    }
                    result.beforeFirst();
                    String str[] = new String[ct];
                    while (result.next()) {
                        str[i++] = result.getString("subjectname");
                    }
                    //System.out.println(str[0]);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(str);
                } else if (s[0].equals("givequizcodes")) {
                    String query2 = "SELECT * FROM " + s[1];
                    PreparedStatement preStat = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    ResultSet result = preStat.executeQuery();
                    int i = 0, ct = 0;
                    while (result.next()) {
                        ct++;
                    }
                    result.beforeFirst();
                    String str[] = new String[ct];
                    while (result.next()) {
                        str[i++] = result.getString("quizcode");
                    }
                    //System.out.println(str[0]);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(str);
                } else if (s[0].equals("quizcodeselected")) {
                    String query1 = "INSERT INTO " + s[1] + "(quizcode,mcqstime,mcqmtime,tftime) VALUES(?,?,?,?);";
                    PreparedStatement preStat = connection.prepareStatement(query1,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    //preStat.setInt(1, 1);
                    preStat.setString(1, s[2]);
                    preStat.setInt(2, 0);
                    preStat.setInt(3, 0);
                    preStat.setInt(4, 0);
                    preStat.executeUpdate();
                    String s1 = s[2] + "Leaderboard";
                    String query = "CREATE TABLE IF NOT EXISTS " + s[2] + "(id int NOT NULL AUTO_INCREMENT PRIMARY KEY,type VARCHAR(5), question VARCHAR(200), A varchar(50),B varchar(50),C varchar(50),D varchar(50), correctans varchar(4))";
                    String query2 = "CREATE TABLE IF NOT EXISTS " + s1 + "(id int NOT NULL AUTO_INCREMENT PRIMARY KEY, username VARCHAR(40), score int, time int,rating float);";
                    preStat = connection.prepareStatement(query,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    preStat.executeUpdate();
                    preStat = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    preStat.executeUpdate();
                    String str[] = {"quizcreated"};
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(str);
                } else if (s[0].equals("single")) {
                    String query1 = "INSERT INTO " + s[1] + "(type,question,A,B,C,D,correctans) VALUES(?,?,?,?,?,?,?);";
                    PreparedStatement preStat = connection.prepareStatement(query1,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    //preStat.setInt(1, 1);
                    preStat.setString(1, s[2]);
                    preStat.setString(2, s[3]);
                    preStat.setString(3, s[4]);
                    preStat.setString(4, s[5]);
                    preStat.setString(5, s[6]);
                    preStat.setString(6, s[7]);
                    preStat.setString(7, s[8]);
                    preStat.executeUpdate();
                    System.out.println("question updated:");
                } else if (s[0].equals("true")) {
                    String query1 = "INSERT INTO " + s[1] + "(type,question,A,B,C,D,correctans) VALUES(?,?,?,?,?,?,?);";
                    PreparedStatement preStat = connection.prepareStatement(query1,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    //preStat.setInt(1, 1);
                    preStat.setString(1, s[2]);
                    preStat.setString(2, s[3]);
                    preStat.setString(3, "");
                    preStat.setString(4, "");
                    preStat.setString(5, "");
                    preStat.setString(6, "");
                    preStat.setString(7, s[4]);
                    preStat.executeUpdate();
                    System.out.println("question updated:");
                } else if (s[0].equals("multiple")) {
                    String query1 = "INSERT INTO " + s[1] + "(type,question,A,B,C,D,correctans) VALUES(?,?,?,?,?,?,?);";
                    PreparedStatement preStat = connection.prepareStatement(query1,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    //preStat.setInt(1, 1);
                    preStat.setString(1, s[2]);
                    preStat.setString(2, s[3]);
                    preStat.setString(3, s[4]);
                    preStat.setString(4, s[5]);
                    preStat.setString(5, s[6]);
                    preStat.setString(6, s[7]);
                    preStat.setString(7, s[8]);
                    preStat.executeUpdate();
                    System.out.println("question updated:");
                } 
                else if(s[0].equals("settime"))
                {  int mcqst=Integer.parseInt(s[3]);
                int mcqmt=Integer.parseInt(s[4]);
                int tft=Integer.parseInt(s[5]);
                   String query4="update "+s[1]+" set mcqstime=? where quizcode=?";
                  
                    PreparedStatement preStat = connection.prepareStatement(query4,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                      preStat.setInt(1, mcqst);
                  
                      preStat.setString(2, s[2]);
                    preStat.executeUpdate();
                    
                     String query3="update "+s[1]+" set mcqmtime=? where quizcode=?";
    
                        PreparedStatement preStat1 = connection.prepareStatement(query3,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                        preStat1.setInt(1, mcqmt);
                  
                        preStat1.setString(2, s[2]);
                        preStat1.executeUpdate();
                    
                     String query2="update "+s[1]+" set tftime=? where quizcode=?";
                  
                    PreparedStatement preStat3 = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                      preStat3.setInt(1, tft);
                  
                      preStat3.setString(2, s[2]);
                    preStat3.executeUpdate();
                }
                 else if (s[0].equals("questionanswermcqm")) {
                    String d = "m";
                    String query2 = "SELECT * FROM " + s[1] + " where type=?";
                    PreparedStatement preStat = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    preStat.setString(1, d);
                    ResultSet result = preStat.executeQuery();
                    int i = 0, ct = 0;
                    while (result.next()) {
                        ct++;
                    }
                    result.beforeFirst();
                    String questions[][] = new String[ct][6];
                    while (result.next()) {
                        questions[i][0] = result.getString("question");
                        questions[i][1] = result.getString("A");
                        questions[i][2] = result.getString("B");
                        questions[i][3] = result.getString("C");
                        questions[i][4] = result.getString("D");
                        questions[i][5] = result.getString("correctans");
                        //questions[i][6] = Integer.toString(result.getInt("time"));
                        i++;
                    }
                    //System.out.println(str[0]);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(questions);
                } 
                 else if (s[0].equals("questionanswermcqs")) {
                    String d = "s";
                    String query2 = "SELECT * FROM " + s[1] + " where type=?";
                    PreparedStatement preStat = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    preStat.setString(1, d);
                    ResultSet result = preStat.executeQuery();
                    int i = 0, ct = 0;
                    while (result.next()) {
                        ct++;
                    }
                    result.beforeFirst();
                    String questions[][] = new String[ct][6];
                    while (result.next()) {
                        questions[i][0] = result.getString("question");
                        questions[i][1] = result.getString("A");
                        questions[i][2] = result.getString("B");
                        questions[i][3] = result.getString("C");
                        questions[i][4] = result.getString("D");
                        questions[i][5] = result.getString("correctans");
                        //questions[i][6] = Integer.toString(result.getInt("time"));
                        i++;
                    }

                    //System.out.println(str[0]);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(questions);
                }else if (s[0].equals("questionanswertf")) {
                    String d = "t";
                    String query2 = "SELECT * FROM " + s[1] + " where type=?";
                    PreparedStatement preStat = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    preStat.setString(1, d);
                    ResultSet result = preStat.executeQuery();
                    int i = 0, ct = 0;
                    while (result.next()) {
                        ct++;
                    }
                    result.beforeFirst();
                    String questions[][] = new String[ct][2];
                    while (result.next()) {
                        questions[i][0] = result.getString("question");
                        questions[i][1] = result.getString("correctans");
                        i++;
                    }
                    //System.out.println(str[0]);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(questions);
                } else if (s[0].equals("quizcompleted")) {
                    String query1 = "INSERT INTO " + s[1] + "leaderboard" + " (username,score,rating) VALUES(?,?,?);";
                    PreparedStatement preStat = connection.prepareStatement(query1,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    //preStat.setInt(1, 1);
                    preStat.setString(1, s[2]);
                    float r = Float.parseFloat(s[4]);
                    preStat.setFloat(3, r);
                    preStat.setInt(2, Integer.parseInt(s[3]));
                    preStat.executeUpdate();
                    System.out.println("Leader-Board Updated");
                } else if (s[0].equals("rating")) {
                    String sr = s[1] + "leaderboard";
                    String query2 = "SELECT rating FROM " + sr;
                    PreparedStatement preStat = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    ResultSet result = preStat.executeQuery();
                    int i = 0, ct = 0;
                    while (result.next()) {
                        ct++;
                    }
                    String str[] = new String[1];
                    if (ct == 0) {
                        str[0] = "0";
                    } else {
                        result.beforeFirst();
                        float rating = 0.0F;

                        while (result.next()) {
                            rating += result.getFloat("rating");
                        }
                        float ans = rating / ct;
                        str[0] = "" + ans;
                    }
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(str);
                } else if (s[0].equals("sectioncompletemcqs")) {
                    float f1 = Float.parseFloat(s[4]);
                    int score = Integer.parseInt(s[3]);
                    String query1 = "INSERT INTO " + s[1] + "leaderboard" + " (username,score,rating) VALUES(?,?,?);";
                    PreparedStatement preStat = connection.prepareStatement(query1,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    preStat.setString(1, s[2]);
                    preStat.setFloat(3, f1);
                    preStat.setInt(2, score);
                    preStat.executeUpdate();
                    System.out.println("Leader-Board Updated");
                } else if (s[0].equals("sectioncompletemcqm")) {
                    int score = Integer.parseInt(s[3]);
                    String query2 = "SELECT score FROM " + s[1] + "leaderboard where username=?";
                    PreparedStatement preStat = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    preStat.setString(1, s[2]);
                    ResultSet result = preStat.executeQuery();
                    result.next();
                    int score1 = Integer.parseInt(result.getString("score"));
                    score = score1 + score;
                    //System.out.println(score);
                    String query1 = "update " + s[1] + "leaderboard set score=" + score + " where username=?;";
                    PreparedStatement preStat1 = connection.prepareStatement(query1,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    preStat1.setString(1, s[2]);
                    preStat1.executeUpdate();
                    System.out.println("Leader-Board Updated");
                } else if (s[0].equals("sectioncompletetf")) {
                    int score = Integer.parseInt(s[3]);
                   // System.out.println("score of t f is" + score);
                    String query2 = "SELECT score FROM " + s[1] + "leaderboard where username=?";
                    PreparedStatement preStat = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    preStat.setString(1, s[2]);
                    ResultSet result = preStat.executeQuery();
                    result.next();
                    int score1 = Integer.parseInt(result.getString("score"));
                    score = score1 + score;
                    //System.out.println(score);
                    String query1 = "update " + s[1] + "leaderboard set score=" + score + " where username=?;";
                    PreparedStatement preStat1 = connection.prepareStatement(query1,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    preStat1.setString(1, s[2]);
                    preStat1.executeUpdate();
                    System.out.println("Leader-Board Updated");
                }else if (s[0].equals("gettime")) {
                    String query2 = "SELECT * FROM " +s[1]+" where quizcode=?;";
                    PreparedStatement preStat = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    preStat.setString(1,s[2]);
                    ResultSet result = preStat.executeQuery();
                    result.next();
                   
                    int str[] = new int[3];
                
                        str[0] = result.getInt("mcqstime");
                        str[1] = result.getInt("mcqmtime");
                        str[2] = result.getInt("tftime");
                    
                    //System.out.println(str[0]);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(str); 
                }
                else if (s[0].equals("final")) {
                    float f1 = Float.parseFloat(s[3]);
                    String query2 = "update " + s[1] + "leaderboard set rating=" + f1 + " where username=?;";

                    PreparedStatement preStat = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    //preStat.setInt(1, 1);
                    preStat.setString(1, s[2]);
                    //float r=Float.parseFloat(s[4]);
                    //preStat.setFloat(3, r);
                    //preStat.setInt(2, Integer.parseInt(s[3]));
                    preStat.executeUpdate();
                    System.out.println("Leader-Board Updated");
                    String query3 = "SELECT score FROM "+s[1]+"leaderboard where username=?";
                    PreparedStatement preStat1 = connection.prepareStatement(query3,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    preStat1.setString(1, s[2]);
                    ResultSet result = preStat1.executeQuery();
                    result.next();
                    int score1 = result.getInt("score");
                    System.out.println(score1);
                    try {
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        //int score = {"final",qcode,username,rating1};
                        objectOutputStream.writeObject(score1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else if(s[0].equals("leaderboard"))
                {
                   
                    String query2 = "SELECT * FROM " + s[1]+"leaderboard order by score desc;";
                    PreparedStatement preStat = connection.prepareStatement(query2,ResultSet.TYPE_SCROLL_SENSITIVE, 
                        ResultSet.CONCUR_UPDATABLE);
                    //preStat.setString(1, d);
                    ResultSet result = preStat.executeQuery();
                    int i = 0, ct = 0;
                    while (result.next()) {
                        ct++;
                    }
                    result.beforeFirst();
                    String leader[][] = new String[ct][2];
                    while (result.next()) {
                        
                        leader[i][0] = result.getString("username");
                        // questions[i][1] = result.getString("A");
                        // questions[i][2] = result.getString("B");
                        // questions[i][3] = result.getString("C");
                        // questions[i][4] = result.getString("D");
                        leader[i][1] = result.getString("score");
                        //questions[i][2] = Integer.toString(result.getInt("time"));
                        i++;
                    }
                    //System.out.println(str[0]);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(leader);
                }

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

}
