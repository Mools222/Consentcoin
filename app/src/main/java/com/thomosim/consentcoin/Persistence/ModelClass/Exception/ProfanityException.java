package com.thomosim.consentcoin.Persistence.ModelClass.Exception;

public class ProfanityException extends Exception {
    String sentence;

    public ProfanityException(String sentence){
        this.sentence = sentence;
    }

    public String toString(){
        return "Profanity in the sentence \"" +sentence+"\"";
    }
}
