package server;

import main.Const;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Irina on 20.09.2016.
 */
public class Server {
    private List<Connection> connections = Collections.synchronizedList(new ArrayList<Connection>());
    private ServerSocket server;

    public Server() {
        try {
            server = new ServerSocket(Const.Port);
            while (true) {
                Socket socket = server.accept();
                Connection con = new Connection(socket);
                connections.add(con);
                System.out.println("Connect start!");
                con.start();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при создании сервера");
            e.printStackTrace();
        }
    }

    private void closeAll() {
        try {
            server.close();
            synchronized(connections) {
                Iterator<Connection> iter = connections.iterator();
                while(iter.hasNext()) {
                    ((Connection) iter.next()).close();
                }
            }
        } catch (Exception e) {
            System.err.println("Потоки не были закрыты!");
        }
    }

    private class Connection extends Thread {
        private BufferedReader in;
        private PrintWriter out;
        private Socket socket;
        private String name = "";
        private FileInputStream inputFile;

        public Connection(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                File file = new File("input.txt");
                inputFile = new FileInputStream(file);
                byte[] toByteArray = IOUtils.toByteArray(inputFile);
                System.out.println("Server is ok after file reading.");
                socket.getOutputStream().write(toByteArray);


            } catch (IOException e) {
                System.out.println("Ошибка при создании сокета");
                e.printStackTrace();
                close();
            }
        }

        @Override
        public void run() {
            try {
                name = in.readLine();
                System.out.println(name + " имя на сервере");
                synchronized(connections) {
                    Iterator<Connection> iter = connections.iterator();
                    while(iter.hasNext()) {
                        ((Connection) iter.next()).out.println(" * " +name + " comes now" + " / ");
                    }
                }
            } catch (IOException e) {
                System.out.println("Ошибка при отправке сообщения о том, что зашёл новый пользователь");
                e.printStackTrace();
            }
            try {
                String str = "";
                while (true) {
                    str = in.readLine();
                    System.out.println(str + " сообщение на сервере");
                    if(str.equals("exit")) break;

                    // Отправляем всем клиентам очередное сообщение
                    synchronized(connections) {
                        Iterator<Connection> iter = connections.iterator();
                        while(iter.hasNext()) {
                            ((Connection) iter.next()).out.println(name + ": " + str);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Ошибка при отправке всем клиентам очередного сообщения");
                e.printStackTrace();
            }
            synchronized(connections) {
                Iterator<Connection> iter = connections.iterator();
                while(iter.hasNext()) {
                    ((Connection) iter.next()).out.println(name + " has left");
                }
            }
         }

        public void close() {
            try {
                in.close();
                out.close();
                socket.close();
                connections.remove(this);
                if (connections.size() == 0) {
                    Server.this.closeAll();
                    System.exit(0);
                }
            } catch (Exception e) {
                System.err.println("Потоки не были закрыты!");
            }
        }
    }
}
