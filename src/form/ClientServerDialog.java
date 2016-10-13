package form;
import client.Client;
import server.Server;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * Created by Irina on 20.09.2016.
 */
public class ClientServerDialog extends JFrame {
    private JButton clientButton;
    private JButton serverButton;
    private JPanel modePanel;

    public ClientServerDialog() {
        setContentPane(modePanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        clientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Client();
            }
        });

        serverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Server();
            }
        });

        setVisible(true);
    }
}

