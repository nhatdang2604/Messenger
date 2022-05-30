package com.nhatdang2604.server.service;

import java.io.File;

import com.nhatdang2604.config.Configuration;
import com.nhatdang2604.server.dao.FileDAO;
import com.nhatdang2604.server.entities.FileInfo;
import com.nhatdang2604.server.entities.Message;
import com.nhatdang2604.server.utils.FileUtil;

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
		FileUtil.copyFile(original, copy);
		
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
	
	public File findFileById(Integer id) {
		
		FileInfo info = fileDAO.find(id);
		
		File file = new File(info.getPath());
		
		return file;
		
	}
}
