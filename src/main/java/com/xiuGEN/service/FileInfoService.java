package com.xiuGEN.service;

import com.github.pagehelper.PageInfo;
import com.xiuGEN.pojo.FileInfo;
import com.xiuGEN.pojo.User;

import java.util.List;

public interface FileInfoService {
    public int addFileInfo(FileInfo fileInfo);
    public List<FileInfo> getFilesByUser(User user);
    public PageInfo<FileInfo> getPageFiles(User user, int page, int pagecount);
    public List<FileInfo> getFileType(int uid,String tname);
    public List<FileInfo> getFileType(int uid);
    public List<FileInfo> searchFilesByName(int uid, String tname, String filename);
    public List<FileInfo> searchFilesByName(int uid,String filename);
    public int delFileByFileName(int uid,String fileName);
}
