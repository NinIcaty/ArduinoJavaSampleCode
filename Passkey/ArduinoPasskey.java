import com.fazecast.jSerialComm.SerialPort;
import java.io.IOException;
import java.util.Scanner;

public class ArduinoPasskey {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        SerialPort[] ports = SerialPort.getCommPorts();
        System.out.println("Available COM Ports:");
        for (int i = 0; i < ports.length; i++) {
            System.out.println(i + ": " + ports[i].getSystemPortName());
        }

        System.out.print("Select port number: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // flush newline

        SerialPort port = ports[choice];
        port.setBaudRate(9600);

        if (!port.openPort()) {
            System.out.println("Failed to open port!");
            return;
        }

        System.out.println("Port opened: " + port.getSystemPortName());

        System.out.print("Enter passkey: ");
        String passkey = scanner.nextLine();

        try {
            port.getOutputStream().write((passkey + "\n").getBytes());
            port.getOutputStream().flush();
        } catch (IOException e) {
            System.out.println("Error writing to Arduino: " + e.getMessage());
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {}

        byte[] buffer = new byte[100];
        int numBytes = 0;

        try {
            numBytes = port.getInputStream().read(buffer);
        } catch (IOException e) {
            System.out.println("Error reading from Arduino: " + e.getMessage());
        }

        if (numBytes > 0) {
            String response = new String(buffer, 0, numBytes);
            System.out.println("Arduino says: " + response);
        } else {
            System.out.println("No reply from Arduino.");
        }

        port.closePort();
    }
}
