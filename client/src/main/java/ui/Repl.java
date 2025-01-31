package ui;

import static ui.EscapeSequences.*;

public class Repl {
    private final ChessClient client;

    public Repl(String serverURL){
        client = new ChessClient(serverURL, this);
    }

}
