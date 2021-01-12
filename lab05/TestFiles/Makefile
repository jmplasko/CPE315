all: lab5 test
lab5:
	javac lab5.java FileLine.java Instruction.java Register.java Simulator.java

test:
	java lab5 ./TestFiles/lab4_fib20.asm ./TestFiles/lab5.script > lab5_ghr_2_notspecified.out
	java lab5 ./TestFiles/lab4_fib20.asm ./TestFiles/lab5.script 2 > lab5_ghr_2.out
	java lab5 ./TestFiles/lab4_fib20.asm ./TestFiles/lab5.script 4 > lab5_ghr_4.out
	java lab5 ./TestFiles/lab4_fib20.asm ./TestFiles/lab5.script 8 > lab5_ghr_8.out
	java lab5 figure.asm ./TestFiles/figure.script
	diff -w -B lab5_ghr_2_notspecified.out ./TestFiles/lab5_ghr2.output
	diff -w -B lab5_ghr_2.out ./TestFiles/lab5_ghr2.output
	diff -w -B lab5_ghr_4.out ./TestFiles/lab5_ghr4.output
	diff -w -B lab5_ghr_8.out ./TestFiles/lab5_ghr8.output
	diff -w -B coordinates.csv ./TestFiles/coordinates.csv
	rm *.out *.class *.csv
