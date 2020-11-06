TARGET=android/src/com/mygdx/game/AndroidLauncher.java

%.class : %.java
	javac $<

run : ${TARGET}.class
	java ${TARGET}
