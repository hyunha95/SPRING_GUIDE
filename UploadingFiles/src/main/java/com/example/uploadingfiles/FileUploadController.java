package com.example.uploadingfiles;

import com.example.uploadingfiles.storage.StorageFileNotFoundException;
import com.example.uploadingfiles.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@Controller
public class FileUploadController {

    private static Logger log = LoggerFactory.getLogger(FileUploadController.class);

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/")
    public String listUploadedFile(Model model) {
        model.addAttribute("files", storageService.loadAll()
                // MvcUriComponentsBuilder.fromMethodName(...): Spring MVC의 MvcUriComponentsBuilder 클래스를 사용하여 컨트롤러 메서드와 메서드 파라미터를 기반으로 URI를 생성합니다. 이 경우, FileUploadController 클래스의 serveFile 메서드에 해당 파일 이름을 전달하여 URI를 생성합니다.
                .map(path -> MvcUriComponentsBuilder.fromMethodName( // .map(path -> ...): 가져온 파일 경로들에 대해 각각 변환 작업을 수행합니다. 여기서는 파일 경로를 기반으로 파일에 접근하는 URL을 생성합니다.
                        FileUploadController.class,
                        "serveFile",
                        path.getFileName().toString())
                        .build()
                        .toUri()
                        .toString() // : MvcUriComponentsBuilder로 생성한 URI를 실제 URI 문자열로 변환합니다.
                ).collect(Collectors.toList())
        );
        return "uploadForm";
    }

    // "/files/파일이름"
    @GetMapping("/files/{filename:.+}") //  경로 변수를 포함하는 요청을 처리하는 메서드를 정의하고 있습니다. 이 코드 조각은 URL 패턴을 통해 파일 이름을 추출하는 것을 목적으로 합니다.
                                          // {filename:.+}은 경로 변수로, 중괄호 내에 있는 filename은 URL에서 추출하려는 파일 이름을 나타냅니다. :.+는 정규 표현식 패턴으로, 여기서 .+는 모든 문자와 문자열을 의미합니다. 즉, 파일 이름에 확장자를 포함한 모든 문자열을 추출하도록 정의한 것입니다.
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getFilename() + "\""
                )
                .body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file")MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        storageService.store(file);
        redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");
        return "redirect:/";
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
