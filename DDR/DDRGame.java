import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.fazecast.jSerialComm.SerialPort;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DDRGame extends JPanel implements ActionListener {

    class Arrow {
        int x, y;
        String direction;
        Arrow(int x, int y, String direction) {
            this.x = x; this.y = y; this.direction = direction;
        }
    }

    javax.swing.Timer timer;
    List<Arrow> arrows = new ArrayList<>();
    int score = 0;

    SerialPort arduinoPort;
    BufferedReader serialReader;

    public DDRGame() {
        setPreferredSize(new Dimension(400, 600));
        setBackground(Color.black);

        // Start game loop
        timer = new javax.swing.Timer(16, this);
        timer.start();

        // Spawn arrows every second
        new javax.swing.Timer(1000, e -> spawnArrow()).start();

        setupSerial();
    }

    void setupSerial() {
        // Replace "COM6" with your Arduino COM port
        arduinoPort = SerialPort.getCommPort("COM6"); 
        arduinoPort.setComPortParameters(9600, 8, 1, 0);
        arduinoPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);

        if (arduinoPort.openPort()) {
            System.out.println("Connected to " + arduinoPort.getSystemPortName());
            serialReader = new BufferedReader(new InputStreamReader(arduinoPort.getInputStream()));
        } else {
            System.out.println("Failed to open serial port.");
        }
    }

    void spawnArrow() {
        String[] dirs = {"LEFT", "DOWN", "UP", "RIGHT"};
        String d = dirs[(int)(Math.random() * 4)];

        int x = switch(d) {
            case "LEFT" -> 50;
            case "DOWN" -> 150;
            case "UP" -> 250;
            default -> 350;
        };
        arrows.add(new Arrow(x, 0, d));
    }

    void checkHit(String direction) {
        for (Arrow a : arrows) {
            if (a.direction.equals(direction) && a.y >= 500 && a.y <= 560) {
                score += 100;
                a.y = 9999; // move off-screen
                System.out.println("Hit! Score = " + score);
                return;
            }
        }
        System.out.println("Miss!");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Move arrows
        for (Arrow a : arrows) a.y += 5;

        // Read Arduino
        if (arduinoPort != null && arduinoPort.isOpen()) {
            try {
                if (serialReader.ready()) {
                    String line = serialReader.readLine().trim();
                    if (!line.isEmpty() && !line.equals("NONE")) {
                        System.out.println("Arduino: " + line);
                        checkHit(line);
                    }
                }
            } catch (Exception ex) {
                System.out.println("Serial read error.");
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.white);
        g.drawString("Score: " + score, 10, 15);

        // Hit zone
        g.setColor(Color.gray);
        g.fillRect(0, 500, 400, 10);

        // Draw arrows
        for (Arrow a : arrows) {
            switch(a.direction) {
                case "LEFT" -> g.setColor(Color.red);
                case "DOWN" -> g.setColor(Color.green);
                case "UP" -> g.setColor(Color.blue);
                case "RIGHT" -> g.setColor(Color.yellow);
            }
            g.fillRect(a.x, a.y, 40, 40);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Arduino DDR");
        DDRGame game = new DDRGame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game);
        frame.pack();
        frame.setVisible(true);
    }
}
