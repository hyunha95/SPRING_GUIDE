package com.example.uploadingfiles.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;

    @Autowired
    public FileSystemStorageService(StorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file.");
            }

            Path destinationFile = this.rootLocation.resolve( // resolve() 메서드는 현재 경로에 상대 경로를 붙여서 새로운 경로를 생성합니다.
                                Paths.get(file.getOriginalFilename()))
                    .normalize() // .normalize(): 경로를 정규화하는 메서드입니다. 경로에서 불필요한 구성 요소를 제거하여 일관된 형식으로 표현합니다.
                                 // 정규화된 경로는 동일한 파일이더라도 경로 표현이 다른 상황을 방지하고, 경로 처리와 비교가 더욱 일관성 있게 이루어질 수 있도록 도와줍니다. 파일 경로를 다룰 때, 특히 다양한 운영 체제나 다른 소스에서 받아온 경우 경로 정규화는 중요한 과정입니다.
                    .toAbsolutePath(); // 정규화된 파일 경로를 절대 경로로 변환하는 메서드입니다. 이 메서드를 호출하면 상대 경로가 포함된 경로가 시스템의 절대 경로로 변환됩니다.
                                       // toAbsolutePath() 메서드는 주어진 경로를 상대 경로에서 절대 경로로 변환하여 경로 처리에 일관성을 제공하고 파일의 위치를 명확하게 알려주는데 사용됩니다.

            if(!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new StorageException("Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch(IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    /**
     * 이 코드는 지정된 디렉토리 내의 파일을 스트림으로 반환하는 메서드로, 파일 시스템에서 특정 디렉토리 내의 파일들을 나열하고 처리하는 데 사용될 수 있습니다.
     */
    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1) // Files.walk() 메서드는 지정된 경로에서 모든 하위 디렉토리와 파일을 재귀적으로 순회하는 스트림을 생성합니다.
                                                             // 여기서는 rootLocation 디렉토리에서 최대 1단계 아래까지 순회하도록 설정되었습니다.
                    .filter(path -> !path.equals(this.rootLocation)) // .filter(path -> !path.equals(this.rootLocation)): 스트림 내의 요소들 중에서 rootLocation 경로와 같지 않은 요소들만 필터링합니다.
                                                                     // 이를 통해 rootLocation 디렉토리 자체는 제외됩니다.
                    .map(this.rootLocation::relativize); // relativize() 메서드는 주어진 경로와 현재 경로 사이의 상대 경로를 생성합니다.
                                                         // 여기서는 rootLocation를 기준으로 상대 경로들을 생성합니다. 이러한 상대 경로들이 최종적으로 스트림으로 반환됩니다.
        } catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(String filename) {
        return this.rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(this.rootLocation.toFile());
    }
}
