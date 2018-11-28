package Objects;

import java.util.ArrayList;

public class FilesArray {
    private ArrayList<FileObject> files;
    public FilesArray(ArrayList<FileObject> files){
        this.files=files;
    }
    public FilesArray(){}

    public ArrayList<FileObject> getFiles() {
        return this.files;
    }

    public void addFile(FileObject file) {
        this.files.add(file);
    }

    public void removeFile(FileObject file)
    {
        this.files.remove(file);
    }

    public int size(){
        return this.files.size();
    }
}
