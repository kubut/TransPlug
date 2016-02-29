import com.google.gson.JsonObject;

import javax.swing.event.TableModelListener;
import java.util.*;

/**
 * Created by kubut on 21.02.2016
 */
public class TransTableModel implements javax.swing.table.TableModel{
    private ArrayList<String> languages;
    private FilesService filesService;
    private HashMap<String, Tree> translations;
    private ArrayList<Tree.Node> mergedKeys;

    public TransTableModel(FilesService filesService){
        this.filesService = filesService;
        this.translations = new HashMap<>();

        this.languages = new ArrayList<>();
        this.updateData();
    }

    @Override
    public int getRowCount() {
        return this.mergedKeys.size();
    }

    @Override
    public int getColumnCount() {
        return this.languages.size() + 1;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return (columnIndex == 0) ? Text.KEY : this.languages.get(columnIndex-1);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return (columnIndex != 0) && this.mergedKeys.get(rowIndex).isLeaf();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        String value = null;
        Tree.Node node = this.mergedKeys.get(rowIndex);

        if(columnIndex == 0){
            value = new String(new char[node.getLevel()-1]).replace("\0", "     ");
            value += node.getValue();
        } else {
            String lang = this.languages.get(columnIndex-1);
            Tree column = this.translations.get(lang);
            if(!node.isLeaf()){
                value = "";
            } else {
                value = column.getValueByPath(node.getPath());
            }
        }

        return value;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String lang = this.languages.get(columnIndex-1);
        Tree column = this.translations.get(lang);
        Tree.Node node = this.mergedKeys.get(rowIndex);
        column.editValueByPath(node.getPath(), (String)aValue);
        this.filesService.saveFile(lang, column.flatToString());
    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }

    public void updateData(){
        this.languages.clear();
        this.translations.clear();

        this.languages.addAll(this.filesService.getLanguagesList());

        FilesParserModel filesParserModel = new FilesParserModel();
        for(String lang : this.languages){
            JsonObject json = this.filesService.getJsonByLanguage(lang);
            if(json != null){
                translations.put(lang, filesParserModel.parseJson(this.filesService.getJsonByLanguage(lang)));
            }
        }

        this.mergedKeys = FilesParserModel.keysTree.flatToArrayList();
    }

    public TranslationState getCellState(int rowIndex, int columnIndex){
        Tree.Node node = this.mergedKeys.get(rowIndex);
        TranslationState state = null;

        if(columnIndex == 0 || !node.isLeaf()){
            state = new TranslationState(node.getColor(), null);
        } else if(columnIndex > 0 && node.isLeaf()){
            String cellValue = (String)this.getValueAt(rowIndex, columnIndex);
            if(cellValue == null){
                state = new TranslationState(ColorValue.incompliteNodeColor, Text.TRANS_STATE_INCOMPLITE);
            } else if(cellValue.isEmpty()){
                state = new TranslationState(ColorValue.emptyNodeColor, Text.TRANS_STATE_EMPTY);
            } else {
                state = new TranslationState(this.mergedKeys.get(rowIndex).getColor(), null);
            }
        }

        return state;
    }
}
