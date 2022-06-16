package com.liza.rest;

import com.liza.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;



@Controller
public class FileController {


    @Value("${content.root}")
    private String contentRoot;

    private StorageService storageService;

    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping(value = "/", method = {RequestMethod.GET})
    public String main(Model model) {
        model.addAttribute("files", storageService.loadAll());

        return "main";
    }


    @RequestMapping(value = "/upload", method = {RequestMethod.POST})
    public @ResponseBody
    RedirectView upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        storageService.store(file);
        return new RedirectView("/");
    }

    @GetMapping("/download/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws FileNotFoundException {

        Resource resource = storageService.loadAsResource(filename);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
