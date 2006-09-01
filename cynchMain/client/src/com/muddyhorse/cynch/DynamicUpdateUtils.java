package com.muddyhorse.cynch;

import java.awt.*;
import java.util.*;
import java.io.*;
import java.net.*;

/**
 *
 */
public class DynamicUpdateUtils implements Constants
{

    //
    // Class variables:
    //
    private static final int BUFFER_SIZE = 10000;

    private static Hashtable parms       = new Hashtable();

    private static Frame     mainframe;

    //
    // File Retrieval methods:
    //

    public static String getStringFromClasspath(Class base, String filename) {
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
    public static String getStringFromURL(String address) throws MalformedURLException {
        return getStringFromURL(new URL(address));
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
    public static Hashtable<String, Serializable> stringToHashtable(String file) {
        if (file == null) {
            return null;
        }

        String sansComments = eraseComments(file);
        int pos = 0, count = sansComments.length();
        Hashtable<String, Serializable> retval = new Hashtable<String, Serializable>();

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
                int comma = value.indexOf(",");
                if (comma == -1) {
                    try {
                        retval.put(key, new Double(value));
                    } catch (NumberFormatException nfe) {
                        retval.put(key, value);
                    } // endtry
                } else {
                    Vector<Comparable> v = new Vector<Comparable>();
                    StringTokenizer values = new StringTokenizer(value, ",");
                    while (values.hasMoreTokens()) {
                        String s = values.nextToken();
                        try {
                            v.addElement(new Double(s));
                        } catch (NumberFormatException nfe) {
                            v.addElement(s.trim());
                        } // endtry
                    } // endwhile
                    retval.put(key, v);
                } // endif
            } // endif
            pos += next;
        } // endwhile

        return retval;
    }

    /** Write a hashtable to file */
    public static void writeHashtable(String filename, Hashtable<String, Vector> hash) {
        //*
        try {
            FileWriter fw = new FileWriter(filename);

            for (Enumeration<String> e = hash.keys(); e.hasMoreElements();) {
                String key = e.nextElement();
                String str = key + "=";
                Object value = hash.get(key);

                if (value instanceof String) {
                    str += (String) value;
                } else {
                    Vector v = (Vector) value;
                    int count = v.size();
                    for (int i = 0; i < count; i++) {
                        //                        str += (String)v.elementAt(i);
                        str += v.elementAt(i).toString();
                        if (i < count - 1) {
                            str += ",";
                        }
                    } // endfor
                } // endif
                fw.write(str);
                fw.write("\r\n");
            } // endfor
            fw.close();
        } catch (Exception ex) {
            System.out.println(ex);
            //            ex.printStackTrace();
        } // endtry
        //*/
    }

    /** Compare local and remote hashtables and return a hashtable
     *  of operations to perform.  <STRONG>Note:</STRONG> In the process,
     *  the original remote hashtable is destroyed.
     */
    public static Hashtable<String, Operation> compareHashtables(Hashtable local, Hashtable remote) {
        Hashtable<String, Operation> retval = new Hashtable<String, Operation>();

        // using the keys of the local hashtable, find out what to
        //  ignore (if it is up-to-date already), update or delete:
        for (Enumeration e = local.keys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            Object localObj = local.get(key);
            Object remoteObj = remote.get(key);

            // if this is an update (ie, present in local and remote)
            //  remove from remote.
            if (remoteObj != null) {
                remote.remove(key);
            }

            // we are only interested in vectors, here!
            if (!(localObj instanceof Vector)) {
                continue;
            }
            Vector localVector = (Vector) localObj;

            Operation op = new Operation(key);
            op.loadVector(localVector, true);

            if (remoteObj == null) {
                // there is no remote entry; we should delete:
                op.operation = Constants.OP_DELETE;
            } else {
                // remote entry is present; check to see if we need to update:
                Vector remoteVector = (Vector) remoteObj;
                op.loadVector(remoteVector, false);
                // if remote version newer, mark as update
                if (op.remoteVersion.doubleValue() > op.localVersion.doubleValue()) {
                    op.operation = Constants.OP_UPDATE;
                }
            } // endif

            // put the populated operation into the hashtable:
            retval.put(key, op);
        } // endfor

        // Now, the remote hashtable should only contain things that
        // are NOT on the local side - these are the new downloads.
        for (Enumeration e = remote.keys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            Object remoteObj = remote.get(key);

            if (remoteObj != null) {
                remote.remove(key);
            }

            if (!(remoteObj instanceof Vector)) {
                continue;
            }
            Vector remoteVector = (Vector) remoteObj;

            Operation op = new Operation(key);
            op.loadVector(remoteVector, false);

            // copy remote info to local info:
            //            op.localPath = op.redirectPath; // one day...
            op.localPath = op.remotePath;
            op.localSize = op.remoteSize;
            op.localDescription = op.remoteDescription;
            op.operation = Constants.OP_DOWNLOAD;
            retval.put(key, op);
        } // endfor

        return retval;
    }

    /**
     * @param ids list of IDs to work with.  If null, all IDs will be
     *   accepted.
     * @return an array of Vectors.  An operation is placed in the vector
     *   at the offset corresponding to its operation kind.  All of these
     *   vectors are guaranteed to be non-null;
     */
    public static Vector[] sortOperationsByOp(Hashtable ops, Vector ids) {
        Vector[] vs = new Vector[4];
        for (int i = 0; i < 4; ++i) {
            vs[i] = new Vector();
        } // endfor

        Enumeration enumer = ops.keys();
        while (enumer.hasMoreElements()) {
            Object k = enumer.nextElement();
            if (ids == null || ids.contains(k)) {
                Operation op = (Operation) ops.get(k);
                vs[op.operation].addElement(op);
            } // endif
        } // endwhile
        return vs;
    }

    /**
     * @param ids list of IDs to work with.  If null, all IDs will be
     *   accepted.
     * @return an array of Vectors.  An operation is placed in the vector
     *   at the offset corresponding to its operation kind.  All of these
     *   vectors are guaranteed to be non-null;
     */
    public static Vector[] sortOperationsByType(Hashtable ops, Vector ids) {
        Vector[] vs = new Vector[3];
        for (int i = 0; i < 3; ++i) {
            vs[i] = new Vector();
        } // endfor

        Enumeration enumer = ops.keys();
        while (enumer.hasMoreElements()) {
            Object k = enumer.nextElement();
            if (ids == null || ids.contains(k)) {
                Operation op = (Operation) ops.get(k);
                vs[op.type].addElement(op);
            } // endif
        } // endwhile
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
    public static int performOperations(Config cfg, int type, Vector ops, ProgressListener l)
            throws InterruptedException {
        int errorCnt = 0;
        int size = ops.size();
        for (int i = 0; i < size; ++i) {
            Operation op = (Operation) ops.elementAt(i);
            if (type == TYPE_ALL || type == op.type) {
                if (!performOperation(cfg, op, l)) {
                    ++errorCnt;
                } else {
                    // completed successfully, remove from config:
                    cfg.getOperations().remove(op.fileID);
                } // endif
            } // endif
        } // endfor
        return errorCnt;
    }

    /**
     * @param cfg the DUConfig containing context information
     * @return true if the operation was successful.
     */
    public static boolean performOperation(Config cfg, Operation op, ProgressListener l) throws InterruptedException {
        System.out.println("duu.pO: starting...");
        try {
            int dlSize = 0;
            File f;
            l.starting(op);
            String u = cfg.get(INI_REMOTE_BASE) + op.remotePath;
            String n = cfg.get(INI_LOCAL_BASE) + op.remotePath;
            switch (op.operation) {
                case Constants.OP_UPDATE:
                    // need to:
                    // * download to a temporary location first
                    // * verfiy contents
                    // * delete backup
                    // * rename original to backup
                    // * rename update to original
                    String temp = new File(n).getParent() + File.separator + cfg.getAppShortName() + op.fileID
                            + "_DynUpd.tmp";
                    // uses current directory (which should exist!)...
                    //                File f = File.createTempFile("GTCS","DynUpd"); // 1.3 specific...
                    f = new File(temp);
                    dlSize = getFileFromURL(new URL(u), f, l);
                    if (dlSize > 0 && verifyFile(temp)) {
                        insurePathToFileExists(n); // is this needed? should already exist...
                        File backf = new File(n + ".bak");
                        // delete old backup file:
                        boolean b = false;
                        if (backf.exists()) {
                            // backup exists, delete it:
                            b = backf.delete();
                            if (!b) {
                                // delete unsuccessful...
                                throw new Exception("(Update)File delete problem: " + backf);
                            } // endif
                        } // endif
                        // rename original to backup:
                        File origf = new File(n);
                        b = origf.renameTo(backf);
                        if (!b) {
                            // rename1 unsuccessful...
                            throw new Exception("(Update)File Rename problem: " + origf);
                        } // endif
                        // rename new to original:
                        b = f.renameTo(origf);
                        if (!b) {
                            // rename1 unsuccessful...
                            throw new Exception("(Update)File Rename problem: " + f);
                        } // endif
                    } else {
                        throw new Exception("(Update)File verification problem: " + temp);
                    } // endif
                break;
                case Constants.OP_DOWNLOAD:
                    // need to:
                    // * download to real name
                    // * verfiy contents
                    insurePathToFileExists(n);
                    dlSize = getFileFromURL(new URL(u), new File(n), l);
                    if (dlSize > 0 && verifyFile(n)) {
                        // no action necessary here.
                    } else {
                        throw new Exception("(Download)File verification problem: " + n);
                    } // endif
                break;
                case Constants.OP_DELETE:
                    // need to:
                    // * delete original
                    n = cfg.get(INI_LOCAL_BASE) + op.localPath;
                    f = new File(n);
                    if (!f.delete()) {
                        throw new Exception("File deletion problem: " + n);
                    } // endif
                    if (cfg != null) {
                        cfg.getLocalFiles().remove(op.fileID);
                        // save updated config info:
                        writeHashtable(cfg.getLocalConfigName(), cfg.getLocalFiles());
                    } // endif
                    return true;
                    // break;
                default:
                    throw new Exception("Unknown operation type: " + op.operation);
            } // endswitch

            op.remoteSize = new Double(dlSize);

            if (op.operation != Constants.OP_NOTHING // no file chgs on nothings...
                    && cfg != null) {
                // update config info:
                // note that, once the download is complete, the remote info
                //  and the local info should be the same.
                // thus, use remote info to build vector:
                cfg.getLocalFiles().put(op.fileID, op.toVector(false));
                // save updated config info:
                writeHashtable(cfg.getLocalConfigName(), cfg.getLocalFiles());
            } // endif -- not nothing
            l.finished(op, true);
            return true;
        } catch (InterruptedException ix) {
            throw ix;
        } catch (Exception ex) {
            System.out.println("duu:" + ex);
            //            ex.printStackTrace();
            l.finished(op, false);
            return false;
        } // endtry
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
    public static int countDownloadSize(Config cfg, int type, Vector ids) {
        int total = 0;
        try {
            Enumeration enumer = cfg.getOperations().elements();
            while (enumer.hasMoreElements()) {
                Operation op = (Operation) enumer.nextElement();

                if (type == TYPE_ALL || type == op.type) {
                    // same type
                    if (ids == null || ids.contains(op.fileID)) {
                        // in vector, or vector null
                        if (op.operation == Constants.OP_UPDATE || op.operation == Constants.OP_DOWNLOAD) {
                            total += op.remoteSize.intValue();
                        } // endif
                    } // endif
                } // endif
            } // endwhile
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
    public static int performAllOperations(Config cfg, int type, Vector ids, ProgressListener l)
            throws InterruptedException {
        try {
            int errorCnt;
            Vector[] opSets = sortOperationsByOp(cfg.getOperations(), ids);
            errorCnt = performOperations(cfg, type, opSets[Constants.OP_DOWNLOAD], l);
            errorCnt += performOperations(cfg, type, opSets[Constants.OP_UPDATE], l);
            errorCnt += performOperations(cfg, type, opSets[Constants.OP_DELETE], l);
            // of course, skip OP_NOTHING operations...
            return errorCnt;
        } catch (InterruptedException ix) {
            throw ix;
        } // endtry
    }

    public static void setParameters(Hashtable parameters) {
        Enumeration enumer = parameters.keys();
        while (enumer.hasMoreElements()) {
            Object k = enumer.nextElement();
            parms.put(k, parameters.get(k));
        } // endwhile
    }

    public static String getParameter(String which) {
        return (String) parms.get(which);
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
                Thread.sleep(1000);
            } // endwhile
            System.out.println("duu.gFFURL: downloaded " + (available - left) + " of " + available);
            System.out.println("duu.gFFURL: from " + rmt + " to " + local);
            return available - left;
        } catch (InterruptedException ix) {
            //System.out.println("duu.gFFURL: caught ix!! @"+System.currentTimeMillis());
            throw ix;
        } catch (Exception ex) {
            System.out.println(ex);
            //            ex.printStackTrace();
            return -1;
        } finally {
            try {
                is.close();
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
                fs.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } // endtry
        } // endtry
    }

    /**
     * @return the number of bytes downloaded, -1 if error occurred
     */
    public static int getFileFromClasspath(Class base, String rmt, File local) {
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
            System.out.println("duu.gFFC: downloaded " + (available - left) + " of " + available);
            System.out.println("duu.gFFC: from " + rmt + " to " + local);
            return available - left;
        } catch (Exception ex) {
            //System.out.println(ex);
            ex.printStackTrace();
            return -1;
        } finally {
            try {
                is.close();
                fs.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } // endtry
        } // endtry
    }

    /**
     * @return the number of bytes saved, -1 if error occurred
     */
    public static int getFileFromString(String data, File local) {
        PrintWriter fs = null;

        try {
            if (data == null || local == null) {
                return -1;
            }

            byte[] buffer = data.getBytes();
            //            final int available = buffer.length;
            fs = new PrintWriter(new BufferedOutputStream(new FileOutputStream(local), BUFFER_SIZE));
            fs.print(data);
            fs.flush();
            //System.out.println("duu.gFFS: saved "+available+" of "+available);
            //System.out.println("duu.gFFS: from string to "+local);
            return data.length();
        } catch (Exception ex) {
            //System.out.println(ex);
            ex.printStackTrace();
            return -1;
        } finally {
            try {
                fs.close();
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
        //System.out.println("duu.sA: running: "+appAndArgs);

        try {
            //            Process p = Runtime.getRuntime().exec(appAndArgs, null, new File(startDir));
            Process p = Runtime.getRuntime().exec(appAndArgs, null);
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
        String parent = f.getParent();
        File p = new File(parent);
        //System.out.println("duu.iPE: parent of "+filename+" is "+parent);

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

    private static boolean verifyFile(String name) {
        return true;
    }

    public static void removeNonExistantFiles(Hashtable filesHash, String base) {
        Enumeration enumer = filesHash.keys();
        while (enumer.hasMoreElements()) {
            Object k = enumer.nextElement();
            Vector v = (Vector) filesHash.get(k);

            Operation op = new Operation(k.toString());
            op.loadVector(v, true); // is local...

            // op is prepped:
            String n = base + op.localPath;
            System.out.println("c.d.DUU.rNEF: checking file " + n);
            if (!new File(n).exists()) {
                // file doesn't exist locally; rmv entry
                System.out.println("c.d.DUU.rNEF:  removing non-existant file " + n);
                filesHash.remove(k);
            } // endif
        } // endwhile
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
