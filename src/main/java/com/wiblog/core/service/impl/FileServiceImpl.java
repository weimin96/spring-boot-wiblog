package com.wiblog.core.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wiblog.core.common.Constant;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.entity.Picture;
import com.wiblog.core.mapper.PictureMapper;
import com.wiblog.core.service.IFileService;
import com.wiblog.core.thirdparty.CosApi;
import com.wiblog.core.utils.ImageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author pwm
 * @date 2019/10/1
 */
@Slf4j
@Service
@PropertySource(value = "classpath:/wiblog.properties", encoding = "utf-8")
public class FileServiceImpl implements IFileService {

    @Autowired
    private PictureMapper pictureMapper;

    @Value("${qcloud-secret-id}")
    private String secretId;

    @Value("${qcloud-secret-key}")
    private String secretKey;

    @Value("${qcloud-oss-bucket-site}")
    private String bucketSite;

    @Value("${qcloud-oss-bucket-name}")
    private String bucketName;

    @Value("${qcloud-oss-path}")
    private String path;

    @Override
    public ServerResponse<?> uploadImage(MultipartFile file, String type) {
        if (StringUtils.isBlank(file.getOriginalFilename())) {
            return ServerResponse.error("文件为空", 40001);
        }
        CosApi cosApi = new CosApi(secretId, secretKey, bucketSite);
        // 生成文件名
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss-");
        String fileName = sf.format(date) + file.getOriginalFilename();
        try {
            // 压缩图片
            InputStream in = ImageUtil.compressPicForScale(file.getInputStream());
            String eTag = cosApi.uploadFile(in, fileName, bucketName);
            if ("file name error".equals(eTag)) {
                return ServerResponse.error("文件名不能带特殊字符", 40003);
            } else if (eTag != null) {
                Picture picture = new Picture(fileName, type, path + fileName, "", date, date);
                pictureMapper.insert(picture);
                return ServerResponse.success(path + fileName, "图片上传成功");
            }
        } catch (IOException e) {
            log.error("异常", e);
        }
        return ServerResponse.error("图片上传失败", 40002);
    }

    @Override
    public Map<String, Object> uploadImageForEditorMd(MultipartFile file) {
        // 返回的数据结果
        Map<String, Object> result = new HashMap<>(3);
        if (StringUtils.isNotBlank(file.getOriginalFilename())) {
            CosApi cosApi = new CosApi(secretId, secretKey, bucketSite);
            // 生成文件名
            Date date = new Date();
            SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss-");
            String fileName = sf.format(date) + file.getOriginalFilename();
            try {
                // 压缩图片
                InputStream in = ImageUtil.compressPicForScale(file.getInputStream());
                String eTag = cosApi.uploadFile(in, fileName, bucketName);
                if ("file name error".equals(eTag)) {
                    result.put("message", "文件名不能带特殊字符");
                } else if (eTag != null) {
                    result.put("success", 1);
                    result.put("message", "图片上传成功");
                    result.put("url", path + fileName);
                    Picture picture = new Picture(fileName, "img", path + fileName, "", date, date);
                    pictureMapper.insert(picture);
                    return result;
                } else {
                    result.put("message", "图片上传失败");
                }
            } catch (IOException e) {
                log.error("异常", e);
            }
        }

        result.put("success", 0);
        result.put("url", "");
        return result;
    }

    @Override
    public ServerResponse<?> getImageList(Integer pageNum, Integer pageSize) {
        Page<Picture> page = new Page<>(pageNum, pageSize);
        IPage<Picture> iPage = pictureMapper.selectPageList(page);
        return ServerResponse.success(iPage, "获取图片列表成功");
    }

    @Override
    public ServerResponse<?> delImage(Long id) {
        Picture picture = pictureMapper.selectById(id);
        CosApi cosApi = new CosApi(secretId, secretKey, bucketSite);
        boolean tag = cosApi.removeFile(bucketName, picture.getName());
        if (tag) {
            int count = pictureMapper.deleteById(id);
            if (count > 0) {
                return ServerResponse.success("删除成功");
            }
        }
        return ServerResponse.error("删除失败", 30001);
    }

    @Override
    public ServerResponse<?> getLogList() {
        List<Map<String, Object>> fileList = new ArrayList<>();
        try {
            File file = new File(System.getProperty("user.dir")+"/logs");
            File[] files = file.listFiles();
            if (files == null){
                return ServerResponse.success(null, "文件不存在，请检查文件路径");
            }
            for (File value : files) {
                if (value.isFile()) {
                    Map<String, Object> temp = new HashMap<>(2);
                    temp.put("path", value.getPath());
                    temp.put("name", value.getName());
                    fileList.add(temp);
                }
            }
        } catch (Exception e) {
            log.error("异常", e);
        }

        return ServerResponse.success(fileList, "获取日志列表成功");
    }

    @Override
    public ServerResponse<?> showLog(String path, Integer pageNum) {
        Map<String, Object> result = new HashMap<>(2);
        try {
            Path paths = Paths.get(path);
            Stream<String> lines = Files.lines(paths);
            List<String> list = lines.skip(pageNum * 10).limit(10).collect(Collectors.toList());
            result.put("list", list);
            result.put("total", Files.lines(paths).count());
        } catch (IOException e) {
            log.error("异常", e);
        }
        return ServerResponse.success(result);
    }
}
