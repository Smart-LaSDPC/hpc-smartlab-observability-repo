package br.icmc.lasdpc.utils;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;

public class MetricsProvider {

    // ---- CollectorAgent ----
    public static final Counter mqttMessagesReceived = Counter.build()
            .name("mas_mqtt_messages_received_total")
            .help("Total MQTT messages received, by topic type")
            .labelNames("topic_type")
            .register();

    public static final Counter deviceDataSent = Counter.build()
            .name("mas_collector_device_data_sent_total")
            .help("Total DeviceData batches sent to ReasonerAgent")
            .register();

    public static final Gauge collectorSensorsInBatch = Gauge.build()
            .name("mas_collector_sensors_in_last_batch")
            .help("Number of sensors included in the last DeviceData batch")
            .register();

    public static final Gauge collectorAssetsInBatch = Gauge.build()
            .name("mas_collector_assets_in_last_batch")
            .help("Number of assets included in the last DeviceData batch")
            .register();

    // ---- ReasonerAgent ----
    public static final Counter reasoningCyclesTotal = Counter.build()
            .name("mas_reasoner_cycles_total")
            .help("Total ontology reasoning cycles executed")
            .register();

    public static final Histogram reasoningDuration = Histogram.build()
            .name("mas_reasoner_cycle_duration_seconds")
            .help("Duration of each ontology reasoning cycle in seconds")
            .buckets(0.01, 0.05, 0.1, 0.5, 1.0, 2.0, 5.0)
            .register();

    public static final Counter reasonerMessagesReceived = Counter.build()
            .name("mas_reasoner_messages_received_total")
            .help("Total messages received by ReasonerAgent from CollectorAgent")
            .register();

    // ---- ControlAgent ----
    public static final Counter controlMessagesReceived = Counter.build()
            .name("mas_control_messages_received_total")
            .help("Total messages received by ControlAgent from ReasonerAgent")
            .register();

    public static final Counter mqttCommandsPublished = Counter.build()
            .name("mas_control_mqtt_commands_published_total")
            .help("Total MQTT commands published by ControlAgent, by type")
            .labelNames("type")
            .register();

    public static final Counter ontologyChangesDetected = Counter.build()
            .name("mas_control_ontology_changes_detected_total")
            .help("Total ontology state changes detected and acted upon")
            .register();

    private MetricsProvider() {}
}
