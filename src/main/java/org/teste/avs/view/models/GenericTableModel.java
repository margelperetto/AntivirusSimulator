package org.teste.avs.view.models;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

@SuppressWarnings("serial")
public class GenericTableModel<T> extends AbstractTableModel implements PropertyChangeListener{

	private final List<T> data = new LinkedList<>();
	private final TableField[] fields;
	private JTable table;

	@SafeVarargs
	public GenericTableModel(TableField... fields) {
		this.fields = fields;
	}

	public void setData(List<T> data) {
		this.data.clear();
		addData(data);
	}

	public void addData(List<T> data) {
		this.data.addAll(data);
		fireTableDataChanged();
	}

	public List<T> getData() {
		return data;
	}

	public void clearData(){
		this.data.clear();
		fireTableDataChanged();
	}

	public void remove(T obj){
		this.data.remove(obj);
		fireTableDataChanged();
	}

	public void add(T obj){
		this.data.add(obj);
		fireTableDataChanged();
	}
	
	public void put(T obj){
		int i = indexTo(obj);
		if(i<0){
			add(obj);
		}else{
			this.data.remove(i);
			this.data.add(i, obj);
			fireTableDataChanged();
		}
	}
	
	public T getSelected(){
		List<T> list = getSelecteds();
		if(list.size()>1){
			throw new RuntimeException("Mais do que um objeto selecionado na tabela!");
		}
		return list.isEmpty()?null:list.get(0);
	}
	
	public List<T> getSelecteds(){
		if(table == null){
			throw new RuntimeException("O tablemodel não foi adicionado em uma tabela!");
		}
		List<T> list = new LinkedList<>();
		int[] rows = table.getSelectedRows();
		if(rows.length>0){
			for(int i : rows){
				list.add(data.get(i));
			}
		}
		
		return list;
	}
	
	public int indexTo(T obj){
		int index = -1;
		for(T o : data){
			index++;
			if(o.equals(obj)){
				return index;
			}
		}
		return -1;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return fields.length;
	}

	@Override
	public String getColumnName(int column) {
		return fields[column].getCollumName();
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return fields[columnIndex].getFieldClass();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return fields[columnIndex].getValue(data.get(rowIndex));
	}
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		super.addTableModelListener(l);

		if(l instanceof JTable){
			table = (JTable)l;
			table.addPropertyChangeListener(this);
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("model")){
			TableColumnModel m = table.getColumnModel();
			for(TableField f : fields){
				TableColumn tc = m.getColumn(m.getColumnIndex(f.getCollumName()));
				f.confTableColumn(tc);
			}
		}
	}

}
