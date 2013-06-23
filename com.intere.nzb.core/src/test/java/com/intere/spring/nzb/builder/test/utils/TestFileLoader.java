package com.intere.spring.nzb.builder.test.utils;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.intere.spring.nzb.builder.StreamUtils;

/**
 * This class provides some test utilities - for instance loading a file for you...
 * 
 * @author Eric Internicola (intere@gmail.com)
 *
 */
public class TestFileLoader {
	
	@Test
	public void testGetFileFromClasspath() throws Exception
	{
		String result = getFileFromClasspath("lotr.html");
		
		assertNotNull(result);
		assertTrue(result.length()>0);		
	}
	
	public static String getFileFromClasspath(String file) throws IOException
	{
		File f = new ClassPathResource(file).getFile();
		
		return getContentFromFile(f);
	}
	
	public static String getContentFromFile(File file) throws IOException {
		
		InputStream in = new FileInputStream(file);
		
		String result = StreamUtils.readBuffer(in, file.length());
		
		in.close();
		
		return result;
	}
	
}
