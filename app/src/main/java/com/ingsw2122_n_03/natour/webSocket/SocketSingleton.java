package com.ingsw2122_n_03.natour.webSocket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketSingleton {

    private static Socket mSocket;
    private static PrintWriter out;
    private static BufferedReader in;

    private static void initSocket() {

        final String SERVER_HOSTNAME = ""; //TODO
        final int SERVER_PORT = 0000; //TODO

        try {

            InetSocketAddress socketAddress = new InetSocketAddress(SERVER_HOSTNAME, SERVER_PORT);
            mSocket = new Socket();
            mSocket.setSoTimeout(5000);
            mSocket.connect(socketAddress, 5000);

            out = initializeOut(mSocket);
            in = initializeIn(mSocket);

        } catch (IOException e) {
            mSocket = null;
        }
    }

    public static Socket getInstance() {
        if (mSocket == null) initSocket();
        return mSocket;
    }

    private static PrintWriter initializeOut(Socket socket) throws IOException {

        return new PrintWriter(
                new BufferedWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream())), true);

    }

    private static BufferedReader initializeIn(Socket socket) throws IOException {

        return new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()));
    }

    public static void write(String response){

        out.print(response);
        out.flush();
    }

    public static String read(){

        String response = null;

        try {
            response = in.readLine();
        } catch (IOException e) {
            closeConnection();
            initSocket();
            return null;
        }

        if(response == null){
            closeConnection();
            initSocket();
        }

        return response;
    }

    public static void closeConnection(){

        try {
            mSocket.close();
            in.close();
            out.close();

            mSocket = null;
            in = null;
            out = null;

        } catch (IOException ignored) {}

    }
}