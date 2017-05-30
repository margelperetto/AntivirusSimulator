package org.teste.avs.monitor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingWorker;

import org.teste.avs.exceptions.MonitorAlreadyRunningException;

public class AntivirusSimulator {

	private File rootFolder;
	private boolean monitoring;
	private MonitoringReport lastMonitorReport;
	private long timeInterval = 3000;
	private List<MonitoringRule> rules = new LinkedList<>();

	public AntivirusSimulator(File folderPath) throws FileNotFoundException, IllegalArgumentException{
		this.rootFolder = folderPath;

		if(rootFolder==null || !rootFolder.exists()){
			throw new FileNotFoundException("Folder '"+folderPath+"' not exists or is invalid!");
		}
		if(!rootFolder.isDirectory()){
			throw new IllegalArgumentException("Path '"+folderPath+"' not a directory!");
		}
	}

	public synchronized void startMonitoring(final AntivirusSimulatorListener listener) throws MonitorAlreadyRunningException{
		if(monitoring){
			throw new MonitorAlreadyRunningException();
		}
		monitoring = true;

		new SwingWorker<Void, MonitoringReport>() {
			@Override
			protected Void doInBackground() throws Exception {
				System.out.println("MONITOR STARTED!");
				while (monitoring) {
					MonitoringReport report = new MonitoringReport();
					scanFonder(rootFolder, report);
					if(lastMonitorReport==null || !lastMonitorReport.equals(report)){
						publish(report);
					}
					lastMonitorReport = report;
					Thread.sleep(timeInterval);
				}
				return null;
			}
			@Override
			protected void process(List<MonitoringReport> chunks) {
				if(listener!=null){
					listener.scanningPerformed(chunks.get(chunks.size()-1));
				}
			}
			@Override
			protected void done() {
				try {
					get();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					System.out.println("MONITOR STOPED!");
					monitoring = false;
				}
			}
		}.execute();
	}

	public boolean addRule(MonitoringRule rule) throws IllegalArgumentException{
		if(rule.getMatch()==null || rule.getMatch().trim().isEmpty()){
			return false;
		}
		synchronized (rules) {
			if(rules.contains(rule)){
				throw new IllegalArgumentException("This rule is already exists!");
			}
			rules.add(rule);
		}
		return true;
	}

	public boolean removeRule(MonitoringRule rule){
		if(rule==null){
			return false;
		}
		synchronized (rules) {
			return rules.remove(rule);
		}
	}

	private void scanFonder(File folder, MonitoringReport report) {
		try {
			if(!folder.exists()){
				if(rootFolder == folder){
					report.setRootFolderMissing(true);
				}
				return;
			}
			
			for(File f : folder.listFiles()){

				report.setTotalLength(report.getTotalLength() + f.length());

				if(f.isDirectory()){
					report.addFile(f);
					scanFonder(f, report);
					return;
				} 
				
				boolean deleteFile = false;

				synchronized (rules) {
					for (MonitoringRule rule : rules) {
						if(rule.isMatchedTo(f.getName())){
							deleteFile = true;
							break;
						}
					}
				}
				
				if(deleteFile){
					DeleteEvent event = deleteFile(f);
					report.addDeleteEvent(event);
					if(!event.isSuccessfullyDeleted()){
						report.addFile(f);
					}
				}else{
					report.addFile(f);
				}
				
			}
		} catch (Exception e) {
			System.err.println("Error while reading directory: "+folder.getAbsolutePath()+"! \n"+e.getMessage());
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

	public File getRootFolder() {
		return rootFolder;
	}

}
