package com.mgm.dmp.common.model;

import java.io.Serializable;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class SearchPageTile implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5546978244904659711L;
    private String url;
    private String category;
    private String image;
    private String desc;
    private String title;
    
    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }
    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }
    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }
    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        String tempCategory = category;
        if(StringUtils.isNotEmpty(category)){
            tempCategory = StringEscapeUtils.unescapeHtml(category);
        }
        this.category = tempCategory;
    }
    /**
     * @return the image
     */
    public String getImage() {
        return image;
    }
    /**
     * @param image the image to set
     */
    public void setImage(String image) {
        this.image = image;
    }
    /**
     * @return the description
     */
    public String getDesc() {
        return desc;
    }
    /**
     * @param description the description to set
     */
    public void setDesc(String description) {
        String tempDesc = description;
        if(StringUtils.isNotEmpty(description)){
            tempDesc = StringEscapeUtils.unescapeHtml(description);
        }
        this.desc = tempDesc;
    }
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        String tempTitle = title;
        if(StringUtils.isNotEmpty(title)){
            tempTitle = StringEscapeUtils.unescapeHtml(title);
        }
        this.title = tempTitle;
    }
}
