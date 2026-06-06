package br.com.fiap.orbitasafe.geografia.domain.model;

public record Coordenadas(double latitude, double longitude) {

    public Coordenadas {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException("Latitude deve estar entre -90 e 90 graus");
        }
        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException("Longitude deve estar entre -180 e 180 graus");
        }
    }

    public double distanciaKm(Coordenadas outro) {
        double raioTerra = 6371.0;
        double dLat = Math.toRadians(outro.latitude - this.latitude);
        double dLon = Math.toRadians(outro.longitude - this.longitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(this.latitude))
                 * Math.cos(Math.toRadians(outro.latitude))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return raioTerra * c;
    }
}
