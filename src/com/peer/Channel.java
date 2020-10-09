package com.peer;

import java.io.IOException;
import java.net.*;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.List;

public class Channel implements Runnable {
    private DatagramSocket socket;
    private boolean running;
    private List<String> musicdata; //Channel contains musicdata as temporary storage

    public Channel(List<String> musicdata) {
        this.musicdata = musicdata;
    }

    public List<String> getMusicdata() {
        return musicdata;
    }

    //binding specified port to socket
    public void bind(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }

    //creating new thread
    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

    //stops receiving and closes socket
    public void stop() {
        running = false;
        socket.close();
    }

    //method to receive packets
    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        running = true;
        while (running) {
            try {
                socket.receive(packet); //receiving packet
                System.out.println("\u001B[33m" + "received" + "\u001B[0m");
                String message = new String(buffer, 0, packet.getLength());
                //does not add the song if the client already contains this song
                if (!this.musicdata.contains(message))
                    this.musicdata.add(message);

                //prints cyan if maximal musicdata length is achieved
                if (musicdata.size()>=6){
                    System.out.println("\u001B[36m");
                }
                System.out.println(musicdata.toString());
            } catch (IOException e) {
                break;
            }
        }
    }

    //sends songs/message to specified address, port
    public void sendTo(InetAddress address, String message, int port) throws IOException {
        byte[] buffer = message.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(packet);
    }

}
