package com.okkuaixiu.oklivehome;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.callback.CallbackContext;
import com.alibaba.sdk.android.callback.InitResultCallback;
import com.alibaba.sdk.android.login.LoginService;
import com.alibaba.sdk.android.login.callback.LoginCallback;
import com.alibaba.sdk.android.login.callback.LogoutCallback;
import com.alibaba.sdk.android.session.model.Session;
import com.alibaba.sdk.android.ui.support.WebViewActivitySupport;

public class LoginAuthWithTaoBaoActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_with_taobao_layout);

		AlibabaSDK.asyncInit(this, new InitResultCallback() {
			@Override
			public void onSuccess() {
				Toast.makeText(LoginAuthWithTaoBaoActivity.this, "初始化成功",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFailure(int code, String message) {
				Toast.makeText(LoginAuthWithTaoBaoActivity.this, "初始化异常",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		CallbackContext.onActivityResult(requestCode, resultCode, data);
	}

	public void loginIn(View v) {
		// 调用getService方法来获取服务
		LoginService loginService = AlibabaSDK.getService(LoginService.class);
		loginService.showLogin(LoginAuthWithTaoBaoActivity.this,
				new LoginCallback() {
					@Override
					public void onSuccess(Session session) {
						// 当前是否登录状态 boolean isLogin();
						// 登录授权时间 long getLoginTime();
						// 当前用户ID String getUserId();
						// 用户其他属性 User getUser();
						// User中：淘宝用户名 淘宝用户ID 淘宝用户头像地址
						Toast.makeText(
								LoginAuthWithTaoBaoActivity.this,
								"-isLogin-" + session.isLogin() + "-UserId-"
										+ session.getUserId() + "-LoginTime-"
										+ session.getLoginTime()
										+ "[user]:nick="
										+ session.getUser().nick + "头像"
										+ session.getUser().avatarUrl,
								Toast.LENGTH_SHORT).show();
						Log.i("lala",
								"授权成功:" + session.isLogin() + "-UserId-"
										+ session.getUserId() + "-LoginTime-"
										+ session.getLoginTime()
										+ "-[user]:nick="
										+ session.getUser().nick + "-头像"
										+ session.getUser().avatarUrl);

						/*
						 * 授权成功:true-UserId-AAH6Sm6PACWzhojHXn8iniLb-LoginTime-
						 * 1463564784192
						 * -[user]:nick=似**r-头像http://wwc.taobaocdn.
						 * com/avatar/getAvatar
						 * .do?userNick=AAH6Sm6PACWzhojHXn8iniLb
						 * &width=160&height=160&type=open&appKey=23362303 *
						 */
					}

					@Override
					public void onFailure(int code, String message) {
						Toast.makeText(LoginAuthWithTaoBaoActivity.this,
								"授权取消" + code + message, Toast.LENGTH_SHORT)
								.show();
					}
				});
	}

	public void loginOut(View v) {
		AlibabaSDK.getService(LoginService.class).logout(this,
				new LogoutCallback() {

					@Override
					public void onFailure(int code, String msg) {
						Toast.makeText(LoginAuthWithTaoBaoActivity.this,
								"登出失败" + code, Toast.LENGTH_SHORT).show();

					}

					@Override
					public void onSuccess() {
						Toast.makeText(LoginAuthWithTaoBaoActivity.this,
								"登出成功", Toast.LENGTH_SHORT).show();

					}
				});
	}

	public void loginInWithQcCode(View v) {

		Map<String, String> params = new HashMap<String, String>();
		params.put("domain", "yunoswatch"); // 打开yunoswatch定义的样式
		params.put("config", "{\"qrwidth\": 200}");
		params.put("userDefActivity",
				"com.alibaba.sdk.android.login.ui.QrLoginActivity");
		params.put("userDefLayoutId", "tae_sdk_login_qr_activity_layout");
		AlibabaSDK.getService(LoginService.class).showQrCodeLogin(this, params,
				new InternalLoginCallback());

	}

	private class InternalLoginCallback implements LoginCallback {

		@Override
		public void onSuccess(Session session) {
			Toast.makeText(
					LoginAuthWithTaoBaoActivity.this,
					"授权成功" + session.getUserId() + session.getUser().nick
							+ session.isLogin() + session.getLoginTime()
							+ session.getUser().avatarUrl, Toast.LENGTH_SHORT)
					.show();
			Log.i("lala",
					"授权成功:" + session.getUserId() + session.getUser().nick
							+ session.isLogin() + session.getLoginTime()
							+ session.getUser().avatarUrl);
			CookieManager.getInstance().removeAllCookie();
			CookieSyncManager.getInstance().sync();
			Map<String, String[]> m = WebViewActivitySupport.getInstance()
					.getCookies();
			for (Entry<String, String[]> e : m.entrySet()) {
				for (String s : e.getValue()) {
					CookieManager.getInstance().setCookie(e.getKey(), s);
					System.out.println("key: " + e.getKey() + " value: " + s);
				}
			}
			CookieSyncManager.getInstance().sync();
			System.out.println("------------"
					+ CookieManager.getInstance()
							.getCookie("http://taobao.com"));
		}

		@Override
		public void onFailure(int code, String message) {
			Toast.makeText(LoginAuthWithTaoBaoActivity.this, "授权取消" + code,
					Toast.LENGTH_SHORT).show();
		}
	}
}
