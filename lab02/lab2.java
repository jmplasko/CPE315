//Section 3
//Names: James Plasko 
//	Jesus Blanco

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map;
import java.io.*;
import java.lang.*; 


public class lab2 {
    
    private static String getFilename(String[] args){
        if (args.length < 1){
            System.err.println("File not specified.");
            System.exit(1);
        }
        
        return args[0];
    }
    
    private static void processFile(final String filename , List<firstpass> ar, Map <String, Integer> lb) throws FileNotFoundException{
        try ( Scanner in = new Scanner(new File (filename))){
            processLines(in,ar,lb);
        }
    }
    
	private static void processLines(final Scanner input, List<firstpass> ar, Map <String, Integer> lb){
        int address = 0;
        while (input.hasNextLine())
        {
            String temp = input.nextLine();
            temp = temp.replaceAll("#.+", "");
            
            String removed = temp.replaceAll("\\s", "");
            
            if (removed.length() != 0){
                
                boolean isLabel = removed.indexOf(":") != -1? true: false;
                
                if ( (isLabel == true) && (removed.endsWith(":") == false) ){
                    String [] split = temp.split("\\:");
					
					lb.put(split[0] + ':', address);
					ar.add(new firstpass(split[1].trim(), address));
                    address++;
                }
                else{
                    if (removed.endsWith(":") == false){
						ar.add(new firstpass(temp.trim(), address));
                        address++;
                    }
                    else{
						
						lb.put(temp.trim(), address);
                    }
                }
            }
        }
    }
	
	public static void instructionDecider(firstpass obj, Map <String, Integer> lb){
		
		String[] first = new String[3];
		int found = 0;
		
		//this is pretty strait forward
		//each if statement is special to the instruction 
		if (obj.getInstruction().contains("and") && !(obj.getInstruction().contains("andi"))){
			first = findBinarythreereg(obj);
			System.out.println("000000 "+first[1]+" "+first[2]+" "+first[0]+" 00000 100100");
			
		}else if (obj.getInstruction().contains("or") && !(obj.getInstruction().contains("ori"))
					&& !(obj.getInstruction().contains("xor"))){
			first = findBinarythreereg(obj);
			System.out.println("000000 "+first[1]+" "+first[2]+" "+first[0]+" 00000 100101");
		
		}else if (obj.getInstruction().contains("addi")){
			first = findBinarytworeg(obj);
			int im = Integer.parseInt(first[2]);
			System.out.println("001000 "+first[1]+" "+first[0]+" "
			+twoComp(im));
			
		}else if (obj.getInstruction().contains("add")){
			first = findBinarythreereg(obj);
			System.out.println("000000 "+first[1]+" "+first[2]+" "+first[0]+" 00000 100000");
				
		}else if (obj.getInstruction().contains("sll")){
			first = findBinarytworeg(obj);
			int im = Integer.parseInt(first[2]);
			System.out.println("000000 00000 "+first[1]+" "+first[0]+" "
			+twoComp(im).substring(11)+" 000000");
			
		}else if (obj.getInstruction().contains("sub") && !(obj.getInstruction().contains("subi"))){
			first = findBinarythreereg(obj);
			System.out.println("000000 "+first[1]+" "+first[2]+" "+first[0]+" 00000 100010");
			
		}else if (obj.getInstruction().contains("slt")){
			first = findBinarythreereg(obj);
			System.out.println("000000 "+first[1]+" "+first[2]+" "+first[0]+" 00000 101010");
			
		}else if (obj.getInstruction().contains("beq")){ //extea
			first = findBinarytworeg(obj);
			first[2] = first[2] + ':';
			found = lb.get(first[2]) - (obj.getAddress() + 1);
			System.out.println("000100 "+first[0]+" "+first[1]+" "
			+twoComp(found));
			
		}else if (obj.getInstruction().contains("bne")){ //extra
			first = findBinarytworeg(obj);
			first[2] = first[2] + ':';
			found = lb.get(first[2]) - (obj.getAddress() + 1);
			System.out.println("000101 "+first[0]+" "+first[1]+" "
			+twoComp(found));
			
		}else if (obj.getInstruction().contains("lw")){
			first = findstoreloadreg(obj);
			int im = Integer.parseInt(first[1]);
			System.out.println("100011 "+first[2]+" "+first[0]+" "
			+twoComp(im));
			
		}else if (obj.getInstruction().contains("sw")){
			first = findstoreloadreg(obj);
			int im = Integer.parseInt(first[1]);
			System.out.println("101011 "+first[2]+" "+first[0]+" "
			+twoComp(im));
		
		}else if (obj.getInstruction().contains("jal")){
			System.out.println("000011 " + findAddress(obj, lb));
			
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
			
			holder = findregisternum(holder);
			
			System.out.println("000000 "+ holder +" 000000000000000 001000");
		
		}else if (obj.getInstruction().contains("j")){
			System.out.println("000010 " + findAddress(obj, lb));
			
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
	
	public static String[] findBinarythreereg(firstpass obj){
		int counter = 0;
		int overallcount = 0;
		String holder = "";
		String[] f = new String[3];
		
		//parses for instructions with three registers
		for(int i = 0; i <= (obj.getInstruction()).length(); i++){
			if((i == (obj.getInstruction()).length()) || ((obj.getInstruction()).charAt(i) == ',')
				|| ((obj.getInstruction()).charAt(i)) == '$'){
				counter ++;
			}
				
			if(counter == 1){
				holder = holder + (obj.getInstruction()).charAt(i);
			}else if(counter > 1){
				f[overallcount] = findregisternum(holder);
				overallcount ++;
				holder = "";
				counter = 0;
			}
				
		}
		
		return f;
	}
	
	public static String[] findBinarytworeg(firstpass obj){
		int counter = 0;
		int overallcount = 0;
		int commacount = 0;
		String holder = "";
		String[] f = new String[3];
		
		//parses for instruction with two registers and one imm
		for(int i = 0; i <= (obj.getInstruction()).length(); i++){
			if(i == (obj.getInstruction()).length()){
				commacount ++;
			}else if(((obj.getInstruction()).charAt(i)) == '$'){
				counter ++;
			}else if ((obj.getInstruction()).charAt(i) == ','){
				counter ++;
				commacount ++;
			}
				
			if(counter == 1){
				holder = holder + (obj.getInstruction()).charAt(i);
			}else if(counter > 1){
				f[overallcount] = findregisternum(holder);
				overallcount ++;
				holder = "";
				counter = 0;
			}
			
			if (commacount == 2){
				if((obj.getInstruction()).charAt(i) != ' ' && (obj.getInstruction()).charAt(i) != ','){
					holder = holder + (obj.getInstruction()).charAt(i);
				}
			}else if(commacount > 2){
				f[overallcount] = holder;
				overallcount ++;
				holder = "";
				counter = 0;
			}
				
		}
		return f;
	}
	
	public static String[] findstoreloadreg(firstpass obj){
		int counter = 0;
		int overallcount = 0;
		int commacount = 0;
		int dollarcount = 0;
		String holder = "";
		String[] f = new String[3];
		
		//parses instructon for store and load
		for(int i = 0; i < (obj.getInstruction()).length(); i++){
			if((obj.getInstruction()).charAt(i) == ')'){
				counter ++;
			}else if(((obj.getInstruction()).charAt(i)) == '$'){
				counter ++;
				dollarcount ++;
			}else if ((obj.getInstruction()).charAt(i) == ','){
				counter ++;
				commacount ++;
			}else if ((obj.getInstruction()).charAt(i) == '('){
				dollarcount ++;
			}
				
			if(counter == 1){
				holder = holder + (obj.getInstruction()).charAt(i);
			}else if(counter > 1){
				f[overallcount] = findregisternum(holder);
				overallcount ++;
				holder = "";
				counter = 0;
			}else if(dollarcount == 1){
				if((obj.getInstruction()).charAt(i) != ' '){
					holder = holder + (obj.getInstruction()).charAt(i);
				}
			}else if(dollarcount > 1){
				f[overallcount] = holder;
				overallcount ++;
				holder = "";
				dollarcount = 0;
			}		
		}
		return f;
	}
	
	public static String findAddress(firstpass obj, Map <String, Integer> lb){
		int counter = 0;
		String holder = "";
		String bits = "";
		int im = 0;
		
		//use counter to parse for label and then find the address number
		for(int i = 0; i <= (obj.getInstruction()).length(); i++){
			if(i == (obj.getInstruction()).length()){
				counter ++;	
			}else if(((obj.getInstruction()).charAt(i)) == ' '){
				counter ++;
				i++;
			}
				
			if(counter == 1){
				holder = holder + (obj.getInstruction()).charAt(i);
			}else if(counter > 1){
				holder = holder + ':';
				//System.out.println(holder);
				im = lb.get(holder);
				return String.format("%26s", Integer.toString(im,2)).replace(' ', '0');
				
			}
		}
		
		return "broke";
	}
	
	public static String findregisternum(String decifer){
		//Just finds the correct binary string for register
		if (decifer.equals("$r0")){
			return "00000";
		}else if (decifer.equals("$v0")){
			return "00010";
		}else if (decifer.equals("$v1")){
			return "00011";
		}else if (decifer.equals("$a0")){
			return "00100";
		}else if (decifer.equals("$a1")){
			return "00101";
		}else if (decifer.equals("$a2")){
			return "00110";
		}else if (decifer.equals("$a3")){
			return "00111";
		}else if (decifer.equals("$t0")){
			return "01000";
		}else if (decifer.equals("$t1")){
			return "01001";
		}else if (decifer.equals("$t2")){
			return "01010";
		}else if (decifer.equals("$t3")){
			return "01011";
		}else if (decifer.equals("$t4")){
			return "01100";
		}else if (decifer.equals("$t5")){
			return "01101";
		}else if (decifer.equals("$t6")){
			return "01110";
		}else if (decifer.equals("$t7")){
			return "01111";
		}else if (decifer.equals("$s0")){
			return "10000";
		}else if (decifer.equals("$s1")){
			return "10001";
		}else if (decifer.equals("$s2")){
			return "10010";
		}else if (decifer.equals("$s3")){
			return "10011";
		}else if (decifer.equals("$s4")){
			return "10100";
		}else if (decifer.equals("$s5")){
			return "10101";
		}else if (decifer.equals("$s6")){
			return "10110";
		}else if (decifer.equals("$s7")){
			return "10111";
		}else if (decifer.equals("$t8")){
			return "11000";
		}else if (decifer.equals("$t9")){
			return "11001";
		}else if (decifer.equals("$s8")){
			return "11110";
		}else if (decifer.equals("$ra")){
			return "11111";
		}else if (decifer.equals("$sp")){
			return "11101";
		}else if (decifer.equals("$0")){
			return "00000";
		}else if (decifer.equals("$zero")){
			return "00000";
		}
		
		return "Broke";
	}
    
    public static void main(String[] args){
        
        final String filename = getFilename(args);
		List<firstpass> arrayList = new ArrayList<firstpass>();		//array of firstpass objects
		Map <String, Integer> labels = new HashMap<String, Integer>();	//map holds label and address #
        
        try{
            processFile(filename, arrayList, labels);
        }
        catch (FileNotFoundException e){
            System.err.println(e.getMessage());
        }
		
		 for (int i = 0; i < arrayList.size(); i++) {	//gets opcode for each instruction
			instructionDecider(arrayList.get(i), labels);
		 }
		
    }

}