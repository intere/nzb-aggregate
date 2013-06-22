package com.intere.spring.nzb.tools;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.intere.spring.nzb.model.NzbRowModel;
import com.intere.spring.nzb.model.NzbSearchFormModel;
import com.intere.spring.nzb.tools.NzbModelFilterer;

/**
 * Test of the filtering of the Nzb Models.
 * 
 * @author <a href="mailto:intere@gmail.com">Eric Internicola</a>
 */
public class NzbModelFiltererTest {
    
    private static final NzbRowModel COLL_PW_ROW = new MockNzbRowModel(1, true, true);
    private static final NzbRowModel COLL_NO_PW_ROW = new MockNzbRowModel(2, true, false);
    private static final NzbRowModel NO_COLL_NO_PW_ROW = new MockNzbRowModel(3, false, false);
    private static final NzbRowModel NO_COL_PW_ROW = new MockNzbRowModel(4, false, true);
    private static final NzbSearchFormModel MODEL = new NzbSearchFormModel();
    
    static {
        MODEL.addNzbRowModel(COLL_PW_ROW);
        MODEL.addNzbRowModel(COLL_NO_PW_ROW);
        MODEL.addNzbRowModel(NO_COLL_NO_PW_ROW);
        MODEL.addNzbRowModel(NO_COL_PW_ROW);
    }
    

    @Test
    public void testFilterModel() {
        
        NzbSearchFormModel unfiltered = NzbModelFilterer.filterModel(false, false, MODEL);
        assertEquals("Unfiltered passthrough failed", 4, unfiltered.getNzbRows().size());
        
        NzbSearchFormModel colOnly = NzbModelFilterer.filterModel(true, false, MODEL);
        assertEquals("Collections only failed", 2, colOnly.getNzbRows().size());
        
        NzbSearchFormModel noPw = NzbModelFilterer.filterModel(false, true, MODEL);
        assertEquals("Password exclusion failed", 2, noPw.getNzbRows().size());
        
        NzbSearchFormModel fullFilter = NzbModelFilterer.filterModel(true, true, MODEL);
        assertEquals("Full Filtering failed", 1, fullFilter.getNzbRows().size());
        
    }
    
    @SuppressWarnings("serial")
	private static class MockNzbRowModel extends NzbRowModel
    {        
        private boolean collection;
        private boolean hasPassword;

        public MockNzbRowModel(int id, boolean collection, boolean hasPassword) {
        	this.setId(id);
            this.collection = collection;
            this.hasPassword = hasPassword;            
        }        
        
        @Override
        public Boolean getCollection() {
            return collection;
        }
        
        @Override
        public Boolean getRequiresPassword() {
            return hasPassword;
        }
    }

}
