package application;

public class Kotorek {

	static String[] jelzok = { "KotokrÈk", "NyomorÈk", "Ostoba", "NYOMOR…………………………K" };
	static String[] names = { "Whoopy", "FoaBÌt", "Woa", "Whoopikaaa", "UPS", "HUNGAR”", "CSANIIII", "Pinky" };

	public static String generateString() {
		String name = names[(int) (Math.random() * names.length)];
		String jelz = jelzok[(int) (Math.random() * jelzok.length)];

		return jelz + " " + name;
	}
}
