package electory.client.audio;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.sound.sampled.AudioFormat;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import ibxm.IBXM;
import ibxm.Module;
import paulscode.sound.ICodec;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemLogger;

/**
 * The CodecIBXM class provides an ICodec interface for reading from MOD/S3M/XM
 * files via the IBXM library. <b><i> SoundSystem CodecIBXM Class
 * License:</b></i><br>
 * <b><br>
 * You are free to use this class for any purpose, commercial or otherwise. You
 * may modify this class or source code, and distribute it any way you like,
 * provided the following conditions are met: <br>
 * 1) You may not falsely claim to be the author of this class or any unmodified
 * portion of it. <br>
 * 2) You may not copyright this class or a modified version of it and then sue
 * me for copyright infringement. <br>
 * 3) If you modify the source code, you must clearly document the changes made
 * before redistributing the modified source code, so other users know it is not
 * the original code. <br>
 * 4) You are not required to give me credit for this class in any derived work,
 * but if you do, you must also mention my website: http://www.paulscode.com
 * <br>
 * 5) I the author will not be responsible for any damages (physical, financial,
 * or otherwise) caused by the use if this class or any portion of it. <br>
 * 6) I the author do not guarantee, warrant, or make any representations,
 * either expressed or implied, regarding the use of this class or any portion
 * of it. <br>
 * <br>
 * Author: Paul Lamb <br>
 * http://www.paulscode.com </b><br>
 * <br>
 * <b> This software is based on or using the IBXM library available from
 * http://www.geocities.com/sunet2000/ </b><br>
 * <br>
 * <br>
 * <b> IBXM is copyright (c) 2007, Martin Cameron, and is licensed under the BSD
 * License. <br>
 * <br>
 * All rights reserved. <br>
 * <br>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <br>
 * <br>
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. Neither the name of mumart nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission. <br>
 * <br>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE. <br>
 * <br>
 * <br>
 * </b>
 */
public class CodecIBXM implements ICodec {
	/**
	 * Used to return a current value from one of the synchronized boolean-interface
	 * methods.
	 */
	private static final boolean GET = false;

	/**
	 * Used to set the value in one of the synchronized boolean-interface methods.
	 */
	private static final boolean SET = true;

	/**
	 * Used when a parameter for one of the synchronized boolean-interface methods
	 * is not aplicable.
	 */
	private static final boolean XXX = false;

	/**
	 * True if there is no more data to read in.
	 */
	private boolean endOfStream = false;

	/**
	 * True if the stream has finished initializing.
	 */
	private boolean initialized = false;

	/**
	 * Format the converted audio will be in.
	 */
	private AudioFormat myAudioFormat = null;

	/**
	 * True if the using library requires data read by this codec to be
	 * reverse-ordered before returning it from methods read() and readAll().
	 */
	private boolean reverseBytes = false;

	/**
	 * IBXM decoder.
	 */
	private IBXM ibxm;

	/**
	 * Module instance to be played.
	 */
	private Module module;

	/**
	 * Processes status messages, warnings, and error messages.
	 */
	private SoundSystemLogger logger;

	private int playPosition;
	private int songDuration;

	/**
	 * Constructor: Grabs a handle to the logger.
	 */
	public CodecIBXM() {
		logger = SoundSystemConfig.getLogger();
	}

	/**
	 * Tells this codec when it will need to reverse the byte order of the data
	 * before returning it in the read() and readAll() methods. The IBXM library
	 * produces audio data in a format that some external audio libraries require to
	 * be reversed. Derivatives of the Library and Source classes for audio
	 * libraries which require this type of data to be reversed will call the
	 * reverseByteOrder() method.
	 * 
	 * @param b True if the calling audio library requires byte-reversal.
	 */
	public void reverseByteOrder(boolean b) {
		reverseBytes = b;
	}

	/**
	 * Prepares an audio stream to read from. If another stream is already opened,
	 * it will be closed and a new audio stream opened in its place.
	 * 
	 * @param url URL to an audio file to stream from.
	 * @return False if an error occurred or if end of stream was reached.
	 */
	public boolean initialize(URL url) {
		System.out.println("i");
		initialized(SET, false);
		cleanup();

		if (url == null) {
			errorMessage("url null in method 'initialize'");
			cleanup();
			return false;
		}

		InputStream is = null;

		try {
			is = url.openStream();
		} catch (IOException ioe) {
			errorMessage("Unable to open stream in method 'initialize'");
			printStackTrace(ioe);
			return false;
		}

		try {
			module = loadModule(is);
		} catch (IllegalArgumentException iae) {
			errorMessage("Illegal argument in method 'initialize'");
			printStackTrace(iae);
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioe) {
				}
			}
			return false;
		} catch (IOException ioe) {
			errorMessage("Error loading module in method 'initialize'");
			printStackTrace(ioe);
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioe2) {
				}
			}
			return false;
		}

		if (ibxm == null)
			ibxm = new IBXM(module, 48000);
		if (myAudioFormat == null)
			myAudioFormat = new AudioFormat(48000, 16, 2, true, true);
		if (mixBuf == null) {
			mixBuf = new int[ibxm.getMixBufferLength()];
		}
		if (outBuf == null) {
			outBuf = new byte[mixBuf.length * 2];
		}

		songDuration = ibxm.calculateSongDuration() * 4;

		if (is != null) {
			try {
				is.close();
			} catch (IOException ioe) {
			}
		}

		endOfStream(SET, false);
		initialized(SET, true);
		return true;
	}

	/**
	 * Returns false if the stream is busy initializing.
	 * 
	 * @return True if steam is initialized.
	 */
	public boolean initialized() {
		return initialized(GET, XXX);
	}

	private int[] mixBuf;
	private byte[] outBuf;

	/**
	 * Reads in one stream buffer worth of audio data. See
	 * {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more
	 * information about accessing and changing default settings.
	 * 
	 * @return The audio data wrapped into a SoundBuffer context.
	 */
	public SoundBuffer read() {
		if (endOfStream(GET, XXX))
			return null;

		if (module == null) {
			errorMessage("Module null in method 'read'");
			return null;
		}

		// Check to make sure there is an audio format:
		if (myAudioFormat == null) {
			errorMessage("Audio Format null in method 'read'");
			return null;
		}
		
		System.out.println("f");

		int count = ibxm.getAudio(mixBuf);

		int outIdx = 0;
		int mixIdx = 0;
		for (int mixEnd = count * 2; mixIdx < mixEnd; mixIdx++) {
			int ampl = mixBuf[mixIdx];
			if (ampl > 32767)
				ampl = 32767;
			if (ampl < -32768)
				ampl = -32768;
			outBuf[outIdx++] = (byte) (ampl >> 8);
			outBuf[outIdx++] = (byte) ampl;
		}

		playPosition += outIdx;

		if (playPosition > songDuration) {
			endOfStream(SET, true);
		}

		if (reverseBytes)
			reverseBytes(outBuf, 0, outIdx);

		SoundBuffer sb = new SoundBuffer(trimArray(outBuf, outIdx), getAudioFormat());

		return sb;
	}

	/**
	 * Reads in all the audio data from the stream (up to the default "maximum file
	 * size". See {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for
	 * more information about accessing and changing default settings.
	 * 
	 * @return the audio data wrapped into a SoundBuffer context.
	 */
	public SoundBuffer readAll() {

		if (module == null) {
			errorMessage("Module null in method 'read'");
			return null;
		}

		// Check to make sure there is an audio format:
		if (myAudioFormat == null) {
			errorMessage("Audio Format null in method 'read'");
			return null;
		}
		
		byte[] fullBuffer = null;

		while (!endOfStream(GET, XXX)) {
			int count = ibxm.getAudio(mixBuf);

			int outIdx = 0;
			int mixIdx = 0;
			for (int mixEnd = count * 2; mixIdx < mixEnd; mixIdx++) {
				int ampl = mixBuf[mixIdx];
				if (ampl > 32767)
					ampl = 32767;
				if (ampl < -32768)
					ampl = -32768;
				outBuf[outIdx++] = (byte) (ampl >> 8);
				outBuf[outIdx++] = (byte) ampl;
			}

			playPosition += outIdx;

			/*
			 * if (outIdx == 0) { endOfStream(SET, true); }
			 */

			if (playPosition > songDuration + 1) {
				endOfStream(SET, true);
			}

			if (reverseBytes)
				reverseBytes(outBuf, 0, outIdx);
			
			fullBuffer = appendByteArrays(fullBuffer, outBuf, outIdx);
		}

		SoundBuffer sb = new SoundBuffer(fullBuffer, getAudioFormat());

		return sb;
	}

	/**
	 * Returns false if there is still more data available to be read in.
	 * 
	 * @return True if end of stream was reached.
	 */
	public boolean endOfStream() {
		return endOfStream(GET, XXX);
	}

	/**
	 * Closes the audio stream and remove references to all instantiated objects.
	 */
	public void cleanup() {
		if (ibxm != null)
			ibxm.seek(0);

		playPosition = 0;
	}

	/**
	 * Returns the audio format of the data being returned by the read() and
	 * readAll() methods.
	 * 
	 * @return Information wrapped into an AudioFormat context.
	 */
	public AudioFormat getAudioFormat() {
		return myAudioFormat;
	}

	/**
	 * Decodes the data in the specified InputStream into an instance of
	 * ibxm.Module.
	 * 
	 * @param input an InputStream containing the module file to be decoded.
	 * @throws IllegalArgumentException if the data is not recognised as a module
	 *                                  file.
	 */
	private static Module loadModule(InputStream input) throws IllegalArgumentException, IOException {
		return new Module(input);
	}

	/**
	 * Internal method for synchronizing access to the boolean 'initialized'.
	 * 
	 * @param action GET or SET.
	 * @param value  New value if action == SET, or XXX if action == GET.
	 * @return True if steam is initialized.
	 */
	private synchronized boolean initialized(boolean action, boolean value) {
		if (action == SET)
			initialized = value;
		return initialized;
	}

	/**
	 * Internal method for synchronizing access to the boolean 'endOfStream'.
	 * 
	 * @param action GET or SET.
	 * @param value  New value if action == SET, or XXX if action == GET.
	 * @return True if end of stream was reached.
	 */
	private synchronized boolean endOfStream(boolean action, boolean value) {
		if (action == SET)
			endOfStream = value;

		return endOfStream;
	}

	/**
	 * Trims down the size of the array if it is larger than the specified maximum
	 * length.
	 * 
	 * @param array     Array containing audio data.
	 * @param maxLength Maximum size this array may be.
	 * @return New array.
	 */
	private static byte[] trimArray(byte[] array, int maxLength) {
		byte[] trimmedArray = null;
		if (array != null && array.length > maxLength) {
			trimmedArray = new byte[maxLength];
			System.arraycopy(array, 0, trimmedArray, 0, maxLength);
		}
		return trimmedArray;
	}

	/**
	 * Reverse-orders all bytes contained in the specified array.
	 * 
	 * @param buffer Array containing audio data.
	 */
	public static void reverseBytes(byte[] buffer) {
		reverseBytes(buffer, 0, buffer.length);
	}

	/**
	 * Reverse-orders the specified range of bytes contained in the specified array.
	 * 
	 * @param buffer Array containing audio data.
	 * @param offset Array index to begin.
	 * @param size   number of bytes to reverse-order.
	 */
	public static void reverseBytes(byte[] buffer, int offset, int size) {

		byte b;
		for (int i = offset; i < (offset + size); i += 2) {
			b = buffer[i];
			buffer[i] = buffer[i + 1];
			buffer[i + 1] = b;
		}
	}

	/**
	 * Converts sound bytes to little-endian format.
	 * 
	 * @param audio_bytes    The original wave data
	 * @param two_bytes_data For stereo sounds.
	 * @return byte array containing the converted data.
	 */
	private static byte[] convertAudioBytes(byte[] audio_bytes, boolean two_bytes_data) {
		ByteBuffer dest = ByteBuffer.allocateDirect(audio_bytes.length);
		dest.order(ByteOrder.nativeOrder());
		ByteBuffer src = ByteBuffer.wrap(audio_bytes);
		src.order(ByteOrder.LITTLE_ENDIAN);
		if (two_bytes_data) {
			ShortBuffer dest_short = dest.asShortBuffer();
			ShortBuffer src_short = src.asShortBuffer();
			while (src_short.hasRemaining()) {
				dest_short.put(src_short.get());
			}
		} else {
			while (src.hasRemaining()) {
				dest.put(src.get());
			}
		}
		dest.rewind();

		if (!dest.hasArray()) {
			byte[] arrayBackedBuffer = new byte[dest.capacity()];
			dest.get(arrayBackedBuffer);
			dest.clear();

			return arrayBackedBuffer;
		}

		return dest.array();
	}

	/**
	 * Creates a new array with the second array appended to the end of the first
	 * array.
	 * 
	 * @param arrayOne The first array.
	 * @param arrayTwo The second array.
	 * @param length   How many bytes to append from the second array.
	 * @return Byte array containing information from both arrays.
	 */
	private static byte[] appendByteArrays(byte[] arrayOne, byte[] arrayTwo, int length) {
		byte[] newArray;
		if (arrayOne == null && arrayTwo == null) {
			// no data, just return
			return null;
		} else if (arrayOne == null) {
			// create the new array, same length as arrayTwo:
			newArray = new byte[length];
			// fill the new array with the contents of arrayTwo:
			System.arraycopy(arrayTwo, 0, newArray, 0, length);
			arrayTwo = null;
		} else if (arrayTwo == null) {
			// create the new array, same length as arrayOne:
			newArray = new byte[arrayOne.length];
			// fill the new array with the contents of arrayOne:
			System.arraycopy(arrayOne, 0, newArray, 0, arrayOne.length);
			arrayOne = null;
		} else {
			// create the new array large enough to hold both arrays:
			newArray = new byte[arrayOne.length + length];
			System.arraycopy(arrayOne, 0, newArray, 0, arrayOne.length);
			// fill the new array with the contents of both arrays:
			System.arraycopy(arrayTwo, 0, newArray, arrayOne.length, length);
			arrayOne = null;
			arrayTwo = null;
		}

		return newArray;
	}

	/**
	 * Prints an error message.
	 * 
	 * @param message Message to print.
	 */
	private void errorMessage(String message) {
		logger.errorMessage("CodecIBXM", message, 0);
	}

	/**
	 * Prints an exception's error message followed by the stack trace.
	 * 
	 * @param e Exception containing the information to print.
	 */
	private void printStackTrace(Exception e) {
		logger.printStackTrace(e, 1);
	}
}
