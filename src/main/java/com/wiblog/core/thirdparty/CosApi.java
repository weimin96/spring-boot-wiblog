package com.wiblog.core.thirdparty;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.region.Region;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 腾讯云对象存储
 * @author pwm
 * @date 2019/10/1
 */
@Slf4j
public class CosApi {

    private COSClient client;

    public CosApi(String secretId, String secretKey, String bucketSite) {
        // 1 初始化用户身份信息（secretId, secretKey）。
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        // 2 设置 bucket 的区域
        Region region = new Region(bucketSite);
        ClientConfig clientConfig = new ClientConfig(region);
        // 3 生成 cos 客户端。
        this.client = new COSClient(cred, clientConfig);
    }

    /**
     * 上传文件
     * 如创建一个对象键为 xxx/yyy/zzz.txt的文件，只用把 key 设置为xxx/yyy/zzz.txt即可
     *
     * @param in       in
     * @param folder     存放路径
     * @param bucketName 存储桶名称
     */
    public String uploadFile(InputStream in, String folder, String bucketName) {
        try {
            if (StringUtils.isBlank(bucketName)) {
                throw new RuntimeException("腾讯云OCS 尚未指定Bucket！");
            }
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 从输入流上传必须制定content length, 否则http客户端可能会缓存所有数据，存在内存OOM的情况
            objectMetadata.setContentLength(in.available());
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, folder, in, objectMetadata);
            // 设置存储类型, 默认是标准(Standard), 低频(standard_ia)
            putObjectRequest.setStorageClass(StorageClass.Standard);

            PutObjectResult putObjectResult = this.client.putObject(putObjectRequest);
            return putObjectResult.getETag();
        } catch (CosServiceException e){
            // 文件名带某些特殊符号会抛异常 例如+
            return "file name error";
        }
        catch (IOException | CosClientException e) {
            log.error("异常",e);
        } finally {
            this.client.shutdown();
        }
        return null;
    }

    /**
     * 获取文件列表
     * @param bucketName 存储桶名称
     * @return List<Map<String,Object>>
     */
    public List<Map<String,Object>> listFile(String bucketName) {
        List<Map<String,Object>> fileList = new ArrayList<>();
        try {
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
            // 设置bucket名称
            listObjectsRequest.setBucketName(bucketName);
            // prefix表示列出的object的key以prefix开始
            listObjectsRequest.setPrefix("");
            // 设置最大遍历出多少个对象, 一次listobject最大支持1000
            listObjectsRequest.setMaxKeys(1000);
            ObjectListing objectListing = this.client.listObjects(listObjectsRequest);
            // common prefix表示表示被delimiter截断的路径, 如delimter设置为/, common prefix则表示所有子目录的路径
            List<String> commonPrefixs = objectListing.getCommonPrefixes();

            // object summary表示所有列出的object列表
            List<COSObjectSummary> cosObjectSummaries = objectListing.getObjectSummaries();
            Map<String,Object> result = null;
            for (COSObjectSummary cosObjectSummary : cosObjectSummaries) {
                // 文件的路径key
                String key = cosObjectSummary.getKey();
                // 文件的etag
                String etag = cosObjectSummary.getETag();
                // 文件的长度
                long fileSize = cosObjectSummary.getSize();
                result = new HashMap<>(16);
                result.put("name",key);
                result.put("size",fileSize);
                fileList.add(result);

            }
        } catch (CosClientException e) {
            log.error("异常",e);
        } finally {
            this.client.shutdown();
        }

        return fileList;
    }

    /**
     * 移除文件
     * @param bucketName 存储桶名称
     * @param folder 存放路径
     */
    public boolean removeFile(String bucketName,String folder){
        try {
            this.client.deleteObject(bucketName, folder);
        } catch (CosClientException serverException) {
            serverException.printStackTrace();
            return false;
        } finally {
            this.client.shutdown();
        }
        return true;
    }

}
