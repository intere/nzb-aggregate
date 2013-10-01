package com.intere.spring.nzb.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.intere.spring.nzb.model.dto.json.NzbPost;
import com.intere.spring.nzb.model.dto.json.Parameter;
import com.intere.spring.nzb.model.dto.json.Post;

/**
 * Model that contains information about the NZB Search, but also allows you to
 * populate for a post.
 * 
 */
@SuppressWarnings("serial")
@XmlType(name="NzbSearchFormModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class NzbSearchFormModel implements Serializable {
	
	private static final Logger LOG = Logger.getLogger(NzbSearchFormModel.class);


	private static final String PARAM_ACTION = "action";
	private static final String PARAM_ACTION_VALUE = "nzb";
	private static final String PARAM_POST_VALUE = "on";

	@XmlElement(name="search-criteria")
	protected String searchText;
	
	@XmlElement(name="search-url")
	protected String searchUrl;
	
	@XmlElement(name="action")
	protected String postAction;

	@XmlElementWrapper(name="nzbRows")
	@XmlElement(name="NzbRowModel")
	protected List<NzbRowModel> nzbRows = new ArrayList<NzbRowModel>();
	
	@XmlElementWrapper(name="posts")
	@XmlElement(name="id")
	protected List<Integer> posts = new ArrayList<Integer>();
	
	@XmlTransient
	protected List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
	
	@XmlTransient
	protected Map<Integer, NzbRowModel> rowMap = new Hashtable<Integer, NzbRowModel>();

	/**
	 * Default Constructor.
	 */
	public NzbSearchFormModel() {
		parameters.add(new BasicNameValuePair(PARAM_ACTION, PARAM_ACTION_VALUE));
	}

	/** Constructor that "deserializes" the JSON object to this object type. */
	public NzbSearchFormModel(NzbPost post) {
		setSearchText(post.getSearchText());
		setAction(post.getAction());
		for(Parameter p : post.getParameters()) {
			parameters.add(new BasicNameValuePair(p.getName(), p.getValue()));
		}
		
		for(Post p : post.getPosts()) {			
			posts.add(p.getId());
			parameters.add(new BasicNameValuePair(p.getCheckboxName(),PARAM_POST_VALUE));			
		}
	}

	/**
	 * This method will remove the specified post (by ID) from the list of posts.
	 * @param id
	 */
	public boolean removePostById(int id) {
		
		boolean removed = posts.remove(new Integer(id));
		
		NzbRowModel row = getRowById(id);
		if(row!=null)
		{
			BasicNameValuePair tbr = null;
			for(BasicNameValuePair pair : parameters)
			{
				if(pair.getName().equals(row.getCheckboxName()))
				{
					tbr = pair;
					break;
				}
			}
			if(tbr!=null)
			{
				parameters.remove(tbr);
			}
		}
		
		return removed;
	}
	
	/**
	 * This method will re-initialize the parameters from the posts list.
	 */
	public void reinitialize()
	{
		for(NzbRowModel model : nzbRows)
		{
			rowMap.put(model.getId(), model);
		}
		
		for(Integer id : posts)
		{
			parameters.add(new BasicNameValuePair(getRowById(id).getCheckboxName(),PARAM_POST_VALUE));
		}
	}

	/**
	 * This method will add the provided row model to the post.
	 * 
	 * @param rowModel
	 */
	public void addRowToPost(NzbRowModel rowModel) {
		if(!posts.contains(rowModel.getId()))
		{
			posts.add(rowModel.getId());
			parameters.add(new BasicNameValuePair(rowModel.getCheckboxName(),PARAM_POST_VALUE));
		}
	}

	/**
	 * This method does a lookup by ID (using the provided ID), gets the row, and adds it to the post.
	 * @param id
	 */
	public void addRowToPostById(int id) {
		NzbRowModel row = getRowById(id);
		if(row!=null)
		{
			addRowToPost(row);
		} else {
			LOG.info("ID: " + id + " was not found in this search form");
		}
	}

	/**
	 * This method will give you a row by its ID.
	 * @param id
	 * @return
	 */
	public NzbRowModel getRowById(Integer id) {
		return rowMap.get(id);
	}

	/**
	 * This method will add an NzbRow to the row list.
	 * @param rowModel
	 */
	public void addNzbRowModel(NzbRowModel rowModel) {
		nzbRows.add(rowModel);
		rowMap.put(rowModel.getId(), rowModel);
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public String getSearchUrl() {
		return searchUrl;
	}

	public void setSearchUrl(String searchUrl) {
		this.searchUrl = searchUrl;
	}

	public String getAction() {
		return postAction;
	}

	public void setAction(String action) {
		this.postAction = action;
	}

	public List<NzbRowModel> getNzbRows() {
		return nzbRows;
	}

	public List<BasicNameValuePair> getParameters() {
		return parameters;
	}

	public int getPostRowCount() {
		return posts.size();
	}
	public void setPosts(List<Integer> posts) {
		this.posts = posts;
	}
	public List<Integer> getPosts() {
		return posts;
	}

}
