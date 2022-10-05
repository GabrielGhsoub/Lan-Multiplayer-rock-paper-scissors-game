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
        ArrayList<String> inputs = new ArrayList<String>();

        // Multidimensional array linking names to scores
        ArrayList<ArrayList<String>> scores = new ArrayList<ArrayList<String>>();

        // Multidimensional array linking names to input
        ArrayList<ArrayList<String>> nameInput = new ArrayList<ArrayList<String>>();

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
            try {
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

                    // if name exists
                    if (names.contains(name)) {
                        System.out.println("Name already exists");
                        // send error msg
                        DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                        outToClient.writeBytes("false" + "\n");
                    } else {
                        System.out.println("New player: " + name);
                        // add name to names array
                        names.add(name);

                        // add socket to sockets array
                        sockets.add(connectionSocket);

                        // search in array of sockets for socket of player
                        int index = names.indexOf(name);
                        Socket playerSocket = sockets.get(index);

                        // send success msg
                        DataOutputStream outToClient = new DataOutputStream(playerSocket.getOutputStream());
                        outToClient.writeBytes("name accepted" + "\n");
                    }
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

                if (responseArray[1].equals("play")) {

                    String resClient_1 = "";
                    String resClient_2 = "";

                    try {
                        // get name of player
                        String name = responseArray[0];

                        System.out.println("Player " + name + " wants to play");

                        // search in array of sockets for socket of player
                        int index = names.indexOf(name);

                        Socket playerSocket = sockets.get(index);

                        System.out.println("PlayerSocket " + playerSocket + " wants to play");

                        // Save input of player
                        inputs.add(responseArray[2]);

                        // Save link between name and input
                        ArrayList<String> nameInputPair = new ArrayList<String>();
                        nameInputPair.add(name);
                        nameInputPair.add(responseArray[2]);
                        nameInput.add(nameInputPair);

                        // count all players with input
                        int count = 0;
                        for (ArrayList<String> pair : nameInput) {
                            if (pair.get(1) != null) {
                                count++;
                            }
                        }

                        // if socket size is 1
                        if (sockets.size() == 1 || count == 1) {
                            System.out.println("Please wait for another player to join and try again!");

                            // create output stream for player
                            DataOutputStream outToClient = new DataOutputStream(playerSocket.getOutputStream());

                            // send message to player
                            outToClient.writeBytes(
                                    "First Player"
                                            + "\n");

                        } else {
                            System.out.println("There are enough players to play!");
                            // Choose random player from array of sockets not including player or player 1
                            // do while input of random player is different than null
                            int randomIndex = 0;
                            String randomInput = "";
                            do {
                                randomIndex = (int) (Math.random() * sockets.size());

                                // get input of random player based on nameimput arraylist
                                for (ArrayList<String> pair : nameInput) {
                                    if (pair.get(0).equals(names.get(randomIndex))) {
                                        randomInput = pair.get(1);
                                    }
                                }

                            } while (randomIndex == index || randomInput == null);

                            // get input of current player based on nameimput arraylist
                            String currentInput = responseArray[2];

                            System.out
                                    .println("Random socket is " + sockets.get(randomIndex) + "Its input is" +
                                            randomInput);
                            // current socket
                            System.out.println("Current socket is " + playerSocket + "Its input is" +
                                    currentInput);

                            /**
                             * If the characters received from C1 and C2 are the same then the
                             * server sends back to both clients the string "DRAW".
                             */
                            if (currentInput.equals(randomInput)) {
                                resClient_1 = "Draw with " + names.get(randomIndex);
                                resClient_2 = "Draw with " + name;
                                System.out.println("It's a draw.");

                                // save scores
                                ArrayList<String> scorePair = new ArrayList<String>();
                                scorePair.add(name);
                                scorePair.add("draw");

                                ArrayList<String> scorePair2 = new ArrayList<String>();
                                scorePair2.add(names.get(randomIndex));
                                scorePair2.add("draw");

                                scores.add(scorePair);
                                scores.add(scorePair2);
                            }
                            /**
                             * If the server receives ’R’ from C1 and ’S’ from C2 it sends the
                             * string "YOU WIN" to C1 and the string "YOU LOSE" to C2.
                             */
                            else if (currentInput.equals("R") && randomInput.equals("S")) {
                                resClient_1 = "You win against " + names.get(randomIndex);
                                resClient_2 = "You lose against " + name;
                                System.out.println("Player one wins.");

                                // save scores
                                ArrayList<String> scorePair = new ArrayList<String>();
                                scorePair.add(name);
                                scorePair.add("win");

                                ArrayList<String> scorePair2 = new ArrayList<String>();
                                scorePair2.add(names.get(randomIndex));
                                scorePair2.add("lose");

                                scores.add(scorePair);
                                scores.add(scorePair2);

                            }
                            /**
                             * If the server receives ’S’ from C1 and ’R’ from C2 it sends the
                             * string "YOU LOSE" to C1 and the string "YOU WIN" to C2.
                             */
                            else if (currentInput.equals("S") && randomInput.equals("R")) {
                                resClient_1 = "You lose against " + names.get(randomIndex);
                                resClient_2 = "You win against " + name;
                                System.out.println("Player two wins.");

                                // save scores
                                ArrayList<String> scorePair = new ArrayList<String>();
                                scorePair.add(name);
                                scorePair.add("lose");

                                ArrayList<String> scorePair2 = new ArrayList<String>();
                                scorePair2.add(names.get(randomIndex));
                                scorePair2.add("win");

                                scores.add(scorePair);
                                scores.add(scorePair2);
                            }
                            /**
                             * If the server receives ’R’ from C1 and ’P’ from C2 it sends the
                             * string "YOU LOSE" to C1 and the string "YOU WIN" to C2.
                             */
                            else if (currentInput.equals("R") && randomInput.equals("P")) {
                                resClient_1 = "You lose against " + names.get(randomIndex);
                                resClient_2 = "You win against " + name;
                                System.out.println("Player two wins.");

                                // save scores
                                ArrayList<String> scorePair = new ArrayList<String>();
                                scorePair.add(name);
                                scorePair.add("lose");

                                ArrayList<String> scorePair2 = new ArrayList<String>();
                                scorePair2.add(names.get(randomIndex));
                                scorePair2.add("win");

                                scores.add(scorePair);
                                scores.add(scorePair2);
                            }
                            /**
                             * If the server receives ’P’ from C1 and ’R’ from C2 it sends the
                             * string "YOU WIN" to C1 and the string "YOU LOSE" to C2.
                             */
                            else if (currentInput.equals("P") && randomInput.equals("R")) {
                                resClient_1 = "You win against " + names.get(randomIndex);
                                resClient_2 = "You lose against " + name;
                                System.out.println("Player one wins.");

                                // save scores
                                ArrayList<String> scorePair = new ArrayList<String>();
                                scorePair.add(name);
                                scorePair.add("win");

                                ArrayList<String> scorePair2 = new ArrayList<String>();
                                scorePair2.add(names.get(randomIndex));
                                scorePair2.add("lose");

                                scores.add(scorePair);
                                scores.add(scorePair2);
                            }
                            /**
                             * If the server receives ’S’ from C1 and ’P’ from C2 it sends the
                             * string "YOU WIN" to C1 and the string "YOU LOSE" to C2.
                             */
                            else if (currentInput.equals("S") && randomInput.equals("P")) {
                                resClient_1 = "You win against " + names.get(randomIndex);
                                resClient_2 = "You lose " + name;
                                System.out.println("Player one wins.");

                                // save scores
                                ArrayList<String> scorePair = new ArrayList<String>();
                                scorePair.add(name);
                                scorePair.add("win");

                                ArrayList<String> scorePair2 = new ArrayList<String>();
                                scorePair2.add(names.get(randomIndex));
                                scorePair2.add("lose");

                                scores.add(scorePair);
                                scores.add(scorePair2);
                            }
                            /**
                             * If the server receives ’P’ from C1 and ’S’ from C2 it sends the
                             * string "YOU LOSE" to C1 and the string "YOU WIN" to C2.
                             */
                            else if (currentInput.equals("P") && randomInput.equals("S")) {
                                resClient_1 = "You lose " + names.get(randomIndex);
                                resClient_2 = "You win " + name;
                                System.out.println("Player two wins.");

                                // save scores
                                ArrayList<String> scorePair = new ArrayList<String>();
                                scorePair.add(name);
                                scorePair.add("lose");

                                ArrayList<String> scorePair2 = new ArrayList<String>();
                                scorePair2.add(names.get(randomIndex));
                                scorePair2.add("win");

                                scores.add(scorePair);
                                scores.add(scorePair2);
                            }

                            // return results
                            DataOutputStream outToClient_1 = new DataOutputStream(playerSocket.getOutputStream());
                            DataOutputStream outToClient_2 = new DataOutputStream(
                                    sockets.get(randomIndex).getOutputStream());

                            // Remove input
                            for (ArrayList<String> pair : nameInput) {
                                // current player
                                if (pair.get(0).equals(name)) {
                                    pair.set(1, null);
                                }
                                // random player
                                if (pair.get(0).equals(names.get(randomIndex))) {
                                    pair.set(1, null);
                                }
                            }

                            outToClient_1.writeBytes(resClient_1 + "\n");
                            outToClient_2.writeBytes(resClient_2 + "\n");

                        }

                    } catch (Exception e) {
                        System.out.println("Error: " + e);
                    }
                }

                if (responseArray[1].equals("scores")) {

                    // get name of player
                    String name = responseArray[0];

                    // search in array of sockets for socket of player
                    int index = names.indexOf(name);
                    Socket playerSocket = sockets.get(index);

                    System.out.println("Player " + name + " wants to see scores.");
                    System.out.println(scores.toString());

                    // create output stream for player
                    DataOutputStream outToClient = new DataOutputStream(playerSocket.getOutputStream());

                    // if scores empty
                    if (scores.isEmpty()) {
                        outToClient.writeBytes("No scores yet." + "\n");
                    } else {
                        int wins = 0;
                        int loses = 0;
                        int draws = 0;

                        for (ArrayList<String> pair : scores) {
                            if (pair.get(0).equals(name)) {
                                // count wins losses and ties
                                if (pair.get(1).equals("win")) {
                                    wins++;
                                } else if (pair.get(1).equals("lose")) {
                                    loses++;
                                } else if (pair.get(1).equals("draw")) {
                                    draws++;
                                }
                            }
                        }

                        // send player's score to player
                        outToClient.writeBytes("Wins: " + wins + " Loses: " + loses + " Draws: " + draws + "\n");
                    }

                }

                // NOT FINISHED
                // if responseArray[1] contains play but not equal to play
                if (responseArray[1].contains("play") && !responseArray[1].equals("play")
                        && !responseArray[1].equals("players")) {
                    // get name of player
                    String name = responseArray[0];

                    // search in array of sockets for socket of player
                    int index = names.indexOf(name);
                    Socket playerSocket = sockets.get(index);

                    // get name of opponent player
                    String OPname = responseArray[1].substring(5);

                    System.out.println("Player " + name + " wants to play with " + OPname);

                    // search for name
                    int OPindex = names.indexOf(OPname);

                    // if name is not found
                    if (OPindex == -1) {
                        DataOutputStream outToClient = new DataOutputStream(playerSocket.getOutputStream());
                        // return error message
                        outToClient.writeBytes("Error: Name not found" + "\n");
                    }

                }

                // print the arrays
                for (int j = 0; j < sockets.size(); j++) {
                    System.out.println("Socket: " + sockets.get(j));
                    System.out.println("Name: " + names.get(j));
                }
            } catch (Exception e) {
                System.out.println(e);
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
    }
}
