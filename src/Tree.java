import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Created by kubut on 24.02.2016
 */
public class Tree {
    public class Node{
        private int level;
        private boolean isLeaf;
        private String path;
        private Color color;
        private Tree tree;

        public Node(Tree tree, int level, boolean isLeaf, String path){
            this.level = level;
            this.isLeaf = isLeaf;
            this.path = path;
            this.color = isLeaf ? ColorValue.leafColor : ColorValue.nodeColor;
            this.tree = tree;
        }

        public String getPath() {
            return path;
        }

        public String getKey() {
            return tree.key;
        }

        public String getValue() {
            return tree.value;
        }

        public int getLevel() {
            return level;
        }

        public boolean isLeaf() {
            return isLeaf;
        }

        public Color getColor() {
            return color;
        }
    }

    private String key = null;
    private String value = null;
    private LinkedHashMap<String, Tree> children;

    public Tree(){
        this.children = new LinkedHashMap<>();
    }

    public Tree(@NotNull String key, @NotNull String value) {
        this.key = key;
        this.children = new LinkedHashMap<>();
        this.value = value;
    }

    public void add(String key, String value){
        String childKey = this.getChildKey(key);
        String grandchildrenKey = this.getGrandchildrenKey(key);

        if(childKey == null){
            this.addChild(key, new Tree(key, value));
        } else {
            Tree childTree = this.children.get(childKey);

            if(childTree == null){
                this.addChild(childKey, new Tree(childKey, value));
                childTree = this.children.get(childKey);
            }

            if(grandchildrenKey != null){
                childTree.add(grandchildrenKey, value);
            }
        }
    }

    public String flatToString(){
        return this.flatToString(true);
    }

    public ArrayList<Node> flatToArrayList(){
        return this.flatToArrayList(0, "");
    }

    @Nullable
    public String getValueByPath(@NotNull String path){
        int lastDotIndex = path.lastIndexOf(".");

        if(lastDotIndex < 0){
            Tree child = this.children.get(path);

            return child == null ? null : child.value;
        } else {
            String childKey = path.substring(0, path.indexOf("."));
            String grandchildKey = path.substring(path.indexOf(".")+1);
            Tree child = this.children.get(childKey);
            if(child == null){
                return null;
            }
            return child.getValueByPath(grandchildKey);
        }
    }

    public void editValueByPath(@NotNull String path, @NotNull String value){
        int lastDotIndex = path.lastIndexOf(".");

        if(lastDotIndex < 0){
            Tree child = this.children.get(path);
            if(child == null){
                this.children.put(path, new Tree(path, value));
            }
            this.children.get(path).value = value;
        } else {
            String childKey = path.substring(0, path.indexOf("."));
            String grandchildKey = path.substring(path.indexOf(".")+1);
            Tree child = this.children.get(childKey);
            if(child != null){
                child.editValueByPath(grandchildKey, value);
            } else {
                this.add(path, value);
            }
        }
    }

    private String flatToString(boolean end){
        String flatted;
        boolean isLeaf = this.children.isEmpty();

        if(isLeaf){
            flatted = "\"" + this.key +"\" : \""+ this.value + "\"";
        } else {
            flatted = this.key == null ? "{\n" : "\"" +this.key + "\" : {\n";
            Iterator<Tree> it = this.children.values().iterator();
            while (it.hasNext()){
                flatted += it.next().flatToString(!it.hasNext());
            }
            flatted += "}";
        }

        flatted += end ? "\n" : ",\n";

        return flatted;
    }

    private ArrayList<Node> flatToArrayList(int level, @NotNull String path){
        ArrayList<Node> flatted = new ArrayList<>();

        if(this.key != null){
            path = path.isEmpty() ? this.key : path + "." + this.key;
            Node node = new Node(this, level, this.children.isEmpty(), path);
            flatted.add(node);
        }

        level++;

        Iterator<Tree> it = this.children.values().iterator();
        while (it.hasNext()){
            flatted.addAll(it.next().flatToArrayList(level, path));
        }

        return flatted;
    }

    @Nullable
    private String getChildKey(String key){
        int lastDotIndex = key.indexOf(".");
        return lastDotIndex < 0 ? null : key.substring(0, key.indexOf("."));
    }

    private String getGrandchildrenKey(String key){
        return key.substring(key.indexOf(".") + 1);
    }

    private void addChild(String key, Tree child){
        if(this.children.get(key) == null){
            this.children.put(key, child);
        }
    }
}
