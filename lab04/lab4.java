//Section 3
//Names: James Plasko
//		Jesus Blanco

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.ArrayList;
import java.lang.*;


public class lab4 {

    private static String getFilename(String[] args){
        if (args.length < 1){
            System.err.println("File not specified.");
            System.exit(1);
        }

        return args[0];
    }

    private static void processFile(final String filename , List<FileLine> ar, Map <String, Integer> lb) throws FileNotFoundException{
        try ( Scanner in = new Scanner(new File (filename))){
            processLines(in,ar,lb);
        }
    }

    private static void processLines(final Scanner input, List<FileLine> ar, Map <String, Integer> lb){
        int address = 0;
        while (input.hasNextLine())
        {
            String temp = input.nextLine();
            temp = temp.replaceAll("#.+", "");

            String removed = temp.replaceAll("\\s", "");
            if (temp.equals("#")){
                ///do nothing
            }
            else if (removed.length() != 0){

                boolean isLabel = removed.indexOf(":") != -1? true: false;

                if ( (isLabel) && (!removed.endsWith(":")) ){
                    String [] split = temp.split("\\:");

                    lb.put(split[0] + ':', address);
                    ar.add(new FileLine(split[1].trim(), address));
                    address++;
                }
                else{
                    if (!removed.endsWith(":")){
                        ar.add(new FileLine(temp.trim(), address));
                        address++;
                    }
                    else{

                        lb.put(temp.trim(), address);
                    }
                }
            }
        }
    }

    private static void processScriptFile(final String filename, List<String> commands) throws FileNotFoundException{
        try ( Scanner in = new Scanner(new File (filename))){
            processScriptLines(in, commands);
        }
    }

    private static void processScriptLines(final Scanner input, List<String> commands){
        while (input.hasNextLine()){
            String temp = input.nextLine();
            //String removed = temp.replaceAll("\\s", "");
            commands.add(temp);
        }
    }

    public static Instruction instructionDecider(FileLine obj, Map <String, Integer> lb){

        String[] first = new String[3];
        int found = 0;
		Instruction newinst = new Instruction(obj, "", "", "", "", 0);

        //this is pretty strait forward
        //each if statement is special to the instruction
        if (obj.getInstruction().contains("and") && !(obj.getInstruction().contains("andi"))){
            first = obj.findBinarythreereg();
			newinst.setOperation("and");
			newinst.setRD(first[0]);
			newinst.setRS(first[1]);
			newinst.setRT(first[2]);
            //System.out.println("000000 "+first[1]+" "+first[2]+" "+first[0]+" 00000 100100");

        }else if (obj.getInstruction().contains("or") && !(obj.getInstruction().contains("ori"))
                && !(obj.getInstruction().contains("xor"))){
            first = obj.findBinarythreereg();
			newinst.setOperation("or");
			newinst.setRD(first[0]);
			newinst.setRS(first[1]);
			newinst.setRT(first[2]);
            //System.out.println("000000 "+first[1]+" "+first[2]+" "+first[0]+" 00000 100101");

        }else if (obj.getInstruction().contains("addi")){
            first = obj.findBinarytworeg();
            int im = Integer.parseInt(first[2]);
			newinst.setOperation("addi");
			newinst.setRT(first[0]);
			newinst.setRS(first[1]);
			newinst.setImmediate(im);
            //System.out.println("001000 "+first[1]+" "+first[0]+" "
            //        +twoComp(im));

        }else if (obj.getInstruction().contains("add")){
            first = obj.findBinarythreereg();
			newinst.setOperation("add");
			newinst.setRD(first[0]);
			newinst.setRS(first[1]);
			newinst.setRT(first[2]);
            //System.out.println("000000 "+first[1]+" "+first[2]+" "+first[0]+" 00000 100000");

        }else if (obj.getInstruction().contains("sll")){
            first = obj.findBinarytworeg();
            int im = Integer.parseInt(first[2]);
			newinst.setOperation("sll");
			newinst.setRD(first[0]);
			newinst.setRT(first[1]);
			newinst.setImmediate(im);
            //System.out.println("000000 00000 "+first[1]+" "+first[0]+" "
            //       +twoComp(im).substring(11)+" 000000");

        }else if (obj.getInstruction().contains("sub") && !(obj.getInstruction().contains("subi"))){
            first = obj.findBinarythreereg();
			newinst.setOperation("sub");
			newinst.setRD(first[0]);
			newinst.setRS(first[1]);
			newinst.setRT(first[2]);
            //System.out.println("000000 "+first[1]+" "+first[2]+" "+first[0]+" 00000 100010");

        }else if (obj.getInstruction().contains("slt")){
            first = obj.findBinarythreereg();
			newinst.setOperation("slt");
			newinst.setRD(first[0]);
			newinst.setRS(first[1]);
			newinst.setRT(first[2]);
            //System.out.println("000000 "+first[1]+" "+first[2]+" "+first[0]+" 00000 101010");

        }else if (obj.getInstruction().contains("beq")){ //extea
            first = obj.findBinarytworeg();
            first[2] = first[2] + ':';
            found = lb.get(first[2]) - (obj.getAddress() + 1);
			newinst.setOperation("beq");
			newinst.setRS(first[0]);
			newinst.setRT(first[1]);
			newinst.setImmediate(found);
            //System.out.println("000100 "+first[0]+" "+first[1]+" "
            //        +twoComp(found));

        }else if (obj.getInstruction().contains("bne")){ //extra
            first = obj.findBinarytworeg();
            first[2] = first[2] + ':';
            found = lb.get(first[2]) - (obj.getAddress() + 1);
			newinst.setOperation("bne");
			newinst.setRS(first[0]);
			newinst.setRT(first[1]);
			newinst.setImmediate(found);
            //System.out.println("000101 "+first[0]+" "+first[1]+" "
            //        +twoComp(found));

        }else if (obj.getInstruction().contains("lw")){
            first = obj.findstoreloadreg();
            int im = Integer.parseInt(first[1]);
			newinst.setOperation("lw");
			newinst.setRT(first[0]);
			newinst.setRS(first[2]);
			newinst.setImmediate(im);
            //System.out.println("100011 "+first[2]+" "+first[0]+" "
            //        +twoComp(im));

        }else if (obj.getInstruction().contains("sw")){
            first = obj.findstoreloadreg();
            int im = Integer.parseInt(first[1]);
			newinst.setOperation("sw");
			newinst.setRT(first[0]);
			newinst.setRS(first[2]);
			newinst.setImmediate(im);
            //System.out.println("101011 "+first[2]+" "+first[0]+" "
            //        +twoComp(im));

        }else if (obj.getInstruction().contains("jal")){
			newinst.setOperation("jal");
			newinst.setImmediate(obj.findAddress(lb));
            //System.out.println("000011 " + obj.findAddress(lb));

        }else if (obj.getInstruction().contains("jr")){

            int counter = 0;
            int overallcount = 0;
            String holder = "";

            for(int i = 0; i <= (obj.getInstruction()).length(); i++){
                if((i == (obj.getInstruction()).length()) || ((obj.getInstruction()).charAt(i)) == '$'){
                    counter ++;
                }

                if(counter == 1){
                    holder = holder + (obj.getInstruction()).charAt(i);
                }
            }

            holder = obj.findregisternum(holder);
			newinst.setOperation("jr");
			newinst.setRS(holder);
            //System.out.println("000000 "+ holder +" 000000000000000 001000");

        }else if (obj.getInstruction().contains("j")){
			newinst.setOperation("j");
			newinst.setImmediate(obj.findAddress(lb));
            //System.out.println("000010 " + obj.findAddress(lb));

        }else if (obj.getInstruction().contains("#")){
            //for special case where there is leftover # in array
        }else{

            //this parses for when no vald instruction is given
            int counter = 0;
            String holder = "";

            for(int i = 0; i < (obj.getInstruction()).length(); i++){
                if((obj.getInstruction().charAt(i) == ' ' || obj.getInstruction().charAt(i) == '$')
                        && (holder.length() != 0)){
                    counter ++;
                }else if(counter == 0){
                    holder = holder + (obj.getInstruction()).charAt(i);
                }
            }
            System.out.println("invalid instruction: " + holder);
            System.exit(0);
        }
    
		return newinst;
	
	}

    public static String twoComp(int stuff){

        //2's complement
        //if negative it does clever math to get 2's binary
        if (stuff < 0){
            return String.format("%16s", Integer.toString((65535 + 1 + stuff),2)).replace(' ', '0');
        }else{
            return String.format("%16s", Integer.toString(stuff,2)).replace(' ', '0');
        }
    }

    public static void main(String[] args){

        final String filename = getFilename(args);
        List<FileLine> arrayList = new ArrayList<FileLine>();		//array of firstpass objects
        Map <String, Integer> labels = new HashMap<String, Integer>();	//map holds label and address #
		List<Instruction> instList = new ArrayList<Instruction>();

        try{
            processFile(filename, arrayList, labels);
        }
        catch (FileNotFoundException e){
            System.err.println(e.getMessage());
        }
		
		for (int i = 0; i < arrayList.size(); i++) {	//gets opcode for each instruction
			instList.add(instructionDecider(arrayList.get(i), labels));
		}

        Scanner prompt = new Scanner(System.in);
        Simulator program = new Simulator(arrayList, labels, instList);
        program.initializeRegisters();
        Boolean running = true;
        if (args.length == 1) {
            while (running) {
                System.out.print("mips> ");
                String input = prompt.nextLine();
                running = program.interpretInput(input);
            }
        }
        else if (args.length == 2){
            final String scriptFileName = args[1];
            List<String> commands = new ArrayList<String>();
            try {
                processScriptFile(scriptFileName, commands);
            }
            catch (FileNotFoundException e) {
                System.err.println(e.getMessage());
            }
            while (running) {
                for (int i = 0; i < commands.size(); i++) {
                    System.out.print("mips> " + commands.get(i) + "\n");
                    running = program.interpretInput(commands.get(i));
                }
            }

        }


    }

}
