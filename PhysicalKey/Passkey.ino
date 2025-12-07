// Arduino Uno serial passkey check

const int LED_PIN = 13;        // Built-in LED
const String CORRECT_KEY = "das";  // Change the passkey here

void setup() {
  pinMode(LED_PIN, OUTPUT);
  digitalWrite(LED_PIN, LOW);

  Serial.begin(9600);
  while (!Serial) { }  // Wait for Serial on some boards
}

void loop() {
  if (Serial.available() > 0) {
    String received = Serial.readStringUntil('\n');
    received.trim();

    if (received == CORRECT_KEY) {
      digitalWrite(LED_PIN, HIGH);
      Serial.println("OK");
    } else {
      digitalWrite(LED_PIN, LOW);
      Serial.println("WRONG");
    }
  }
}
