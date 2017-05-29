package org.teste.avs.view.renderers;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class DateCellRenderer extends DefaultTableCellRenderer{
	
	private SimpleDateFormat sdf;
	
	public DateCellRenderer(String pattern) {
		sdf = new SimpleDateFormat(pattern);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		Object newValue = value;
		if(value instanceof Date){
			newValue = sdf.format((Date)value);
		}
		return super.getTableCellRendererComponent(table, newValue, isSelected, hasFocus, row, column);
	}
}
