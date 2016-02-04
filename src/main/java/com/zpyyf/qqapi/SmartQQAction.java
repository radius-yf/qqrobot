package com.zpyyf.qqapi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zpyyf.utils.HttpUtils;
import com.zpyyf.utils.ImageUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class SmartQQAction {

	private static final Logger log = Logger.getLogger(SmartQQAction.class);
	private static final long Client_ID = 53999199;
	private static final HttpUtils client = new HttpUtils();

	private String ptwebqq;
	private String vfwebqq;
	private String psessionid;
	private long uin;

	private Consumer<InputStream> imageConsumer = new Consumer<InputStream>() {
		@Override
		public void accept(InputStream inputStream) {
			String filePath = getClass().getResource("/").getPath().concat("qrcode.png");
			ImageUtils.saveImage(inputStream, filePath);
		}
	};


	public SmartQQAction() {
		login();
	}

	/**
	 * 登陆
	 */
	public void login() {
		getQRCode();
		String url = verifyQRCode();
		getPtwebqq(url);
		getVfwebqq();
		getUinAndPsessionid();
	}

	/**
	 * 拉取消息
	 */
	public String getMessage() {
		log.info("开始接受消息");
		JSONObject r = new JSONObject();
		r.put("ptwebqq", ptwebqq);
		r.put("clientid", Client_ID);
		r.put("psessionid", psessionid);
		r.put("key", "");

		ApiURL url = ApiURL.POLL_MESSAGE;

		HttpResponse response = client.post(url.getUrl(),
				"r=" + r.toJSONString(),
				new BasicHeader("Referer", url.getReferer()));

		return HttpUtils.getString(response);
	}

	/**
	 * 获取群列表
	 *
	 * @return 群列表
	 */
	public String getGroupList() {
		log.info("开始获取群列表");
		JSONObject r = new JSONObject();
		r.put("vfwebqq", vfwebqq);
		r.put("hash", hash());

		ApiURL url = ApiURL.GET_GROUP_LIST;

		//发送请求的客户端
		HttpResponse response = client.post(url.getUrl(), "r=" + r.toJSONString(), new BasicHeader("Referer", url.getReferer()));
		return HttpUtils.getString(response);
	}

	/**
	 * 获取讨论组列表
	 *
	 * @return 讨论组列表
	 */
	public String getDiscussList() {
		log.info("开始获取讨论组列表");
		ApiURL url = ApiURL.GET_DISCUSS_LIST;
		HttpResponse response = client.get(url.buildUrl(psessionid, vfwebqq), new BasicHeader("Referer", url.getReferer()));
		return HttpUtils.getString(response);
	}


	/**
	 * 获取二维码，登录第一步
	 */
	private void getQRCode() {
		log.info("开始获取二维码");

		ApiURL url = ApiURL.GET_QE_CODE;
		HttpResponse response = client.get(url.getUrl());
		InputStream inputStream = HttpUtils.getInputStream(response);
		imageConsumer.accept(inputStream);

	}

	/**
	 * 等待用户扫描二维码，登陆第二步
	 *
	 * @return result
	 */
	private String verifyQRCode() {
		log.info("等待扫描二维码");
		ApiURL url = ApiURL.VERIFY_QR_CODE;
		HttpResponse response = client.get(url.getUrl(), new BasicHeader("Referer", url.getReferer()));

		return HttpUtils.getString(response);
	}

	/**
	 * 获取ptwebqq，登陆第三步
	 *
	 * @param param 第二步获取的参数
	 * @return result
	 */
	private String getPtwebqq(String param) {
		log.info("开始获取ptwebqq");
		//发送请求的客户端
		ApiURL url = ApiURL.GET_PTWEBQQ;
		HttpResponse response = client.get(url.buildUrl(param));
		for (Header header : response.getHeaders("set-cookie")) {
			for (HeaderElement element : header.getElements()) {
				log.debug(element.getName() + " = " + element.getValue());
				if (element.getName().equals("ptwebqq")) {
					ptwebqq = element.getValue();
				}
			}
		}
		return HttpUtils.getString(response);
	}

	/**
	 * 获取vfwebqq，登陆第四步
	 */
	private void getVfwebqq() {
		log.info("开始获取vfwebqq");
		//发送请求的客户端
		ApiURL url = ApiURL.GET_VFWEBQQ;
		HttpResponse response = client.get(url.buildUrl(ptwebqq), new BasicHeader("Referer", url.getReferer()));
		String result = HttpUtils.getString(response);
		JSONObject json = JSON.parseObject(result);
		vfwebqq = json.getJSONObject("result").getString("vfwebqq");
	}

	/**
	 * 获取uin和psessionid，登陆最后一步
	 */
	private void getUinAndPsessionid() {
		log.info("开始获取uin和psessionid");
		JSONObject r = new JSONObject();
		r.put("ptwebqq", ptwebqq);
		r.put("clientid", Client_ID);
		r.put("psessionid", "");
		r.put("status", "online");

		ApiURL url = ApiURL.GET_UIN_AND_PSESSIONID;
		HttpResponse response = client.post(url.getUrl(), "r=" + r.toJSONString(), new BasicHeader("Referer", url.getReferer()));
		String result = HttpUtils.getString(response);

		JSONObject json = JSON.parseObject(result).getJSONObject("result");
		psessionid = json.getString("psessionid");
		uin = json.getLongValue("uin");

	}

	private String hash() {
		return hash(uin, ptwebqq);
	}

	private static String hash(long x, String K) {
		int[] N = new int[4];
		for (int T = 0; T < K.length(); T++) {
			N[T % 4] ^= K.charAt(T);
		}
		String[] U = {"EC", "OK"};
		long[] V = new long[4];
		V[0] = x >> 24 & 255 ^ U[0].charAt(0);
		V[1] = x >> 16 & 255 ^ U[0].charAt(1);
		V[2] = x >> 8 & 255 ^ U[1].charAt(0);
		V[3] = x & 255 ^ U[1].charAt(1);

		long[] U1 = new long[8];

		for (int T = 0; T < 8; T++) {
			U1[T] = T % 2 == 0 ? N[T >> 1] : V[T >> 1];
		}

		String[] N1 = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
		String V1 = "";
		for (long aU1 : U1) {
			V1 += N1[(int) ((aU1 >> 4) & 15)];
			V1 += N1[(int) (aU1 & 15)];
		}
		return V1;
	}

}
