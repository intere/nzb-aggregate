package com.intere.spring.nzb.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.intere.spring.nzb.model.NzbSize.Unit;

/**
 * This class is used to compare the NzbSize comparisons.
 * 
 * @author einternicola
 */
public class NzbSizeTest {

	private static final NzbSize[] COMPARE_SIZES = {
			new NzbSize(NzbSize.Unit.B, 10.0),
			new NzbSize(NzbSize.Unit.B, 1024.0),
			new NzbSize(NzbSize.Unit.KB, 1.5),
			new NzbSize(NzbSize.Unit.KB, 1023.0),
			new NzbSize(NzbSize.Unit.MB, 1.0),
			new NzbSize(NzbSize.Unit.MB, 1023.0),
			new NzbSize(NzbSize.Unit.GB, 1.0),
			new NzbSize(NzbSize.Unit.GB, 1.1) };

	private static final NzbSize[] SIZE_1 = { new NzbSize(NzbSize.Unit.B, 1.0),
			new NzbSize(NzbSize.Unit.KB, 1.0),
			new NzbSize(NzbSize.Unit.MB, 1.0),
			new NzbSize(NzbSize.Unit.GB, 1.0) };
	
	private static final NzbSize[][] SIZE_EQUALITY = {
		{ new NzbSize(Unit.B, 1024.0), new NzbSize(Unit.KB, 1.0)},
		{ new NzbSize(Unit.B, 1024.0*1024.0), new NzbSize(Unit.KB, 1024.0), new NzbSize(Unit.MB, 1.0)},
		{ new NzbSize(Unit.B, 1024.0*1024.0*1024.0), new NzbSize(Unit.KB, 1024.0*1024.0), new NzbSize(Unit.MB, 1024.0), new NzbSize(Unit.GB, 1.0)}
	};

	/**
	 * Verify that the size in bytes of each of the orders (Bytes, Kilobytes,
	 * Megabytes, Gigabytes) is correct.
	 */
	@Test
	public void testSize1() {
		long bytes = 1;

		for (NzbSize size : SIZE_1) {
			assertEquals("Size is off for NzbSize: " + size, bytes, size
					.getSizeInBytes().longValue());
			bytes *= 1024L;
		}
	}
	
	/**
	 * Verify that the comparisons are correct.
	 */
	@Test
	public void testComparisons() {
		
		NzbSize last = null;
		
		for(NzbSize comparison : COMPARE_SIZES)
		{
			if(last!=null)
			{
				assertTrue("The comparison is incorrect", comparison.compareTo(last)>0);
			}
			
			last = comparison;
		}
	}
	
	/**
	 * Compare various equalities of various unit sizes.
	 */
	@Test
	public void compareEquality() 
	{
		for(NzbSize[] equals : SIZE_EQUALITY)
		{
			NzbSize last = null;
			for(NzbSize comparison : equals)
			{
				if(last != null)
				{
					assertEquals("The comparison should be equal", 0L, comparison.compareTo(last));
				}
				last = comparison;
			}
		}
	}

}
