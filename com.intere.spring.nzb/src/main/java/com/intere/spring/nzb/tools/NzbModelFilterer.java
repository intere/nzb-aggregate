package com.intere.spring.nzb.tools;

import com.intere.spring.nzb.model.NzbRowModel;
import com.intere.spring.nzb.model.NzbSearchFormModel;

/**
 * Nzb Model Filterer.  This class will do filtering for you.
 * 
 */
public class NzbModelFilterer {
    
    /**
     * This method is responsible for performing filtering for you.
     * @param collectionsOnly
     * @param excludePasswords
     * @param model
     * @return
     */
    public static NzbSearchFormModel filterModel(boolean collectionsOnly, boolean excludePasswords, NzbSearchFormModel model)
    {
        NzbSearchFormModel filtered = new NzbSearchFormModel();
        filtered.setAction(model.getAction());
        
        for(int i=0; i<model.getNzbRows().size(); i++) {
            
            NzbRowModel row = model.getNzbRows().get(i);
            
            boolean keep = true;
            if(collectionsOnly) {
                keep = row.getCollection();
            }
            if(excludePasswords) {
                keep = keep && !row.getRequiresPassword();
            }
            
            if(keep) {
                filtered.addNzbRowModel(row);
            }
        } 
        
        return filtered;
    }
}
