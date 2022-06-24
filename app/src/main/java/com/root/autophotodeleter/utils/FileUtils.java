package com.root.autophotodeleter.utils;

import com.root.autophotodeleter.vo.FileInfoVO;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;

public final class FileUtils {

    private FileUtils(){

    }

    public static FileInfoVO getAttributes(File file) throws IOException {
        FileInfoVO fileInfo = new FileInfoVO();
        BasicFileAttributes attr = Files.getFileAttributeView(file.toPath(),
                BasicFileAttributeView.class).readAttributes();
        fileInfo.setCreationDate(LocalDateTime.ofInstant(attr.creationTime().toInstant(),
                ZoneId.systemDefault()));
        fileInfo.setFileSize(attr.size());
        fileInfo.setFileType(getFileExtension(file));
        fileInfo.setFileName(file.getName());
        fileInfo.setFile(file);
        return fileInfo;
    }

    private static String getFileExtension(File file){
        String fileName = file.getName();
        if(StringUtils.isNotEmpty(fileName)){
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);
        }
        return null;
    }

}
