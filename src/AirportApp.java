import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

public class AirportApp {
	
	public static boolean isRunning = true;
	public static GraphInterface<String> airportData = new DirectedGraph<>();
	public static Scanner kb = new Scanner(System.in);
	public static String userInput;
	public static Map<String, String> airportText = new HashMap<>();
	
	
	public static void main(String[] args) {
		System.out.println("Airports by M. Ngoi");
		System.out.println();
		
		// Setting up graph
		try {
			setUpVertices();
			setUpEdges();
		} catch(IOException e) {
			System.out.println("Error: Files not found.");
			System.exit(0);
		}	
		
		while(isRunning) {
			System.out.print("Command? ");
			userInput = kb.nextLine().toUpperCase();
			options(userInput);
		}
	}
	
	public static void setUpVertices() throws IOException {
		File airportList = new File("airports.csv");
		Scanner readFile = new Scanner(airportList);
		String line;
		while(readFile.hasNext()) {
			line = readFile.nextLine();
			String[] splitArray = line.split(",");
			airportData.addVertex(splitArray[0]);
			airportText.put(splitArray[0], splitArray[1]);
		}
		readFile.close();
	}
	
	public static void setUpEdges() throws IOException {
		File distanceList = new File("distances.csv");
		Scanner readFile = new Scanner(distanceList);
		String line;
		while(readFile.hasNext()) {
			line = readFile.nextLine();
			String[] splitArray = line.split(",");
			airportData.addEdge(splitArray[0], splitArray[1], Double.parseDouble(splitArray[2]));
		}
		readFile.close();
	}
	
	public static void options(String userInput) {
		switch(userInput) {
		case "Q":
			airportInfo();
			break;
		case "D":
			minDistance();
			break;
		case "I":
			insertConnection();
			break;
		case "R":
			removeConnection();
			break;
		case "H":
			menu();
			break;
		case "E":
			isRunning = false;
			System.exit(0);
			break;
		default:
			System.out.println("Enter an appropriate character.");
		}
	}
	
	public static void airportInfo() {
		System.out.print("Airport code: ");
		userInput = kb.nextLine().toUpperCase();
		if(!airportText.containsKey(userInput))
			System.out.println("Error: Airport could not be found.");
		else
			System.out.println(airportText.get(userInput));
	}
	
	// Uses a Dijkstra's algorithm to output the cheapest path from one airport to another
	public static void minDistance() {
		StackInterface<String> path = new ArrayStack<>();
		System.out.print("Airport codes: ");
		userInput = kb.nextLine();
		String[] splitArray = userInput.split(" ");
		try {
			double result = airportData.getCheapestPath(splitArray[0], splitArray[1], path);
			if(result == 0) {
				System.out.println("Error: There are no possible routes.");
				
			} else {
				System.out.println("The minimum distance between " + airportText.get(splitArray[0]) + " and " + airportText.get(splitArray[1]) + " is " + result + " through the route: ");
				while(!path.isEmpty()) {
					System.out.print(path.pop() + " ");
				}
				System.out.println();
			}
			
		} catch(Exception e) {
			System.out.println("Error: Airports could not be found.");
		}
		
	}
	
	public static void insertConnection() {
		System.out.print("Airport codes and distance: ");
		userInput = kb.nextLine();
		String[] splitArray = userInput.split(" ");
		boolean result = airportData.addEdge(splitArray[0], splitArray[1], Double.parseDouble(splitArray[2]));
		if(result)
			System.out.println("You have inserted a connection from " + airportText.get(splitArray[0]) + " to " + airportText.get(splitArray[1]) + " with a distance of " + splitArray[2] + ".");
		else
			System.out.println("Error: Airports could not be found.");
	}
	
	public static void removeConnection() {
		System.out.print("Airport codes: ");
		userInput = kb.nextLine();
		String[] splitArray = userInput.split(" ");
		boolean result = airportData.removeEdge(splitArray[0], splitArray[1]);
		if(result)
			System.out.println("The connection from " + airportText.get(splitArray[0]) + " to " + airportText.get(splitArray[1]) + " removed.");
		else
			System.out.println("Error: Connection could not be found.");
	}
	
	public static void menu() {
		System.out.println("Q Query the airport information by entering the airport code.");
		System.out.println("D Find the minimum distance between two airports.");
		System.out.println("I Insert a connection by entering two airport codes and distance.");
		System.out.println("R Remove an existing connection by entering two airport codes.");
		System.out.println("H Display this message.");
		System.out.println("E Exit.");
	}
	
}
