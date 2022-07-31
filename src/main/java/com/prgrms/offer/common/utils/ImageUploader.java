package com.prgrms.offer.common.utils;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface ImageUploader {

    String upload(MultipartFile multipartFile, String dirName) throws IOException;
}
