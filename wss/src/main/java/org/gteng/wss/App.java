package org.gteng.wss;

import io.vertx.core.Vertx;
import io.vertx.core.spi.resolver.ResolverProvider;

/**
 * Hello world!
 *
 */
public class App 
{
	static {
		System.setProperty(ResolverProvider.DISABLE_DNS_RESOLVER_PROP_NAME, "true");
	}
	
    public static void main( String[] args )
    {
        Vertx vertx = Vertx.vertx();
        
        vertx.deployVerticle(WebSocketVerticle.class.getName());
    }
}
