## General Description

The purpose of the program is to provide a network version of the hand game Rock-paperscissors using the Transmission Control Protocol.
The initial challenge was done as well as 3 optional tasks:
    a. -The 3-game rule
    b. Keeping the score after disconnection
    c. A heartbeat ping every few seconds to check online players
The program contains 2 executables files, the server file and the client file

## Server Description
By executing the server class (Server.java) the user will be prompt to specify a port number which has to be an integer value strictly greater than zero and less than or equal to 65535. Alternatively, “0” is accepted for the standard port (1337) . After submitting the script runs a little validation and checks if the value is within the defined range (0 > x <= 65535) and sets the port value. So far, no further validation is implemented, I’m not verifying for example if the selected port is reserved or just busy at the moment.

If no exception is thrown the program dumps a status message to inform the user, that the server is running on the specified
port number.

Since the socket is not closed the server remains in a while() loop waiting for incoming connections. A corresponding status message will be provided if a new client connects. The server accepts an input stream from any clients. And based on that input the server
returns a specific response:
If it receives a play request from the client the following use case will happen:

Once the players have sent their packets, the program computes a result based on the user inputs (R – rock , S – scissors , P - paper ) and the following rule set. Rock beats scissors, scissors beat paper, paper beats rock.

If the characters received from client one and client two are the same, then the server sends back to both clients the string "Draw". If the server receives “R” from client one and “S” from client two, it sends the string “You win against X" to client one and the string " You lose against X " to client two. If the server receives “S” from client one and “R” from client two, it sends the string "You lose against X" to client one and the string " You win against X " to client two.

The result will be echoed out and a correspondent massage will be sent to each client. If it receives a “players” or “scores” request, it will return the list of players or the scores respectively. Noting that the server sends a ping every couple of seconds to return the state of the connected sockets which is essential to keeping an up-to-date list of online players.

## Client’s Description

The client class (Client.java) creates a connection to the server on the local host at the default port 1337. If the connection has been successfully established the script prompts the user to choose a correspondent name and a name check will be done server side, if the name is not accepted the user has to choose again, if it is then he will be prompted to choose an action.

By typing “-rules” the rule set can be displayed, by typing “-players” a list of connected players will be displayed, by typing “-scores” the user’s scores will be displayed (Noting that if the user has the same name as an older disconnected user, the score will transfer) and finally, by typing “-play” a random active opponent gets chosen and a second prompt will be displayed to choose a character (R)rock, (P)paper or (S)scissors. To complete the challenge a user must win 3 games in a row. 

After sending the character to the server via the TCP protocol the client waits for a reply from the server and a notification will be dumped.
Once the client receives a response from the server the message will be printed to the screen and the connection will be closed.

