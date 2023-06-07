package com.xiuGEN.mapper;

import com.xiuGEN.pojo.FileInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FileInfoMapper {
    public List<FileInfo> selFilesByUid(int uid);
    public List<FileInfo> selFilesByType(@Param("uid")int uid,@Param("tname")String tname);
    public List<FileInfo> selRecently();
    public List<FileInfo> selFilesByOther(int uid);
    public List<FileInfo> selFilesByName_filetype(@Param("uid")int uid,@Param("tname")String tname,@Param("filename")String filename);
    public List<FileInfo> selFilesByName(@Param("uid")int uid,@Param("filename")String filename);
    public int selSumSize(int uid);
    public int insFileInfo(FileInfo fileInfo);
    public int delFile(@Param("uid") int uid,@Param("fileName")String fileName);
}
