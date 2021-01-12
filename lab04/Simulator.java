// Name:  Jesus Blanco and James Plasko
// Section:  CPE 315-03

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

// Runs the Simulator
public class Simulator {
	private List<FileLine> fileLines;
	private Map<String, Integer> programLabels; //Labels
	private List<Instruction> programInst;

	private List<Register> registerFile;
	private int progCount;
	private int instructionCount;
	private int[] memory;
	private boolean stall, squash;
	private int loadedReg;
	private List<String> structs;
	private int cycleCount;
	private int targetPC;
	private int squashNum;

	public Simulator(List<FileLine> program, Map <String, Integer> labels, List<Instruction> programI){
		this.fileLines = program;
		this.programLabels = labels;
		this.programInst = programI;

		this.registerFile = new ArrayList<>();
		this.structs = new ArrayList<>();
		structs.add("empty");
		structs.add("empty");
		structs.add("empty");
		structs.add("empty");
		this.progCount = 0;
		this.instructionCount = 0;
		this.stall = false;
		this.squash = false;
		this.loadedReg = -1;	//because -1 is not a register
		this.memory = new int[8192];
		this.cycleCount = 4;
	}

	// Get Methods
	public List<FileLine> getFileLines(){return this.fileLines;}
	public Map<String, Integer> getProgramLabels(){return this.programLabels;}

	public List<Register> getRegisterFile(){return this.registerFile;}
	public int getProgCount(){return this.progCount;}
	public int[] getMemory(){return this.memory;}

	// MUST be ran to initialize the register list
	public void initializeRegisters(){
		for (int i=0; i < 32; i++){
			registerFile.add(new Register(0));
		}
	}

	// Interpret Input
	public  Boolean interpretInput(String input){
		String [] split = input.split("\\ ");

		if (split.length == 1){
			if (split[0].equals("h")){
				displayHelp();
			}
			else if (split[0].equals("d")){
				System.out.print('\n');
				dump();
				System.out.print('\n');
			}
			else if (split[0].equals("p")){

				pipeline();

			}
			else if (split[0].equals("s")){
				runamount(1);
			}
			else if (split[0].equals("r")){
				runfull();
				float cpi = (float) cycleCount/ (float)instructionCount;
				System.out.println("\nProgram complete");
				System.out.println( String.format( "%-16s", "CPI = " + String.format("%.3f", cpi))
						+ String.format("%-17s", " Cycles = " + cycleCount)
						+ String.format("%-17s", "Instructions = " + instructionCount));
				System.out.println();
			}
			else if (split[0].equals("c")){
				clear();
			}
			else if (split[0].equals("q")){
				return false;
			}
			else{
				System.out.println("        Invalid command. Press 'h' for help.");
				return true;
			}
		}
		else if (split.length == 2){
			if (split[0].equals("s")) {
				try{
					int stepThrough = Integer.parseInt(split[1]);
					System.out.println("        " + stepThrough +" clock cycle(s) executed");
					runamount(stepThrough);

				}
				catch (NumberFormatException e) {
					System.err.println("        Invalid number. Usage: s num");
					return true;
				}
			}
			else{
				System.out.println("        Invalid command. Press 'h' for help.");
				return true;
			}
		}
		else if (split.length == 3) {
			if (split[0].equals("m")){
				try{
					int firstIndex = Integer.parseInt(split[1]);
					int secondIndex = Integer.parseInt(split[2]);
					System.out.print('\n');
					memoryDump(firstIndex, secondIndex);
					System.out.print('\n');
				}
				catch (NumberFormatException e) {
					System.err.println("        Invalid number. Usage: m num1 num2");
					return true;
				}
			}
			else{
				System.out.println("        Invalid command. Press 'h' for help.");
				return true;
			}
		}
		else{
			System.out.print("        Invalid command. Press 'h' for help.");
			return true;
		}
		return true;
	}

	// Input Functions
	private static void displayHelp(){
		System.out.println("\nh = show help");
		System.out.println("d = dump register state");
		System.out.println("s = single step through the program (i.e. execute 1 instruction and stop)");
		System.out.println("s num = step through num instructions of the program");
		System.out.println("r = run until the program ends");
		System.out.println("m num1 num2 = display data memory from location num1 to num2");
		System.out.println("c = clear all registers, memory, and the program counter to 0");
		System.out.println("q = exit the program\n");
	}

	private void clear(){
		this.registerFile = new ArrayList<>();
		this.progCount = 0;
		this.cycleCount = 4;
		this.instructionCount = 0;
		this.memory = new int[8192];
		this.loadedReg = -1;
		this.stall = false;
		this.squash = false;
		structs.set(3,"empty");
		structs.set(2, "empty");
		structs.set(1, "empty");
		structs.set(0, "empty");
		this.initializeRegisters();
		System.out.println("        Simulator reset");
		System.out.print("\n");
	}

	private void memoryDump(int indexOne, int indexTwo){
		try {
			for (int i = indexOne; i <= indexTwo; i++) {
				System.out.println("[" + i + "] = " + memory[8191 - i]);
			}
		}
		catch(ArrayIndexOutOfBoundsException e){
			System.err.println(e.getMessage());
		}
	}

	// MIGHT NOT BE RIGHT PADDED WITH SPACES
	// it might be System.out.print("$register = " + value + "          ");
	private void dump(){
		System.out.println("pc = " + progCount);
		// First Row
		System.out.print(String.format("%-" + 16 + "s","$0 = " + registerFile.get(0).getValue()));
		System.out.print(String.format("%-" + 16 + "s","$v0 = " + registerFile.get(2).getValue()));
		System.out.print(String.format("%-" + 16 + "s","$v1 = " + registerFile.get(3).getValue()));
		System.out.print("$a0 = " + registerFile.get(4).getValue());
		System.out.print('\n');
		// Second Row
		System.out.print(String.format("%-" + 16 + "s","$a1 = " + registerFile.get(5).getValue()));
		System.out.print(String.format("%-" + 16 + "s","$a2 = " + registerFile.get(6).getValue()));
		System.out.print(String.format("%-" + 16 + "s","$a3 = " + registerFile.get(7).getValue()));
		System.out.print("$t0 = " + registerFile.get(8).getValue());
		System.out.print('\n');
		// Third Row
		System.out.print(String.format("%-" + 16 + "s","$t1 = " + registerFile.get(9).getValue()));
		System.out.print(String.format("%-" + 16 + "s","$t2 = " + registerFile.get(10).getValue()));
		System.out.print(String.format("%-" + 16 + "s","$t3 = " + registerFile.get(11).getValue()));
		System.out.print("$t4 = " + registerFile.get(12).getValue());
		System.out.print('\n');
		// Fourth Row
		System.out.print(String.format("%-" + 16 + "s","$t5 = " + registerFile.get(13).getValue()));
		System.out.print(String.format("%-" + 16 + "s","$t6 = " + registerFile.get(14).getValue()));
		System.out.print(String.format("%-" + 16 + "s","$t7 = " + registerFile.get(15).getValue()));
		System.out.print("$s0 = " + registerFile.get(16).getValue());
		System.out.print('\n');
		// Fifth Row
		System.out.print(String.format("%-" + 16 + "s","$s1 = " + registerFile.get(17).getValue()));
		System.out.print(String.format("%-" + 16 + "s","$s2 = " + registerFile.get(18).getValue()));
		System.out.print(String.format("%-" + 16 + "s","$s3 = " + registerFile.get(19).getValue()));
		System.out.print("$s4 = " + registerFile.get(20).getValue());
		System.out.print('\n');
		// Sixth Row
		System.out.print(String.format("%-" + 16 + "s","$s5 = " + registerFile.get(21).getValue()));
		System.out.print(String.format("%-" + 16 + "s","$s6 = " + registerFile.get(22).getValue()));
		System.out.print(String.format("%-" + 16 + "s","$s7 = " + registerFile.get(23).getValue()));
		System.out.print("$t8 = " + registerFile.get(24).getValue());
		System.out.print('\n');
		// Seventh Row
		System.out.print(String.format("%-" + 16 + "s","$t9 = " + registerFile.get(25).getValue()));
		System.out.print(String.format("%-" + 16 + "s","$sp = " + registerFile.get(29).getValue()));
		System.out.print("$ra = " + registerFile.get(31).getValue());
		System.out.print('\n');
	}

	//nedd to put rest of prints
	private void pipeline(){
		System.out.println();
		System.out.println("pc      if/id   id/exe  exe/mem mem/wb");
		System.out.println(String.format("%-8s", progCount)
				+ String.format("%-8s", structs.get(0))
				+ String.format("%-8s", structs.get(1))
				+ String.format("%-8s", structs.get(2))
				+ String.format("%-8s", structs.get(3)));
		System.out.println();
	}

	private void runfull(){
		int decimal = 0;
		int decimal2 = 0;
		int decimal3 = 0;
		while(progCount != fileLines.size()){
			if(stall == true){
				loadedReg = -1;

				stall = false;

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, "stall");

				instructionCount ++;
				cycleCount ++;
			}else if (squash) {
				squash = false;
				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "squash");
				cycleCount++;
				progCount = targetPC;
			}else if(squashNum > 0) {
				if (squashNum != 1) {
					structs.set(3, structs.get(2));
					structs.set(2, structs.get(1));
					structs.set(1, structs.get(0));
					structs.set(0, programInst.get(progCount).getOperation());
					progCount++;
				} else {
					structs.set(3, structs.get(2));
					structs.set(2, "squash");
					structs.set(1, "squash");
					structs.set(0, "squash");
					progCount = targetPC;
				}
				cycleCount++;
				squashNum--;
			}
			else if (programInst.get(progCount).getOperation().contains("and") && !(programInst.get(progCount).getOperation().contains("andi"))){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);

				if (decimal2 == loadedReg || decimal3 == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "and");

				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()&registerFile.get(decimal3).getValue());

				progCount ++;
				cycleCount ++;
			}else if (programInst.get(progCount).getOperation().contains("or") && !(programInst.get(progCount).getOperation().contains("ori"))
					&& !(programInst.get(progCount).getOperation().contains("xor"))){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);

				if (decimal2 == loadedReg || decimal3 == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "or");

				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()|registerFile.get(decimal3).getValue());

				progCount ++;
				cycleCount ++;
			}else if (programInst.get(progCount).getOperation().contains("addi")){
				decimal = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = programInst.get(progCount).getImmediate();

				if (decimal2 == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "addi");

				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()+decimal3);

				progCount ++;
				cycleCount ++;
			}else if (programInst.get(progCount).getOperation().contains("add")){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);

				if (decimal2 == loadedReg || decimal3 == loadedReg) {
					stall = true;
				}else{
					instructionCount ++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "add");

				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()+registerFile.get(decimal3).getValue());

				progCount ++;
				cycleCount ++;
			}else if (programInst.get(progCount).getOperation().contains("sll")){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal3 = programInst.get(progCount).getImmediate();

				if (decimal2 == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "sll");

				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()<< decimal3);

				progCount ++;
				cycleCount ++;
			}else if (programInst.get(progCount).getOperation().contains("sub") && !(programInst.get(progCount).getOperation().contains("subi"))){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);

				if (decimal2 == loadedReg || decimal3 == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "sub");

				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()+registerFile.get(decimal3).getValue());

				progCount ++;
				cycleCount ++;
			}else if (programInst.get(progCount).getOperation().contains("slt")){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);

				if (decimal2 == loadedReg || decimal3 == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "slt");

				if(registerFile.get(decimal2).getValue()<registerFile.get(decimal3).getValue()){
					registerFile.get(decimal).setValue(1);
				}else{
					registerFile.get(decimal).setValue(0);
				}

				progCount ++;
				cycleCount ++;
			}else if (programInst.get(progCount).getOperation().contains("beq")){
				decimal = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal3 = programInst.get(progCount).getImmediate();

				if (decimal == loadedReg || decimal2 == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}


				if(registerFile.get(decimal).getValue() == registerFile.get(decimal2).getValue()){
					progCount ++;
					targetPC = progCount + decimal3;
					squashNum = 3;
				}else{
					progCount ++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "beq");

				cycleCount ++;
			}else if (programInst.get(progCount).getOperation().contains("bne")){
				decimal = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal3 = programInst.get(progCount).getImmediate();

				if (decimal == loadedReg || decimal2 == loadedReg) {
					stall = true;
				}else{
					instructionCount ++;
				}


				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "bne");

				if(registerFile.get(decimal).getValue() != registerFile.get(decimal2).getValue()){
					//System.out.println(decimal3);
					progCount ++;
					targetPC = progCount + decimal3;
					squashNum = 3;
				}else{
					progCount ++;
				}

				cycleCount ++;
			}else if (programInst.get(progCount).getOperation().contains("lw")){	//work on
				decimal = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = programInst.get(progCount).getImmediate();

				if (decimal2 == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}

				if(decimal != 0){
					loadedReg = decimal;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "lw");

				registerFile.get(decimal).setValue(memory[8191- (registerFile.get(decimal2).getValue()+decimal3)]);

				cycleCount ++;
				progCount ++;
			}else if (programInst.get(progCount).getOperation().contains("sw")){	//work on
				decimal = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = programInst.get(progCount).getImmediate();

				if (decimal == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "sw");


				memory[8191 - (registerFile.get(decimal2).getValue()+decimal3)] = registerFile.get(decimal).getValue();

				progCount ++;
				cycleCount ++;
			}else if (programInst.get(progCount).getOperation().contains("jal")){
				decimal3 = programInst.get(progCount).getImmediate();
				//System.out.println(decimal3);

				registerFile.get(31).setValue(progCount + 1);
				progCount++;
				targetPC = decimal3;

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "jal");
				squash = true;
				instructionCount++;
				cycleCount ++;
			}else if (programInst.get(progCount).getOperation().contains("jr")){
				decimal = Integer.parseInt(programInst.get(progCount).getRS(),2);

				progCount++;
				targetPC = registerFile.get(decimal).getValue();

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "jr");
				squash = true;
				instructionCount++;
				cycleCount ++;
			}else if (programInst.get(progCount).getOperation().contains("j")){
				decimal = programInst.get(progCount).getImmediate();
				progCount++;
				targetPC = decimal;

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "j");
				squash = true;
				instructionCount++;
				cycleCount ++;
			}
		}
	}

	private void runamount(int amount){
		int decimal = 0;
		int decimal2 = 0;
		int decimal3 = 0;
		while((progCount != fileLines.size()) && (amount != 0)){
			if(stall == true){
				loadedReg = -1;

				stall = false;

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, "stall");

				instructionCount ++;
				cycleCount ++;
				amount --;
			}else if (squash) {
				squash = false;
				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "squash");
				cycleCount++;
				progCount = targetPC;
				amount--;
			}else if(squashNum > 0){
				if (squashNum != 1){
					structs.set(3, structs.get(2));
					structs.set(2, structs.get(1));
					structs.set(1, structs.get(0));
					structs.set(0, programInst.get(progCount).getOperation());
					progCount++;
				}
				else{
					structs.set(3, structs.get(2));
					structs.set(2, "squash");
					structs.set(1, "squash");
					structs.set(0, "squash");
					progCount = targetPC;
				}
				cycleCount++;
				squashNum--;
				amount--;
			}else if (programInst.get(progCount).getOperation().contains("and") && !(programInst.get(progCount).getOperation().contains("andi"))){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);

				if (decimal2 == loadedReg || decimal3 == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "and");

				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()&registerFile.get(decimal3).getValue());

				progCount ++;
				cycleCount ++;
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("or") && !(programInst.get(progCount).getOperation().contains("ori"))
					&& !(programInst.get(progCount).getOperation().contains("xor"))){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);

				if (decimal2 == loadedReg || decimal3 == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "or");

				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()|registerFile.get(decimal3).getValue());

				progCount ++;
				cycleCount ++;
				amount --;

			}else if (programInst.get(progCount).getOperation().contains("addi")){
				decimal = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = programInst.get(progCount).getImmediate();

				if (decimal2 == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "addi");

				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()+decimal3);

				progCount ++;
				cycleCount ++;
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("add")){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);

				if (decimal2 == loadedReg || decimal3 == loadedReg) {
					stall = true;
				}else{
					instructionCount ++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "add");

				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()+registerFile.get(decimal3).getValue());

				progCount ++;
				cycleCount ++;
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("sll")){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal3 = programInst.get(progCount).getImmediate();

				if (decimal2 == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "sll");

				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()<< decimal3);

				progCount ++;
				cycleCount ++;
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("sub") && !(programInst.get(progCount).getOperation().contains("subi"))){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);

				if (decimal2 == loadedReg || decimal3 == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "sub");

				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()+registerFile.get(decimal3).getValue());

				progCount ++;
				cycleCount ++;
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("slt")){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);

				if (decimal2 == loadedReg || decimal3 == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "slt");

				if(registerFile.get(decimal2).getValue()<registerFile.get(decimal3).getValue()){
					registerFile.get(decimal).setValue(1);
				}else{
					registerFile.get(decimal).setValue(0);
				}

				progCount ++;
				cycleCount ++;
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("beq")){
				decimal = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal3 = programInst.get(progCount).getImmediate();

				if (decimal == loadedReg || decimal2 == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}


				if(registerFile.get(decimal).getValue() == registerFile.get(decimal2).getValue()){
					progCount ++;
					targetPC = progCount + decimal3;
					squashNum = 3;
				}else{
					progCount ++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "beq");

				cycleCount ++;
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("bne")){
				decimal = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal3 = programInst.get(progCount).getImmediate();

				if (decimal == loadedReg || decimal2 == loadedReg) {
					stall = true;
				}else{
					instructionCount ++;
				}


				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "bne");

				if(registerFile.get(decimal).getValue() != registerFile.get(decimal2).getValue()){
					//System.out.println(decimal3);
					progCount ++;
					targetPC = progCount + decimal3;
					squashNum = 3;
				}else{
					progCount ++;
				}

				cycleCount ++;
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("lw")){	//work on
				decimal = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = programInst.get(progCount).getImmediate();

				if (decimal2 == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}

				if(decimal != 0){
					loadedReg = decimal;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "lw");

				registerFile.get(decimal).setValue(memory[8191- (registerFile.get(decimal2).getValue()+decimal3)]);

				progCount ++;
				amount --;
				cycleCount ++;
			}else if (programInst.get(progCount).getOperation().contains("sw")){	//work on
				decimal = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = programInst.get(progCount).getImmediate();

				if (decimal == loadedReg) {
					stall = true;
				}else{
					instructionCount++;
				}

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "sw");


				memory[8191 - (registerFile.get(decimal2).getValue()+decimal3)] = registerFile.get(decimal).getValue();

				progCount ++;
				amount --;
				cycleCount ++;
			}else if (programInst.get(progCount).getOperation().contains("jal")){
				decimal3 = programInst.get(progCount).getImmediate();
				//System.out.println(decimal3);

				registerFile.get(31).setValue(progCount + 1);
				targetPC = decimal3;
				progCount++;

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "jal");
				squash = true;
				amount --;
				instructionCount++;
				cycleCount ++;
			}else if (programInst.get(progCount).getOperation().contains("jr")){
				decimal = Integer.parseInt(programInst.get(progCount).getRS(),2);

				progCount++;
				targetPC = registerFile.get(decimal).getValue();

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "jr");
				squash = true;
				amount --;
				instructionCount++;
				cycleCount ++;
			}else if (programInst.get(progCount).getOperation().contains("j")){
				decimal = programInst.get(progCount).getImmediate();

				progCount++;
				targetPC = decimal;

				structs.set(3, structs.get(2));
				structs.set(2, structs.get(1));
				structs.set(1, structs.get(0));
				structs.set(0, "j");
				squash = true;
				amount --;
				instructionCount++;
				cycleCount ++;
			}
		}
		pipeline();

	}
}