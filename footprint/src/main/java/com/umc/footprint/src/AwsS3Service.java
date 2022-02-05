package com.umc.footprint.src;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.umc.footprint.config.BaseException;
import com.umc.footprint.config.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    // 파일 여러 개 넣을 때
    public ArrayList<String> uploadFile(List<MultipartFile> multipartFile) {
        ArrayList<String> photoUrlList = new ArrayList<>();
        System.out.println("AwsS3Service.uploadFile start");


        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
        multipartFile.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try(InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch(IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }

            photoUrlList.add(amazonS3.getUrl(bucket, fileName).toString());
        });

        System.out.println("getFileExtension(photoUrlList.get(0)) = " + getFileExtension(photoUrlList.get(0)));

        System.out.println("AwsS3Service.uploadFile end");

        return photoUrlList;
    }

    // 파일 하나 넣을 때
    public String uploadFile(MultipartFile oneMultipartFile) throws BaseException {
        String photoUrl;
        System.out.println("AwsS3Service.uploadFile start");


        System.out.println("oneMultipartFile.getOriginalFilename() = " + oneMultipartFile.getOriginalFilename());
        String OnefileName = createFileName(oneMultipartFile.getOriginalFilename());
        System.out.println("OnefileName = " + OnefileName);
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(oneMultipartFile.getSize());
        System.out.println("objectMetadata.getContentLength() = " + objectMetadata.getContentLength());
        objectMetadata.setContentType(oneMultipartFile.getContentType());
        System.out.println("objectMetadata.getContentType() = " + objectMetadata.getContentType());

        try (InputStream inputStream = oneMultipartFile.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(bucket, OnefileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException exception) {
            throw new BaseException(BaseResponseStatus.S3UPLOAD_ERROR);
        }

        photoUrl = amazonS3.getUrl(bucket, OnefileName).toString();
        System.out.println("getFileExtension(photo) = " + getFileExtension(photoUrl));

        System.out.println("AwsS3Service.uploadFile end");
        return photoUrl;
    }

    public void deleteFile(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    private String createFileName(String fileName) { // 먼저 파일 업로드 시, 파일명을 난수화하기 위해 random으로 돌립니다.
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) { // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직이며, 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단하였습니다.
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }
}