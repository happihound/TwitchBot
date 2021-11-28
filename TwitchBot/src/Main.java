import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.cavariux.twitchirc.Chat.Channel;

public class Main extends JPanel implements ActionListener {
	private static final long serialVersionUID = -7052336505253806151L;
	// channel to connect to allows us to dynamically assign channel
	static String channelString = "happihound";
	// location of the user database which can be relocated anywhere
	static String userFileLocation = "C:\\Userlist\\";
	static Proofreads bot = new Proofreads();
	// global variable for the names in the user database to avoid redeclaring it
	// extensively
	static String[] pathnames;
	static String[] leaderBoard = { "na 0", "na 0", "na 0", "na 0", "na 0", };
	static File f = new File(userFileLocation);
	// frame declaration
	static JFrame frame2 = new JFrame("Status");
	// panel for buttons and user list
	static JPanel userActions = new JPanel();
	// panel for the log
	static JPanel logDisplay = new JPanel(new GridBagLayout());
	static JPanel centerDisplay = new JPanel(new GridBagLayout());
	static String[] logMessages = { "no data", "no data", "no data", "no data", "no data", "no data", "no data",
			"no data" };
	static long updateUserTime = System.currentTimeMillis();
	// list for the user list
	static DefaultListModel<String> listModel = new DefaultListModel<String>();
	// list for the log display
	static DefaultListModel<String> listModel2 = new DefaultListModel<String>();
	static DefaultListModel<String> tableModelLeaderBoard = new DefaultListModel<String>();
	static long startTime = System.currentTimeMillis();
	static Channel channelName = Channel.getChannel(channelString, bot);
	// administrator list to determine advanced commands
	static String[] admins = { "happihound", "wattson_x", "ninahelane", "almost_lived" };
	static Random rand = new Random();
	static int pointStep = 1;
	// default selected name in order to avoid null string errors
	static String selectedName = "happihound";
	// list of rigged users
	static List<riggedUser> riggedList = new ArrayList<riggedUser>();
	static JLabel points = new JLabel("");
	static JLabel status = new JLabel("");
	static JLabel riggedStatus = new JLabel("");
	static JLabel timeSinceLastMessage = new JLabel("");
	static boolean oneTimeRigged = false;

	public static void main(String[] args) {
		writeLog("program started");
		// starts the drawing and connects the twitchbot
		new Thread(new Runnable() {
			@Override
			public void run() {
				Draw();
				connect1();
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				updateUserTime();
			}
		}).start();
		// continually redraws the display to keep it updated in parallel to other tasks
		// updates the list in order to show updated user list and updated leaderboard
		updateUsers();
		updateList();

	}

	// driver code to write to the log file centralized to allow writing from any
	// point
	public static void writeLog(String message) {
		try
		{
		    FileWriter fw = new FileWriter(userFileLocation + "logFile.txt",true); //the true will append the new data
		    fw.write(message +"\n");//appends the string to the file
		    fw.close();
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
	}

// Separate method so that the bot can be restarted by running this again
	public static void connect1() {
		writeLog("bot connecting");
		bot.connect();
		bot.joinChannel(channelString);
		bot.start();
	}

// leaderboard update code 
	public static void leaderBoard() {
		pathnames = f.list();
// has to be array literals in order to have a 0 secondary value
		leaderBoard[0] = ("na 0");
		leaderBoard[1] = ("na 0");
		leaderBoard[2] = ("na 0");
		leaderBoard[3] = ("na 0");
		leaderBoard[4] = ("na 0");
// loops through the names in the user database and checks for difference in point value to determine top 5 users 
		for (int i = 0; pathnames.length != i + 1; i++) {

			int points = Proofreads.getUserPoints(pathnames[i].replaceAll(".txt", ""));

			for (byte j = 0; leaderBoard.length != j; j++) {

				String[] commandString = leaderBoard[j].split(" ");

				if (Integer.parseInt(commandString[commandString.length - 1]) <= points
						&& commandString[1] != pathnames[i]) {
					leaderBoard[j] = +j + 1 + "# " + pathnames[i].replaceAll(".txt", " ") + " has " + points;
					break;
				}

			}
		}
		frame2.repaint();

		tableModelLeaderBoard.clear();
		for (int i = 0; leaderBoard.length > i;) {
			tableModelLeaderBoard.addElement(leaderBoard[i]);
			i++;
		}
	}

	public static void updateUserTime() {
		while (1 > 0) {
			if (selectedName == null) {
				selectedName = "happihound";
			}
			updateUserTime = System.currentTimeMillis();
			timeSinceLastMessage.setText(selectedName + " time since last message "
					+ String.valueOf(((int) (System.currentTimeMillis() / 1000)
							- Proofreads.returnUserTime(selectedName.replace(".txt", "")))));
		}
	}

	// code to auto update the various lists in the panel every minute
	public static void updateUsers() {

		if (selectedName == null) {
			selectedName = "happihound";
		}

		points.setText(selectedName.replace(".txt", "") + " Points: " + Proofreads.getUserPoints(selectedName));
		riggedStatus.setText("");
		if (Proofreads.checkRigged(selectedName)[0]) {
			String tempRigged = "";
			if (Proofreads.checkRigged(selectedName)[1]) {
				tempRigged = "win";
			}
			if (!Proofreads.checkRigged(selectedName)[1]) {
				tempRigged = "lose";
			}
			riggedStatus.setText("rigged= " + (selectedName).replace(".txt", "") + " " + tempRigged);

		}
	}

	public static void Draw() {
		updateLogDisplay();
		GridBagConstraints c = new GridBagConstraints();
		centerDisplay.setLayout(new GridBagLayout());
		userActions.setLayout(new GridBagLayout());
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weighty = 0;
		pathnames = f.list();
		for (int i = 0; pathnames.length > i;) {
			listModel.addElement(pathnames[i]);
			i++;
		}
		// user list component for the panel
		JList<String> jList = new JList<String>(listModel);
		jList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		jList.setLayoutOrientation(JList.VERTICAL);
		jList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				String lastSelectedName = selectedName;
				selectedName = jList.getSelectedValue();
				updateUsers();
				if (selectedName == null) {
					selectedName = lastSelectedName;
				}

				lastSelectedName = selectedName;

			}
		});

		// log display list
		JList<String> Jlist2 = new JList<String>(listModel2);
		Jlist2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		Jlist2.setLayoutOrientation(JList.VERTICAL);
		Jlist2.setPreferredSize(new Dimension(500, 50));

		// reconnect button to easily restart the bot code if it crashes due to a socket
		// exception
		c.gridwidth = 2;
		JButton reConnect = new JButton("reConnect");
		reConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeLog("Bot reconnecting");
				bot.partChannel(channelString);
				bot.stop();
				new Thread(new Runnable() {
					@Override
					public void run() {
						connect1();
					}
				}).start();

			}
		});
		// c.weightx = 0.5;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		userActions.add(reConnect, c);

		// button to submit the points entered in the text input
		JButton setPointsSubmit = new JButton("setPointsSubmit");
		setPointsSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selectedName1 = selectedName.replaceAll(".txt", "");
				writeLog("admin changed points on " + selectedName1 + " to " + pointStep);

				int points = Proofreads.getUserPoints(selectedName1);

				int oldPoints = points;

				points = Proofreads.getUserPoints(selectedName1);

				if (points == -1) {
					Main.writeLog("a read/write error occured");
				}

				else if (points == -2) {
					Main.writeLog("error editing " + selectedName1 + " user does not exists");
				}

				int statusCheck = Proofreads.setUserPoints(selectedName1, pointStep);

				if (statusCheck == -1) {
					Main.writeLog("a read/write error occured when trying to edit points");
					return;
				}

				else if (statusCheck == -2) {
					Main.writeLog("user " + selectedName1 + " did not exists");
					return;
				}

				if (Proofreads.returnStatus()) {
					bot.sendMessage(selectedName1 + "'s points were changed from " + oldPoints + " to " + pointStep,
							channelName);
				}

			}

		});

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		userActions.add(setPointsSubmit, c);

// text field to change user's points
		JTextField textField = new JTextField("10", 16);
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String tempNumber = textField.getText();
				byte sign = 1;
				if (tempNumber.contains("-")) {
					sign = -1;
				}
				int tempNumber2 = Integer.parseInt(tempNumber.replaceAll("[^\\d.]", ""));
				pointStep = tempNumber2 * sign;

			}
		});

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		userActions.add(textField, c);
// scrollable user list
		JScrollPane scrollPane = new JScrollPane(jList);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 3;
		c.ipady = 400;
		c.ipadx = 250;
		userActions.add(scrollPane, c);
		c.ipady = 0;
		c.ipadx = 0;
// easy way to force a user list update instead of waiting 60 seconds
		JButton refreshUserList = new JButton("refreshUserlist");
		refreshUserList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				riggedStatus.setText("");
				if (Proofreads.checkRigged(selectedName)[0]) {
					String tempRigged = "";
					if (Proofreads.checkRigged(selectedName)[1]) {
						tempRigged = "win";
					}
					if (!Proofreads.checkRigged(selectedName)[1]) {
						tempRigged = "lose";
					}
					riggedStatus.setText("rigged= " + (selectedName).replace(".txt", "") + " " + tempRigged);
				}
				frame2.repaint();
				updateLogDisplay();
				updateList();
				frame2.repaint();

			}
		});

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 4;
		userActions.add(refreshUserList, c);
// turn on or off command execution
		JButton toggleStatus = new JButton("toggleStatus");
		toggleStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				writeLog("Admin changed status");
				Proofreads.toggleRunning();
				status.setText("STATUS Online: " + Proofreads.returnStatus());
				updateLogDisplay();

			}
		});

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 5;
		userActions.add(toggleStatus, c);
// allows the selected user to be rigged to lose
		c.gridwidth = 1;
		c.weightx = 1;
		JButton rigUserLose = new JButton("rigUserLose");
		rigUserLose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 1; riggedList.size() >= i && riggedList.size() != 0; i++) {
					if (riggedList.get(i - 1).name == selectedName) {
						riggedList.remove(i - 1);
					}
				}
				riggedUser tempUser = new riggedUser(selectedName, false);
				riggedList.add(tempUser);
				riggedStatus.setText("");
				if (Proofreads.checkRigged(selectedName)[0]) {
					String tempRigged = "";
					if (Proofreads.checkRigged(selectedName)[1]) {
						tempRigged = "win";
					}
					if (!Proofreads.checkRigged(selectedName)[1]) {
						tempRigged = "lose";
					}
					riggedStatus.setText("rigged= " + (selectedName).replace(".txt", "") + " " + tempRigged);
				}
				frame2.repaint();
				writeLog("User " + selectedName + " was rigged to lose");
				updateLogDisplay();
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 6;
		userActions.add(rigUserLose, c);
// allows the selected user to be rigged to win
		JButton rigUserWin = new JButton("rigUserWin");
		rigUserWin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				for (int i = 1; riggedList.size() >= i && riggedList.size() != 0; i++) {
					if (riggedList.get(i - 1).name == selectedName) {
						riggedList.remove(i - 1);
					}
				}
				riggedUser tempUser = new riggedUser(selectedName, true);
				riggedList.add(tempUser);
				riggedStatus.setText("");
				if (Proofreads.checkRigged(selectedName)[0]) {
					String tempRigged = "";
					if (Proofreads.checkRigged(selectedName)[1]) {
						tempRigged = "win";
					}
					if (!Proofreads.checkRigged(selectedName)[1]) {
						tempRigged = "lose";
					}
					riggedStatus.setText("rigged= " + (selectedName).replace(".txt", "") + " " + tempRigged);
				}
				frame2.repaint();
				writeLog("User " + selectedName + " was rigged to win");
				updateLogDisplay();
			}
		});

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 6;
		userActions.add(rigUserWin, c);
		c.gridwidth = 2;
		c.weightx = 0;
//disables rigged gambles on the selected user
		JButton unRigUser = new JButton("unRigUser");
		unRigUser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				for (int i = 1; riggedList.size() >= i && riggedList.size() != 0; i++) {
					if (riggedList.get(i - 1).name == selectedName) {
						riggedList.remove(i - 1);
					}
				}
				riggedStatus.setText("");
				if (Proofreads.checkRigged(selectedName)[0]) {
					String tempRigged = "";
					if (Proofreads.checkRigged(selectedName)[1]) {
						tempRigged = "win";
					}
					if (!Proofreads.checkRigged(selectedName)[1]) {
						tempRigged = "lose";
					}
					riggedStatus.setText("rigged= " + (selectedName).replace(".txt", "") + " " + tempRigged);
				}
				frame2.repaint();
				writeLog("User " + selectedName + " was unrigged");
				updateLogDisplay();
			}
		});

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 7;
		userActions.add(unRigUser, c);
//disables rigged gambles on all users
		JButton unRigAll = new JButton("unRigAll");
		unRigAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (byte j = 0; 10 >= j; j++) {
					for (int i = 1; riggedList.size() >= i && riggedList.size() != 0; i++) {
						riggedList.remove(i - 1);
					}
				}
				riggedStatus.setText("");
				if (Proofreads.checkRigged(selectedName)[0]) {
					String tempRigged = "";
					if (Proofreads.checkRigged(selectedName)[1]) {
						tempRigged = "win";
					}
					if (Proofreads.checkRigged(selectedName)[1]) {
						tempRigged = "lose";
					}
					riggedStatus.setText("rigged= " + (selectedName).replace(".txt", "") + " " + tempRigged);
				}

				frame2.repaint();
				writeLog("User all users were unrigged");
				updateLogDisplay();
			}
		});

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 8;
		userActions.add(unRigAll, c);
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.NORTH;
		c.weighty = 0;
		// leader board display list

		JCheckBox oneTimeRiggedToggle = new JCheckBox("oneTimeRigged");
		oneTimeRiggedToggle.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				oneTimeRigged = !oneTimeRigged;
				writeLog("One time rigged mode was changed to " + oneTimeRigged);
				updateLogDisplay();
				frame2.repaint();
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 9;
		userActions.add(oneTimeRiggedToggle, c);
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.NORTH;
		c.weighty = 0;

		
		
		JList<String> leaderBoard = new JList<String>(tableModelLeaderBoard);
		leaderBoard.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		leaderBoard.setLayoutOrientation(JList.VERTICAL);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0;
		c.ipadx = 40;
		c.gridx = 0;
		c.gridy = 0;
		centerDisplay.add(leaderBoard, c);
		
		
		c.ipady = 0;
		c.ipadx = 0;
		points.setText(selectedName.replace(".txt", "") + " Points: " + Proofreads.getUserPoints(selectedName));

		points.setForeground(Color.red);
		c.ipady = 0;
		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 1;
		centerDisplay.add(points, c);
		c.ipady = 0;
		c.ipadx = 0;

		status.setText("STATUS Online: " + Proofreads.returnStatus());

		status.setForeground(Color.red);
		c.ipady = 0;
		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 2;
		centerDisplay.add(status, c);
		c.ipady = 0;
		c.ipadx = 0;

		String riggedText = "is not rigged";
		if (Proofreads.checkRigged("selectedName")[0]) {
			riggedText = "is rigged and win win next bet " + Proofreads.checkRigged(selectedName)[1];
		}
		riggedStatus.setText("happihound " + riggedText);
		riggedStatus.setForeground(Color.red);
		c.ipady = 0;
		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 3;
		centerDisplay.add(riggedStatus, c);
		c.ipady = 0;
		c.ipadx = 0;

		timeSinceLastMessage.setText(
				selectedName + " time since last message " + String.valueOf(((int) (System.currentTimeMillis() / 1000)
						- Proofreads.returnUserTime(selectedName.replace(".txt", "")))));
		timeSinceLastMessage.setForeground(Color.red);
		c.ipady = 0;
		c.ipadx = 0;
		c.gridx = 0;
		c.gridy = 4;
		centerDisplay.add(timeSinceLastMessage, c);
		c.ipady = 0;
		c.ipadx = 0;

		centerDisplay.setPreferredSize(new Dimension(300, 300));
		logDisplay.add(Jlist2);
		// aligns the log to the top
		// logDisplay.setBorder(new LineBorder(Color.RED));
		frame2.add(logDisplay, BorderLayout.NORTH);
		// aligns the admin actions to the left
		// userActions.setBorder(new LineBorder(Color.BLUE));
		frame2.add(userActions, BorderLayout.LINE_START);

		// logDisplay.setBorder(new LineBorder(Color.GREEN));
		frame2.add(centerDisplay, BorderLayout.CENTER);

		logDisplay.setBackground(Color.black);

		userActions.setBackground(Color.black);

		centerDisplay.setBackground(Color.black);

		frame2.setSize(1000, 900);

		frame2.setResizable(true);

		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		userActions.setPreferredSize(new Dimension(270, 600));

		frame2.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {

			}
		});

		frame2.setVisible(true);
	}

//method to update the scrollable user list to account for user additions 
	public static void updateList() {
		pathnames = f.list();
		listModel.clear();
		for (int i = 0; pathnames.length > i;) {
			listModel.addElement(pathnames[i]);
			i++;
		}
		leaderBoard();
		updateLogDisplay();
	}

//method to update the display log on the admin panel to keep it updated 
	public static void updateLogDisplay() {

		listModel2.clear();
		List<String> newLines = new ArrayList<>();

		try {
			for (String line : Files.readAllLines(Paths.get(Main.userFileLocation + "logFile.txt"),
					StandardCharsets.UTF_8)) {
				newLines.add(line);
			}

		} catch (IOException e1) {
			// error catching here

			e1.printStackTrace();
		}

		for (int i = newLines.size() - 5; (newLines.size()) != i && newLines.size() <= 0; i++) {
			listModel2.addElement(newLines.get(i));

		}

	}

	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D eg = (Graphics2D) g;

		eg.drawString(
				selectedName + " time since last message " + String.valueOf(((int) (System.currentTimeMillis() / 1000)
						- Proofreads.returnUserTime(selectedName.replace(".txt", "")))),
				20, 100);

		// eg.drawString(selectedName +pointsDisplay, 40, 200);

		eg.drawString("uptime=" + String.valueOf((System.currentTimeMillis() - startTime) / 1000),
				frame2.getWidth() - frame2.getWidth() / 2, (int) (frame2.getHeight() - frame2.getHeight() / 3.5));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// error catching here

	}
}
