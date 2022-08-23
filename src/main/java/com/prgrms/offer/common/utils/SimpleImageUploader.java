package com.prgrms.offer.common.utils;

import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class SimpleImageUploader implements ImageUploader {

    @Override
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        return "https://avatars.githubusercontent.com/u/97747387?s=400&u=8563ff1c03e13674253aa2a487ef47a9b1f654c7&v=4";
    }
}
