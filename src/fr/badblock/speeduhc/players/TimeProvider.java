package fr.badblock.speeduhc.players;

public interface TimeProvider {
	public String getId(int num);
	
	public int getTime(int num);

	public int getProvidedCount();
	
	public boolean displayed();
}
