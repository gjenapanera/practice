/**
 * 
 */
package com.mgm.dmp.web.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author ssahu6
 *
 */
@JsonInclude(Include.NON_NULL)
public class GenericDmpResponse implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 8689845723780412398L;
	
	private Object request;
	private String successMessage;
	private Object response;
	private Collection<?> responses;
	private List<Message> messages;
	
	/**
     * @return the messages
     */
	public List<Message> getMessages() {
        return messages;
    }
	/**
     * @param messages the messages to set
     */
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
	/**
     * @param message the message to add to messages list
     */
    public void addMessage(Message message) {
        if(this.messages == null){
        	this.messages = new ArrayList<Message>();
        }
    	this.messages.add(message);
    }
    /**
	 * @return the request
	 */
	public Object getRequest() {
		return request;
	}
	/**
	 * @param request the request to set
	 */
	public void setRequest(Object request) {
		this.request = request;
	}	
	/**
	 * @return the successMessage
	 */
	public String getSuccessMessage() {
		return successMessage;
	}
	/**
	 * @return the response
	 */
	public Object getResponse() {
		return response;
	}
	/**
	 * @return the responses
	 */
	public Collection<?> getResponses() {
		return responses;
	}
	/**
	 * @param successMessage the successMessage to set
	 */
	@Deprecated
	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}
	/**
	 * @param response the response to set
	 */
	public void setResponse(Object response) {
		this.response = response;
	}
	/**
	 * @param responses the responses to set
	 */
	public void setResponses(Collection<?> responses) {
		this.responses = responses;
	}
	
}
