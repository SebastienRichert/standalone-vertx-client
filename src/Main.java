import java.util.concurrent.atomic.AtomicInteger;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;

public class Main {

	public static void main(String[] args) throws InterruptedException {

		final String host = args[0];
		final int port = Integer.parseInt(args[1]);
		final boolean isSSL = Boolean.parseBoolean(args[2]);
		final boolean isTrustAll = Boolean.parseBoolean(args[3]);
		final boolean isVerifyHost = Boolean.parseBoolean(args[4]);
		final boolean isVerbose = Boolean.parseBoolean(args[5]);
		final long delay = Integer.parseInt(args[6]);
		final long loop = Integer.parseInt(args[7]);

		final Vertx vertx = Vertx.vertx();
		//Vertx vertx = Vertx.vertx(new VertxOptions().setAddressResolverOptions(new AddressResolverOptions().setHostsValue(Buffer.buffer(ipAddressResolver + " " + hostnameAddressResolver))));
		
		final AtomicInteger success = new AtomicInteger();
		final AtomicInteger failure = new AtomicInteger();
		for (int i = 0 ; i < loop ; i++) {
			try {
				if (isVerbose)
					System.err.println("Creating client");
				HttpClientOptions options = new HttpClientOptions();
				options.setSsl(isSSL);
				options.setTrustAll(isTrustAll);
				options.setLogActivity(true);
				options.setVerifyHost(isVerifyHost);

				HttpClient client = vertx.createHttpClient(options);
				client.getNow(port,
						host,
						"/explore/",
						resp -> {
							if (isVerbose)
								resp.headers().entries().stream().forEach(System.out::println);
							System.err.println("CONNECTED. Status code:" + resp.statusCode());
							success.incrementAndGet();
						});
				if (isVerbose)
					System.err.println("Created client");
				Thread.sleep(delay);
			} catch (final Throwable t) {
				t.printStackTrace();
				failure.incrementAndGet();
			}			
		}
		Thread.sleep(3000);
		System.out.println("Success: " + success.get() + ", failure: " + failure.get());
		vertx.close();
	}

}
