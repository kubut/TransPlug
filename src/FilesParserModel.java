import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kubut on 21.02.2016
 */
public class FilesParserModel {
    private Tree keysTree;

    public FilesParserModel() {
        this.keysTree = new Tree(null, null);
    }

    public HashMap<String, String> parseJson(JsonObject json) {
        return this.parseJson(json, "");
    }

    public Tree getKeysTree(){
        return this.keysTree;
    }

    private HashMap<String, String> parseJson(JsonObject json, String prefix) {
        HashMap<String, String> translations = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String key = prefix + entry.getKey();
            this.keysTree.add(key);

            try {
                JsonObject subJson = entry.getValue().getAsJsonObject();
                translations.putAll(this.parseJson(subJson, key + "."));
            } catch (Exception e) {
                translations.put(key, entry.getValue().getAsString());
            }
        }

        return translations;
    }

    @NotNull
    private String getKeyParent(String key) {
        int lastDotIndex = key.lastIndexOf(".");
        return lastDotIndex < 0 ? "" : key.substring(0, key.lastIndexOf("."));
    }
}
