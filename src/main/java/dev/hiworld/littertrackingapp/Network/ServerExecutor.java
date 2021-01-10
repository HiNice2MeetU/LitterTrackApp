package dev.hiworld.littertrackingapp.Network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLParameters;


public class ServerExecutor {
    static int Port = 2048;
    static String IP = "192.168.6.133";


    public ServerExecutor(){
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            char[] keyStorePassword = "changeit".toCharArray();
            try (InputStream keyStoreData = new FileInputStream("keystore.jks")) {
                keyStore.load(keyStoreData, keyStorePassword);
            }
        } catch (Exception e){
            Log.e("ServerExecutor", e.toString());
        }
    }

    public static void Execute() throws Exception {
        try {
            // Establish Socket
            Socket Soc = new Socket(IP, Port);

            // Make Streams
            PrintWriter out = new PrintWriter(Soc.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(Soc.getInputStream()));

            while (true) {
                //Log.d("ServerTransport", "");
                out.println("Hello World!");
                Log.d("ServerExecutor", in.readLine());


            }

        } catch (IOException e) {
            Log.d("ServerExecutor", e.toString());
        }
    }

    public static String SecureExecute2(){
        SocketFactory factory = SSLSocketFactory.getDefault();

        try  {
            // Create Connection
            Socket connection = factory.createSocket(IP, Port);

            // Set Ciphers
            ((SSLSocket) connection).setEnabledCipherSuites(new String[] { "TLS_DHE_DSS_WITH_AES_256_CBC_SHA256"});
            ((SSLSocket) connection).setEnabledProtocols(new String[] { "TLSv1.2"});

            // Set Endpoint and params
            SSLParameters sslParams = new SSLParameters();
            sslParams.setEndpointIdentificationAlgorithm("HTTPS");
            ((SSLSocket) connection).setSSLParameters(sslParams);

            // Make Readers
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return input.readLine();
        } catch (IOException e) {
            Log.e("ServerExecutor", e.toString());
            return null;
        }
    }

    public static void SecureExecute(){
        try {
            // Make SSL Connection
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(IP, Port);

            // Make Streams
            InputStream in = sslsocket.getInputStream();
            OutputStream out = sslsocket.getOutputStream();

            out.write(1);
            while (in.available() > 0) {
                System.out.print(in.read());
            }

            //System.out.println("Secured connection performed successfully");
        } catch (IOException e) {
            Log.d("ServerExcecutor", e.toString());
        }
    }
}
