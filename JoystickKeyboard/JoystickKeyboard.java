import java.awt.Robot;
import java.awt.event.KeyEvent;
import com.fazecast.jSerialComm.SerialPort;

public class JoystickKeyboard {

    public static void main(String[] args) throws Exception {

        SerialPort port = SerialPort.getCommPort("COM6"); // CHANGE COM PORT
        port.setBaudRate(9600);

        if (!port.openPort()) {
            System.out.println("ERROR: Could not open serial port!");
            return;
        }

        System.out.println("Connected on " + port.getSystemPortName());

        Robot robot = new Robot();
        byte[] buffer = new byte[1024];
        StringBuilder sb = new StringBuilder();

        while (true) {
            int bytes = port.readBytes(buffer, buffer.length);

            if (bytes > 0) {
                sb.append(new String(buffer, 0, bytes));

                int newlineIndex;
                while ((newlineIndex = sb.indexOf("\n")) >= 0) {
                    String line = sb.substring(0, newlineIndex).trim();
                    sb.delete(0, newlineIndex + 1);

                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        try {
                            int joyX = Integer.parseInt(parts[0]);
                            int joyY = Integer.parseInt(parts[1]);
                            int sw   = Integer.parseInt(parts[2]);

                            pressKeys(robot, joyX, joyY, sw);

                        } catch (Exception e) {
                            // ignore bad lines
                        }
                    }
                }
            }
        }
    }

    // -------------------------
    // Map joystick to arrow keys
    // -------------------------
    static void pressKeys(Robot robot, int joyX, int joyY, int sw) {

        int center = 512;
        int deadZone = 100; // bigger deadzone for key presses

        // Left / Right
        if (joyX < center - deadZone) {
            robot.keyPress(KeyEvent.VK_LEFT);
            robot.keyRelease(KeyEvent.VK_LEFT);
        } else if (joyX > center + deadZone) {
            robot.keyPress(KeyEvent.VK_RIGHT);
            robot.keyRelease(KeyEvent.VK_RIGHT);
        }

        // Up / Down (invert Y if needed)
        if (joyY < center - deadZone) {
            robot.keyPress(KeyEvent.VK_UP);
            robot.keyRelease(KeyEvent.VK_UP);
        } else if (joyY > center + deadZone) {
            robot.keyPress(KeyEvent.VK_DOWN);
            robot.keyRelease(KeyEvent.VK_DOWN);
        }

        // Button = Space
        if (sw == 0) {
            robot.keyPress(KeyEvent.VK_SPACE);
            robot.keyRelease(KeyEvent.VK_SPACE);
        }
    }
}
