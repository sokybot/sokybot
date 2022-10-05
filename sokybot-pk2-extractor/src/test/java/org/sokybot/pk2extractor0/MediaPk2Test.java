package org.sokybot.pk2extractor0;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.EndianUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Conversion;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sokybot.domain.Division;
import org.sokybot.domain.DivisionInfo;
import org.sokybot.domain.SilkroadType;
import org.sokybot.pk2.IPk2File;
import org.sokybot.pk2.JMXFile;
import org.sokybot.pk2.Pk2File;
import org.sokybot.pk2.Pk2IO;
import org.sokybot.pk2extractor.Pk2Extractors;
import org.sokybot.pk2extractor.IMediaPk2;
import org.sokybot.pk2extractor.MediaPk2;
import org.sokybot.pk2extractor.exception.Pk2InvalidResourceFormatException;
import org.sokybot.pk2extractor.exception.Pk2MissedResourceException;
import org.sokybot.security.Blowfish;

@ExtendWith(MockitoExtension.class)
class MediaPk2Test {

	@Test
	void testExtractType() {
		InputStream in = IOUtils.toInputStream("Language=English\nCountry=Egypt", StandardCharsets.UTF_8);
		JMXFile typeFile = Mockito.mock(JMXFile.class);
		IPk2File file = Mockito.mock(Pk2File.class);
		when(file.findFirst("type.txt")).thenReturn(Optional.of(typeFile));
		when(typeFile.getInputStream()).thenReturn(in);

		MediaPk2 media = new MediaPk2(file, null);
		SilkroadType t = media.extractType();
		assertEquals(t.getLanguage(), "English");
		assertEquals(t.getCountry(), "Egypt");

	}

	@Test
	void testExtractTypeWhenJmxFileNotFound() {

		IPk2File file = Mockito.mock(IPk2File.class);
		assertThrows(Pk2MissedResourceException.class, () -> new MediaPk2(file, null).extractType());
	}

	@Test
	void testExtractTypeWithInvalidJmxFile() {

		InputStream in = IOUtils.toInputStream("bla bla bla ", StandardCharsets.UTF_8);
		JMXFile jmxFile = Mockito.mock(JMXFile.class);
		IPk2File pk2File = Mockito.mock(IPk2File.class);

		when(pk2File.findFirst("type.txt")).thenReturn(Optional.of(jmxFile));
		when(jmxFile.getInputStream()).thenReturn(in);

		assertThrows(Pk2InvalidResourceFormatException.class, () -> new MediaPk2(pk2File, () -> {
		}).extractType());

	}

	@Test
	void testExtractTypewithEmptyJmxFile() {

		InputStream in = InputStream.nullInputStream();
		JMXFile jmxFile = Mockito.mock(JMXFile.class);
		IPk2File pk2File = Mockito.mock(IPk2File.class);

		when(pk2File.findFirst("type.txt")).thenReturn(Optional.of(jmxFile));

		when(jmxFile.getInputStream()).thenReturn(in);
		assertThrows(Pk2InvalidResourceFormatException.class, () -> new MediaPk2(pk2File, () -> {
		}).extractType());

	}

	@Test
	void testExtractVersion() {

		byte[] encVer = Blowfish.newInstance("SILKROAD".getBytes())
								.encode(0, "123     ".getBytes());
		int len = encVer.length; // EndianUtils.swapInteger(encVer.length) ;
		byte[] res = new byte[encVer.length + 4];

		Conversion.intToByteArray(len, 0, res, 0, 4);
		System.arraycopy(encVer, 0, res, 4, encVer.length);

		InputStream in = new ByteArrayInputStream(res);
		JMXFile jmxFile = Mockito.mock(JMXFile.class);
		IPk2File pk2File = Mockito.mock(IPk2File.class);
		when(pk2File.findFirst("SV.T")).thenReturn(Optional.of(jmxFile));
		when(jmxFile.getInputStream()).thenReturn(in);

		assertEquals(123, new MediaPk2(pk2File, () -> {
		}).extractVersion());

	}

	@Test
	void testExtractVersionWhenMissedResource() {
		IPk2File pk2File = Mockito.mock(IPk2File.class);
		when(pk2File.findFirst("SV.T")).thenReturn(Optional.empty());

		assertThrows(Pk2MissedResourceException.class, () -> new MediaPk2(pk2File, () -> {
		}).extractVersion());
	}

	@Test
	void testExtractVersionWhenInvalidFormat() {

		byte[] encVer = Blowfish.newInstance("SILKROAD".getBytes())
								.encode(0, "abc".getBytes());
		int len = encVer.length; // EndianUtils.swapInteger(encVer.length) ;
		byte[] res = new byte[encVer.length + 4];

		Conversion.intToByteArray(len, 0, res, 0, 4);
		System.arraycopy(encVer, 0, res, 4, encVer.length);

		InputStream in = new ByteArrayInputStream(res);
		JMXFile jmxFile = Mockito.mock(JMXFile.class);
		IPk2File pk2File = Mockito.mock(IPk2File.class);
		when(pk2File.findFirst("SV.T")).thenReturn(Optional.of(jmxFile));
		when(jmxFile.getInputStream()).thenReturn(in);
		assertThrows(Pk2InvalidResourceFormatException.class, () -> new MediaPk2(pk2File, () -> {
		}).extractVersion());

	}

	@Test
	void testExtractVersionWhenEmptyJmxFile() {

		InputStream in = InputStream.nullInputStream();
		JMXFile jmxFile = Mockito.mock(JMXFile.class);
		IPk2File pk2File = Mockito.mock(IPk2File.class);
		when(pk2File.findFirst("SV.T")).thenReturn(Optional.of(jmxFile));
		when(jmxFile.getInputStream()).thenReturn(in);

		assertThrows(Pk2InvalidResourceFormatException.class, () -> new MediaPk2(pk2File, () -> {
		}).extractVersion());

	}

	@Test
	void testExtractPort() {

		InputStream in = IOUtils.toInputStream("15779", StandardCharsets.UTF_8);
		JMXFile portFile = Mockito.mock(JMXFile.class);
		IPk2File file = Mockito.mock(Pk2File.class);
		when(file.findFirst("(?i)gateport.txt")).thenReturn(Optional.of(portFile));
		when(portFile.getInputStream()).thenReturn(in);

		MediaPk2 media = new MediaPk2(file, null);
		int port = media.extractPort();
		assertEquals(15779, port);

	}

	@Test
	void testExtractPortWhenMissedResource() {
		IPk2File pk2File = Mockito.mock(IPk2File.class);
		when(pk2File.findFirst("(?i)gateport.txt")).thenReturn(Optional.empty());

		assertThrows(Pk2MissedResourceException.class, () -> new MediaPk2(pk2File, () -> {
		}).extractPort());
	}

	@Test
	void testExtractPortWhenInvalidResourceFormat() {
		InputStream in = IOUtils.toInputStream("NAN", StandardCharsets.UTF_8);
		JMXFile portFile = Mockito.mock(JMXFile.class);
		IPk2File file = Mockito.mock(Pk2File.class);
		when(file.findFirst("(?i)gateport.txt")).thenReturn(Optional.of(portFile));
		when(portFile.getInputStream()).thenReturn(in);
		MediaPk2 media = new MediaPk2(file, null);

		assertThrows(Pk2InvalidResourceFormatException.class, () -> media.extractPort());

	}

	@Test
	void testExtractPortWhenEmptyJMXFile() {
		InputStream in = InputStream.nullInputStream();

		JMXFile portFile = Mockito.mock(JMXFile.class);
		IPk2File file = Mockito.mock(Pk2File.class);
		when(file.findFirst("(?i)gateport.txt")).thenReturn(Optional.of(portFile));

		when(portFile.getInputStream()).thenReturn(in);
		MediaPk2 media = new MediaPk2(file, null);

		assertThrows(Pk2InvalidResourceFormatException.class, () -> media.extractPort());

	}

	@Test
	void testExtractDiviInfo() {

		byte[] arr = { 22, 1, 5, 0, 0, 0, 68, 73, 86, 48, 49, 0, 1, 14, 0, 0, 0, 55, 55, 46, 50, 50, 51, 46, 49, 53, 53,
				46, 50, 52, 53, 0 };
		InputStream in = new ByteArrayInputStream(arr);

		JMXFile divInfoFile = Mockito.mock(JMXFile.class);
		IPk2File file = Mockito.mock(IPk2File.class);
		when(file.findFirst("(?i)divisioninfo.txt")).thenReturn(Optional.of(divInfoFile));
		when(divInfoFile.getInputStream()).thenReturn(in);

		DivisionInfo info = new MediaPk2(file, null).extractDivisionInfo();
		assertEquals(22, info.local);
		List<Division> divs = info.getDivisions();
		assertEquals(1, divs.size());
		Division div = divs.get(0);
		assertEquals("DIV01", div.name);
		List<String> hosts = div.getHosts();
		assertEquals(1, hosts.size());
		String host = hosts.get(0);
		assertEquals("77.223.155.245", host);

	}

	@Test
	void testExtractDivInfoWhenMissed() {
		IPk2File file = Mockito.mock(IPk2File.class);
		when(file.findFirst("(?i)divisioninfo.txt")).thenReturn(Optional.empty());
		assertThrows(Pk2MissedResourceException.class, () -> new MediaPk2(file, null).extractDivisionInfo());
	}

	@Test
	void testExtractDivInfoWhenInvalidFormat() {

		byte[] arr = { 22, 25, 50, 10, 20, 30, 60, 53, 86, 48, 49, 0, 10, 14, 10, 30, 1, 55, 55, 46, 50, 50, 51, 46, 49,
				53, 53, 60, 60, 60, 60, 30 }; // invalid data

		InputStream in = new ByteArrayInputStream(arr);

		JMXFile divInfoFile = Mockito.mock(JMXFile.class);
		IPk2File file = Mockito.mock(IPk2File.class);
		when(file.findFirst("(?i)divisioninfo.txt")).thenReturn(Optional.of(divInfoFile));
		when(divInfoFile.getInputStream()).thenReturn(in);

		assertThrows(Pk2InvalidResourceFormatException.class, () -> new MediaPk2(file, null).extractDivisionInfo());

	}

	@Test
	void testExtractDivInfoWhenEmpty() {

		InputStream in = InputStream.nullInputStream();

		JMXFile divInfoFile = Mockito.mock(JMXFile.class);
		IPk2File file = Mockito.mock(IPk2File.class);
		when(file.findFirst("(?i)divisioninfo.txt")).thenReturn(Optional.of(divInfoFile));
		when(divInfoFile.getInputStream()).thenReturn(in);

		assertThrows(Pk2InvalidResourceFormatException.class, () -> new MediaPk2(file, null).extractDivisionInfo());

	}


}
