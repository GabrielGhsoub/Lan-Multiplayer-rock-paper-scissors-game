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
    private static String msgWelcome = "--- Welcome to Paper Scissors Stone V. "
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
        String response;

        // get name
        System.out.print(rps.msgEnterName);

        // get input
        BufferedReader UserNameBuffer = new BufferedReader(new InputStreamReader(System.in));
        name = UserNameBuffer.readLine();

        System.out.println(rps.msgWelcome);

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(
                System.in));
        Socket clientSocket = new Socket(rps.host, rps.port);
        DataOutputStream outToServer = new DataOutputStream(
                clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(
                clientSocket.getInputStream()));

        do {

            if (input.equals("-rules")) {
                System.out.println(rps.msgRules);
            }

            // Prompt user for select rock, paper or scissors ...
            System.out
                    .println("Start the game by selecting (R)ock (P)aper, (S)cissors");
            System.out.print("or type \"-rules\" in order to see the rules: ");
            input = inFromUser.readLine();

        } while (!input.equals("R") && !input.equals("P") && !input.equals("S"));

        // Transmit input to the server and provide some feedback for the user
        outToServer.writeBytes(name + "-" + input + "\n");
        System.out
                .println("\nYour input ("
                        + input
                        + ") was successfully transmitted to the server. Now just be patient and wait for the result ...");

        // Catch respones
        response = inFromServer.readLine();

        // Display respones
        System.out.println("Response from server: " + response);

        // Close socket
        clientSocket.close();

    }
}
