package org.teste.avs.monitor;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class MonitoringReport {
	private long totalLength;
	private long validator;
	private List<File> files = new LinkedList<>();
	private List<DeleteEvent> deleteEvents = new LinkedList<>();
	private boolean rootFolderMissing;

	@Override
	public boolean equals(Object obj) {
		MonitoringReport other = (MonitoringReport)obj;
		return this.files.size() == other.files.size() &&
				this.deleteEvents.size() == other.deleteEvents.size() &&
				this.totalLength == other.totalLength &&
				this.validator == other.validator &&
				this.rootFolderMissing == other.rootFolderMissing;
	}
	
	public long getTotalLength() {
		return totalLength;
	}

	public void setTotalLength(long totalLength) {
		this.totalLength = totalLength;
	}

	private void appendHash(int hashCode) {
		validator+=hashCode;
	}
	
	public List<File> getFiles() {
		return files;
	}

	public List<DeleteEvent> getDeleteEvents() {
		return deleteEvents;
	}

	public void addFile(File f) {
		files.add(f);
		appendHash(f.getAbsolutePath().hashCode());
	}

	public void addDeleteEvent(DeleteEvent event) {
		this.deleteEvents.add(event);
	}

	public boolean isRootFolderMissing() {
		return rootFolderMissing;
	}

	public void setRootFolderMissing(boolean rootFolderMissing) {
		this.rootFolderMissing = rootFolderMissing;
	}

}
