JVM = java
JC = javac
SRC = com/craftinginterpreters/lox
OUT = out

compile:
	mkdir -p $(OUT)
	$(JC) -d $(OUT) $(SRC)/*.java

run: compile
	$(JVM) -cp $(OUT) com.craftinginterpreters.lox.Lox

clean:
	rm -rf $(OUT)
