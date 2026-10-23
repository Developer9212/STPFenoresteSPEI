package fenoreste.spei.util;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class GeolocalizacionServer {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();

    public Map<String, Object> obtenerUbicacionServidor(String token)
            throws IOException {

        String url = "https://ipinfo.io/json?token=" + token;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException(
                        "Error de geolocalizacion. HTTP: "
                                + response.code());
            }

            if (response.body() == null) {
                throw new IOException("La API no devolvio informacion");
            }

            JsonObject json = JsonParser.parseString(
                    response.body().string()).getAsJsonObject();

            Map<String, Object> resultado = new LinkedHashMap<String, Object>();

            resultado.put("ip", json.has("ip")
                    ? json.get("ip").getAsString() : null);

            resultado.put("ciudad", json.has("city")
                    ? json.get("city").getAsString() : null);

            resultado.put("region", json.has("region")
                    ? json.get("region").getAsString() : null);

            resultado.put("pais", json.has("country")
                    ? json.get("country").getAsString() : null);

            if (json.has("loc") && !json.get("loc").isJsonNull()) {
                String[] coordenadas =
                        json.get("loc").getAsString().split(",");

                if (coordenadas.length == 2) {
                    resultado.put("latitud",
                            Double.parseDouble(coordenadas[0]));
                    resultado.put("longitud",
                            Double.parseDouble(coordenadas[1]));
                }
            }

            return resultado;
        }
    }
}