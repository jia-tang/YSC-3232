TARGET=android/src/com/mygdx/game/AndroidLauncher

%.class : %.java
	javac $<

run : ${TARGET}.class
	java ${TARGET}
