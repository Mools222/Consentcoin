package com.thomosim.consentcoin.Persistence.ModelClass.Exception;

public class ProfanityException extends Exception {
    String sentence;

    public ProfanityException(String sentence){
        this.sentence = sentence;
        if (sentence.toLowerCase().contains("fuck")){
            System.out.println("Profanity in the sentence \"" +sentence+"\"");
        }
    }

    public String toString(){
        return "Profanity in the sentence \"" +sentence+"\"";
    }
}
