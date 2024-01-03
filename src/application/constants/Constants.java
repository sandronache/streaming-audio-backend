package application.constants;

/**
 * Class for constants
 */
public final class Constants {
    public static final int THREE = 3;
    public static final int FIVE = 5;
    public static final int FOUR = 4;
    public static final int NINETY = 90;
    /**
     * Default constructor modified to avoid unwanted behavior
     */
    private Constants() {
        throw new AssertionError("Utility class - do not instantiate!");
    }
}
