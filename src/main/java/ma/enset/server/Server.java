package ma.enset.server;

import io.grpc.ServerBuilder;
import ma.enset.services.Services;

import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        io.grpc.Server server = ServerBuilder.forPort(1997)
                .addService(new Services())
                .build();
        server.start();
        server.awaitTermination();
    }
}
