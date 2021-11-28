import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.cavariux.twitchirc.Chat.Channel;
import com.cavariux.twitchirc.Chat.User;
import com.cavariux.twitchirc.Core.TwitchBot;

public class Proofreads extends TwitchBot {
	
	static String[] pathnames;
	static Random rand = new Random();
	static File f = new File(Main.userFileLocation);
	static boolean runCommands = false;
	static long coolDownTime = 60;

	public Proofreads() {

		//this.setUsername("Proofreads");
		//this.setOauth_Key("oauth:1bpltca4gs5as6dvu47ycz24b9kvqk");
	//	this.setClientID("5dngewtl176az4k5p7siw8uwy3knz7");
		this.setUsername("happihound");
		this.setOauth_Key("oauth:ohuzhxptzl6qamatuewdeglklmqav8");
		this.setClientID("evcpyezzzpgj7855xtekv936yk04a1");

	}

	public void onMessage(User user, Channel channel, String message) {
		String userName = user.toString().toLowerCase();
		pathnames = f.list();

		if (!userName.equalsIgnoreCase("proofreads") && !userName.equalsIgnoreCase("nightbot")) {

			if (!userExists(userName)) {
				makeUserFile(userName);

			} else {
				int points = getUserPoints(userName);
				points++;
				setUserPoints(userName, points);

			}

		}

	}

	public void onCommand(User user, Channel channel, String command) {
		Main.updateLogDisplay();
		@SuppressWarnings("deprecation")
		String userName = user.toString().toLowerCase();
		pathnames = f.list();

		if (!userName.equalsIgnoreCase("proofreads") && !userName.equalsIgnoreCase("nightbot")) {

			if (!userExists(userName)) {
				makeUserFile(userName);
			}
		}

		boolean userIsAdmin = false;
		if (Arrays.asList(Main.admins).contains(userName)) {
			userIsAdmin = true;
		}
		int tempTimeInt = returnUserTime(userName);
		tempTimeInt = (int) (System.currentTimeMillis() / 1000) - tempTimeInt;
		if (tempTimeInt < coolDownTime && (!userIsAdmin)) {
			return;
		} else if (tempTimeInt > coolDownTime && (!userIsAdmin)) {
			setUserTime(userName);
		}

		String[] commandString = command.split(" ");

		if (commandString[0].equalsIgnoreCase("on") && userIsAdmin) {
			on(userName, channel);
		}

		else if (commandString[0].equalsIgnoreCase("off") && userIsAdmin) {
			off(userName, channel);
		}

		else if (commandString[0].equalsIgnoreCase("forcestop") && userIsAdmin) {
			forcestop();
		}

		if (runCommands) {

			if (commandString[0].equalsIgnoreCase("top") && commandString.length == 1) {
				top(channel, userName);
			}

			else if (commandString[0].equalsIgnoreCase("editpoints") && commandString.length == 3 && userIsAdmin) {
				if (commandString[2].length() > 9) {
					this.sendMessage("The number you entered is too long " + userName, channel);
					return;
				}
				editpoints(commandString, channel, userName);
			}

			else if (commandString[0].equalsIgnoreCase("gamble") && commandString.length == 2) {
				if (commandString[1].length() > 9) {
					this.sendMessage("The number you entered is too long " + userName, channel);
					return;
				}
				gamble(commandString, channel, userName);
			}

			else if (commandString[0].equalsIgnoreCase("points")
					&& (commandString.length == 1 || commandString.length == 2)) {
				points(commandString, channel, userName);
			}

			else if (commandString[0].equalsIgnoreCase("cooldown") && (commandString.length == 2)) {
				if (commandString[1].length() > 9) {
					this.sendMessage("The number you entered is too long " + userName, channel);
					return;
				}
				cooldown(commandString, userName, channel);
			}

			else if (commandString[0].equalsIgnoreCase("give") && commandString.length == 3) {
				if (commandString[2].length() > 9) {
					this.sendMessage("The number you entered is too long " + userName, channel);
					return;
				}
				give(commandString, userName, channel);

			}

		}

		else if (runCommands == false) {
			if (command.contains("points") || command.contains("gamble") || command.contains("off")
					|| command.contains("on") || command.contains("top") || command.contains("editpoints")
					|| command.contains("cooldown")) {
				Main.writeLog(userName + " tried to run a command, but the bot was turned off");
				this.sendMessage("The bot is offline and commands are disabled", channel);
			}
		}
		Main.updateLogDisplay();
	}

	public void on(String user, Channel channel) {
		this.sendMessage("Operator " + user + " changed gamble status to online", channel);
		runCommands = true;
		Main.writeLog("Status was changed to on by " + user);
	}

	public void off(String user, Channel channel) {
		runCommands = false;
		this.sendMessage("Operator " + user + " changed gamble status to offline", channel);
		Main.writeLog("Status was changed to off by " + user);
	}

	public void forcestop() {
		System.exit(0);
	}

	public void top(Channel channel, String user) {
		Main.leaderBoard();
		this.sendMessage("LeaderBoard " + (Arrays.toString(Main.leaderBoard)), channel);
		Main.writeLog("Leaderboard was requested by " + user);
	}

	public void editpoints(String[] commandString, Channel channel, String user) {

		String editedUser = commandString[1].replaceAll("@", "");
		if (!commandString[2].matches("[0-9]*")) {
			this.sendMessage("entered point value was invalid", channel);
			return;
		}

		int points = getUserPoints(editedUser);
		int editedPoints = Integer.parseInt(commandString[2].replaceAll("[^0-9]*", ""));
		int statusCheck = setUserPoints(editedUser, editedPoints);

		if (statusCheck == -1) {
			this.sendMessage("a read/write error occured", channel);
			Main.writeLog("a read/write error occured when trying to edit points");
			return;
		}

		else if (statusCheck == -2) {
			this.sendMessage(commandString[1] + " doesn't exist", channel);
			Main.writeLog("user " + user + " did not exists");
			return;
		}

		this.sendMessage("Operator " + user + " changed points from " + points + " to " + commandString[2] + " on user "
				+ editedUser, channel);
		Main.writeLog("Operator " + user + " changed points from " + points + " to " + commandString[2] + " on user "
				+ editedUser);

	}

	public void gamble(String[] commandString, Channel channel, String user) {
		boolean outcome = false;

		int randomNumber = rand.nextInt(2);

		int points = getUserPoints(user);

		if (randomNumber == 0) {
			outcome = true;
		}

		int pointsGambled = Math(commandString[1], user);

		if (pointsGambled == -1) {
			this.sendMessage("Invalid points entered", channel);
			return;
		}

		if (pointsGambled == -2) {
			this.sendMessage(user + " is too poor to gamble with " + commandString[1], channel);
			return;
		}

		if (pointsGambled > points) {
			this.sendMessage(user + " is too poor to gamble with " + pointsGambled + " points", channel);
			Main.writeLog(user + " tried to gamble with " + pointsGambled + " but was too poor");
			return;
		}

		if (pointsGambled < 5) {
			this.sendMessage("Minimum gamble is 5 " + user, channel);
			return;
		}

		if (pointsGambled >= 15000) {
			randomNumber = rand.nextInt(10);
			if (randomNumber >= 3) {
				outcome = false;
			}
		}

		if (checkRigged(user)[0]) {
			outcome = checkRigged(user)[1];
			Main.writeLog(outcome ? user + " won a rigged bet" : user + " lost a rigged bet");
		}

		if (outcome) {
			points = points + pointsGambled;
			this.sendMessage(user + " Went in with " + pointsGambled + " and won " + pointsGambled + ". " + user
					+ " now has " + points + " points", channel);
			Main.writeLog(user + " gambled with " + pointsGambled + " and won " + pointsGambled);
			setUserPoints(user, points);
			return;
		}

		if (!outcome) {
			points = points - pointsGambled;
			this.sendMessage(user + " Went in with " + pointsGambled + " and lost " + pointsGambled + ". " + user
					+ " now has " + points + " points", channel);
			Main.writeLog(user + " gambled with " + pointsGambled + " and lost " + pointsGambled);
			setUserPoints(user, points);
			return;

		}

	}

	public void points(String[] commandString, Channel channel, String user) {

		String selectedUser = "";
		if (commandString.length == 2) {
			selectedUser = commandString[1].replaceAll("@", "");
			;
		}

		else if (commandString.length == 1) {
			selectedUser = user;
		}

		int points = getUserPoints(selectedUser);

		if (points == -1) {
			this.sendMessage("unhandled error getting points", channel);
			Main.writeLog(selectedUser + " requested their points, but an error occurred");
		}

		else if (points == -2) {
			this.sendMessage(selectedUser + " does not exist", channel);
			Main.writeLog(selectedUser + " requested their points, but didn't exsist");
		}

		else if (points >= 0) {
			this.sendMessage(selectedUser + " has " + points + " points", channel);
			Main.writeLog(selectedUser + " requested their point count. They have " + points + " points");
		}
	}

	public void cooldown(String[] commandString, String user, Channel channel) {
		commandString[1] = commandString[1].replaceAll("[^0-9]*", "");
		if (commandString[1].equals("")) {
			return;
		}
		coolDownTime = Integer.parseInt(commandString[1]);
		this.sendMessage(user + " changed cooldown to " + commandString[1], channel);
		Main.writeLog(user + " changed cooldown to " + commandString[1]);
	}

	public void give(String[] commandString, String user, Channel channel) {

		int pointsSent = Math(commandString[2], user);

		if (pointsSent == -1) {
			this.sendMessage("An error occured", channel);
			return;
		}

		if (pointsSent == -2) {
			this.sendMessage(user + " is too poor to send " + commandString[2] + " points", channel);
			return;
		}

		String sentUser = user;

		String givenUser = commandString[1].replaceAll("@", "");

		if (givenUser.equals(sentUser)) {
			this.sendMessage("You cannot give yourself points", channel);
			return;
		}

		System.out.println(pointsSent);
		if (pointsSent <= 0) {
			this.sendMessage("entered point value was invalid", channel);
			return;
		}

		int sentUserPoints = getUserPoints(sentUser);

		if (sentUserPoints == -1) {
			this.sendMessage("a read/write error occured", channel);
			Main.writeLog("a read/write error occured when trying to edit points");
			return;
		}

		else if (sentUserPoints == -2) {
			this.sendMessage(commandString[1] + " doesn't exist", channel);
			Main.writeLog("user " + user + " did not exists");
			return;
		}

		int givenUserPoints = getUserPoints(givenUser);

		if (givenUserPoints == -1) {
			this.sendMessage("a read/write error occured", channel);
			Main.writeLog("a read/write error occured when trying to edit points");
			return;
		}

		else if (givenUserPoints == -2) {
			this.sendMessage(commandString[1] + " doesn't exist", channel);
			Main.writeLog("user " + user + " did not exists");
			return;
		}

		if (pointsSent > sentUserPoints) {
			this.sendMessage(sentUser + " is too poor to send " + givenUser + " " + pointsSent, channel);
			Main.writeLog(sentUser + " tried to send points to  " + givenUser + " but was too poor");
			return;
		}

		setUserPoints(sentUser, sentUserPoints - pointsSent);
		givenUserPoints = getUserPoints(givenUser);
		setUserPoints(givenUser, givenUserPoints + pointsSent);

		this.sendMessage(sentUser + " gave " + givenUser + " " + pointsSent + " points", channel);
	}

	public static void makeUserFile(String user) {
		user = user.replaceAll(".txt", "");
		Main.writeLog("made new user " + user);
		try {
			FileOutputStream fos = new FileOutputStream(Main.userFileLocation + user + ".txt", false);
			String str = user + "\n" + "points=1" + "\n" + "lastTime=" + (int) (System.currentTimeMillis() / 1000);
			byte[] b = str.getBytes(); // converts string into bytes
			fos.write(b); // writes bytes into file
			fos.close(); // close the file
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int returnUserTime(String user) {
		user = user.replaceAll(".txt", "");
		int lastTime = 0;
		try {
			for (String line : Files.readAllLines(Paths.get(Main.userFileLocation + user + ".txt"),
					StandardCharsets.UTF_8)) {
				if (line.contains("lastTime=")) {
					lastTime = Integer.parseInt(line.replaceAll("[^0-9]*", ""));
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block

			e1.printStackTrace();
		}

		return lastTime;

	}

	public static void setUserTime(String user) {
		user = user.replaceAll(".txt", "");
		List<String> newLines = new ArrayList<>();
		try {
			for (String line : Files.readAllLines(Paths.get(Main.userFileLocation + user + ".txt"),
					StandardCharsets.UTF_8)) {
				if (line.contains("lastTime=")) {
					newLines.add("lastTime=" + ((int) (System.currentTimeMillis() / 1000)));

				}

				else {
					newLines.add(line);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Files.write(Paths.get(Main.userFileLocation + user + ".txt"), newLines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static boolean[] checkRigged(String user) {
		user = user.replaceAll(".txt", "");
		boolean outcome[] = { false, false };
		for (int i = 0; Main.riggedList.size() != i && Main.riggedList.size() != 0; i++) {
			if ((Main.riggedList.get(i).getName()).replace(".txt", "").equalsIgnoreCase(user)) {
				outcome[0] = true;
				if (Main.riggedList.get(i).getRigged()) {
					outcome[1] = true;

				}

				else if (!Main.riggedList.get(i).getRigged()) {
					outcome[1] = false;
					Main.writeLog(user + " lost a rigged bet");
				}

			}
		}

		if (Main.oneTimeRigged && new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName() == "gamble") {
			for (int i = 0; Main.riggedList.size() != i && Main.riggedList.size() != 0; i++) {
				if ((Main.riggedList.get(i).getName()).replace(".txt", "").equalsIgnoreCase(user)) {
					Main.writeLog("one time rigged was used on " + user);
					Main.riggedList.remove(i);
					return outcome;
				}
			}
		}
		return outcome;
	}

	public static int Math(String commandString, String user) {
		int pointsGambled;
		user = user.replaceAll(".txt", "");
		int userPoints = getUserPoints(user);

		if (userPoints == -1) {
			Main.writeLog("error getting user points");
			return -1;
		}

		if (userPoints == -2) {
			Main.writeLog("no points error");
			return -1;
		}

		if (commandString.equalsIgnoreCase("all")) {
			pointsGambled = userPoints;
			return pointsGambled;
		}

		if (!commandString.replaceAll("%", "").matches("[0-9].*")) {
			return -1;
		}

		if (commandString.replaceAll("[^0-9].*", "").equals("")) {
			return -1;
		}

		else if (commandString.contains("%")) {
			if (!commandString.replaceFirst("%", "").matches("[0-9]*")) {
				return -1;
			}

			commandString = commandString.replaceAll("%", "");
			commandString = "" + Integer.parseInt(commandString.replaceAll("[^0-9].*", ""));

			if (Math.round(Float.parseFloat(commandString)) > 100) {
				return -2;
			}

			pointsGambled = (int) ((userPoints * Math.round(Float.parseFloat(commandString)) / 100));
			return pointsGambled;
		}

		if (commandString.replaceAll("[^0-9].*", "").equals("")) {
			return -1;
		}
		pointsGambled = Integer.parseInt(commandString.replaceAll("[^0-9]*", ""));
		return pointsGambled;
	}

	public static int getUserPoints(String user) {
		user = user.replaceAll("@", "");
		user = user.replaceAll(".txt", "");
		if (!userExists(user)) {
			return -2;
		}

		try {
			for (String line : Files.readAllLines(Paths.get(Main.userFileLocation + user + ".txt"),
					StandardCharsets.UTF_8)) {
				if (line.contains("points=")) {
					if (!(Integer.parseInt(line.replaceAll("[^0-9]*", "")) >= 0)) {
						return -1;
					}

					return Integer.parseInt(line.replaceAll("[^0-9]*", ""));

				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block

			e1.printStackTrace();
		}
		return -1;

	}

	public static int setUserPoints(String user, int points) {
		user = user.replaceAll(".txt", "");
		if (!userExists(user)) {
			return -2;
		}

		user = user.replaceAll(".txt", "");

		List<String> newLines = new ArrayList<>();
		try {
			for (String line : Files.readAllLines(Paths.get(Main.userFileLocation + user + ".txt"),
					StandardCharsets.UTF_8)) {
				if (line.contains("points=")) {
					newLines.add("points=" + points);

				}

				else {
					newLines.add(line);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		try {
			Files.write(Paths.get(Main.userFileLocation + user + ".txt"), newLines, StandardCharsets.UTF_8);
			return 1;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	public static boolean userExists(String user) {
		pathnames = f.list();
		user = user.replaceAll(".txt", "");
		if (Arrays.asList(pathnames).contains(user.toLowerCase() + ".txt")) {
			return true;
		}
		return false;
	}

	public static boolean returnStatus() {

		return runCommands;
	}

	public static void toggleRunning() {
		runCommands = !runCommands;
	}

}
