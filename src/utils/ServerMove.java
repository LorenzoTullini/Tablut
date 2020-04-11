package utils;

import java.io.Serializable;

public class ServerMove implements Serializable {
    private String from;
    private String to;
    private ServerPlayerType turn;

    public ServerMove(String from, String to, ServerPlayerType turn) {
        this.from = from;
        this.to = to;
        this.turn = turn;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public ServerPlayerType getTurn() {
        return turn;
    }

    public void setTurn(ServerPlayerType turn) {
        this.turn = turn;
    }
}
