/**
 * Copyright 2010 The University of Nottingham
 * 
 * This file is part of GenericAndroidClient.
 *
 *  GenericAndroidClient is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  GenericAndroidClient is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with GenericAndroidClient.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package uk.ac.horizon.ug.exploding.client;

import java.util.Arrays;

/**
 * @author cmg
 *
 */
public class Message {
	/** sequence number */
	protected int seqNo;
	/** type */
	protected String /*MessageType*/ type;
	/** time */
	protected Long time;
	/** subscription index - for FACT_ADD/UPD/DEL & SUBS_EN/DIS */
	protected Integer subsIx;
	/** old value - for FACT_UPD/DEL & UPD/DEL_FACT */
	protected Object oldVal;
	/** new value - for FACT_ADD/UPD & ADD/UPD_FACT */
	protected Object newVal;
	/** handle - FACT_UPD/DEL, UPD/DEL_FACT (option vs oldVal), ADD/UPD_FACT ACK */
	protected String handle;
	/** ack seq - for ACK, ERROR, POLL and POLL_RESP */
	protected Integer ackSeq;
	/** new-style multi-ack */
	protected int ackSeqs[];
	/** messages to follow - for POLL/POLL_RESP */
	protected Integer toFollow;
	/** priority of required messages (POLL) or of message (ADD/UPD/DEL_FACT) */
	protected Integer priority;
	/** error code - for ERROR / ACK */
	protected String /*MessageStatusType*/ status;
	/** error message - for ERROR */
	protected String errorMsg;
	/** cons */
	public Message() {		
	}
	/**
	 * @return the seqNo
	 */
	public int getSeqNo() {
		return seqNo;
	}
	/**
	 * @param seqNo the seqNo to set
	 */
	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}
//	/**
//	 * @return the type
//	 */
//	public MessageType getType() {
//		return type;
//	}
//	/**
//	 * @param type the type to set
//	 */
//	public void setType(MessageType type) {
//		this.type = type;
//	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the time
	 */
	public Long getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(Long time) {
		this.time = time;
	}
	/**
	 * @return the subsIx
	 */
	public Integer getSubsIx() {
		return subsIx;
	}
	/**
	 * @param subsIx the subsIx to set
	 */
	public void setSubsIx(Integer subsIx) {
		this.subsIx = subsIx;
	}
	/**
	 * @return the oldVal
	 */
	public Object getOldVal() {
		return oldVal;
	}
	/**
	 * @param oldVal the oldVal to set
	 */
	public void setOldVal(Object oldVal) {
		this.oldVal = oldVal;
	}
	/**
	 * @return the newVal
	 */
	public Object getNewVal() {
		return newVal;
	}
	/**
	 * @param newVal the newVal to set
	 */
	public void setNewVal(Object newVal) {
		this.newVal = newVal;
	}
	/**
	 * @return the handle
	 */
	public String getHandle() {
		return handle;
	}
	/**
	 * @param handle the handle to set
	 */
	public void setHandle(String handle) {
		this.handle = handle;
	}
	/**
	 * @return the ackSeq
	 */
	public Integer getAckSeq() {
		return ackSeq;
	}
	/**
	 * @param ackSeq the ackSeq to set
	 */
	public void setAckSeq(Integer ackSeq) {
		this.ackSeq = ackSeq;
	}
	/**
	 * @return the ackSeqs
	 */
	public int[] getAckSeqs() {
		return ackSeqs;
	}
	/**
	 * @param ackSeqs the ackSeqs to set
	 */
	public void setAckSeqs(int[] ackSeqs) {
		this.ackSeqs = ackSeqs;
	}
	/**
	 * @return the toFollow
	 */
	public Integer getToFollow() {
		return toFollow;
	}
	/**
	 * @param toFollow the toFollow to set
	 */
	public void setToFollow(Integer toFollow) {
		this.toFollow = toFollow;
	}
	/**
	 * @return the priority
	 */
	public Integer getPriority() {
		return priority;
	}
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
//	/**
//	 * @return the status
//	 */
//	public MessageStatusType getStatus() {
//		return status;
//	}
//	/**
//	 * @param status the status to set
//	 */
//	public void setStatus(MessageStatusType status) {
//		this.status = status;
//	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}
	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	@Override
	public String toString() {
		return "Message [ackSeq=" + ackSeq + ", ackSeqs="
				+ Arrays.toString(ackSeqs) + ", errorMsg=" + errorMsg
				+ ", handle=" + handle + ", newVal=" + newVal + ", oldVal="
				+ oldVal + ", priority=" + priority + ", seqNo=" + seqNo
				+ ", status=" + status + ", subsIx=" + subsIx + ", time="
				+ time + ", toFollow=" + toFollow + ", type=" + type + "]";
	}
	
}
