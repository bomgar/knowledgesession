package util;


public class Magic {

    public static int doMagic(int i) {
        if (Character.isAlphabetic(i)) {
            return i ^ ' ';
        } else {
            return i;
        }
    }
}
