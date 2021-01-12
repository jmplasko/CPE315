public class Instruction {

    private FileLine line;
    private String operation;
    private String rd;
    private String rt;
    private String rs;
    private int immediate;

    public Instruction(FileLine line, String operation, 
						String rd, String rt, String rs, int immediate){
        this.line = line;
        this.operation = operation;
        this.rd = rd;
        this.rt = rt;
        this.rs = rs;
        this.immediate = immediate;
    }

    public FileLine getLine(){return this.line;}
    public String getOperation(){return this.operation;}
    public String getRD(){return this.rd;}
    public String getRT(){return this.rt;}
    public String getRS(){return this.rs;}
    public int getImmediate(){return this.immediate;}

    public void setLine(FileLine newValue){this.line = newValue;}
    public void setOperation(String newValue){this.operation = newValue;}
    public void setRD(String newValue){this.rd = newValue;}
    public void setRT(String newValue){this.rt = newValue;}
    public void setRS(String newValue){this.rs = newValue;}
    public void setImmediate(int newValue){this.immediate = newValue;}

}
