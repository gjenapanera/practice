package com.mgm.dmp.dao.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.mgm.dmp.common.model.phoenix.Program;
import com.mgm.dmp.common.util.CommonUtil;

@Component
public class PhoenixProgramByIdDAOImpl extends AbstractPhoenixBaseDAO<String, Program> {

	@Value("${phoenix.program.by.id}")
	private String phoenixProgramByID;

	@Override
	protected HttpMethod getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	protected Class<String> getRequestClass() {
		return String.class;
	}

	@Override
	protected Class<Program> getResponseClass() {
		return Program.class;
	}

	@Override
	protected String getUrl(final String programId) {
		return CommonUtil.getComposedUrl(phoenixProgramByID, phoenixBaseURL, programId);
	}

}
