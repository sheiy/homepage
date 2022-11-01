package site.ownw.homepage.domain.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import site.ownw.homepage.common.enums.FileType;
import site.ownw.homepage.common.exception.BusinessException;
import site.ownw.homepage.common.exception.EntityNotFoundException;
import site.ownw.homepage.controller.file.model.CreateFolderRequest;
import site.ownw.homepage.controller.file.model.UpdateFileRequest;
import site.ownw.homepage.controller.file.model.UpdateFolderRequest;
import site.ownw.homepage.domain.file.model.CreateFileParam;
import site.ownw.homepage.domain.file.model.GetFileResult;
import site.ownw.homepage.domain.file.model.GetFilesItem;
import site.ownw.homepage.domain.file.repository.UserFileRepository;
import site.ownw.homepage.domain.file.repository.UserFolderRepository;
import site.ownw.homepage.entity.UserFile;
import site.ownw.homepage.entity.UserFolder;

@Slf4j
@Service
@Validated
public class FileService {

    private final Path userUploadFileRootPath;
    private final UserFileRepository userFileRepository;
    private final UserFolderRepository userFolderRepository;

    public FileService(
            @Value("${app.user-upload-file-root-path}") String userUploadFileRootPath,
            UserFileRepository userFileRepository,
            UserFolderRepository userFolderRepository) {
        this.userUploadFileRootPath = Paths.get(userUploadFileRootPath);
        this.userFileRepository = userFileRepository;
        this.userFolderRepository = userFolderRepository;
        log.info("user upload file root path = {}", userUploadFileRootPath);
    }

    public Long getRootFolderId(Long userId) {
        Optional<UserFolder> rootFolder =
                userFolderRepository.findByUserIdAndParentFolderIdIsNull(userId);
        if (rootFolder.isPresent()) {
            return rootFolder.get().getId();
        } else {
            Path path = Paths.get(userUploadFileRootPath.toString(), userId.toString());
            if (!path.toFile().exists() && !path.toFile().mkdirs()) {
                throw new BusinessException("System busy. Please try again later");
            }
            UserFolder userFolder = new UserFolder();
            userFolder.setName("User:" + userId + ":RootFolder");
            userFolder.setPath(path.toString());
            userFolder.setUserId(userId);
            userFolderRepository.save(userFolder);
            return userFolder.getId();
        }
    }

    public GetFileResult getFile(Long userFileId) {
        GetFileResult result = new GetFileResult();
        UserFile userFile =
                userFileRepository
                        .findById(userFileId)
                        .orElseThrow(() -> new EntityNotFoundException("UserFile", userFileId));
        result.setName(userFile.getName());
        result.setSize(userFile.getSize());
        result.setContentType(userFile.getContentType());
        try {
            result.setInputStream(new FileInputStream(Paths.get(userFile.getPath()).toFile()));
        } catch (FileNotFoundException e) {
            userFileRepository.deleteById(userFileId);
            throw new BusinessException("Get file error.", e);
        }
        return result;
    }

    public List<GetFilesItem> getFiles(Long userId, Long userFolderId) {
        List<GetFilesItem> result = new ArrayList<>();
        Sort sort = Sort.by(Sort.Order.desc("updatedAt"));
        List<UserFolder> userFolders =
                userFolderRepository.findAllByUserIdAndParentFolderId(userId, userFolderId, sort);
        List<UserFile> userFiles =
                userFileRepository.findAllByUserIdAndFolderId(userId, userFolderId, sort);
        for (UserFolder userFolder : userFolders) {
            GetFilesItem item = new GetFilesItem();
            item.setType(FileType.FOLDER);
            item.setName(userFolder.getName());
            item.setId(userFolder.getId());
            result.add(item);
        }
        for (UserFile userFile : userFiles) {
            GetFilesItem item = new GetFilesItem();
            item.setType(FileType.FILE);
            item.setId(userFile.getId());
            item.setName(userFile.getName());
            item.setSize(userFile.getSize());
            item.setContentType(userFile.getContentType());
            result.add(item);
        }
        return result;
    }

    public void deleteFile(Long userFileId) {
        UserFile userFile =
                userFileRepository
                        .findById(userFileId)
                        .orElseThrow(() -> new EntityNotFoundException("UserFile", userFileId));
        try {
            userFileRepository.deleteById(userFileId);
            Files.deleteIfExists(Paths.get(userFile.getPath()));
        } catch (IOException e) {
            log.error("delete file error", e);
        }
    }

    public void deleteFolder(Long folderId) {
        UserFolder userFolder =
                userFolderRepository
                        .findById(folderId)
                        .orElseThrow(() -> new EntityNotFoundException("UserFolder", folderId));
        for (UserFolder folder :
                userFolderRepository.findAllByUserIdAndParentFolderId(
                        userFolder.getUserId(), userFolder.getId())) {
            deleteFolder(folder.getId());
        }
        userFolderRepository.deleteById(folderId);
        userFileRepository.deleteAllByFolderId(folderId);
        try (Stream<Path> walk = Files.walk(Path.of(userFolder.getPath()))) {
            walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(
                            file -> {
                                if (!file.delete()) {
                                    log.error("Delete file error. Path:{}", file.getPath());
                                }
                            });
        } catch (IOException e) {
            log.error("Delete file error. Path:{}", userFolder.getPath());
        }
    }

    public void createFolder(Long userId, Long parentFolderId, @Valid CreateFolderRequest request) {
        Path filesystemPath;
        UserFolder parentFolder =
                userFolderRepository
                        .findById(parentFolderId)
                        .orElseThrow(() -> new EntityNotFoundException("UserFolder", parentFolderId));
        filesystemPath = Paths.get(parentFolder.getPath(), request.getName());
        if (userFolderRepository.findByPath(filesystemPath.toString()).isPresent()) {
            throw new BusinessException("Folder already exist.");
        }
        File filesystemFile = filesystemPath.toFile();
        if (!filesystemFile.exists()) {
            if (!filesystemFile.mkdirs()) {
                throw new BusinessException("Create folder error");
            }
        }
        UserFolder userFolder = new UserFolder();
        userFolder.setParentFolderId(parentFolderId);
        userFolder.setName(request.getName());
        userFolder.setUserId(userId);
        userFolder.setPath(filesystemPath.toString());
        userFolderRepository.save(userFolder);
    }

    public void createFile(Long userId, @Valid CreateFileParam param) {
        this.createUserRootFolderIfNecessary(userId);
        String filesystemFileName = UUID.randomUUID() + "-" + param.getFileName();
        Path filesystemFilePath;
        if (param.getFolderId() != null) {
            UserFolder userFolder =
                    userFolderRepository
                            .findById(param.getFolderId())
                            .orElseThrow(() -> new EntityNotFoundException("UserFolder", param.getFolderId()));
            filesystemFilePath = Path.of(userFolder.getPath(), filesystemFileName);
        } else {
            filesystemFilePath =
                    Path.of(userUploadFileRootPath.toString(), userId.toString(), filesystemFileName);
        }
        try (InputStream inputStream = param.getInputStream()) {
            Files.copy(inputStream, filesystemFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BusinessException("Create file error. Please try again later.", e);
        }
        UserFile userFile = new UserFile();
        userFile.setName(param.getFileName());
        userFile.setFolderId(param.getFolderId());
        userFile.setPath(filesystemFilePath.toString());
        userFile.setSize(param.getSize());
        userFile.setContentType(param.getContentType());
        userFile.setUserId(userId);
        userFileRepository.save(userFile);
    }

    private void createUserRootFolderIfNecessary(Long userId) {
        Path userRootFolder = Paths.get(userUploadFileRootPath.toString(), userId.toString());
        if (!userRootFolder.toFile().exists() && !userRootFolder.toFile().mkdirs()) {
            throw new BusinessException("Create file error. Please try again later.");
        }
    }

    public void updateFolder(Long folderId, @Valid UpdateFolderRequest request) {
        UserFolder userFolder =
                userFolderRepository
                        .findById(folderId)
                        .orElseThrow(() -> new EntityNotFoundException("UserFolder", folderId));
        userFolder.setName(request.getName());
        userFolderRepository.save(userFolder);
    }

    public void updateFile(Long fileId, UpdateFileRequest request) {
        UserFile userFile =
                userFileRepository
                        .findById(fileId)
                        .orElseThrow(() -> new EntityNotFoundException("UserFile", fileId));
        userFile.setName(request.getName());
        userFileRepository.save(userFile);
    }
}
