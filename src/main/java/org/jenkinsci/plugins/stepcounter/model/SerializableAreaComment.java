package org.jenkinsci.plugins.stepcounter.model;

import java.io.Serializable;

import jp.sf.amateras.stepcounter.AreaComment;

public class SerializableAreaComment extends AreaComment implements Serializable{

	private static final long serialVersionUID = 5166286079088850813L;

	public SerializableAreaComment(String start, String end) {
		super(start,end);
	}

}
