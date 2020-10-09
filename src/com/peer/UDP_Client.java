package com.peer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class UDP_Client {
    private List<String> musicdata;
    //one port for each client
    private int port;

    public UDP_Client(int clientNr, int port) {
        this.port = port;
        this.musicdata = new ArrayList<>();
        //client-specific musicdata repeats after the 3rd client (modulo)
        switch (clientNr % 3) {
            case 0:
                this.musicdata.add("Interpret: Beatles Titel: I Wanna Be Your Man");
                this.musicdata.add("Interpret: Sportfreunde Stiller Titel: Ein Kompliment");
                break;
            case 1:
                this.musicdata.add("Interpret: Beatles Titel: All My Loving");
                this.musicdata.add("Interpret: Rolling Stones Titel: Satisfaction");
                break;
            case 2:
                this.musicdata.add("Interpret: Michael Jackson Titel: Thriller");
                this.musicdata.add("Interpret: Razorlight Titel: Wire to Wire");
                break;
            default:
                System.out.println("No music was added");
        }
    }

    public int getPort() {
        return port;
    }

    public List<String> getMusicdata() {
        return musicdata;
    }

    public void setMusicdata(List<String> musicdata) {
        this.musicdata = musicdata;
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Client Nr: ");
        int name = scanner.nextInt(); // client-nr. as user input

        UDP_Client client = new UDP_Client(name, 50000+name); // creates instance of client
        System.out.println("Default musicdata. " + "\u001B[32m" + client.getMusicdata().toString() + "\u001B[0m");

        Channel channel = new Channel(client.getMusicdata()); //channel gets musicdata from client for temporary storage
        channel.bind(client.getPort()); //binding specified port to socket
        channel.start(); // starts receive

        System.out.println("Started on port: " + client.getPort());

        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost(); // destination IP set to localhost
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        while (client.getMusicdata().size()<6) { // stops sending when all 6 songs are stored
            sendMusicdata(client, channel, address);
            System.out.println("\u001B[35m" + "musicdata sent" + "\u001B[0m");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        sendMusicdata(client, channel, address); // sends one more time

        scanner.close();
        channel.stop();
        System.out.println("Closed.");
    }

    //sends musicdata over port 50001, ..., 50010 to specified address
    private static void sendMusicdata(UDP_Client client, Channel channel, InetAddress address) throws IOException {
        for (int i = 50001; i <= 50010; i++) {
            for (int j = 0; j < client.getMusicdata().size(); j++) {
                client.setMusicdata(channel.getMusicdata());
                channel.sendTo(address, client.getMusicdata().get(j), i);
            }
        }
    }
}