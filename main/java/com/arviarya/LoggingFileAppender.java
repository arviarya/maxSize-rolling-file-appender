
package com.arviarya;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;


/**
 * This is Log4j appender and is using Rolling file appender. It create log file on daily basis with support of maximum
 * size of file on a day. If size of a file is reached to defined limit (in properties file), it starts creating new
 * file on same date stamp with appending number.
 * 
 * @author arvindkumar
 */
public class LoggingFileAppender extends RollingFileAppender {
    private String fileNameFormat;
    private SimpleDateFormat sdf;
    private String currentDate;
    private String orgFile;

    public LoggingFileAppender() {
        fileNameFormat = "yyyy-MM-dd";
        sdf = new SimpleDateFormat(fileNameFormat);
        currentDate = sdf.format(new Date());
        orgFile = null;
    }

    @Override
    public void subAppend(LoggingEvent event) {
        try {
            if (!checkDate()) {
                final String fName = getFileName();
                setFile(fName, fileAppend, bufferedIO, bufferSize);
            }
        }
        catch (final IOException e) {
            LogLog.warn("Error creating file");
        }
        super.subAppend(event);
    }

    @Override
    public void setFile(String s) {
        orgFile = s;
        super.setFile(s);
    }

    @Override
    public void activateOptions() {
        getFileName();
        LogLog.debug("file name" + fileName);
        super.activateOptions();
    }

    public String getFileNameFormat() {
        return fileNameFormat;
    }

    public void setFileNameFormat(String fileNameFormat) {
        try {
            sdf = new SimpleDateFormat(fileNameFormat);
            currentDate = sdf.format(new Date());
        }
        catch (final Exception e) {
            LogLog.warn("Invalid format");
        }
        this.fileNameFormat = fileNameFormat;
    }

    /**
     * @return
     */
    private String getFileName() {
        if (orgFile != null) {
            final StringBuilder sbuf = new StringBuilder(orgFile);
            final int rslt = orgFile.indexOf("%d");
            if (rslt >= 0) {
                sbuf.replace(rslt, rslt + 2, sdf.format(new Date()));
            }
            fileName = sbuf.toString();
        }
        return fileName;
    }

    /**
     * @return
     */
    private boolean checkDate() {
        final String newDate = sdf.format(new Date());
        if (currentDate.equals(newDate)) {
            return true;
        }
        currentDate = newDate;

        return false;
    }

}
