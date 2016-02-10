/**
 * 
 */
package com.mgm.dmp.common.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author ssahu6
 *
 */
@JsonInclude(Include.NON_NULL)
public class Image implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6355734804074043298L;
	private String src = "filename.jpg";
	private String domain;
	private ImageSrc grid;
	private ImageSrc list;
	private String attr;
	/**
	 * @return the src
	 */
	public String getSrc() {
		return src;
	}
	/**
	 * @param src the src to set
	 */
	public void setSrc(String src) {
		this.src = src;
	}
	/**
	 * @return the grid
	 */
	public ImageSrc getGrid() {
		return grid;
	}
	/**
	 * @param grid the grid to set
	 */
	public void setGrid(ImageSrc grid) {
		this.grid = grid;
	}
	/**
	 * @return the list
	 */
	public ImageSrc getList() {
		return list;
	}
	/**
	 * @param list the list to set
	 */
	public void setList(ImageSrc list) {
		this.list = list;
	}
	/**
	 * @return the attr
	 */
	public String getAttr() {
		return attr;
	}
	/**
	 * @param attr the attr to set
	 */
	public void setAttr(String attr) {
		this.attr = attr;
	}
    /**
     * @return the domain
     */
    public String getDomain() {
        return domain;
    }
    /**
     * @param domain the domain to set
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

}
