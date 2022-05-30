package com.nhatdang2604.server.service;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.nhatdang2604.config.Configuration;
import com.nhatdang2604.server.dao.FileDAO;
import com.nhatdang2604.server.entities.FileInfo;
import com.nhatdang2604.server.entities.Message;

public enum FileService {

	INSTANCE;
	
	private Configuration config;
	private FileDAO fileDAO;
	private MessageService messageService;
	
	private FileService() {
		config = Configuration.INSTANCE;
		fileDAO = FileDAO.INSTANCE;
		messageService = MessageService.INSTANCE;
	}

	private void copyFile(File original, File copy) {
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
	
	public FileInfo copyFileFromClientWithGivenInfo(File original, FileInfo info) {
		
		//Get the name in storage base on file info id
		String originalName = original.getName();
		int dotIndx = originalName.indexOf('.');
		
		String extension = "";
		if (-1 != dotIndx) {
			extension = originalName.substring(dotIndx);
		}
		
		String path = config.getStoragePath() + info.getId() + extension;
		
		System.out.println(originalName);
		System.out.println(extension);
		System.out.println(path);
		
		File copy = new File(path);
		try {
			if (!copy.exists()) {
				copy.createNewFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Copy for original file to copy file
		copyFile(original, copy);
		
		//Set new data for info
		info.setOriginalName(originalName);
		info.setPath(path);
		
		return info;
	}
	
	public FileInfo saveFile(Message message) {
		
		FileInfo info = new FileInfo();
		File file = message.getFile();
		
		info.setMessage(message);
		message.setFileInfo(info);
		message.setContent(file.getName());
		
		//Save the message, cause we haven't have the id yet
		Message saveMessage = messageService.createMessage(message);
		
		//Get the id from the save msg
		info.setId(saveMessage.getId());
		
		//Save the file to storage
		info = copyFileFromClientWithGivenInfo(file, info);
		
		//Update the info
		return updateFileInfo(info);
	}
	
	
	
	//Return message binding with the file
	public Message sendFileToClient(FileInfo info) {
		Integer id = info.getId();
		Message message = messageService.findMessageById(id);
		info = message.getFileInfo();
		
		//Read file from storage folder and set to message
		File file = new File(info.getPath());
		message.setFile(file);
		
		return message;
	}
	
	public Message recievedFileFromClient(Message message) {
		FileInfo info = saveFile(message);
		return sendFileToClient(info);
	}
	
	public FileInfo updateFileInfo(FileInfo info) {
		return fileDAO.update(info);
	}
}
