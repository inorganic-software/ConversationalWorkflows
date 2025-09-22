package com.inorganic.tools;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonObject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.inorganic.tools.exceptions.ToolException;

import dev.langchain4j.agent.tool.Tool;

@ApplicationScoped
public class StockTools {
    
    private static final Logger LOG = Logger.getLogger(StockTools.class);

    public record Stock(
            int stockId,
            int productId,
            int vwarehouseId,
            int totalAmount,
            int availableAmount,
            int reservedAmount,
            String stockDate,
            String reference,
            int prepickingOrders
    ) {}

    @RestClient
    StockRestClient stockRestClient;

    @Tool(name = "get-stock")
    public JsonObject getStock(int productId) {
        LOG.info("[StockTools] - getStock | Fetching stock for productId: " + productId);
        List<Stock> stocks;
        try {
            stocks = stockRestClient.getStock();
        } catch (jakarta.ws.rs.ProcessingException e) {
            LOG.error("[StockTools] - getStock | Error contacting stock service for productId: " + productId + ", error: " + e.getMessage());
            throw new ToolException("Error contacting stock service: " + e.getMessage(), "ProcessingException");
        }

        if (stocks == null || stocks.isEmpty()) {
            LOG.warn("[StockTools] - getStock | No stock information found in the system");
            throw new ToolException("No stock information found", "NotFound");
        }

        var filtered = stocks.stream()
                .filter(s -> s.productId() == productId)
                .toList();

        if (filtered.isEmpty()) {
            LOG.warn("[StockTools] - getStock | No stock found for productId: " + productId);
            return Json.createObjectBuilder()
                    .add("message", "No stock found for product " + productId)
                    .build();
        }

        var stockJsonArrayBuilder = Json.createArrayBuilder();

        for (Stock s : filtered) {
            int available = s.availableAmount() - (s.reservedAmount() + s.prepickingOrders());
            stockJsonArrayBuilder.add(Json.createObjectBuilder()
                    .add("stockId", s.stockId())
                    .add("productId", s.productId())
                    .add("vwarehouseId", s.vwarehouseId())
                    .add("availableAmount", available)
                    .add("stockDate", s.stockDate())
                    .add("reference", s.reference())
            );
        }

        JsonObject result = Json.createObjectBuilder()
                .add("stocks", stockJsonArrayBuilder)
                .build();
        LOG.info("[StockTools] - getStock | Successfully retrieved stock information for productId: " + productId + " with " + filtered.size() + " stock entries");
        return result;
    }
}
