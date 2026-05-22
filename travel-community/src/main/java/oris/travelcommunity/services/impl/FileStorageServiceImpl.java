package oris.travelcommunity.services.impl;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import oris.travelcommunity.services.FileStorageService;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.external-url}")
    private String externalUrl;

    @Override
    public String uploadFile(MultipartFile file, String bucketName) {
        if (file.isEmpty()) {
            return null;
        }
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return externalUrl + "/" + bucketName + "/" + fileName;

        } catch (Exception e) {
            log.error("Ошибка при загрузке файла в MinIO", e);
            throw new IllegalArgumentException("Не удалось сохранить изображение");
        }
    }
}
