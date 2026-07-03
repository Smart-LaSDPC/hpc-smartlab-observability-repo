package br.icmc.lasdpc;

import br.icmc.lasdpc.metrics.MetricsServer;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Main {
    public static void main(String[] args) {
        try {
            MetricsServer.start(9000);
        } catch (Exception e) {
            System.err.println("Failed to start metrics server: " + e.getMessage());
        }

        Runtime runtime = Runtime.instance();
        Profile profile = new ProfileImpl();
        AgentContainer mainContainer = runtime.createMainContainer(profile);

        try {
            AgentController collectorAgent = mainContainer.createNewAgent("CollectorAgent", "br.icmc.lasdpc.agents.CollectorAgent", null);
            AgentController reasonerAgent = mainContainer.createNewAgent("ReasonerAgent", "br.icmc.lasdpc.agents.ReasonerAgent", null);
            AgentController controlAgent = mainContainer.createNewAgent("ControlAgent", "br.icmc.lasdpc.agents.ControlAgent", null);

            collectorAgent.start();
            reasonerAgent.start();
            controlAgent.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}

