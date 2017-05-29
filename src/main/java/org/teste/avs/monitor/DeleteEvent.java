package org.teste.avs.monitor;

import java.util.Date;

public class DeleteEvent {
	private String filePath;
	private Date date = new Date();
	private boolean successfullyDeleted;
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public boolean isSuccessfullyDeleted() {
		return successfullyDeleted;
	}
	public void setSuccessfullyDeleted(boolean successfullyDeleted) {
		this.successfullyDeleted = successfullyDeleted;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
