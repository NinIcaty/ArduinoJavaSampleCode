import com.fazecast.jSerialComm.*;
import javax.swing.*;
import java.util.Scanner;

public class ArduinoKeyApp {

    private static final String EXPECTED_KEY = "TESTIFICATE";

    public static void main(String[] args) {

        SerialPort port = SerialPort.getCommPort("COM6");
        port.setBaudRate(9600);

        if (!port.openPort()) {
            showLockedScreen("Cannot open COM6.\nKey not detected — vault locked.");
            return;
        }

        System.out.println("Connected. Waiting for key...");

        Scanner input = new Scanner(port.getInputStream());
        long startTime = System.currentTimeMillis();

        while (true) {
            // timeout after 5 seconds
            if (System.currentTimeMillis() - startTime > 5000) {
                port.closePort();
                showLockedScreen("Key not detected — vault locked.");
                return;
            }

            if (input.hasNextLine()) {
                String line = input.nextLine().trim();
                System.out.println("Received: " + line);

                if (line.equals(EXPECTED_KEY)) {
                    System.out.println("✓ Key verified! Opening vault...");
                    port.closePort();

                    SwingUtilities.invokeLater(() -> new PasswordVault().setVisible(true));
                    return;
                }
            }
        }
    }

    private static void showLockedScreen(String message) {
        JOptionPane.showMessageDialog(null, message, "Vault Locked", JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }
}
