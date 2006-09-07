/**
 * 
 */
package com.muddyhorse.cynch.admin;

import com.muddyhorse.cynch.manifest.DownloadType;
import com.muddyhorse.cynch.manifest.RemoteFileInfo;
import com.muddyhorse.table.TableColumnInfo;

public enum AdminColumns implements TableColumnInfo<RemoteFileInfo> {
    downloadType("Download Type", DownloadType.class) {
        @Override
        public Object getData(RemoteFileInfo dataValue) {
            return dataValue.getDownloadType();
        }
    },
    remotePath        ("Remote Path"         , String.class) {
        @Override
        public Object getData(RemoteFileInfo dataValue) {
            return dataValue.getRawURL();
        }
    },
    localPath        ("Local Path"         , String.class) {
        @Override
        public Object getData(RemoteFileInfo dataValue) {
            return dataValue.getRawLocalPath();
        }
    },
    version     ("Version"      , Double.class) {
        @Override
        public Object getData(RemoteFileInfo dataValue) {
            return dataValue.getVersion();
        }
    },
    size        ("Size"         , Long.class){
        @Override
        public Object getData(RemoteFileInfo dataValue) {
            return dataValue.getSize();
        }
    },
    description ("Description"  , String.class) {
        @Override
        public Object getData(RemoteFileInfo dataValue) {
            return dataValue.getDescription();
        }
    };
    private final Class<?> fieldClass;
    private final String   desc;
    private AdminColumns(String desc, Class<?> fieldClass) {
        this.desc = desc;
        this.fieldClass = fieldClass;
    }
    public String getColumnName() {
        return desc;
    }
    public Class<?> getColumnClass() {
        return fieldClass;
    }

    public abstract Object getData(RemoteFileInfo dataValue);

    // old order:
//  private static final int        OFS_DL_TYPE    = 0;  // where ofs == offset...
//  private static final int        OFS_PATH       = 1;
//  private static final int        OFS_VERSION    = 2;
//  private static final int        OFS_SIZE       = 3;
//  private static final int        OFS_DESC       = 4;
  //    private static final int OFS_REDIR    = 5;

}