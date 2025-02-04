package ui;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverURL){
        client = new ChessClient(serverURL/*, this*/);
    }

    public void run(){
        System.out.println("Welcome to Devyn's Chess Program!");
        System.out.println();
        System.out.println(client.help());

        Scanner scanner = new Scanner(System.in);

        String result = scanner.nextLine();
        while (!result.equals("quit")){
            client.eval(result);
        }
    }



}
