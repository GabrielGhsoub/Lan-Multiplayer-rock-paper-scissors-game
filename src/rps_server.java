import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
// arraylist
import java.util.ArrayList;

public class rps_server {
    /**
     * The defautl port number
     * 
     * @var integer
     *      adssad
     */
    private static Integer port = 1337;

    /**
     * The script version number
     * 
     * @var integer
     * 
     */
    private static Double versionNumber = 1.0;

    /**
     * The welcome boiler plate
     * 
     * @var string
     * 
     */
    private static String welcomeMsg = "--- Welcome to Paper Scissors Stone Server V. " + versionNumber + " --- \n";

    /**
     * Function takes an integer x as an input and returns the boolean value
     * true if the input is strictly greater than 0 and less than or equal to
     * 65535.
     * 
     * @param integer
     *                x
     * @return boolean
     */
    private static boolean validPort(Integer x) {
        return x >= 1 && x <= 65535 ? true : false;
    }

    /**
     * Function prompts the user to choose a specific port number or to press
     * enter in order to continue with default setting (Server.port).
     * 
     * The returned integer strictly greater than 0 and less than or equal to
     * 65535.
     * 
     * @return integer
     */
    private static int getPort() {

        Integer input;

        Scanner sc = new Scanner(System.in);

        do {
            System.out.print("Please select a port by entering an integer value between 1 and 65535 or\n");
            System.out
                    .print("insert \"0\" in order to continue with the default setting (" + rps_server.port + "): ");
            input = sc.nextInt();

        } while (input != 0 && !rps_server.validPort(input));

        sc.close();

        return input == 0 ? rps_server.port : input;
    }

    public static void main(String args[]) throws Exception {

        // Array of sockets
        ArrayList<Socket> sockets = new ArrayList<Socket>();

        // Array of names
        ArrayList<String> names = new ArrayList<String>();

        // Array of inputs
        // ArrayList<String> inputs = new ArrayList<String>();

        // Print welcome msg
        System.out.println(rps_server.welcomeMsg);

        // Set port
        rps_server.port = rps_server.getPort();

        // Create new server socket & dump out a status msg
        ServerSocket welcomeSocket = new ServerSocket(rps_server.port);
        System.out.println("\nOk, we're up and running on port " + welcomeSocket.getLocalPort() + " ...");

        while (!welcomeSocket.isClosed()) {

            // Create socket for new player
            Socket connectionSocket = welcomeSocket.accept();

            // if socket not null

            // Read input from player
            BufferedReader inClient = new BufferedReader(
                    new InputStreamReader(connectionSocket.getInputStream()));

            // read from inclient
            String response = inClient.readLine();

            // split response with -
            String[] responseArray = response.split("--");

            // if responseArray[1] == "name"
            if (responseArray[1].equals("name")) {
                String name = responseArray[0];

                // add socket to array of sockets at the end
                sockets.add(connectionSocket);
                names.add(name);

                // response = inClient.readLine();
                // responseArray = response.split("--");

            }

            if (responseArray[1].equals("players")) {

                try {
                    // get name of player
                    String name = responseArray[0];

                    System.out.println("Player " + name + " wants to see the list of players");

                    // search in array of sockets for socket of player
                    int index = names.indexOf(name);
                    Socket playerSocket = sockets.get(index);

                    System.out.println("Player " + name + " is at index " + index);
                    System.out.println("Player " + name + " has socket " + playerSocket);

                    // create output stream for player
                    DataOutputStream outToClient = new DataOutputStream(playerSocket.getOutputStream());

                    // create string with all names
                    String allNames = "";
                    for (String n : names) {
                        allNames += n + "--";
                    }

                    // send all names to player
                    outToClient.writeBytes(allNames + "\n");
                } catch (Exception e) {
                    System.out.println("Player not connected anymore!");
                }
            }

            // print the arrays
            for (int j = 0; j < sockets.size(); j++) {
                System.out.println("Socket: " + sockets.get(j));
                System.out.println("Name: " + names.get(j));

            }
            System.out.println("--------------------------------------------------");

            // every 30 seconds send ping byte to clients to check availability
            Runnable r = new Runnable() {
                public void run() {

                    // for each socket if not connected anymore remove from array
                    for (int j = 0; j < sockets.size(); j++) {
                        // send ping to socket to check if still connected
                        if (sockets.get(j) != null) {
                            try {
                                sockets.get(j).sendUrgentData(0xFF);
                            } catch (Exception e) {
                                System.out.println("Socket " + sockets.get(j) + " is not connected anymore");
                                // if not connected anymore remove from array
                                sockets.remove(j);
                                names.remove(j);
                                // inputs.remove(j);
                            }
                        }
                    }
                }
            };
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(r, 0, 30, TimeUnit.SECONDS);
        }

        // while (!welcomeSocket.isClosed()) {

        // // Player one
        // Socket client_1 = welcomeSocket.accept();
        // if (client_1.isConnected()) {
        // System.out.println("\nPlayer one (" +
        // (client_1.getLocalAddress().toString()).substring(1) + ":"
        // + client_1.getLocalPort() + ") has joined ... waiting for player two ...");
        // }
        // DataOutputStream outClient_1 = new
        // DataOutputStream(client_1.getOutputStream());
        // BufferedReader inClient_1 = new BufferedReader(new
        // InputStreamReader(client_1.getInputStream()));

        // // Player two
        // Socket client_2 = welcomeSocket.accept();
        // if (client_2.isConnected()) {
        // System.out.println("Player two (" +
        // (client_2.getLocalAddress().toString()).substring(1) + ":"
        // + client_1.getLocalPort() + ") has joined ... lets start ...");
        // }
        // DataOutputStream outClient_2 = new
        // DataOutputStream(client_2.getOutputStream());
        // BufferedReader inClient_2 = new BufferedReader(new
        // InputStreamReader(client_2.getInputStream()));

        // // Get client inputs
        // inputClient_1 = inClient_1.readLine();
        // inputClient_2 = inClient_2.readLine();

        // /**
        // * If the characters received from C1 and C2 are the same then the
        // * server sends back to both clients the string "DRAW".
        // */
        // if (inputClient_1.equals(inputClient_2)) {
        // resClient_1 = "Draw";
        // resClient_2 = "Draw";
        // System.out.println("It's a draw.");
        // }
        // /**
        // * If the server receives ’R’ from C1 and ’S’ from C2 it sends the
        // * string "YOU WIN" to C1 and the string "YOU LOSE" to C2.
        // */
        // else if (inputClient_1.equals("R") && inputClient_2.equals("S")) {
        // resClient_1 = "You win";
        // resClient_2 = "You lose";
        // System.out.println("Player one wins.");

        // }
        // /**
        // * If the server receives ’S’ from C1 and ’R’ from C2 it sends the
        // * string "YOU LOSE" to C1 and the string "YOU WIN" to C2.
        // */
        // else if (inputClient_1.equals("S") && inputClient_2.equals("R")) {
        // resClient_1 = "You lose";
        // resClient_2 = "You win";
        // System.out.println("Player two wins.");
        // }
        // /**
        // * If the server receives ’R’ from C1 and ’P’ from C2 it sends the
        // * string "YOU LOSE" to C1 and the string "YOU WIN" to C2.
        // */
        // else if (inputClient_1.equals("R") && inputClient_2.equals("P")) {
        // resClient_1 = "You lose";
        // resClient_2 = "You win";
        // System.out.println("Player two wins.");
        // }
        // /**
        // * If the server receives ’P’ from C1 and ’R’ from C2 it sends the
        // * string "YOU WIN" to C1 and the string "YOU LOSE" to C2.
        // */
        // else if (inputClient_1.equals("P") && inputClient_2.equals("R")) {
        // resClient_1 = "You win";
        // resClient_2 = "You lose";
        // System.out.println("Player one wins.");
        // }
        // /**
        // * If the server receives ’S’ from C1 and ’P’ from C2 it sends the
        // * string "YOU WIN" to C1 and the string "YOU LOSE" to C2.
        // */
        // else if (inputClient_1.equals("S") && inputClient_2.equals("P")) {
        // resClient_1 = "You win";
        // resClient_2 = "You lose";
        // System.out.println("Player one wins.");
        // }
        // /**
        // * If the server receives ’P’ from C1 and ’S’ from C2 it sends the
        // * string "YOU LOSE" to C1 and the string "YOU WIN" to C2.
        // */
        // else if (inputClient_1.equals("P") && inputClient_2.equals("S")) {
        // resClient_1 = "You lose";
        // resClient_2 = "You win";
        // System.out.println("Player two wins.");
        // }

        // // Send responses in uppercase and close sockets
        // outClient_1.writeBytes(resClient_1.toUpperCase());
        // outClient_2.writeBytes(resClient_2.toUpperCase());
        // client_1.close();
        // client_2.close();

        // System.out.println("\nWaiting for new players ...\n");

        // }
    }
}
