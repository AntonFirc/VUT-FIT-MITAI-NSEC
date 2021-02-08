package com.vut.fit.pdb2020.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.time.Instant;

@Component
public class FileUtility {

    @Autowired
    private ServletContext context;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String uploadsDir = "/uploads/";

    public File saveFile(MultipartFile file, Long pageId, String userMail) throws IOException {

        String realPathtoUploads = context.getRealPath(uploadsDir);

        if (!new File(realPathtoUploads).exists()) {
            new File(realPathtoUploads).mkdir();
        }

        String randomFilename = null;

        if (pageId != null) {
            // generate unique new name for file using page id and current time
            randomFilename = passwordEncoder.encode(pageId.toString().concat(Instant.now().toString()));
        }
        else {
            // generate unique new name for file using users email and current time
            randomFilename = passwordEncoder.encode(userMail.concat(Instant.now().toString()));
        }

        // remove non-alphabetical characters and take last 15 characters as filename
        String orgName = StringUtils.right(randomFilename.replaceAll("[^a-zA-Z]", ""), 15);
        String filePath = realPathtoUploads + orgName;
        File dest = new File(filePath);
        file.transferTo(dest);

        return dest;

    }

}
