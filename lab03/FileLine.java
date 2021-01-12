// Name:  Jesus Blanco and James Plasko
// Section:  CPE 315-03
import java.util.Map;

public class FileLine {

    private String instruction;
    private int address;

    public FileLine(String instruction, int address)
    {
        this.instruction = instruction;
        this.address = address;
    }

    public String getInstruction()
    {
        return this.instruction;
    }

    public int getAddress()
    {
        return this.address;
    }

    public  String[] findBinarythreereg(){
        int counter = 0;
        int overallcount = 0;
        String holder = "";
        String[] f = new String[3];

        //parses for instructions with three registers
        for(int i = 0; i <= (this.instruction).length(); i++){
            if((i == (this.instruction).length()) || ((this.instruction).charAt(i) == ',')
                    || ((this.instruction).charAt(i)) == '$'){
                counter ++;
            }

            if(counter == 1){
                holder = holder + (this.instruction).charAt(i);
            }else if(counter > 1){
                f[overallcount] = findregisternum(holder);
                overallcount ++;
                holder = "";
                counter = 0;
            }

        }

        return f;
    }

    public  String[] findBinarytworeg(){
        int counter = 0;
        int overallcount = 0;
        int commacount = 0;
        String holder = "";
        String[] f = new String[3];

        //parses for instruction with two registers and one imm
        for(int i = 0; i <= (this.instruction).length(); i++){
            if(i == (this.instruction).length()){
                commacount ++;
            }else if(((this.instruction).charAt(i)) == '$'){
                counter ++;
            }else if ((this.instruction).charAt(i) == ','){
                counter ++;
                commacount ++;
            }

            if(counter == 1){
                holder = holder + (this.instruction).charAt(i);
            }else if(counter > 1){
                f[overallcount] = findregisternum(holder);
                overallcount ++;
                holder = "";
                counter = 0;
            }

            if (commacount == 2){
                if((this.instruction).charAt(i) != ' ' && (this.instruction).charAt(i) != ','){
                    holder = holder + (this.instruction).charAt(i);
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

    public  String[] findstoreloadreg(){
        int counter = 0;
        int overallcount = 0;
        int commacount = 0;
        int dollarcount = 0;
        String holder = "";
        String[] f = new String[3];

        //parses instructon for store and load
        for(int i = 0; i < (this.instruction).length(); i++){
            if((this.instruction).charAt(i) == ')'){
                counter ++;
            }else if(((this.instruction).charAt(i)) == '$'){
                counter ++;
                dollarcount ++;
            }else if ((this.instruction).charAt(i) == ','){
                counter ++;
                commacount ++;
            }else if ((this.instruction).charAt(i) == '('){
                dollarcount ++;
            }

            if(counter == 1){
                holder = holder + (this.instruction).charAt(i);
            }else if(counter > 1){
                f[overallcount] = findregisternum(holder);
                overallcount ++;
                holder = "";
                counter = 0;
            }else if(dollarcount == 1){
                if((this.instruction).charAt(i) != ' '){
                    holder = holder + (this.instruction).charAt(i);
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

    public int findAddress(Map<String, Integer> lb){
        int counter = 0;
        String holder = "";
        String bits = "";
        int im = 0;

        //use counter to parse for label and then find the address number
        for(int i = 0; i <= (this.instruction).length(); i++){
            if(i == (this.instruction).length()){
                counter ++;
            }else if(((this.instruction).charAt(i)) == ' '){
                counter ++;
                i++;
            }

            if(counter == 1){
                holder = holder + (this.instruction).charAt(i);
            }else if(counter > 1){
                holder = holder + ':';
                //System.out.println(holder);
                im = lb.get(holder);
                //return String.format("%26s", Integer.toString(im,2)).replace(' ', '0');
				return im;

            }
        }

        return 666;
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
}
