package ma.enset.services;

import io.grpc.stub.StreamObserver;
import ma.enset.stubs.Guess;
import ma.enset.stubs.GuessGameGrpc;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Services extends GuessGameGrpc.GuessGameImplBase {

    private int numberToGuess;
    private Map<String, StreamObserver<Guess.Response>> observers = new HashMap<>();

    public Services() {
        numberToGuess = new Random().nextInt(1000);
        System.out.println(numberToGuess);
    }

    @Override
    public void guess(Guess.Request request, StreamObserver<Guess.Response> responseObserver) {
        String nickName = request.getNickName();
        int numberGuessed = request.getNumberGuessed();
        String message = "";

        System.out.println(String.format("%s sent %d", nickName, numberGuessed));

        if (!observers.containsKey(nickName)) {
            observers.put(nickName, responseObserver);
        }

        if(numberGuessed < numberToGuess){
            message = String.format("Le nombre %d est inferieur.", numberGuessed );
        } else if (numberGuessed > numberToGuess) {
            message = String.format("Le nombre %d est superieur", numberGuessed );
        } else {
            message = String.format("Bravo ! Le nombre %d est correcte.", numberGuessed );
            Guess.Response responseToLosers = Guess.Response.newBuilder()
                    .setMessage(String.format("GAME OVER ! The number was %d and found by %s.", numberToGuess, request.getNickName()))
                    .build();
            for (Map.Entry<String, StreamObserver<Guess.Response>> e : observers.entrySet()) {
                if (!request.getNickName().equals(e.getKey())){
                    e.getValue().onNext(responseToLosers);
                    e.getValue().onCompleted();
                }
            }
        }

        Guess.Response response = Guess.Response.newBuilder()
                .setMessage(message)
                .build();
        responseObserver.onNext(response);
        if(numberGuessed == numberToGuess){
            responseObserver.onCompleted();
            observers.clear();
        }
    }
}
