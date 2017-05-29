package org.teste.avs.view.models;

import java.lang.reflect.Field;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class TableField {
	private final Class<?> clazz;
	private final String fieldName;
	private final String columnName;
	private String width;
	private TableCellRenderer cellRenderer;
	private TableCellEditor cellEditor;

	public TableField(Class<?> clazz, String fieldName, String columnName) {
		this.clazz = clazz;
		this.fieldName = fieldName;
		this.columnName = columnName;
	}

	public Object getValue(Object obj) {
		try {
			return getField(clazz).get(obj);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public Class<?> getFieldClass(){
		try {
			return getField(clazz).getDeclaringClass();
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getCollumName() {
		return columnName;
	}

	private Field getField(Class<?> clazz) throws Exception{
		try {
			Field f = clazz.getDeclaredField(fieldName);
			f.setAccessible(true);
			return f;
		} catch (NoSuchFieldException nsfe) {
			if(clazz.getSuperclass()!=null){
				return getField(clazz.getSuperclass());
			}else{
				throw nsfe;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public void confTableColumn(TableColumn tc) {
		configureWidth(tc);
		configureRenderer(tc);
		configureEditor(tc);
	}

	private void configureRenderer(TableColumn tc) {
		if(cellRenderer!=null){
			tc.setCellRenderer(cellRenderer);
		}
	}
	
	private void configureEditor(TableColumn tc){
		if(cellEditor!=null){
			tc.setCellEditor(cellEditor);
		}
	}

	private void configureWidth(TableColumn tc) {
		if(width==null || width.trim().isEmpty()){
			return;
		}
		if(width.endsWith("!")){
			int s = Integer.parseInt(width.replace("!", ""));
			tc.setMaxWidth(s);
			tc.setMinWidth(s);
		}else if(width.contains(":")){
			String[] split = width.split(":");
			int index = 0;
			Integer min = toInt(split, index++);
			Integer pref = toInt(split, index++);
			Integer max = toInt(split, index++);
			if(min!=null){
				tc.setMinWidth(min);
			}
			if(pref!=null){
				tc.setPreferredWidth(pref);
			}
			if(max!=null){
				tc.setMaxWidth(max);
			}
		}else{
			tc.setPreferredWidth(Integer.parseInt(width.trim()));
		}
	}

	private Integer toInt(String[] split, int index){
		String s= index>split.length-1?"":split[index];
		return s==null || s.isEmpty() ? null : Integer.parseInt(s);
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public TableCellRenderer getCellRenderer() {
		return cellRenderer;
	}

	public void setCellRenderer(TableCellRenderer cellRenderer) {
		this.cellRenderer = cellRenderer;
	}

	public TableCellEditor getCellEditor() {
		return cellEditor;
	}

	public void setCellEditor(TableCellEditor cellEditor) {
		this.cellEditor = cellEditor;
	}

}
