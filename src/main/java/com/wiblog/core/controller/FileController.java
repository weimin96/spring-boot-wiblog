package com.wiblog.core.controller;

import com.wiblog.core.aop.AuthorizeCheck;
import com.wiblog.core.common.RoleEnum;
import com.wiblog.core.common.ServerResponse;
import com.wiblog.core.service.IFileService;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author pwm
 * @date 2019/10/1
 */
@Log
@RestController
public class FileController {

    private final IFileService fileService;

    public FileController(IFileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/uploadImage")
    @AuthorizeCheck(grade = RoleEnum.ADMIN)
    public ServerResponse<?> uploadImage(MultipartFile file){
        return fileService.uploadImage(file,"img");
    }

    @PostMapping("/uploadImageForEditorMd")
    @AuthorizeCheck(grade = RoleEnum.ADMIN)
    public Map<String,Object> uploadImageForEditorMd(@RequestParam(value = "editormd-image-file")MultipartFile file){
        return fileService.uploadImageForEditorMd(file);
    }

    @GetMapping("/getImageList")
    @AuthorizeCheck(grade = RoleEnum.ADMIN)
    public ServerResponse<?> getImageList(@RequestParam(value = "pageNum", defaultValue = "0") Integer pageNum,
                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        return fileService.getImageList(pageNum,pageSize);
    }

    @GetMapping("/delImage")
    @AuthorizeCheck(grade = RoleEnum.ADMIN)
    public ServerResponse<?> delImage(Long id){
        return fileService.delImage(id);
    }

    @GetMapping("/getLogList")
    @AuthorizeCheck(grade = RoleEnum.ADMIN)
    public ServerResponse<?> getLogList(){
        return fileService.getLogList();
    }

    @GetMapping("/showLog")
    @AuthorizeCheck(grade = RoleEnum.ADMIN)
    public ServerResponse<?> showLog(String path,@RequestParam(value = "pageNum", defaultValue = "0")Integer pageNum){
        return fileService.showLog(path,pageNum);
    }
}
