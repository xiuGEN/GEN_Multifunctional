package com.xiuGEN.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiuGEN.mapper.FileInfoMapper;
import com.xiuGEN.pojo.FileInfo;
import com.xiuGEN.pojo.User;
import com.xiuGEN.service.FileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class FileInfoServiceImpl implements FileInfoService {
    @Autowired
    private FileInfoMapper fileInfoMapper;
    @Override
    public int addFileInfo(FileInfo fileInfo) {
         return fileInfoMapper.insFileInfo(fileInfo);
    }
    @Override
    public List<FileInfo> getFilesByUser(User user) {
        return fileInfoMapper.selFilesByUid(user.getUid());
    }
    @Override
    public PageInfo<FileInfo> getPageFiles(User user, int page, int pageSize) {
        PageHelper.startPage(page,pageSize);
        PageInfo<FileInfo> pageInfo = new PageInfo<>(getFilesByUser(user));
        return pageInfo;
    }

    @Override
    public List<FileInfo> getFileType(int uid, String tname) {
        return fileInfoMapper.selFilesByType(uid,tname);
    }
    /*
    * 获取其他未标记的类型
    * */
    @Override
    public List<FileInfo> getFileType(int uid) {
        return fileInfoMapper.selFilesByOther(uid);
    }

    @Override
    public List<FileInfo> searchFilesByName(int uid, String tname, String filename) {
        return fileInfoMapper.selFilesByName_filetype(uid,tname,filename);
    }

    @Override
    public List<FileInfo> searchFilesByName(int uid, String filename) {
        return fileInfoMapper.selFilesByName(uid,filename);
    }

    @Override
    public int delFileByFileName(int uid, String fileName) {
        return fileInfoMapper.delFile(uid,fileName);
    }


}
