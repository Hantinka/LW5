package form;

import main.Const;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Irina on 20.09.2016.
 */
public class ChatInterface extends JFrame {
    private JPanel ChatInterface;
    private JButton connectButton;
    private JTextField serverIPTextField;
    private JTextField yourNicknameTextField;
    public JTextArea chatTextArea;
    private JTextArea yourMessageTextArea;
    private JButton sendMessageButton;

    String ip;
    String nickname;

    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;

    public ChatInterface() {

        //final JScrollPane scrollPane = new JScrollPane(chatTextArea);

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
                    // Подключаемся в серверу и получаем потоки(in и out) для передачи сообщений
                    socket = new Socket(ip, Const.Port);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    Resender resend = new Resender();
                    resend.start();
                    //chatTextArea.setText(nickname + " присоединился к чату");
                    resend.setStop();

                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    //close();
                }
            }
        });

        sendMessageButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

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
            //System.err.println("Потоки не были закрыты!");// TODO: 01.10.2016  
        }
    }

    private class Resender extends Thread {

        private boolean stoped;

        /**
         * Прекращает пересылку сообщений
         */
        public void setStop() {
            stoped = true;
        }

        /**
         * Считывает все сообщения от сервера и выводит их в окно чата
         * Останавливается вызовом метода setStop()
         */
        @Override
        public void run() {
            try {
                while (!stoped) {

                    String strOut = in.readLine();
                    chatTextArea.setText(strOut);


                }
            } catch (IOException e) {
                //System.err.println("Ошибка при получении сообщения.");
                e.printStackTrace();
            }
        }
    }

}

