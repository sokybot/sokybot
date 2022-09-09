package org.sokybot.packetsniffer.trafficmonitor;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import sokybot.bot.network.packets.Encoding;
import sokybot.bot.network.packets.ImmutablePacket;
import sokybot.bot.network.packets.PacketDirection;

public class TrafficMonitorCellRender extends DefaultTableCellRenderer {

	public TrafficMonitorCellRender() {

		
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

		if (!isSelected) {

			if (value == PacketDirection.SERVER) {
				// rgb(178, 64, 128)

				// c.setForeground(new Color(0xFFF9B2));
				c.setForeground(new Color(0xECAC5D));
			} else if (value == PacketDirection.CLIENT) {
				c.setForeground(new Color(178, 64, 128));

			} else if (column == 1) { // 0x3E215D
				if (value == Encoding.PLAIN) {
					c.setForeground(new Color(0x5D8233));
				} else {
					c.setForeground(new Color(0x890596));
				}
			} else if (column == 2 || column == 3 || column == 4 || column == 5) {
				c.setForeground(new Color(0xF5F7B2));

			} else {
				c.setForeground(null);
			}

			// c.setForeground(new Color(0xFFF9B2));

			// c.setForeground(new Color(0xF5F7B2));


			
			if ((row % 2) == 0) {
				c.setBackground(Color.DARK_GRAY);
			} else {
				c.setBackground(null);
			}
			
			//TableModel model = table.getModel();

			//if (model.getValueAt(row, 1) == Encoding.ENCRYPTED) {
			//	 c.setBackground(new Color(0xFFF9B2));

		//	} else {
			//	 c.setBackground(null);
			//}
		}
		return c;
	}

}
