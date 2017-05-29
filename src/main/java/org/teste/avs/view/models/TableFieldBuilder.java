package org.teste.avs.view.models;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class TableFieldBuilder {
	private List<TableField> fields = new LinkedList<>();
	private final Class<?> clazz;
	private TableField current;
	
	public TableFieldBuilder(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public TableFieldBuilder field(String fieldName, String columnName){
		current = new TableField(clazz, fieldName, columnName);
		return this;
	}
	
	public TableFieldBuilder width(String width){
		verifyCurrent();
		current.setWidth(width);
		return this;
	}
	
	public TableFieldBuilder renderer(TableCellRenderer renderer){
		verifyCurrent();
		current.setCellRenderer(renderer);
		return this;
	}
	
	public TableFieldBuilder editor(TableCellEditor editor){
		verifyCurrent();
		current.setCellEditor(editor);
		return this;
	}

	public TableFieldBuilder add(){
		verifyCurrent();
		fields.add(current);
		this.current = null;
		return this;
	}
	
	private void verifyCurrent() {
		if(current==null){
			throw new RuntimeException("É necessário chamar o método 'field' antes de adicionar o campo!");
		}
	}
	
	public TableField[] build() {
		return this.fields.toArray(new TableField[0]);
	}
}
