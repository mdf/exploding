/**
 * 
 */
package uk.ac.horizon.ug.exploding.clientapi;

/**
 * @author cmg
 *
 */
public class Message {
	/** sequence number */
	protected int seqNo;
	/** type */
	protected MessageType type;
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
	protected MessageStatusType status;
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
	/**
	 * @return the type
	 */
	public MessageType getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(MessageType type) {
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
	/**
	 * @return the status
	 */
	public MessageStatusType getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(MessageStatusType status) {
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
	
}
