package electory.utils;

public class LProperty<Type> {
	public boolean isActive;
	public Type prop;
	public Type origin;
	public LProperty(boolean par1, Type par2, Type par3) {
		this.isActive = par1;
		this.prop = par2;
		this.origin = par3;
	}
	
	public Type getValue() {
		return this.isActive ? this.prop : this.origin;
	}
	
	public LProperty<Type> setActive(boolean par1) {
		this.isActive = par1;
		return this;
	}
	
	public LProperty<Type> setProp(Type par1) {
		this.prop = par1;
		return this;
	}
	
	public LProperty<Type> setOrigin(Type par1) {
		this.origin = par1;
		return this;
	}
}
