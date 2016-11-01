package server;

import main.Const;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Irina on 01.11.2016.
 */
public class FileServer {
    private List<Connection> connectionsFile = Collections.synchronizedList(new ArrayList<Connection>());
    private ServerSocket fileServer;

    public FileServer () {
        try {
            fileServer = new ServerSocket(Const.PortFile);
            while (true) {
                Socket socketFile = fileServer.accept();
                Connection con = new Connection(socketFile);
                connectionsFile.add(con);
                System.out.println("File Connect start!");
                con.start();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при создании сервера для передачи файлов");
            e.printStackTrace();
        }
    }

    private void closeAll() {
        try {
            fileServer.close();
            synchronized(connectionsFile) {
                Iterator<Connection> iter = connectionsFile.iterator();
                while(iter.hasNext()) {
                    ((Connection) iter.next()).close();
                }
            }
        } catch (Exception e) {
            System.err.println("Потоки не были закрыты!");
        }
    }

    private class Connection extends Thread {
        private BufferedReader inFile;
        private PrintWriter outFile;
        private Socket socketFile;
        private FileInputStream inputFile;
        private FileOutputStream outputFile;

        public Connection(Socket socketFile) {
            this.socketFile = socketFile;
            try {
                File fileIn = new File("input.txt");
                inputFile = new FileInputStream(fileIn);
                File fileOut = new File("output.txt");
                outputFile = new FileOutputStream(fileOut);




            } catch (IOException e) {
                System.out.println("Ошибка при создании сокета");
                e.printStackTrace();
                close();
            }
        }

        @Override
        public void run() {

        }

        public void close() {
            try {
                inFile.close();
                outFile.close();
                socketFile.close();
                connectionsFile.remove(this);
                if (connectionsFile.size() == 0) {
                    FileServer.this.closeAll();
                    System.exit(0);
                }
            } catch (Exception e) {
                System.err.println("Потоки не были закрыты!");
            }
        }
    }
}
