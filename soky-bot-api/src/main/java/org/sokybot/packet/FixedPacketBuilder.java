package org.sokybot.packet;

import java.nio.ByteBuffer;


public class FixedPacketBuilder implements IPacketBuilder {

	private ByteBuffer buffer;

	public FixedPacketBuilder(ByteBuffer buffer) {

		this.buffer = buffer;
		this.buffer.position(6);
		
	}

	@Override
	public MutablePacket build() {

		MutablePacket packet =  new MutablePacket(buffer);
		packet.setPacketSize((short)(this.buffer.limit() - 6));
		packet.setDataEncoding(Encoding.PLAIN);
		return packet ; 
	}

	@Override
	public IPacketBuilder put(byte value) {
		this.buffer.put(value);
		return this;
	}

	@Override
	public IPacketBuilder putBytes(byte[] bytes) {
		this.buffer.put(bytes);
		return this;
	}

	@Override
	public IPacketBuilder putInt(int value) {
		this.buffer.putInt(value);
		return this;
	}

	@Override
	public IPacketBuilder putLong(long value) {

		this.buffer.putLong(value);
		return this;
	}

	@Override
	public IPacketBuilder putShort(short value) {
		this.buffer.putShort(value);
		return this;
	}

}
