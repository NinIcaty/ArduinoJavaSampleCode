void setup() {
  Serial.begin(9600);
  pinMode(2, INPUT_PULLUP); // SW button
}

void loop() {
  int x = analogRead(A0);
  int y = analogRead(A1);
  int sw = digitalRead(2); // 1 = not pressed, 0 = pressed

  Serial.print(x);
  Serial.print(",");
  Serial.print(y);
  Serial.print(",");
  Serial.println(sw);

  delay(10);
}
