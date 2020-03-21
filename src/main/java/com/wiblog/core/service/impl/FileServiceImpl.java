package com.wiblog.core.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.Picture;
import com.wiblog.core.mapper.PictureMapper;
import com.wiblog.core.service.IFileService;
import com.wiblog.core.thirdparty.CosApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pwm
 * @date 2019/10/1
 */
@Slf4j
@Service
@PropertySource(value = "classpath:/config/wiblog.properties", encoding = "utf-8")
public class FileServiceImpl implements IFileService {

    @Autowired
    private PictureMapper pictureMapper;

    @Value("${qcloud-secret-id}")
    private String secretId;

    @Value("${qcloud-secret-key}")
    private String secretkey;

    @Value("${qcloud-oss-bucket-site}")
    private String bucketSite;

    @Value("${qcloud-oss-bucket-name}")
    private String bucketName;

    @Value("${qcloud-oss-path}")
    private String path;

    @Override
    public ServerResponse uploadImage(MultipartFile file,String type) {
        if(StringUtils.isBlank(file.getOriginalFilename())){
            return ServerResponse.error("文件为空",40001);
        }
        CosApi cosApi = new CosApi(secretId,secretkey,bucketSite);
        // 生成文件名
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss-");
        String fileName = sf.format(date)+file.getOriginalFilename();
        try {
            String eTag = cosApi.uploadFile(file.getInputStream(),fileName,bucketName);
            if ("file name error".equals(eTag)){
                return ServerResponse.error("文件名不能带特殊字符",40003);
            }else if(eTag != null){
                Picture picture = new Picture(fileName,type,path+fileName,"",date,date);
                pictureMapper.insert(picture);
                return ServerResponse.success(path+fileName,"图片上传成功");
            }
        } catch (IOException e) {
            log.error("异常",e);
        }
        return ServerResponse.error("图片上传失败",40002);
    }

    @Override
    public Map<String, Object> uploadImageForEditorMd(MultipartFile file) {
        // 返回的数据结果
        Map<String, Object> result = new HashMap<>(3);
        if(StringUtils.isNotBlank(file.getOriginalFilename())){
            CosApi cosApi = new CosApi(secretId,secretkey,bucketSite);
            // 生成文件名
            Date date = new Date();
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss-");
            String fileName = sf.format(date)+file.getOriginalFilename();
            try {
                String eTag = cosApi.uploadFile(file.getInputStream(),fileName,bucketName);
                if ("file name error".equals(eTag)){
                    result.put("message", "文件名不能带特殊字符");
                }else if(eTag != null){
                    result.put("success", 1);
                    result.put("message", "图片上传成功");
                    result.put("url", path+fileName);
                    Picture picture = new Picture(fileName,"img",path+fileName,"",date,date);
                    pictureMapper.insert(picture);
                    return result;
                }else{
                    result.put("message", "图片上传失败");
                }
            } catch (IOException e) {
                log.error("异常",e);
            }
        }

        result.put("success", 0);
        result.put("url", "");
        return result;
    }

    @Override
    public ServerResponse getImageList(Integer pageNum,Integer pageSize){
        Page<Picture> page = new Page<>(pageNum,pageSize);
        IPage<Picture> iPage = pictureMapper.selectPageList(page);
        return ServerResponse.success(iPage,"获取图片列表成功");
    }

    @Override
    public ServerResponse delImage(Long id) {
        Picture picture = pictureMapper.selectById(id);
        CosApi cosApi = new CosApi(secretId,secretkey,bucketSite);
        boolean tag = cosApi.removeFile(bucketName,picture.getName());
        if (tag){
            int count = pictureMapper.deleteById(id);
            if (count>0){
                return ServerResponse.success("删除成功");
            }
        }
        return ServerResponse.error("删除失败",30001);
    }

    @Override
    public ServerResponse getLogList() {
        return ServerResponse.success(null,"获取日志列表成功");
    }
}
