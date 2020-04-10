package client;

import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/*
Network class uses for all communications to game server
*/

/*
TODO: bisogna distinguere se il giocatore è bianco o nero per scegliere la porta a cui inviare i messaggi, decidere se qui o nel main con due oggetti Network separati
*/

public class Network {
    private Socket playerSocket;
    private String ip;
    private int port;
    private DataInputStream in;
    private DataOutputStream out;
    private Gson gson;

    public Network(String ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            this.playerSocket = new Socket(ip, port);
            this.out = new DataOutputStream(playerSocket.getOutputStream());
            this.in = new DataInputStream(playerSocket.getInputStream());
            this.gson = new Gson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Send player name to server
    public void sendPlayerName(String name){
        try {
            StreamUtils.writeString(out, this.gson.toJson(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO: getState per ricevere lo stato attuale dal server

    //TODO: sendMove per mandare la mossa scelta al server

    //Close connection with the server
    public void distroyNetwork(){
        try {
            out.close();
            in.close();
            playerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
