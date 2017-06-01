package org.teste.avs;

import java.io.File;
import java.util.Scanner;

import org.teste.avs.monitor.AntivirusSimulator;
import org.teste.avs.monitor.MonitoringRule;
import org.teste.avs.monitor.MonitoringRule.MatchType;
import org.teste.avs.monitor.MonitoringRule.RuleType;

public class ConsoleMode {

	public static void run(String[] args) {
		try {
			String path = args[0];

			AntivirusSimulator antivirus = new AntivirusSimulator(new File(path));

			if(args.length>1){
				for (int i = 1; i < args.length; i++) {
					MonitoringRule rule = convertRule(args[i]);
					antivirus.addRule(rule);
				}
			}

			antivirus.startMonitoring(null);

			Thread.sleep(1000);
			System.out.println("MONITORING: \n"+path+"\nPress a key to STOP");
			Scanner scanner = new Scanner(System.in);
			scanner.nextLine();
			scanner.close();
			
			antivirus.stopMonitoring();
			
		} catch (Exception e) {
			System.err.println("ERROR! "+e.getMessage());
		}finally {
			System.exit(0);
		}
	}

	private static MonitoringRule convertRule(String ruleStr) {
		try {
			String[] split = ruleStr.split("|");

			RuleType ruleType = RuleType.valueOf(split[0]);
			MatchType matchType = MatchType.valueOf(split[1]);
			String match = split[2];

			return new MonitoringRule(match, matchType, ruleType);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid rule! "+ruleStr+" \nUse format: "
					+ "RULE_TYPE|MATCH_TYPE|MATCH \n\n"
					+ "Where:\n"
					+ "RULE TYPES: FILE_NAME PATH ALL \n"
					+ "MATCH TYPES: EQUAL_TO STARTS_WITH ENDS_WITH CONTAINS \n"
					+ "MATCH: file names or folder path etc\n\n"
					+ "EXAMPLES: \n"
					+ "FILE_NAME|EQUAL_TO|test.txt"
					);
		}
	}

}
