package edu.whu.cgf.geoportal.util.gridsystem;

/**
 * @author lyx
 */
public class ContentEmbedding {
    private String key;
    private String value;

    public ContentEmbedding(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return this.key + "_" + this.value;
    }
}
