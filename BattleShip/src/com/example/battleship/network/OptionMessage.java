package com.example.battleship.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Properties;

public class OptionMessage extends BattleShipMessage {

	private Properties property;
	
	public OptionMessage() {
	}
	
	public OptionMessage(Properties property) {
		setProperty(property);
	}
	
	public Properties getProperty() {
		return property;
	}
	
	public void setProperty(Properties property) {
		this.property = property;
	}
	
//	@Override
//	public void read(InputStream in) throws IOException {
//		ObjectInputStream ois = new ObjectInputStream(in);
//		try {
//			property = (Properties) ois.readObject();
//		} catch (ClassNotFoundException e) {
//			throw new IOException("Bad options");
//		}
//	}
//
//	@Override
//	public void write(OutputStream out) throws IOException {
//		ObjectOutputStream ous = new ObjectOutputStream(out);
//		ous.writeObject(property);
//	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(property);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		property = (Properties) in.readObject();
	}

}
