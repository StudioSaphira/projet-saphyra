package net.saphyra.common.api.rendering;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.Logger;

/** Loads model JSON asynchronously; the consumer owns identifiers and definition interpretation. */
public final class ModelDefinitionLoader {

    private ModelDefinitionLoader() {}

    /** A parser may return null to ignore a JSON model handled by another loader. */
    public static <T> CompletableFuture<Map<ResourceLocation, T>> load(
            ResourceManager resources, Executor executor, Predicate<ResourceLocation> filter,
            Function<JsonObject, T> parser, Logger logger) {
        return CompletableFuture.supplyAsync(() -> {
            Map<ResourceLocation, T> definitions = new HashMap<>();
            for (var entry : resources.listResources("models", id ->
                    id.getPath().endsWith(".json") && filter.test(id)).entrySet()) {
                try (Reader reader = entry.getValue().openAsReader()) {
                    var element = JsonParser.parseReader(reader);
                    if (!element.isJsonObject()) continue;
                    T definition = parser.apply(element.getAsJsonObject());
                    if (definition == null) continue;
                    String path = entry.getKey().getPath();
                    ResourceLocation id = ResourceLocation.fromNamespaceAndPath(entry.getKey().getNamespace(),
                            path.substring("models/".length(), path.length() - ".json".length()));
                    definitions.put(id, definition);
                } catch (Exception exception) {
                    logger.error("Failed to load model definition: {}", entry.getKey(), exception);
                }
            }
            return definitions;
        }, executor);
    }
}
