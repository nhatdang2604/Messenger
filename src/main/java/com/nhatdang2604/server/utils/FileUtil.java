package com.nhatdang2604.server.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class FileUtil {

	//Copy data from original to copy file
	public static void copyFile(File original, File copy) {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(original));
			out = new BufferedOutputStream(new FileOutputStream(copy));
			byte[] buffer = new byte[1025];
			int lengthRead;
			while ((lengthRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, lengthRead);
			    out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != in) {try { in.close();} catch(Exception e) {}}	//do nothing if error
			if (null != out) {try { out.close();} catch(Exception e) {}} //do nothing iff error
		}
	}
}
