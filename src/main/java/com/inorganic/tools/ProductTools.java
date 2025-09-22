package com.inorganic.tools;

import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

import java.io.StringReader;
import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParsingException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.inorganic.tools.exceptions.ToolException;

import dev.langchain4j.agent.tool.Tool;

@ApplicationScoped
public class ProductTools {
    
    private static final Logger LOG = Logger.getLogger(ProductTools.class);

    public record ProductSearchRequest(
            int productBaseId,
            String name,
            String billingCategory,
            String organization,
            List<Characteristic> characteristics
    ) {
        public record Characteristic(String name, String value) {
        }
    }

    @RestClient
    ProductRestClient productRestClient;

    @Tool(name = "search-products-by-name")
    public JsonObject searchProduct(String name) {
        LOG.info("[ProductTools] - searchProduct | Searching products by name: " + (name == null ? "all" : name));
        try {
            ProductSearchRequest request = new ProductSearchRequest(
                    1,
                    (name == null || name.isBlank()) ? null : name,
                    "sim",
                    "orange",
                    List.of(new ProductSearchRequest.Characteristic("Color", "Black"))
            );

            Response response = productRestClient.searchProducts(request);

            int status = response.getStatus();
            String rawJson = response.readEntity(String.class);
            if (status == HTTP_OK) {
                try (JsonReader reader = Json.createReader(new StringReader(rawJson))) {
                    JsonArray results = reader.readArray();
                    if (results.isEmpty()) {
                        LOG.warn("[ProductTools] - searchProduct | No products found for name: " + (name == null ? "all" : name));
                        throw new ToolException("No products found for name: " + (name == null ? "all" : name), "NotFound"
                        );
                    }

                    LOG.info("[ProductTools] - searchProduct | Successfully retrieved products for name: " + (name == null ? "all" : name));
                    return Json.createObjectBuilder().add("results", results).build();
                } catch (JsonParsingException e) {
                    LOG.error("[ProductTools] - searchProduct | Invalid JSON response for product: " + (name == null ? "all" : name));
                    throw new ToolException("Invalid JSON response for product: " + (name == null ? "all" : name), "InvalidJson"
                    );
                }
            } else if (status == HTTP_NOT_FOUND) {
                LOG.error("[ProductTools] - searchProduct | Product service not found (404) for name: " + (name == null ? "all" : name));
                throw new ToolException("No product service found for name: " + (name == null ? "all" : name), "NotFound"
                );
            } else if (status >= HTTP_INTERNAL_ERROR && status < 600) {
                LOG.error("[ProductTools] - searchProduct | Server error (" + status + ") when searching product: " + (name == null ? "all" : name));
                throw new ToolException("Server error (" + status + ") when searching product: " + (name == null ? "all" : name), "HttpError"
                );
            } else {
                LOG.error("[ProductTools] - searchProduct | Unexpected HTTP status " + status + " for product: " + (name == null ? "all" : name));
                throw new ToolException("Unexpected HTTP status " + status + " for product: " + (name == null ? "all" : name), "UnexpectedHttpStatus"
                );
            }

        } catch (ProcessingException e) {
            LOG.error("[ProductTools] - searchProduct | Network error searching product: " + (name == null ? "all" : name) + ", error: " + e.getMessage());
            throw new ToolException("Network error searching product: " + (name == null ? "all" : name) + " - " + e.getMessage(), "Timeout"
            );
        } catch (Exception e) {
            LOG.error("[ProductTools] - searchProduct | Unexpected error searching product: " + (name == null ? "all" : name) + ", error: " + e.getMessage());
            throw new ToolException("Unexpected error searching product: " + (name == null ? "all" : name) + " - " + e.getMessage(), "UnexpectedError"
            );
        }
    }

    @Tool(name = "search-products-by-name-return-product-id")
    public String findProductIdFromRawInput(String rawInput) {
        LOG.info("[ProductTools] - findProductIdFromRawInput | Processing raw input to find product ID");
        StringBuilder fullText = new StringBuilder();

        try (JsonReader reader = Json.createReader(new StringReader(rawInput))) {
            JsonValue val = reader.readValue();

            if (val.getValueType() == JsonValue.ValueType.ARRAY) {
                JsonArray array = val.asJsonArray();
                for (JsonValue item : array) {
                    switch (item.getValueType()) {
                        case STRING -> {
                            JsonString str = (JsonString) item;
                            fullText.append(str.getString()).append(" ");
                        }
                        case NUMBER -> {
                            JsonNumber num = (JsonNumber) item;
                            fullText.append(num.toString()).append(" ");
                        }
                        case TRUE, FALSE, NULL -> fullText.append(item.toString()).append(" ");
                        default -> {
                        }
                    }
                }
            } else {
                fullText.append(val.toString());
            }
        } catch (Exception e) {
            fullText.append(rawInput);
        }

        String text = fullText.toString()
                .replaceAll("<[^>]*>", " ")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ");

        String[] keywords = {"iphone", "galaxy", "pixel", "xperia", "nokia", "oneplus"};

        String foundKeyword = null;
        for (String kw : keywords) {
            if (text.matches(".*\\b" + kw + "s?\\b.*")) {
                foundKeyword = kw;
                LOG.debug("[ProductTools] - findProductIdFromRawInput | Found keyword: " + foundKeyword);
                break;
            }
        }

        if (foundKeyword == null) {
            String[] words = text.trim().split("\\s+");
            if (words.length > 0) {
                foundKeyword = words[words.length - 1];
                LOG.debug("[ProductTools] - findProductIdFromRawInput | Using last word as keyword: " + foundKeyword);
            } else {
                LOG.warn("[ProductTools] - findProductIdFromRawInput | No keyword found in raw input");
                return "NOT_FOUND";
            }
        }

        JsonObject productSearchResult = searchProduct(foundKeyword);

        JsonArray results = productSearchResult.getJsonArray("results");
        if (results != null && !results.isEmpty()) {
            JsonObject firstProduct = results.getJsonObject(0);
            if (firstProduct.containsKey("productId")) {
                int productId = firstProduct.getInt("productId");
                LOG.info("[ProductTools] - findProductIdFromRawInput | Found productId: " + productId);
                return String.valueOf(productId);
            }
        }
        LOG.warn("[ProductTools] - findProductIdFromRawInput | Product ID not found");
        return "NOT_FOUND";
    }

}
