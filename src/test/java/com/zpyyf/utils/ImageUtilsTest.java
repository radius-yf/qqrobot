package com.zpyyf.utils;

import org.junit.Test;

import java.io.File;
import java.io.InputStream;

import static org.junit.Assert.*;

public class ImageUtilsTest {

	@Test
	public void testSaveImage() throws Exception {
		String filename = "test.png";
		HttpUtils http = new HttpUtils();
		InputStream image = HttpUtils.getInputStream(http.get("https://coding.net/static/project_icon/scenery-10.png"));
		ImageUtils.saveImage(image, filename);
		File file = new File(filename);
		assertTrue(file.exists());
	}
}