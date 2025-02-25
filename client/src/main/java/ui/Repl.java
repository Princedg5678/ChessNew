package ui;

import websocket.ServerMessageHandler;
import websocket.messages.ServerMessage;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl implements ServerMessageHandler {
    private final ChessClient client;

    public Repl(String serverURL) {
        client = new ChessClient(serverURL, this);
    }

    public void run(){
        System.out.println("Welcome to Devyn's Chess Program!");
        System.out.println();
        System.out.println(client.help());

        Scanner scanner = new Scanner(System.in);

        String result = scanner.nextLine();
        while (!result.equals("quit")) {
             try {
                result = client.eval(result);
                System.out.println(result);
            } catch (Exception e) {
                 System.out.println(e.getMessage());
             }
            result = scanner.nextLine();
        }
    }


    @Override
    public void notify(ServerMessage serverMessage) {
        if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.LOAD_GAME){

        }
        else if (serverMessage.getServerMessageType() == ServerMessage.ServerMessageType.ERROR){

        }
        else {

        }
    }
}
