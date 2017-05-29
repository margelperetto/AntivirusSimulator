package org.teste.avs.monitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingWorker;

public class MonitorFolder {

	private File folderFile;
	private boolean monitoring;
	private MonitorReport lastMonitorReport;
	private long timeInterval = 3000;
	private List<String> monitoringFileNamesToDelete = new LinkedList<>();
	
	public MonitorFolder(File folderPath) throws Exception{
		this.folderFile = folderPath;
		
		if(folderFile==null || !folderFile.exists()){
			throw new FileNotFoundException("Folder '"+folderPath+"' not exists or is invalid!");
		}
		if(!folderFile.isDirectory()){
			throw new IllegalArgumentException("Path '"+folderPath+"' not a directory!");
		}
	}
	
	public void startMonitoring(final MonitorFolderListener listener){
		if(monitoring){
			throw new RuntimeException("Monitor is already running!");
		}
		monitoring = true;
		
		new SwingWorker<Void, MonitorReport>() {
			@Override
			protected Void doInBackground() throws Exception {
				while (monitoring) {
					MonitorReport report = new MonitorReport();
					populateAllFilesAndFolders(folderFile, report);
					if(lastMonitorReport==null || !lastMonitorReport.equals(report)){
						publish(report);
					}
					lastMonitorReport = report;
					Thread.sleep(timeInterval);
				}
				return null;
			}
			@Override
			protected void process(List<MonitorReport> chunks) {
				if(listener!=null){
					listener.loadMonitoredFiles(chunks.get(chunks.size()-1));
				}
			}
			@Override
			protected void done() {
				try {
					get();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					monitoring = false;
				}
			}
		}.execute();
	}
	
	public boolean addFileNameToDelete(String fileName){
		if(fileName==null || fileName.trim().isEmpty()){
			return false;
		}
		synchronized (monitoringFileNamesToDelete) {
			monitoringFileNamesToDelete.add(fileName);
		}
		return true;
	}
	
	public boolean removeFileNameToDelete(String fileName){
		if(fileName==null || fileName.trim().isEmpty() ){
			return false;
		}
		synchronized (monitoringFileNamesToDelete) {
			return monitoringFileNamesToDelete.remove(fileName);
		}
	}
	
	private void populateAllFilesAndFolders(File folder, MonitorReport report) {
		try {
			for(File f : folder.listFiles()){
				
				report.setTotalLength(report.getTotalLength() + f.length());
				
				if(f.isDirectory()){
					report.addFile(f);
					populateAllFilesAndFolders(f, report);
				}else{
					boolean deleteFile = false;
					
					synchronized (monitoringFileNamesToDelete) {
						deleteFile = monitoringFileNamesToDelete.contains(f.getName());
					}
					
					if(deleteFile){
						DeleteEvent event = deleteFile(f);
						deleteFile = event.isSuccessfullyDeleted();
						report.getDeleteEvents().add(event);
					}
					if(!deleteFile){
						report.addFile(f);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Erro ao ler folder: "+folder.getAbsolutePath()+"! \n"+e.getMessage());
		}
	}

	private DeleteEvent deleteFile(File f) {
		DeleteEvent event = new DeleteEvent();
		try {
			event.setFilePath(f.getAbsolutePath());
			if(f.delete()){
				System.out.println("FILE DELETED! "+f.getAbsolutePath());
				event.setSuccessfullyDeleted(true);
			}
		} catch (Exception e) {
			System.err.println("Erro while remove "+f.getAbsolutePath());
		}
		return event;
	}

	public void stopMonitoring(){
		this.monitoring = false;
	}
	
	public boolean isMonitoring(){
		return monitoring;
	}

	public long getTimeInterval() {
		return timeInterval;
	}

	public void setTimeInterval(long timeInterval) {
		this.timeInterval = timeInterval;
	}
	
}
