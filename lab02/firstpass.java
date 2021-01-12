//Section 3
//Names: James Plasko 
//	Jesus Blanco

public class firstpass{

	private String instruction;
	private int address;

	public firstpass(String instruction, int address)
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
}