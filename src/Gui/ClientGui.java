package Gui;

import javax.swing.*;
import java.awt.*;

public class ClientGui {
    public static void main(String[] args) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        //Frame layout
        JFrame login = new JFrame();
        login.setTitle("Cinemax Reservation");
        login.setResizable(false);
        login.setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());
        login.setVisible(true);


        //Logo
        ImageIcon logo = new ImageIcon("res/logo/Blogo.png");
        login.setIconImage(logo.getImage());
        login.getContentPane().setBackground(new Color(30,40,49 ));


   }
}
