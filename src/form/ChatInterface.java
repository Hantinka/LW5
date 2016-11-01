package form;

import main.Const;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;
import java.net.Socket;

/**
 * Created by Irina on 20.09.2016.
 */
public class ChatInterface extends JFrame {
    private JPanel ChatInterface;
    private JScrollPane scrollPane;
    private JButton connectButton;
    private JTextField serverIPTextField;
    private JTextField yourNicknameTextField;
    private JTextArea chatTextArea;
    private JTextArea yourMessageTextArea;
    private JButton sendMessageButton;
    private JButton sendFileButton;
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private Socket socketFile;
    private FileInputStream inputFile;
    private FileOutputStream outputFile;
    private DataOutputStream outputStream;

    String ip;
    String nickname;

    public ChatInterface() {
        setContentPane(ChatInterface);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatTextArea.setEditable(false);
        String setIP = "127.0.0.1";
        serverIPTextField.setText(setIP);

        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ip = serverIPTextField.getText();
                nickname = yourNicknameTextField.getText();
                try {
                    socket = new Socket(ip, Const.Port);

                    if (socket.isConnected()){
                        System.out.println("connected");}
                    else {
                        System.out.println("not connected");
                    }
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(nickname);

                    ReSender reader = new ReSender();
                    reader.start();
                } catch (Exception e1) {
                    System.out.println("Ошибка при подключении к серверу и получению потоков (in и out) для передачи сообщений");
                    e1.printStackTrace();
                }

                connectButton.setVisible(false);
                serverIPTextField.setEnabled(false);
                yourNicknameTextField.setEnabled(false);
            }
        });

        sendFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


            }
        });

        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String str = yourMessageTextArea.getText();
                out.println(str);

            }
        });

        chatTextArea.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                chatTextArea.setLineWrap(true);
                chatTextArea.setWrapStyleWord(true);
            }
        });

        yourMessageTextArea.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                yourMessageTextArea.setLineWrap(true);
                yourMessageTextArea.setWrapStyleWord(true);
            }
        });

        setVisible(true);

    }

    private void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            System.err.println("Потоки не были закрыты!");
        }
    }

    private class ReSender extends Thread {
        private boolean stoped;

        public void setStop() {
            stoped = true;
        }

        @Override
        public void run() {
            try {

                while (!stoped) {
                    String str = in.readLine();
                    chatTextArea.append(str);
                    chatTextArea.append("\n");
                    System.out.println(str + " Это текстовое сообщение");
                }
            } catch (IOException e) {
                System.err.println("Ошибка при получении сообщения.");
                e.printStackTrace();
            }
        }
    }
}




