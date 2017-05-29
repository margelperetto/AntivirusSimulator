package org.teste.avs.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.teste.avs.monitor.DeleteEvent;
import org.teste.avs.monitor.MonitorFolder;
import org.teste.avs.monitor.MonitorFolderListener;
import org.teste.avs.monitor.MonitorReport;
import org.teste.avs.view.models.GenericTableModel;
import org.teste.avs.view.models.TableField;
import org.teste.avs.view.models.TableFieldBuilder;
import org.teste.avs.view.renderers.BooleanCellRenderer;
import org.teste.avs.view.renderers.DateCellRenderer;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class JFMonitorFolder extends JFrame implements MonitorFolderListener{

	private JButton jbNewMonitor = new JButton("New Monitoring");
	private JButton jbPauseMonitor = new JButton("Stop");
	private JButton jbResumeMonitor = new JButton("Resume");
	private JButton jbAddFileName = new JButton("Add monitored filename");
	private JButton jbRemoveFileName = new JButton("Remove selected");
	private JLabel jlStatus = new JLabel("Status: Not started");
	private JTextField jtfFolder = new JTextField();
	private GenericTableModel<MonitoredFile> tmMonitoredFiles;
	private GenericTableModel<DeleteEvent> tmDeleteEvents;
	private GenericTableModel<FileNameToDelete> tmFilesNamesToDelete;
	private JTable jtFileNamesToDelete;
	private MonitorFolder monitor;

	public JFMonitorFolder() {

		jbNewMonitor.addActionListener((evt)->{
			newMonitoring();
		});
		jbPauseMonitor.addActionListener((evt)->{
			pause();
		});
		jbResumeMonitor.addActionListener((evt)->{
			resume();
		});
		jbAddFileName.addActionListener((evt)->{
			addFilename();
		});
		jbRemoveFileName.addActionListener((evt)->{
			removeFilename();
		});


		TableFieldBuilder builderDelete = new TableFieldBuilder(DeleteEvent.class);
		TableField[] fieldsDelete = builderDelete
				.field("filePath", "Path").add()
				.field("date", "Time").renderer(new DateCellRenderer("HH:mm:ss")).width("80!").add()
				.field("successfullyDeleted", "Delete").renderer(new BooleanCellRenderer()).width("70!").add()
				.build();
		tmDeleteEvents = new GenericTableModel<>(fieldsDelete);

		TableFieldBuilder builderFiles = new TableFieldBuilder(MonitoredFile.class);
		TableField[] fieldsFiles = builderFiles
				.field("path", "Path").add()
				.field("type", "Type").width("100!").add()
				.build();
		tmMonitoredFiles = new GenericTableModel<>(fieldsFiles);

		TableFieldBuilder builderFileNames = new TableFieldBuilder(FileNameToDelete.class);
		TableField[] fieldsFileNames = builderFileNames
				.field("fileName", "Filename").add()
				.build();
		tmFilesNamesToDelete = new GenericTableModel<>(fieldsFileNames);
		jtFileNamesToDelete = new JTable(tmFilesNamesToDelete);
		jtFileNamesToDelete.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jtfFolder.setEditable(false);
		jtfFolder.setVisible(false);
		
		JLabel jlWarning = new JLabel("!!! MONITORED FILES WILL BE DELETED !!!");
		jlWarning.setFont(jlWarning.getFont().deriveFont(Font.BOLD));
		jlWarning.setForeground(Color.ORANGE.darker());
		
		JPanel jpDeleteFileNames = new JPanel(new MigLayout(new LC().noGrid().insetsAll("0")));
		jpDeleteFileNames.add(jlWarning, new CC().wrap());
		jpDeleteFileNames.add(jbAddFileName, new CC());
		jpDeleteFileNames.add(jbRemoveFileName, new CC().wrap());
		jpDeleteFileNames.add(new JScrollPane(jtFileNamesToDelete), new CC().width("0:100%:").height("50:100%:"));
		
		JPanel jpDeleteEvents = new JPanel(new MigLayout(new LC().noGrid().insetsAll("0")));
		jpDeleteEvents.add(new JLabel("Delete events"), new CC().wrap());
		jpDeleteEvents.add(new JScrollPane(new JTable(tmDeleteEvents)), new CC().width("0:100%:").height("50:100%:"));

		setLayout(new MigLayout(new LC().hideMode(3).noGrid()));
		add(jbNewMonitor, new CC());
		add(jbPauseMonitor, new CC());
		add(jbResumeMonitor, new CC());
		add(jlStatus, new CC().wrap());
		add(jtfFolder, new CC().width("0:100%:").wrap());
		add(new JSeparator(), new CC().width("100%").wrap());
		
		add(new JLabel("Monitored files and folders"), new CC().wrap());
		add(new JScrollPane(new JTable(tmMonitoredFiles)), new CC().width("750:100%:").height("50:65%:").wrap());
		
		add(jpDeleteEvents, new CC().width("100%").height("35%"));
		add(jpDeleteFileNames, new CC().height("35%:"));

		pack();
		setMinimumSize(getSize());
		setSize(new Dimension(getSize().width, 600));
		setLocationRelativeTo(null);
		setTitle("Antivirus Simulator");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void removeFilename() {
		try {
			FileNameToDelete selec = tmFilesNamesToDelete.getSelected();
			if(selec==null){
				return;
			}
			tmFilesNamesToDelete.remove(selec);

			if(monitor!=null){
				monitor.removeFileNameToDelete(selec.fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error remove filename! \n"+e.getMessage(),"Erro", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void addFilename() {
		try {
			String name = JOptionPane.showInputDialog(this, "Enter a filename for monitoring and DELETE", "Filename to delete", JOptionPane.WARNING_MESSAGE);
			if(name==null || name.trim().isEmpty()){
				return;
			}
			tmFilesNamesToDelete.add(new FileNameToDelete(name));
			
			if(monitor!=null){
				monitor.addFileNameToDelete(name);
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error add filename! \n"+e.getMessage(),"Erro", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void resume() {
		if(verifyMonitorNotStarted()){
			return;
		}
		monitor.startMonitoring(this);
	}

	private void pause() {
		if(verifyMonitorNotStarted()){
			return;
		}
		try {
			monitor.stopMonitoring();
			Thread.sleep(monitor.getTimeInterval());
			jlStatus.setText("Status: STOPED");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean verifyMonitorNotStarted() {
		if(monitor == null){
			JOptionPane.showMessageDialog(this, "Monitor not started!", "Warning", JOptionPane.WARNING_MESSAGE);
			return true;
		}
		return false;
	}

	private void newMonitoring(){
		try {
			if(monitor!=null && monitor.isMonitoring()){
				JOptionPane.showMessageDialog(this, "Monitor is already running!! Pause monitor and repeat operation.", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}

			File folderPath = openFolderChooser();

			if(folderPath==null){
				return;
			}

			monitor = new MonitorFolder(folderPath);
			for(FileNameToDelete name : tmFilesNamesToDelete.getData()){
				monitor.addFileNameToDelete(name.fileName);
			}
			monitor.startMonitoring(this);

			jlStatus.setText("Status: WAITING");
			jtfFolder.setVisible(true);
			jtfFolder.setText(folderPath.getAbsolutePath());

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error starting monitoring! \n"+e.getMessage(),"Erro", JOptionPane.ERROR_MESSAGE);
		}
	}

	private File openFolderChooser() {
		JFileChooser chooser = new JFileChooser(jtfFolder.getText());
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setMultiSelectionEnabled(false);
		chooser.setDialogTitle("Select a directory to monitor");
		chooser.showOpenDialog(this);

		return chooser.getSelectedFile();
	}

	class MonitoredFile{
		File file;
		String path;
		String type;

		public MonitoredFile(File file) {
			this.file = file;
			if(file!=null){
				this.path = file.getAbsolutePath();
				this.type = file.isDirectory()?"Directory":"File";
			}
		}

	}

	class FileNameToDelete{
		String fileName;

		public FileNameToDelete(String fileName) {
			this.fileName = fileName;
		}
	}

	@Override
	public void loadMonitoredFiles(MonitorReport report) {
		List<MonitoredFile> data = new ArrayList<>(report.getFiles().size());
		for (File f : report.getFiles()) {
			data.add(new MonitoredFile(f));
		}
		tmMonitoredFiles.setData(data);
		tmDeleteEvents.addData(report.getDeleteEvents());
		jlStatus.setText("Status: MONITORING - Last: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
	}
}
