package org.sokybot.packetsniffer;

import java.util.List;

import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;

import sokybot.bot.network.packets.ImmutablePacket;
import sokybot.ui.bot.packetsniffer.packetanalyzer.PacketAnalyzer;
import sokybot.ui.bot.packetsniffer.packetanalyzer.PacketVar;
import sokybot.ui.bot.packetsniffer.packetanalyzer.VarTable;
import sokybot.ui.bot.packetsniffer.packettracer.PacketTracer;
import sokybot.ui.bot.packetsniffer.packettracer.PacketTracerModel;
import sokybot.ui.bot.packetsniffer.packettracer.PacketTracerTableModel;
import sokybot.ui.bot.packetsniffer.trafficmonitor.PacketMonitorTableModel;
import sokybot.ui.bot.packetsniffer.trafficmonitor.TablePacket;

public class PacketSnifferService implements IPacketSnifferService {

	private PacketMonitorTableModel monitor;

	private PacketTracerTableModel tracers;

	private List<PacketVar> packetVars ; 
	
	private ObjectRepository<PacketTracerModel> tracersRepo;

	private boolean monitorEnabled = true;

	public PacketSnifferService(PacketMonitorTableModel monitorModel,
			PacketTracerTableModel tracerModel,
			ObjectRepository<PacketTracerModel> tracerRepo  , List<PacketVar> packetVars) {

		this.monitor = monitorModel;
		this.tracers = tracerModel;
		this.tracersRepo = tracerRepo;
		this.packetVars = packetVars;
	}

	@Override
	public void analyze() {

		TablePacket [] packets = this.monitor.toArray() ; 
		if(packets.length > 0)
		PacketAnalyzer.showPacketAnalyzer(packets, this.packetVars);
		
	}

	@Override
	public void ignore(short opcode) {

		this.tracers.setIgnored(opcode);
	}

	@Override
	public void clearMonitor() {

		this.monitor.clear();

	}

	@Override
	public void display(ImmutablePacket packet) {

		short opcode = packet.getOpcode();

		PacketTracerModel tracer = null ; 
		
		if (!this.tracers.containsOpcode(opcode)) {

			  tracer = this.tracersRepo.find(ObjectFilters.eq("opcode", opcode)).firstOrDefault();

			if (tracer == null) {
				tracer = new PacketTracerModel();

				tracer.setIgnored(false);
				
				tracer.setName("UNKNOWN");
				tracer.setOpcode(opcode);
				tracer.setSource(packet.getPacketSource());

			} 

			tracer.setCount(1);
			this.tracers.addTracer(tracer);
			
		} else {

			// we need to update packet count field
			tracer = this.tracers.getByOpcode(opcode);
			this.tracers.incPacketCount(opcode);
			

		}
		
		
		if (!tracer.isIgnored() && monitorEnabled)
			this.monitor.addRow(TablePacket.createTablePacket(tracer.getName(), packet));
	

		
	}

	@Override
	public void pasueMonitoring() {
		this.monitorEnabled = false;
	}

	@Override
	public void resumeMonitoring() {

		this.monitorEnabled = true;
	}

	@Override
	public void saveOrUpdate(PacketTracerModel tracer) {
	   
		if(tracer != null) {
		this.tracersRepo.update(tracer, true) ; 
		 System.out.println("On Save  : " + tracer) ; 
		}
	}
}
