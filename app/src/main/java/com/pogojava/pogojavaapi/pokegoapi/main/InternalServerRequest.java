package com.pogojava.pogojavaapi.pokegoapi.main;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;

import POGOProtos.Networking.Requests.RequestOuterClass;
import POGOProtos.Networking.Requests.RequestTypeOuterClass;

/**
 * Created by iGio90 on 01/09/16.
 */

public class InternalServerRequest {

	RequestOuterClass.Request request;
	private RequestTypeOuterClass.RequestType type;
	private ByteString data;

	/**
	 * Instantiates a new Server request.
	 *
	 * @param type the type
	 * @param req  the req
	 */
	protected InternalServerRequest(RequestTypeOuterClass.RequestType type, GeneratedMessage req) {
		RequestOuterClass.Request.Builder reqBuilder = RequestOuterClass.Request.newBuilder();
		reqBuilder.setRequestMessage(req.toByteString());
		reqBuilder.setRequestType(type);
		this.request = reqBuilder.build();
		this.type = type;
	}

	/**
	 * Instantiates a new Server request.
	 *
	 * @param type    the type
	 * @param request the req
	 */
	InternalServerRequest(RequestTypeOuterClass.RequestType type, RequestOuterClass.Request request) {
		this.request = request;
		this.type = type;
	}

	/**
	 * Handle data.
	 *
	 * @param bytes the bytes
	 */
	public void handleData(ByteString bytes) {
		this.data = bytes;
	}

	/**
	 * Gets data.
	 *
	 * @return the data
	 * @throws InvalidProtocolBufferException the invalid protocol buffer exception
	 */
	public ByteString getData() throws InvalidProtocolBufferException {
		if (data == null) {
			throw new InvalidProtocolBufferException("Contents of buffer are null");
		}
		return data;
	}

	public RequestOuterClass.Request getRequest() {
		return request;
	}

	public RequestTypeOuterClass.RequestType getType() {
		return type;
	}
}
