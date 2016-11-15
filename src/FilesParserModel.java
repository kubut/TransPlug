import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

/**
 * Created by kubut on 21.02.2016
 */
public class FilesParserModel {
    private static Tree keysTree;

    public FilesParserModel(){
        keysTree = new Tree();
    }

    public Tree parseJson(JsonObject json) {
        Tree tree = new Tree();
        this.fillTreeByJsonData(json, "", tree);
        return tree;
    }

    public Tree getKeysTree(){
        return keysTree;
    }

    private void fillTreeByJsonData(JsonObject json, String prefix, Tree tree){
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String key = prefix + entry.getKey();

            try {
                JsonObject subJson = entry.getValue().getAsJsonObject();
                keysTree.add(key, entry.getKey(), false);

                this.fillTreeByJsonData(subJson, key + ".", tree);
            } catch (Exception e) {
                keysTree.add(key, entry.getKey(), true);

                tree.add(key, entry.getValue().getAsString());
            }
        }
    }
}
