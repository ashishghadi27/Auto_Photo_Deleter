package com.root.autophotodeleter.vo;

import java.io.File;
import java.time.LocalDateTime;

public class FileInfoVO {

    private String fileName;
    private long fileSize;
    private String fileType;
    private LocalDateTime creationDate;
    private File file;

    public FileInfoVO(){

    }

    public FileInfoVO(String fileName, long fileSize, String fileType, LocalDateTime creationDate) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.creationDate = creationDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "FileInfoVO{" +
                "fileName='" + fileName + '\'' +
                ", fileSize=" + fileSize +
                ", fileType='" + fileType + '\'' +
                ", creationDate=" + creationDate +
                '}';
    }
}
