package org.zerock.apiserver.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.util.FileUtil;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.apiserver.dto.PageRequestDTO;
import org.zerock.apiserver.dto.PageResponseDTO;
import org.zerock.apiserver.dto.ProductDTO;
import org.zerock.apiserver.service.ProductService;
import org.zerock.apiserver.util.CustomFileUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final CustomFileUtil fileUtil;

    private final ProductService productService;

//
//    @PostMapping("/")
//    public Map<String, String> register(ProductDTO productDTO){
//        log.info("register" + productDTO);
//
//        List<MultipartFile> files = productDTO.getFiles();
//
//        List<String> uploadFileNames = fileUtil.saveFiles(files);
//
//        productDTO.setUploadFileNames(uploadFileNames);
//
//        log.info(uploadFileNames);
//
//        return Map.of("RESULT", "SUCCESS");
//    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable("fileName") String fileName){
        return fileUtil.getFile(fileName);
    }

    @GetMapping("/list")
    public PageResponseDTO<ProductDTO> list(PageRequestDTO pageRequestDTO){

        return productService.getList(pageRequestDTO);
    }

    @PostMapping("/")
    public Map<String,Long> register(ProductDTO productDTO){
        List<MultipartFile> files = productDTO.getFiles();

        List<String> uploadFileNames = fileUtil.saveFiles(files);

        productDTO.setUploadFileNames(uploadFileNames);

        log.info(uploadFileNames);

        Long pno = productService.register(productDTO);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Map.of("result", pno);
    }

    @GetMapping("/{pno}")
    public ProductDTO read(@PathVariable("pno") Long pno) {
        return productService.get(pno);
    }

    @PutMapping("/{pno}")
    public Map<String, String> modify(@PathVariable Long pno, ProductDTO productDTO) {
        /*
        * productDTO(수정할값)의 uploadFileNames에 해당 파일명이 존재하는 파일만 기존의 업로드 폴더에 놔두고 나머지는 지움
        *
        * */

        productDTO.setPno(pno);

        //old product
        ProductDTO oldProductDTO = productService.get(pno);

        //file upload
        List<MultipartFile> files = productDTO.getFiles();
        List<String> currentUploadFileNames = fileUtil.saveFiles(files);

        //keep files
        List<String> uploadedFileNames = productDTO.getUploadFileNames();

        if(currentUploadFileNames != null && !currentUploadFileNames.isEmpty()){
            uploadedFileNames.addAll(currentUploadFileNames);
        }

        productService.modify(productDTO);

        List<String> oldFileNames = oldProductDTO.getUploadFileNames();
        if(oldFileNames != null && oldFileNames.size() > 0){
            //지워야 하는 애들만  모음
            List<String> removeFiles =
            oldFileNames.stream().filter(fileName -> uploadedFileNames.indexOf(fileName) == -1 ).collect(Collectors.toList());

            fileUtil.deleteFiles(removeFiles);
        }

        return Map.of("result", "success");
    }


    @DeleteMapping("/{pno}")
    public Map<String,String> remove(@PathVariable Long pno){
        List<String> oldFileNames = productService.get(pno).getUploadFileNames();

        productService.remove(pno);

        fileUtil.deleteFiles(oldFileNames);

        return Map.of("RESULT", "SUCCESS");
    }

}
