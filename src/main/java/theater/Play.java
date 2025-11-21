package theater;

/**
 * Represents a play in the theater.
 *
 * @null required by project Checkstyle configuration
 */
public class Play {

    private String name;
    private String type;

    public Play(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public Object getName() {
        return name;
    }
}
