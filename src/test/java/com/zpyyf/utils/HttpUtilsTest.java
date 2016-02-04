package com.zpyyf.utils;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class HttpUtilsTest {
	HttpUtils http;

	@Before
	public void setUp() throws Exception {
		http = new HttpUtils();
	}


	@Test
	public void testGet() throws Exception {
		HttpResponse response = http.get("http://www.baidu.com/");
		assertNotNull(response);
	}

	@Test
	public void testPost() throws Exception {
		assertNotNull(http.post("http://www.baidu.com/", ""));
	}

	@Test
	public void testGetString() throws Exception {
		assertNotNull(HttpUtils.getString(http.get("http://www.baidu.com/")));
	}

	@Test
	public void testGetInputStream() throws Exception {
		assertNotNull(HttpUtils.getInputStream(http.get("https://coding.net/static/project_icon/scenery-10.png")));
	}

}