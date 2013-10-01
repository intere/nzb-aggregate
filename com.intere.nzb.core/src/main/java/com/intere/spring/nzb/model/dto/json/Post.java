package com.intere.spring.nzb.model.dto.json;

public class Post {
	private String checkboxName;
	private Integer id;
	/**
	 * @return the checkboxName
	 */
	public String getCheckboxName() {
		return checkboxName;
	}
	/**
	 * @param checkboxName the checkboxName to set
	 */
	public void setCheckboxName(String checkboxName) {
		this.checkboxName = checkboxName;
	}
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((checkboxName == null) ? 0 : checkboxName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Post))
			return false;
		Post other = (Post) obj;
		if (checkboxName == null) {
			if (other.checkboxName != null)
				return false;
		} else if (!checkboxName.equals(other.checkboxName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
