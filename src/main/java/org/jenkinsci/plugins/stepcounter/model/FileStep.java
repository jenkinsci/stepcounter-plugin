package org.jenkinsci.plugins.stepcounter.model;

import java.io.File;
import java.io.Serializable;

public class FileStep implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1395778114180423941L;

	File _file;

    long _total;

    long _comments;

    long _runs;

    long _blanks;

    String _fileType;

    String _fileName;

    String _parentDirRelativePath;

    public File getFile() {
        return _file;
    }

    public void setFile(File file) {
        _file = file;
    }

    public long getTotal() {
        return _total;
    }

    public void setTotal(long total) {
        _total = total;
    }

    public long getComments() {
        return _comments;
    }

    public void setComments(long comments) {
        _comments = comments;
    }

    public long getRuns() {
        return _runs;
    }

    public void setRuns(long runs) {
        _runs = runs;
    }

    public long getBlanks() {
        return _blanks;
    }

    public void setBlanks(long blanks) {
        _blanks = blanks;
    }

    public String getFileType() {
        return _fileType;
    }

    public void setFileType(String fileType) {
        _fileType = fileType;
    }

    public String getFileName() {
        return _fileName;
    }

    public void setFileName(String fileName) {
        _fileName = fileName;
    }

    public String getParentDirRelativePath() {
        return this._parentDirRelativePath;
    }

    public void setParentDirRelativePath(String parentDirRelativePath) {
        this._parentDirRelativePath = parentDirRelativePath;

    }

}
