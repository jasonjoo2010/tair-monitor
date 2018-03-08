package com.tair_2_3.statmonitor.comm;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.buc.sso.client.util.SimpleUserUtil;
import com.alibaba.buc.sso.client.vo.BucSSOUser;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.domain.AclCheckPermissionResult;
import com.taobao.api.request.AlibabaAclPermissionsCheckRequest;
import com.taobao.api.response.AlibabaAclPermissionsCheckResponse;

public class MonitorAcl {
	private String topUrl = "";
	private String topAppKey = "";
	private String topSecret = "";
	private TaobaoClient client = null;
	private String aclPermissionName = "";
	private String aclFailedJumpUrl = "";
	private Boolean needAclCheck = true;
	
	private static final Log log = LogFactory.getLog(MonitorAcl.class);

	public MonitorAcl() {

		InputStream RA;
		try {
			RA = Class
					.forName("com.tair_2_3.statmonitor.MonitorOutputRetrieve")
					.getResourceAsStream("MonitorArgs");
			Properties config = new Properties();
			config.load(RA);

			if (null != config.getProperty("needAclCheck")) {
				needAclCheck = Boolean.valueOf(config.getProperty("needAclCheck"));
			}

			if (needAclCheck) {
				topUrl = config.getProperty("topUrl");
				topAppKey = config.getProperty("topAppKey");
				topSecret = config.getProperty("topSecret");
				aclPermissionName = config.getProperty("aclPermissionName");
				aclFailedJumpUrl = config.getProperty("aclFailedJumpUrl")
						+ aclPermissionName;
				client = new DefaultTaobaoClient(topUrl, topAppKey, topSecret);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean CheckAccessStatus(HttpServletRequest request) throws IOException, ServletException {
		boolean success = true;
		BucSSOUser bucSSOUser = SimpleUserUtil.getBucSSOUser(request);
		AlibabaAclPermissionsCheckRequest req = new AlibabaAclPermissionsCheckRequest();
		req.setPermissionNames(aclPermissionName);
		req.setUserId(bucSSOUser.getId().longValue());
		AlibabaAclPermissionsCheckResponse resp;
		try {
			resp = client.execute(req);
			List<AclCheckPermissionResult> results = resp.getResults();
			for (int i = 0; i < results.size(); i++) {
				AclCheckPermissionResult aclRslt = results.get(i);
				if (aclRslt.getPermissionName().equals(aclPermissionName)) {
					if (!aclRslt.getAccessible()) {
						success = false;
					} else {
						success = true;
					}
					log.info(bucSSOUser.getEmailAddr() + " got accessible  of "
							+ aclPermissionName + " : "
							+ aclRslt.getAccessible());
				}
			}
		} catch (ApiException e) {
			log.error(bucSSOUser.getEmailAddr()
					+ " err trying to get acl info : ", e);
			success = false;
		}
		return success;
	}

	public String getAclFailedJumpUrl() {
		return aclFailedJumpUrl;
	}

	public void setAclFailedJumpUrl(String aclFailedJumpUrl) {
		this.aclFailedJumpUrl = aclFailedJumpUrl;
	}

	public Boolean getNeedAclCheck() {
		return needAclCheck;
	}

	public void setNeedAclCheck(Boolean needAclCheck) {
		this.needAclCheck = needAclCheck;
	}

}
