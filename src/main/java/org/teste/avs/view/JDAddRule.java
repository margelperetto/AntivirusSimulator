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

	private JTextField jtfMatchSentence = new JTextField();
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
		jtfMatchSentence.setToolTipText("Match sentence");
		
		setLayout(new MigLayout(new LC().gridGapY("5")));
		add(new JLabel("<html><b>DELETE</b> files when</html>"), new CC().wrap());
		add(jcbRuleType, new CC().growX().wrap());
		add(jcbMatchType, new CC().growX().wrap());
		add(jtfMatchSentence, new CC().width("250:100%:").wrap());
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
		String match = jtfMatchSentence.getText();
		MatchType matchType = (MatchType) jcbMatchType.getSelectedItem();
		RuleType ruleType = (RuleType) jcbRuleType.getSelectedItem();
		
		if(match.trim().isEmpty()){
			JOptionPane.showMessageDialog(this, "Enter a match sentence!", "Warning", JOptionPane.WARNING_MESSAGE);
			jtfMatchSentence.requestFocus();
			return;
		}
		rule = new MonitoringRule(match, matchType, ruleType);
		dispose();
	}
	
	public MonitoringRule getRule(){
		return rule;
	}
}
