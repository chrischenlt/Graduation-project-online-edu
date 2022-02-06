package com.clt.service.sms.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CannedAccessControlList;
import com.clt.service.sms.service.FileService;
import com.clt.service.sms.utils.OssProperties;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.UUID;

/**
 * @Author 陈力天
 * @Date 2022/1/16
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private OssProperties ossProperties;

    private String endpoint;
    private String keyid;
    private String keysecret;
    private String bucketname;

    @PostConstruct
    private void init() {
        endpoint = ossProperties.getEndpoint();
        keyid = ossProperties.getKeyid();
        keysecret = ossProperties.getKeysecret();
        bucketname = ossProperties.getBucketname();
    }

    @Override
    public String upload(InputStream inputStream, String module, String originalFilename) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, keysecret);
        if (!ossClient.doesBucketExist(bucketname)) {
            ossClient.createBucket(bucketname);
            ossClient.setBucketAcl(bucketname, CannedAccessControlList.PublicRead);
        }
        try {
            //构建objectName：文件路径 avatar/2022/01/01/default.jpg
            String folder = new DateTime().toString("yyyy/MM/dd");
            String fileName = UUID.randomUUID().toString();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String key = module + "/" + folder + "/" + fileName + fileExtension;

            //上传文件流
            ossClient.putObject(bucketname, key, inputStream);

            //返回url
            return "https://" + bucketname + "." + endpoint + "/" + key;
        } finally {
            ossClient.shutdown();
        }
    }

    @Override
    public void removeFile(String url) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, keyid, keysecret);
        try {
            //删除文件
            String host = "https://" + bucketname + "." + endpoint + "/";
            String objectName = url.substring(host.length());
            ossClient.deleteObject(bucketname, objectName);
        } finally {
            ossClient.shutdown();
        }
    }
}
