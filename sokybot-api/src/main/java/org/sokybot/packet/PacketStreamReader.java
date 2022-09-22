package org.sokybot.packet;

public class PacketStreamReader implements IStreamReader {

	private IPacketReader reader;

	private int pos = 0;

	public PacketStreamReader(IPacketReader reader) {
		this.reader = reader;
	}

	@Override
	public byte getByte() {

		if (this.reader.remainingBytes(this.pos) >= 1) {
			byte res = this.reader.readByte(this.pos);
			this.pos++;
			return res;
		}
		throw new IndexOutOfBoundsException(this.pos);

	}

	@Override
	public byte[] getBytes(int len) {

		if (this.reader.remainingBytes(this.pos) >= len) {
			byte res[] = this.reader.readBytes(this.pos, len);
			this.pos += len;
			return res;
		}
		throw new IndexOutOfBoundsException(this.pos);
	}

	@Override
	public int getInt() {

		if (this.reader.remainingBytes(this.pos) >= 4) {
			int res = this.reader.readInt(this.pos);
			this.pos += 4;
			return res;
		}
		throw new IndexOutOfBoundsException(this.pos);

	}

	@Override
	public long getLong() {

		if (this.reader.remainingBytes(this.pos) >= 8) {
			long res = this.reader.readLong(this.pos);
			this.pos += 8;
			return res;
		}
		throw new IndexOutOfBoundsException(this.pos);

	}

	@Override
	public short getShort() {

		if (this.reader.remainingBytes(this.pos) >= 2) {
			short res = this.reader.readShort(this.pos);
			this.pos += 2;
			return res;
		}
		throw new IndexOutOfBoundsException(this.pos);

	}

	
	@Override
	public void skip(int len) {
	 
		if (this.reader.remainingBytes(this.pos) >= len) {
			
			this.pos += len;
			return ; 
		}
		throw new IndexOutOfBoundsException(this.pos);

	}
}
