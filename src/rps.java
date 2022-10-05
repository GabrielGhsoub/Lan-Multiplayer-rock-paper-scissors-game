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
                String nameAvailability = "";

                // save port in variable
                int port = rps.port;

                BufferedReader inFromUser = new BufferedReader(new InputStreamReader(
                                System.in));
                Socket clientSocket;
                DataOutputStream outToServer;
                BufferedReader inFromServer;

                // check for name availability
                do {
                        clientSocket = new Socket(host, port);
                        outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        inFromServer = new BufferedReader(new InputStreamReader(
                                        clientSocket.getInputStream()));

                        // get name
                        System.out.print(rps.msgEnterName);

                        // get input
                        BufferedReader UserNameBuffer = new BufferedReader(new InputStreamReader(System.in));
                        name = UserNameBuffer.readLine();

                        outToServer.writeBytes(name + "--name" + "\n");
                        outToServer.flush();

                        nameAvailability = inFromServer.readLine();
                        System.out.println(nameAvailability);

                        if (nameAvailability.equals("false")) {
                                System.out.println("Name already taken, please choose another one.");

                        }

                } while (nameAvailability.equals("false"));

                // welcome msg
                System.out.println(rps.msgWelcome);

                do {

                        if (input.equals("-rules")) {
                                System.out.println(rps.msgRules);
                        }

                        if (input.equals("-quit")) {
                                System.out.println("Goodbye!");
                                break;
                        }

                        // scores
                        if (input.equals("-scores")) {
                                System.out.println("Score: ");
                                Socket clientSocket6 = new Socket(rps.host, port);
                                DataOutputStream outToServer6 = new DataOutputStream(
                                                clientSocket6.getOutputStream());

                                outToServer6.writeBytes(name + "--scores" + "\n");
                                outToServer6.flush();

                                String scores = inFromServer.readLine();
                                System.out.println(scores);

                                clientSocket6.close();
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

                                System.out.println("Challenge started! Win 3 times to win the game!");

                                int wins = 0;

                                // for 3 games
                                for (int i = 0; i < 3; i++) {

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

                                        if (result.contains("win")) {
                                                wins++;
                                        }

                                        clientSocket3.close();
                                }

                                // if 3 wins -> win
                                if (wins == 3) {
                                        System.out.println("You won the challenge!");
                                } else {
                                        System.out.println("You lost the challenge!");
                                }

                        }

                        if (input.contains("-play") && !input.equals("-play") && !input.equals("-players")) {

                                System.out.println(
                                                "Please enter your move (R for rock, P for paper, S for scissors ): ");
                                String move = inFromUser.readLine();
                                String opponent = input.substring(6);

                                Socket clientSocket4 = new Socket(rps.host, port);
                                DataOutputStream outToServer4 = new DataOutputStream(
                                                clientSocket4.getOutputStream());

                                outToServer4.writeBytes(name + "--play " + opponent + "--" + move + "\n");
                                outToServer4.flush();

                                System.out.println("Waiting for " + opponent + " to play...");
                                String result = inFromServer.readLine();
                                System.out.println(result);
                                clientSocket4.close();

                        }

                        System.out.print(
                                        "Start the game by typing \"-rules\" to see the rules, \"-scores\" to see your score, \"-players\" to see the players in the lobby,  \"-play\" to play with a random player , \"-play [PLAYER-NAME]\" to play with a specific oponent or \"-quit\" to quit : ");

                        input = inFromUser.readLine();

                } while (!input.equals("-quit"));

                // Close socket
                clientSocket.close();

        }
}
