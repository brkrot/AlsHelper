
void right() { 
$1
} 
 
void left() { 
$3
} 
 
void up() { 
$0
} 
 
void down() { 
$2
} 
 
void setup(){ 
   
} 
 
void loop() 
{ 
  xData = analogRead(X_PIN);    
  yData = analogRead(Y_PIN); 
 
  if (xData > 800 && currentDirection != 'R') { 
	right(); 
  } 
  else if (xData < 200 && currentDirection != 'L') { 
	left(); 
  } 
  else if (yData < 200 && currentDirection != 'U') { 
	up(); 
  } 
  else if (yData > 800 && currentDirection != 'D') { 
	down(); 
  } 
  currentDirection != 'N'; 
}