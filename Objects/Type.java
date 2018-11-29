package Objects;

public enum Type {
    MOVIE("movie"),
    IMAGE("image"),
    TEXT("text"),
    PDF("pdf"),
    AUDIO("audio");

    private String text;
    Type(String text){
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static Type fromString(String text) {
        for (Type b : Type.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}