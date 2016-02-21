import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kubut on 21.02.2016
 */
public class FilesParserModel {
    private ArrayList<String> keys;

    public FilesParserModel() {
        this.keys = new ArrayList<>();
    }

    public HashMap<String, String> parseJson(JsonObject json) {
        return this.parseJson(json, "");
    }

    public ArrayList<String> getKeys() {
        return this.keys;
    }

    private HashMap<String, String> parseJson(JsonObject json, String prefix) {
        HashMap<String, String> translations = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String key = prefix + entry.getKey();

            try {
                JsonObject subJson = entry.getValue().getAsJsonObject();
                this.parseJson(subJson, key + ".");
            } catch (Exception e) {
                this.addKeyIfUnique(key, this.getKeyParent(key));
            }
        }

        return translations;
    }

    private void addKeyIfUnique(String key, String node) {
        if (!this.keys.contains(key)) {
            if (node.isEmpty()) {
                this.keys.add(key);
            } else {
                int nodeIndex = this.getNodeIndex(node);
                if (nodeIndex < 0) {
                    this.addKeyIfUnique(key, this.getKeyParent(node));
                } else {
                    this.keys.add(nodeIndex + 1, key);
                }
            }
        }
    }

    @NotNull
    private String getKeyParent(String key) {
        int lastDotIndex = key.lastIndexOf(".");
        return lastDotIndex < 0 ? "" : key.substring(0, key.lastIndexOf("."));
    }

    private int getNodeIndex(String node) {
        int index = -1;

        for (int i = 0; i < this.keys.size(); i++) {
            String key = this.keys.get(i);
            int lastDotIndex = key.lastIndexOf(".");

            if (lastDotIndex > 0 && key.substring(0, lastDotIndex).equals(node)) {
                index = i;
            }
        }

        return index;
    }
}
