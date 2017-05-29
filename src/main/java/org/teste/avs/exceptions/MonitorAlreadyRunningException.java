package org.teste.avs.exceptions;

@SuppressWarnings("serial")
public class MonitorAlreadyRunningException extends Exception{

	public MonitorAlreadyRunningException() {
		super("Monitor is already running!");
	}
}
