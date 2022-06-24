package com.root.autophotodeleter.operationHandlers;

import android.util.Log;

import com.root.autophotodeleter.utils.Constants;
import com.root.autophotodeleter.utils.FileUtils;
import com.root.autophotodeleter.vo.FileInfoVO;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class CameraPicsReader {

    private CameraPicsReader(){

    }

    public static List<FileInfoVO> getCameraFilesCapturedToday() throws IOException {
        return getCameraFiles(LocalDateTime.now(), LocalDateTime.now().minusDays(1));
    }

    public static List<FileInfoVO> getCameraFilesCapturedLastWeek() throws IOException {
        return getCameraFiles(LocalDateTime.now(), LocalDateTime.now().minusWeeks(1));
    }

    public static List<FileInfoVO> getCameraFilesCapturedPastSevenDays() throws IOException {
        return getCameraFiles(LocalDateTime.now(), LocalDateTime.now().minusDays(7));
    }

    public static List<FileInfoVO> getCameraFilesCapturedLastMonth() throws IOException {
        return getCameraFiles(LocalDateTime.now(), LocalDateTime.now().minusMonths(1));
    }

    public static List<FileInfoVO> getCameraFilesCapturedInSelectedTimeSpan(LocalDateTime startDate
            ,LocalDateTime endDate) throws IOException {
        return getCameraFiles(startDate, endDate);
    }

    public static List<FileInfoVO> getCameraFiles(LocalDateTime startDate ,LocalDateTime endDate) throws IOException {
        List<FileInfoVO> fileInfoList = new ArrayList<>();
        File cameraDirectory = new File(Constants.cameraPath);
        Log.i("Files Directory", Constants.cameraPath);
        File[] files = cameraDirectory.listFiles();

        if(files != null){
            Log.i("Files array SIze", files.length + "");
            for(File file : files){
                FileInfoVO fileInfo = FileUtils.getAttributes(file);
                if (isValidFile(startDate, endDate, fileInfo.getCreationDate())){
                    fileInfoList.add(fileInfo);
                }
            }
        }
        return fileInfoList;
    }

    private static boolean isValidFile(LocalDateTime startDate ,LocalDateTime endDate,
                                       LocalDateTime fileDate){
        return (fileDate.isBefore(startDate) || fileDate.equals(startDate))
                && (fileDate.isAfter(endDate) || fileDate.equals(endDate));
    }
}
