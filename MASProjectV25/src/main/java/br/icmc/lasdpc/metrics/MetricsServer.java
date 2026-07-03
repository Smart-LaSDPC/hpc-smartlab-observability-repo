package br.icmc.lasdpc.metrics;

import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;

import java.io.IOException;

public class MetricsServer {

    private static HTTPServer server;

    public static void start(int port) throws IOException {
        DefaultExports.initialize(); // JVM: heap, GC, threads, CPU
        server = new HTTPServer(port);
        System.out.println("Metrics server started on port " + port + " — http://localhost:" + port + "/metrics");
    }

    public static void stop() {
        if (server != null) {
            server.stop();
        }
    }
}
