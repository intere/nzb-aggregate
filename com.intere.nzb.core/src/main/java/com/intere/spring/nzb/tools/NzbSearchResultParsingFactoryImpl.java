package com.intere.spring.nzb.tools;

import javax.xml.xpath.XPathExpressionException;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.intere.spring.nzb.builder.StreamUtils;
import com.intere.spring.nzb.model.NzbRowModel;
import com.intere.spring.nzb.model.NzbSearchFormModel;

/**
 * Implementation of the {@link NzbSearchResultParsingFactory}.
 * 
 * @author Eric Internicola (intere@gmail.com)
 */
@Component("nzbParsingFactory")
public class NzbSearchResultParsingFactoryImpl implements
		NzbSearchResultParsingFactory {
	
	public NzbSearchResultParsingFactoryImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public NzbSearchFormModel parseSearchResults(Document doc) throws XPathExpressionException {
		return parseSearchResults(doc, false);
	}

	@Override
	public NzbSearchFormModel parseSearchResults(Document doc,
			boolean collectionsOnly) throws XPathExpressionException {
		return parseSearchResults(doc, collectionsOnly, false);
	}

	@Override
	public NzbSearchFormModel parseSearchResults(Document doc,
			boolean collectionsOnly, boolean excludePasswordProtected) throws XPathExpressionException {

		NzbSearchFormModel model = new NzbSearchFormModel();
		
		NodeList actionList = StreamUtils.getByXpath(doc, XPath.ActionNode.getPath());
		
		if(actionList!=null&&actionList.getLength()==1)
		{
//			model.setSearchUrl(actionList.item(0).getTextContent());
			model.setAction(actionList.item(0).getTextContent());
		}
		
		// Get the node for each row:
		NodeList rows = StreamUtils.getByXpath(doc, XPath.RowNodes.getPath());
		
		// ensure we have some rows, and iterate over them all (skip the first row - it's the "title" row)
		if(rows!=null&&rows.getLength()>1)
		{
			for(int i=1; i<rows.getLength(); i++)
			{
				NzbRowModel rowModel = NzbRowModel.fromNzbRowNode(rows.item(i));
				
				if(rowModel!=null)
				{
					// If it is a collection, or we're not excluding collections:
					if(!collectionsOnly || rowModel.getCollection())
					{
						// If it is not password protected, or we're not excluding password protected collections:
						if(!excludePasswordProtected || !rowModel.getRequiresPassword())
						{
							model.addNzbRowModel(rowModel);
						}
					}
				}
			}			
		}
		

		return model;
	}

	
	/**
	 * The XPath to the various xpath locations.
	 * 
	 */
	public enum XPath
	{
		ActionNode("//form[2]//@action"),
		RowNodes("//form[2]/p/table/tbody/tr");
		
		private String path;
		
		private XPath(String path) {
			this.path = path;
		}
		
		/**
		 * Getter for the Path.
		 * 
		 * @return
		 */
		public String getPath() {
			return path;
		}		
	}
}
