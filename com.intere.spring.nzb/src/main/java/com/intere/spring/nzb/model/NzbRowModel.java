package com.intere.spring.nzb.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author <a href="mailto:intere@gmail.com">Eric Internicola</a>
 */
@SuppressWarnings("serial")
@XmlType(name = "NzbRowModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class NzbRowModel implements Serializable, Comparable<NzbRowModel> {
	private static final String COLLECTION = "collection";
	private static final String SIZE = "size:";
	private static final String REQUIRES_PASSWORD = "requires password";

	@XmlAttribute(name = "id")
	private Integer id;

	@XmlAttribute(name = "name")
	private String checkboxName;

	@XmlElement(name = "subject")
	private String subject;

	@XmlElement(name = "poster")
	private String poster;

	@XmlAttribute(name = "group")
	private String group;

	@XmlElement(name = "collection-url")
	private String collectionUrl;

	@XmlElement(name = "nzb-size")
	private NzbSize size;

	@XmlAttribute(name = "total-parts")
	private Long partsTotal;

	@XmlAttribute(name = "parts-of")
	private Long partsOf;

	@XmlElement(name = "contains")
	private String contains;

	@XmlAttribute(name = "collection")
	private Boolean collection = false;

	@XmlAttribute(name = "password")
	private Boolean requiresPassword = false;

	/**
	 * Default Constructor.
	 */
	public NzbRowModel() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param checkboxName
	 * @param subject
	 * @param poster
	 * @param group
	 */
	public NzbRowModel(Integer id, String checkboxName, String subject, String poster, String group) {
		super();
		this.id = id;
		this.checkboxName = checkboxName;
		this.subject = subject;
		this.poster = poster;
		this.group = group;
	}

	@Override
	public int compareTo(NzbRowModel row) {
		return id.compareTo(row.id);
	}

	/**
	 * This method parses the provided
	 * 
	 * @param row
	 * @return
	 */
	public static NzbRowModel fromNzbRowNode(Node row) {
		if (row.getChildNodes().item(1) == null || row.getTextContent().trim().startsWith("The posts below were posted a long time ago.")) {
			return null;
		}

		// Cache the Nodes from the DOM:
		Node tdId = row.getChildNodes().item(0).getChildNodes().item(0);
		Node tdCheckbox = row.getChildNodes().item(1).getChildNodes().item(0);
		Node tdSubject = row.getChildNodes().item(2);
		Node tdPoster = row.getChildNodes().item(3);
		Node tdGroup = row.getChildNodes().item(4);

		NzbRowModel model = new NzbRowModel();

		String cleaned = tdId.getTextContent().replaceAll("\\.", "").trim();

		model.setId(new Integer(cleaned));

		if (tdCheckbox.getAttributes() == null) {
			return null;
		}

		model.setCheckboxName(tdCheckbox.getAttributes().getNamedItem("name").getNodeValue());
		model.setPoster(tdPoster.getTextContent());
		model.setGroup(tdGroup.getTextContent());

		if (tdSubject.getChildNodes().getLength() == 1) {
			model.setSubject(tdSubject.getTextContent());
		} else {
			model.setSubject(tdSubject.getChildNodes().item(0).getTextContent());
			parseMetadata(model, tdSubject.getChildNodes().item(1).getChildNodes());
		}

		return model;
	}

	/**
	 * This method is responsible for parsing the secondary subject data.
	 * 
	 * @param childNodes
	 */
	private static void parseMetadata(NzbRowModel model, NodeList childNodes) {

		// TODO - comment the hell out of this method, and figure out why we're
		// only getting part
		// of the content list (it appears to maybe be only the last line -
		// maybe we're clobbering
		// the content of it?)

		for (int i = 0; i < childNodes.getLength(); i++) {
			Node current = childNodes.item(i);
			String nodeText = current.getTextContent().trim();

			if (nodeText.equals("")) {
				continue;
			} else if (COLLECTION.equalsIgnoreCase(nodeText)) {
				model.collection = true;
				model.collectionUrl = current.getAttributes().getNamedItem("href").getNodeValue();
			} else if (REQUIRES_PASSWORD.equalsIgnoreCase(nodeText)) {
				model.requiresPassword = true;
			} else if (nodeText.startsWith(SIZE)) {
				String[] parts = current.getTextContent().split(",");
				String rawSize = parts[0].replaceAll(SIZE, "").trim();
				model.size = new NzbSize(rawSize.replaceAll(".*[0-9].", "").trim(), Double.valueOf(rawSize.replaceAll(".[A-Z]+", "").trim()));

				String partsRight = parts[1].split(":")[1].trim();

				if (!"".equals(partsRight)) {
					try {
						model.partsOf = Long.parseLong(partsRight.split("/")[0].trim());
						model.partsTotal = Long.parseLong(partsRight.split("/")[1].replace("[", "").trim());
					} catch (NumberFormatException ex) {
						System.err.println("Couldn't parse numbers from: " + partsRight);
					}
				}
			} else {
				if (nodeText.equalsIgnoreCase("view NFO")) {
					continue;
				}
				model.contains = nodeText;
			}
		}
	}

	@Override
	public String toString() {
		return "NzbRowModel [id=" + id + ", checkboxName=" + checkboxName + ", subject=" + subject + ", poster=" + poster + ", group=" + group + ", collectionUrl=" + collectionUrl
				+ ", size=" + size + ", partsTotal=" + partsTotal + ", partsOf=" + partsOf + ", contains=" + contains + ", collection=" + collection + ", requiresPassword="
				+ requiresPassword + "]";
	}

	/**
	 * This will tell you if the row has "parts info" or not.
	 * 
	 * @return
	 */
	public boolean hasPartsInfo() {
		return getPartsOf() != null && getPartsTotal() != null;
	}

	/**
	 * If this row has "parts info" available, this method will tell you if the
	 * archive has all of the parts or not.
	 * 
	 * @return
	 */
	public boolean hasAllParts() {
		if (hasPartsInfo()) {
			return getPartsOf().longValue() == getPartsTotal().longValue();
		}

		return false;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	/**
	 * @return the checkboxName
	 */
	public String getCheckboxName() {
		return checkboxName;
	}

	/**
	 * @param checkboxName
	 *            the checkboxName to set
	 */
	public void setCheckboxName(String checkboxName) {
		this.checkboxName = checkboxName;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the poster
	 */
	public String getPoster() {
		return poster;
	}

	/**
	 * @param poster
	 *            the poster to set
	 */
	public void setPoster(String poster) {
		this.poster = poster;
	}

	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group
	 *            the group to set
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	public Boolean getCollection() {
		return collection;
	}

	public String getCollectionUrl() {
		return collectionUrl;
	}

	public String getContains() {
		return contains;
	}

	public Long getPartsTotal() {
		return partsTotal;
	}

	public Long getPartsOf() {
		return partsOf;
	}

	public NzbSize getSize() {
		return size;
	}

	public Boolean getRequiresPassword() {
		return requiresPassword;
	}

	/**
	 * 
	 * @return
	 */
	public String getShortString() {
		return getId() + " - " + getSubject() + (getSize() != null ? " [" + getSize() + "]" : "") + ", " + getPoster() + ", " + getGroup();
	}
}
