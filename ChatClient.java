package Chat;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.EventHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.text.Keymap;

/**
 * A simple Swing-based client for the chat server.  Graphically
 * it is a frame with a text field for entering messages and a
 * textarea to see the whole dialog.
 *
 * The client follows the Chat Protocol which is as follows.
 * When the server sends "SUBMITNAME" the client replies with the
 * desired screen name.  The server will keep sending "SUBMITNAME"
 * requests as long as the client submits screen names that are
 * already in use.  When the server sends a line beginning
 * with "NAMEACCEPTED" the client is now allowed to start
 * sending the server arbitrary strings to be broadcast to all
 * chatters connected to the server.  When the server sends a
 * line beginning with "MESSAGE " then all characters following
 * this string should be displayed in its message area.
 */
public class ChatClient {

    BufferedReader in;
    PrintWriter out;
    JFrame frame = new JFrame("ChatApp :)");
    JButton Jb = new JButton("Send");
    JTextArea messageArea = new JTextArea(8, 40);
    JTextField textField = new JTextField(40);
    JPanel Jp = new JPanel();
    JLabel Jl = new JLabel("Click enter to send");
	JCheckBox Jc = new JCheckBox();
        /**
     * Constructs the client by laying out the GUI and registering a
     * listener with the textfield so that pressing Return in the
     * listener sends the textfield contents to the server.  Note
     * however that the textfield is initially NOT editable, and
     * only becomes editable AFTER the client receives the NAMEACCEPTED
     * message from the server.
     */
    public ChatClient() {

        // Layout GUI
    	Jb.setPreferredSize(new Dimension(75,75));
    	Jp.add(textField);
    	Jp.add(Jb);
    	Jp.add(Jl);
    	Jp.add(Jc);
        textField.setEditable(false);
        messageArea.setEditable(false);
      // Jb.setEnabled(false);
        frame.getContentPane().add(Jp, "South");
       // frame.getContentPane().add(Jp, "East");
        frame.getContentPane().add(new JScrollPane(messageArea), "North");
        frame.pack();
       
        	Jc.addActionListener(new ActionListener(){
        	 public void actionPerformed(ActionEvent e){
        		
        		 if(Jc.isSelected()){
        			
                	 Jb.setEnabled(false);
                	
                	textField.addActionListener(new ActionListener(){
                		 public void actionPerformed(ActionEvent e){
                			
                			 out.println(textField.getText());
                             textField.setText("");
                    	 }
                	});
                }
        		 else{
        			 
        			
        			textField.addKeyListener(new KeyAdapter(){
        				public void KeyPressed(KeyEvent e){
        					JOptionPane.showMessageDialog(frame, "Enter");
        					if(e.getKeyCode() == KeyEvent.VK_ENTER){
        						e.consume();
        					    Jb.doClick();
        				}
        				}
        			});
        			 Jb.setEnabled(true);
        			
        		 }
        		 
        	 }
        });
        	 /**
             * Responds to pressing the enter key in the textfield by sending
             * the contents of the text field to the server.    Then clear
             * the text area in preparation for the next message.
             */
        	 Jb.addActionListener(new ActionListener(){
				 public void actionPerformed(ActionEvent e){
					 out.println(textField.getText());
		                textField.setText("");
				 }
			 });
        
            /**
             * Responds to pressing the enter key in the textfield by sending
             * the contents of the text field to the server.    Then clear
             * the text area in preparation for the next message.
             */
        
    }

    /**
     * Prompt for and return the address of the server.
     */
    private String getServerAddress() {
        return JOptionPane.showInputDialog(
            frame,
            "Enter IP Address of the Server(enter mypc):",
            "Welcome to the ChatApp :)",
            JOptionPane.QUESTION_MESSAGE);
    }

    /**
     * Prompt for and return the desired screen name.
     */
    private String getName() {
        return JOptionPane.showInputDialog(
            frame,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.QUESTION_MESSAGE);
    }
    /**
     * If a user enters a screen name that is already being used, this method is called.
     * This displays a dialog box with a message prompting to re enter a new screen name.
     */
    private void Message() {
         JOptionPane.showMessageDialog(frame, "Name exists, Please select a new name");
        		
        		//.showInputDialog("Screen name exists");
        		//e.showConfirmDialog(frame, "Screen name already exists");
        		
        		/*showInputDialog(
                frame,
                "Screen name already exists, Choose another screen name:",
                "Screen name selection",
                JOptionPane.PLAIN_MESSAGE);*/
        }

    /**
     * Connects to the server then enters the processing loop.
     */
    private void run() throws IOException {
    	
    	String serverAddress = null;
        // Make connection and initialize streams
    	if(getServerAddress().equals("mypc")){
        serverAddress = "192.168.0.15";
    	}
        Socket socket = new Socket(serverAddress, 9001);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        while (true) {
            String line = in.readLine();
            if(line.startsWith("EXISTS")){
            	Message();
            	
        } 
            else if (line.startsWith("SUBMITNAME")) {
                out.println(getName());
            }
             
            else if (line.startsWith("NAMEACCEPTED")) {
                textField.setEditable(true);
            } else if (line.startsWith("MESSAGE")) {
                messageArea.append(line.substring(8) + "\n");
            }
            
           
        } 
        }

    /**
     * Runs the client as an application with a closeable frame.
     */
    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }
}