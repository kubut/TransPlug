import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.project.Project;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by kubut on 20.02.2016
 */
public class FilesService {
    private Project project;
    private PropertiesComponent propertiesComponent;
    private ArrayList<File> files;

    public static FilesService getInstance(Project project){
        return new FilesService(project);
    }

    private FilesService(Project project){
        this.project = project;
        this.propertiesComponent = PropertiesComponent.getInstance(project);
        this.files = new ArrayList<>();
    }

    public boolean isCorrectPath(){
        String path = this.project.getBasePath() + "/" + this.propertiesComponent.getValue("transPath", "");
        this.files.clear();

        try {
            this.loadFiles(path);
        } catch (Exception e){
            return false;
        }

        return !this.files.isEmpty();
    }

    private void loadFiles(String path){
        File dir = new File(path);
        if(dir.isDirectory()){
            for(File file : dir.listFiles()){
                if(!file.isDirectory() && this.getFileExtension(file).equals("json")){
                    this.files.add(file);
                }
            }
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }


}
