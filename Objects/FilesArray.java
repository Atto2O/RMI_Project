package Objects;

import java.util.ArrayList;

public class FilesArray {
    private ArrayList<FileObject> files = new ArrayList<>();
    private boolean empty;

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
        System.out.println("Before remove:");
        for (FileObject f:this.files) {
            System.out.println(f.getId());
        }
        this.files.remove(file);
        System.out.println("After remove: ");
        for (FileObject f:this.files) {
            System.out.println(f.getId());
        }
    }

    public int size(){
        return this.files.size();
    }
}
