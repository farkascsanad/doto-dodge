package application;

public class Kotorek {

	static String[] jelzok = { "Kotokr�k", "Nyomor�k", "Ostoba", "NYOMOR����������K" };
	static String[] names = { "Whoopy", "FoaB�t", "Woa", "Whoopikaaa", "UPS", "HUNGAR�", "CSANIIII", "Pinky" };

	public static String generateString() {
		String name = names[(int) (Math.random() * names.length)];
		String jelz = jelzok[(int) (Math.random() * jelzok.length)];

		return jelz + " " + name;
	}
}
