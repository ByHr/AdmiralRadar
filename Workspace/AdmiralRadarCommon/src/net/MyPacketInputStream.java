package net;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import exceptions.BufferEmptyException;
import exceptions.PacketPayloadIncompatable;
import game.Position;
import game.GameMap;
import game.Role;
import game.Spaceship;
import ops.User;

public class MyPacketInputStream extends ObjectInputStream {
	
	MyPacket buffer;
	
	public MyPacketInputStream(InputStream in) throws IOException {
		super(in);
	}
	
	
	public ObjEnum getClassOfNext(){
			
		if (buffer == null) throw new BufferEmptyException();
		return buffer.getObjectClass();
		
	}
	
	private MyPacketable getBuffer() {
		MyPacket x = buffer;
	
		try {
			buffer = (MyPacket) readUnshared();
		} catch (ClassNotFoundException | IOException e) {
			
		}
		return x.getObject();
	}
	
	public User getNextUser() throws IOException {
		if (getClassOfNext() == ObjEnum.USER) return (User) getBuffer();
		else throw new PacketPayloadIncompatable();
	}


	public Spaceship getNextSpaceship() throws IOException {
		if (getClassOfNext() == ObjEnum.SPACESHIP) return (Spaceship) getBuffer();
		else throw new PacketPayloadIncompatable();
	}
	
	
	public GameMap getNextMap() throws IOException {
		if (getClassOfNext() == ObjEnum.MAP) return (GameMap) getBuffer();
		else throw new PacketPayloadIncompatable();
	}
	
	
	public Position getNextPosition() throws IOException {
		if (getClassOfNext() == ObjEnum.POSITION) return (Position) getBuffer();
		else throw new PacketPayloadIncompatable();
	}
	
	
	public String getNextString() throws IOException {
		if (getClassOfNext() == ObjEnum.STRING) return ((SendableString) getBuffer()).getContent();
		else throw new PacketPayloadIncompatable();
	}
	
	
	public Role getNextRole() throws IOException {
		if (getClassOfNext() == ObjEnum.ROLE) return (Role) getBuffer();
		else throw new PacketPayloadIncompatable();
	}

	
}
