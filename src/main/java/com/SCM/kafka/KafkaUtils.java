package com.SCM.kafka;

import java.net.InetSocketAddress;
import java.net.Socket;

public class KafkaUtils {

    public static boolean isKafkaRunning(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), 2000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

