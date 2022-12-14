package site.ownw.homepage.controller.file;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import site.ownw.homepage.common.exception.BusinessException;
import site.ownw.homepage.common.exception.EntityNotFoundException;
import site.ownw.homepage.controller.file.model.BatchDeleteFileRequest;
import site.ownw.homepage.controller.file.model.BatchDeleteFileResponse;
import site.ownw.homepage.controller.file.model.CreateFolderRequest;
import site.ownw.homepage.controller.file.model.UpdateFileRequest;
import site.ownw.homepage.controller.file.model.UpdateFolderRequest;
import site.ownw.homepage.domain.file.FileService;
import site.ownw.homepage.domain.file.model.CreateFileParam;
import site.ownw.homepage.domain.file.model.GetFileResult;
import site.ownw.homepage.domain.file.model.GetFilesItem;
import site.ownw.homepage.domain.file.repository.UserFileRepository;
import site.ownw.homepage.domain.file.repository.UserFolderRepository;
import site.ownw.homepage.entity.UserFile;
import site.ownw.homepage.entity.UserFolder;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final UserFileRepository userFileRepository;
    private final UserFolderRepository userFolderRepository;

    @PreAuthorize("@authUtil.isMe(#userId)")
    @GetMapping("/api/v1/users/{userId}:getRootFolderId")
    public String getRootFolderId(@PathVariable @Schema(implementation = String.class) Long userId) {
        return fileService.getRootFolderId(userId).toString();
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PostMapping(
            value = "/api/v1/users/{userId}/folders/{folderId}/files",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadFile(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable @Schema(implementation = String.class) Long folderId,
            @RequestPart MultipartFile file) {
        CreateFileParam param = new CreateFileParam();
        param.setFileName(file.getOriginalFilename());
        param.setContentType(file.getContentType());
        param.setSize(file.getSize());
        param.setFolderId(folderId);
        try {
            param.setInputStream(file.getInputStream());
        } catch (IOException e) {
            throw new BusinessException("File upload error. Please try again later.", e);
        }
        fileService.createFile(userId, param);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PostMapping(value = "/api/v1/users/{userId}/folders/{parentFolderId}")
    public void createFolder(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable @Schema(implementation = String.class) Long parentFolderId,
            @Valid @RequestBody CreateFolderRequest request) {
        fileService.createFolder(userId, parentFolderId, request);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @GetMapping(value = "/api/v1/users/{userId}/folders/{folderId}/files")
    public List<GetFilesItem> getFiles(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable @Schema(implementation = String.class) Long folderId) {
        UserFolder userFolder =
                userFolderRepository
                        .findById(folderId)
                        .orElseThrow(() -> new EntityNotFoundException("UserFolder", folderId));
        if (!userFolder.getUserId().equals(userId)) {
            throw new AccessDeniedException("Not Allow");
        }
        return fileService.getFiles(userId, folderId);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PutMapping(value = "/api/v1/users/{userId}/folders/{folderId}/files/{fileId}")
    public void updateFile(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable @Schema(implementation = String.class) Long folderId,
            @PathVariable @Schema(implementation = String.class) Long fileId,
            @RequestBody @Valid UpdateFileRequest request) {
        UserFile userFile =
                userFileRepository
                        .findById(fileId)
                        .orElseThrow(() -> new EntityNotFoundException("UserFile", fileId));
        if (!userFile.getUserId().equals(userId)) {
            throw new AccessDeniedException("Not Allow");
        }
        fileService.updateFile(fileId, request);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @GetMapping(value = "/api/v1/users/{userId}/files/{fileId}")
    public void downloadFile(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable @Schema(implementation = String.class) Long fileId,
            @Parameter(hidden = true) HttpServletResponse response) {
        UserFile userFile =
                userFileRepository
                        .findById(fileId)
                        .orElseThrow(() -> new EntityNotFoundException("UserFile", fileId));
        if (!userFile.getUserId().equals(userId)) {
            throw new AccessDeniedException("Not Allow");
        }
        GetFileResult fileResult = fileService.getFile(fileId);
        response.setContentType(fileResult.getContentType());
        response.setHeader(
                "Content-Disposition", "attachment; filename=\"" + fileResult.getName() + "\"");
        try {
            fileResult.getInputStream().transferTo(response.getOutputStream());
        } catch (IOException e) {
            throw new BusinessException("Get file error. Please try again later.", e);
        }
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @DeleteMapping(value = "/api/v1/users/{userId}/files/{fileId}")
    public void deleteFile(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable @Schema(implementation = String.class) Long fileId) {
        UserFile userFile =
                userFileRepository
                        .findById(fileId)
                        .orElseThrow(() -> new EntityNotFoundException("UserFile", fileId));
        if (!userFile.getUserId().equals(userId)) {
            throw new AccessDeniedException("Not Allow");
        }
        fileService.deleteFile(fileId);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @DeleteMapping(value = "/api/v1/users/{userId}/files:batchDelete")
    public BatchDeleteFileResponse deleteFiles(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @RequestBody @Valid BatchDeleteFileRequest request) {
        BatchDeleteFileResponse response = new BatchDeleteFileResponse();
        response.setSuccessful(new ArrayList<>());
        response.setFailed(new ArrayList<>());
        for (Long fileId : request.getFileIds()) {
            try {
                this.deleteFile(userId, fileId);
                response.getSuccessful().add(fileId);
            } catch (Throwable t) {
                response.getFailed().add(fileId);
                log.error("batch delete file error. id:{}", fileId, t);
            }
        }
        return response;
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @DeleteMapping(value = "/api/v1/users/{userId}/folders/{folderId}")
    public void deleteFolder(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable @Schema(implementation = String.class) Long folderId) {
        UserFolder userFolder =
                userFolderRepository
                        .findById(folderId)
                        .orElseThrow(() -> new EntityNotFoundException("UserFolder", folderId));
        if (!userFolder.getUserId().equals(userId)) {
            throw new AccessDeniedException("Not Allow");
        }
        fileService.deleteFolder(folderId);
    }

    @PreAuthorize("@authUtil.isMe(#userId)")
    @PutMapping(value = "/api/v1/users/{userId}/folders/{folderId}")
    public void updateFolder(
            @PathVariable @Schema(implementation = String.class) Long userId,
            @PathVariable @Schema(implementation = String.class) Long folderId,
            @RequestBody @Valid UpdateFolderRequest request) {
        UserFolder userFolder =
                userFolderRepository
                        .findById(folderId)
                        .orElseThrow(() -> new EntityNotFoundException("UserFolder", folderId));
        if (!userFolder.getUserId().equals(userId)) {
            throw new AccessDeniedException("Not Allow");
        }
        fileService.updateFolder(folderId, request);
    }
}
