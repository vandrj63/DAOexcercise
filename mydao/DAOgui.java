package mydao;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


import mymodel.User;

/**
 * Test harness for the mydao package. This require the following preconditions:
 * <ol>
 * <li>A MySQL server running at localhost:3306 with a database named 'javabase'.
 * <li>A 'user' table in the 'javabase' database which is created as follows:
 * <pre>
 * CREATE TABLE javabase.user (
 *     id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
 *     username VARCHAR(15) NOT NULL,
 *     password VARCHAR(32) NOT NULL,
 *     email VARCHAR(60) NULL,
 *     age SMALLINT(3) UNSIGNED NULL,
 *
 *     PRIMARY KEY (id),
 *     UNIQUE (username),
 *     UNIQUE (email)
 * )
 * </pre>
 * <li>A MySQL user with the name 'java' and password 'd$7hF_r!9Y' which has sufficient rights on
 * the javabase.user table.
 * <li>A MySQL JDBC Driver JAR file in the classpath.
 * <li>A properties file 'dao.properties' in the classpath with the following entries:
 * <pre>
 * javabase.jdbc.driver = com.mysql.jdbc.Driver
 * javabase.jdbc.url = jdbc:mysql://localhost:3306/javabase
 * javabase.jdbc.username = java
 * javabase.jdbc.password = d$7hF_r!9Y
 * </pre>
 * </ol>
 *
 * @author BalusC
 * @link http://balusc.blogspot.com/2008/07/dao-tutorial-data-layer.html
 */
public class DAOgui {
	
	
    public static boolean RIGHT_TO_LEFT = false;
    
    public static void addComponentsToPane(Container pane, final UserDAO dao) {
         
        if (!(pane.getLayout() instanceof BorderLayout)) {
            pane.add(new JLabel("Container doesn't use BorderLayout!"));
            return;
        }
         
        if (RIGHT_TO_LEFT) {
            pane.setComponentOrientation(
                    java.awt.ComponentOrientation.RIGHT_TO_LEFT);
        }
         
        //JButton button = new JButton("Button 1 (PAGE_START)");
        //pane.add(button, BorderLayout.PAGE_START);
         
        //Make the center component big, since that's the
        //typical usage of BorderLayout.
        final JTextArea ta = new JTextArea();
        //ta.setPreferredSize(new Dimension(600,300));
        
        ta.setText("");
        JScrollPane textPane = new JScrollPane(ta);
        textPane.setPreferredSize(new Dimension(500, 200));
        pane.add(textPane, BorderLayout.SOUTH);
         
        JPanel south = new JPanel();
        pane.add(south,BorderLayout.WEST);
        
        JPanel east = new JPanel();
        pane.add(east, BorderLayout.EAST);
        
        south.setLayout(new GridLayout(2,2));
        
        JButton insertButton = new JButton("insert");
        south.add(insertButton, BorderLayout.WEST);
         
        JButton deleteButton = new JButton("delete");
        south.add(deleteButton, BorderLayout.WEST);

        JButton listButton = new JButton("list");
        south.add(listButton, BorderLayout.WEST);
        
        JButton updateButton = new JButton("update");
        south.add(updateButton, BorderLayout.WEST);
        
        JLabel nameLabel, pwLabel, emailLabel, ageLabel, idLabel;
        final JTextField name;
		final JTextField pw;
		final JTextField email;
		final JTextField age;
		final JTextField id;
        nameLabel = new JLabel("name");
        
        Font newLabelFont=new Font(nameLabel.getFont().getName(),Font.BOLD,nameLabel.getFont().getSize());
        nameLabel.setFont(newLabelFont);
        
        name = new JTextField();
        name.setColumns(10);
        //name.setPreferredSize(new Dimension(75,25));
        pw = new JTextField();
        pwLabel = new JLabel("Password");
        pwLabel.setFont(newLabelFont);
        //name.setPreferredSize(new Dimension(75,25));
        email = new JTextField();
        emailLabel = new JLabel("Email");
        emailLabel.setFont(newLabelFont);
        //name.setPreferredSize(new Dimension(75,25));
        age = new JTextField();
        ageLabel = new JLabel("Age");
        ageLabel.setFont(newLabelFont);
        
        id = new JTextField();
        idLabel = new JLabel("ID");
        idLabel.setFont(newLabelFont);
        
        east.setLayout(new GridLayout(5,2));
        east.add(idLabel);
        east.add(id,BorderLayout.EAST);
        east.add(nameLabel);
        east.add(name,BorderLayout.EAST);
        east.add(pwLabel);
        east.add(pw,BorderLayout.EAST);
        east.add(emailLabel);
        east.add(email,BorderLayout.EAST);
        east.add(ageLabel);
        east.add(age,BorderLayout.EAST);
        //final User user = new User(null, "username", "password");  
        
        insertButton.addActionListener(new ActionListener(){
        	
            public void actionPerformed(ActionEvent ae) {
                try{
            	User user = new User(null, name.getText(), pw.getText(), email.getText(), Integer.parseInt(age.getText()));
                dao.create(user);
                ta.setText("Added: " + user);
                }catch(Exception e) {
					// TODO Auto-generated catch block
                	ta.setText("reEnter");
					e.printStackTrace();
				}

            }
        });
        
        deleteButton.addActionListener(new ActionListener(){
            
            public void actionPerformed(ActionEvent ae) {
            	
            	try{
            	User user = dao.find(Long.parseLong(id.getText()));	
                dao.delete(user);
                ta.setText("Deleted: \n" + user);
            	}//catch(DAOException e) {
					// TODO Auto-generated catch block
            		//ta.setText("Must enter username and password!");
					//e.printStackTrace();
				//}
            	catch(Exception e) {
					// TODO Auto-generated catch block
            		ta.setText("reEnter");
					e.printStackTrace();
				}
            }
        });
        
        listButton.addActionListener(new ActionListener(){
        	@Override
            public void actionPerformed(ActionEvent ae) {
            	
                List<User> users;
				try {
					users = dao.list();
					Object[] users1 = users.toArray();
					//ta.setText("");
					ta.setText("current list: \n");
					for(int i=0;i<users.size();i++)
						ta.append((users1[i].toString() + "\n"));
					
					//ta.append("Thus, amount of users in database is: " + users.size());
	                //System.out.println("List of users successfully queried: " + users.toString());
	                //System.out.println("Thus, amount of users in database is: " + users.size());
				} catch (DAOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


   
            }
        });   
        
        updateButton.addActionListener(new ActionListener(){
        	
            public void actionPerformed(ActionEvent ae) {
                try{
            	User user = dao.find(Long.parseLong(id.getText()));	
                
            	if(name.getText() != null)
                	user.setUsername(name.getText());
                if(pw.getText() != null)
                	user.setPassword(pw.getText());
                if(email.getText() != null)
                	user.setEmail(email.getText());
                if(age.getText() != null)
                	user.setAge(Integer.parseInt(age.getText()));
                
                dao.update(user);
                //System.out.println("User successfully updated: " + user);
                ta.setText("User successfully updated: \n" + user);
                }catch(Exception e) {
					// TODO Auto-generated catch block
                	ta.setText("Must enter id!");
					e.printStackTrace();
				}

            }
        });
    }
     
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI(UserDAO dao) {
         
        //Create and set up the window.
        JFrame frame = new JFrame("Heya");
        frame.setPreferredSize(new Dimension(600, 400));
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Set up the content pane.
        addComponentsToPane(frame.getContentPane(),dao);
        //Use the content pane's default BorderLayout. No need for
        //setLayout(new BorderLayout());
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
	
    public static void main(String[] args) throws Exception {

        // Obtain DAOFactory.
        DAOFactory javabase = DAOFactory.getInstance("javabase.jdbc");
        System.out.println("DAOFactory successfully obtained: " + javabase);

        // Obtain UserDAO.
        UserDAO userDAO = javabase.getUserDAO();
        System.out.println("UserDAO successfully obtained: " + userDAO);
        
        createAndShowGUI(userDAO);

    }

}