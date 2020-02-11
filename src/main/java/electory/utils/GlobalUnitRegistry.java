package electory.utils;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import electory.block.Block;
import electory.item.Item;

public class GlobalUnitRegistry {
	private static ConcurrentHashMap<Unit, IUnit> unitMap = new ConcurrentHashMap<>();
	
	private GlobalUnitRegistry(Unit par1, IUnit par2) {
		if (unitMap.containsKey(par1)) unitMap.replace(par1, par2);
		else unitMap.put(par1, par2);
	}
	
	public static ConcurrentHashMap<Unit, IUnit> getMap() {
		return unitMap;
	}
	
	public static void registerUnit(Unit par1, IUnit par2) {
		unitMap.put(par1, par2);
	}
	
	public static void registerUnit(int par1, IUnit par2) {
		unitMap.put(new Unit(par1), par2);
	}
	
	public static void registerUnit(int par1, int par2, IUnit par3) {
		unitMap.put(new Unit(par1, par2), par3);
	}
	
	/**
	 * Working with id less than 32768‬
	 */
	public static void registerUnitNext(IUnit par1) {
		for (int i = 0; i != 32768; i++) {
			if (getUnitWithID(i) == null) {
				registerUnit(new Unit(i), par1);
			}
		}
	}
	
	/**
	 * Working with subid less than 8196
	 */
	public static void registerUnitSubNext(int par1, IUnit par2) {
		if (getIUnitWithID(par1) != null) {
			IUnit[] var1 = getIUnitsWithID(par1);
			for (int i = 1; i != 8196; i++) {
				Unit var3 = getUnit(var1[i]);
				if (var3.subid == i) {
					continue;
				} else {
					registerUnit(new Unit(par1, i), par2);
					return;
				}
			}
		} else {
			registerUnitNext(par2);
		}
	}
	
	public static void unregisterUnit(Unit par1) {
		unitMap.remove(par1);
	}
	
	public static IUnit getIUnit(Unit par1) {
		return unitMap.get(par1);
	}
	
	public static Unit getUnit(IUnit par1) {
		for (Unit var1 : unitMap.keySet()) {
			if (unitMap.get(var1).equals(par1)) {
				return var1;
			}
		}
		return null;
	}
	
	public static Unit getUnitWithID(int par1) {
		for (Unit var1 : unitMap.keySet()) {
			if (var1.id == par1) {
				return var1;
			}
		}
		return null;
	}
	
	public static IUnit getIUnitWithID(int par1) {
		for (Unit var1 : unitMap.keySet()) {
			if (var1.id == par1) {
				return unitMap.get(var1);
			}
		}
		return null;
	}

	public static IUnit[] getIUnitsWithID(int par1) {
		IUnit[] var1 = new IUnit[8192];
		int var2 = 0;
		for (Unit var3 : unitMap.keySet()) {
			if (var3.id == par1) {
				var1[var2] = unitMap.get(var3);
				var2++;
			}
		}
		return Arrays.copyOf(var1, var2 + 1);
	}
	
	/**
	 * maximum id for all blocks - 1 048 576‬
	 * @return Array of all registered blocks from 1 to 1048576
	 */
	public static Block[] getAllBlocks() {
		Block[] var1 = new Block[1048576];
		int var2 = 0;
		for (Unit var3 : unitMap.keySet()) {
			if (unitMap.get(var3) instanceof Block) {
				var1[var2] = (Block)unitMap.get(var3);
				var2++;
			}
		}
		return Arrays.copyOf(var1, var2 + 1);
	}
	
	/**
	 * maximum id for all items - 1 048 576‬
	 * @return Array of all registered items from 1 to 1048576
	 */
	public static Item[] getAllItems() {
		Item[] var1 = new Item[1048576];
		int var2 = 0;
		for (Unit var3 : unitMap.keySet()) {
			if (unitMap.get(var3) instanceof Item) {
				var1[var2] = (Item)unitMap.get(var3);
				var2++;
			}
		}
		return Arrays.copyOf(var1, var2 + 1);
	}
}
