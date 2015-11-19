/**
 *  Copyright (C) 2015 DreamLinx <dreamlinx@dreamlinx.org>
 *  All Rights Reserved.
 *
 *  This file is part of DreamLinx.
 *
 *  DreamLinx is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DreamLinx is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with DreamLinx. If not, see <http://www.gnu.org/licenses/>.
 */

package org.dreamlinx.engine.fn;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Functions for handling the serialization of objects.
 * 
 * @author Marco Merli <yohji@dreamlinx.org>
 * @since 1.0
 */
public class SerialFn {

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T load(String filename, Class<T> type) throws Exception
	{
		if (! (type instanceof Serializable))
			throw new IllegalArgumentException("Type class must be implements Serializable");

		T obj = (T) read(filename);
		if (obj == null) {
			obj = type.newInstance();
			write(filename, (Serializable) obj);
		}

		return obj;
	}

	public static Serializable read(String filename)
	{
		Serializable obj = null;
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
			obj = (Serializable) in.readObject();
		}
		catch (Exception e) {}

		return obj;
	}

	public static void write(String filename, Serializable instance) throws Exception
	{
		try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(filename))) {
			o.writeObject(instance);
		}
	}

	public static boolean delete(String filename) throws Exception
	{
		return new File(filename).delete();
	}

	public static ByteArrayInputStream serialize(Serializable obj) throws Exception
	{
		try (ByteArrayOutputStream binOut = new ByteArrayOutputStream();
			ObjectOutputStream objOut = new ObjectOutputStream(binOut);) {
			objOut.writeObject(obj);
			return new ByteArrayInputStream(binOut.toByteArray());
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T deserialize(InputStream in, Class<T> type) throws Exception
	{
		try (ObjectInputStream objIn = new ObjectInputStream(in);) {
			return (T) objIn.readObject();
		}
	}

	private SerialFn() {}
}
