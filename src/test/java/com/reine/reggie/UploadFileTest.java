package com.reine.reggie;

import org.junit.jupiter.api.Test;

/**
 * @author reine
 * @since 2022/4/14 11:34
 */
public class UploadFileTest {

    @Test
    public void testUpload() {
        String fileName = "erere.jpg";
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        System.out.println(suffix);
    }
}
