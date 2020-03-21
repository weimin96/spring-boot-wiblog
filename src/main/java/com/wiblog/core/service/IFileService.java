package com.wiblog.core.service;

import com.wiblog.core.common.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author pwm
 * @date 2019/10/1
 */
public interface IFileService {

    /**
     * 腾讯云对象存储 上传图片
     * @param file file
     * @param type type img avatar
     * @return ServerResponse
     */
    ServerResponse uploadImage(MultipartFile file,String type);

    /**
     * md编辑器 腾讯云对象存储 上传图片
     * @param file file
     * @return ServerResponse
     */
    Map<String, Object> uploadImageForEditorMd(MultipartFile file);

    /**
     * 分页获取图片
     * @param pageNum pageNum
     * @param pageSize pageSize
     * @return ServerResponse
     */
    ServerResponse getImageList(Integer pageNum, Integer pageSize);

    /**
     * 删除图片
     * @param id id
     * @return ServerResponse
     */
    ServerResponse delImage(Long id);

    /**
     * 获取日志列表
     * @return ServerResponse
     */
    ServerResponse getLogList();
}
