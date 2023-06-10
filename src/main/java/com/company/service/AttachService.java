package com.company.service;


import com.company.dto.attach.AttachDTO;
import com.company.dto.attach.AttachRequestDTO;
import com.company.dto.attach.AttachResponseDTO;
import com.company.entity.AttachEntity;
import com.company.exps.AppBadRequestException;
import com.company.exps.ItemNotFoundException;
import com.company.repository.AttachRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AttachService {
    @Autowired
    private AttachRepository attachRepository;
    @Value("attaches")
    private String folderName;
    @Value("${server.host}")
    private String serverHost;
    public AttachDTO upload(MultipartFile file) {
        try {
            String pathFolder = getYmDString(); // 2022/04/23
            File folder = new File("attaches/" + pathFolder);  // attaches/2023/04/26
            if (!folder.exists()) {
                folder.mkdirs();
            }
            byte[] bytes = file.getBytes();
            String extension = getExtension(file.getOriginalFilename());

            AttachEntity attachEntity = new AttachEntity();
            attachEntity.setId(UUID.randomUUID().toString());
            attachEntity.setCreatedDate(LocalDateTime.now());
            attachEntity.setExtension(extension);
            attachEntity.setSize(file.getSize());
            attachEntity.setPath(pathFolder);
            attachEntity.setOriginalName(file.getOriginalFilename());
            attachRepository.save(attachEntity);

            Path path = Paths.get("attaches/" + pathFolder + "/" + attachEntity.getId() + "." + extension);
            // attaches/2023/05/15/uuid().jpg
            Files.write(path, bytes);

            AttachDTO dto = new AttachDTO();
            dto.setId(attachEntity.getId());
            dto.setOriginalName(file.getOriginalFilename());
            dto.setUrl(serverHost + "/api/v1/attach/open/" + attachEntity.getId() + "." + extension);
            return dto;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public AttachRequestDTO getAttachById(String id){
        AttachEntity attachEntity = get(id);
        AttachRequestDTO dto = new AttachRequestDTO();
        dto.setId(attachEntity.getId());
        dto.setOriginalName(attachEntity.getOriginalName());
        String extension = getExtension(attachEntity.getOriginalName());
        dto.setUrl(serverHost + "/api/v1/attach/open/" + attachEntity.getId() + "." + extension);
        return dto;
    }
    public Resource download(String fileName) {
        try {
            int lastIndex = fileName.lastIndexOf(".");
            String id = fileName.substring(0, lastIndex);
            AttachEntity attachEntity = get(id);

            Path file = Paths.get("attaches/" + attachEntity.getPath() + "/"+id+"."+attachEntity.getExtension());
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
    public boolean delete(String id) {
        AttachEntity entity = get(id);
        File file = new File(folderName + "/" + entity.getPath() + "/" + entity.getId()+"."+entity.getExtension());
        if (file.delete()) {
            attachRepository.delete(entity);
        } else {
            throw new AppBadRequestException("Cannot delete this file");
        }
        return true;
    }
    public AttachEntity get(String id) {
        Optional<AttachEntity> byId = attachRepository.findById(id);
        if (byId == null){
            throw new ItemNotFoundException("Attach not found");
        }
        return byId.get();
    }
    public Page<AttachDTO> paginationWithDate(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        Pageable paging = PageRequest.of(page - 1, size, sort);
        Page<AttachEntity> pageObj = attachRepository.findAll(paging);

        Long totalCount = pageObj.getTotalElements();
        List<AttachEntity> entityList = pageObj.getContent();
        List<AttachDTO> dtoList = new LinkedList<>();
        for (AttachEntity entity : entityList) {
            AttachDTO dto = new AttachDTO();
            dto.setId(entity.getId());
            dto.setOriginalName(entity.getOriginalName());
            dto.setExtension(entity.getExtension());
            dto.setSize(entity.getSize());
            dto.setPath(entity.getPath());
            dtoList.add(dto);
        }
        Page<AttachDTO> response = new PageImpl<AttachDTO>(dtoList, paging, totalCount);
        return response;
    }
    public String getExtension(String name){
        int lastIndex= name.lastIndexOf(".");
        return name.substring(lastIndex+1);
    }
    public String getYmDString() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month =Calendar.getInstance().get(Calendar.MONTH)+1;
        int day = Calendar.getInstance().get(Calendar.DATE);
        return year + "/" + month + "/" + day; // 2022/04/23
    }
    public AttachResponseDTO toResponseDTO(AttachEntity entity) {
        AttachResponseDTO dto = new AttachResponseDTO();
        dto.setId(entity.getId());
        dto.setUrl(entity.getPath());
        return dto;
    }
}
