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
        private String key;
        private int level;
        private boolean isLeaf;
        private String path;
        private Color color;

        public Node(String key, int level, boolean isLeaf, String path){
            this.key = key;
            this.level = level;
            this.isLeaf = isLeaf;
            this.path = path;
            this.color = isLeaf ? ColorValue.leafColor : ColorValue.nodeColor;
        }

        public String getPath() {
            return path;
        }

        public String getKey() {
            return key;
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

    private String key;
    private LinkedHashMap<String, Tree> children;
    private Tree parent;
    private int size = 0;
    private static int searchIndex = 0;

    public Tree(@Nullable String key, @Nullable Tree parent) {
        this.key = key;
        this.children = new LinkedHashMap<>();
        this.parent = parent;
    }

    public void add(String key){
        String childKey = this.getChildKey(key);
        String grandchildrenKey = this.getGrandchildrenKey(key);

        if(childKey == null){
            this.addChild(key, new Tree(key, this));
        } else {
            Tree childTree = this.children.get(childKey);

            if(childTree == null){
                this.addChild(childKey, new Tree(childKey, this));
                childTree = this.children.get(childKey);
            }

            if(grandchildrenKey != null){
                childTree.add(grandchildrenKey);
            }
        }
    }

    @Nullable
    public Node getKeyByIndex(final int index){
        searchIndex = index;
        return this.getKeyByIndex("", 0);
    }

    // TODO: change it to valid json
    public String flatToString(){
        String flatted = this.key == null ? "{" : "\"" +this.key + "\" : {";
        Iterator<Tree> it = this.children.values().iterator();
        while (it.hasNext()){
            flatted += it.next().flatToString();
        }
        flatted += " }, \n";

        return flatted;
    }

    public ArrayList<Node> flatToArrayList(){
        return this.flatToArrayList(0, "");
    }

    public void incSize(){
        this.size++;
        if(this.parent != null){
            this.parent.incSize();
        }
    }

    public int getSize(){
        return this.size;
    }

    private ArrayList<Node> flatToArrayList(int level, @NotNull String path){
        ArrayList<Node> flatted = new ArrayList<>();

        if(this.key != null){
            path = path.isEmpty() ? this.key : path + "." + this.key;
            Node node = new Node(this.key, level, this.children.isEmpty(), path);
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
    private Node getKeyByIndex(@Nullable  String path, int level){
        path = path == null ? "" : path;

        if(searchIndex == 0){
            path = path.isEmpty() ? this.key : path + "." + this.key;
            return new Node(this.key, level, this.children.isEmpty(), path);
        }

        level++;

        Iterator<Tree> it = this.children.values().iterator();
        while (it.hasNext()){
            searchIndex--;
            Node node = it.next().getKeyByIndex(path.isEmpty() ? this.key : path+"."+this.key, level);

            if(node != null){
                return node;
            }
        }

        return null;
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
            this.incSize();
        }
    }
}
