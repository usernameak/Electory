package electory.utils;

public class Unit {
	public int id;
	public int subid;
	
	public Unit(int par1) {
		this.id = par1;
		this.subid = 0;
	}
	
	public Unit(int par1, int par2) {
		this(par1);
		this.subid = par2;
	}
	
	@Override
	public boolean equals(Object par1) {
		if (par1 instanceof Unit) {
			return ((Unit)par1).id == this.id && ((Unit)par1).subid == this.subid;
		}
		return false;
	}
}
