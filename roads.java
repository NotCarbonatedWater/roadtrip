// Jonathan Jaco ID: 20516017 // 
// -------------------------------------------------------------------------------------------------------
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
// -------------------------------------------------------------------------------------------------------
public class roads {
    private static List<String> route(String starting_city, String ending_city, List<String> attractions)
            throws IOException {
        Graph graph = new Graph();
        Scanner inputRoads = new Scanner(new File("roads.csv"));
        Scanner inputAttractions = new Scanner(new File("attractions.csv"));

        while (inputRoads.hasNextLine()) {
            String tmpLine = inputRoads.nextLine();
            String[] tmpLSpl = tmpLine.split(","); // temporray comma split string
            float dist = Float.parseFloat(tmpLSpl[2]); 
            float time = Float.parseFloat(tmpLSpl[3]); 
            float doMath = dist / time; // calculates route efficency 
            graph.addEdge(tmpLSpl[0], tmpLSpl[1], doMath);
        }
        while (inputAttractions.hasNextLine()) {
            String tmpLine = inputAttractions.nextLine();
            String[] tmpLSpl = tmpLine.split(","); // temporray comma split string
            // Add keys and values (Attraction, Location)
            graph.addPlace(tmpLSpl[1], tmpLSpl[0]);
        }
        // adds road trips stops
        for (int i = 0; i < attractions.size(); i++) {
            graph.addStop(attractions.get(i));
        }

        // find and return the route
        LinkedList<String> list = graph.route(starting_city, ending_city);
        return list;
    }
    // -------------------------------------------------------------------------------------------------------
    public static void main(String[] args) throws IOException {
        // set up start and end of trip // 
        Scanner userInput = new Scanner(System.in);
        System.out.print("Starting City Name: ");
        String starting_city = userInput.nextLine();
        System.out.print("Ending City Name: ");
        String ending_city = userInput.nextLine();



        // attarction input from user // 
        LinkedList<String> attractions = new LinkedList<String>();
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("Enter Attraction (type 'done' if none or done adding): ");
            String attractionInput = userInput.nextLine();
            // adds attraction if given // 
            if (attractionInput.equals("Done") || attractionInput.equals("done")) {
                isRunning = false; // stops adding attractions 
            } 
            else {
                attractions.add(attractionInput); // adds attraction
            }
        }
        userInput.close();

        // makes route //
        List<String> roadTripMap = route(starting_city, ending_city, attractions);

        // map route printer //
        System.out.println("\nRoad Trip Map:\n");
        for (int i = 0; i < roadTripMap.size(); i++) {
            System.out.println((i + 1) + ". " + roadTripMap.get(i));
        }
    }
}
