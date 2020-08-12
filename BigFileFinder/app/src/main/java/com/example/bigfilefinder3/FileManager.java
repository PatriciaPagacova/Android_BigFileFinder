package com.example.bigfilefinder3;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class FileManager {

    private StartFolderSearchingListener myStartFolderSearchingListener;

    public FileManager(StartFolderSearchingListener startFolderSearchingListener) {
        myStartFolderSearchingListener = startFolderSearchingListener;
    }

    public ArrayList<String> getNBiggestSortedFiles(List<String> folderPaths, int n) {
        List<String> folderPathsWithoutDuplicities = removeDuplicatePaths(folderPaths);
        List<File> allFiles = getAllFilesInAllFolders(folderPathsWithoutDuplicities);
        sortFilesBySize(allFiles, false);
        List<File> nBiggestFiles = takeFirstNElements(allFiles, n);
        return getFilesDescriptions(nBiggestFiles);
    }

    public static List<String> removeDuplicatePaths(List<String> folderPaths)
    {
        List<String> sortedPathsFromMostGenericToMostConcrete = getSortFolderPathsFromMostGenericToMostConcrete(folderPaths);
        List<String> pathsGroups = groupFolderPathsByTheMostGeneric(sortedPathsFromMostGenericToMostConcrete);
        return pathsGroups;
    }

    private static List<String> groupFolderPathsByTheMostGeneric(List<String> folderPaths)
    {
        List<String> pathsGroups = new ArrayList<>();
        for (String fPath : folderPaths)
        {
            boolean pathHasGroupAlready = false;
            for(String pGroup : pathsGroups)
            {
                if(isFirstPathSubPathOfSecondPath(fPath, pGroup))
                {
                    pathHasGroupAlready = true;
                    break;
                }
            }
            if(!pathHasGroupAlready)
            {
                pathsGroups.add(fPath);
            }
        }
        return pathsGroups;
    }

    private static boolean isFirstPathSubPathOfSecondPath(String path1, String path2)
    {
        return path1.contains(path2);
    }

    private static List<String> getSortFolderPathsFromMostGenericToMostConcrete(List<String> folderPathsIn) {
        List<String> folderPaths = new ArrayList<>(folderPathsIn);
        Collections.sort(folderPaths, new Comparator<String>() {
            @Override
            public int compare(String fp1, String fp2) {
                if (fp1.length() == fp2.length()) {
                    return 0;
                }
                return fp1.length() < fp2.length() ? -1 : 1;
            }
        });
        return folderPaths;
    }

    private List<File> getAllFilesInAllFolders(List<String> folderPaths) {
        List<File> allFiles = new ArrayList<>();

        for (String path : folderPaths) {
            allFiles.addAll(getAllFilesInFolder(path));
        }
        return allFiles;
    }

    private List<File> getAllFilesInFolder(String path) {
        File directory = new File(path);
        List<File> allFiles = new ArrayList<File>();
        getAllFilesInFolder(directory, allFiles);
        return allFiles;
    }

    private void getAllFilesInFolder(File file, List<File> files) {
        if (file.isDirectory()) {
            myStartFolderSearchingListener.onStartFolderSearching(file.getAbsolutePath());
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                for (File subFile : subFiles) {
                    getAllFilesInFolder(subFile, files);
                }
            }
        } else {
            files.add(file);
        }
    }

    private void sortFilesBySize(List<File> files, boolean ascending) {
        final int FIRST_FILE_IS_SMALLER = ascending ? -1 : 1;
        final int FIRST_FILE_IS_BIGGER = ascending ? 1 : -1;
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                if (f1.length() == f2.length()) {
                    return 0;
                }
                return f1.length() < f2.length() ? FIRST_FILE_IS_SMALLER : FIRST_FILE_IS_BIGGER;
            }
        });
    }

    private List<File> takeFirstNElements(List<File> files, int number) {
        List<File> result = new ArrayList<File>();
        for (int i = 0; i < number && i < files.size(); i++) {
            result.add(files.get(i));
        }
        return result;
    }

    private ArrayList<String> getFilesDescriptions(List<File> files) {
        ArrayList<String> allPaths = new ArrayList<>();

        for (File file : files) {
            allPaths.add(getFileDescription(file));
        }
        return allPaths;
    }

    private String getFileDescription(File file) {
        return "Name: " + file.getName() + "\nPath: " + file.getAbsolutePath() + "\nSize: " + convertFileSizeToCorrectUnit(file);
    }

    private String convertFileSizeToCorrectUnit(File file) {
        DecimalFormat decForm2 = new DecimalFormat("#.##");
        DecimalFormat decForm0 = new DecimalFormat("#");
        decForm2.setRoundingMode(RoundingMode.UP);
        decForm0.setRoundingMode(RoundingMode.UP);

        long fileSize = file.length();
        if (fileSize < 1024) {
            return fileSize + "B";
        } else if ((fileSize / 1024) < 1024) {
            return decForm0.format((fileSize / (double) 1024)) + "Kb";
        } else
            return decForm2.format(fileSize / (double) 1024 / 1024) + "Mb";
    }
}
