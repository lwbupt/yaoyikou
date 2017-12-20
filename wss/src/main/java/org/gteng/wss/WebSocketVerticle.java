package org.gteng.wss;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.JsonObject;

public class WebSocketVerticle extends AbstractVerticle {

	@Override
	public void start(Future<Void> startFuture) throws Exception {

		HttpClient client = vertx.createHttpClient(new HttpClientOptions());

		client.websocket(new RequestOptions().setHost("api.huobi.pro").setURI("/ws").setSsl(true).setPort(443), wss -> {

			System.out.println("connected");

			wss.writeFinalTextFrame(new JsonObject().put("sub", "market.btcusdt.depth.percent10")
					.put("id", "depth" + System.currentTimeMillis()).toString());
			
			wss.closeHandler(v -> {
				System.out.println("disconnected");
			});
			
			wss.binaryMessageHandler(buffer -> {
				String message = uncompress(buffer.getBytes());

				JsonObject json = new JsonObject(message);

				Long ping = json.getLong("ping");

				if (ping != null) {
					System.out.println("ping: " + ping);
					String pong = new JsonObject().put("pong", ping).toString();
					wss.writeFinalTextFrame(pong);
					return;
				}
				
				System.out.println(json.encodePrettily());
			});
		});

		super.start(startFuture);
	}

	public static String uncompress(byte[] bytes) {

		try {

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			GZIPInputStream gunzip = new GZIPInputStream(in);
			byte[] buffer = new byte[256];
			int n;
			while ((n = gunzip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
			// toString()使用平台默认编码，也可以显式的指定如toString(&quot;GBK&quot;)
			return out.toString();
		} catch (Exception e) {
			System.err.println(e);
			return null;
		}
	}
}
