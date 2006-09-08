package com.muddyhorse.cynch;

import java.awt.Frame;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.muddyhorse.cynch.manifest.*;

/**
 *
 */
public class UpdateUtils implements Constants
{

    //
    // Class variables:
    //
    private static final int BUFFER_SIZE = 10000;

    //    private static Map<String, String> parms       = new HashMap<String, String>();

    private static Frame     mainframe;

    //
    // File Retrieval methods:
    //
    public static String getStringFromClasspath(Class<?> base, String filename) {
        try {
            InputStream is = base.getResourceAsStream(filename);
            int available = is.available();
            byte buffer[] = new byte[available];
            is.read(buffer);
            is.close();

            String tmp = new String(buffer);
            //System.out.println("\nfrom CP:"+filename+":\n"+tmp);
            return tmp;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        } // endtry
    }

    /** Read the contents of a webpage at the given address */
    public static String getStringFromURL(String urlString) throws MalformedURLException {
        return getStringFromURL(new URL(urlString));
    }

    /** Read the contents of a webpage indicated by a URL */
    public static String getStringFromURL(URL url) {
        try {
            if (url == null) {
                return null;
            }

            URLConnection conn = url.openConnection();
            //System.out.println("URL conn is "+conn);
            conn.connect();

            if (conn instanceof HttpURLConnection) {
                int status = ((HttpURLConnection) conn).getResponseCode();
                if (status != HttpURLConnection.HTTP_OK) {
                    return null;
                }
            } // endif instanceof HttpURLConn

            int available = conn.getContentLength();
            InputStream is = conn.getInputStream();
            byte buffer[] = new byte[available];
            int pos = 0;
            while (available > 0) {
                int amount = is.read(buffer, pos, available);
                available -= amount;
                pos += amount;
            } // endif
            is.close();

            String tmp = new String(buffer);
            //System.out.println("\nfrom URL:"+url+":\n"+tmp);
            return tmp;
        } catch (Exception ex) {
            System.out.println(ex);
            //            ex.printStackTrace();
            return "";
        } // endtry
    }

    /** Read the contents of a file */
    public static String getStringFromFile(String filename) {
        try {
            FileInputStream fis = new FileInputStream(filename);
            int available = fis.available();
            byte buffer[] = new byte[available];
            fis.read(buffer);
            fis.close();

            String tmp = new String(buffer);
            //System.out.println("\nfrom file:"+filename+":\n"+tmp);
            return tmp;
        } catch (Exception ex) {
            System.out.println(ex);
            //            ex.printStackTrace();
            return "";
        } // endtry
    }

    //
    // Utility methods:
    //

    /** Erase all C style comments in the string prior to parsing it */
    public static String eraseComments(String input) {
        int count = input.length();
        StringBuffer sb = new StringBuffer(count);
        boolean isCComment = false;
        boolean isString = false;

        for (int i = 0; i < count; i++) {
            char c = input.charAt(i);

            if (c == '\"') {
                isString = !isString;
            }

            if (!isString && c == '/' && input.charAt(i + 1) == '*') {
                isCComment = true;
                i++;
            } // endif

            if (!isCComment) {
                sb.append(c);
            }

            if (!isString && c == '*' && input.charAt(i + 1) == '/') {
                isCComment = false;
                i++;
            } // endif
        } // endfor

        return sb.toString();
    }

    private static int offsetToEOL(String data, int pos) {
        int i = 0, count = data.length();
        for (; pos + i < count && !Character.isISOControl(data.charAt(pos + i)); i++) {
            // nothing here
        }
        return i;
    }

    private static int offsetToNextLine(String data, int pos) {
        int i = 0, count = data.length();
        for (; pos + i < count && !Character.isISOControl(data.charAt(pos + i)); i++) {
            // nothing here
        }
        for (; pos + i < count && Character.isISOControl(data.charAt(pos + i)); i++) {
            // nothing here
        }
        return i;
    }

    /** Split the file, line by line, into key/value pairs */
    public static Map<String, String> stringToHashtable(String file) {
        if (file == null) {
            return null;
        }

        String sansComments = eraseComments(file);
        int pos = 0, count = sansComments.length();
        Map<String, String> retval = new HashMap<String, String>();

        // Split the file, line by line, into key/value pairs
        while (pos < count) {
            int eol = offsetToEOL(sansComments, pos);
            int next = offsetToNextLine(sansComments, pos);
            if (eol > 0) {
                String line = sansComments.substring(pos, pos + eol);
                StringTokenizer st = new StringTokenizer(line, "=");
                String key = st.nextToken();
                String value = "";
                if (st.hasMoreTokens()) {
                    value = st.nextToken();
                }

                // Place scalar values in the hashtable directly
                // otherwise break comma separated ones into Vectors.
                // If they can be, items are inserted as Doubles, failing
                // that, as Strings.
                //                int comma = value.indexOf(",");
                //                if (comma == -1) {
                //                    try {
                //                        retval.put(key, new Double(value));
                //                    } catch (NumberFormatException nfe) {
                retval.put(key, value);
                //                    } // endtry
                //                } else {
                //                    Vector<Comparable> v = new Vector<Comparable>();
                //                    StringTokenizer values = new StringTokenizer(value, ",");
                //                    while (values.hasMoreTokens()) {
                //                        String s = values.nextToken();
                //                        try {
                //                            v.addElement(new Double(s));
                //                        } catch (NumberFormatException nfe) {
                //                            v.addElement(s.trim());
                //                        } // endtry
                //                    } // endwhile
                //                    retval.put(key, v);
                //                } // endif
            } // endif
            pos += next;
        } // endwhile

        return retval;
    }

    /** Write a hashtable to file */
    public static void writeHashtable(String filename, Map<String, String> hash) {
        try {
            FileWriter fw = new FileWriter(filename);

            Set<Entry<String, String>> entries = hash.entrySet();
            for (Entry<String, String> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                String str = key + "=" + value;

                //                if (value instanceof String) {
                //                    str += (String) value;
                //                } else {
                //                    Vector v = (Vector) value;
                //                    int count = v.size();
                //                    for (int i = 0; i < count; i++) {
                //                        // str += (String)v.elementAt(i);
                //                        str += v.elementAt(i).toString();
                //                        if (i < count - 1) {
                //                            str += ",";
                //                        }
                //                    } // endfor
                //                } // endif
                fw.write(str);
                fw.write("\r\n");
            } // endfor
            fw.close();
        } catch (Exception ex) {
            System.out.println(ex);
            //            ex.printStackTrace();
        } // endtry
    }

    /** Compare local and remote hashtables and return a hashtable
     *  of operations to perform.
     */
    public static Map<String, Operation> compareManifests(LocalManifest localManifest, RemoteManifest remoteManifest) {
        SortedMap<String, LocalFileInfo> localFIs = localManifest.getAllFileInfo();
        TreeMap<String, RemoteFileInfo> remoteFIs = new TreeMap<String, RemoteFileInfo>(remoteManifest.getAllFileInfo());

        Map<String, Operation> rv = new TreeMap<String, Operation>();

        // using the keys of the local hashtable, find out what to
        //  ignore (if it is up-to-date already), update or delete:
        for (LocalFileInfo lfi : localFIs.values()) {
            String key = lfi.getFileID();
            RemoteFileInfo rfi = remoteFIs.get(key);
            // if this is an update (ie, present in local and remote)
            //  remove from remote copy.
            if (rfi != null) {
                remoteFIs.remove(key);
            } // endif

            Operation op = new Operation(key);
            op.setLocal(lfi);
            op.setRemote(rfi);

            if (rfi == null) {
                // there is no remote entry; we should delete:
                op.setOperation(OperationType.delete);

            } else {
                // remote entry is present; check to see if we need to update:

                // if remote version newer, mark as update
                if (rfi.getVersion() > lfi.getVersion()) {
                    op.setOperation(OperationType.update);

                } else {
                    op.setOperation(OperationType.nothing);
                } // endif
            } // endif

            // put the populated operation into the hashtable:
            rv.put(key, op);
        } // endforeach

        // Now, the remote map should only contain things that
        // are NOT on the local side - these are the new downloads.
        //        Set<Entry<String, String>> remoteEntries = remoteCopy.entrySet();
        Iterator<Entry<String, RemoteFileInfo>> remoteEntryItor = remoteFIs.entrySet().iterator();
        while (remoteEntryItor.hasNext()) {
            Entry<String, RemoteFileInfo> remoteEntry = remoteEntryItor.next();

            String key = remoteEntry.getKey();
            RemoteFileInfo rfi = remoteEntry.getValue();

            //            if (rfi != null) {
            //                remoteEntryItor.remove(); // this is likely gratuitous -- ie, doesn't affect outcome
            //            } // endif

            Operation op = new Operation(key);
            op.setRemote(rfi);
            op.setLocal(null);
            op.setOperation(OperationType.download);

            rv.put(key, op);
        } // endwhile

        return rv;
    }

    /**
     * @param ids list of IDs to work with.  If null, all IDs will be
     *   accepted.
     * @return an array of Vectors.  An operation is placed in the vector
     *   at the offset corresponding to its operation kind.  All of these
     *   vectors are guaranteed to be non-null;
     */
    public static Map<OperationType, List<Operation>> sortOperationsByOp(Map<String, Operation> ops, List<String> ids) {
        Map<OperationType, List<Operation>> vs = new HashMap<OperationType, List<Operation>>();
        for (OperationType op : OperationType.values()) {
            vs.put(op, new ArrayList<Operation>());
        } // endforeach

        Set<Entry<String, Operation>> entries = ops.entrySet();
        for (Entry<String, Operation> entry : entries) {
            String k = entry.getKey();
            if (ids == null || ids.contains(k)) {
                Operation op = entry.getValue();
                OperationType olt = op.getOperation();
                vs.get(olt).add(op);
            } // endif
        } // endforeach

        return vs;
    }

    /**
     * @param ids list of IDs to work with.  If null, all IDs will be
     *   accepted.
     * @return a list of 3 lists of operations.  An operation is placed in the list
     *   at the offset corresponding to its operation kind.  All of these
     *   lists are guaranteed to be non-null;
     */
    public static Map<DownloadType, List<Operation>> sortOperationsByType(Map<String, Operation> ops, List<String> ids) {
        Map<DownloadType, List<Operation>> vs = new HashMap<DownloadType, List<Operation>>();
        vs.put(DownloadType.required, new ArrayList<Operation>());
        vs.put(DownloadType.critical, new ArrayList<Operation>());
        vs.put(DownloadType.optional, new ArrayList<Operation>());

        Set<Entry<String, Operation>> entries = ops.entrySet();
        for (Entry<String, Operation> entry : entries) {
            String k = entry.getKey();
            if (ids == null || ids.contains(k)) {
                Operation op = entry.getValue();
                DownloadType dlt = op.getRemote().getDownloadType();
                vs.get(dlt).add(op);
            } // endif
        } // endforeach

        return vs;
    }

    /**
     * Note that this method removes operations from the config as
     *  they are successfully completed.
     * @param cfg the hashtable to write the results to.  If null, no
     *   record of the transaction is kept.
     * @param type the type of operation to perform. If the type is TYPE_ALL,
     *  all types will be performed.
     * @return the count of errors that occurred in performing the
     *   various operations.
     */
    public static int performOperations(Config cfg, DownloadType type, List<Operation> ops, ProgressListener l)
            throws InterruptedException {
        int errorCnt = 0;

        for (Operation op : ops) {
            if (type == DownloadType.all || type == op.getRemote().getDownloadType()) {
                if (!performOperation(cfg, op, l)) {
                    ++errorCnt;

                } else {
                    // completed successfully, remove from config:
                    cfg.getOperations().remove(op.getFileID());
                } // endif
            } // endif
        } // endforeach

        return errorCnt;
    }

    /**
     * @param cfg the DUConfig containing context information
     * @return true if the operation was successful.
     */
    public static boolean performOperation(Config cfg, Operation op, ProgressListener l) throws InterruptedException {
        System.out.println("uu.pO: starting...");
        try {
            int dlSize = 0;
            l.starting(op);
            RemoteFileInfo rfi = op.getRemote();
            URL u;
            if (rfi != null) {
                u = rfi.getPath();

            } else {
                u = null;
            } // endif

            LocalFileInfo lfi = op.getLocal();
            if (lfi == null) {
                // almost safe to use rfi here... both FIs should not be null at the same time...
                lfi = rfi.getLocalInfo(new File(cfg.getLocalBase()));
            } // endif

            final File n = lfi.getPath();

            // do something based on the operatoin
            switch (op.getOperation()) {
                case update: {
                    String tempPrefix = cfg.getAppShortName();
                    String tempSuffix = "_Cynch.tmp";
                    // need to:
                    // * download to a temporary location first
                    // * verfiy contents
                    // * delete backup
                    // * rename original to backup
                    // * rename update to original
                    //                    String temp = n.getParent() + File.separator + tempPrefix + op.getFileID()
                    //                            + tempSuffix;
                    // uses current directory (which should exist!)...
                    //                    File tempf = new File(temp);
                    insurePathToFileExists(n); // is this needed? should already exist...
                    File tempf = File.createTempFile(tempPrefix, tempSuffix, n.getParentFile()); // 1.3 specific...
                    dlSize = getFileFromURL(u, tempf, l);

                    if (dlSize > 0 && verifyFile(tempf)) {
                        postProcessDownload(n, tempf, rfi.getAction());

                    } else {
                        throw new IllegalStateException("(Update)File verification problem: " + tempf);
                    } // endif
                }
                break;
                case download: {
                    // need to:
                    // * download to real name
                    // * verfiy contents
                    insurePathToFileExists(n);
                    // TODO set up temporary ops, then call postProc
                    dlSize = getFileFromURL(u, n, l);
                    if (dlSize > 0 && verifyFile(n)) {
                        // no action necessary here.
                    } else {
                        throw new Exception("(Download)File verification problem: " + n);
                    } // endif
                }
                break;
                case delete: {
                    // need to:
                    // * delete original
                    n = cfg.get(INI_LOCAL_BASE) + op.getLocalPath();
                    f = new File(n);
                    if (!f.delete()) {
                        throw new Exception("File deletion problem: " + n);
                    } // endif

                    cfg.getLocalFiles().remove(op.getFileID());
                    // save updated config info:
                    writeHashtable(cfg.getLocalConfigName(), cfg.getLocalFiles());

                }
                break;
                case nothing: {
                    // do nothing here.
                }
                break;
                default:
                    throw new Exception("Unknown operation type: " + op.getOperation());
            } // endswitch

            op.setRemoteSize(dlSize);

            if (op.getOperation() != OperationType.nothing) { // no file chgs on nothings...
                // update config info:
                // note that, once the download is complete, the remote info
                //  and the local info should be the same.
                // thus, use remote info to build vector:
                cfg.getLocalFiles().put(op.getFileID(), op.toVector(false));
                // save updated config info:
                writeHashtable(cfg.getLocalConfigName(), cfg.getLocalFiles());
            } // endif -- not nothing
            l.finished(op, true);

            return true;

        } catch (InterruptedException ix) {
            throw ix;

        } catch (Exception ex) {
            System.out.println("uu:" + ex);
            ex.printStackTrace();
            l.finished(op, false);

            return false;
        } // endtry
    }

    private static void postProcessDownload(final File targetFile, File tempFile, PostDownloadActionType action) throws ZipException, IOException {
        File backf = new File(targetFile.getPath() + ".bak");
        // delete old backup file:
        boolean b = false;
        if (backf.exists()) {
            // backup exists, delete it:
            b = backf.delete();
            if (!b) {
                // delete unsuccessful...
                throw new IllegalStateException("(Update)File delete problem: " + backf);
            } // endif
        } // endif
        if (targetFile.exists()) {
            // rename original to backup:
            b = targetFile.renameTo(backf);
            if (!b) {
                // rename1 unsuccessful...
                throw new IllegalStateException("(Update)File Rename problem: " + targetFile);
            } // endif
        } // endif

        // final post-processing:
        switch (action) {
            case nothing: {
                // normal, rename download to original:
                b = tempFile.renameTo(targetFile);
                if (!b) {
                    // rename2 unsuccessful...
                    throw new IllegalStateException("(Update)File Rename problem: " + tempFile);
                } // endif
            } break;

            case unzip: {
                unzipFileToLocation(tempFile, targetFile);
            } break;

            default:
            break;
        } // endswitch
    }

    private static void unzipFileToLocation(File tempFile, File targetFile) throws ZipException, IOException {
        ZipFile zf = new ZipFile(tempFile);
        Enumeration<? extends ZipEntry> enumer = zf.entries();
        while (enumer.hasMoreElements()) {
            ZipEntry ze = enumer.nextElement();
            ze.getName();
            // TODO finish unzipping!  Need to run a few tests, too
        } // endwhile
    }

    /*
     public static int countOperations(DUConfig cfg, int type, Vector ids) {
     int total = 0;
     Enumeration enumer = cfg.getOperations().elements();
     while (enumer.hasMoreElements()) {
     DUOperation op = (DUOperation)enumer.nextElement();

     if (type == TYPE_ALL
     || type == op.type) {
     // same type
     if (ids == null
     || ids.contains(op.fileID)) {
     // in vector, or vector null
     if (op.operation != DUOperation.OP_NOTHING) {
     ++total;
     } // endif
     } // endif
     } // endif
     } // endwhile
     return total;
     }
     //*/

    /** Counts the total download size (in bytes) of downloads and
     *  updates.  Operations that are deletes or "nothing"s do not
     *  add to the total.
     * @param type the type of operation to total
     * @param ids the list of file IDs to consider.  If null,
     *   consider all operations.
     * @return the size of download and update operations, or -1 if
     *   an error occurred.
     */
    public static long countDownloadSize(Config cfg, DownloadType type, List<String> ids) {
        long total = 0;
        try {
            Collection<Operation> ops = cfg.getOperations().values();
            for (Operation op : ops) {
                if (type == DownloadType.all || type == op.getDownloadType()) {
                    // same type
                    if (ids == null || ids.contains(op.getFileID())) {
                        // in vector, or vector null
                        if (op.getOperation() == OperationType.update || op.getOperation() == OperationType.download) {
                            total += op.getRemoteSize();
                        } // endif
                    } // endif
                } // endif
            } // endforeach

            return total;

        } catch (NullPointerException ex) {
            return -1;
        } // endtry
    }

    /** Performs in the order: New downloads, Update files, Delete files.
     * Note that this method removes operations from the config as
     *  they are successfully completed.
     * @param cfg the hashtable to write the results to.  If null, no
     *  record of the transaction is kept.
     * @param type the type of operation to perform. If the type is TYPE_ALL,
     *  all types will be performed.
     * @param ids list of IDs to work with.  If null, all IDs will be
     *  accepted.
     * @return the number of errors that occurred while performing the
     *  operations, or -1 if the operation was interrupted (by, for example,
     *  user intervention).
     */
    public static int performAllOperations(Config cfg, DownloadType type, List<String> ids, ProgressListener l)
            throws InterruptedException {
        try {
            int errorCnt;
            Map<OperationType, List<Operation>> opSets = sortOperationsByOp(cfg.getOperations(), ids);
            errorCnt = performOperations(cfg, type, opSets.get(OperationType.download), l);
            errorCnt += performOperations(cfg, type, opSets.get(OperationType.update), l);
            errorCnt += performOperations(cfg, type, opSets.get(OperationType.delete), l);

            // of course, skip OP_NOTHING operations...
            return errorCnt;
        } catch (InterruptedException ix) {
            throw ix;
        } // endtry
    }

    /**
     * @return the number of bytes downloaded, -1 if error occurred
     */
    public static int getFileFromURL(URL rmt, File local, ProgressListener l) throws InterruptedException {
        InputStream is = null;
        OutputStream fs = null;

        try {
            if (rmt == null) {
                return -1;
            }

            URLConnection conn = rmt.openConnection();
            conn.connect();

            if (conn instanceof HttpURLConnection) {
                int status = ((HttpURLConnection) conn).getResponseCode();
                if (status != HttpURLConnection.HTTP_OK) {
                    return -1;
                }
            } // endif instanceof HttpURLConn

            final int available = conn.getContentLength();
            is = conn.getInputStream();
            fs = new BufferedOutputStream(new FileOutputStream(local), BUFFER_SIZE);

            byte buffer[] = new byte[BUFFER_SIZE];

            int left = available;
            while (left > 0) {
                if (l != null) {
                    l.progress(local.getName(), "Downloading", available - left, available);
                } // endif

                int amount = is.read(buffer, 0, BUFFER_SIZE);
                if (amount == -1) {
                    // eof
                    break;
                } // endif

                left -= amount;

                // commit buffer:
                fs.write(buffer, 0, amount);
                //                Thread.sleep(1000);
            } // endwhile
            System.out.println("uu.gFFURL: downloaded " + (available - left) + " of " + available);
            System.out.println("uu.gFFURL: from " + rmt + " to " + local);
            return available - left;
        } catch (InterruptedException ix) {
            //System.out.println("uu.gFFURL: caught ix!! @"+System.currentTimeMillis());
            throw ix;
        } catch (Exception ex) {
            System.out.println(ex);
            //            ex.printStackTrace();
            return -1;
        } finally {
            try {
                if (is != null) {
                    is.close();
                } // endif                
                /*! this causes a slowdown/lockup... see also bug 4211025
                 related stacktrace:
                 "Operations thread" prio=7 tid=0x79b8d0 nid=0x157 runnable [0x91af000..0x91afdc8 ]
                 at java.net.SocketInputStream.socketRead(Native Method)
                 at java.net.SocketInputStream.read(SocketInputStream.java:86)
                 at java.net.SocketInputStream.skip(SocketInputStream.java:123)
                 at java.io.BufferedInputStream.skip(BufferedInputStream.java:308)
                 at java.io.FilterInputStream.skip(FilterInputStream.java:132)
                 at java.io.PushbackInputStream.skip(PushbackInputStream.java:279)
                 at sun.net.www.MeteredStream.skip(MeteredStream.java:80)
                 at sun.net.www.http.KeepAliveStream.close(KeepAliveStream.java:64)
                 at common.du.DynamicUpdateUtils.getFileFromURL(DynamicUpdateUtils.java:6 52)
                 at common.du.DynamicUpdateUtils.performOperation(DynamicUpdateUtils.java :444)
                 at common.du.DynamicUpdateUtils.performOperations(DynamicUpdateUtils.jav a:372)
                 at common.du.DynamicUpdateUtils.performAllOperations(DynamicUpdateUtils. java:570)
                 at common.du.DynamicUpdateButons.run(DynamicUpdateButons.java:116)
                 at java.lang.Thread.run(Thread.java:484)
                 */
                if (fs != null) {
                    fs.close();
                } // endif                

            } catch (Exception ex) {
                ex.printStackTrace();
            } // endtry
        } // endtry
    }

    /**
     * @return the number of bytes downloaded, -1 if error occurred
     */
    public static int getFileFromClasspath(Class<?> base, String rmt, File local) {
        InputStream is = null;
        OutputStream fs = null;

        try {
            if (rmt == null || local == null) {
                return -1;
            }

            is = base.getResourceAsStream(rmt);
            final int available = is.available();
            fs = new BufferedOutputStream(new FileOutputStream(local), BUFFER_SIZE);

            byte buffer[] = new byte[BUFFER_SIZE];

            int left = available;
            while (left > 0) {
                int amount = is.read(buffer, 0, BUFFER_SIZE);
                if (amount == -1) {
                    // eof
                    break;
                } // endif

                left -= amount;

                // commit buffer:
                fs.write(buffer, 0, amount);
            } // endwhile
            System.out.println("uu.gFFC: downloaded " + (available - left) + " of " + available);
            System.out.println("uu.gFFC: from " + rmt + " to " + local);
            return available - left;

        } catch (Exception ex) {
            //System.out.println(ex);
            ex.printStackTrace();
            return -1;

        } finally {
            try {
                if (is != null) {
                    is.close();
                } // endif
                if (fs != null) {
                    fs.close();
                } // endif                
            } catch (Exception ex) {
                ex.printStackTrace();
            } // endtry
        } // endtry
    }

    /**
     * @return the number of bytes saved, -1 if error occurred
     */
    public static int writeStringToFile(String data, File local) {
        PrintWriter fs = null;

        try {
            if (data == null || local == null) {
                return -1;
            }

            //            byte[] buffer = data.getBytes();
            //            final int available = buffer.length;
            insurePathToFileExists(local);
            fs = new PrintWriter(new BufferedOutputStream(new FileOutputStream(local), BUFFER_SIZE));
            fs.print(data);
            fs.flush();
            //System.out.println("uu.gFFS: saved "+available+" of "+available);
            //System.out.println("uu.gFFS: from string to "+local);
            return data.length();

        } catch (Exception ex) {
            //System.out.println(ex);
            ex.printStackTrace();
            return -1;

        } finally {
            try {
                if (fs != null) {
                    fs.close();
                } // endif                
            } catch (Exception ex) {
                ex.printStackTrace();
            } // endtry
        } // endtry
    }

    public static boolean startApplication(Config cfg) {
        return startApplication(cfg.get(INI_EXEC_NAME), cfg.get(INI_EXEC_ARGS), cfg.get(INI_LOCAL_BASE));
    }

    public static boolean startApplication(String execName, String args, String startDir) {
        // get arguments:
        //        StringTokenizer izer = new StringTokenizer(cfg.get(INI_EXEC_ARGS),"+");
        String appAndArgs = execName + " " + startDir + " " + args;
        //System.out.println("uu.sA: running: "+appAndArgs);

        try {
            //            Process p = Runtime.getRuntime().exec(appAndArgs, null, new File(startDir));
            //            Process p = 
            Runtime.getRuntime().exec(appAndArgs, null);
            //            System.out.println("done starting application");
            //            return p.waitFor() == 0;
            return true;
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        } // endtry
    }

    /*
     public static boolean startJavaApplication(String jarName, String mainClass, String args[]) {
     URL[] ulist = {new URL(jarName)};
     ClassLoader cl = new URLClassLoader(ulist)

     }
     //*/

    /** This method insures that a path exists to allow creation
     *  of the specified file.
     */
    public static void insurePathToFileExists(String filename) throws FileNotFoundException {
        insurePathToFileExists(new File(filename));
    }

    /** This method insures that a path exists to allow creation
     *  of the specified file.
     */
    public static void insurePathToFileExists(File f) throws FileNotFoundException {
        File p = f.getParentFile();
        //System.out.println("uu.iPE: parent of "+filename+" is "+parent);

        if (!p.exists()) {
            // parent doesn't exist, create it:
            if (!p.mkdirs()) {
                throw new FileNotFoundException("Unable to make directory " + parent);
            } // endif -- second mkdir unsuc
        } // endif -- parent exists
    }

    public static void setMainFrame(Frame f) {
        mainframe = f;
    }

    public static Frame getMainFrame() {
        return mainframe;
    }

    private static boolean verifyFile(File f) {
        // TODO implement verifyFile
        return true;
    }

    /*
     public static String buildClasspath(DUConfig cfg) {
     }

     public static void writeToLog(String s) {
     // list when say no.
     }

     public static void writeToLog(Exception ex) {
     // list when say no.
     }
     //*/

    //    public static void main(String[] args) {
    //try {
    //        System.out.println(getStringFromURL("http://developer.earthweb.com/onlineopinion/onlineopinion.js"));
    ////        insurePathToFileExists(args[0]);
    //////        getFileFromURL(new URL(args[0]), new File(args[1]), null);
    //} catch (Exception ex) {
    //System.out.println(ex);
    ////            ex.printStackTrace();
    //} // endtry
    //    }
}
