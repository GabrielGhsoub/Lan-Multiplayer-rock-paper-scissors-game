import java.io.*;
import java.net.*;

public class rps {

        /**
         * The host
         * 
         * @var string
         * 
         */

        private static String host = "localhost";

        /**
         * The port
         * 
         * @var integer
         */
        private static Integer port = 1337;

        /**
         * The version of the client class
         * 
         * @var double
         */
        private static Double versionNumber = 1.0;

        /**
         * A short welcome msg
         * 
         * @var string
         */
        private static String msgWelcome = "--- Welcome to Rock Paper Scissors V. "
                        + versionNumber + " --- \n";

        /**
         * A short msg to prompt the user to enter a name
         * 
         * @var string
         */
        private static String msgEnterName = "Please enter your name: ";

        /**
         * The help context
         * 
         * @var string
         * 
         */
        private static String msgRules = "\nRule set:\n - (R)ock beats (S)cissors\n - (S)cissors beats (P)aper\n - (P)aper beats (R)ock\n";

        public static void main(String args[]) throws Exception {

                String input = "";
                String name = "";

                // save port in variable
                int port = rps.port;

                // get name
                System.out.print(rps.msgEnterName);

                // get input
                BufferedReader UserNameBuffer = new BufferedReader(new InputStreamReader(System.in));
                name = UserNameBuffer.readLine();

                System.out.println(rps.msgWelcome);

                BufferedReader inFromUser = new BufferedReader(new InputStreamReader(
                                System.in));
                Socket clientSocket = new Socket(rps.host, port);
                DataOutputStream outToServer = new DataOutputStream(
                                clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
                                clientSocket.getInputStream()));

                outToServer.writeBytes(name + "--name" + "\n");

                do {

                        if (input.equals("-rules")) {
                                System.out.println(rps.msgRules);
                        }

                        if (input.equals("-quit")) {
                                System.out.println("Goodbye!");
                                break;
                        }

                        // players
                        if (input.equals("-players")) {

                                System.out.println("Players: ");
                                Socket clientSocket2 = new Socket(rps.host, port);
                                DataOutputStream outToServer2 = new DataOutputStream(
                                                clientSocket2.getOutputStream());

                                outToServer2.writeBytes(name + "--players" + "\n");
                                outToServer2.flush();

                                String players = inFromServer.readLine();
                                System.out.println(players);

                                clientSocket2.close();
                        }

                        // play
                        if (input.equals("-play")) {

                                System.out.println(
                                                "Please enter your move (R for rock, P for paper, S for scissors ): ");
                                String move = inFromUser.readLine();

                                Socket clientSocket3 = new Socket(rps.host, port);
                                DataOutputStream outToServer3 = new DataOutputStream(
                                                clientSocket3.getOutputStream());

                                outToServer3.writeBytes(name + "--play--" + move + "\n");
                                outToServer3.flush();

                                System.out.println("Waiting for opponent to play...");
                                String result = inFromServer.readLine();
                                System.out.println(result);

                                if (result.equals("First Player")) {
                                        System.out.println("Please wait for another player to join!");
                                        result = inFromServer.readLine();
                                        System.out.println(result);
                                }

                                clientSocket3.close();

                        }

                        System.out.print("Hello " + name
                                        + ", Start the game by typing \"-rules\" to see the rules, \"-players\" to see the players in the lobby,  \"-play\" to play with a random player , \"-play [PLAYER-NAME]\" to play with a specific oponent or \"-quit\" to quit : ");

                        input = inFromUser.readLine();

                } while (!input.equals("-quit"));

                // // Transmit input to the server and provide some feedback for the user
                // outToServer.writeBytes(name + "-" + input + "\n");

                // System.out
                // .println("\nYour input ("
                // + input
                // + ") was successfully transmitted to the server. Now just be patient and wait
                // for the result ...");

                // // Catch respones
                // response = inFromServer.readLine();

                // // Display respones
                // System.out.println("Response from server: " + response);

                // Close socket
                clientSocket.close();

        }
}
