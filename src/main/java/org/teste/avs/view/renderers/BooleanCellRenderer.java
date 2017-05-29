package org.teste.avs.view.renderers;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

@SuppressWarnings("serial")
public class BooleanCellRenderer extends DefaultTableCellRenderer{

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row, int column) {
		Object newValue = value;
		if(value instanceof Boolean){
			newValue = ((Boolean)value)?"YES":"NO";
		}
		return super.getTableCellRendererComponent(table, newValue, isSelected, hasFocus, row, column);
	}
}
