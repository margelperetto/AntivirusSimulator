package org.teste.avs.view;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.teste.avs.monitor.MonitoringRule;
import org.teste.avs.monitor.MonitoringRule.MatchType;
import org.teste.avs.monitor.MonitoringRule.RuleType;

import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class JDAddRule extends JDialog {

	private JTextField jtfMatch = new JTextField();
	private JComboBox<MatchType> jcbMatchType = new JComboBox<>(MatchType.values());
	private JComboBox<RuleType> jcbRuleType = new JComboBox<>(RuleType.values());
	private JButton jbAdd = new JButton("Add rule");
	private JButton jbCancel = new JButton("Cancel");
	private MonitoringRule rule;
	
	public JDAddRule(JFrame frame) {
		super(frame);
		
		jbAdd.addActionListener((e)->{
			addRule();
		});
		jbCancel.addActionListener((e)->{
			rule = null;
			dispose();
		});
		
		setLayout(new MigLayout(new LC()));
		add(new JLabel("File name to match"), new CC().wrap());
		add(jtfMatch, new CC().width("250:100%:").wrap());
		add(new JLabel("Match type"), new CC().wrap());
		add(jcbMatchType, new CC().wrap());
		add(new JLabel("Compare with"), new CC().wrap());
		add(jcbRuleType, new CC().wrap());
		add(new JSeparator(), new CC().width("100%").wrap());
		add(jbAdd, new CC().spanX().split());
		add(jbCancel, new CC());
		
		pack();
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
		setTitle("Add monitoring rule");
		setModal(true);
		
		setVisible(true);
	}
	
	private void addRule(){
		String match = jtfMatch.getText();
		MatchType matchType = (MatchType) jcbMatchType.getSelectedItem();
		RuleType ruleType = (RuleType) jcbRuleType.getSelectedItem();
		
		if(match.trim().isEmpty()){
			JOptionPane.showMessageDialog(this, "Enter a file name to match!", "Warning", JOptionPane.WARNING_MESSAGE);
			return;
		}
		rule = new MonitoringRule(match, matchType, ruleType);
		dispose();
	}
	
	public MonitoringRule getRule(){
		return rule;
	}
}
