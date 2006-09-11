package com.muddyhorse.cynch.admin;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.muddyhorse.cynch.UpdateUtils;
import com.muddyhorse.cynch.manifest.DownloadType;
import com.muddyhorse.cynch.manifest.PostDownloadActionType;
import com.muddyhorse.cynch.manifest.RemoteFileInfo;

public class AdminUtils
{

    private static final DateFormat sdf = new SimpleDateFormat("yyyyMMdd.HHmm");

    public static void createManifest(String filename) throws IOException {
        createManifest(new File(filename));
    }

    public static void createManifest(File maniFile) throws IOException {
        // set up; delete existing manifest:
        maniFile.delete();
        updateManifest(maniFile);
    }

    /** Finds the relative path between two files
     * Copied from http://forum.java.sun.com/thread.jspa?threadID=584546
     */
    public static String getRelativePath(File root, File f) throws IOException {
        File file = new File(f + File.separator + "89243jmsjigs45u9w43545lkhj7").getParentFile();
        File relativeTo = new File(root + File.separator + "984mvcxbsfgqoykj30487df556").getParentFile();
        File origFile = file;
        File origRelativeTo = relativeTo;
        List<File> filePathStack = new ArrayList<File>();
        List<File> relativeToPathStack = new ArrayList<File>();

        // build the path stack info to compare it afterwards
        file = file.getCanonicalFile();
        while (file != null) {
            filePathStack.add(0, file);
            file = file.getParentFile();
        }
        relativeTo = relativeTo.getCanonicalFile();
        while (relativeTo != null) {
            relativeToPathStack.add(0, relativeTo);
            relativeTo = relativeTo.getParentFile();
        }

        // compare as long it goes
        int count = 0;
        file = filePathStack.get(count);
        relativeTo = relativeToPathStack.get(count);
        while ((count < filePathStack.size() - 1) && (count != relativeToPathStack.size() - 1)
                && file.equals(relativeTo)) {
            count++;
            file = filePathStack.get(count);
            relativeTo = relativeToPathStack.get(count);
        }
        if (file.equals(relativeTo)) count++;

        // up as far as necessary
        StringBuffer relString = new StringBuffer();
        for (int i = count; i != relativeToPathStack.size(); i++) {
            relString.append(".." + File.separator);
        }

        // now back down to the file
        for (int i = count; i < filePathStack.size() - 1; i++) {
            relString.append(filePathStack.get(i).getName() + File.separator);
        }
        relString.append(filePathStack.get(filePathStack.size() - 1).getName());

        // just to test
        File relFile = new File(origRelativeTo.getAbsolutePath() + File.separator + relString.toString());
        if (!relFile.getCanonicalFile().equals(origFile.getCanonicalFile())) {
            throw new IOException("Failed to find relative path.");
        }
        return relString.toString().replaceAll("\\\\", "/");
    }

    private static SortedSet<File> gatherAllFiles(File root, FileFilter filter) {
        SortedSet<File> rv = new TreeSet<File>();
        gatherAllFilesImpl(root, filter, rv);
        return rv;
    }

    private static void gatherAllFilesImpl(File root, FileFilter filter, SortedSet<File> rv) {
        File[] files = root.listFiles(filter);

        // go through all files
        for (File file : files) {
            if (file.isDirectory()) {
                // recurse:
                gatherAllFilesImpl(file, filter, rv);

            } else {
                // add:
                rv.add(file);
            } // endif
        } // endforeach
    }

    public static void updateManifest(String filename) throws IOException {
        updateManifest(new File(filename));
    }

    public static void updateManifest(final File maniFile) throws IOException {
        // set up; make sure path is there, delete existing manifest:
        UpdateUtils.insurePathToFileExists(maniFile);

        // create manifest:
        AdminManifest adm = new AdminManifest(maniFile);
        SortedMap<String, RemoteFileInfo> allFileInfo = adm.getAllFileInfo();
        Set<String> usedIDs = new HashSet<String>(allFileInfo.keySet());
        boolean dirty = false;

        // list all files:
        File root = maniFile.getParentFile();
        SortedSet<File> files = gatherAllFiles(root, new FileFilter() {
            public boolean accept(File pathname) {
                return !maniFile.equals(pathname);
            }
        });
        Map<String, Integer> nameCounter = new HashMap<String, Integer>(files.size() / 10); // quick guess at a metric
        for (File file : files) {
            String name = file.getName();

            // get count of name use:
            int count;
            if (nameCounter.containsKey(name)) {
                // increment and put back:
                count = nameCounter.get(name);

            } else {
                count = 0;
            } // endif

            // increment and store the count:
            count++;
            nameCounter.put(name, count);

            String fileID;
            // determine whether to use the suffix:
            if (count > 1) {
                fileID = name + count;

            } else {
                fileID = name;
            } // endif

            BigDecimal version = getVersion(sdf, file);
            long size = file.length();
            String relPath = getRelativePath(root, file);
            RemoteFileInfo info = allFileInfo.get(fileID);

            if (info == null) {
                dirty = true;
                // create the file info:
                info = new AdminFileInfo(fileID);
                info.setAction(PostDownloadActionType.nothing);
                info.setDescription(name + " " + count);
                info.setDownloadType(DownloadType.critical);
                info.setRawURL(relPath);
                info.setRawLocalPath(relPath);
                info.setSize(size);
                info.setVersion(version);

                System.out.println("Created entry " + info);
                allFileInfo.put(fileID, info);

            } else {
                // update the relevant fields of the fileinfo
                // things to update: version, size, location-fields
                if (version.compareTo(info.getVersion()) > 0) {
                    dirty = true;
                    info.setVersion(version);
                    System.out.println("Updated entry version: " + info);
                } // endif

                if (size != info.getSize()) {
                    dirty = true;
                    info.setSize(size);
                    System.out.println("Updated entry size: " + info);
                } // endif

                // TODO if path changes, below, we probably should update all fields
                if (info.isLocalPathRelative()
                        && !info.getRawLocalPath().equals(relPath)) {
                    dirty = true;
                    boolean remoteSameAsLocal = info.getRawLocalPath().equals(info.getRawURL());
                    info.setRawLocalPath(relPath);
                    System.out.println("Updated local path: " + info);
                    if (remoteSameAsLocal) {
                        info.setRawURL(relPath);
                        System.out.println("Updated remote path: " + info);
                    } // endif -- remote, local same
                } // endif -- relative local path, and is different
            } // endif

            // to mark that we used an ID, pull it out of the original pool:
            usedIDs.remove(fileID);
        } // endforeach

        // TODO this only works assuming the fileIDs are always generated as above... a custom ID would get obliterated -- id should almost be entire rel path...
        for (String fileID : usedIDs) {
            allFileInfo.remove(fileID);
            System.out.println("File removed: " + fileID);
            dirty = true;
        } // endforeach

        if (dirty) {
            adm.save(maniFile);
        } else {
            System.out.println("Not saving -- no changes");
        } // endif
    }

    private static BigDecimal getVersion(DateFormat sdf, File file) {
        return new BigDecimal(sdf.format(file.lastModified()));
    }

}
