// Name:  Jesus Blanco and James Plasko
// Section:  CPE 315-03

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

// Runs the Simulator
public class Simulator {
    private List<FileLine> fileLines; //FileLine Objects (Firstpass) -> Ideally we want to convert these to the Instruction objects
    private Map<String, Integer> programLabels; //Labels
	private List<Instruction> programInst;

    private List<Register> registerFile;
    private int progCount;
    private int[] memory;

    public Simulator(List<FileLine> program, Map <String, Integer> labels, List<Instruction> programI){
        this.fileLines = program;
        this.programLabels = labels;
		this.programInst = programI;

        this.registerFile = new ArrayList<>();
        this.progCount = 0;
        this.memory = new int[8192];
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
            else if (split[0].equals("s")){
                System.out.println("        1 instruction(s) executed");
                runamount(1);
            }
            else if (split[0].equals("r")){
				runfull();
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
                    System.out.println("        " + stepThrough +" instruction(s) executed");
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
        this.memory = new int[8192];
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
	
	private void runfull(){
		int decimal = 0;
		int decimal2 = 0;
		int decimal3 = 0;
		while(progCount != fileLines.size()){
			if (programInst.get(progCount).getOperation().contains("and") && !(programInst.get(progCount).getOperation().contains("andi"))){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				
				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()&registerFile.get(decimal3).getValue());
				
				progCount ++;
			}else if (programInst.get(progCount).getOperation().contains("or") && !(programInst.get(progCount).getOperation().contains("ori"))
					&& !(programInst.get(progCount).getOperation().contains("xor"))){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				
				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()|registerFile.get(decimal3).getValue());
				
				progCount ++;
			}else if (programInst.get(progCount).getOperation().contains("addi")){
				decimal = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = programInst.get(progCount).getImmediate();
				
				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()+decimal3);
				
				progCount ++;
			}else if (programInst.get(progCount).getOperation().contains("add")){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				
				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()+registerFile.get(decimal3).getValue());
				
				progCount ++;
			}else if (programInst.get(progCount).getOperation().contains("sll")){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal3 = programInst.get(progCount).getImmediate();
				
				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()<< decimal3);
				
				progCount ++;
			}else if (programInst.get(progCount).getOperation().contains("sub") && !(programInst.get(progCount).getOperation().contains("subi"))){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				
				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()+registerFile.get(decimal3).getValue());
				
				progCount ++;
			}else if (programInst.get(progCount).getOperation().contains("slt")){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				
				if(registerFile.get(decimal2).getValue()<registerFile.get(decimal3).getValue()){
					registerFile.get(decimal).setValue(1);
				}else{
					registerFile.get(decimal).setValue(0);
				}
				
				progCount ++;
			}else if (programInst.get(progCount).getOperation().contains("beq")){
				decimal = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal3 = programInst.get(progCount).getImmediate();
				
				if(registerFile.get(decimal).getValue() == registerFile.get(decimal2).getValue()){
					progCount ++;
					progCount = progCount + decimal3;
				}else{
					progCount ++;
				}
				
			}else if (programInst.get(progCount).getOperation().contains("bne")){
				decimal = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal3 = programInst.get(progCount).getImmediate();
				
				if(registerFile.get(decimal).getValue() != registerFile.get(decimal2).getValue()){
					progCount ++;
					progCount = progCount + decimal3;
				}else{
					progCount ++;
				}
				
			}else if (programInst.get(progCount).getOperation().contains("lw")){	//work on
				decimal = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = programInst.get(progCount).getImmediate();
				
				registerFile.get(decimal).setValue(memory[8191- (registerFile.get(decimal2).getValue()+decimal3)]);
				
				progCount ++;
			}else if (programInst.get(progCount).getOperation().contains("sw")){	//work on
				decimal = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = programInst.get(progCount).getImmediate();
				
				memory[8191 - (registerFile.get(decimal2).getValue()+decimal3)] = registerFile.get(decimal).getValue();
				
				progCount ++;
			}else if (programInst.get(progCount).getOperation().contains("jal")){
				decimal3 = programInst.get(progCount).getImmediate();
				
				registerFile.get(31).setValue(progCount + 1);
				progCount = decimal3;
				
			}else if (programInst.get(progCount).getOperation().contains("jr")){
				decimal = Integer.parseInt(programInst.get(progCount).getRS(),2);
				
				progCount = registerFile.get(decimal).getValue();
				
			}else if (programInst.get(progCount).getOperation().contains("j")){
				decimal = programInst.get(progCount).getImmediate();
				
				progCount = decimal;
				
			}
		}
	}
	
	private void runamount(int amount){
		int decimal = 0;
		int decimal2 = 0;
		int decimal3 = 0;
		while((progCount != fileLines.size()) && (amount != 0)){
			if (programInst.get(progCount).getOperation().contains("and") && !(programInst.get(progCount).getOperation().contains("andi"))){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				
				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()&registerFile.get(decimal3).getValue());
				
				progCount ++;
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("or") && !(programInst.get(progCount).getOperation().contains("ori"))
					&& !(programInst.get(progCount).getOperation().contains("xor"))){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				
				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()|registerFile.get(decimal3).getValue());
				
				progCount ++;
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("addi")){
				decimal = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = programInst.get(progCount).getImmediate();
				
				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()+decimal3);
				
				progCount ++;
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("add")){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				
				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()+registerFile.get(decimal3).getValue());
				
				progCount ++;
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("sll")){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal3 = programInst.get(progCount).getImmediate();
				
				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()<< decimal3);
				
				progCount ++;
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("sub") && !(programInst.get(progCount).getOperation().contains("subi"))){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				
				registerFile.get(decimal).setValue(registerFile.get(decimal2).getValue()+registerFile.get(decimal3).getValue());
				
				progCount ++;
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("slt")){
				decimal = Integer.parseInt(programInst.get(progCount).getRD(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				
				if(registerFile.get(decimal2).getValue()<registerFile.get(decimal3).getValue()){
					registerFile.get(decimal).setValue(1);
				}else{
					registerFile.get(decimal).setValue(0);
				}
				
				progCount ++;
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("beq")){
				decimal = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal3 = programInst.get(progCount).getImmediate();
				
				if(registerFile.get(decimal).getValue() == registerFile.get(decimal2).getValue()){
					progCount ++;
					progCount = progCount + decimal3;
				}else{
					progCount ++;
				}
				
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("bne")){
				decimal = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal3 = programInst.get(progCount).getImmediate();
				
				if(registerFile.get(decimal).getValue() != registerFile.get(decimal2).getValue()){
					//System.out.println(decimal3);
					progCount ++;
					progCount = progCount + decimal3;
				}else{
					progCount ++;
				}
				
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("lw")){	//work on
				decimal = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = programInst.get(progCount).getImmediate();
				
				registerFile.get(decimal).setValue(memory[8191- (registerFile.get(decimal2).getValue()+decimal3)]);
				
				progCount ++;
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("sw")){	//work on
				decimal = Integer.parseInt(programInst.get(progCount).getRT(),2);
				decimal2 = Integer.parseInt(programInst.get(progCount).getRS(),2);
				decimal3 = programInst.get(progCount).getImmediate();
				
				memory[8191 - (registerFile.get(decimal2).getValue()+decimal3)] = registerFile.get(decimal).getValue();
				
				progCount ++;
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("jal")){
				decimal3 = programInst.get(progCount).getImmediate();
				//System.out.println(decimal3);
				
				registerFile.get(31).setValue(progCount + 1);
				progCount = decimal3;
				
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("jr")){
				decimal = Integer.parseInt(programInst.get(progCount).getRS(),2);
				
				progCount = registerFile.get(decimal).getValue();
				
				amount --;
			}else if (programInst.get(progCount).getOperation().contains("j")){
				decimal = programInst.get(progCount).getImmediate();
				
				progCount = decimal;
				
				amount --;
			}
		}
	}
}
