package com.zpyyf.utils;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtils {
	private static final Logger log = Logger.getLogger(ImageUtils.class);

	public static void saveImage(InputStream is, String fileName) {
		File file = new File(fileName);
		try {
			String path = file.getCanonicalPath();
			File mkdir = new File(path.substring(0, path.lastIndexOf(File.separator)));
			if (!mkdir.exists() && mkdir.mkdirs()) {
				log.info("创建文件夹" + mkdir.getCanonicalPath());
			}

			log.info("图片保存到" + file.getCanonicalPath());
			String suffix = fileName.substring(fileName.lastIndexOf('.') + 1);
			BufferedImage image = ImageIO.read(is);
			ImageIO.write(image, suffix, file);
		} catch (IOException e) {
			log.error("图片保存失败");
			log.error(e);
		}
	}
}
