package org.teste.avs.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;

import org.teste.avs.exceptions.MonitorAlreadyRunningException;
import org.teste.avs.monitor.DeleteEvent;
import org.teste.avs.monitor.AntivirusSimulator;
import org.teste.avs.monitor.AntivirusSimulatorListener;
import org.teste.avs.monitor.MonitoringReport;
import org.teste.avs.monitor.MonitoringRule;
import org.teste.avs.view.models.GenericTableModel;
import org.teste.avs.view.models.TableField;
import org.teste.avs.view.models.TableFieldBuilder;
import org.teste.avs.view.renderers.BooleanCellRenderer;
import org.teste.avs.view.renderers.DateCellRenderer;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class MonitorView extends JFrame implements AntivirusSimulatorListener{

	private JButton jbStartNewMonitor = new JButton("Start new monitoring");
	private JButton jbPauseMonitor = new JButton("Stop");
	private JButton jbResumeMonitor = new JButton("Resume");
	private JButton jbAddRule = new JButton("Add monitoring rule");
	private JButton jbRemoveRule = new JButton("Remove selected rule");
	private JTextField jtfFolder = new JTextField();
	private JTextField jtfStatus = new JTextField();
	private JTextField jtfTimeOfLastChange = new JTextField();
	private JTextField jtfDuration = new JTextField();
	private GenericTableModel<MonitoredFile> tmMonitoredFiles;
	private GenericTableModel<DeleteEvent> tmDeleteEvents;
	private GenericTableModel<MonitoringRule> tmMonitoringRules;
	private AntivirusSimulator monitor;
	private DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public MonitorView() {

		jbStartNewMonitor.addActionListener((evt)->{
			newMonitoring();
		});
		jbPauseMonitor.addActionListener((evt)->{
			stop();
		});
		jbResumeMonitor.addActionListener((evt)->{
			resume();
		});
		jbAddRule.addActionListener((evt)->{
			addRule();
		});
		jbRemoveRule.addActionListener((evt)->{
			removeRule();
		});


		TableFieldBuilder builderDelete = new TableFieldBuilder(DeleteEvent.class);
		TableField[] fieldsDelete = builderDelete
				.field("filePath", "Path").add()
				.field("date", "Time").renderer(new DateCellRenderer("dd/MM/yyyy HH:mm:ss")).width("120!").add()
				.field("successfullyDeleted", "Deleted").renderer(new BooleanCellRenderer()).width("70!").add()
				.build();
		tmDeleteEvents = new GenericTableModel<>(fieldsDelete);

		TableFieldBuilder builderFiles = new TableFieldBuilder(MonitoredFile.class);
		TableField[] fieldsFiles = builderFiles
				.field("path", "Path").add()
				.field("type", "Type").width("100!").add()
				.build();
		tmMonitoredFiles = new GenericTableModel<>(fieldsFiles);

		TableFieldBuilder builderRules = new TableFieldBuilder(MonitoringRule.class);
		TableField[] fieldsRules = builderRules
				.field("ruleType", "Rule Type").width("80!").add()
				.field("matchType", "Match Type").width("75!").add()
				.field("match", "Match With").add()
				.build();
		tmMonitoringRules = new GenericTableModel<>(fieldsRules);
		JTable jtRules = new JTable(tmMonitoringRules);
		jtRules.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		jtfFolder.setToolTipText("Monitored folder");
		jtfFolder.setEditable(false);
		jtfStatus.setEditable(false);
		jtfTimeOfLastChange.setEditable(false);
		jtfDuration.setEditable(false);
		
		JLabel jlWarning = new JLabel("!!! MATCHED FILES WILL BE DELETED !!!", JLabel.CENTER);
		jlWarning.setFont(jlWarning.getFont().deriveFont(Font.BOLD));
		jlWarning.setForeground(Color.RED.darker());
		jlWarning.setToolTipText("...forever!");
		
		JPanel jpMonitoringRules = new JPanel(new MigLayout(new LC().noGrid().insetsAll("0")));
		jpMonitoringRules.add(jlWarning, new CC().alignX("center").wrap());
		jpMonitoringRules.add(jbAddRule, new CC());
		jpMonitoringRules.add(jbRemoveRule, new CC().wrap());
		jpMonitoringRules.add(new JScrollPane(jtRules), new CC().width("0:100%:").height("100:100%:"));
		
		JPanel jpDeleteEvents = new JPanel(new MigLayout(new LC().noGrid().insets("0", "5", "0", "0")));
		jpDeleteEvents.add(new JLabel("Delete events"), new CC().wrap());
		jpDeleteEvents.add(new JScrollPane(new JTable(tmDeleteEvents)), new CC().width("0:100%:").height("100:100%:"));
		
		JPanel jpMonitorFiles = new JPanel(new MigLayout(new LC().insetsAll("0")));
		jpMonitorFiles.add(new JLabel("Monitored files and folders"), new CC().wrap());
		jpMonitorFiles.add(new JScrollPane(new JTable(tmMonitoredFiles)), new CC().width("750:100%:").height("80:100%:").wrap());
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, jpMonitoringRules, jpDeleteEvents);
		split.setBorder(new EmptyBorder(0, 0, 0, 0));
		
		JSplitPane split2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, split, jpMonitorFiles);
		split2.setBorder(new EmptyBorder(0, 0, 0, 0));

		setLayout(new MigLayout(new LC().hideMode(3).noGrid()));
		add(jbStartNewMonitor, new CC().gapRight("10"));
		add(jbPauseMonitor, new CC());
		add(jbResumeMonitor, new CC());
		add(new JLabel("Status"), new CC());
		add(jtfStatus, new CC().width("130::"));
		add(new JLabel("Last change"), new CC());
		add(jtfTimeOfLastChange, new CC().width("130::"));
		add(new JLabel("Duration"), new CC());
		add(jtfDuration, new CC().width("70::").wrap());
		add(jtfFolder, new CC().width("0:100%:").wrap());
		add(new JSeparator(), new CC().width("100%").wrap());
		
		add(split2, new CC().width("100%").height("100%"));

		pack();
		setMinimumSize(getSize());
		setSize(new Dimension(getSize().width, 600));
		setLocationRelativeTo(null);
		setTitle("Antivirus Simulator");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void removeRule() {
		try {
			MonitoringRule selec = tmMonitoringRules.getSelected();
			if(selec==null){
				JOptionPane.showMessageDialog(this, "Select an item from table!", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			tmMonitoringRules.remove(selec);

			if(monitor!=null){
				monitor.removeRule(selec);
			}
		} catch (Exception e) {
			error(e);
		}
	}

	private void addRule() {
		try {
			MonitoringRule rule = new JDAddRule(this).getRule();
			
			if(rule==null){
				return;
			}
			
			if(tmMonitoringRules.getData().contains(rule)){
				JOptionPane.showMessageDialog(this, "This rule has already been added!", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			tmMonitoringRules.add(rule);
			
			if(monitor!=null){
				monitor.addRule(rule);
			}
		} catch (IllegalArgumentException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
		} catch (Exception e) {
			error(e);
		}
	}

	private void resume() {
		if(verifyMonitorNotStarted()){
			return;
		}
		try {
			monitor.startMonitoring(this);
			jtfStatus.setText("RESUMED");
		} catch (MonitorAlreadyRunningException e) {
			JOptionPane.showMessageDialog(this, e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
		} catch (Exception e) {
			error(e);
		}
	}

	private void stop() {
		try {
			if(verifyMonitorNotStarted()){
				return;
			}
			if(!monitor.isMonitoring()){
				JOptionPane.showMessageDialog(this, "Monitor is already stoped!!", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}
			monitor.stopMonitoring();
			
			jtfStatus.setText("STOPING...");
			jbStartNewMonitor.setEnabled(false);
			jbPauseMonitor.setEnabled(false);
			jbResumeMonitor.setEnabled(false);
			new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					while (!monitor.isFinished()) {
						Thread.sleep(monitor.getTimeInterval());
					}
					return null;
				}
				@Override
				protected void done() {
					try {
						get();
					} catch (Exception e) {
						error(e);
					} finally {
						jtfStatus.setText("STOPED");
						jbStartNewMonitor.setEnabled(true);
						jbPauseMonitor.setEnabled(true);
						jbResumeMonitor.setEnabled(true);
					}
				}
			}.execute();
			
		} catch (Exception e) {
			error(e);
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
				JOptionPane.showMessageDialog(this, "Monitor is running!! Stop monitor and repeat operation.", "Warning", JOptionPane.WARNING_MESSAGE);
				return;
			}

			File folderPath = openFolderChooser();

			if(folderPath==null){
				return;
			}

			monitor = new AntivirusSimulator(folderPath);
			for(MonitoringRule rule : tmMonitoringRules.getData()){
				monitor.addRule(rule);
			}
			monitor.startMonitoring(this);

			jtfStatus.setText("WAITING");
			jtfFolder.setText(folderPath.getAbsolutePath());
		} catch (IllegalArgumentException|FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Could not start monitoring! \n"+e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
		} catch (Exception e) {
			error(e);
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
		String path;
		String type;

		public MonitoredFile(String path, String type) {
			this.path = path;
			this.type = type;
		}

	}

	@Override
	public void scanningPerformed(MonitoringReport report) {
		List<MonitoredFile> data = new LinkedList<>();
		int limit = 500, count= 0;
		for (File f : report.getFiles()) {
			data.add(new MonitoredFile(f.getAbsolutePath(), f.isDirectory()?"Directory":"File"));
			count++;
			if(count>limit){
				data.add(new MonitoredFile("...and more "+(report.getFiles().size()-limit)+" file(s)",""));
				break;
			}
		}
		tmMonitoredFiles.setData(data);
		tmDeleteEvents.addData(report.getDeleteEvents());
		jtfStatus.setText("MONITORING");
		jtfTimeOfLastChange.setText(sdf.format(new Date()));
		jtfDuration.setText(report.getDurationTime()/1000+" s");
		if(report.isRootFolderMissing()){
			jtfFolder.setForeground(Color.RED);
			jtfFolder.setText("MISSING! "+monitor.getRootFolder().getAbsolutePath());
		}else{
			jtfFolder.setForeground(Color.BLACK);
			jtfFolder.setText(monitor.getRootFolder().getAbsolutePath());
		}
	}
	
	private void error(Exception e) {
		if(e==null){
			JOptionPane.showMessageDialog(this, "Catastrophic error!","Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		e.printStackTrace();
		JOptionPane.showMessageDialog(this, "Error! \n"+e.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
	}
}
