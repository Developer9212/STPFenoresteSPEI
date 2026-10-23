package fenoreste.spei.util;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fenoreste.spei.modeloSalida.GeolocalizacionVo;
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

    public GeolocalizacionVo obtenerUbicacionServidor(String token)
            throws IOException {

        GeolocalizacionVo geolocalizacionVo = new GeolocalizacionVo();

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

            geolocalizacionVo.setIp(json.has("ip")
                    ? json.get("ip").getAsString() : null);

            geolocalizacionVo.setCiudad(json.has("region")
                    ? json.get("region").getAsString() : null);

            geolocalizacionVo.setPais(json.has("pais")
                    ? json.get("pais").getAsString() : null);

            geolocalizacionVo.setCountry(json.has("country")
                    ? json.get("country").getAsString() : null);

            geolocalizacionVo.setCiudad(json.has("city")
                    ? json.get("city").getAsString() : null);
            if (json.has("loc") && !json.get("loc").isJsonNull()) {
                String[] coordenadas =
                        json.get("loc").getAsString().split(",");

                if (coordenadas.length == 2) {
                    geolocalizacionVo.setLatitud(Double.parseDouble(coordenadas[0]));
                    geolocalizacionVo.setLongitud(Double.parseDouble(coordenadas[1]));
                }
            }

            return geolocalizacionVo;
        }
    }
}