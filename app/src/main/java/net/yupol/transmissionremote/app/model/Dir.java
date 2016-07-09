package net.yupol.transmissionremote.app.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import net.yupol.transmissionremote.app.model.json.File;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public final class Dir implements Parcelable {

    private String name;
    private List<Dir> dirs = new LinkedList<>();
    private List<File> files = new LinkedList<>();
    private List<Integer> fileIndices = new LinkedList<>();

    public Dir(String name) {
        this.name = name;
    }

    protected Dir(Parcel in) {
        name = in.readString();
        dirs = in.createTypedArrayList(Dir.CREATOR);
        files = in.createTypedArrayList(File.CREATOR);
    }

    public String getName() {
        return name;
    }

    public List<Dir> getDirs() {
        return dirs;
    }

    public List<File> getFiles() {
        return files;
    }

    public List<Integer> getFileIndices() {
        return fileIndices;
    }

    public static Dir createFileTree(@NonNull File[] files) {
        Dir root = new Dir("/");

        for (int i=0; i<files.length; i++) {
            File file = files[i];
            List<String> pathParts = Arrays.asList(file.getPath().split("/"));
            if (pathParts.get(0).isEmpty()) pathParts = pathParts.subList(1, pathParts.size());
            parsePath(pathParts, root, file, i);
        }

        return root;
    }

    public static Dir emptyDir() {
        return new Dir("/");
    }

    private static void parsePath(List<String> pathParts, Dir parentDir, File fileAtPath, int fileIndex) {
        if (pathParts.size() == 1) {
            parentDir.files.add(fileAtPath);
            parentDir.fileIndices.add(fileIndex);
            return;
        }

        String dirName = pathParts.get(0);
        Dir dir = findDirWithName(dirName, parentDir.dirs);
        if (dir == null) {
            dir = new Dir(dirName);
            parentDir.dirs.add(dir);
        }
        parsePath(pathParts.subList(1, pathParts.size()), dir, fileAtPath, fileIndex);
    }

    private static Dir findDirWithName(String name, List<Dir> dirs) {
        for (Dir dir : dirs) {
            if (dir.name.equals(name)) return dir;
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(dirs);
        dest.writeTypedList(files);
    }

    public static final Creator<Dir> CREATOR = new Creator<Dir>() {
        @Override
        public Dir createFromParcel(Parcel in) {
            return new Dir(in);
        }

        @Override
        public Dir[] newArray(int size) {
            return new Dir[size];
        }
    };
}
