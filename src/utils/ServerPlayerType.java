package utils;

public enum ServerPlayerType {
    WHITE("W"), BLACK("B"), WHITEWIN("WW"), BLACKWIN("BW"), DRAW("D");
    private final String turn;

    private ServerPlayerType(String s) {
        turn = s;
    }

    public boolean equalsTurn(String otherName) {
        return (otherName == null) ? false : turn.equals(otherName);
    }

    public String toString() {
        return turn;
    }
}