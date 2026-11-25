package fr.datashare.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

// Am.Yo : interface pour pouvoir brancher S3 plus tard
public interface StorageService {

    void store(String storageKey, MultipartFile file);

    InputStream open(String storageKey);

    void delete(String storageKey);
}
