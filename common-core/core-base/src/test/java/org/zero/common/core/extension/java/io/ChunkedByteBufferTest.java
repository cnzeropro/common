package org.zero.common.core.extension.java.io;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/10/24
 */
class ChunkedByteBufferTest {
	@Test
	void shouldSupportInsertDeleteReplaceAndGet() {
		ChunkedByteBuffer buffer = new ChunkedByteBuffer(2);

		buffer.put("helloworld!".getBytes(StandardCharsets.UTF_8));
		buffer.insert(BigInteger.valueOf(5), " ".getBytes(StandardCharsets.UTF_8));
		buffer.replace(BigInteger.valueOf(6), "W".getBytes(StandardCharsets.UTF_8));
		buffer.delete(BigInteger.valueOf(11), BigInteger.ONE);

		assertEquals(BigInteger.valueOf(11), buffer.size());
		assertEquals("hello World", new String(buffer.get(11), StandardCharsets.UTF_8));
		assertEquals("World", new String(buffer.get(BigInteger.valueOf(6), 5), StandardCharsets.UTF_8));
		assertTrue(buffer.blockSize() > 1);
	}

	@Test
	void shouldCopyWithoutSharingContent() {
		ChunkedByteBuffer buffer = new ChunkedByteBuffer(2);
		buffer.put("abcdef".getBytes(StandardCharsets.UTF_8));

		ChunkedByteBuffer copied = buffer.copy(3);

		assertTrue(copied.contentEquals(buffer));

		copied.insert(BigInteger.ZERO, "z".getBytes(StandardCharsets.UTF_8));

		assertFalse(copied.contentEquals(buffer));
		assertEquals("abcdef", new String(buffer.get(6), StandardCharsets.UTF_8));
		assertEquals("zabcdef", new String(copied.get(7), StandardCharsets.UTF_8));
	}

	@Test
	void shouldReadFromAndWriteToStreams() throws IOException {
		byte[] source = "0123456789".getBytes(StandardCharsets.UTF_8);
		ChunkedByteBuffer buffer = new ChunkedByteBuffer(4);

		BigInteger bytesRead = buffer.readFrom(
				new ByteArrayInputStream(source),
				BigInteger.valueOf(2),
				BigInteger.valueOf(5),
				true
		);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		buffer.writeTo(outputStream);

		assertEquals(BigInteger.valueOf(5), bytesRead);
		assertArrayEquals("23456".getBytes(StandardCharsets.UTF_8), buffer.get(5));
		assertArrayEquals("23456".getBytes(StandardCharsets.UTF_8), outputStream.toByteArray());
	}

	@Test
	void shouldRejectInvalidBlockCapacity() {
		ChunkedByteBuffer buffer = new ChunkedByteBuffer(2);

		assertThrows(IllegalArgumentException.class, () -> new ChunkedByteBuffer(0));
		assertThrows(IllegalArgumentException.class, () -> new ChunkedByteBuffer(-1));
		assertThrows(IllegalArgumentException.class, () -> buffer.copy(0));
	}

	@Test
	void shouldRejectOverflowRangeAndIgnoreZeroLengthWrites() {
		ChunkedByteBuffer buffer = new ChunkedByteBuffer(4);

		buffer.put(new byte[]{1, 2}, 1, 0);

		assertTrue(buffer.isEmpty());
		assertEquals(0, buffer.blockSize());
		assertEquals(BigInteger.ZERO, buffer.size());
		assertThrows(IndexOutOfBoundsException.class, () -> buffer.put(new byte[1], Integer.MAX_VALUE, 2));
	}

	@Test
	void shouldReturnZeroWhenReadingPastEndIntoExistingArray() {
		ChunkedByteBuffer buffer = new ChunkedByteBuffer(4);
		buffer.put("abc".getBytes(StandardCharsets.UTF_8));
		byte[] target = new byte[]{9, 8, 7};

		assertEquals(0, buffer.get(BigInteger.valueOf(3), target));
		assertArrayEquals(new byte[]{9, 8, 7}, target);

		assertEquals(0, buffer.get(BigInteger.valueOf(5), target));
		assertArrayEquals(new byte[]{9, 8, 7}, target);
	}

	@Test
	void shouldKeepCapacityForEmptySlicesAndSnapshotSelfOperations() {
		ChunkedByteBuffer appendBuffer = new ChunkedByteBuffer(2);
		appendBuffer.put("ab".getBytes(StandardCharsets.UTF_8));
		appendBuffer.put(appendBuffer);
		assertEquals("abab", new String(appendBuffer.get(4), StandardCharsets.UTF_8));

		ChunkedByteBuffer insertBuffer = new ChunkedByteBuffer(2);
		insertBuffer.put("ab".getBytes(StandardCharsets.UTF_8));
		insertBuffer.insert(BigInteger.ONE, insertBuffer);
		assertEquals("aabb", new String(insertBuffer.get(4), StandardCharsets.UTF_8));

		ChunkedByteBuffer replaceBuffer = new ChunkedByteBuffer(2);
		replaceBuffer.put("abc".getBytes(StandardCharsets.UTF_8));
		replaceBuffer.replace(BigInteger.ONE, replaceBuffer);
		assertEquals("aabc", new String(replaceBuffer.get(4), StandardCharsets.UTF_8));

		ChunkedByteBuffer emptySlice = replaceBuffer.get(BigInteger.valueOf(99));
		assertTrue(emptySlice.isEmpty());
		assertEquals(2, emptySlice.maxBlockCapacity);
	}

	@Test
	void shouldCompareEqualContentAcrossDifferentLayouts() {
		ChunkedByteBuffer left = new ChunkedByteBuffer(2);
		ChunkedByteBuffer right = new ChunkedByteBuffer(3);
		left.put("abcdef".getBytes(StandardCharsets.UTF_8));
		right.put("abcdef".getBytes(StandardCharsets.UTF_8));

		assertTrue(left.contentEquals(right));

		right.replace(BigInteger.ZERO, "z".getBytes(StandardCharsets.UTF_8));

		assertFalse(left.contentEquals(right));
	}

	@Test
	void shouldCloseInputOnlyWhenRequestedEvenOnFailure() {
		ChunkedByteBuffer closeOnFailure = new ChunkedByteBuffer(4);
		FailingInputStream closedStream = new FailingInputStream("ab".getBytes(StandardCharsets.UTF_8));

		assertThrows(IOException.class, () -> closeOnFailure.readFrom(closedStream, BigInteger.ZERO, BigInteger.TEN, true));
		assertTrue(closedStream.closed);

		ChunkedByteBuffer keepOpenOnFailure = new ChunkedByteBuffer(4);
		FailingInputStream openStream = new FailingInputStream("ab".getBytes(StandardCharsets.UTF_8));

		assertThrows(IOException.class, () -> keepOpenOnFailure.readFrom(openStream, BigInteger.ZERO, BigInteger.TEN, false));
		assertFalse(openStream.closed);
	}

	private static final class FailingInputStream extends InputStream {
		private final byte[] data;
		private boolean firstRead = true;
		private boolean closed = false;

		private FailingInputStream(byte[] data) {
			this.data = data;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			if (len == 0) {
				return 0;
			}
			if (firstRead) {
				firstRead = false;
				int count = Math.min(len, data.length);
				System.arraycopy(data, 0, b, off, count);
				return count;
			}
			throw new IOException("boom");
		}

		@Override
		public int read() throws IOException {
			byte[] single = new byte[1];
			int count = this.read(single, 0, 1);
			return count < 0 ? -1 : single[0] & 0xFF;
		}

		@Override
		public void close() {
			closed = true;
		}
	}
}
