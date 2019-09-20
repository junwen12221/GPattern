package cn.lightfish;

public class TextItem extends Item {

    public TextItem(String pettern, String code) {
        super(pettern, code);
    }

    @Override
    public String toString() {
        return "TextItem{" +
                "pettern='" + pettern + '\'' +
                ", code='" + code + '\'' +
                ", instruction=" + instruction +
                '}';
    }
}