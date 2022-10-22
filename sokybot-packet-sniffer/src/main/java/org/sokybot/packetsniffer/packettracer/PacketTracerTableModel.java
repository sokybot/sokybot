package org.sokybot.packetsniffer.packettracer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.table.AbstractTableModel;
import javax.swing.text.StyledEditorKit.BoldAction;

import sokybot.bot.network.packets.PacketDirection;
import sokybot.ui.bot.packetsniffer.trafficmonitor.PacketMonitorHandler;

public class PacketTracerTableModel extends AbstractTableModel {

	private String[] columns = { "Ignored", "Source", "Name", "Opcode", "Packet Count" };

	private Map<Short, PacketTracerModel> rows = new LinkedHashMap<>();

	private Map<Short, Integer> opcodeIndex = new HashMap<>();
	private Map<Integer, Short> indexOpcode = new HashMap<>();

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columns.length;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return rows.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {

		short opcode = this.indexOpcode.get(rowIndex);

		PacketTracerModel model = this.rows.get(opcode);

		if (model != null) {

			switch (columnIndex) {

			case 0:
				return model.isIgnored();
			case 1:
				return model.getSource();
			case 2:
				return model.getName();
			case 3:
				return "0x" + Integer.toHexString(model.getOpcode() & 0xffff);
			case 4:
				return model.getCount();

			}

		}

		return null;
	}

	@Override
	public String getColumnName(int column) {
		return this.columns[column];
	}

	public void addTracer(PacketTracerModel tracer) {
		short opcode = tracer.getOpcode();

		if (!this.rows.containsKey(opcode)) {
			this.rows.put(opcode, tracer);

			int index = this.rows.size() - 1;
			updateConstrains(index, opcode);
			this.fireTableRowsInserted(index, index);
		}
	}

	private void updateConstrains(int index, short opcode) {
		this.opcodeIndex.put(opcode, index);
		this.indexOpcode.put(index, opcode);
	}

	public boolean containsOpcode(short opcode) {
		return this.rows.containsKey(opcode);
	}

	public PacketTracerModel getByOpcode(short opcode) {

		return this.rows.get(opcode);
	}

	public void incPacketCount(short opcode) {
		PacketTracerModel model = this.rows.get(opcode);

		if (model != null) {
			int count = model.getCount();
			model.setCount((count + 1));

			int index = this.opcodeIndex.get(opcode);

			this.fireTableCellUpdated(index, 4);
		}
	}

	public String getName(short opcode) {

		PacketTracerModel model = this.rows.get(opcode);

		if (model != null) {

			return model.getName();
		}

		return null;
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {

		switch (columnIndex) {
		case 0:
			return Boolean.class;
		case 1:
			return NetworkPeer.class;
		case 2:

		case 3:
		case 4:
			return String.class;
		default:
			break;
		}

		return Object.class;
	}

	public PacketTracerModel getByIndex(int index) {
		short opcode = this.indexOpcode.get(index);

		return this.rows.get(opcode);

	}

	public int getOpcodeIndex(short opcode) { 
		return this.opcodeIndex.get(opcode) ; 
	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {

		return (columnIndex == 0 || columnIndex == 2);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

		short opcode = this.indexOpcode.get(rowIndex);
		PacketTracerModel model = this.rows.get(opcode);
		if (model != null) {
			if (columnIndex == 0 && aValue instanceof Boolean) {
				model.setIgnored((Boolean) aValue);
			} else if (columnIndex == 2 && aValue instanceof String) {
				model.setName((String) aValue);
			}

		}
	}
	
	public void setIgnored(short opcode) { 
	
	 PacketTracerModel model = 	this.rows.get(opcode) ; 
	 
	 	if(model != null) { 
	 		model.setIgnored(true);
	 		
	 		
	 		this.fireTableCellUpdated(this.opcodeIndex.get(opcode), 0);
	 	}
	}
	
}
